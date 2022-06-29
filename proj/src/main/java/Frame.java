package com.awarematics.postmedia.types.mediamodel;

import org.locationtech.jts.geom.Coordinate;

public class Frame {
	/*
	 *  fid integer,
   		relativeTime bigint
   		framefov fov,   
   		geo      mcoordinate[]
	 */
	long relativeTime;
	FoV	 fov;
	Coordinate coos;
	
	public long getRelativeTime() {
		return relativeTime;
	}
	public void setRelativeTime(long relativeTime) {
		this.relativeTime = relativeTime;
	}
	public FoV getFov() {
		return fov;
	}
	public void setFov(FoV fov) {
		this.fov = fov;
	}
	public Coordinate getCoos() {
		return coos;
	}
	public void setCoos(Coordinate coos) {
		this.coos = coos;
	}
	
	
	
}
