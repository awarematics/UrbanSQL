package com.awarematics.postmedia.types.mediamodel;


public class MInstant extends MTime {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	long[] instant;
	
	
	public MInstant(MInstant minstant) {
//		/System.out.println(minstant.getTimes());
		this.instant = minstant.instant;
	}
	
	
	public MInstant(long[] instant) {
		this.instant = instant;
	}
	
	
	public long[] getInstant() {
		return instant;
	}
	public void setInstant(long[] instant) {
		this.instant = instant;
	}
	@Override
	public String toGeoString() {
		
		String minstant = "MINSTANT (";
		for (int i = 0; i < numOf(); i++) {
			if (i == 0) {
				minstant = minstant + instant[i];
			} else {
				minstant = minstant + ", " + instant[i];
			}
		}
		minstant = minstant + ")";
		return minstant;
		
	}
	@Override
	public int numOf() {
		return instant.length;
	}

}

