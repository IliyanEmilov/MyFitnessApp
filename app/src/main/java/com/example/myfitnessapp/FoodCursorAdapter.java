package com.example.myfitnessapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class FoodCursorAdapter extends CursorAdapter {
    public FoodCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // Методът newView се използва за раздуване(inflate) на нов изглед и връщането му,
    // не се свързват никакви данни към изгледа на този етап.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.fragment_food_list_item, parent, false);
    }

    // Методът bindView се използва за свързване на всички данни към даден изглед
    // като например задаване на текст в TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Намиране на полета за попълване в раздут(inflated) шаблон
        TextView textViewListName = (TextView) view.findViewById(R.id.textViewListName);
        TextView textViewListNumber = (TextView) view.findViewById(R.id.textViewListNumber);
        TextView textViewListSubName = (TextView) view.findViewById(R.id.textViewListSubName);

        // Извличане на свойства от курсора
        int getID = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        String getName = cursor.getString(cursor.getColumnIndexOrThrow("food_name"));

        String getManufactorName = cursor.getString(cursor.getColumnIndexOrThrow("food_manufactor_name"));
        String getDescription = cursor.getString(cursor.getColumnIndexOrThrow("food_description"));
        String getServingSize = cursor.getString(cursor.getColumnIndexOrThrow("food_serving_size_gram"));
        String getServingMesurment = cursor.getString(cursor.getColumnIndexOrThrow("food_serving_size_gram_mesurment"));
        String getServingNameNumber = cursor.getString(cursor.getColumnIndexOrThrow("food_serving_size_pcs"));
        String getServingNameWord = cursor.getString(cursor.getColumnIndexOrThrow("food_serving_size_pcs_mesurment"));
        int getEnergyCalculated = cursor.getInt(cursor.getColumnIndexOrThrow("food_energy_calculated"));

        String subLine = getManufactorName + ", " +
                getServingSize + " " +
                getServingMesurment + ", " +
                getServingNameNumber + " " +
                getServingNameWord;

        // Попълване на полета с извлечени свойства
        textViewListName.setText(getName);
        textViewListNumber.setText(String.valueOf(getEnergyCalculated));
        textViewListSubName.setText(subLine);

    }
}