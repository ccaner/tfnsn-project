package com.tfnsnproject.service;

import com.tfnsnproject.to.User;

public interface AuthenticationService {

    boolean checkPassword(String username, String password);

    String login(String username, String password);

    User authenticate(String token);

}
