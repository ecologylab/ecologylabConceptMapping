TRUNCATE freq_surfaces;
INSERT INTO freq_surfaces
  SELECT surface, count(surface)
  FROM wikilinks
  GROUP BY surface
  HAVING count(surface) > 10
  ORDER BY count(surface) DESC;

-- manually filter freq_surfaces with stop word list...

TRUNCATE freq_concepts;
INSERT INTO freq_concepts
  SELECT to_title, count(to_title)
  FROM wikilinks
  GROUP BY to_title
  HAVING count(to_title) > 10
  ORDER BY count(to_title) DESC;
  
-- init commonness using freq_surfaces & freq_concepts

DELETE FROM freq_concepts WHERE title NOT IN (SELECT DISTINCT concept FROM commonness);

DELETE FROM commonness WHERE concept NOT IN (SELECT DISTINCT title FROM freq_concepts);

DELETE FROM commonness WHERE commonness < 0.001;

DELETE FROM freq_surfaces WHERE surface NOT IN (SELECT DISTINCT surface FROM commonness);

-- manually filter freq_surfaces for those with a lot of senses

TRUNCATE surface_occurrences;
INSERT INTO surface_occurrences SELECT surface, 0, 0 FROM freq_surfaces;
-- calc surface occurrences

DELETE FROM surface_occurrences WHERE total < 10;

TRUNCATE keyphraseness;
INSERT INTO keyphraseness
	SELECT surface, labeled*1.0/total AS keyphraseness
	FROM surface_occurrences
	WHERE total > 0;
	
DELETE FROM freq_surfaces USING keyphraseness WHERE freq_surfaces.surface = keyphraseness.surface AND keyphraseness.keyphraseness < 0.001;

DELETE FROM freq_surfaces USING surface_occurrences WHERE freq_surfaces.surface = surface_occurrences.surface AND surface_occurrences.total < 10;

INSERT INTO freq_concept_inlink_count
  SELECT title, util_query_inlink_count(title) FROM freq_concepts;
