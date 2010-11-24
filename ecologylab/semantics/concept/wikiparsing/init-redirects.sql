-- init redirects
INSERT INTO redirects
  WITH t AS (
    SELECT title AS from_title, redirected_name
    FROM dbp_redirects, dbp_titles
    WHERE dbp_redirects.name = dbp_titles.name
  )
  SELECT from_title, title AS to_title
  FROM t, dbp_titles
  WHERE t.redirected_name = dbp_titles.name;


-- import redirects as wikilinks
DROP TABLE IF EXISTS tmp_wikilinks;
CREATE TABLE tmp_wikilinks (
  from_title VARCHAR NOT NULL,
  to_title VARCHAR NOT NULL,
  surface VARCHAR NOT NULL,
  PRIMARY KEY (from_title, to_title, surface)
) WITHOUT OIDS;

INSERT INTO tmp_wikilinks
  SELECT from_title, to_title, trim(both from regexp_replace(regexp_replace(lower(to_title), '[^A-Za-z0-9 ]', ' ', 'g'), E'\\s+', ' ', 'g')) as surface
  FROM redirects;
  
DELETE FROM tmp_wikilinks USING wikilinks
  WHERE tmp_wikilinks.from_title = wikilinks.from_title
        AND tmp_wikilinks.to_title = wikilinks.to_title
        AND tmp_wikilinks.surface = wikilinks.surface;

INSERT INTO wikilinks SELECT * FROM tmp_wikilinks;

DROP TABLE tmp_wikilinks;

