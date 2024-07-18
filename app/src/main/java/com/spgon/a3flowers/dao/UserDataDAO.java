package com.spgon.a3flowers.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.spgon.a3flowers.model.UserData;

@Dao
public interface UserDataDAO {
    @Query("Select * from UserData where phoneNumber=:phoneNumber")
    UserData getUser(String phoneNumber);

    @Query("Delete from userdata")
    void deleteuserdata();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void update(UserData... userData);
}
