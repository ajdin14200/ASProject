package com.example.project_master.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ObjectCoordonate implements Parcelable {

    private double latitude;

    protected ObjectCoordonate(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<ObjectCoordonate> CREATOR = new Creator<ObjectCoordonate>() {
        @Override
        public ObjectCoordonate createFromParcel(Parcel in) {
            return new ObjectCoordonate(in);
        }

        @Override
        public ObjectCoordonate[] newArray(int size) {
            return new ObjectCoordonate[size];
        }
    };

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private double longitude;

    public ObjectCoordonate(double latitude,double longitude) {
        this.latitude=latitude;
        this.longitude=longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
