

UPDATE bdd100k 
SET    mvideo = append(mvideo, ('POINT (200 200)'::geometry)::point, '1180389003000'::bigint, 1.0, 2.0, 3.0, 5.0, 6.0, 'http://u-gist/1.mp4') 
WHERE  taxi_id = 1;
	