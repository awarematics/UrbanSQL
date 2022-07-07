# UrbanSQL

UrbanSQL is an open source database extension based on PostgreSQL and PostGIS for managing spatio-temporal data
### Requirements
 1. CentOS 7
 2. PostgreSQL 11
 3. JDK 1.8
 4. Maven 3.3.9
 5. PL\JAVA 1.5.1
 6. PostGIS 2.5
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
  ###### Set connection
  sudo vi /var/lib/pgsql/11/data/postgresql.conf  
  listen_addresses='*'  

  sudo vi /var/lib/pgsql/11/data/pg_hba.conf  
  host  all  all 0.0.0.0/0 md5  
		
  sudo systemctl restart postgresql-11  
### 3. JDK 1.8
  sudo yum  install java-1.8.0-openjdk-devel.x86_64  
### 4. Maven 3.3.9 (Optional)
  wget https://mirror.navercorp.com/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz  
  tar -zxvf apache-maven-3.3.9-bin.tar.gz  
  mv apache-maven-3.3.9 maven  
  sudo vim  /etc/profile  
  
  ###### Add tail of profile
  MAVEN_HOME=/home/Your username/maven  
  export PATH=\\\${MAVEN\\\_HOME}/bin:\\\${PATH}   
	
  PATH="$PATH:/home/Your username/maven/bin:/usr/pgsql-11/bin"  
  export PATH  
  ###### Restart profile
  source /etc/profile  
  
  ###### test 
  mvn -v  
  
  ### 5. PL\JAVA 1.5.6
  
  sudo yum install pljava-11.x86_64  
  ###### Setting in PostgreSQL for PL/Java
  cd /var/lib/pgsql/11/data  
  sudo vi postgresql.conf  
  ###### Add the following lines for the pljava setting
  pljava.classpath='/usr/pgsql-11/share/pljava/pljava-1.5.6.jar'  
  pljava.libjvm_location='/usr/lib/jvm/java-1.8.0-openjdk/jre/lib/amd64/server/libjvm.so'  
  ###### Restart of PostgreSQL
  systemctl restart postgresql-11.service 
  
  ### 6. PostGIS 2.5
  
  sudo yum install -y libtool libxml2 libxml2-devel libxslt libxslt-devel json-c json-c-devel cmake gmp gmp-devel mpfr mpfr-devel boost-devel pcre-  devel   
  sudo yum install -y postgis25_11
  systemctl restart postgresql-11.service 
  
  ### 7. UrbanSQL
  cd /tmp   
  wget https://github.com/awarematics/mgeometry/blob/master/PostgreSQL/proj_berlinmod/target/proj-0.0.1-SNAPSHOT.jar    
  wget https://github.com/awarematics/mgeometry/blob/master/PostgreSQL/proj_berlinmod/jts-core-1.15.0-SNAPSHOT.jar  
  ###### open database  
  select sqlj.install_jar('file:/tmp/proj/target/proj-0.0.1-SNAPSHOT.jar', 'jar1', true);    
  select sqlj.install_jar('file:/tmp/jts-core-1.15.0-SNAPSHOT.jar', 'jar2', true);    
  select sqlj.set_classpath('public', 'jar1:jar2');    
  select sqlj.get_classpath('public');    
  
  
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

CREATE OR REPLACE FUNCTION public.addmgeometrycolumn(
	character varying,
	character varying,
	character varying,
	integer,
	character varying,
	integer,
	integer)
    RETURNS text
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
    f_schema_name     alias for $1;
    f_table_name     alias for $2;
    f_column_name     alias for $3;
    srid        alias for $4;
    new_type     alias for $5;
    dimension     alias for $6;	
    tpseg_size    alias for $7;
    real_schema name;
    sql text;
    table_oid text;
    temp_segtable_name text;
    f_mgeometry_segtable_name text;
    f_sequence_name    text;
    f_segtable_oid    oid;
BEGIN
    --verify SRID
    IF ( f_schema_name IS NOT NULL AND f_schema_name != '' ) THEN
        sql := 'SELECT nspname FROM pg_namespace ' ||
            'WHERE text(nspname) = ' || quote_literal(f_schema_name) ||
            'LIMIT 1';
        RAISE DEBUG '%', sql;
        EXECUTE sql INTO real_schema;

        IF ( real_schema IS NULL ) THEN
            RAISE EXCEPTION 'Schema % is not a valid schemaname', quote_literal(f_schema_name);
            RETURN 'fail';
        END IF;
    END IF;

    IF ( real_schema IS NULL ) THEN
        RAISE DEBUG 'Detecting schema';
        sql := 'SELECT n.nspname AS schemaname ' ||
            'FROM pg_catalog.pg_class c ' ||
              'JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace ' ||
            'WHERE c.relkind = ' || quote_literal('r') ||
            ' AND n.nspname NOT IN (' || quote_literal('pg_catalog') || ', ' || quote_literal('pg_toast') || ')' ||
            ' AND pg_catalog.pg_table_is_visible(c.oid)' ||
            ' AND c.relname = ' || quote_literal(f_table_name);
        RAISE DEBUG '%', sql;
        EXECUTE sql INTO real_schema;

        IF ( real_schema IS NULL ) THEN
            RAISE EXCEPTION 'Table % does not occur in the search_path', quote_literal(f_table_name);
            RETURN 'fail';
        END IF;
    END IF;

    sql := 'select '|| quote_literal(f_table_name) ||'::regclass::oid';
    RAISE DEBUG '%', sql;
    EXECUTE sql INTO table_oid;
