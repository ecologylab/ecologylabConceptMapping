DROP TABLE IF EXISTS freq_concepts;
CREATE TABLE freq_concepts (
	title VARCHAR PRIMARY KEY,
	count_of_reference INTEGER NOT NULL
) WITHOUT OIDS;

INSERT INTO freq_concepts
  SELECT to_title, count(*)
  FROM wikilinks
  GROUP BY to_title HAVING count(*) > 10 ORDER BY count DESC;

DROP TABLE IF EXISTS freq_surfaces;
CREATE TABLE freq_surfaces (
  surface VARCHAR PRIMARY KEY
) WITHOUT OIDS;

INSERT INTO freq_surfaces
  SELECT DISTINCT surface
  FROM wikilinks, freq_concepts
  WHERE wikilinks.to_title = freq_concepts.title;
