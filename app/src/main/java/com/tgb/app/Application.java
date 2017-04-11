package com.tgb.app;

public class Application extends android.app.Application {

	@Override
	public void onCreate() {
		AppResources.Application = this;
		super.onCreate();
	}
	
	
}
