package com.itsinic.sso.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HAIOU on 2016/3/16.
 */
public class Ticket implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String username;
	
	private Timestamp createTime;
	
	private Timestamp recoverTime;

	private List<App> apps;

	public List<App> getApps() {
		if(apps == null){
			apps = new ArrayList<>();
		}
		return apps;
	}

	public void setApps(App app) {
		apps.add(app);
	}

	public Ticket() {
		super();
	}

	public Ticket(String username, Timestamp createTime, Timestamp recoverTime) {
		super();
		this.username = username;
		this.createTime = createTime;
		this.recoverTime = recoverTime;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getRecoverTime() {
		return recoverTime;
	}

	public void setRecoverTime(Timestamp recoverTime) {
		this.recoverTime = recoverTime;
	}
	
}
