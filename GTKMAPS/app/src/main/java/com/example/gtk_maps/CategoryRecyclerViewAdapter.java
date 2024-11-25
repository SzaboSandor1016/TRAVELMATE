package com.example.gtk_maps;


import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {

    private final List<Category> mData;
    private final LayoutInflater mInflater;
    private final AddRemoveCategory addRemoveCategory;
    private final UpdateUrl updateUrl;
    private final AddRemoveFirebaseCategory addRemoveFirebaseCategory;
    private final Resources resources;

    public interface AddRemoveCategory{
        void addCategory(String category);
        void removeCategory(String category);
    }
    public interface UpdateUrl{
        void addToURL(String urlPart);
        void removeFromURL(String urlPart);
    }
    public interface AddRemoveFirebaseCategory{
        void addCategory(String category);
        void removeCategory(String category);
    }

    public CategoryRecyclerViewAdapter(Context context, Resources resources, List<Category> mData, AddRemoveCategory addRemoveCategory, UpdateUrl updateUrl, AddRemoveFirebaseCategory addRemoveFirebaseCategory){
        this.mData = mData;
        this.mInflater = LayoutInflater.from(context);
        this.addRemoveCategory = addRemoveCategory;
        this.updateUrl = updateUrl;
        this.addRemoveFirebaseCategory = addRemoveFirebaseCategory;
        this.resources = resources;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.other_category_item_layout, parent, false);
        return new CategoryRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Category item = mData.get(position);

        holder.categoryItem.setBackground(null);

        // Beállítod a szöveget
        holder.categoryItemCheckbox.setText(item.getLabel());

        // Beállítod a képeket
        if (item.getCategoryIcon1() != null) {
            holder.categoryLogo1.setImageDrawable(item.getCategoryIcon1());
        } else {
            holder.categoryLogo1.setImageDrawable(null); // Üres kép beállítása
        }

        if (item.getCategoryIcon2() != null) {
            holder.categoryLogo2.setImageDrawable(item.getCategoryIcon2());
        } else {
            holder.categoryLogo2.setImageDrawable(null); // Üres kép beállítása
        }

        if (item.getCategoryIcon3() != null) {
            holder.categoryLogo3.setImageDrawable(item.getCategoryIcon3());
        } else {
            holder.categoryLogo3.setImageDrawable(null); // Üres kép beállítása
        }
        if (position % 2 == 1){
            holder.categoryItem.setBackground(resources.getDrawable(R.drawable.category_item));
        }

        // Esemény figyelő leállítása
        holder.categoryItemCheckbox.setOnCheckedChangeListener(null);

        // Az aktuális állapot visszaállítása
        holder.categoryItemCheckbox.setChecked(false);

        holder.categoryItemCheckbox.setChecked(item.isChecked());

        // Új OnCheckedChangeListener beállítása
        holder.categoryItemCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    addRemoveCategory.addCategory(item.getLabel());
                    updateUrl.addToURL(item.getUrlPart());
                    addRemoveFirebaseCategory.addCategory(item.getFirebaseCategory());
                    item.setChecked(true);
                } else {
                    addRemoveCategory.removeCategory(item.getLabel());
                    updateUrl.removeFromURL(item.getUrlPart());
                    addRemoveFirebaseCategory.removeCategory(item.getFirebaseCategory());
                    item.setChecked(false);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mData!=null)
            return mData.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox categoryItemCheckbox;
        LinearLayout categoryItem;
        ImageView categoryLogo1;
        ImageView categoryLogo2;
        ImageView categoryLogo3;

        ViewHolder(View itemView) {
            super(itemView);
            categoryItemCheckbox = itemView.findViewById(R.id.category_item_checkbox);
            categoryItem = itemView.findViewById(R.id.category_item);
            categoryLogo1 = itemView.findViewById(R.id.category_logo_1);
            categoryLogo2 = itemView.findViewById(R.id.category_logo_2);
            categoryLogo3 = itemView.findViewById(R.id.category_logo_3);

        }
    }
}
