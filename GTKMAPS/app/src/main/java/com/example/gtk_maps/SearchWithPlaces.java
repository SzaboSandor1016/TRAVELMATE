package com.example.gtk_maps;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

public class SearchWithPlaces {
    @Embedded
    private Search search;
    @Relation(
            parentColumn = "id",
            entityColumn = "searchId"
    )
    private List<Place> places;
    @Relation(
            parentColumn = "id",
            entityColumn = "searchId"
    )
    private List<CategoryEntity> categories;

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public List<CategoryEntity> getCategories(){
        return categories;
    }
    public List<String> getCategoriesAsStrings() {
        ArrayList<String> categoriesL = new ArrayList<>();
        for (CategoryEntity entity: categories){
            categoriesL.add(entity.getCategory());
        }
        return categoriesL;
    }

    public void setCategories(List<CategoryEntity> categories) {
        this.categories = categories;
    }
}
