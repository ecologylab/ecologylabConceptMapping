-- provisional freq surfaces
TRUNCATE freq_surfaces;
INSERT INTO freq_surfaces
  SELECT surface, count(surface)
  FROM wikilinks
  GROUP BY surface
  HAVING count(surface) > 10
  ORDER BY count(surface) DESC;

-- provisional freq concepts
TRUNCATE freq_concepts;
INSERT INTO freq_concepts
  SELECT to_title, count(to_title)
  FROM wikilinks
  GROUP BY to_title
  HAVING count(to_title) > 10
  ORDER BY count(to_title) DESC;
  
-- /* calc provisional commonness using Java code... */

-- commonness threshold
DELETE FROM commonness WHERE commonness < 0.001;

-- freq concepts
DELETE FROM commonness WHERE concept NOT IN (SELECT DISTINCT title FROM freq_concepts);
-- filter out stop words manually...
DELETE FROM freq_concepts WHERE title NOT IN (SELECT DISTINCT concept FROM commonness);

-- filter freq_surfaces using commonness
DELETE FROM freq_surfaces WHERE surface NOT IN (SELECT DISTINCT surface FROM commonness);

-- surface occurrences
TRUNCATE surface_occurrences;
INSERT INTO surface_occurrences SELECT surface, 0, 0 FROM freq_surfaces;
-- /* calc surface occurrences using Java code... */

-- occurrence threshold
DELETE FROM surface_occurrences WHERE total < 10;

-- provisional keyphraseness
TRUNCATE keyphraseness;
INSERT INTO keyphraseness
	SELECT surface, labeled*1.0/total AS keyphraseness
	FROM surface_occurrences
	WHERE total > 0;
	
-- keyphraseness threshold
DELETE FROM keyphraseness WHERE keyphraseness < 0.001;

-- freq surfaces
DELETE FROM keyphraseness WHERE surface NOT IN (SELECT DISTINCT surface FROM freq_surfaces);
DELETE FROM freq_surfaces WHERE surface NOT IN (SELECT DISTINCT surface FROM keyphraseness);

-- /* filter freq_surfaces with stop words when generating the dictionary */

-- clean wikilinks
DELETE FROM wikilinks WHERE from_title NOT IN (SELECT DISTINCT title FROM freq_concepts);
DELETE FROM wikilinks WHERE to_title NOT IN (SELECT DISTINCT title FROM freq_concepts);
DELETE FROM wikilinks WHERE surface NOT IN (SELECT DISTINCT surface FROM freq_surfaces);
