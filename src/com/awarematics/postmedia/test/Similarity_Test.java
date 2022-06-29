package com.awarematics.postmedia.test;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import com.awarematics.postmedia.algorithms.similarity.MLCSS;
import com.awarematics.postmedia.algorithms.similarity.MLCVS;
import com.awarematics.postmedia.algorithms.similarity.MLCVSwithMBT;
import com.awarematics.postmedia.algorithms.similarity.MLCVSwithMBR;
import com.awarematics.postmedia.io.MWKTReader;
import com.awarematics.postmedia.mgeom.MGeometryFactory;
import com.awarematics.postmedia.types.mediamodel.MVideo;

public class Similarity_Test {

	public static final int ARRAY_SIZE = 1000;
	public static final double EPSILON_R = 0.1;
	private static MLCSS lcss;
	private static MLCVS lcvs;
	private static MLCVSwithMBT lcvsmbt;
	private static MLCVSwithMBR lcvsmbr;

	public static void main(String[] args) throws org.locationtech.jts.io.ParseException, ParseException, IOException {
		/*
		 * test for bbd data straight direction
		 */

		lcss = new MLCSS();
		lcvs = new MLCVS();
		lcvsmbt = new MLCVSwithMBT();
		lcvsmbr = new MLCVSwithMBR();

		// bootup
		MVideo[] videos = new MVideo[ARRAY_SIZE];
		MVideo[] videos1 = new MVideo[ARRAY_SIZE];
		for (int i = 0; i < ARRAY_SIZE; i++) {
			File file = new File("d://exp/" + i + ".json");
			File files = new  File("d://exp_random/" + i + ".json");
			videos[i] = getVideo(file);
			videos1[i] = getVideo(files);	
		}
		double accmbr =0.0;
		double accmbt =0.0;
		double acclcss = 0.0;
		
		double res1 =0;
		double res2 = 0;
		double res3 =0;
		double res4 =0;
		for (int i = 0; i < 10; i++) {
			for (int j = 1+i; j < 30; j++) {
				 res1 += doExperiementLCVS(videos[i],videos1[j] ,1);
				 res2 += doExperiementLCSS(videos[i],videos1[j],EPSILON_R, 1);
				 res3 += doExperiementLCVSMBT(videos[i],videos1[j], 1);
				 res4 += doExperiementLCVSMBR(videos[i], videos1[j],1);
			
			}
			System.out.println(res1+"\t"+res2+"\t"+res3+"\t"+res4);		
		}
		accmbt = res1/res3;
		//accmbr = res1/res4;
		//acclcss = res1/res2;
		System.out.println("acclcss\t"+acclcss);
		System.out.println(EPSILON_R+"\t"+accmbr);
		System.out.println(EPSILON_R+"\t"+accmbt);	
	}


public static double doExperiementLCVS( MVideo videos, MVideo videos1, int delta )
{
	
	Double lcvsdata = 0.0;
	//long start1=System.currentTimeMillis(); 			 
	lcvsdata =lcvsdata+ lcvs.similarity(videos, videos1, delta );	
	//System.out.println(lcvsdata+"\tsimialrity");
	//long end1=System.currentTimeMillis(); 
	//long times= end1- start1;
	//System.out.println("LCVS\t"+times);
	return lcvsdata;
}

public static double doExperiementLCSS( MVideo videos, MVideo videos1,double epsilon, int delta )
{
	
	Double lcssdata = 0.0;
	//long start1=System.currentTimeMillis(); 			 
	lcssdata += lcss.similarity(videos, videos1, epsilon, delta );	
	//long end1=System.currentTimeMillis(); 
	//long times= end1- start1;
	//System.out.println("LCSS\t"+times);
	return lcssdata;
}

public static double doExperiementLCVSMBT( MVideo videos,  MVideo videos1,int delta )
{
	
	Double lcvsmbtdata = 0.0;
	//long start1=System.currentTimeMillis(); 			 
	lcvsmbtdata += lcvsmbt.similarity(videos, videos1, delta );	
	//System.out.println(lcvsmbtdata+"\tsimialrity");
	
	//long end1=System.currentTimeMillis(); 
	//long times= end1- start1;

	//System.out.println("LCVSMBT\t"+times);
	return lcvsmbtdata;
}

public static double doExperiementLCVSMBR( MVideo videos, MVideo videos1, int delta)
{
	
	Double lcvsmbrdata = 0.0;
	//long start1=System.currentTimeMillis(); 			 
	lcvsmbrdata += lcvsmbr.similarity(videos, videos1, delta );
	//long end1=System.currentTimeMillis(); 
	//long times= end1- start1;

	//System.out.println("LCVSMBR\t"+times);
	return lcvsmbrdata;
}
	public static MVideo getVideo(File file)
			throws ParseException, org.locationtech.jts.io.ParseException, IOException {

		String content = FileUtils.readFileToString(file, "UTF-8");
		JSONObject jsonObject = new JSONObject(content);
		JSONArray getJsonArray = jsonObject.getJSONArray("mvideo_data");
		int num = getJsonArray.length();

		String mvideoString = "MVIDEO (";

		for (int j = 0; j < num; j++) {
			String[] array = getJsonArray.get(j).toString().split(":");
			//String result_distance = array[1].split(",\"")[0];
			String result_x = array[2].split(",\"")[0];
			String result_y = array[3].split(",\"")[0];
			String result_vangle = array[4].split(",\"")[0];
			String result_direction = array[5].split(",\"")[0];
			if (j != num - 1)
				mvideoString = mvideoString + "(" + "uri" + " " + "0.0" + " "+ "0.0"+ " " + result_vangle.replaceAll("}", "") + " "+ "0.0"+ " "+ EPSILON_R + " " 
						+ result_direction.replaceAll("}", "") + " "+ "0.0" + " "+ "0.0" + " " + "null"+ " "+ "null"+ " "+result_x
						+ " " + result_y + ") " + "100000" + ", ";
			else
				mvideoString = mvideoString + "(" + "uri" + " " + "0.0" + " "+ "0.0"+ " " + result_vangle.replaceAll("}", "") + " "+ "0.0"+ " "+ EPSILON_R + " " 
						+ result_direction.replaceAll("}", "") + " "+ "0.0" + " "+ "0.0" + " " + "null"+ " "+ "null"+" "+ result_x
						+ " " + result_y + ") " + "100000" + ")";
		}		
		//System.out.println(mvideoString);
		MGeometryFactory geometryFactory = new MGeometryFactory();
		MWKTReader reader = new MWKTReader(geometryFactory);
		MVideo mv = (MVideo) reader.read(mvideoString);
		return mv;
	}

}