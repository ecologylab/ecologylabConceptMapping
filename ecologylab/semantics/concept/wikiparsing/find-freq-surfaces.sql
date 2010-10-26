-- note the threshold --
TRUNCATE freq_surfaces;
INSERT INTO freq_surfaces
  SELECT surface, count(surface)
  FROM wikilinks
  GROUP BY surface
  HAVING count(surface) > 10
  ORDER BY count(surface) DESC;
