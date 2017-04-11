package com.tgb.app;

public class AppState {
	
	public static Boolean isLogin = false;
	public static int MemberID = 0;
	public static String RealName = null;
	public static int CardID = 0;
	public static String RFCode = null;
	public static String Permissions = null;
	public static String RFCardCode = null;
	
	public static int photoIndex = -1;
	
	public final static String SDCARD_PHOTO_FOLDER="/castic/photo";
	public final static String SDCARD_THUMBNAIL_FOLDER="/castic/thumbnail";
	public final static String SDCARD_MIDDLE_FOLDER="/castic/middle";

	public static void init() {
		isLogin = false;
		MemberID = 0;
		RealName = null;
		CardID = 0;
		RFCode = null;
		Permissions = null;
		RFCardCode = null;
	}

}
