

 create table bdd100k(
	carid integer,
	carnumber varchar,
	model varchar,
	driver varchar
);
 select * from bdd100k;
 insert into bdd100k values(1, '57NU2001', 'Optima', 'hongkd7');
insert into bdd100k values(2, '57NU2002', 'SonataYF', 'hongkd7');
 
 ---select pg_column_size(geo)/1024+1 from mpoint_186468; --- Byte to KB


---select pg_column_size(geo), mpid from mpoint_186468 order by  pg_column_size(geo) desc; --- Byte