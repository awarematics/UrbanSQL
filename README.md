# UrbanSQL
UrbanSQL is an open source database extension based on PostgreSQL and PostGIS for managing spatio-temporal data  
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
  wget https://github.com/awarematics/mgeometry/blob/master/PostgreSQL/proj_berlinmod/target/proj-0.0.1-SNAPSHOT.jar    
  wget https://github.com/awarematics/mgeometry/blob/master/PostgreSQL/proj_berlinmod/jts-core-1.15.0-SNAPSHOT.jar  
  ```
  ###### Open database or pgAdmin 
  ```
  select sqlj.install_jar('file:/tmp/proj/target/proj-0.0.1-SNAPSHOT.jar', 'jar1', true);    
  select sqlj.install_jar('file:/tmp/jts-core-1.15.0-SNAPSHOT.jar', 'jar2', true);    
  select sqlj.set_classpath('public', 'jar1:jar2');    
  select sqlj.get_classpath('public'); 
  ```
  ###### Download UrbanSQL execution functions
   cd /tmp/
   wget https://github.com/awarematics/UrbanSQL/tree/main/PostgreSQL/01.install 
   su - 
   su postgres
   psql -h localhost -p 5432 -U postgres -d postgres -f /tmp/01.install/installation.sql
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
  CREATE TABLE trip(
	carId INTEGER PRIMARY KEY,
	plateNum VARCHAR,
	tripId INTGER
	);
// Add a trip column as a mpoint type.	
SELECT addmgeometrycolumn( 'trip', 'mpoint', 'mpoint');

// detail settings : Add a trip column as a mpoint type with 
// 'public' : The schema_name is the name of the table schema
// 4326 :  The srid must be an integer value reference to an entry in the SPATIAL_REF_SYS table
// 2 :  Dimension
// 50 : Number of splits
 SELECT addmgeometrycolumn('public', 'Trip', 'mpoint', 4326, 'mpoint', 2, 50);

```
### Insert a feature object 
```

insert into Trip values(1, '22A0001', 1);
insert into Trip values(1, '22A0001', 2);
insert into Trip values(2, '22A0002', 1);
insert into Trip values(2, '22A0002', 2);
insert into Trip values(3, '22A0002', 1);
insert into Trip values(3, '22A0002', 2);


### Insert a mpoint trip for a feature object 
```
Update



```
### Temporal queries

```

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
``` 
### K Nearest Neighbors Query
```
SELECT M_KNN(t.mpoint,'POINT (1 5)',3)
FROM Trip t;

SELECT M_KNN(t.mpoint,p.point,3)
FROM Trip t,Points p;

SELECT M_KNN(t1.mpoint,t2.mpoint,3)
FROM Trip t,Trip t;
```
