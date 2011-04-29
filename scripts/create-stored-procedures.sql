-- this function calculate relatedness for any two pairs of concept IDs.
-- note that we cannot put any constraints on the two IDs because this function is used by other functions like those for top links.
-- this function also stores relatedness value into corresponding table.
DROP FUNCTION IF EXISTS calculate_relatedness(id1 int, id2 int, total int, min_dist double precision, max_dist double precision);
CREATE FUNCTION calculate_relatedness(id1 int, id2 int, total int, min_dist double precision, max_dist double precision)
RETURNS double precision AS $$
DECLARE
  r double precision;
  s1 int;
  s2 int;
  s3 int;
BEGIN
  IF id1 = id2 THEN
    RETURN min_dist;
  END IF;

  IF id1 > id2 THEN
    RETURN calculate_relatedness(id2, id1, total);
  END IF;

  SELECT relatedness INTO r FROM relatedness WHERE concept_id1 = id1 AND concept_id2 = id2;
  IF NOT FOUND THEN
    SELECT count(from_id) INTO s1 FROM wiki_links WHERE to_id = id1;
    SELECT count(from_id) INTO s2 FROM wiki_links WHERE to_id = id2;
    WITH t AS (
      SELECT from_id FROM wiki_links WHERE to_id = id1
      INTERSECT
      SELECT from_id FROM wiki_links WHERE to_id = id2
    ) SELECT count(*) INTO s3 FROM t;
    
    IF s3 <= 0 THEN
      RETURN max_dist;
    END IF;
    
    IF s1 > s2 THEN
      r = (ln(s1) - ln(s3)) / (ln(total) - ln(s2));
    ELSE
      r = (ln(s2) - ln(s3)) / (ln(total) - ln(s1));
    END IF;
  END IF;
  RETURN r;
End;
$$ LANGUAGE plpgsql;
