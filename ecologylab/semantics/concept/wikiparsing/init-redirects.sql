-- init redirects
INSERT INTO redirects
  WITH t AS (
    SELECT title AS from_title, redirected_name
    FROM dbp_redirects, dbp_titles
    WHERE dbp_redirects.name = dbp_titles.name
  )
  SELECT from_title, title as to_title
  FROM t, dbp_titles
  WHERE t.redirected_name = dbp_titles.name;

