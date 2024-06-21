package com.rmberch.weighttrackingapplication;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//Code to inflate RecyclerView with Weight Records
public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    private List<DataModel> dataList;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
        void onEditClick(int position);
    }

    public GridAdapter(List<DataModel> dataList, OnItemClickListener clickListener) {
        this.dataList = dataList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    //Retrives Grid item layout
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LinearLayout.LayoutParams weightTextViewLayoutParams = (LinearLayout.LayoutParams) holder.weightTextView.getLayoutParams();

        if (position == 0) {

            weightTextViewLayoutParams.weight = 2;
            holder.weightTextView.setLayoutParams(weightTextViewLayoutParams);

            // Hide or handle the visibility of other views like delete and edit buttons for header
            holder.deleteButton.setVisibility(View.GONE);
            holder.editButton.setVisibility(View.GONE);

            // Set the typeface to bold for the TextViews
            holder.dateTextView.setTypeface(null, Typeface.BOLD);
            holder.weightTextView.setTypeface(null, Typeface.BOLD);

            // Set the visibility of header TextViews
            holder.dateTextView.setVisibility(View.VISIBLE);
            holder.weightTextView.setVisibility(View.VISIBLE);

        } else {
            // This is a data row
            DataModel currentItem = dataList.get(position - 1);

            // Bind data to TextViews
            holder.dateTextView.setText(currentItem.getDate());
            holder.weightTextView.setText(String.valueOf(currentItem.getWeight()));

            weightTextViewLayoutParams.weight = 1;
            holder.weightTextView.setLayoutParams(weightTextViewLayoutParams);

            // Set the typeface to normal for the TextViews
            holder.dateTextView.setTypeface(null, Typeface.NORMAL);
            holder.weightTextView.setTypeface(null, Typeface.NORMAL);

            // Show delete and edit buttons for data rows
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.editButton.setVisibility(View.VISIBLE);

            // Set click listeners for delete and edit buttons
            holder.deleteButton.setOnClickListener(v -> clickListener.onDeleteClick(position - 1));
            holder.editButton.setOnClickListener(v -> clickListener.onEditClick(position - 1));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() + 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, weightTextView;
        ImageButton deleteButton, editButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            weightTextView = itemView.findViewById(R.id.weightTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);
        }
    }
}

