

CREATE TABLE carTrajs 
(
	id	integer,
   	carnumber  text
);

CREATE TABLE carSensors
(
	id	integer,
	carnumber	text
);

CREATE TABLE userVideos 
(
	id	integer,
   	name  text
);

CREATE TABLE cityLandmark
(
	id	integer,
	name	text,
	geo	polygon
);


CREATE TABLE userStphotos 
(
	id	integer,
   	carnumber  text
);







select addmgeometrycolumn('public','cartrajs','st',4326,'stphoto',2, 1);


select addmgeometrycolumn('public','cartrajs','mt',4326,'mpoint',2, 50);

select addmgeometrycolumn('public','cartrajs','accx',4326,'mdouble',2, 50);
select addmgeometrycolumn('public','cartrajs','accy',4326,'mdouble',2, 50);
select addmgeometrycolumn('public','cartrajs','accz',4326,'mdouble',2, 50);
select addmgeometrycolumn('public','cartrajs','gyrox',4326,'mdouble',2, 50);
select addmgeometrycolumn('public','cartrajs','gyroy',4326,'mdouble',2, 50);
select addmgeometrycolumn('public','cartrajs','gyroz',4326,'mdouble',2, 50);


select addmgeometrycolumn('public','cartrajs','mv',4326,'mvideo',2, 50);


select addmgeometrycolumn('public','carsensors','accx',4326,'mdouble',2, 50);
select addmgeometrycolumn('public','carsensors','accy',4326,'mdouble',2, 50);
select addmgeometrycolumn('public','carsensors','accz',4326,'mdouble',2, 50);
select addmgeometrycolumn('public','carsensors','gyrox',4326,'mdouble',2, 50);
select addmgeometrycolumn('public','carsensors','gyroy',4326,'mdouble',2, 50);
select addmgeometrycolumn('public','carsensors','gyroz',4326,'mdouble',2, 50);


select addmgeometrycolumn('public','uservideos','mv',4326,'mvideo',2, 50);
