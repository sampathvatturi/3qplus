package com.spgon.a3flowers.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.spgon.a3flowers.model.User;

import java.util.List;

@Dao
public interface UserDAO {
    @Query("Select * from user where phoneNumber=:phoneNumber")
    User getUser(String phoneNumber);

    @Query("Select * from user where name=:name")
    User getUserName(String name);

    @Query("Select * from user ")
    List<User> getAllUsers();

    @Query("Delete from user ")
    void deleteUsers();
    @Update
    void update(User entity);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(User... users);
}
