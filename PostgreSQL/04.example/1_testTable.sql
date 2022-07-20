

CREATE TABLE trip 
(
	carid	integer primary key,
        tripid  integer
);


select addmgeometrycolumn('public','trip','traj',4326,'mpoint',2, 50);
