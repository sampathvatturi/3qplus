package com.spgon.a3flowers.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.android.billingclient.api.InAppMessageResponseListener;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.spgon.a3flowers.util.CommonFunctions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Generated;

@Entity(tableName = "User")
@Generated("jsonschema2pojo")
public class User {
    @PrimaryKey
    @NonNull
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;

    @NonNull
    @SerializedName("name")
    @Expose
    private String name;


    @SerializedName("sub_date")
    @Expose
    private String sub_date;

    @SerializedName("sub_status")
    @Expose
    private int sub_status;

    public User()
    {

    }
    public User(String name,String phoneNumber)
    {
        this.name= name;
        this.phoneNumber= phoneNumber;
    }

    public String getSub_date() {
        return sub_date;
    }

    public int getSub_status() {
        return sub_status;
    }

    public Date getSub_date_in_dateformat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = dateFormat.parse(sub_date);
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return date;
    }

    public void setSub_date(String sub_date) {
        this.sub_date = sub_date;
    }

    public boolean is_Sub_status() {
        if (sub_status == 1)
            return true;
        else
            return false;

    }

    public void setSub_status(int sub_status) {
        this.sub_status = sub_status;
    }

    @NonNull
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@NonNull String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        if(phoneNumber.isEmpty())
        {
            return CommonFunctions.convertToCamelCase(name);
        }
        return CommonFunctions.convertToCamelCase(name) + " - " + phoneNumber; //
        // Return the 'name'
        // field to display
        // in the Spinner
    }
    @Override
    public boolean equals(Object other) {
        if (other == null || getClass() != other.getClass()) return false;
        User user = (User) other;
        if (user.phoneNumber.equals(phoneNumber))
            return true;
        else
            return false;
    }
}
