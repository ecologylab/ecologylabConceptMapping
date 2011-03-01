DROP INDEX IF EXISTS wiki_concepts_title;
CREATE INDEX wiki_concepts_title ON wiki_concepts (title);

DROP INDEX IF EXISTS wiki_links_from_id;
CREATE INDEX wiki_links_from_id ON wiki_links (from_id);

DROP INDEX IF EXISTS wiki_links_to_id;
CREATE INDEX wiki_links_to_id ON wiki_links (to_id);

DROP INDEX IF EXISTS wiki_links_surface;
CREATE INDEX wiki_links_surface ON wiki_links (surface);

DROP INDEX IF EXISTS wiki_redirects_from_title;
CREATE INDEX wiki_redirects_from_title ON wiki_redirects (from_title);

DROP INDEX IF EXISTS wiki_redirects_to_title;
CREATE INDEX wiki_redirects_to_title ON wiki_redirects (to_title);

DROP INDEX IF EXISTS surface_occurrences_total;
CREATE INDEX surface_occurrences_total ON surface_occurrences (total DESC);

DROP INDEX IF EXISTS surface_occurrences_linked;
CREATE INDEX surface_occurrences_linked ON surface_occurrences (linked DESC);

DROP INDEX IF EXISTS commonness_surface;
CREATE INDEX commonness_surface ON commonness (surface);

DROP INDEX IF EXISTS commonness_concept_id;
CREATE INDEX commonness_concept_id ON commonness (concept_id);

DROP INDEX IF EXISTS relatedness_concept_id1;
CREATE INDEX relatedness_concept_id1 ON relatedness (concept_id1);

DROP INDEX IF EXISTS relatedness_concept_id2;
CREATE INDEX relatedness_concept_id2 ON relatedness (concept_id2);
