package com.example.gtk_maps;

import android.graphics.drawable.Drawable;

public class Category {

    private Drawable categoryIcon1;
    private Drawable categoryIcon2;
    private Drawable categoryIcon3;
    private String urlPart;
    private String label;
    private String firebaseCategory;
    private boolean isChecked;

    public Category(Drawable categoryIcon1, Drawable categoryIcon2, Drawable categoryIcon3, String urPart, String label, String firebaseCategory){
        this.categoryIcon1 = categoryIcon1;
        this.categoryIcon2 = categoryIcon2;
        this.categoryIcon3 = categoryIcon3;
        this.urlPart = urPart;
        this.label = label;
        this.firebaseCategory = firebaseCategory;
        this.isChecked = false;
    }


    public String getUrlPart() {
        return urlPart;
    }

    public void setUrlPart(String urPart) {
        this.urlPart = urPart;
    }

    public Drawable getCategoryIcon1() {
        return categoryIcon1;
    }

    public void setCategoryIcon1(Drawable categoryIcon1) {
        this.categoryIcon1 = categoryIcon1;
    }

    public Drawable getCategoryIcon2() {
        return categoryIcon2;
    }

    public void setCategoryIcon2(Drawable categoryIcon2) {
        this.categoryIcon2 = categoryIcon2;
    }

    public Drawable getCategoryIcon3() {
        return categoryIcon3;
    }

    public void setCategoryIcon3(Drawable categoryIcon3) {
        this.categoryIcon3 = categoryIcon3;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFirebaseCategory() {
        return firebaseCategory;
    }

    public void setFirebaseCategory(String firebaseCategory) {
        this.firebaseCategory = firebaseCategory;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
