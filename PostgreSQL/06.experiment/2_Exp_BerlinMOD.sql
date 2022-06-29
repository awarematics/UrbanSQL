

```

### BerlinMOD Queries

###  1. What are the models of the vehicles with license plate numbers from Licenses?
```

explain analyze SELECT L.Licence, C.Model AS Model
FROM Cars C, Licences L
WHERE C.Licence = L.Licence;
```
### 2. How many vehicles exist that are "passenger" cars?
```
explain analyze SELECT COUNT (Licence)
FROM Cars C
WHERE Type = 'passenger';
```
### 3. Where have the vehicles with licenses from Licences been at each of the instants from QueryInstants?
```
	
explain analyze 
WITH CarList AS (
SELECT C.Licence As Licence, C.mt AS mpoint
FROM Cars C, Licences L
WHERE C.licence = L.licence
)
SELECT DISTINCT I.Instant AS Instant, C.Licence, m_snapshots(C.mpoint, I.instant) As Positions 
FROM QueryInstants I, CarList C;


```
### 4. Which vehicles have passed the points from QueryPoints?
```
	
explain analyze 
WITH CarList AS (
SELECT  P.PointId, P.geom, m_sintersects(C.mt, P.geom) AS Intersects
FROM Cars C, QueryPoints P
)
SELECT C.PointId, C.geom
FROM CarList C
where C.Intersects;

```
### 5. What is the minimum distance between places, where a vehicle with a license from Licences and a vehicle with a license from Licences have been?
```

explain analyze 
With CarList AS(
SELECT Distinct C1.Licence AS Licence1, C2.Licence AS Licence2, C1.mt AS mt1, C2.mt AS mt2
FROM Cars C1, Licences L1, Cars C2, Licences L2
WHERE C1.CarId < C2.CarId 
AND L1.LicenceId = C1.CarId AND C2.CarId = L2.LicenceId
)
SELECT m_mindistance(C.mt1, C.mt2) AS MinDist
FROM CarList C;
``` 
###  6. What are the pairs of trips from Licences that have ever been as close as 10m or less to each other?
```

explain analyze 
WITH CarList AS (
SELECT  C1.Licence AS Licence1, C2.Licence AS Licence2,  m_dwithin(C1.mt, C2.mt, 10) AS Dwithin
FROM Cars C1, Cars C2
WHERE C1.CarId < C2.CarId 
)
SELECT C.Dwithin, C.Licence1 , C.Licence2 
FROM CarList C
WHERE C.Dwithin


```
###  7. What are the licence plate numbers of the "passenger" cars that have reached the points from QueryPoints first of all "passenger" cars during the complete observation period?
```

explain analyze 
WITH CarList AS (
SELECT C.Licence AS Licence, P.Geom AS Geom, C.mt AS mpoint, m_sintersects(C.mt, P.Geom) As Intersects
FROM Cars C, QueryPoints P
WHERE C.Type = 'passenger'
)
SELECT DISTINCT CL1.Licence, CL1.Geom, m_eventtime(CL1.mpoint, CL1.Geom) AS Instant
FROM CarList CL1 
WHERE CL1.Intersects

```
### 8. What are the overall travelled distances of the vehicles with licence plate numbers from Licences during the periods from QueryPeriods?
```

explain analyze 
WITH CarList AS (
SELECT C.Licence, (m_slice(C.mt, P.Period)) AS Mpoint, P.PeriodId 
FROM  Cars C, Licences L, QueryPeriods P
WHERE C.Licence = L.licence
)
SELECT C.Licence, C.PeriodId, m_timeAtCummulative(C.Mpoint) AS Distance
FROM CarList C 
GROUP BY C.Licence, C.PeriodId, C.Mpoint

```
### 9. What is the longest distance that was travelled by a vehicle during each of the periods from QueryPeriods?
```

explain analyze 
WITH CarList AS (
SELECT m_timeAtCummulative(m_slice(C.mt, P.Period)) AS Distance, P.PeriodId 
FROM  Cars C, QueryPeriods P
)
SELECT MAX(CT.Distance)
FROM CarList CT

```
### 10. When and where did the vehicles with licence plate numbers from Licences meet other vehicles (distance < 3m) and what are the latters' licences?
```
SELECT C1.Licence AS QueryLicence , C2.Licence AS OtherLicence,
m_eventposition(C1.mt, C2.mt, 3) AS meetPos, m_eventtime(C1.mt, C2.mt, 3) AS meetTime
FROM Cars C1, Cars C2, Licences L1
WHERE C1.Licence = L1.Licence AND C2.Licence <> C1.Licence
AND st_dwithin(m_spatial(C1.mt), m_spatial(C2.mt), 3.0)
AND m_tintersects(C1.mt, m_time(C2.mp));
```
### 11. Which vehicles passed a point from QueryPoints at one of the instants from QueryInstants?
 ```
SELECT C.licence AS Licence, P.geom AS QueryPoint, I.instant As Instant
FROM Cars C, QueryPoints P, QueryInstants I
WHERE m_passes(C.mt,P.geom)
AND m_tintersects(C.mt, I.instant)
```
### 12. Which vehicles met at a point from QueryPoints at an instant from QueryInstants?
```
SELECT C1.licence AS Licence1, P.geom AS QueryPoint, I.instant As Instant
FROM Cars C1, QueryPoints P, QueryInstants I
WHERE m_meets(C1.mt, P.geom)
AND m_tintersects(C1.mt, I.instant)
```
### 13. Which vehicles travelled within one of the regions from QueryRegions during the periods from QueryPeriods?
```
SELECT C1.CarId AS CarId, P.period AS Period, R.geom AS QueryRegions
FROM Cars C1, QueryPeriods P, QueryRegions R
WHERE m_tintersects(C1.mt, P.period)
AND m_mstayIn(C1.mt, R.geom)
```
### 14. Which vehicles travelled within one of the regions from QueryRegions at one of the instants from QueryInstants?
```
SELECT C1.CarId AS CarId, I.Instant AS Instant, R.geom AS QueryRegions
FROM Cars C1, QueryInstants I, QueryRegions R
WHERE ST_WITHIN(m_spatial(C1.mt), R.geom) 
AND m_tintersects(C1.mt, I.instant)
```
### 15. Which vehicles passed a point from QueryPoints during a period from QueryPeriods?
```
SELECT C1.CarId AS CarId, Pr.period AS Period, P.geom AS QueryPoint
FROM Cars C1, QueryPeriods Pr, QueryPoints P
WHERE m_passes(C1.mt, P.geom)
AND m_tintersects(C1.mt, Pr.period)
```
### 16. List the pairs of licences for vehicles from Licences where the corresponding vehicles are both present within a region from Regions1 during a period from QueryPeriod, but do not meet each other there and then.
```
SELECT C1.Licence, C2.Licence, P.period, R.geom 
FROM Cars C1, Cars C2, QueryPeriods P, QueryRegions R
WHERE m_insides(C1.mt, R.geom) 
AND m_insides(C2.mt, R.geom)
And m_disjoints(C1.mt,C2.mt,P.period)
```
### 17. Which points from Points have been visited by a maximum number of different vehicles?
```

WITH PointCount AS (
SELECT P.PointId, COUNT(C.CarId) AS Hits
FROM Cars C, QueryPoints P
WHERE m_sintersects(C.mt, P.geom )
GROUP BY P.PointId 
)
SELECT PointId, max(P.Hits) 
FROM PointCount P

```

	
