INSERT INTO concepts
  (SELECT title AS name FROM dbp_titles
   EXCEPT
   SELECT from_concept AS name FROM redirects)
   