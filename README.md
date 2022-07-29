# UrbanSQL
UrbanSQL is an open source database extension based on PostgreSQL and PostGIS for managing spatiotemporal data  
***
# Installation
## Requirements
 1. CentOS 7
 2. PostgreSQL 11
 3. JDK 1.8
 4. Maven 3.3.9(Optional)
 5. PL\JAVA 1.5.6
 6. PostGIS 2.5
### 2. PostgreSQL 11
  ```
  sudo yum install -y https://download.postgresql.org/pub/repos/yum/reporpms/EL-7-x86_64/pgdg-redhat-repo-latest.noarch.rpm    
  sudo yum install  centos-release-scl-rh llvm-toolset-7-clang  centos-release-scl    
  sudo yum install gcc-c++    
  sudo yum install https://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm     
  sudo yum install  postgresql11 postgresql11-server postgresql11-contrib postgresql11-devel   
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
  ### 5. PL\JAVA 1.5.6
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
  wget https://github.com/awarematics/UrbanSQL/blob/main/proj/target/proj-0.0.1-SNAPSHOT.jar 
  wget https://github.com/awarematics/UrbanSQL/blob/main/proj/jts-core-1.15.0-SNAPSHOT.jar
  ```
  ###### Open database or pgAdmin 
  ```
  create extension postgis;
  create extension pljava;
  
  select sqlj.install_jar('file:/tmp/proj/target/mgeometry-0.0.2', 'jar1', true);    
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
  MInt :  MINT (2 1556911346, 3 1556911347, ...)

  MBool :  MBOOL (ture 1000, false 1000, true ...)  

  MDouble : MDOUBLE (1743.6106216698727 1556811344, 1587.846969956488 1556911345 ...)

  MMultiPoint :  MMUltiPoint (((0 0) 1589302899, (1 1) 1589305899, ...) ...)

  MString :  MSTRING (disjoint 1481480632123, meet 1481480637123 ...)

  MPoint :  MPOINT ((0.0 0.0) 1481480632123, (2.0 5.0) 1481480637123 ...)
 
  MLineString :  MLINESTRING ((-1 0, 0 0, 0 0.5, 5 5) 1481480632123, (0 0, -1 0) 1481480637123 ...)

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

insert into Trip values(1, '22A0001', 1);
insert into Trip values(1, '22A0001', 2);
insert into Trip values(2, '22A0002', 1);
insert into Trip values(2, '22A0002', 2);
insert into Trip values(3, '22A0002', 1);
insert into Trip values(3, '22A0002', 2);
```

### Insert a mpoint trip for a feature object 

