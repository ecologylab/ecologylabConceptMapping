INSERT INTO inlink_count
  SELECT to_id, count(from_id) AS inlink_count FROM wiki_links GROUP BY to_id;