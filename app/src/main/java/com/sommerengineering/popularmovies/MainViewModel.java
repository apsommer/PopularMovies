package com.sommerengineering.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    // list of custom movies objects
    private LiveData<List<MovieObject>> mFavorites;

    // flag for if the user is viewing the favorites list, handles configuration change
    private boolean mIsViewingFavorites;

    // constructor sets list of custom movie objects from database
    public MainViewModel(Application application) {
        super(application);
        FavoritesDatabase database = FavoritesDatabase.getsInstance(this.getApplication());
        mFavorites = database.favoritesDao().loadAllFavoriteMovies();
    }

    // getters
    public LiveData<List<MovieObject>> getFavorites() {
        return mFavorites;
    }
    public boolean getIsViewingFavorites() {
        return mIsViewingFavorites;
    }

    // setters
    public void setIsViewingFavorites(boolean isViewingFavorites) {
        mIsViewingFavorites = isViewingFavorites;
    }

}
