package com.tfnsnproject.dao;

import com.tfnsnproject.to.Checkin;
import com.tfnsnproject.to.Photo;

public interface CheckinDao {

    void create(Checkin checkin);

    void updatePhoto(Checkin checkin, Photo photo);

}
