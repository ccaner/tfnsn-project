package com.tfnsnproject.api.controller;

import com.tfnsnproject.service.AuthenticationService;
import com.tfnsnproject.to.Checkin;
import com.tfnsnproject.to.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@Controller
public class LoginController extends BaseController {

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(value = "/auth/login", method= RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public @ResponseBody String login(@RequestParam String username, @RequestParam String password) {
        String token = authenticationService.login(username, password);
        if (token == null) {
            throw new SecurityException("Invalid username / password");
        }
        return token;
	}

}
