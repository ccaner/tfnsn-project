package com.tfnsnproject.dao;

import com.tfnsnproject.to.User;

public interface UserDao {

    User loadUser(String username);
}
