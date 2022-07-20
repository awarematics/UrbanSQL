SELECT M_KNN(t.mpoint,'POINT (1 5)',3)
FROM Trip t;

SELECT M_KNN(t.mpoint,p.point,3)
FROM Trip t,Points p;

SELECT M_KNN(t1.mpoint,t2.mpoint,3)
FROM Trip t,Trip t;
