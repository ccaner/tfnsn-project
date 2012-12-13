package com.tfnsnproject.api.controller;

import java.io.IOException;
import java.util.Date;

import com.tfnsnproject.service.AuthenticationService;
import com.tfnsnproject.to.Checkin;
import com.tfnsnproject.service.CheckinService;
import com.tfnsnproject.to.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class CheckinController {

    @Autowired
    private AuthenticationService authenticationService;

	@Autowired
    private CheckinService checkinService;

    @RequestMapping(value = "/checkin-with-media", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public @ResponseBody String processUpload(@RequestHeader("Authorization") String authToken,
                                              @RequestParam String message, @RequestParam MultipartFile media,
                                              @RequestParam String lat, @RequestParam(value = "long") String longg)
            throws Exception {

        User user = authenticationService.authenticate(authToken.trim());
        if (user == null) {
            throw new SecurityException("Invalid token");
        }
        Checkin checkin = new Checkin();
        checkin.setAccountId(user.getUserId());
        checkin.setCheckinTime(new Date());
        checkin.setComment(message);
        checkin.setLatitude(lat);
        checkin.setLongitude(longg);
        checkinService.checkinWithMedia(checkin, media.getBytes());
        return "OK";
	}

}