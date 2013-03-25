package com.tfnsnproject.to;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class MediaCheckin implements Parcelable {

    private String authToken;

    private String message;

    private Uri media;

    private Double lat, longg;

    private String placeName;

    private String placeId;

    public MediaCheckin() {
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Uri getMedia() {
        return media;
    }

    public void setMedia(Uri media) {
        this.media = media;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLong() {
        return longg;
    }

    public void setLong(Double longg) {
        this.longg = longg;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(authToken);
        out.writeString(message);
        out.writeString(placeName);
        out.writeString(placeId);
        out.writeParcelable(media, 0);
        out.writeDouble(lat == null ? 0 : lat);
        out.writeDouble(longg == null ? 0 : longg);
     }

     public static final Parcelable.Creator<MediaCheckin> CREATOR
             = new Parcelable.Creator<MediaCheckin>() {
         public MediaCheckin createFromParcel(Parcel in) {
             return new MediaCheckin(in);
         }

         public MediaCheckin[] newArray(int size) {
             return new MediaCheckin[size];
         }
     };

     private MediaCheckin(Parcel in) {
         authToken = in.readString();
         message = in.readString();
         placeName = in.readString();
         placeId = in.readString();
         media = in.readParcelable(getClass().getClassLoader());
         lat = in.readDouble();
         longg = in.readDouble();
     }
}


