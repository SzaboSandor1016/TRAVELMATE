package com.example.gtk_maps;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ShareRecyclerViewAdapter extends RecyclerView.Adapter<ShareRecyclerViewAdapter.ShareViewHolder> {

    private final List<Share> mData;
    private final boolean isSharedByMe;
    private final LayoutInflater mInflater;
    private final Resources resources;
    private final DeleteShare deleteShare;
    private final ModifyShare modifyShare;
    private final SelectShare selectShare;

    public interface DeleteShare{
        void deleteShare(Share toBeDeleted);
    }
    public interface ModifyShare{
        void modifyShare(Share toBeModified);
    }
    public interface SelectShare{
        void select(Share share);
    }

    public ShareRecyclerViewAdapter(@NonNull Context context, ArrayList<Share> mData, Resources resources, boolean isSharedByMe, DeleteShare deleteShare, ModifyShare modifyShare, SelectShare selectShare) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
        this.isSharedByMe = isSharedByMe;
        this.resources = resources;
        this.deleteShare = deleteShare;
        this.modifyShare = modifyShare;
        this.selectShare = selectShare;
    }

    @NonNull
    @Override
    public ShareViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.share_item_layout, parent, false);
        return new ShareViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShareViewHolder holder, int position) {
        Share item = mData.get(position);
        holder.shareTitle.setText(item.getTitle());
        if (isSharedByMe){
            if(item.getSharedWithStrings()!=null) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String sharedWith : item.getSharedWithStrings()) {
                    stringBuilder.append(sharedWith + " ");
                    //Log.d("RecyclerSharedWith", sharedWith);
                }
                holder.shareSubTitle.setText(stringBuilder.toString());
            }
        }
        else{
            holder.shareSubTitle.setText(item.getOpActual());
            //Log.d("RecyclerOp", item.getOpActual());
        }

        holder.fromShareStartAddress.setVisibility(View.VISIBLE);
        holder.shareNoteLayout.setVisibility(View.VISIBLE);
        holder.modifyShare.setVisibility(View.GONE);

        holder.deleteShare.setOnClickListener(null);
        holder.selectShare.setOnClickListener(null);
        holder.modifyShare.setOnClickListener(null);

        //TODO show the op or the usernames of the users that this search is shared with

        if (item.getShareDetails().getTransportMode().equals("walk"))
            holder.shareTransport.setText(resources.getString(R.string.walk));
        else holder.shareTransport.setText(resources.getString(R.string.car));

        if (item.getShareDetails().getAddress()!=null){
            if (!item.getShareDetails().getAddress().AddressAsString().equals("unknown")) {
                holder.shareStartAddress.setText(item.getShareDetails().getAddress().AddressAsString());
            }else {
                holder.fromShareStartAddress.setVisibility(View.GONE);
            }
        } else {
            holder.fromShareStartAddress.setVisibility(View.GONE);
        }
        if (!item.getNote().equals("")) {
            holder.shareNote.setText(item.getNote());
        } else {
            holder.shareNoteLayout.setVisibility(View.GONE);
        }

        holder.shareDistance.setText(String.valueOf(item.getShareDetails().getDistance()));

        StringBuilder stringBuilder = new StringBuilder();
        for (String category: item.getShareDetails().getCategories()){
            stringBuilder.append(category+"\n");
        }

        holder.shareCategories.setText(stringBuilder.toString());
        holder.shareDate.setText(item.getShareDetails().getDate());

        if (isSharedByMe){
            holder.modifyShare.setVisibility(View.VISIBLE);
            holder.modifyShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO modify share probably with drop down layout.
                        modifyShare.modifyShare(item);

                }
            });
        }
        holder.deleteShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO delete share according to the op
                deleteShare.deleteShare(item);
            }
        });

        holder.showShareDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.shareDetails.getVisibility() == View.GONE) {
                    holder.showShareDetails.animate().rotation(180).setDuration(300).start();
                    Animation animation = AnimationUtils.loadAnimation(mInflater.getContext(), R.anim.slide_down);
                    holder.shareDetails.startAnimation(animation);
                    holder.shareDetails.setVisibility(View.VISIBLE);
                } else {
                    holder.showShareDetails.animate().rotation(0).setDuration(300).start();
                    Animation animation = AnimationUtils.loadAnimation(mInflater.getContext(), R.anim.close_slide_down);
                    holder.shareDetails.startAnimation(animation);
                    holder.shareDetails.setVisibility(View.GONE);
                }
            }
        });
        holder.selectShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectShare.select(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mData!= null)
            return mData.size();
        return 0;
    }

    public class ShareViewHolder extends RecyclerView.ViewHolder {
        TextView shareTitle,shareSubTitle, shareTransport,shareDistance,shareCategories,shareDate,shareStartAddress, shareNote;
        View showShareDetails;
        Button deleteShare,modifyShare;
        LinearLayout shareDetails, selectShare, fromShareStartAddress, shareNoteLayout;

        ShareViewHolder(View itemView) {
            super(itemView);
            shareTitle = itemView.findViewById(R.id.share_title);
            shareSubTitle = itemView.findViewById(R.id.share_sub_title);
            shareTransport = itemView.findViewById(R.id.share_transport);
            shareDistance = itemView.findViewById(R.id.share_distance);
            shareCategories = itemView.findViewById(R.id.share_categories);
            shareDate = itemView.findViewById(R.id.share_date);
            shareNote = itemView.findViewById(R.id.share_note_item);

            shareDetails = itemView.findViewById(R.id.share_details);

            fromShareStartAddress = itemView.findViewById(R.id.from_share_start_address);
            shareNoteLayout = itemView.findViewById(R.id.share_note_item_layout);

            showShareDetails = itemView.findViewById(R.id.show_share_details);
            deleteShare = itemView.findViewById(R.id.delete_share);
            modifyShare = itemView.findViewById(R.id.modify_share);
            shareStartAddress = itemView.findViewById(R.id.share_start_address);
            selectShare = itemView.findViewById(R.id.select_share);
        }
    }
}
