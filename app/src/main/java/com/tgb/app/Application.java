package com.tgb.app;

import cn.smssdk.SMSSDK;

public class Application extends android.app.Application {

	@Override
	public void onCreate() {
		AppResources.Application = this;
		super.onCreate();
		SMSSDK.initSDK(this, "1d401b5115d92", "b3a89f4846715acc90bf517f093fdd1f");
	}
	
	
}
