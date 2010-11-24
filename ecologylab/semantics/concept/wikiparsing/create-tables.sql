---------------------------
-- create dbpedia tables --
---------------------------

-- map dbpedia names to wikipedia titles.
DROP TABLE IF EXISTS dbp_titles;
CREATE TABLE dbp_titles (
	name VARCHAR PRIMARY KEY,
	title VARCHAR NOT NULL
) WITHOUT OIDS;

-- redirects recorded using dbpedia names.
DROP TABLE IF EXISTS dbp_redirects;
CREATE TABLE dbp_redirects (
	name VARCHAR PRIMARY KEY,
	redirected_name VARCHAR NOT NULL
) WITHOUT OIDS;

-- not redirected concepts (primary concepts) recorded using dbpedia names.
DROP TABLE IF EXISTS dbp_primary_concepts;
CREATE TABLE dbp_primary_concepts (
  name VARCHAR PRIMARY KEY
) WITHOUT OIDS;

---------------------------
-- create primary tables --
---------------------------

-- redirects recorded using wikipedia titles.
DROP TABLE IF EXISTS redirects;
CREATE TABLE redirects (
	from_title VARCHAR PRIMARY KEY,
	to_title VARCHAR NOT NULL
) WITHOUT OIDS;

-- wikipedia internal links, with source, destination and surface information. surfaces are
-- preprocessed.
DROP TABLE IF EXISTS wikilinks;
CREATE TABLE wikilinks (
	from_title VARCHAR NOT NULL,
	to_title VARCHAR NOT NULL,
	surface VARCHAR NOT NULL,
	PRIMARY KEY (from_title, to_title, surface)
) WITHOUT OIDS;

-- map wikipedia titles to texts. these are parsed paragraphs, with no wiki-markups or html codes.
DROP TABLE IF EXISTS wikitexts;
CREATE TABLE wikitexts (
  title VARCHAR PRIMARY KEY,
  text TEXT NOT NULL
) WITHOUT OIDS;

-----------------------------
-- create secondary tables --
-----------------------------

-- frequent surfaces with reference count.
DROP TABLE IF EXISTS freq_surfaces;
CREATE TABLE freq_surfaces (
  surface VARCHAR PRIMARY KEY,
  count_of_references INTEGER NOT NULL
) WITHOUT OIDS;

-- frequent concepts with reference count.
DROP TABLE IF EXISTS freq_concepts;
CREATE TABLE freq_concepts (
  title VARCHAR PRIMARY KEY,
  count_of_references INTEGER NOT NULL
) WITHOUT OIDS;

DROP TABLE IF EXISTS freq_concept_inlink_count;
CREATE TABLE freq_concept_inlink_count (
  title VARCHAR PRIMARY KEY,
  inlink_count INTEGER NOT NULL
) WITHOUT OIDS;

-- commonness of a concept to a surface.
DROP TABLE IF EXISTS commonness;
CREATE TABLE commonness (
	surface VARCHAR NOT NULL,
	concept VARCHAR NOT NULL,
	commonness DOUBLE PRECISION NOT NULL,
	PRIMARY KEY (surface, concept)
) WITHOUT OIDS;

-- # of total and labeled occurrence of a surface. computed using wikilinks & wikitexts. can be
-- used to compute keyphraseness.
DROP TABLE IF EXISTS surface_occurrences;
CREATE TABLE surface_occurrences (
	surface VARCHAR PRIMARY KEY,
	total INTEGER NOT NULL,
	labeled INTEGER NOT NULL
) WITHOUT OIDS;

-- keyphraseness of a surface.
DROP TABLE IF EXISTS keyphraseness;
CREATE TABLE keyphraseness (
	surface VARCHAR PRIMARY KEY,
	keyphraseness DOUBLE PRECISION NOT NULL
) WITHOUT OIDS;
