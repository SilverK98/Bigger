package com.example.arnold.biggernbetter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ViewHolder>{

    private List<String> itemData;
    private LayoutInflater mInflater;
    private AdapterView.OnItemClickListener listener;

    // constructor
    public ItemRecyclerAdapter(Context context, List<String> itemData) {
        this.itemData = itemData;
        this.mInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    // inflates row layout from xml
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    //binds data to TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = itemData.get(position);
        holder.itemTextView.setText(item);
    }

    // get total number of rows
    @Override
    public int getItemCount() {
        return itemData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.item_name);
        }
    }

    String getItem(int id) {
        return itemData.get(id);
    }
}
