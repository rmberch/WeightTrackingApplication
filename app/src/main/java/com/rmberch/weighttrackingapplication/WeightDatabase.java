package com.rmberch.weighttrackingapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

//Class for database code
public class WeightDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "weight.db";
    private static final int VERSION = 1;

    public WeightDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    //Class for Account table name and columns
    private static final class AccountTable {
        private static final String TABLE = "account";
        private static final String COL_ID = "_id";
        private static final String COL_USERNAME = "username";
        private static final String COL_PASSWORD = "password";
        private static final String COL_PHONE = "phone";
    }

    //History table name and columns
    private static final class HistoryTable {
        private static final String TABLE = "history";
        private static final String COL_ID = "_id";
        private static final String COL_DATE ="date";
        private static final String COL_WEIGHT = "weight";
    }

    //Goal table name and columns
    private static final class GoalTable {
        private static final String TABLE = "goal";
        private static final String COL_ID = "_id";
        private static final String COL_GOAL_WEIGHT = "goal_weight";
        private static final String COL_GOAL_CALORIES = "goal_calories";
    }

    //Food record table name can columns
    private static final class FoodTable {
        private static final String TABLE = "food";
        private static final String COL_ID = "_id";
        private static final String COL_DATE = "date";
        private static final String COL_FOOD = "food";
        private static final String COL_CALORIES = "calories";
    }

    @Override
    //Create database
    public void onCreate (SQLiteDatabase db) {
        //Creates table for user account information, auto generating an ID
        db.execSQL("create table " + AccountTable.TABLE + " (" +
            AccountTable.COL_ID + " integer primary key autoincrement, " +
            AccountTable.COL_USERNAME + " text, " +
            AccountTable.COL_PASSWORD + " text, " +
            AccountTable.COL_PHONE + " text)");
        //Creates the table for the user weight history, with id foreign key from account table
        db.execSQL("create table " + HistoryTable.TABLE + " (" +
                HistoryTable.COL_ID + " integer," +
                HistoryTable.COL_DATE + " text, " +
                HistoryTable.COL_WEIGHT + " real, " +
                " foreign key (_id) references account(_id))");
        //Creates table for user goal weight and calories with id foreign key from account table
        db.execSQL("create table " + GoalTable.TABLE + " (" +
                GoalTable.COL_ID + " integer, " +
                GoalTable.COL_GOAL_WEIGHT + " real, " +
                GoalTable.COL_GOAL_CALORIES + " integer, " +
                " foreign key (_id) references account(_id))");
        //Creates table for food records with id, date, description, and calorie columns
        db.execSQL("create table " + FoodTable.TABLE + " (" +
                FoodTable.COL_ID + " integer, " +
                FoodTable.COL_DATE + " text, " +
                FoodTable.COL_FOOD + " text, " +
                FoodTable.COL_CALORIES + " integer, " +
                " foreign key (_id) references account(_id))");
    }

    //
    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        //On upgrade, drop all tables, and recreate
        db.execSQL("DROP TABLE " + AccountTable.TABLE);
        db.execSQL("DROP TABLE " + GoalTable.TABLE);
        db.execSQL("DROP TABLE " + HistoryTable.TABLE);
        db.execSQL("DROP TABLE " + FoodTable.TABLE);
        onCreate(db);
    }

    @Override
    //Allows foreign key
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // ACCOUNT TABLE FUNCTIONS

    //Add account
    public boolean addAccount(@NonNull UserModel userModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues accountCV = new ContentValues();

        accountCV.put(AccountTable.COL_USERNAME, userModel.getUsername());
        accountCV.put(AccountTable.COL_PASSWORD, userModel.getPassword());

        long accountInsert = db.insert(AccountTable.TABLE, null, accountCV);

        //Get ID of new account, and create record in goal table
        int id = checkExisting(userModel);
        if (id != -1){
            ContentValues goalCV = new ContentValues();
            goalCV.put(GoalTable.COL_ID, id);
            long goalInsert = db.insert(GoalTable.TABLE, null, goalCV);
        }

        return accountInsert != -1;
    }

    //Check for existing account
    public int checkExisting(@NonNull UserModel userModel) {
        int id = -1;
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select * from " + AccountTable.TABLE + " where " +
                AccountTable.COL_USERNAME + " = '" + userModel.getUsername() + "'";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);
            }while (cursor.moveToNext());
        }
        cursor.close();

        return id;
    }

    //If matches account record, log in and return id
    public int logIn(@NonNull UserModel userModel) {
        int id = -1;
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select * from " + AccountTable.TABLE + " where " +
                AccountTable.COL_USERNAME + " = '" + userModel.getUsername() + "'" +
                " and " + AccountTable.COL_PASSWORD + " = '" + userModel.getPassword() + "'";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);
            }while (cursor.moveToNext());
        }
        cursor.close();

        return id;
    }
    //Add phone number to account record
    public Boolean setPhoneNumber(String phoneText, int userId) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(AccountTable.COL_PHONE, phoneText);

        int rowsUpdated = db.update(AccountTable.TABLE, cv, AccountTable.COL_ID + " = " + userId, null);
        return rowsUpdated > 0;
    }
    //Retrieve phone number from account record
    public String getPhoneNumber(int ID) {
        String phoneNumber = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + AccountTable.TABLE + " where " +
                AccountTable.COL_ID + " = " + ID;

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do{
                phoneNumber = cursor.getString(3);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return phoneNumber;
    }


    // GOAL TABLE FUNCTIONS


    //Set goal weight record
    public boolean setGoalWeight(int id, float weight) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(GoalTable.COL_GOAL_WEIGHT, weight);

        int rowsUpdated = db.update(GoalTable.TABLE, cv, GoalTable.COL_ID + " = " + id, null);
        return rowsUpdated > 0;
    }

    
    //Get goal weight record
    public String getGoalWeight(int id) {
        String goalWeight = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + GoalTable.TABLE + " where " +
                GoalTable.COL_ID + " = " + id;

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do{
                goalWeight = cursor.getString(1);
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (goalWeight == null) {
            goalWeight = "No goal weight. Please set one.";
        }

        return goalWeight;
    }

    public boolean setCalorieGoal(int id, int calorieGoal) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(GoalTable.COL_GOAL_CALORIES, calorieGoal);

        int rowsUpdated = db.update(GoalTable.TABLE, cv, GoalTable.COL_ID + " = " + id, null);
        return rowsUpdated > 0;
    }

    public String getCalorieGoal(int id) {
        String goalCalorie = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select " + GoalTable.COL_GOAL_CALORIES + " from " + GoalTable.TABLE + " where " +
                GoalTable.COL_ID + " = " + id;

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do{
                goalCalorie = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return goalCalorie;
    }

    //WEIGHT HISTORY TABLE FUNCTIONS

    //Get all weight log records
    public List getHistory(int id) {
        List<DataModel> history = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "select *  from " + HistoryTable.TABLE +
                " where " + HistoryTable.COL_ID + " = " + id;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(1);
                float weight = Float.parseFloat(cursor.getString(2));
                DataModel dataModel = new DataModel(date, weight);
                history.add(dataModel);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return history;
    }
    //Add weight log record
    public boolean addWeightRecord(DataModel dataModel, int userId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(HistoryTable.COL_ID, userId);
        cv.put(HistoryTable.COL_DATE, dataModel.getDate());
        cv.put(HistoryTable.COL_WEIGHT, dataModel.getWeight());

        long insert = db.insert(HistoryTable.TABLE, null, cv);
        return insert != -1;
    }
    //update weight log record
    public boolean updateWeightRecord(int id, String oldDate, String newDate, float newWeight) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(HistoryTable.COL_WEIGHT, newWeight);
        cv.put(HistoryTable.COL_DATE, newDate);

        int rowsUpdated = db.update(HistoryTable.TABLE, cv, HistoryTable.COL_ID + " = " + id +
                " and " + HistoryTable.COL_DATE + " = '" + oldDate + "'", null);
        return rowsUpdated > 0;
    }
    //remove weight log record
    public Boolean removeWeightRecord(int userId, DataModel removeData) {
        SQLiteDatabase db = getWritableDatabase();

        int rowsDeleted = db.delete(HistoryTable.TABLE,
                HistoryTable.COL_ID + " = " + userId + " and " +
                HistoryTable.COL_DATE + " = '" +  removeData.getDate() + "'", null);
        return rowsDeleted > 0;
    }


    // CALORIE HISTORY FUNCTIONS BELOW //

    //Retrieves user's food records from the database
    public List<FoodModel> getCalorieHistory(int ID, String DATE) {
        List<FoodModel> calorieHistory = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "select * from " + FoodTable.TABLE +
                " where " + FoodTable.COL_ID + " = " + ID +
                " and " + FoodTable.COL_DATE + " = '" + DATE + "'";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String description = cursor.getString(2);
                double calories = Double.parseDouble(cursor.getString(3));
                FoodModel foodModel = new FoodModel(description, calories);
                calorieHistory.add(foodModel);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return calorieHistory;
    }

    //Adds new food record to the database
    public boolean addFoodRecord(int ID, FoodModel model, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(FoodTable.COL_ID, ID);
        cv.put(FoodTable.COL_DATE, date);
        cv.put(FoodTable.COL_FOOD, model.getFoodName());
        cv.put(FoodTable.COL_CALORIES, model.getCalories());

        long insert = db.insert(FoodTable.TABLE, null, cv);
        return insert != -1;
    }
    //Remove existing food record
    public boolean removeFoodRecord(int ID, FoodModel model, String date){
        SQLiteDatabase db = getWritableDatabase();

        int rowsDeleted = db.delete(FoodTable.TABLE,
                FoodTable.COL_ID + " = " + ID + " and " +
                        FoodTable.COL_DATE + " = '" + date + "' and " +
                        FoodTable.COL_FOOD + " = '" + model.getFoodName() + "' and " +
                        FoodTable.COL_CALORIES + " = " + model.getCalories(), null);
        return rowsDeleted > 0;
    }



}
