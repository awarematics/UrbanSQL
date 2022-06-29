
UPDATE bdd100k 
SET    mpoint = append(mpoint, ('POINT (200 200)'::geometry)::point,'1180389003000'::bigint) 
WHERE  carid = 1;


