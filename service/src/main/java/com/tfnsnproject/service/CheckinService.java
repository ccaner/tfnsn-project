package com.tfnsnproject.service;

import com.tfnsnproject.to.Checkin;

/**
 * Created by IntelliJ IDEA.
 * User: caner
 * Date: 11/7/12
 * Time: 12:02 AM
 * To change this template use File | Settings | File Templates.
 */
public interface CheckinService {

    void checkinWithMedia(Checkin checkin, byte[] originalBytes);

}
