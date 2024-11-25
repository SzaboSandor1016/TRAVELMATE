package com.example.gtk_maps;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gtk_maps.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FragmentsViewModel extends ViewModel {

    private AppDatabase.SearchDao searchDao;
    private MutableLiveData<Boolean> updated = new MutableLiveData<>();


    /*private MutableLiveData<String> selectedTransport = new MutableLiveData<>();
    private MutableLiveData<Integer> selectedDistance = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Place>> places = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> categories = new MutableLiveData<>();

    public void setTransport(String transport) {
        selectedTransport.setValue(transport);
    }

    public MutableLiveData<String> getTransport() {
        return selectedTransport;
    }

    public void setDistance(int distance) {
        selectedDistance.setValue(distance);
    }

    public MutableLiveData<Integer> getDistance() {
        return selectedDistance;
    }

    public void setPlaces(ArrayList<Place> places) {
        this.places.setValue(places);
    }

    public MutableLiveData<ArrayList<Place>> getPlaces() {
        return places;
    }

    public MutableLiveData<ArrayList<String>> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
       this.categories.setValue(categories);
    }*/

    public void uploadToDatabase(int distance, String transport, ArrayList<Place> places, ArrayList<String> categories) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            try {
                // Create the search object
                Search search = new Search(distance, transport);
                Log.d("databaseVM", String.valueOf(search.getTransport()));
                Log.d("databaseVM", String.valueOf(search.getDistance()));
                ArrayList<CategoryEntity> categoryEntities = new ArrayList<>();

                // Convert categories to CategoryEntity objects
                for (String category : categories) {
                    CategoryEntity entity = new CategoryEntity();
                    entity.setCategory(category);
                    categoryEntities.add(entity);
                }

                // Get the previous search (should be done in the background)
                List<Search> previous = searchDao.getSearch();

                // If there's a previous search, delete it
                if (previous != null ) {
                    if(previous.size()!=0)
                        searchDao.deleteTemporarySearch(previous.get(0));
                }

                // Insert the new search and related entities
                searchDao.insertTemporarySearch(search, categoryEntities, places);

                // Notify success on the main thread
                new Handler(Looper.getMainLooper()).post(() -> updated.postValue(true));
            } catch (Exception e) {
                e.printStackTrace(); // Log the error or handle it appropriately
                new Handler(Looper.getMainLooper()).post(() -> updated.postValue(false)); // Notify failure
            } finally {
                executorService.shutdown(); // Shut down the executor service
            }
        });
    }

    public List<Search> getSearch(){

        return searchDao.getSearch();

    }
    public List<Place> getPlaces(long id){

        return searchDao.getPlacesForSearch(id);

    }
    public List<String> getCategories(long id){

        List<String> categories = new ArrayList<>();
        List<CategoryEntity> categoryEntities = searchDao.getCategoriesForSearch(id);

        for (CategoryEntity category: categoryEntities){
             categories.add(category.getCategory());
        }
        return categories;
    }

    public AppDatabase.SearchDao getSearchDao() {
        return searchDao;
    }

    public void setSearchDao(AppDatabase.SearchDao searchDao) {
        this.searchDao = searchDao;
    }

    public MutableLiveData<Boolean> getUpdated() {
        return updated;
    }

    public void setUpdated(MutableLiveData<Boolean> updated) {
        this.updated = updated;
    }
}
