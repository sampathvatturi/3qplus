package com.spgon.a3flowers.model;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Entity
@Generated("jsonschema2pojo")
public class UserData {

    @PrimaryKey
    @NonNull
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;


    @SerializedName("task_name")
    @Expose
    public int dayNumber=1;

    @NonNull
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@NonNull String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }
}