-------------------------------------------mpoint	
 	IF (new_type = 'mpoint')
    THEN       
    	f_sequence_name = quote_ident(f_table_name) || '_' || quote_ident(f_column_name) || '_mpointid_seq';

    	sql := 'CREATE SEQUENCE ' || quote_ident(f_sequence_name) || ' START 1';
   	 	RAISE DEBUG '%', sql;
   	 	EXECUTE sql;

    	-- Add trajectory column to table
    	sql := 'ALTER TABLE ' || quote_ident(f_table_name) || 
        	' ADD ' || quote_ident(f_column_name) || ' mpoint';
   		RAISE DEBUG '%', sql;
    	RAISE INFO '%', sql;
   		EXECUTE sql;    

    	-- Delete stale record in geometry_columns (if any)
   		sql := 'DELETE FROM mgeometry_columns WHERE
			f_table_name = ' || quote_literal(f_table_name) ||
        	' AND f_mgeometry_column = ' || quote_literal(f_column_name);
    	RAISE DEBUG '%', sql;
    	EXECUTE sql;

    	sql := 'DELETE FROM mgeometry_columns WHERE
       	 f_table_catalog = ' || quote_literal('') ||
       	 ' AND f_table_schema = ' ||quote_literal(real_schema) ||
       	 ' AND f_table_name = ' || quote_literal(f_table_name) ||
       	 ' AND f_mgeometry_column = ' || quote_literal(f_column_name);
   		RAISE DEBUG '%', sql;
   		EXECUTE sql;
   	 	temp_segtable_name := 'mpoint_' || table_oid || '_' || f_column_name;
	
    	EXECUTE 'CREATE TABLE ' || temp_segtable_name || ' 
        (
            mpid        integer,
            segid        integer,
			mbr	box2d,
			timerange	int8range,
            trajectory    	geometry,
            wkttraj        text
        )';
    	sql := 'select '|| quote_literal(temp_segtable_name) ||'::regclass::oid';
   		RAISE DEBUG '%', sql;
    	EXECUTE sql INTO f_segtable_oid;
   		-- segment table name
    	f_mgeometry_segtable_name := 'mpoint_' || f_segtable_oid ;   
   	 	EXECUTE 'ALTER TABLE ' || quote_ident(temp_segtable_name) || ' RENAME TO ' || quote_ident(f_mgeometry_segtable_name);
	
    	-- Add record in geometry_columns 
    	sql := 'INSERT INTO mgeometry_columns (f_table_catalog, f_table_schema, f_table_name, ' ||
            'f_mgeometry_column, f_mgeometry_segtable_name, coord_dimension, srid, type, '|| 
            'f_segtableoid, f_sequence_name, tpseg_size)' ||
        	' VALUES (' ||
       	 	quote_literal('') || ',' ||
        	quote_literal(real_schema) || ',' ||
        	quote_literal(f_table_name) || ',' ||
        	quote_literal(f_column_name) || ',' ||
        	quote_literal(f_mgeometry_segtable_name) || ',' || 
        	dimension::text || ',' ||
        	srid::text || ',' ||
        	quote_literal(new_type) || ', ' ||
        	quote_literal(f_segtable_oid) || ', ' ||
        	quote_literal(f_sequence_name) || ', ' ||
        	tpseg_size || ')';
    	RAISE DEBUG '%', sql;
    	EXECUTE sql;

    	sql := 'UPDATE ' || quote_ident(f_table_name)|| ' SET ' || quote_ident(f_column_name) || '.moid '
     	|| '= NEXTVAL(' || quote_literal(f_sequence_name) ||'), ' || quote_ident(f_column_name) || '.segid = ' || f_segtable_oid;
   		-- sql := 'UPDATE ' || quote_ident(f_table_name)|| ' SET ' || quote_ident(f_column_name) || '.id = NEXTVAL(' || quote_literal(f_sequence_name) ||')';
   		 RAISE DEBUG '%', sql;
   		 EXECUTE sql;
    END IF;	
	
    RETURN
        real_schema || '.' ||
        f_table_name || '.' || f_column_name ||
        ' SRID:' || srid::text ||
        ' TYPE:' || new_type ||
        ' DIMS:' || dimension::text || ' ';
END;
$BODY$;

ALTER FUNCTION public.addmgeometrycolumn(character varying, character varying, character varying, integer, character varying, integer, integer)
    OWNER TO postgres;


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
### Distance Join Queries
```

-----basic join query no index  

select carid from Trip where m_mindistance_noindex(mpid,'POINT (-73.9917157777343 40.7424697420008)'::geometry, 100.0);

-----join query with index  

select carid from Trip where m_mindistance_index(mpid,'POINT (-73.9917157777343 40.7424697420008)'::geometry, 100.0);

----- index with materialized

select carid from Trip where m_mindistance_materialized(mpid,'POINT (-73.9917157777343 40.7424697420008)'::geometry, 100.0);


