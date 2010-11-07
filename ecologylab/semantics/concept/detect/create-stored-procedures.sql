DROP FUNCTION IF EXISTS query_keyphraseness(varchar);
CREATE FUNCTION query_keyphraseness(varchar) RETURNS double precision
  AS 'SELECT keyphraseness FROM keyphraseness WHERE surface=$1;'
  STABLE
  RETURNS NULL ON NULL INPUT
  LANGUAGE SQL;
    
DROP FUNCTION IF EXISTS query_inlink_concepts(varchar);
CREATE FUNCTION query_inlink_concepts(varchar) RETURNS TABLE(from_title varchar)
  AS 'SELECT from_title FROM wikilinks WHERE to_title=$1;'
  STABLE
  RETURNS NULL ON NULL INPUT
  LANGUAGE SQL;
  
DROP FUNCTION IF EXISTS query_senses(varchar);
CREATE FUNCTION query_senses(varchar) RETURNS TABLE(concept varchar, commonness double precision)
  AS 'SELECT concept, commonness FROM commonness WHERE surface=$1;'
  STABLE
  RETURNS NULL ON NULL INPUT
  LANGUAGE SQL;
  