DROP INDEX IF EXISTS dbp_titles_title;
CREATE INDEX dbp_titles_title ON dbp_titles (title);

DROP INDEX IF EXISTS dbp_redirects_redirected_name;
CREATE INDEX dbp_redirects_redirected_name ON dbp_redirects (redirected_name);

DROP INDEX IF EXISTS redirects_to_title;
CREATE INDEX redirects_to_title ON redirects (to_title);

DROP INDEX IF EXISTS wikilinks_from_title;
CREATE INDEX wikilinks_from_title ON wikilinks (from_title);

DROP INDEX IF EXISTS wikilinks_to_title;
CREATE INDEX wikilinks_to_title ON wikilinks (to_title);

DROP INDEX IF EXISTS wikilinks_surface;
CREATE INDEX wikilinks_surface ON wikilinks (surface);

DROP INDEX IF EXISTS commonness_surface;
CREATE INDEX commonness_surface ON commonness (surface);

DROP INDEX IF EXISTS commonness_concept;
CREATE INDEX commonness_concept ON commonness (concept);

DROP INDEX IF EXISTS surface_occurrences_total;
CREATE INDEX surface_occurrences_total ON surface_occurrences (total DESC);

DROP INDEX IF EXISTS surface_occurrences_labeled;
CREATE INDEX surface_occurrences_labeled ON surface_occurrences (labeled DESC);
