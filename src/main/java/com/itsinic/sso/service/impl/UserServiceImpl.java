package com.itsinic.sso.service.impl;

import com.itsinic.sso.dao.UserDAO;
import com.itsinic.sso.model.User;
import com.itsinic.sso.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created by HAIOU on 2016/3/16.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    @Qualifier("userDAO")
    private UserDAO userDAO;


    @Override
    public User getUserByName(String username) {
        return userDAO.getUserByName(username);
    }
}
