package com.tfnsnproject.util;

import com.tfnsnproject.to.Photo;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhotoUtil {

    private static final String FULLSIZE_SUFFIX = ".jpg";
    private static final String THUMB_SUFFIX = "_thumb.jpg";

    private static final int THUMBNAIL_LONG_EDGE = 150;

    private static Logger logger = Logger.getLogger(PhotoUtil.class.getName());

    private static final String PHOTO_BUCKET="tfnsnprj-photo";

    private static String uniqueBucketName;

    // Initialize the unique bucket name
    static {
        Configuration config = Configuration.getInstance();
        uniqueBucketName = (config.getProperty("accessKey") + "-" + PHOTO_BUCKET);
        // Amazon S3 recommends bucket name to be lower case
        uniqueBucketName = uniqueBucketName.toLowerCase();
    }

    public static Photo storePhoto(Photo photo, byte[] photoData) throws IOException {

        // Store various photo sizes
        String thumbnailPath = storeThumbnail(photo, photoData);
        photo.setThumbnailPath(thumbnailPath);

        String originalPath = storeOriginal(photo,photoData);
        photo.setOriginalPath(originalPath);

        return photo;
    }

    private static String storeOriginal(Photo photo, byte[] photoData) throws IOException {
        S3StorageObject obj = getStorageObject(photoData, photo.getCheckinId() + FULLSIZE_SUFFIX);
        S3StorageManager mgr = new S3StorageManager();
        mgr.storePublicRead(obj, true);
        return obj.getAwsUrl();
    }

    private static String storeThumbnail(Photo photo, byte[] photoData) throws IOException {
        byte[] thumbnail = scalePhoto(THUMBNAIL_LONG_EDGE, photoData);
        S3StorageObject obj = getStorageObject(thumbnail, photo.getCheckinId() + THUMB_SUFFIX);
        S3StorageManager mgr = new S3StorageManager();
        mgr.storePublicRead(obj, true);
        return obj.getAwsUrl();
    }

    private static S3StorageObject getStorageObject (byte [] data, String storagePath) {
        S3StorageObject obj = new S3StorageObject();
        obj.setData(data);
        obj.setBucketName(uniqueBucketName);
        obj.setStoragePath(storagePath);
        return obj;
    }

    public static byte[] scalePhoto(int longEdgeSize, byte[] photoData) throws IOException {

        ByteArrayInputStream dataStream = new ByteArrayInputStream(photoData);
        ImageInputStream iis = ImageIO.createImageInputStream(dataStream);
        Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

        // Determine image format
        final String formatName;
        if (readers.hasNext()) {
            ImageReader reader = readers.next();
            formatName = reader.getFormatName();
        }
        else {
            logger.log(Level.SEVERE,"Unsupported image type");
            return null;
        }

        BufferedImage image = ImageIO.read(iis);

        int baseWidth = image.getWidth();
        int baseHeight = image.getHeight();

        float aspectRatio = (float)baseHeight/(float)baseWidth;

        final int modWidth;
        final int modHeight;

        if (baseWidth>baseHeight) {
            // width is long edge so scale based on that
            modWidth = longEdgeSize;
            modHeight = Math.round(aspectRatio*longEdgeSize);
        }
        else {
            modHeight = longEdgeSize;
            modWidth = Math.round(aspectRatio*longEdgeSize);
        }

        return scalePhoto(modWidth,modHeight, image, formatName);
    }

    private static byte[] scalePhoto(int targetWidth, int targetHeight, BufferedImage image, String formatName) throws IOException {

        ByteArrayOutputStream imageOut = new ByteArrayOutputStream();
        if (targetWidth>image.getWidth() || targetHeight>image.getHeight()) {
            // we don't want to scale up.  If it's smaller just leave it alone
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, formatName,out);
            return out.toByteArray();
        }
        else {
            BufferedImage scaledImage = scalePhoto(image, targetWidth, targetHeight);
            ImageIO.write(scaledImage,formatName,imageOut);
            return imageOut.toByteArray();
        }
    }

    private static BufferedImage scalePhoto (BufferedImage img, int targetWidth,
            int targetHeight) {
        int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
                : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage) img;

        int width = img.getWidth();
        int height = img.getHeight();

        /**
         * A quirk of the image scaling algorithm is that if you scale from a very large
         * image to a small image in one step, the quality of the scaling degrades quickly
         * leading to a noisy photo.  This process instead scales a photo down in smaller
         * increments in a loop until the right size is achieved.
         */
        while (width != targetWidth || height != targetHeight) {
            if (width > targetWidth) {
                width = width/2;
                if (width < targetWidth) {
                    width = targetWidth;
                }
            }

            if ( height > targetHeight) {
                height = height/2;
                if (height< targetHeight) {
                    height = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(width, height, type);
            Graphics2D graphics = tmp.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.drawImage(ret, 0, 0, width, height, null);
            graphics.dispose();

            ret = tmp;
        }

        return ret;
    }

}
