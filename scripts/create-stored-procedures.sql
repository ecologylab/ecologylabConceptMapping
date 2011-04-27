DROP FUNCTION IF EXISTS calculate_relatedness(cid1 int, cid2 int, total int);
CREATE FUNCTION calculate_relatedness(cid1 int, cid2 int, total int)
RETURNS relatedness AS $$
DECLARE
  r relatedness%rowtype;
  id1 int;
  id2 int;
  rel double precision;
  s1 int;
  s2 int;
  s3 int;
  tmp_id int;
BEGIN
  IF cid1 = cid2 THEN
    r.concept_id1 := cid1;
    r.concept_id2 := cid2;
    r.relatedness := 0;
    RETURN r;
  END IF;

  IF cid1 < cid2 THEN
    id1 := cid1;
    id2 := cid2;
  ELSE
    id1 := cid2;
    id2 := cid1;
  END IF;
  
  SELECT * INTO r FROM relatedness WHERE concept_id1 = id1 AND concept_id2 = id2;
  IF FOUND THEN
    RETURN r;
  ELSE
    SELECT count(from_id) INTO s1 FROM wiki_links WHERE to_id = id1;
    SELECT count(from_id) INTO s2 FROM wiki_links WHERE to_id = id2;
    WITH t AS (
      SELECT from_id FROM wiki_links WHERE to_id = id1
      INTERSECT
      SELECT from_id FROM wiki_links WHERE to_id = id2
    ) SELECT count(*) INTO s3 FROM t;
    
    IF s3 <= 0 THEN
      r.concept_id1 := id1;
      r.concept_id2 := id2;
      r.relatedness := 1;
      RETURN r;
    END IF;
    
    IF s1 > s2 THEN
      rel = (ln(s1) - ln(s3)) / (ln(total) - ln(s2));
    ELSE
      rel = (ln(s2) - ln(s3)) / (ln(total) - ln(s1));
    END IF;
    INSERT INTO relatedness(concept_id1, concept_id2, relatedness) VALUES (id1, id2, rel);

    r.concept_id1 := id1;
    r.concept_id2 := id2;
    r.relatedness := rel;
    RETURN r;
  END IF;
End;
$$ LANGUAGE plpgsql;



DROP FUNCTION IF EXISTS calculate_top_inlinks(id int, total int);
CREATE FUNCTION calculate_top_inlinks(id int, total int)
RETURNS SETOF top_inlinks AS $$
DECLARE
  r top_inlinks%rowtype;
BEGIN
  SELECT * INTO r FROM top_inlinks WHERE to_id = id LIMIT 1;
  IF FOUND THEN
    FOR r IN SELECT DISTINCT ON (from_id, to_id) * FROM top_inlinks WHERE to_id = id ORDER BY (from_id, to_id)
    LOOP
      RETURN NEXT r;
    END LOOP;
    RETURN;
  ELSE
    FOR r IN
    WITH t AS (SELECT DISTINCT ON (from_id, to_id) from_id, to_id FROM wiki_links WHERE to_id = id)
    SELECT from_id, to_id, calculate_relatedness(from_id, id, total) AS relatedness
    FROM t ORDER BY relatedness ASC LIMIT 100
    LOOP
      INSERT INTO top_inlinks (from_id, to_id, relatedness) VALUES (r.from_id, r.to_id, r.relatedness);
      RETURN NEXT r;
    END LOOP;
    RETURN;
  END IF;
END;
$$ LANGUAGE plpgsql;

DROP FUNCTION IF EXISTS calculate_top_outlinks(id int, total int);
CREATE FUNCTION calculate_top_outlinks(id int, total int)
RETURNS SETOF top_outlinks AS $$
DECLARE
  r top_outlinks%rowtype;
BEGIN
  SELECT * INTO r FROM top_outlinks WHERE from_id = id LIMIT 1;
  IF FOUND THEN
    FOR r IN SELECT DISTINCT ON (from_id, to_id) * FROM top_outlinks WHERE from_id = id ORDER BY (from_id, to_id)
    LOOP
      RETURN NEXT r;
    END LOOP;
    RETURN;
  ELSE
    FOR r IN
    WITH t AS (SELECT DISTINCT ON (from_id, to_id) from_id, to_id FROM wiki_links WHERE from_id = id)
    SELECT from_id, to_id, calculate_relatedness(id, to_id, total) AS relatedness
    FROM t ORDER BY relatedness ASC LIMIT 100
    LOOP
      INSERT INTO top_outlinks (from_id, to_id, relatedness) VALUES (r.from_id, r.to_id, r.relatedness);
      RETURN NEXT r;
    END LOOP;
    RETURN;
  END IF;
END;
$$ LANGUAGE plpgsql;

