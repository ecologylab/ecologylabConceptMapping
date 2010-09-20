-- fill redirects
INSERT INTO redirects
  WITH t AS (
    SELECT title AS from_concept, redirected
    FROM dbp_redirects, dbp_titles
    WHERE dbp_redirects.name = dbp_titles.name
  )
  SELECT from_concept, title as to_concpt
  FROM t, dbp_titles
  WHERE t.redirected = dbp_titles.name;

-- fill wikilinks
INSERT INTO wikilinks
  SELECT from_concept, to_concept, to_concept FROM redirects;
