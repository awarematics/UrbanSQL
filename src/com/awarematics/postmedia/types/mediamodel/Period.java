package com.awarematics.postmedia.types.mediamodel;

public class Period {
	long from;
	long to;
	
	
	public long getFrom() {
		return from;
	}
	public void setFrom(long from) {
		this.from = from;
	}
	public long getTo() {
		return to;
	}
	public void setTo(long to) {
		this.to = to;
	}
	public String toGeoString() {
		String mperiodstring = "("  + from + " " + to+ ")";
		return mperiodstring;
		}
}
