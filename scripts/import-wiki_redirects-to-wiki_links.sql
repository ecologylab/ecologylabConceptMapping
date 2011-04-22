
-- this script imports redirects (in wiki_redirects) as links (in wiki_links), so that redirects
-- are handled as surfaces.


DROP SEQUENCE IF EXISTS tmp_wiki_link_seq;
CREATE SEQUENCE tmp_wiki_link_seq INCREMENT 1 START 1;

DROP TABLE IF EXISTS tmp_wiki_links;
CREATE TABLE tmp_wiki_links (
  link_id INTEGER NOT NULL DEFAULT nextval('tmp_wiki_link_seq'),
  from_id INTEGER NOT NULL,
  to_id INTEGER NOT NULL,
  surface VARCHAR NOT NULL,
  PRIMARY KEY (link_id)
) WITHOUT OIDS;

INSERT INTO tmp_wiki_links (from_id, to_id, surface)
  SELECT wiki_concepts.id, wiki_concepts.id, trim(both from regexp_replace(regexp_replace(lower(wiki_redirects.from_title), '[^A-Za-z0-9 ]', ' ', 'g'), E'\\s+', ' ', 'g')) as surface
  FROM wiki_redirects, wiki_concepts
  WHERE wiki_redirects.to_title = wiki_concepts.title;
    
-- make sure that wiki_links does not have constraints and indexes for efficiency.
INSERT INTO wiki_links (from_id, to_id, surface) SELECT from_id, to_id, surface FROM tmp_wiki_links;
-- add fkey constraints with indexes to from_id and to_id; add index to surface.

DROP TABLE tmp_wiki_links;
DROP SEQUENCE tmp_wiki_link_seq;
