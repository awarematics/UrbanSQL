# UrbanSQL
UrbanSQL is an open source database extension based on PostgreSQL and PostGIS for managing spatiotemporal data  
***
# Installation
## Requirements
 1. CentOS 7
 2. PostgreSQL 11
 3. JDK 1.8
 4. Maven 3.3.9 (Optional)
 5. PL\Java 1.5.6
 6. PostGIS 2.5
 7. UrbanSQL
### 2. PostgreSQL 11
  ```
  sudo yum install -y https://download.postgresql.org/pub/repos/yum/reporpms/EL-7-x86_64/pgdg-redhat-repo-latest.noarch.rpm    
  sudo yum install centos-release-scl-rh llvm-toolset-7-clang  centos-release-scl    
  sudo yum install gcc-c++    
  sudo yum install https://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm     
  sudo yum install postgresql11 postgresql11-server postgresql11-contrib postgresql11-devel   
  ```
  ###### Initialize database
  ```
  sudo /usr/pgsql-11/bin/postgresql-11-setup initdb 
  ```
  ###### Start
  ```
  sudo systemctl start postgresql-11  
  ```
  ###### Update postgres password
  ```
  su -   
  su postgres    
  psql    
  ALTER USER postgres WITH PASSWORD 'password';    
  ```
  ###### Permanently disable firewall  
  ```
  sudo firewall-cmd --add-port=5432/tcp --permanent  
  sudo firewall-cmd --reload  
  ```
  ###### Set connection
  ```
  sudo vi /var/lib/pgsql/11/data/postgresql.conf  
  listen_addresses='*'  

  sudo vi /var/lib/pgsql/11/data/pg_hba.conf  
  host  all  all 0.0.0.0/0 md5  
		
  sudo systemctl restart postgresql-11  
  ```
### 3. JDK 1.8
  ```
  sudo yum  install java-1.8.0-openjdk-devel.x86_64  
  ```
### 4. Maven 3.3.9 (Optional)  
```
  wget https://mirror.navercorp.com/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz   
  tar -zxvf apache-maven-3.3.9-bin.tar.gz  
  
  mv apache-maven-3.3.9 maven  
  sudo vim  /etc/profile  
```  
  ###### Add tail of profile
  ```
  MAVEN_HOME=/home/Your username/maven  
  export PATH=\\\${MAVEN\\\_HOME}/bin:\\\${PATH}   
	
  PATH="$PATH:/home/Your username/maven/bin:/usr/pgsql-11/bin"  
  export PATH
  ```
  ###### Restart profile
  ```
  source /etc/profile  
  ```
  ###### Test
  ```
  mvn -v  
  ```
  ### 5. PL\Java 1.5.6
  ```
  sudo yum install pljava-11.x86_64  
  ```
  ###### Setting in PostgreSQL for PL/Java
  ```
  cd /var/lib/pgsql/11/data  
  sudo vi postgresql.conf  
  ```
  ###### Add the following lines for the PL/Java setting
  ```
  pljava.classpath='/usr/pgsql-11/share/pljava/pljava-1.5.6.jar'  
  pljava.libjvm_location='/usr/lib/jvm/java-1.8.0-openjdk/jre/lib/amd64/server/libjvm.so'  
  ```
  ###### Restart PostgreSQL
  ```
  systemctl restart postgresql-11.service 
  ```
  ### 6. PostGIS 2.5
  ```
  sudo yum install -y libtool libxml2 libxml2-devel libxslt libxslt-devel json-c json-c-devel cmake gmp gmp-devel mpfr mpfr-devel boost-devel pcre-  devel   
  sudo yum install -y postgis25_11  
  systemctl restart postgresql-11.service   
  ```
  ### 7. UrbanSQL
  ```
  cd /tmp   
  wget https://github.com/awarematics/UrbanSQL/blob/main/proj/target/mgeometry-0.0.2
  wget https://github.com/awarematics/UrbanSQL/blob/main/proj/jts-core-1.15.0-SNAPSHOT.jar
  ```
  ###### Open database or pgAdmin 
  ```
  create extension postgis;
  create extension pljava;
  
  select sqlj.install_jar('file:/tmp/proj/target/mgeometry-0.0.2.jar', 'jar1', true);    
  select sqlj.install_jar('file:/tmp/jts-core-1.15.0-SNAPSHOT.jar', 'jar2', true);    
  select sqlj.set_classpath('public', 'jar1:jar2');    
  select sqlj.get_classpath('public'); 
  ```
  ###### Download UrbanSQL execution functions
   ```
   cd /tmp/
   wget https://github.com/awarematics/UrbanSQL/tree/main/PostgreSQL/01.install 
   su - 
   su postgres
   psql -h localhost -p 5432 -U postgres -d postgres -f /tmp/01.install/installation.sql
   ```
  ###### 
