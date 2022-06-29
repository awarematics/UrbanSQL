package com.awarematics.postmedia.io;

import java.text.ParseException;

import com.awarematics.postmedia.types.mediamodel.MGeometry;

public class MWKTWriter
{
 public MWKTWriter()
 {
 }
 public String write(MGeometry geometry) throws ParseException
 {
   return geometry.toGeoString();
 }

}