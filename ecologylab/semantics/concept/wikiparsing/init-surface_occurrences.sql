TRUNCATE surface_occurrences;
INSERT INTO surface_occurrences SELECT surface, 0, 0 FROM freq_surfaces;
