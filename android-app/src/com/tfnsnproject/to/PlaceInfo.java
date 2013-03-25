package com.tfnsnproject.to;

import android.os.Parcel;
import android.os.Parcelable;

public class PlaceInfo implements Parcelable {

    private String id;

    private String name;

    private Double lat, longg;

    public PlaceInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(name);
        out.writeDouble(lat);
        out.writeDouble(longg);
    }

    public static final Parcelable.Creator<PlaceInfo> CREATOR
            = new Parcelable.Creator<PlaceInfo>() {
        public PlaceInfo createFromParcel(Parcel in) {
            return new PlaceInfo(in);
        }

        public PlaceInfo[] newArray(int size) {
            return new PlaceInfo[size];
        }
    };

    private PlaceInfo(Parcel in) {
        id = in.readString();
        name = in.readString();
        lat = in.readDouble();
        longg = in.readDouble();
    }

}
