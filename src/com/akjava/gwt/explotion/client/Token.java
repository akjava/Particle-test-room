package com.akjava.gwt.explotion.client;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;



public class Token {
	Map<String,List<String>> parameters;
	public Token(){
		parameters=parseToken(History.getToken());
	}
	public static Map<String,List<String>> parseToken(String token){
		Map<String,List<String>> params=new HashMap<String,List<String>>();
		String values[]=token.split(";");
		for(String v:values){
			String name_value[]=v.split("=");
			if(name_value.length==2 && !name_value[0].isEmpty()){
				List<String> vs=params.get(name_value[0]);
				if(vs==null){
					vs=new ArrayList<String>();
					params.put(name_value[0], vs);
				}
				vs.add(name_value[1]);
			}
		}
		return params;
	}
	
	public int getInt(String key,int defaultValue){
		int value=defaultValue;
		String v=getValue(key);
		if(v!=null){
			try{
			value=Integer.parseInt(v);
			}catch(Exception e){}
		}
		return value;
	}
	public double getDouble(String key,double defaultValue){
		double value=defaultValue;
		String v=getValue(key);
		if(v!=null){
			try{
			value=Double.parseDouble(v);
			}catch(Exception e){}
		}
		return value;
	}
	//be carefull empty means false
	public boolean getBoolean(String key,boolean defaultValue){
		boolean value=defaultValue;
		String v=getValue(key);
		if(v!=null){
			try{
			value=Boolean.parseBoolean(v);
			}catch(Exception e){}
		}
		return value;
	}
	public int getHexInt(String key,int defaultValue){
		int value=defaultValue;
		String v=getValue(key);
		if(v!=null){
			if(v.startsWith("0x")){
				v=v.substring(2);
			}
			
			try{
			value=Integer.parseInt(v,16);
			}catch(Exception e){}
		}
		return value;
	}
	public String getString(String key,String defaultValue){
		String value=defaultValue;
		String v=getValue(key);
		if(v!=null){
			value=v;
		}
		return value;
	}
	
	private String getValue(String key){
		List<String> string=parameters.get(key);
		if(string==null){
			return null;
		}else{
			if(string.size()==0){
				return "";
			}else{
				return string.get(0);
			}
		}
	}
	
	public static String createToken(Map<String,String> params){
		StringBuilder builder=new StringBuilder();
		
		for(String key:params.keySet()){
			String value=params.get(key);
			builder.append(key+"="+URL.encodeQueryString(value)+";");
		}
		if(builder.length()>0){
			builder.delete(builder.length()-1,builder.length() );//chop
		}
		return builder.toString();
	}
	
	public static String createMultiToken(Map<String,List<String>> params){
		StringBuilder builder=new StringBuilder();
		
		for(String key:params.keySet()){
			List<String> values=params.get(key);
			
			for(String value:values){
				builder.append(key+"="+URL.encodeQueryString(value)+";");
			}
		}
		if(builder.length()>0){
			builder.delete(builder.length()-1,builder.length() );//chop
		}
		return builder.toString();
	}

}
