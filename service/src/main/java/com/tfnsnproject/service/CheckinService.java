package com.tfnsnproject.service;

import com.tfnsnproject.to.Checkin;

public interface CheckinService {

    void checkinWithMedia(Checkin checkin, byte[] originalBytes);

}
