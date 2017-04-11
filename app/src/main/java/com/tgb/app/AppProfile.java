package com.tgb.app;

import android.content.Context;
import android.content.SharedPreferences;  
import android.content.SharedPreferences.Editor;



public class AppProfile {
	
	public static String getBaseAddress(Context context){
		SharedPreferences sp = context.getSharedPreferences("NetConfig",Context.MODE_PRIVATE);  
		return sp.getString("BaseAddress", "http://10.185.1.151:9000");
	}
	
	
	public static void setBaseAddress(Context context,String address){
		SharedPreferences sp = context.getSharedPreferences("NetConfig", Context.MODE_PRIVATE);
		Editor editor = sp.edit();//获取编辑器
		editor.putString("BaseAddress", address);		
		editor.commit();//提交修改
	}
	/**
	 * 上传图片基地址
	 * @param context
	 * @return
	 */
	public static String getUploadPhotoBaseAddress(Context context){
		return getBaseAddress(context) + "/Regist/";
	}
	/**
	 * 获取注册列表基地址
	 * @param context
	 * @return
	 */
	public static String getRegistBaseAddress(Context context){
		return getBaseAddress(context) + "/Regist/";
	}
	
	/**
	 * 获取照片基地址
	 * @param context
	 * @return
	 */
	public static String getPhotoBaseAddress(Context context){
		return getBaseAddress(context) + "/MemberPhotos";
	}
	
	public static String getRegistPhotoBaseAddress(Context context){
		return getBaseAddress(context) + "/RegistPhotos";
	}
	
	/**
	 * 会场检录基地址
	 * @param context
	 * @return
	 */
	public static String getCheckInBaseAddress(Context context){
		return getBaseAddress(context) + "/CheckIn/";
	}
	/**
	 * 会场检录的照片
	 * @return
	 */
	public static String getRFCardPhotos(Context context){
		return getBaseAddress(context) + "/RFCardPhotos/";
	}
	
	public static String getCommonBaseAddress(Context context){
		return getBaseAddress(context) + "/Common/";
	}
	
	public static String getBoothCheckBaseAddress(Context context){
		return getBaseAddress(context) + "/BoothCheck/";
	}
	
	public static String getToolsBorrowBaseAddress(Context context){
		return getBaseAddress(context) + "/ToolsBorrow/";
	}
	
}
