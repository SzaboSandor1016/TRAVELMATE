package com.example.gtk_maps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SuggestionLayout extends ArrayAdapter<String> {
    private final Context mContext;
    private final int mResource;

    public SuggestionLayout(@NonNull Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
        }

        String currentItem = getItem(position);
        String[] splitCurrent = currentItem.split(";");

        TextView mainItemTextView = listItemView.findViewById(R.id.main_item_text_view);
        TextView subItemTextView = listItemView.findViewById(R.id.sub_item_text_view);

        mainItemTextView.setText(splitCurrent[0]);
        subItemTextView.setText(splitCurrent[1]);

        return listItemView;
    }
}
