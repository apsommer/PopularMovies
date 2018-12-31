package com.sommerengineering.popularmovies;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

@Database(entities = {MovieObject.class}, version = 1, exportSchema = false)
public abstract class FavoritesDatabase extends RoomDatabase {

    // String tag for log messages
    private static final String LOG_TAG = FavoritesDatabase.class.getSimpleName();

    // arbitrary new object that makes the class a singleton
    private static final Object LOCK = new Object();

    // constants
    private static final String DATABASE_NAME = "favorites";
    private static FavoritesDatabase sInstance;

    // returns a reference to the database
    public static FavoritesDatabase getsInstance(Context context) {

        // create a new database on the device if it does not already exist
        if (sInstance == null) {

            // only one database of this name can exist on the device; a singleton
            synchronized (LOCK) {

                Log.e(LOG_TAG, "Creating new database named 'favorites'.");

                // create the new database using Room architecture
                // TODO temporarily allow on main thread
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        FavoritesDatabase.class, DATABASE_NAME).allowMainThreadQueries().build();
            }
        }

        Log.e(LOG_TAG, "Database named 'favorites' already exists, getting it.");

        // this database already exists so return a reference to it
        return sInstance;
    }

    public abstract FavoritesDao favoritesDao();

}