# Tutorials 
## Supported Types
```
  MInt :  MINT (2 1000, 3 2000, ...)

  MBool :  MBOOL (ture 1000, false 1000, true ...)  

  MDouble : MDOUBLE (1.10 1000, 2.20 2000 ...)

  MMultiPoint :  MMUltiPoint (((0 0) 1000, (1 1) 2000, ...) ...)

  MString :  MSTRING (disjoint 1000, meet 2000 ...)

  MPoint :  MPOINT ((0.0 0.0) 1000, (2.0 5.0) 2000 ...)
 
  MLineString :  MLINESTRING ((-1 0, 0 0, 0 0.5, 5 5) 1000, (0 0, -1 0) 2000 ...)

  MPolygon : MPOLYGON ((0 0, 1 1, 1 0, 0 0) 1000, (0 0, 1 1, 1 0, 0 0) 2000 ...)
```
### Create TABLE and Update data 

```
  CREATE TABLE trips(
	carId INTEGER PRIMARY KEY,
	plateNum VARCHAR,
	tripId INTGER
	);
// Add a trip column as a mpoint type.	
SELECT addmgeometrycolumn( 'traj', 'mpoint', 'mpoint');

// detail settings : Add a trip column as a mpoint type with 
// 'public' : The schema_name is the name of the table schema
// 4326 :  The srid must be an integer value reference to an entry in the SPATIAL_REF_SYS table
// 2 :  Dimension
// 50 : Number of splits
 SELECT addmgeometrycolumn('public', 'trip', 'mpoint', 4326, 'mpoint', 2, 50);

```
### Insert a feature object 
```

insert into trips values(1, '22A0001', 1 );

insert into trips values(2, '22A0002', 1 );

```

### Insert a mpoint trip for a feature object 

<img src="https://github.com/awarematics/UrbanSQL/blob/main/lou/example.jpg" width="50%" height="50%" alt="example"/><br/>

```

UPDATE trips 
SET    mpoint = append(mpoint, ('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)' ) 
WHERE  carid  = 1
AND    tripid = 1;

UPDATE trips 
SET    mpoint = append(mpoint, ('MPOINT ((3 4) 2000, (5 4) 3000), (8 5) 4000), (10 7) 5000), (7 8) 6000), (2 5) 7000)' ) 
WHERE  carid = 2
AND    tripid = 1;

```
### Temporal queries

