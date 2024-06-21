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

//Code to inflate RecyclerView with Food Records
public class CalorieGridAdapter extends RecyclerView.Adapter<CalorieGridAdapter.ViewHolder> {
    private List<FoodModel> FoodList;
    private OnItemClickListener ClickListener;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }
    public CalorieGridAdapter(List<FoodModel> foodList, OnItemClickListener clickListener){
        this.FoodList = foodList;
        this.ClickListener = clickListener;
    }
    public void setFoodList(List<FoodModel> foodList) {
        this.FoodList = foodList;
    }

    @NonNull
    @Override
    //Retrieves Calorie Grid item layout
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_layout_calorie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LinearLayout.LayoutParams calorieCountTextViewLayoutParams = (LinearLayout.LayoutParams) holder.calorieCountTextView.getLayoutParams();

        //For the first item in the list
        if (position == 0){
            //Increase weight of text views
            calorieCountTextViewLayoutParams.weight = 2;
            holder.calorieCountTextView.setLayoutParams(calorieCountTextViewLayoutParams);

            //Make delete button invisible
            holder.deleteButton.setVisibility(View.GONE);

            //Set text view typeface to bold
            holder.foodTextView.setTypeface(null, Typeface.BOLD);
            holder.calorieCountTextView.setTypeface(null, Typeface.BOLD);

            //Set visibility of textViews
            holder.foodTextView.setVisibility(View.VISIBLE);
            holder.calorieCountTextView.setVisibility(View.VISIBLE);
        } else {
            FoodModel currentItem = FoodList.get(position - 1);

            //Bind Data to text views
            holder.foodTextView.setText(currentItem.getFoodName());
            holder.calorieCountTextView.setText(Double.toString(currentItem.getCalories()));

            calorieCountTextViewLayoutParams.weight = 1;
            holder.calorieCountTextView.setLayoutParams(calorieCountTextViewLayoutParams);

            //Set typeface to normal
            holder.foodTextView.setTypeface(null, Typeface.NORMAL);
            holder.calorieCountTextView.setTypeface(null, Typeface.NORMAL);

            //Show the delete button
            holder.deleteButton.setVisibility(View.VISIBLE);

            //Set click listener for delete button
            holder.deleteButton.setOnClickListener((v -> ClickListener.onDeleteClick(position - 1)));
        }

    }

    public int getItemCount() { return FoodList.size() + 1; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodTextView, calorieCountTextView;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodTextView = itemView.findViewById(R.id.foodNameTextView);
            calorieCountTextView = itemView.findViewById(R.id.calorieCountTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}

