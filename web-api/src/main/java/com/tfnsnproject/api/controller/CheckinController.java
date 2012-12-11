package com.tfnsnproject.api.controller;

import java.io.IOException;
import java.util.Date;

import com.tfnsnproject.to.Checkin;
import com.tfnsnproject.service.CheckinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class CheckinController {

	@Autowired
    private CheckinService checkinService;

    @RequestMapping(value = "/checkin-with-media", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public @ResponseBody String processUpload(@RequestParam String message, @RequestParam MultipartFile media,
                                              @RequestParam String lat, @RequestParam(value = "long") String longg)
            throws IOException {
        Checkin checkin = new Checkin();
        checkin.setAccountId(52l);
        checkin.setCheckinTime(new Date());
        checkin.setComment(message);
        checkin.setLatitude(lat);
        checkin.setLongitude(longg);
        checkinService.checkinWithMedia(checkin, media.getBytes());
        return "OK";
	}

}