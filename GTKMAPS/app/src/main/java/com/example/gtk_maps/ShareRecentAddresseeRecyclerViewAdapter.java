package com.example.gtk_maps;

import android.content.Context;
import android.content.res.Resources;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ShareRecentAddresseeRecyclerViewAdapter extends RecyclerView.Adapter<ShareRecentAddresseeRecyclerViewAdapter.ViewHolder> {

    private final LayoutInflater mInflater;
    private final ArrayList<Pair<String,Boolean>> mData;
    private final SelectAddresseeItem selectAddresseeItem;
    private final Resources resources;

    public interface SelectAddresseeItem{
        void select(Pair<String,Boolean> pair);
        void unselect(Pair<String,Boolean> pair);
    }

    public ShareRecentAddresseeRecyclerViewAdapter(Context context, ArrayList<Pair<String,Boolean>> mData, SelectAddresseeItem selectAddresseeItem, Resources resources){
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
        this.selectAddresseeItem = selectAddresseeItem;
        this.resources = resources;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.addresee_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pair<String,Boolean> item = mData.get(position);
        int index = position;
        holder.addressee.setActivated(false);
        holder.addressee.setText(item.first);

        if (item.second) {
            holder.addressee.setBackground(resources.getDrawable(R.drawable.toggle_button_style_on));
            holder.addressee.setActivated(true);
        }else {
            holder.addressee.setBackground(resources.getDrawable(R.drawable.toggle_button_style_off));
            holder.addressee.setActivated(false);
        }

        holder.addressee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!item.second) {
                    mData.set(index, new Pair<>(item.first, true));
                    notifyItemChanged(index,null);
                    selectAddresseeItem.select(new Pair<>(item.first, true));
                }else{
                    mData.set(index, new Pair<>(item.first, false));
                    notifyItemChanged(index,null);
                    selectAddresseeItem.unselect(new Pair<>(item.first, false));
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

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView addressee;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            addressee = itemView.findViewById(R.id.addressee);
        }
    }
}
