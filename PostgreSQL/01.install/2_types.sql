CREATE TYPE mgeometry AS
(
   moid oid,
   segid text 
);

CREATE TYPE fov AS
(
   horizontalAngle double precision,
   verticalAngle double precision,
   direction2d double precision,
   direction3d double precision,
   distance double precision
);


CREATE TYPE frame AS
(
   relativeTime bigint,
   framefov fov,   
   geo      point
);


CREATE TYPE period AS
(
	fromtime bigint,
	totime bigint
);


CREATE TYPE mpoint AS
(
	geo point[],
	t bigint[]
);

CREATE TYPE mvideo AS
(
   uri text,
   annotations text[],
   mgeo mpoint, 
   startTime bigint[],
   frames frame[],
   afov fov             
);


CREATE TYPE mperiod AS
(
	fromtime bigint[],
	totime bigint[]
);


CREATE TYPE mdouble AS
(
	doubles double precision[],
	t 	bigint[]
);


CREATE TYPE mbool AS
(
	bools boolean[],
	t 	bigint[]
);



CREATE TYPE mpolygon AS
(
	polygons polygon[],
		t bigint[]
);



CREATE TYPE mduration AS   ----continue time
(
	duration bigint[]
);

CREATE TYPE minstant AS    ----instant
(
	t bigint[]
);

CREATE TYPE mint AS   
(
	ints integer[]
);


CREATE TYPE mstring AS
(
	mstrings text[],
	t bigint[]
);

CREATE TYPE mlinestring AS
(
	mlinestrings text[],
	t bigint[]
);











