package com.tfnsnproject.util;

import com.tfnsnproject.to.Photo;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FsPhotoUtil {

    private static final int THUMBNAIL_LONG_EDGE = 150;

    private static String photoDir;
    static {
        photoDir = AppConfiguration.getInstance().getProperty("photo.dir");
    }


    public static Photo storePhoto(Long id, Photo photo, byte[] photoData) throws IOException {

        // Store various photo sizes
        String thumbnailPath = storeThumbnail(id, photo, photoData);
        photo.setThumbnailPath(thumbnailPath);

        String originalPath = storeOriginal(id, photo,photoData);
        photo.setOriginalPath(originalPath);

        return photo;
    }

    private static String storeOriginal(Long id, Photo photo, byte[] photoData) throws IOException {
        String path = "mobile/upload/" + "app_" + id + ".jpg";
        IOUtils.write(photoData, new FileOutputStream(photoDir + path));
        return path;
    }

    private static String storeThumbnail(Long id, Photo photo, byte[] photoData) throws IOException {
        byte[] thumbnail = PhotoUtil.scalePhoto(THUMBNAIL_LONG_EDGE, photoData);
        String thumbPath = "mobile/upload/small/" + "app_" + id + ".jpg";
        IOUtils.write(thumbnail, new FileOutputStream(photoDir + thumbPath));
        return thumbPath;
    }

}
