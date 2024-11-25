package com.example.gtk_maps;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Search.class,parentColumns = "id", childColumns = "searchId"))
class CategoryEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private long searchId;
    private String category;

    public CategoryEntity() {}

    public long getSearchId() {
        return searchId;
    }

    public void setSearchId(long searchId) {
        this.searchId = searchId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
