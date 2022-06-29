

SELECT M_TimeAtCummulative(mt, 2) FROM cartrajs;
	------>Return: 1504354471666

SELECT M_Slice(mt, 'POLYGON ((39 -74, 39 -72, 43 -72, 43 -74, 39 -74))') FROM cartrajs;
	------>Return:(1,286114,"{""(40.77,-73.95)"",""(40.77,-73.96)""}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 

SELECT M_SnapToGrid(mt, 1) FROM cartrajs;
	------>Return:(1,286114,"{""(40.8,-74.0)"",""(40.8,-74.0)""}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 

SELECT M_mEnters(mt, 'POLYGON ((39 -74, 39 -72, 43 -72, 43 -74, 39 -74))') FROM cartrajs;
	------>Return:false

SELECT M_mBypasses(mt, 'POLYGON ((39 -74, 39 -72, 43 -72, 43 -74, 39 -74))') FROM cartrajs;
	------>Return:false

SELECT M_mStayIn(mt, 'POLYGON ((39 -74, 39 -72, 43 -72, 43 -74, 39 -74))') FROM cartrajs;
	------>Return:true

SELECT M_mLeaves(mt, 'POLYGON ((39 -74, 39 -72, 43 -72, 43 -74, 39 -74))') FROM cartrajs;
	------>Return:false

SELECT M_mCrosses(mt, 'POLYGON ((39 -74, 39 -72, 43 -72, 43 -74, 39 -74))') FROM cartrajs;
	------>Return:false 
	
SELECT M_Area(mv) FROM uservideos;
	------>Return: (1,286114,"{57.7,57.7}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 
	
SELECT M_Direction(mt) FROM cartrajs;
	------>Return: (1,286114,"{0,-0.08}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 
	
SELECT M_VelocityAtTime(mt, 2000) FROM cartrajs;
	------>Return: 1.37
	
SELECT M_AccelerationAtTime(mt, 2000) FROM cartrajs;
	------>Return: 0.0006
	
SELECT M_Max('MDOUBLE (1.002 1503828254949, 1.042 1503828254969)');
	------>Return: 1.8
	
SELECT M_Min('MDOUBLE (1.002 1503828254949, 1.042 1503828254969)');
	------>Return: 0.6
	
SELECT M_Avg('MDOUBLE (1.002 1503828254949, 1.042 1503828254969)');
	------>Return: 0.9
		