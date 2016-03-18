package com.itsinic.sso.service;

import com.itsinic.sso.model.User;
/**
 * Created by HAIOU on 2016/3/16.
 */
public interface UserService {

	public User  getUserByName(String username);
}
