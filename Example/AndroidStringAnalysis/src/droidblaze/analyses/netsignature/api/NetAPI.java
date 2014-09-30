package droidblaze.analyses.netsignature.api;

import java.util.Hashtable;

import soot.Value;
import soot.jimple.InvokeExpr;

public class NetAPI {
	private static String[] SigInfoList = {"<org.apache.http.client.methods.HttpGet: void <init>(java.lang.String)>",
		"<org.apache.http.client.methods.HttpPost: void <init>(java.lang.String)>",
		"<org.apache.http.client.methods.HttpPut: void <init>(java.lang.String)>",
		"<org.apache.http.client.methods.HttpDelete: void <init>(java.lang.String)>",
		"<org.apache.http.client.methods.BasicHttpRequest: void <init>(java.lang.String)>",
		"<java.net.URI: void <init>(java.lang.String)>",
		"<java.net.URI: void create(java.lang.String)>",
		"<android.net.URI: android.net.URI parse(java.lang.String)>",
		"<android.net.URI: android.net.URI encode(java.lang.String)>",
		"<android.net.MailTo: android.net.MailTo parse(java.lang.String)>",
		"<java.net.URL: void <init>(java.lang.String)>",
		"<java.net.URL: void <init>(java.net.URL,java.lang.String)>",
		"<java.net.URL: void <init>(java.net.URL,java.lang.String,java.net.URLStreamHandler)>",
		"<java.net.URLConnection: java.io.OutputStream getOutputStream()>",
		"<java.net.Socket: java.io.OutputStream getOutputStream()>",
		"<java.net.HttpURLConnection: java.io.OutputStream getOutputStream()>"
	};
	public static Hashtable<String, NetAPI> NetAPItable = new Hashtable<String, NetAPI>();
	public String apiSig;
	private NetAPI(String apiSig){
		this.apiSig = apiSig;
	}
	public static void initiate(){
		for(String sigInfo:NetAPI.SigInfoList){
			NetAPI api = new NetAPI(sigInfo);
			NetAPItable.put(sigInfo, api);
		}
	}
	public static NetAPIInvoke createNetAPIInvoke(InvokeExpr arg0) {
		// TODO Auto-generated method stub
		String apiSig = arg0.getMethod().toString();
		if(apiSig.equals(NetAPI.SigInfoList[0])||apiSig.equals(NetAPI.SigInfoList[1])||apiSig.equals(NetAPI.SigInfoList[2])||apiSig.equals(NetAPI.SigInfoList[3])||apiSig.equals(NetAPI.SigInfoList[4])){
			return new HttpRequestInvoke(arg0.getArgs());
		}else if(apiSig.equals(NetAPI.SigInfoList[5])||apiSig.equals(NetAPI.SigInfoList[6])||apiSig.equals(NetAPI.SigInfoList[7])||apiSig.equals(NetAPI.SigInfoList[8])||apiSig.equals(NetAPI.SigInfoList[9])||apiSig.equals(NetAPI.SigInfoList[10])){
			return new NewURLInvoke((Value) arg0.getArgs().get(0));
		}else if(apiSig.equals(NetAPI.SigInfoList[11])){
			return new NewURLInvoke((Value) arg0.getArgs().get(1));
		}else if(apiSig.equals(NetAPI.SigInfoList[12])){
			return new NewURLInvoke((Value) arg0.getArgs().get(1));
		}else if(apiSig.equals(NetAPI.SigInfoList[13])||apiSig.equals(NetAPI.SigInfoList[14])||apiSig.equals(NetAPI.SigInfoList[15])){
			return new NewURLInvoke(arg0);
		}else{
			//unreachable
			return null;
		}
	}
}
