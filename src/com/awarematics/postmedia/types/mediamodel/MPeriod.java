package com.awarematics.postmedia.types.mediamodel;


public class MPeriod extends MTime {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Period[]  period;
	
	public MPeriod(Period[] period) {
		this.period = period;
	}
	public MPeriod(MPeriod mp) {
		this.period = mp.period;
	}

	public MPeriod() {
		// TODO Auto-generated constructor stub
	}

	public String toGeoString() {
	String mperiodstring = "MPERIOD (";
	for (int i = 1; i < period.length; i++) {
		if (i == 1) {
			mperiodstring = mperiodstring  + period[i].toGeoString() ;
		} else {
			mperiodstring = mperiodstring + ", " + period[i].toGeoString() ;
		}
	}
	mperiodstring = mperiodstring + ")";
	return mperiodstring;
	}

	public Period[] getPeriod() {
		return period;
	}

	public void setPeriod(Period[] period) {
		this.period = period;
	}
}

