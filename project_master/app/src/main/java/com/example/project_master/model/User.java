package com.example.project_master.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

public class User implements Parcelable {

    private String username;
    private ObjectCoordonate userLocation;

    public User(String pseudo, ObjectCoordonate userLocation) {
        this.username =pseudo;
        this.userLocation= userLocation ;
    }

    protected User(Parcel in) {
        username = in.readString();
        userLocation = in.readParcelable(ObjectCoordonate.class.getClassLoader());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ObjectCoordonate getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(ObjectCoordonate userLocation) {
        this.userLocation = userLocation;
    }

    public List sendInformation() {
        return  Arrays.asList(username,userLocation.getLatitude(),userLocation.getLongitude());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeParcelable(userLocation, flags);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof User)) {
            return false;
        }
        return username.equals(((User) obj).username);
    }
}
