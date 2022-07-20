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

SELECT M_At(mt, 2) FROM usertrajs;
	------>Return: (1,286114,"{""(40.77,-73.96)""}","{""2017-09-02 08:14:23""}")

SELECT M_NumOf(mt) FROM usertrajs;
	------>Return: 2

SELECT M_Time(mt) FROM usertrajs;
	------>Return:(1504354462000,1504354501000)

SELECT M_StartTime(mt) FROM usertrajs;
	------>Return:1504354462000

SELECT M_EndTime(mt) FROM usertrajs;
	------>Return:1504354501000

SELECT M_Spatial(mt) FROM usertrajs;
	------>Return:LINESTRING(40.77 -73.95,40.77 -73.96)

SELECT M_Snapshot(mt, 1000) FROM usertrajs;
	------>Return:POINT(40.77 -73.95)

SELECT M_Slice(mt, 'Period (1100, 1200)') FROM usertrajs;
	------>Return:(1,286114,"{""(40.77,-73.95)"",""(40.77,-73.96)""}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}")

SELECT M_Lattice(mt, 2000) FROM usertrajs;
	------>Return:(1,286114,"{""(40.77,-73.95)""}","{""2017-09-02 08:14:22""}")

SELECT M_tOverlaps(mt, 'Period (1100, 2200)') FROM usertrajs;
	------>Return: true

### Spatial and spatiotemporal queries

SELECT M_TimeAtCummulative('MPOINT ((40.67 -73.83) 1000,(41.67 -73.81) 2000)', 2);
	------>Return: 1504354471666

SELECT M_Slice('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 'POLYGON ((39 -74, 39 -72, 43 -72, 43 -74, 39 -74))');
	------>Return:(,,"{""(40.77,-73.95)"",""(40.77,-73.96)""}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 

SELECT M_SnapToGrid('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 1);
	------>Return:(,,"{""(40.8,-74.0)"",""(40.8,-74.0)""}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 

SELECT M_mEnters('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 'POLYGON ((39 -74, 39 -72, 43 -72, 43 -74, 39 -74))');
	------>Return:false

SELECT M_mBypasses('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 'POLYGON ((39 -74, 39 -72, 43 -72, 43 -74, 39 -74))');
	------>Return:false

SELECT M_mStayIn('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 'POLYGON ((39 -74, 39 -72, 43 -72, 43 -74, 39 -74))');
	------>Return:true

SELECT M_mLeaves('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 'POLYGON ((39 -74, 39 -72, 43 -72, 43 -74, 39 -74))');
	------>Return:false

SELECT M_mCrosses('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 'POLYGON ((39 -74, 39 -72, 43 -72, 43 -74, 39 -74))');
	------>Return:false
	
SELECT M_Direction('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)');
	------>Return: (,,"{0,-0.08}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 
	
SELECT M_VelocityAtTime('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 1500);
	------>Return: 1.37
	
SELECT M_AccelerationAtTime('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 1500);
	------>Return: 0.0006
	
SELECT M_Max('MDOUBLE (1.002 1503828254949, 1.042 1503828254969)');
	------>Return: 1.042
	
SELECT M_Min('MDOUBLE (1.002 1503828254949, 1.042 1503828254969)');
	------>Return: 1.002
	
SELECT M_Avg('MDOUBLE (1.002 1503828254949, 1.042 1503828254969)');
	------>Return: 1.022
	
SELECT M_DWithin('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 
	'MVIDEO ((00001.mp4?t=1 10 -1 0.1 -5.59 -1 -1 null null 40.67 -73.83) 1000, 
		(00001.mp4?t=2 10 -1 0.1 -5.61 -1 -1 null null 41.67 -73.81) 2000)', 500);
	------>Return: true
