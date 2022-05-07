package com.example.tileshop;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ShoppingTileItemAdapter extends RecyclerView.Adapter<ShoppingTileItemAdapter.ViewHolder> implements Filterable {
    private final static String LOG_TAG = ShoppingTileItemAdapter.class.getName();
    private ArrayList<TileItems> tileItems;
    private ArrayList<TileItems> tileItemsAll;
    private Context context;
    private int lastPosition = -1;

    ShoppingTileItemAdapter(Context context, ArrayList<TileItems> itemsData){
        this.tileItems = itemsData;
        this.tileItemsAll = itemsData;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.tile_items_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ShoppingTileItemAdapter.ViewHolder holder, int position) {
        TileItems currentItem = tileItems.get(position);
        holder.bindTo(currentItem);
        if(holder.getAdapterPosition() > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return tileItems.size();
    }

    @Override
    public Filter getFilter() {
        return sfilter;
    }

    private Filter sfilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<TileItems> filterList = new ArrayList<>();
            FilterResults results = new FilterResults();
            if(charSequence == null && charSequence.length() == 0){
                results.count = tileItemsAll.size();
                results.values = tileItemsAll;
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(TileItems item : tileItemsAll){
                    if(item.getName().toLowerCase().contains(filterPattern)){
                        filterList.add(item);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            tileItems = (ArrayList)filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView titleText;
        private TextView descriptionText;
        private TextView priceText;
        private ImageView itemImage;
        private RatingBar ratingBar;
        public ViewHolder(View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.itemTitle);
            descriptionText = itemView.findViewById(R.id.subTitle);
            priceText = itemView.findViewById(R.id.price);
            itemImage = itemView.findViewById(R.id.itemImage);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }

        public void bindTo(TileItems currentItem) {
            titleText.setText(currentItem.getName());
            descriptionText.setText(currentItem.getDescription());
            priceText.setText(currentItem.getPrice());
            ratingBar.setRating(currentItem.getRatedInfo());
            Glide.with(context).load(currentItem.getImage()).into(itemImage);

            itemView.findViewById(R.id.add_to_cart).setOnClickListener(view -> ((TileListActivity)context).updateCartIcon(currentItem));

            itemView.findViewById(R.id.delete).setOnClickListener(view -> ((TileListActivity)context).delete(currentItem));
        }
    }
}


