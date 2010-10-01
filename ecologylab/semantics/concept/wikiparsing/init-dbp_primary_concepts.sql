INSERT INTO dbp_primary_concepts
  (SELECT name FROM dbp_titles
   EXCEPT
   SELECT name FROM dbp_redirects);
   