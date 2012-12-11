package com.tfnsnproject.service;

import com.tfnsnproject.dao.CheckinDao;
import com.tfnsnproject.to.Checkin;
import com.tfnsnproject.to.Photo;
import com.tfnsnproject.util.PhotoUtil;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: caner
 * Date: 11/7/12
 * Time: 12:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class CheckinServiceImpl implements CheckinService {

    private CheckinDao checkinDao;

    public void setCheckinDao(CheckinDao checkinDao) {
        this.checkinDao = checkinDao;
    }

    public void checkinWithMedia(Checkin checkin, byte[] originalBytes) {
        checkinDao.create(checkin);
        Photo photo = new Photo();
        photo.setCheckinId(checkin.getId());
        try {
            PhotoUtil.storePhoto(photo, originalBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        checkinDao.updatePhoto(checkin, photo);
    }

}
