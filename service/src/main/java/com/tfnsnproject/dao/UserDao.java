package com.tfnsnproject.dao;

import com.tfnsnproject.to.User;

/**
 * Created by IntelliJ IDEA.
 * User: caner
 * Date: 12/13/12
 * Time: 7:44 PM
 * To change this template use File | Settings | File Templates.
 */
public interface UserDao {

    User loadUser(String username);
}
