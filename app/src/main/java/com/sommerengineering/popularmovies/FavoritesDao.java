package com.sommerengineering.popularmovies;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface FavoritesDao {

    @Query("SELECT * FROM movies ORDER BY mTitle")
    List<MovieObject> loadAllFavoriteMovies();

    @Insert
    void insertFavoriteMovie(MovieObject movie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFavoriteMovie(MovieObject movie);

    @Delete
    void deleteFavoriteMovie(MovieObject movie);

}
