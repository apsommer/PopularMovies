package com.sommerengineering.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class DetailViewModel extends AndroidViewModel {

    // list of custom movies objects
    private LiveData<List<Integer>> mFavoritesIds;
    private FavoritesDatabase mDatabase;

    // constructor sets list of custom movie objects from database
    public DetailViewModel(Application application) {
        super(application);
        mDatabase = FavoritesDatabase.getsInstance(this.getApplication());
        mFavoritesIds = mDatabase.favoritesDao().loadAllFavoriteIds();
    }

    // getters
    public LiveData<List<Integer>> getFavoritesIds() {
        return mFavoritesIds;
    }

    public FavoritesDatabase getDatabase() {
        return mDatabase;
    }

}
