
	
-----car table
 create index IF NOT EXISTS bl_index_carid on public."car" using btree (taxi_id) tablespace pg_default ;  
 create index IF NOT EXISTS bl_index_number on public."car" using btree (taxi_number text_pattern_ops) tablespace pg_default ;  
 create index IF NOT EXISTS bl_index_model on public."car" using btree (taxi_model text_pattern_ops) tablespace pg_default ; 
 create index IF NOT EXISTS bl_index_driver on public."car" using btree (taxi_driver text_pattern_ops) tablespace pg_default ;
 
----seg-table

 create index IF NOT EXISTS bl_index_mpid on public."mpoint_186468" using btree (mpid) tablespace pg_default ;  
 create index IF NOT EXISTS bl_index_segid on public."mpoint_186468" using btree (segid) tablespace pg_default ;  
 create index IF NOT EXISTS bl_index_mbr on public."mpoint_186468" using gist (mbr) tablespace pg_default ;  
 create index IF NOT EXISTS bl_index_timerange on public."mpoint_186468" using gist (timerange) tablespace pg_default ;  
 
create index IF NOT EXISTS bl_index_datetimes on public."mpoint_186468" using btree (datetimes) tablespace pg_default ;
create index IF NOT EXISTS bl_index_geo on public."mpoint_186468" using gist(ST_MakeLine(geo::geometry[])) tablespace pg_default ;

 
 -----mgeo
 create index IF NOT EXISTS bl_index_segoid on public."mgeometry_columns" using btree (f_segtableoid text_pattern_ops) tablespace pg_default ; 
  create index IF NOT EXISTS bl_index_segtname on public."mgeometry_columns" using btree (f_mgeometry_segtable_name text_pattern_ops) tablespace pg_default ; 
  