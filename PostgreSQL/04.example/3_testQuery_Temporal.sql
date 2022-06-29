SELECT M_At('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 2);
	------>Return: (,,"{""(40.77,-73.96)""}","{""2017-09-02 08:14:23""}")

SELECT M_NumOf('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)');
	------>Return: 2

SELECT M_Time('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)');
	------>Return:(1504354462000,1504354501000)

SELECT M_StartTime('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)');
	------>Return:1504354462000

SELECT M_EndTime('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)');
	------>Return:1504354501000

SELECT M_Spatial('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)');
	------>Return:LINESTRING(40.77 -73.95,40.77 -73.96)

SELECT M_Snapshot('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 1000);
	------>Return:POINT(40.77 -73.95)

SELECT M_Slice('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 'Period (1100, 1200)');
	------>Return:(,,"{""(40.77,-73.95)"",""(40.77,-73.96)""}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}")

SELECT M_Lattice('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 2000);
	------>Return:(,,"{""(40.77,-73.95)""}","{""2017-09-02 08:14:22""}")

SELECT M_tOverlaps('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 'Period (1100, 2200)');
	------>Return: true
	
	
SELECT M_At(mt, 2) FROM cartrajs;
	------>Return: (1,286114,"{""(40.77,-73.96)""}","{""2017-09-02 08:14:23""}")

SELECT M_NumOf(mt) FROM cartrajs;
	------>Return: 2

SELECT M_Time(mt) FROM cartrajs;
	------>Return:(1504354462000,1504354501000)

SELECT M_StartTime(mt) FROM cartrajs;
	------>Return:1504354462000

SELECT M_EndTime(mt) FROM cartrajs;
	------>Return:1504354501000

SELECT M_Spatial(mt) FROM cartrajs;
	------>Return:LINESTRING(40.77 -73.95,40.77 -73.96)

SELECT M_Snapshot(mt, 1000) FROM cartrajs;
	------>Return:POINT(40.77 -73.95)

SELECT M_Slice(mt, 'Period (1100, 1200)') FROM cartrajs;
	------>Return:(1,286114,"{""(40.77,-73.95)"",""(40.77,-73.96)""}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}")

SELECT M_Lattice(mt, 2000) FROM cartrajs;
	------>Return:(1,286114,"{""(40.77,-73.95)""}","{""2017-09-02 08:14:22""}")

SELECT M_tOverlaps(mt, 'Period (1100, 2200)') FROM cartrajs;
	------>Return: true