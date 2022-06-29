SELECT M_Intersects(a.mt, b.mv) FROM cartrajs a, userVideos b;
	------>Return:(1,286114,"{t,t}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 


SELECT M_EvenTime(a.mt, b.mv) FROM cartrajs a, userVideos b;
	------>Return: (1,286114,"{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 
	
SELECT M_Relationship(a.mt, b.mv) FROM cartrajs a, userVideos b;
	------>Return: (1,286114,"{meet"",inside""}") 

SELECT M_Inside(a.mt, b.mv) FROM cartrajs a, userVideos b;
	------>Return: (1,286114,"{f,t}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 

SELECT M_Equal(a.mt, b.mv) FROM cartrajs a, userVideos b;
	------>Return: (1,286114,"{f,f}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 
	
SELECT M_Overlaps(a.mt, b.mv) FROM cartrajs a, userVideos b;
	------>Return: (1,286114,"{f,f}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 
	
SELECT M_Disjoint(a.mt, b.mv) FROM cartrajs a, userVideos b;
	------>Return: (1,286114,"{f,f}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 
	
SELECT M_Meet(a.mt, b.mv) FROM cartrajs a, userVideos b;
	------>Return: (1,286114,"{t,f}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 
	
SELECT M_Distance(a.mt, b.mv) FROM cartrajs a, userVideos b;
	------>Return: (1,286114,"{0,0}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 
	
SELECT M_DWithin(a.mt, b.mv, 500) FROM cartrajs a, userVideos b;
	------>Return: true
	
SELECT M_Hausdorff(a.mt, b.mv) FROM cartrajs a, userVideos b;
	------>Return: 0

SELECT M_LCSS(a.mt, b.mv) FROM cartrajs a, userVideos b;
	------>Return: 1

SELECT M_Traclus(a.mt, b.mv) FROM cartrajs a, userVideos b;
	------>Return: 0.00145

SELECT M_LCVS(a.mv, b.mv) FROM userVideos a, userVideos b;
	------>Return: 1
	
	

SELECT M_Intersects(a.st, b.mv) FROM userstphotos a, userVideos b;
	------>Return:(5,287557,{f},"{""1969-12-31 19:00:01""}")

SELECT M_Intersects(a.st, b.mt) FROM userstphotos a, cartrajs b;
	------>Return:(5,287557,{f},"{""1969-12-31 19:00:01""}")
	
		
SELECT M_Relationship( b.mv, a.st) FROM userstphotos a, userVideos b;
	------>Return: (1,286114,"{meet"",inside""}") 
	
SELECT M_Relationship( b.mt, a.st) FROM userstphotos a, cartrajs b;
	------>Return: (1,286114,"{meet"",inside""}") 
	
	
SELECT M_Inside(a.st, b.mv) FROM userstphotos a, userVideos b;
	------>Return: (1,286114,"{f,t}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 

SELECT M_Inside(a.st, b.mt) FROM userstphotos a, cartrajs b;
	------>Return: (1,286114,"{f,t}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 
	
SELECT M_Equal(a.st, b.mv) FROM userstphotos a, userVideos b;
	------>Return: (1,286114,"{f,f}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 
	
SELECT M_Equal(a.st, b.mt) FROM userstphotos a, cartrajs b;
	------>Return: (1,286114,"{f,f}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 
	

SELECT M_Overlaps(a.st, b.mv) FROM userstphotos a, userVideos b;
	------>Return: (1,286114,"{f,f}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 
	
SELECT M_Overlaps(a.st, b.mt) FROM userstphotos a, cartrajs b;
	------>Return: (1,286114,"{f,f}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 
	
SELECT M_Disjoint(a.st, b.mv) FROM userstphotos a, userVideos b;
	------>Return: (1,286114,"{f,f}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 
	
SELECT M_Disjoint(a.st, b.mt) FROM userstphotos a, cartrajs b;
	------>Return: (1,286114,"{f,f}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 
	
SELECT M_Meet(a.st, b.mv) FROM userstphotos a, userVideos b;
	------>Return: (1,286114,"{t,f}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 
	
SELECT M_Meet(a.st, b.mt) FROM userstphotos a, cartrajs b;
	------>Return: (1,286114,"{t,f}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 
	
SELECT M_Distance(a.st, b.mv) FROM userstphotos a, userVideos b;
	------>Return: (1,286114,"{0,0}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 
	
SELECT M_Distance(a.st, b.mt) FROM userstphotos a, cartrajs b;
	------>Return: (1,286114,"{0,0}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}") 
	
SELECT M_DWithin(a.st, b.mv, 500) FROM userstphotos a, userVideos b;
	------>Return: true
	
SELECT M_DWithin(a.st, b.mt, 500) FROM userstphotos a, cartrajs b;
	------>Return: true
	
SELECT M_Hausdorff(a.mv, b.mv) FROM userVideos a, userVideos b;
	------>Return: 0

SELECT M_LCSS(a.mv, b.mv) FROM userVideos a, userVideos b;
	------>Return: 1

SELECT M_Traclus(a.mv, b.mv) FROM userVideos a, userVideos b;
	------>Return: 0.00145

SELECT M_LCVS(a.mv, b.mv) FROM userVideos a, userVideos b;
	------>Return: 1