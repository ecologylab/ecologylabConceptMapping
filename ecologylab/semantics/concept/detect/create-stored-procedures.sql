DROP FUNCTION IF EXISTS query_keyphraseness(varchar);
CREATE FUNCTION query_keyphraseness(varchar) RETURNS double precision
  AS 'SELECT keyphraseness FROM keyphraseness WHERE surface=$1;'
  STABLE
  RETURNS NULL ON NULL INPUT
  LANGUAGE SQL;
  
DROP FUNCTION IF EXISTS query_commonness(varchar, varchar);
CREATE FUNCTION query_commonness(varchar, varchar) RETURNS double precision
  AS 'SELECT commonness FROM commonness WHERE surface=$1 AND concept=$2;'
  STABLE
  RETURNS NULL ON NULL INPUT
  LANGUAGE SQL;
  
DROP FUNCTION IF EXISTS query_inlink_concepts(varchar);
CREATE FUNCTION query_inlink_concepts(varchar) RETURNS SETOF varchar
  AS 'SELECT from_title FROM wikilinks WHERE to_title=$1;'
  STABLE
  RETURNS NULL ON NULL INPUT
  LANGUAGE SQL;
  