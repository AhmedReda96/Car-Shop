package com.example.carapp.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ProductEntity.class,CartEntity.class}, version = 1,exportSchema = false)
public abstract class MainDataBase extends RoomDatabase {
    private static MainDataBase instance;

    public abstract ProductDao productDao();


    public static synchronized MainDataBase getInstance(final Context context) {
        if (instance == null) {

            instance = Room.databaseBuilder(context.getApplicationContext(), MainDataBase.class, "database")
                    .fallbackToDestructiveMigration()
                    .build();


        }
        return instance;
    }
}