package com.awarematics.postmedia.algorithms.distance;

import java.util.ArrayList;
import org.locationtech.jts.geom.Geometry;

import com.awarematics.postmedia.types.mediamodel.MGeometry;

public class AtomicTimeGenerator implements TimeGenerator {

	long atomicUnit;

	public AtomicTimeGenerator(long timeUnit) {
		atomicUnit = timeUnit;
	}

	public long[] genTimes(MGeometry mg1, MGeometry mg2) {
		long overlappedStartTime = Math.max( mg1.getTimes()[0],mg2.getTimes()[0] );
	    long overlappedEndTime = Math.min( mg1.getTimes()[mg1.numOf()-1], mg2.getTimes()[mg2.numOf()-1] );
		long[] timeArea = new long[] {overlappedStartTime, overlappedEndTime};
		long[] target = finalTime(timeArea);
		return target;
	}

	public long[] genTimes(MGeometry mg, Geometry geo) {
		long[] timeArea = new long[] { mg.getTimes()[0], mg.getTimes()[mg.numOf() - 1]};		
		long[] target = finalTime(timeArea);
		return target;
	}

	public long[] genTimes(Geometry geo, MGeometry mg) {
		long[] timeArea = new long[] { mg.getTimes()[0], mg.getTimes()[mg.numOf() - 1]};		
		long[] target = finalTime(timeArea);
		return target;
	}
	
	public long[] finalTime(long[] timeArea){
		ArrayList<Long> nowTime = new ArrayList<Long>();
		standardTime(timeArea, nowTime);
		long[] tempList = new long[nowTime.size()];
		for(int i=0;i<nowTime.size();i++)
			tempList[i] = nowTime.get(i);				
		//long[] tempList = nowTime.stream().mapToLong(i -> i).toArray();
		return tempList;
	}
	
	public  ArrayList<Long> standardTime(long[] timeArea, ArrayList<Long> nowTime) {
		long k = (timeArea[1] - timeArea[0]) / (atomicUnit);
		if( k <= 0 ) 
			return null;
		long start = timeArea[0];
		long temp =0;
		
		if(start % atomicUnit==0)
		{
			temp = start;k=k+1;
		}
		else
		{
			temp = start + (atomicUnit - start % atomicUnit);
		}
		for (int i = 0; i < (int) k; i++) 
		{
			nowTime.add(temp);
			//System.out.println(temp);
			temp = temp + atomicUnit;
		}
		return nowTime;
	}

}



