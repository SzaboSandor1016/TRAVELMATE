package com.example.gtk_maps;



import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// MyAdapter.java
public class SaveRecyclerViewAdapter extends RecyclerView.Adapter<SaveRecyclerViewAdapter.ViewHolder> {

    private final List<Save> mData;
    private final LayoutInflater mInflater;

    private final DeleteSaveItem deleteSaveItem;
    private final SelectSaveItem selectSaveItem;
    private final Resources resources;

    public interface DeleteSaveItem{
        void deleteSave(Save save);
    }
    public interface SelectSaveItem{
        void select(Save save);
    }

    // Konstruktor
    public SaveRecyclerViewAdapter(Context context, Resources resources, List<Save> mData, DeleteSaveItem deleteSaveItem, SelectSaveItem selectSaveItem) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
        this.deleteSaveItem = deleteSaveItem;
        this.selectSaveItem = selectSaveItem;
        this.resources = resources;
    }

    // Inflálja a listaelem nézetét
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.save_item_layout, parent, false);
        return new ViewHolder(view);
    }

    // Kitölti a listaelem nézetét az adatokkal
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Save item = mData.get(position);
        int index = position;

        holder.showSaveDetails.setOnClickListener(null);
        holder.deleteSave.setOnClickListener(null);
        holder.saveDetails.setVisibility(View.GONE);

        holder.saveTitle.setText(item.getTitle());
        if (item.getTransport().equals("walk"))
            holder.saveTransport.setText(resources.getString(R.string.walk));
        else holder.saveTransport.setText(resources.getString(R.string.car));
        holder.saveDistance.setText(String.valueOf(item.getDistance()));

        StringBuilder stringBuilder = new StringBuilder();
        for (String category: item.getCategories()){
            stringBuilder.append(category+"\n");
        }
        holder.saveCategories.setText(stringBuilder.toString());

        // Visszaállítás alapértelmezett láthatóságra
        holder.fromStartAddress.setVisibility(View.VISIBLE);

        if(item.getStartAddress()!=null){
            if (!item.getStartAddress().AddressAsString().equals("unknown")) {
                holder.saveStartAddress.setText(item.getStartAddress().AddressAsString());
            } else {
                holder.fromStartAddress.setVisibility(View.GONE);
            }
        } else {
            holder.fromStartAddress.setVisibility(View.GONE);
        }

        holder.saveDate.setText(item.getDate());

        // Visszaállítod a forgatás alaphelyzetbe
        holder.showSaveDetails.setRotation(0);

        holder.showSaveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.saveDetails.getVisibility() == View.GONE) {
                    holder.showSaveDetails.animate().rotation(180).setDuration(300).start();
                    Animation animation = AnimationUtils.loadAnimation(mInflater.getContext(), R.anim.slide_down);
                    holder.saveDetails.startAnimation(animation);
                    holder.saveDetails.setVisibility(View.VISIBLE);
                } else {
                    holder.showSaveDetails.animate().rotation(0).setDuration(300).start();
                    Animation animation = AnimationUtils.loadAnimation(mInflater.getContext(), R.anim.close_slide_down);
                    holder.saveDetails.startAnimation(animation);
                    holder.saveDetails.setVisibility(View.GONE);
                }
            }
        });

        holder.deleteSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement delete save
                deleteSaveItem.deleteSave(item);
            }
        });
        holder.saveTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSaveItem.select(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mData!=null)
            return mData.size();
        return 0;
    }

    // ViewHolder osztály
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView saveTitle, saveTransport,saveDistance,saveCategories,saveDate,saveStartAddress;
        View showSaveDetails;
        Button deleteSave;
        LinearLayout saveDetails, fromStartAddress;

        ViewHolder(View itemView) {
            super(itemView);
            saveTitle = itemView.findViewById(R.id.save_title);
            saveTransport = itemView.findViewById(R.id.save_transport);
            saveDistance = itemView.findViewById(R.id.save_distance);
            saveCategories = itemView.findViewById(R.id.save_categories);
            saveDate = itemView.findViewById(R.id.save_date);

            saveDetails = itemView.findViewById(R.id.save_details);

            showSaveDetails = itemView.findViewById(R.id.show_save_details);
            deleteSave = itemView.findViewById(R.id.delete_save);
            saveStartAddress = itemView.findViewById(R.id.save_start_address);
            fromStartAddress = itemView.findViewById(R.id.from_start_address);
        }
    }
}

