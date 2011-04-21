INSERT INTO wiki_surfaces(surface)
    SELECT DISTINCT surface FROM wiki_links;
