
-- this script imports surfaces from wiki_links to wiki_surfaces, and initialize linked_occurrence
-- for them. total_occurrence will be set to 0.
 
TRUNCATE wiki_surfaces;
INSERT INTO wiki_surfaces
  SELECT surface, 0, count(surface)
  FROM wiki_links
  GROUP BY surface
  ORDER BY count(surface) DESC;

-- add fkey constraint to wiki_links on surface.
