package com.jike.supercollection;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class MyApplication extends Application {
	private Boolean hasCheckedUpdate=false;

	public Boolean getHasCheckedUpdate() {
		return hasCheckedUpdate;
	}

	public void setHasCheckedUpdate(Boolean hasCheckedUpdate) {
		this.hasCheckedUpdate = hasCheckedUpdate;
	}
	
	
	// 记录Activity列表，方便退出
	private List<Activity> activityList = new LinkedList<Activity>();
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}
	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		activityList.clear();
	}
}
