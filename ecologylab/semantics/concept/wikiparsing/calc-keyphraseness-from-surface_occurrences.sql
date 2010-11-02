INSERT INTO keyphraseness
	SELECT surface, labeled*1.0/total AS keyphraseness
	FROM surface_occurrences
	WHERE total > 0;
	