package com.tfnsnproject.service;

import com.tfnsnproject.dao.CheckinDao;
import com.tfnsnproject.to.Checkin;
import com.tfnsnproject.to.Photo;
import com.tfnsnproject.util.FsPhotoUtil;
import com.tfnsnproject.util.PhotoUtil;

import java.io.IOException;

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
            FsPhotoUtil.storePhoto(checkin.getId(), photo, originalBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        checkinDao.updatePhoto(checkin, photo);
    }

}
