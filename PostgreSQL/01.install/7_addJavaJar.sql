select sqlj.remove_jar('myjar', true);
select sqlj.remove_jar('myjar2', true);
select sqlj.install_jar('file:/tmp/proj/target/proj-0.0.1-SNAPSHOT.jar', 'myjar', true);
select sqlj.install_jar('file:/tmp/jts-core-1.15.0-SNAPSHOT.jar', 'myjar2', true);	
select sqlj.set_classpath('public', 'myjar:myjar2');
select sqlj.get_classpath('public');

