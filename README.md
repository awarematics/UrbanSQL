
### Requirements
 1. CentOS 7
 2. PostgreSQL 11
 3. JDK 1.8
 4. Maven 3.3.9
 5. PL\JAVA 1.5.1
### 2. PostgreSQL 11
  sudo yum install -y https://download.postgresql.org/pub/repos/yum/reporpms/EL-7-x86_64/pgdg-redhat-repo-latest.noarch.rpm
  sudo yum install  centos-release-scl-rh llvm-toolset-7-clang  centos-release-scl
  sudo yum install gcc-c++
  sudo yum install https://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm 
  sudo yum install  postgresql11 postgresql11-server postgresql11-contrib postgresql11-devel
  
  ###### Initialize database
  sudo /usr/pgsql-11/bin/postgresql-11-setup initdb
  ###### Start
  sudo systemctl start postgresql-11
  ###### Update postgres password
  su -
  su postgres
  psql
  ALTER USER postgres WITH PASSWORD 'password';
  ###### Permanently disable firewall
  sudo firewall-cmd --add-port=5432/tcp --permanent
  sudo firewall-cmd --reload
### JDK 1.8
  sudo yum  install java-1.8.0-openjdk-devel.x86_64
### Maven 3.3.9
  wget https://mirror.navercorp.com/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
  tar -zxvf apache-maven-3.3.9-bin.tar.gz
  mv apache-maven-3.3.9 maven
  sudo vim  /etc/profile
  
## Supported MGemoetry Types

	MPoint :  MPOINT ((0.0 0.0) 1481480632123, (2.0 5.0) 1481480637123 ...)

## UrbanSQL SQL Real Examples

### Create TABLE examples 

```
  CREATE TYPE mpoint AS(
   	moid oid,
  	segid text 
);

  CREATE TABLE Trip(
	CarId integer primary key,
	TripId varchar,
	mpid mpoint
);

CREATE TABLE mgeometry_columns
(
	f_table_catalog character varying(256) NOT NULL,
	f_table_schema character varying(256) NOT NULL,
	f_table_name character varying(256) NOT NULL,
	f_mgeometry_column character varying(256) NOT NULL,
	f_mgeometry_segtable_name character varying(256) NOT NULL,
	mgeometry_compress character varying(256),
	coord_dimension integer,
	srid integer,
	"type" character varying(30),
	f_segtableoid character varying(256) NOT NULL,
	f_sequence_name character varying(256) NOT NULL,
	tpseg_size	integer
);


```
### Insert Examples 
```

insert into Trip values(1, 1);
insert into Trip values(1, 2);
insert into Trip values(2, 1);
insert into Trip values(2, 2);
insert into Trip values(3, 1);
insert into Trip values(3, 2);

select addmgeometrycolumn('public', 'Trip', 'mpoint', 4326, 'mpoint', 2, 50);

insert into mpoint_150348 values(1, 1,BOX(13.43593 52.41721,13.43593 52.41721),[1180191600000,1180309007846),ST_GeomFromText(LINESTRING(13.43593 52.41721,13.43593 52.41721)),MPOINT ((13.43593 52.41721) 1180191600000, (13.43593 52.41721) 1180309007846));
insert into mpoint_150348 values(1, 2,BOX(13.47552 52.43829,13.47552 52.43829),[1180438295782,1180442939602),ST_GeomFromText(LINESTRING(13.47552 52.43829,13.47552 52.43829)),MPOINT ((13.47552 52.43829) 1180438295782, (13.47552 52.43829) 1180442939602));
insert into mpoint_150348 values(2, 1,BOX(13.39108 52.58387,13.39108 52.58387),[1180303835745,1180337117147),ST_GeomFromText(LINESTRING(13.39108 52.58387,13.39108 52.58387)),MPOINT ((13.39108 52.58387) 1180303835745, (13.39108 52.58387) 1180337117147));
insert into mpoint_150348 values(2, 2,BOX(13.40434 52.54036,13.40434 52.54036),[1180396529245,1180424269005),ST_GeomFromText(LINESTRING(13.40434 52.54036,13.40434 52.54036)),MPOINT ((13.40434 52.54036) 1180396529245, (13.40434 52.54036) 1180424269005));
insert into mpoint_150348 values(3, 1,BOX(13.54834 52.493,13.54834 52.493),[1180445630247,1180537200000),ST_GeomFromText(LINESTRING(13.54834 52.493,13.54834 52.493)),MPOINT ((13.54834 52.493) 1180445630247, (13.54834 52.493) 1180537200000));
insert into mpoint_150348 values(3, 2,BOX(13.32512 52.45972,13.40489 52.52358),[1180334169257,1180334751818),ST_GeomFromText(LINESTRING(13.54834 52.493,13.54834 52.493)),MPOINT ((13.54834 52.493) 1180445630247, (13.54834 52.493) 1180537200000));



```
### UDF Function Examples 
```

SELECT M_At('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 2);
	------>Return: MPOINT ((41.67 -73.81) 2000)

SELECT M_NumOf('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)');
	------>Return: 2

SELECT M_Time('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)');
	------>Return:(1000,2000)
	
SELECT M_Spatial('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)');
	------>Return: Geometry

SELECT M_StartTime('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)');
	------>Return:1000

SELECT M_EndTime('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)');
	------>Return:2000

SELECT M_Spatial('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)');
	------>Return:LINESTRING(40.77 -73.95,40.77 -73.96)

SELECT M_Snapshot('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 1000);
	------>Return:POINT(40.77 -73.95)

SELECT M_Slice('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 'Period (1100, 1200)');
	------>Return:(,,"{""(40.77,-73.95)"",""(40.77,-73.96)""}","{""2017-09-02 08:14:22"",""2017-09-02 08:14:23""}")

SELECT M_Lattice('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 2000);
	------>Return:(,,"{""(40.77,-73.95)""}","{""2017-09-02 08:14:22""}")

SELECT M_Overlaps('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 'Period (1100, 2200)');
	------>Return: true
	

SELECT M_TimeAtCummulative('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 2);
	------>Return: 1000

SELECT M_Slice('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 'Period (1000, 2000)');
	------>Return: 'MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)'

SELECT M_SnapToGrid('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 1);
	------>Return: 'MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)'

SELECT M_Enters('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 'POLYGON ((39 -74, 39 -72, 43 -72, 43 -74, 39 -74))');
	------>Return:false

SELECT M_Bypasses('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 'POLYGON ((39 -74, 39 -72, 43 -72, 43 -74, 39 -74))');
	------>Return:false

NO
SELECT M_sStayIn('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 'POLYGON ((39 -74, 39 -72, 43 -72, 43 -74, 39 -74))');
	------>Return:true

SELECT M_Leaves('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 'POLYGON ((39 -74, 39 -72, 43 -72, 43 -74, 39 -74))');
	------>Return:false

SELECT M_sCrosses('MPOINT ((40.67 -73.83) 1000, (41.67 -73.81) 2000)', 'POLYGON ((39 -74, 39 -72, 43 -72, 43 -74, 39 -74))');
	------>Return:false 

### SELECT Examples 
--- 
SELECT *
FROM Trip;

---
SELECT carid, M_AsText(mpid)
FROM Trip;

---
SELECT carid, ST_AsText(m_spatial(mpid))
FROM Trip;

---
SELECT carid, M_Time(mpid)
FROM Trip;

``` 

