-- create dbpedia tables
DROP TABLE IF EXISTS dbp_titles;
CREATE TABLE dbp_titles (
	name VARCHAR PRIMARY KEY,
	title VARCHAR NOT NULL
) WITHOUT OIDS;

DROP TABLE IF EXISTS dbp_redirects;
CREATE TABLE dbp_redirects (
	name VARCHAR PRIMARY KEY,
	redirected_name VARCHAR NOT NULL
) WITHOUT OIDS;

DROP TABLE IF EXISTS dbp_primary_concepts;
CREATE TABLE dbp_primary_concepts (
  name VARCHAR PRIMARY KEY
) WITHOUT OIDS;

-- create primary tables
DROP TABLE IF EXISTS redirects;
CREATE TABLE redirects (
	from_title VARCHAR PRIMARY KEY,
	to_title VARCHAR NOT NULL
) WITHOUT OIDS;

DROP TABLE IF EXISTS wikilinks;
CREATE TABLE wikilinks (
	from_title VARCHAR NOT NULL,
	to_title VARCHAR NOT NULL,
	surface VARCHAR NOT NULL
);

-- create secondary tables
DROP TABLE IF EXISTS commonness;
CREATE TABLE commonness (
	surface VARCHAR NOT NULL,
	concept VARCHAR NOT NULL,
	commonness DOUBLE PRECISION NOT NULL,
	PRIMARY KEY (surface, concept)
) WITHOUT OIDS;

DROP TABLE IF EXISTS surface_occurrences;
CREATE TABLE surface_occurrences (
	surface VARCHAR PRIMARY KEY,
	total INTEGER NOT NULL,
	labeled INTEGER NOT NULL
) WITHOUT OIDS;

DROP TABLE IF EXISTS keyphraseness;
CREATE TABLE keyphraseness (
	surface VARCHAR PRIMARY KEY,
	keyphraseness DOUBLE PRECISION NOT NULL
) WITHOUT OIDS;
