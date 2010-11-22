-- this function will only be used to create table freq_concept_inlink_count
DROP FUNCTION IF EXISTS util_query_inlink_count(varchar);
CREATE FUNCTION util_query_inlink_count(varchar) RETURNS bigint
  AS 'WITH t AS (SELECT DISTINCT from_title FROM wikilinks WHERE to_title=$1)
      SELECT count(*) FROM t;'
  STABLE
  RETURNS NULL ON NULL INPUT
  LANGUAGE SQL;

DROP FUNCTION IF EXISTS query_keyphraseness(varchar);
CREATE FUNCTION query_keyphraseness(varchar) RETURNS double precision
  AS 'SELECT keyphraseness FROM keyphraseness WHERE surface=$1;'
  STABLE
  RETURNS NULL ON NULL INPUT
  LANGUAGE SQL;
  
DROP FUNCTION IF EXISTS query_inlink_count(varchar);
CREATE FUNCTION query_inlink_count(varchar) RETURNS bigint
  AS 'SELECT inlink_count FROM freq_concept_inlink_count WHERE title=$1;'
  STABLE
  RETURNS NULL ON NULL INPUT
  LANGUAGE SQL;  
    
DROP FUNCTION IF EXISTS query_common_inlink_count(varchar, varchar);
CREATE FUNCTION query_common_inlink_count(varchar, varchar) RETURNS bigint
  AS 'WITH t1 AS (SELECT DISTINCT from_title FROM wikilinks WHERE to_title=$1),
           t2 AS (SELECT DISTINCT from_title FROM wikilinks WHERE to_title=$2)
      SELECT count(*) FROM t1, t2 WHERE t1.from_title = t2.from_title;'
  STABLE
  RETURNS NULL ON NULL INPUT
  LANGUAGE SQL;
  
DROP FUNCTION IF EXISTS query_senses(varchar);
CREATE FUNCTION query_senses(varchar) RETURNS TABLE(concept varchar, commonness double precision)
  AS 'SELECT concept, commonness FROM commonness WHERE surface=$1;'
  STABLE
  RETURNS NULL ON NULL INPUT
  LANGUAGE SQL;
