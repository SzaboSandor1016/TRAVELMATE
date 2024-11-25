package com.example.gtk_maps;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Embedded;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Relation;
import androidx.room.RoomDatabase;
import androidx.room.Transaction;

import java.util.ArrayList;
import java.util.List;

@Database(entities = {Search.class, Place.class, CategoryEntity.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SearchDao searchDao();

    @Dao
    public interface SearchDao {
        /*@Transaction
        @Query("SELECT * FROM Search")
        public List<SearchWithPlaces> getSearchWithPlaces();*/

        @Insert
        long insert(Search search);
        @Insert
        void insertAllPlaces(List<Place> places);
        @Insert
        void insertAllCategories(List<CategoryEntity> categoryEntities);

        @Transaction
        default void insertTemporarySearch(Search search, ArrayList<CategoryEntity> categoryEntities, ArrayList<Place> places){
            long searchId = insert(search);

            for (Place place: places){
                place.setSearchId(searchId);
            }

            insertAllPlaces(places);

            for (CategoryEntity category: categoryEntities){
                category.setSearchId(searchId);
            }
            insertAllCategories(categoryEntities);
        }

        @Delete
        void delete(Search search);

        @Delete
        void deletePlaces(List<Place> places);
        @Delete
        void deleteCategories(List<CategoryEntity> places);

        @Transaction
        default void deleteTemporarySearch(Search search) {
            // Retrieve the associated Places before deleting the Search
            List<Place> places = getPlacesForSearch(search.getId());
            List<CategoryEntity> categoryEntities = getCategoriesForSearch(search.getId());

            // Delete Places
            deletePlaces(places);

            deleteCategories(categoryEntities);

            // Delete Search
            delete(search);
        }

        @Query("SELECT * FROM Search")
        List<Search> getSearch();

        @Query("SELECT * FROM Place WHERE searchId = :searchId")
        List<Place> getPlacesForSearch(long searchId);
        @Query("SELECT * FROM CategoryEntity WHERE searchId = :searchId")
        List<CategoryEntity> getCategoriesForSearch(long searchId);

    }

}