![Alt text](https://github.com/awarematics/UrbanSQL/blob/main/lou/example.jpg)  
<img src="https://github.com/awarematics/UrbanSQL/blob/main/lou/example.jpg" width="50%" height="50%" alt="example"/><br/>

```
MPOINT( 
UPDATE trip 
SET    mpoint = append(mpoint, ('MPOINT ((1 1) 1180389003000, (1 2) 1180389004000) ' ) 
WHERE  carid  = 1
AND    tripid = 1;

UPDATE trip 
SET    mpoint = append(mpoint, ('MPOINT ((1 3) 1180389005000, (1 4) 1180389006000)' ) 
WHERE  carid = 1
AND    tripid = 2;

UPDATE trip 
SET    mpoint = append(mpoint, ('MPOINT ((2 1) 1180389003000, (2 2) 1180389004000)' ) 
WHERE  carid = 2
AND    tripid = 1;

UPDATE trip 
SET    mpoint = append(mpoint, ('MPOINT ((2 3) 1180389003000, (2 4) 1180389004000)' ) 
WHERE  carid = 2
AND    tripid = 2;

UPDATE trip 
SET    mpoint = append(mpoint, ('MPOINT ((3 1) 1180389003000, (3 2) 1180389004000)' ) 
WHERE  carid = 3
AND    tripid = 1;

UPDATE trip 
SET    mpoint = append(mpoint, ('MPOINT ((3 3) 1180389003000, (3 4) 1180389004000)' ) 
WHERE  carid = 3
AND    tripid = 2;
```
### Temporal queries

```

SELECT M_At('MPOINT ((6 6) 4000, (10 2) 5000)', 4500);  wrong 
SELECT M_At('MPOINT ((6 6) 4000, (10 2) 5000)', 1);  right
	------>Return: MPOINT ((6.0 6.0) 4000)
SELECT M_At('MPOINT ((6 6) 4000, (10 2) 5000)', 4500);


SELECT M_NumOf('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)');
	------>Return: 2
	
SELECT carId, tripId, M_NumOf( traj )
FROM trips;
----> carId   tripId   m_numof()
        1      1          6
        2      1          7   

SELECT M_TIME( 'MPOINT ((6 6) 4000, (10 2) 5000)' );
    ---> Return ( 4000 5000 )
    
SELECT M_TIME( traj )
FROM trips;
--->        m_time
	( 1000 8000 )
        ( 2000 7000 )


SELECT M_StartTime('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)');
	------>Return: 1000
	
SELECT M_StartTime('MPOINT ((3 4) 2000, (5 4) 3000), (8 5) 4000), (10 7) 5000), (7 8) 6000), (2 5) 7000)');
	------>Return: 2000
	
SELECT M_StartTime(traj)
FROM Trips;
--->    M_StartTime(traj)
	     1000
	     2000

SELECT M_EndTime('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)');
	------>Return: 7000
	
SELECT M_EndTime('MPOINT ((3 4) 2000, (5 4) 3000), (8 5) 4000), (10 7) 5000), (7 8) 6000), (2 5) 7000)');
	------>Return: 7000
	
SELECT M_EndTime(traj)
FROM Trips;
--->    M_EndTime()
	     7000
	     7000

SELECT M_Spatial('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)');
	------>Return:LINESTRING (3 6, 4 7, 5 6, 7 6, 10 2, 7 3, 3 2)

SELECT M_Spatial(traj)
FROM Trips
---> M_Spatial()
LINESTRING (3 6, 4 7, 5 6, 7 6, 10 2, 7 3, 3 2)
LINESTRING (3 4, 5 4, 8 5, 10 7, 7 8, 2 5)

SELECT M_Snapshot('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)',1000);
	------>Return:POINT (3 6)
	
SELECT M_Snapshot('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)',1500);
	------>Return:  _______
	
SELECT M_Snapshot(traj)
FROM Trips;
---> M_snapshot()
      POINT (3 6)
      MPOINT()

SELECT M_Slice('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)','Period (2000, 5000)');
	------>Return: MPOINT ((4.0 7.0) 2000, (5.0 6.0) 3000, (7.0 6.0) 4000, (10.0 2.0) 5000)

SELECT M_Slice('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)','Period (2500, 5500)');
	------>Return: ____
	
SELECT M_Slice(traj, 'Period (2500, 5500)')
FROM Trips;
---> M_Slice()
    MPOINT ((4.0 7.0) 2000, (5.0 6.0) 3000, (7.0 6.0) 4000, (10.0 2.0) 5000)
    MPOINT ((3.0 4.0) 2000, (5.0 4.0) 3000, (8.0 5.0) 4000, (10.0 7.0) 5000)

SELECT M_Lattice('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)', 500);
	------>Return:_____
	
SELECT M_Lattice('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)',2000);
	------>Return:MPOINT ((4.0 7.0) 2000, (7.0 6.0) 4000, (7.0 3.0) 6000)

SELECT M_Lattice(Trip,3000)
FROM Trips;
--->  M_Lattice
    MPOINT ((5.0 6.0) 3000, (7.0 3.0) 6000)
    MPOINT ((5.0 4.0) 3000, (7.0 8.0) 6000)
    
SELECT M_Overlaps('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)','Period (2000, 7000)');
	------>Return: true

SELECT M_tOverlaps(traj, 'Period (1100, 2200)') 
FROM Trips;
---> m_overlaps
       true
       true
       
### Spatial and spatiotemporal queries
	
SELECT M_TimeAtCummulative('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)', 1);
	------>Return: POINT ((3.0 6.0) 1000, (4.0 7.0) 2000, (5.0 6.0) 3000, (7.0 6.0) 4000, (10.0 2.0) 5000, (7.0 3.0) 6000, (3.0 2.0) 7000)

SELECT M_Enters('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)', 'POLYGON (______))');
	------>Return:false
	
SELECT carid, tripid
FROM trips
WHERE M_ENETER( traj, 'POLYGON (______))' 

SELECT M_Bypasses('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)', 'POLYGON ((39 -74, 39 -72, 43 -72, 43 -74, 39 -74))');
	------>Return:false

SELECT M_Leaves('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)', 'POLYGON ((39 -74, 39 -72, 43 -72, 43 -74, 39 -74))');

	------>Return:false

SELECT M_Crosses('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)', 'POLYGON ((39 -74, 39 -72, 43 -72, 43 -74, 39 -74))');
	------>Return:false
	
SELECT M_Direction('MPOINT ((3 6) 1000, (4 7) 2000), (5 6) 3000), (7 6) 4000), (10 2) 5000), (7 3) 6000), (3 2) 7000)');
	------>Return: MDOUBLE (63.63961030678928 1000, 63.63961030678928 2000, -63.63961030678928 3000, 0.0 4000, NaN 5000, 28.460498941515414 6000, -21.828206253269965 7000)
	
SELECT M_VelocityAtTime('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 1500);
	------>Return: 1.37
	

	
SELECT M_DWithin('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 
	'MVIDEO ((00001.mp4?t=1 10 -1 0.1 -5.59 -1 -1 null null 40.67 -73.83) 1000, 
		(00001.mp4?t=2 10 -1 0.1 -5.61 -1 -1 null null 41.67 -73.81) 2000)', 500);
	------>Return: true
``` 
### K Nearest Neighbors Query
```
SELECT M_KNN(t.traj,'POINT (1 5)',3)
FROM Trip t;

SELECT M_KNN(t.traj,p.point,3)
FROM Trip t,Points p;

SELECT M_KNN(t1.traj,t2.traj,3)
FROM Trip t,Trip t;
```