### Range Queries

### Temporal Range Queries
```


-----basic temporal query no index  
explain analyze
select carid, mpid, m_time(mpid)
from Trip 
where m_tintersects_noindex(mpid, '(1504010956999,1504012995999)'::int8range)
and carid <2000;

-----no temporary table with index
explain analyze
select carid, mpid, m_time(mpid)
from Trip
where m_tintersects_index(mpid, '(1504010956999,1504012995999)'::int8range )
and carid <2000;


----- materialized
explain analyze
select carid, mpid, m_time(mpid)
from Trip 
where m_tintersects_materialized(mpid, '(1504010956999,1504012995999)'::int8range)
and carid <2000;






```
---Spatial Range Queries

```


-----basic spatial query no index  
explain analyze
select carid, mpid, m_spatial(mpid)
from Trip 
where m_intersects_noindex(mpid, 'LINESTRING(40 -73,40.7416693959765 -73.9897693321798,40.7416693959765 -73.9897693321798)'::geometry,  '(1414010956999,1504012995999)'::int8range)
and carid <8000;


-----no spatial table with index
explain analyze
select carid, mpid, m_spatial(mpid)
from Trip
where m_intersects_index(mpid, 'LINESTRING(40 -73,40.7416693959765 -73.9897693321798,40.7416693959765 -73.9897693321798)'::geometry, '(1414010956999,1504012995999)'::int8range)
and carid <8000;



-----spatial table with materialized
explain analyze
select carid, mpid, m_spatial(mpid)
from Trip 
where m_intersects_materialized(mpoint, 'LINESTRING(40 -73,40.7416693959765 -73.9897693321798,40.7416693959765 -73.9897693321798)'::geometry, '(1414010956999,1504012995999)'::int8range)
and carid <8000;





```
### Spatial-temporal Range Queries
```




-----basic spatial query no index  
explain analyze
select carid, mpid, m_spatial(mpid)
from Trip 
where m_sintersects_noindex(mpoint, 'LINESTRING(40 -73,40.7416693959765 -73.9897693321798,40.7416693959765 -73.9897693321798)'::geometry)
and carid <2000;


-----no spatial table with index
explain analyze
select carid, mpid, m_spatial(mpid)
from Trip
where m_sintersects_index(mpoint, 'LINESTRING(40 -73,40.7416693959765 -73.9897693321798,40.7416693959765 -73.9897693321798)'::geometry)
and carid <2000;



-----spatial table with index
explain analyze
select carid, mpid, m_spatial(mpid)
from Trip 
where m_sintersects_materialized(mpoint, 'LINESTRING(40 -73,40.7416693959765 -73.9897693321798,40.7416693959765 -73.9897693321798)'::geometry)
and carid <10000;


```
### K Nearest Neighbor Query
```
SELECT m_knn(t.mpid,'LINESTRING(40 -73,40.7416693959765 -73.9897693321798,40.7416693959765 -73.9897693321798)'::geometry,3)
FROM Trip t

SELECT m_knn(t.mpid,p.geo,3)
FROM Trip t,POI p

SELECT m_knn(t1.mpid,t2.mpid,3)
FROM Trip t1,Trip t2

```
### Distance Join Queries (MGeometry to Geometry)
```

-----basic join query no index  

select carid from Trip where m_mindistance_noindex(mpid,'POINT (-73.9917157777343 40.7424697420008)'::geometry, 100.0);

-----join query with index  

select carid from Trip where m_mindistance_index(mpid,'POINT (-73.9917157777343 40.7424697420008)'::geometry, 100.0);

----- index with materialized

select carid from Trip where m_mindistance_materialized(mpid,'POINT (-73.9917157777343 40.7424697420008)'::geometry, 100.0);


