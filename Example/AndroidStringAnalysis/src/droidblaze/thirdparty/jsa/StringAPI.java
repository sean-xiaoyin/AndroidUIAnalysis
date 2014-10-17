package droidblaze.thirdparty.jsa;



public class StringAPI {
	private static String[] SigInfoList = {};
	public static int getAPIIndex(String signature){
		for(int i = 0; i<StringAPI.SigInfoList.length; i++){
			if(StringAPI.SigInfoList[i].equals(signature)){
				return i;
			}
		}
		return -1;
	}
}
