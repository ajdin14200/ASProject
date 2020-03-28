package com.example.project_master.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;


public class Checkpoint implements Parcelable {

    private String name;
    private ObjectCoordonate coordonate;


    protected Checkpoint(Parcel in) {
        name = in.readString();
        coordonate = in.readParcelable(ObjectCoordonate.class.getClassLoader());
    }

    public static final Creator<Checkpoint> CREATOR = new Creator<Checkpoint>() {
        @Override
        public Checkpoint createFromParcel(Parcel in) {
            return new Checkpoint(in);
        }

        @Override
        public Checkpoint[] newArray(int size) {
            return new Checkpoint[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Checkpoint(String name, ObjectCoordonate coordonate) {
        this.name = name;
        this.coordonate = coordonate;


    }

    public List sendInformation() {
        return Arrays.asList(name,coordonate.getLatitude(),coordonate.getLongitude());
    }

    public ObjectCoordonate getCoordonate() {
        return coordonate;
    }

    public void setCoordonate(ObjectCoordonate coordonate) {
        this.coordonate = coordonate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(coordonate, flags);
    }
}