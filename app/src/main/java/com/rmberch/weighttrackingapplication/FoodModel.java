package com.rmberch.weighttrackingapplication;

public class FoodModel {
    private double CarbAmount, FatAmount, ProteinAmount, Calories, Servings;
    private String FoodName;
    //Declare constants for Calories per gram of carb, protein, and fat.
    int CALSPERCARBANDPROTEIN = 4;
    int CALSPERFAT = 9;

    public FoodModel(double carbAmount, double fatAmount, double proteinAmount, double servings, String foodName) {


        this.CarbAmount = carbAmount;
        this.FatAmount = fatAmount;
        this.ProteinAmount = proteinAmount;
        this.Servings = servings;
        this.FoodName = foodName;

        this.Calories = CalculateCalories(CarbAmount, FatAmount, ProteinAmount) * servings;
    }
    public FoodModel(String foodName, double calories) {
        this.FoodName = foodName;
        this.Calories = calories;
    }

    public double getCalories() { return Calories; }
    public String getFoodName() { return FoodName; }


    private double CalculateCalories(double carbAmount, double fatAmount, double proteinAmount) {
        double calories = -1;
        calories = ((CarbAmount + ProteinAmount) * CALSPERCARBANDPROTEIN) + (FatAmount * CALSPERFAT);
        return calories;
    }


}
