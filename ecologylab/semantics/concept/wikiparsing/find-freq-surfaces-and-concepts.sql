-- note the threshold --
TRUNCATE freq_surfaces;
INSERT INTO freq_surfaces
  SELECT surface, count(surface)
  FROM wikilinks
  GROUP BY surface
  HAVING count(surface) > 10
  ORDER BY count(surface) DESC;

TRUNCATE freq_concepts;
INSERT INTO freq_concepts
  SELECT to_title, count(to_title)
  FROM wikilinks
  GROUP BY to_title
  HAVING count(to_title) > 10
  ORDER BY count(to_title) DESC;
  