package com.awarematics.postmedia.algorithms.similarity;

import com.awarematics.postmedia.types.mediamodel.MGeometry;

public interface MSimilarityMeasure
{	
	double measure( MGeometry g1, MGeometry g2);
}
