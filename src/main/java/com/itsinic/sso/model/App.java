package com.itsinic.sso.model;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by HAIOU on 2016/3/16.
 */
public class App implements Serializable{

 	private String appName;
	private String appEnName;
	private String appUrl;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppEnName() {
		return appEnName;
	}

	public void setAppEnName(String appEnName) {
		this.appEnName = appEnName;
	}

	public String getAppUrl() {
		return appUrl;
	}

	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}
}