package com.itsinic.sso.dao;

import com.itsinic.sso.model.User;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * Created by HAIOU on 2016/3/16.
 */
@Transactional(propagation=Propagation.REQUIRES_NEW,readOnly=false,isolation=Isolation.DEFAULT)
public interface UserDAO {

	public User getUserByName(String userName);
}
