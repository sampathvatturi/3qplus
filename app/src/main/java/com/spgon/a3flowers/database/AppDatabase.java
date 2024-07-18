package com.spgon.a3flowers.database;

import androidx.room.RoomDatabase;
import androidx.room.Database;

import com.spgon.a3flowers.dao.UserDAO;
import com.spgon.a3flowers.dao.UserDataDAO;
import com.spgon.a3flowers.model.User;
import com.spgon.a3flowers.model.UserData;

@Database(entities = {User.class, UserData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDAO userDAO();
    public abstract UserDataDAO userDataDAO();

}
