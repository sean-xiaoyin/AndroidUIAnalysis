package main.test;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



import com.google.gson.Gson;


public class Dumper {
	
	public static void dump(Object obj, String methodName){
		try{
			File file = new File("output"+obj.getClass().getName()+ methodName+".json");
			FileWriter output = new FileWriter(file);
			if (!file.exists()) {
				file.createNewFile();
			}
			Gson gson = new Gson();
			String objOutput = gson.toJson(obj);
			output.write(objOutput);	
			System.out.println("Done");
			output.close();
			
			}catch(IOException e){
			e.printStackTrace();
			}		
	}
		
	String getObjectType(String className, String packageName){
		className =className.replace("Test", "");
		String[] split = className.split("_");
		return packageName+"."+split[0];
	}
	public static void main(String[] args){
		Dumper dump = new Dumper();
		 // System.out.println(dump.getObjectType("TestMonths_sdkhksdj", "org.time"));
		
		}
		//DateTime date = new DateTime();
		//Property date = test.dayOfMonth();
	//	System.out.println("The Hour of the day is :"+date.getHourOfDay());
		//System.out.println("The minitue of the day is :"+date.getSecondOfMinute());
		//System.out.println("The day of the year is :"+date.getDayOfYear());
		
		
}