```

Query 1 : Return the first point of a trjaectory
SELECT M_At('MPOINT ((6 6) 4000, (10 2) 5000)', 1);  
	------>Return: MPOINT ((6.0 6.0) 4000)
	
Query 2 : Return the first point of a trjaectory in the trips table	
SELECT M_At(traj，1)
FROM trips;
----> carId   tripId   m_at()
        1      1       MPOINT ((3 6) 1000)
        2      1       MPOINT ((3 4) 2000)     

Query 3 : Return number of a trajectory
SELECT M_NumOf('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)');
	------>Return: 7

Query 4 : Return number of a trajectories in the trips table
SELECT carId, tripId, M_NumOf( traj )
FROM trips;
----> carId   tripId   m_numof()
        1      1          6
        2      1          7   

Query 5 : Return time range of a trajectory
SELECT M_TIME( 'MPOINT ((6 6) 4000, (10 2) 5000)' );
    ---> Return ( 4000 5000 )

Query 6 : Return time range of a trajectories in the trips table
SELECT M_TIME( traj )
FROM trips;
--->        m_time
	( 1000 8000 )
        ( 2000 7000 )

Query 7 : Return start time of a trajectory
SELECT M_StartTime('MPOINT ((3 6) 1000, (7 3) 6000), (3 2) 7000)');
	------>Return: 1000

Query 8 : Return time range of a trajectories in the trips table	
SELECT M_StartTime(traj)
FROM Trips;
--->    M_StartTime(traj)
	     1000
	     2000

Query 9 : Return end time of a trajectory
SELECT M_EndTime('MPOINT ((3 4) 2000, (5 4) 3000), (8 5) 4000), (10 7) 5000), (7 8) 6000), (2 5) 7000)');
	------>Return: 7000

Query 10 : Return end time of a trajectories in the trips table
SELECT M_EndTime(traj)
FROM Trips;
--->    M_EndTime()
	   7000
	   7000
	     
Query 11 : Return the geometry representation of a trajectory
SELECT M_Spatial('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)');
	------>Return:LINESTRING (3 6, 4 7, 5 6, 7 6, 10 2, 7 3, 3 2)

Query 12 : Return the geometry representation of a trajectories in the trips table
SELECT M_Spatial(traj)
FROM Trips;
---> M_Spatial()
LINESTRING (3 6, 4 7, 5 6, 7 6, 10 2, 7 3, 3 2)
LINESTRING (3 4, 5 4, 8 5, 10 7, 7 8, 2 5)

Query 13 : Return to a geometry representation of the trajectory of 1000 instant
SELECT M_Snapshot('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)',1000);
	------>Return: POINT (3 6)

Query 14 : Return the geometry representation of the trajectories of 1000 instant in the trips table	
SELECT M_Snapshot(traj)
FROM Trips;
---> M_snapshot()
      POINT (3 6)
      MPOINT()

Query 15 : Return a sliced sub trajectory by a period	
SELECT M_Slice('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)','Period (2000, 5000)');
	------>Return: MPOINT ((4.0 7.0) 2000, (5.0 6.0) 3000, (7.0 6.0) 4000, (10.0 2.0) 5000)

Query 16 : Return a sliced sub trajectory by a period in the trips table		
SELECT M_Slice(traj, 'Period (2500, 5500)')
FROM Trips;
---> M_Slice()
    MPOINT ((5.0 6.0) 3000, (7.0 6.0) 4000, (10.0 2.0) 5000)
    MPOINT ((5.0 4.0) 3000, (8.0 5.0) 4000, (10.0 7.0) 5000)

Query 17 : Return a latticed trajectory by 500 intant 		
SELECT M_Lattice('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)', 500);
	------>Return: MPOINT ((3.5 6.5) 1500, (4.0 7.0) 2000, (4.5 6.5) 2500, (5.0 6.0) 3000, (6.0 6.0) 3500, (7.0 6.0) 4000,
	               (8.5 4.0) 4500, (10.0 2.0) 5000, (8.5 2.5) 5500, (7.0 3.0) 6000, (5.0 2.5) 6500)

Query 18 : Return a latticed trajectories by 500 intant in the tirps table		
SELECT M_Lattice(traj,3000)
FROM Trips;
--->  M_Lattice
    MPOINT ((5.0 6.0) 3000, (7.0 3.0) 6000)
    MPOINT ((5.0 4.0) 3000, (7.0 8.0) 6000)
 
Query 19 : Return TRUE if a trajectory "period overlap"		
SELECT M_Overlaps('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)','Period (2000, 7000)');
	------>Return: TRUE

Query 20 : Returns TRUE if trajectories "period overlap" in the trips table		
SELECT *
FROM Trips
WHERE M_tOverlaps(traj, 'Period (1100, 2200)') ;
--->  m_overlaps
       TRUE
       TRUE
       
### Spatial and spatiotemporal queries

Query 21 : Return sum of total distance		
SELECT M_TimeAtCummulative('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)', 1);
	------>Return: 1000
	
Query 22 : Return sum of total distance in the trips table	
SELECT M_TimeAtCummulative(traj,1)
FROM Trips
--->  m_timeatcummulative
	   1000
	   1000

Query 23 : Returns TRUE if trajectories "spatially enter"
SELECT M_Enters('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)', 'POLYGON ((3 4, 3 7, 4 7, 4 3))');
	------>Return:true

Query 24 : Returns TRUE if trajectories "spatially enter" in the trips table		
SELECT carid, tripid
FROM trips
WHERE M_Enters( traj, 'POLYGON ((3 4, 3 7, 4 7, 4 3))' 
---> m_eneters
       true
       true

Query 25 : Return true if their intersection "spatially leave"	
SELECT M_Bypasses('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)', 'POLYGON ((39 -74, 39 -72, 43 -72, 43 -74, 39 -74))');
	------>Return:false

Query 26 : Return true if their intersection "spatially pass" in the trips table
SELECT M_Bypasses( traj, 'POLYGON ((3 4, 3 7, 4 7, 4 3))' 
FROM trips
WHERE M_Bypasses( traj, 'POLYGON ((3 4, 3 7, 4 7, 4 3))' 
---> M_bypasses
       true
       true

Query 27 : Return true if their intersection "spatially leave" 
SELECT M_Leaves('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)', 'POLYGON ((39 -74, 39 -72, 43 -72, 43 -74, 39 -74))');
	------>Return:false

Query 28 : Return true if their intersection "spatially leave" in the trips table
SELECT M_Leaves( traj, 'POLYGON ((3 4, 3 7, 4 7, 4 3))' 
FROM trips
WHERE M_Leaves( traj, 'POLYGON ((3 4, 3 7, 4 7, 4 3))' 
---> M_leaves
       true
       true

Query 29 : Return true if their intersection "spatially cross"
SELECT M_Crosses('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)', 'POLYGON ((39 -74, 39 -72, 43 -72, 43 -74, 39 -74))');
	------>Return:false

Query 30 : Return true if their intersection "spatially cross" in the trips table
SELECT M_Crosses( traj, 'POLYGON ((3 4, 3 7, 4 7, 4 3))' 
FROM trips
WHERE M_Crosses( traj, 'POLYGON ((3 4, 3 7, 4 7, 4 3))' 
---> M_leaves
       true
       true

Query 31 : Returns direction of trajectories 
SELECT M_Direction('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)');
	------> Return: MDOUBLE (63.63961030678928 1000, 63.63961030678928 2000, -63.63961030678928 3000, 0.0 4000, NaN 5000, 28.460498941515414 6000, -21.828206253269965 7000)

Query 32 : Returns direction of trajectories in the trips table
SELECT M_Direction(trip)
FROM trips
---> m_direction
	MDOUBLE (63.63961030678928 1000, 63.63961030678928 2000, -63.63961030678928 3000, 0.0 4000, NaN 5000, 28.460498941515414 6000, -21.828206253269965 7000)
	MDOUBLE (0.0 2000, 0.0 3000, 28.460498941515414 4000, NaN 5000, 28.460498941515414 6000, NaN 7000)
	
Query 33 : Returns each distance on the instantaneous merge set of two trajectories
SELECT M_Distance('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)' ,
		  'MPOINT ((3 4) 2000, (5 4) 3000), (8 5) 4000), (10 7) 5000), (7 8) 6000), (2 5) 7000)')
	------> MDOUBLE (0.0 1000, 3.1622776601683795 2000, 2.5495097567963922 2500, 2.0 3000, 1.4142135623730951 4000, 0.9455009254358242 4266, 5.0 5000, 5.0 6000, 3.1622776601683795 7000)

Query 34 : Return M_Distance for Trips table and trajectory
SELECT M_Distacne(trip,'MPOINT ((1 1) 1000, (2 2) 2000), (3 3) 3000), (4 5) 4000)')
FROM trips
---> m_distance()
	MDOUBLE (5.385164807134504 1000, 5.385164807134504 2000, 3.605551275463989 3000, 3.1622776601683795 4000, 3.1622776601683795 5000, 3.1622776601683795 6000, 3.1622776601683795 7000)
	MDOUBLE (0.0 1000, 2.23606797749979 2000, 2.1505813167606567 2250, 2.23606797749979 3000, 4.0 4000, 4.0 5000, 4.0 6000, 4.0 7000)


Query 35 : Return whether the distance between trajectory and geography is within 100m
SELECT M_MinDistacne(trip,'POINT (-73.9917157777343 40.7424697420008)'::geometry, 100.0)
------> Return : false

Query 36 : Return whether the distance between trajectory and geography is within 100m in the trips table
SELECT M_MinDistacne(trip,'POINT (-73.9917157777343 40.7424697420008)'::geometry, 100.0)
------> m_mindistance
	    false
	    false


Query 37 : Return whether the distance between 2 trajectoroies is within 100m
SELECT M_MinDistacne('MPOINT ((3 4) 2000, (5 4) 3000), (8 5) 4000), (10 7) 5000), (7 8) 6000), (2 5) 7000)','MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)',100.0 )
------> Return ：true

Query 38 : Return whether the distance between 2 trajectoroies is within 100m in the trips table
SELECT M_MinDistacne(trip,'MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)',100.0 )
---> m_mindistance
	true
	true
	

``` 
### K Nearest Neighbors Query
```
Query 32 : For each trajectory from Trip, list the 3 trajectories that have been closest to given point.
SELECT M_knn(t.traj,'POINT (1 5)',3)
FROM Trip t;

Query 33 : For each trajectory from Trip, list the 3 nearest points from POI that have been closest to each trajectory. 
SELECT M_knn(t.traj,p.point,3)
FROM Trip t, POI p;

Query 34 : For each trip from Trips, list the 3 trips that are closest to that this one
SELECT M_knn(t1.traj,t2.traj,3)
FROM Trip t1, Trip t2;
```
