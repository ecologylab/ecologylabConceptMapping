DROP TABLE IF EXISTS dbp_records;
CREATE TABLE dbp_records (
	dbp_title VARCHAR PRIMARY KEY,
	wiki_title VARCHAR NOT NULL
) WITHOUT OIDS;





DROP TABLE IF EXISTS wiki_redirects;
CREATE TABLE wiki_redirects (
	from_title VARCHAR NOT NULL,
	to_title VARCHAR NOT NULL
) WITHOUT OIDS;

DROP TABLE IF EXISTS wiki_concepts;
CREATE TABLE wiki_concepts (
	id INTEGER PRIMARY KEY,
	title VARCHAR NOT NULL,
	text TEXT NOT NULL
) WITHOUT OIDS;

DROP TABLE IF EXISTS wiki_links;
CREATE TABLE wiki_links (
	seq_id INTEGER PRIMARY KEY,
	from_id INTEGER NOT NULL,
	to_id INTEGER NOT NULL,
	surface VARCHAR NOT NULL
) WITHOUT OIDS;





DROP TABLE IF EXISTS surface_occurrences;
CREATE TABLE surface_occurrences (
  surface VARCHAR PRIMARY KEY,
  total INTEGER NOT NULL,
  linked INTEGER NOT NULL
) WITHOUT OIDS;





DROP TABLE IF EXISTS commonness;
CREATE TABLE commonness (
	surface VARCHAR NOT NULL,
	concept_id INTEGER NOT NULL,
	commonness DOUBLE PRECISION NOT NULL,
	PRIMARY KEY (surface, concept_id)
) WITHOUT OIDS;

DROP TABLE IF EXISTS keyphraseness;
CREATE TABLE keyphraseness (
	surface VARCHAR PRIMARY KEY,
	keyphraseness DOUBLE PRECISION NOT NULL
) WITHOUT OIDS;

DROP TABLE IF EXISTS relatedness;
CREATE TABLE relatedness (
  concept_id1 INTEGER NOT NULL,
  concept_id2 INTEGER NOT NULL,
  relatedness DOUBLE PRECISION NOT NULL,
  PRIMARY KEY (concept_id1, concept_id2)
) WITHOUT OIDS;
