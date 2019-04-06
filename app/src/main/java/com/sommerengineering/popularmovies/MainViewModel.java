package com.sommerengineering.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    // list of custom movies objects
    private final LiveData<List<MovieObject>> mFavorites;

    // constructor sets list of custom movie objects from database
    public MainViewModel(Application application) {
        super(application);
        FavoritesDatabase database = FavoritesDatabase.getsInstance(this.getApplication());
        mFavorites = database.favoritesDao().loadAllFavoriteMovies();
    }

    // getter
    public LiveData<List<MovieObject>> getFavorites() {
        return mFavorites;
    }

}
