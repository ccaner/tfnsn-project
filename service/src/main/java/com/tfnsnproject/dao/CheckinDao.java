package com.tfnsnproject.dao;

import com.tfnsnproject.to.Checkin;
import com.tfnsnproject.to.Photo;

/**
 * Created by IntelliJ IDEA.
 * User: caner
 * Date: 11/7/12
 * Time: 12:02 AM
 * To change this template use File | Settings | File Templates.
 */
public interface CheckinDao {

    void create(Checkin checkin);

    void updatePhoto(Checkin checkin, Photo photo);

}
