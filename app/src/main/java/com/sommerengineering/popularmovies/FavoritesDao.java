package com.sommerengineering.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@SuppressWarnings("unused")
@Dao
public interface FavoritesDao {

    @Query("SELECT * FROM movies ORDER BY mId")
    LiveData<List<MovieObject>> loadAllFavoriteMovies();

    @Query("SELECT mId FROM movies ORDER BY mId")
    LiveData<List<Integer>> loadAllFavoriteIds();

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertFavoriteMovie(MovieObject movie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFavoriteMovie(MovieObject movie);

    @Delete
    void deleteFavoriteMovie(MovieObject movie);

}
