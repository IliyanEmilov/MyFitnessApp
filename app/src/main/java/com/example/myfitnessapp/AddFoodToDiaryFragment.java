package com.example.myfitnessapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class AddFoodToDiaryFragment extends Fragment {

    /* Променливи на класа */
    private View mainView;
    private Cursor listCursorCategory;
    private Cursor listCursorFood;

    //Бутони за действие на лентата с инструменти
    private MenuItem menuItemEdit;
    private MenuItem menuItemDelete;

    //Holder
    private String currentMealNumber;
    private String currentCategoryId;
    private String currentCategoryName;
    private String currentFoodId;
    private String currentFoodName;

    private boolean lockPortionSizeByPcs;
    private boolean lockPortionSizeByGram;


    /* Фрагментни променливи */
    // Необходим за изпълнение на фрагмента
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    /* конструктор*/
    //Необходим за наличието на Fragment като клас
    public AddFoodToDiaryFragment() {
        // Необходим празен публичен конструктор
    }

    /* Създаване на фрагмент*/
    public static AddFoodToDiaryFragment newInstance(String param1, String param2) {
        AddFoodToDiaryFragment fragment = new AddFoodToDiaryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /* на Създадена дейност*/
    // Run methods when started
    // Изпълняване на методи при стартиране
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* Задаване на заглавие */
        ((FragmentActivity)getActivity()).getSupportActionBar().setTitle("Add food to diary");


        /* Взимане на данни от фрагмент */
        String stringAction = "";
        Bundle bundle = this.getArguments();
        if(bundle != null){
            currentMealNumber = bundle.getString("mealNumber");
            currentFoodId = bundle.getString("currentFoodId");
            stringAction = bundle.getString("action");
        }

        if(stringAction.equals("")) {
            // Попълване на списъка с категории
            populateListWithCategories("0", "");
        }
        else if(stringAction.equals("foodInCategoryListItemClicked")){
            preFoodInCategoryListItemClicked();
        }

        // Създаване на меню
        // setHasOptionsMenu(true);


    } // onActivityCreated

    /* При създаване на view*/
    // Задава главната променлива на View към изгледа,
    // така че можем да променяме изгледите във фрагмент
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_add_food_to_diary, container, false);
        return mainView;
    }

    /* задаване на main View */
    // Промяна на метода на изглед във фрагмент
    private void setMainView(int id){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(id, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(mainView);
    }

    /* в менюто за опции за създаване*/
    // Създаване на икона за действие в лентата с инструменти
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Inflate menu
        ((FragmentActivity)getActivity()).getMenuInflater().inflate(R.menu.menu_categories, menu);

        // Присвояване на елементи от менюто към променливи
        menuItemEdit = menu.findItem(R.id.action_edit);
        menuItemDelete = menu.findItem(R.id.action_delete);

        // Скриване по подразбиране
        menuItemEdit.setVisible(false);
        menuItemDelete.setVisible(false);
    }


    /* на Опции Избран елемент*/
    // Щракване върху иконата за действие
    // Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        int id = menuItem.getItemId();

        return super.onOptionsItemSelected(menuItem);
    }

    /* Попълване на списък с категории */
    public void populateListWithCategories(String stringCategoryParentID, String stringCatgoryName){

        /* База данни */
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        // Вземете категории
        String fields[] = new String[] {
                "_id",
                "category_name",
                "category_parent_id"
        };
        listCursorCategory = db.select("categories", fields, "category_parent_id", stringCategoryParentID, "category_name", "ASC");

        // Създайте масив
        ArrayList<String> values = new ArrayList<String>();

        // Преобразуване на категории в стринг
        int categoriesCount = listCursorCategory.getCount();
        for(int x=0;x<categoriesCount;x++){
            values.add(listCursorCategory.getString(listCursorCategory.getColumnIndex("category_name")));

            listCursorCategory.moveToNext();
        }


        // Затваряне на курсора
        // categoriesCursor.close();

        // Създаване на адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, values);

        // Задаване на адаптер
        ListView lv = (ListView)getActivity().findViewById(R.id.listViewAddFoodToDiary);
        lv.setAdapter(adapter);

        // OnClick
        if(stringCategoryParentID.equals("0")) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    categoryListItemClicked(arg2);
                }
            });
        }

        // затваряне на db
        db.close();

    } // populateListWithCategories

    /* Щракване върху елемент от списъка с категории */
    public void categoryListItemClicked(int listItemIndexClicked){

        // Преместване на курсора до щракнатото ID
        listCursorCategory.moveToPosition(listItemIndexClicked);

        // Взимане на ID и име от курсора
        // Задаване на текущо име и ID
        currentCategoryId = listCursorCategory.getString(0);
        currentCategoryName = listCursorCategory.getString(1);
        String parentCategoryID = listCursorCategory.getString(2);

        // Промяна на заглавието
        ((FragmentActivity)getActivity()).getSupportActionBar().setTitle("Add food from " + currentCategoryName + " to diary");


        // Преминаване към подклас
        populateListWithCategories(currentCategoryId, currentCategoryName);


        // Показване на храна в категория
        showFoodInCategory(currentCategoryId, currentCategoryName, parentCategoryID);


    }

    /* Показване на храна в категория */
    public void showFoodInCategory(String categoryId, String categoryName, String categoryParentID){
        if(!(categoryParentID.equals("0"))) {

            /* Промяна на layout */
            int id = R.layout.fragment_food;
            setMainView(id);

            /* Database */
            DBAdapter db = new DBAdapter(getActivity());
            db.open();

            // Вземане на категории
            String fields[] = new String[] {
                    "_id",
                    "food_name",
                    "food_manufactor_name",
                    "food_description",
                    "food_serving_size_gram",
                    "food_serving_size_gram_mesurment",
                    "food_serving_size_pcs",
                    "food_serving_size_pcs_mesurment",
                    "food_energy_calculated"
            };
            listCursorFood = db.select("food", fields, "food_category_id", categoryId, "food_name", "ASC");


            // Намиране ListView за попълване
            ListView lvItems = (ListView)getActivity().findViewById(R.id.listViewFood);



            // Адаптер за настройка на курсора
            FoodCursorAdapter continentsAdapter = new FoodCursorAdapter(getActivity(), listCursorFood);

            // Прикрепете адаптера на курсора към ListView
            try{
                lvItems.setAdapter(continentsAdapter); // използва ContinensCursorAdapter
            }
            catch (Exception e){
                Toast.makeText(getActivity(), "E: " + e.toString(), Toast.LENGTH_LONG).show();
            }


            // OnClick
            lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    foodInCategoryListItemClicked(arg2);
                }
            });


            // затваряне на db
            db.close();

        } //categoryParentID.equals
    } // showFoodInCategory


    /*- Предварителната храна в елемента от списъка с категории когато е щракната*/
    // Идваме от друг клас
    public void preFoodInCategoryListItemClicked() {

            /* Database */
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        // Вземане на категории
        String fields[] = new String[] {
                "_id",
                "food_name",
                "food_manufactor_name",
                "food_description",
                "food_serving_size_gram",
                "food_serving_size_gram_mesurment",
                "food_serving_size_pcs",
                "food_serving_size_pcs_mesurment",
                "food_energy_calculated"
        };

        String currentFoodIdSQL = db.quoteSmart(currentFoodId);

        listCursorFood = db.select("food", fields, "_id", currentFoodIdSQL);

        int simulatedIndex = 0;
        foodInCategoryListItemClicked(simulatedIndex);

    } // preFoodInCategoryListItemClicked

    /* Храна в елемент от списък с категории когато е щракнат  */
    public void foodInCategoryListItemClicked(int listItemFoodIndexClicked){

        /* Промяна на layout */
        int id = R.layout.fragment_add_food_to_diary_view_food;
        setMainView(id);

        // Преместване на курсора до щракнатото ID
        listCursorFood.moveToPosition(listItemFoodIndexClicked);

        // Взимане на ID и име от курсора
        // Задаване на текущо име и id
        currentFoodId = listCursorFood.getString(0);
        currentFoodName = listCursorFood.getString(1);

        // Промяна на заглавието
        ((FragmentActivity)getActivity()).getSupportActionBar().setTitle("Add " + currentFoodName);


        /*  Вземане на данни от база данни */

        // база данни
        DBAdapter db = new DBAdapter(getActivity());
        db.open();


        String fields[] = new String[] {
                "_id",
                "food_name",
                "food_manufactor_name",
                "food_description",
                "food_serving_size_gram",
                "food_serving_size_gram_mesurment",
                "food_serving_size_pcs",
                "food_serving_size_pcs_mesurment",
                "food_energy",
                "food_proteins",
                "food_carbohydrates",
                "food_fat",
                "food_energy_calculated",
                "food_proteins_calculated",
                "food_carbohydrates_calculated",
                "food_fat_calculated",
                "food_user_id",
                "food_barcode",
                "food_category_id",
                "food_image_a",
                "food_image_b",
                "food_image_c"
        };
        String currentIdSQL = db.quoteSmart(currentFoodId);
        Cursor foodCursor = db.select("food", fields, "_id", currentIdSQL);

        // Convert cursor to strings
        String stringId = foodCursor.getString(0);
        String stringName = foodCursor.getString(1);
        String stringManufactorName = foodCursor.getString(2);
        String stringDescription = foodCursor.getString(3);
        String stringServingSize = foodCursor.getString(4);
        String stringServingMesurment = foodCursor.getString(5);
        String stringServingNameNumber = foodCursor.getString(6);
        String stringServingNameWord = foodCursor.getString(7);
        String stringEnergy = foodCursor.getString(8);
        String stringProteins = foodCursor.getString(9);
        String stringCarbohydrates = foodCursor.getString(10);
        String stringFat = foodCursor.getString(11);
        String stringEnergyCalculated = foodCursor.getString(12);
        String stringProteinsCalculated = foodCursor.getString(13);
        String stringCarbohydratesCalculated = foodCursor.getString(14);
        String stringFatCalculated = foodCursor.getString(15);
        String stringUserId = foodCursor.getString(16);
        String stringBarcode = foodCursor.getString(17);
        String stringCategoryId = foodCursor.getString(18);
        String stringImageA = foodCursor.getString(19);
        String stringImageB = foodCursor.getString(20);
        String stringImageC = foodCursor.getString(21);


        // Заглавие
        TextView textViewViewFoodName = (TextView) getView().findViewById(R.id.textViewViewFoodName);
        textViewViewFoodName.setText(stringName);

        // Подзаглавие
        TextView textViewViewFoodManufactorName = (TextView) getView().findViewById(R.id.textViewViewFoodManufactorName);
        textViewViewFoodManufactorName.setText(stringManufactorName);


        // Размер на порцията
        EditText editTextPortionSizePcs = (EditText)getActivity().findViewById(R.id.editTextPortionSizePcs);
        editTextPortionSizePcs.setText(stringServingNameNumber);

        TextView textViewPcs = (TextView)getActivity().findViewById(R.id.textViewPcs);
        textViewPcs.setText(stringServingNameWord);

        // Порция грам
        EditText editTextPortionSizeGram = (EditText)getActivity().findViewById(R.id.editTextPortionSizeGram);
        editTextPortionSizeGram.setText(stringServingSize);

        // Линия за изчисление
        TextView textViewViewFoodAbout = (TextView) getView().findViewById(R.id.textViewViewFoodAbout);
        String foodAbout = stringServingSize + " " + stringServingMesurment + " = " +
                stringServingNameNumber  + " " + stringServingNameWord + ".";
        textViewViewFoodAbout.setText(foodAbout);

        // Описание
        TextView textViewViewFoodDescription = (TextView) getView().findViewById(R.id.textViewViewFoodDescription);
        textViewViewFoodDescription.setText(stringDescription);

        // Таблица с калории
        TextView textViewViewFoodEnergyPerHundred = (TextView) getView().findViewById(R.id.textViewViewFoodEnergyPerHundred);
        TextView textViewViewFoodProteinsPerHundred = (TextView) getView().findViewById(R.id.textViewViewFoodProteinsPerHundred);
        TextView textViewViewFoodCarbsPerHundred = (TextView) getView().findViewById(R.id.textViewViewFoodCarbsPerHundred);
        TextView textViewViewFoodFatPerHundred = (TextView) getView().findViewById(R.id.textViewViewFoodFatPerHundred);

        TextView textViewViewFoodEnergyPerN = (TextView) getView().findViewById(R.id.textViewViewFoodEnergyPerN);
        TextView textViewViewFoodProteinsPerN = (TextView) getView().findViewById(R.id.textViewViewFoodProteinsPerN);
        TextView textViewViewFoodCarbsPerN = (TextView) getView().findViewById(R.id.textViewViewFoodCarbsPerN);
        TextView textViewViewFoodFatPerN = (TextView) getView().findViewById(R.id.textViewViewFoodFatPerN);

        textViewViewFoodEnergyPerHundred.setText(stringEnergy);
        textViewViewFoodProteinsPerHundred.setText(stringProteins);
        textViewViewFoodCarbsPerHundred.setText(stringCarbohydrates);
        textViewViewFoodFatPerHundred.setText(stringFat);

        textViewViewFoodEnergyPerN.setText(stringEnergyCalculated);
        textViewViewFoodProteinsPerN.setText(stringProteinsCalculated);
        textViewViewFoodCarbsPerN.setText(stringCarbohydratesCalculated);
        textViewViewFoodFatPerN.setText(stringFatCalculated);

        /* Приемател за editTextPortionSizePcs */
        editTextPortionSizePcs.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if(!(s.toString().equals(""))){
                    editTextPortionSizePcsOnChange();
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        editTextPortionSizePcs.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                }else {
                    String lock = "portionSizePcs";
                    releaseLock(lock);
                }
            }
        });

        editTextPortionSizeGram.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if(!(s.toString().equals(""))){
                    editTextPortionSizeGramOnChange();
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        editTextPortionSizeGram.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                }else {
                    String lock = "portionSizeGram";
                    releaseLock(lock);
                }
            }
        });



        /* Listner за editTextPortionSizeGram */
        /* Listner за добавяне */
        Button buttonSubmit = (Button)getActivity().findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFoodToDiary();
            }
        });

        /* Затваряне на db */
        db.close();




    } // foodInCategoryListItemClicked

    private void releaseLock(String lock){
        if(lock.equals("portionSizeGram")){
            lockPortionSizeByGram = false;
        }
        else {
            lockPortionSizeByPcs = false;
        }
    }

    /*- editTextPortionSizePcsOnChange  */
    public void editTextPortionSizePcsOnChange(){
        if(!(lockPortionSizeByGram)) {
            // Ключалка
            lockPortionSizeByPcs = true;

            // Вземете стойност на бр
            EditText editTextPortionSizePcs = (EditText) getActivity().findViewById(R.id.editTextPortionSizePcs);
            String stringPortionSizePcs = editTextPortionSizePcs.getText().toString();

            double doublePortionSizePcs = 0;

            if (stringPortionSizePcs.equals("")) {
                doublePortionSizePcs = 0;
            } else {
                try {
                    doublePortionSizePcs = Double.parseDouble(stringPortionSizePcs);
                } catch (NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }
            }


            // База данни
            DBAdapter db = new DBAdapter(getActivity());
            db.open();

            String fields[] = new String[]{
                    "food_serving_size_gram"
            };
            String currentIdSQL = db.quoteSmart(currentFoodId);
            Cursor foodCursor = db.select("food", fields, "_id", currentIdSQL);

            // Преобразуване на курсора в стрингове
            String stringServingSize = foodCursor.getString(0);
            db.close();


            // Преобразуване на курсора в double
            double doubleServingSize = 0;
            try {
                doubleServingSize = Double.parseDouble(stringServingSize);
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

            // Изчисляване колко е n размерът на порциите в грамове.
            // Смеване наме бр
            //Актуализиране на грам
            double doublePortionSizeGram = Math.round(doublePortionSizePcs * doubleServingSize);

            // Актуализиране размера на порцията в грам
            EditText editTextPortionSizeGram = (EditText) getActivity().findViewById(R.id.editTextPortionSizeGram);
            editTextPortionSizeGram.setText("" + doublePortionSizeGram);
        }
    } // editTextPortionSizePcs

    /*- editTextPortionSizeGramOnChange */
    public void editTextPortionSizeGramOnChange(){
        if(!(lockPortionSizeByPcs)) {

            // Ключалка
            lockPortionSizeByGram = true;

            // Вземане стойността на грам
            EditText editTextPortionSizeGram = (EditText) getActivity().findViewById(R.id.editTextPortionSizeGram);
            String stringPortionSizeGram = editTextPortionSizeGram.getText().toString();
            double doublePortionSizeGram = 0;
            try {
                doublePortionSizeGram = Double.parseDouble(stringPortionSizeGram);
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

            // база от данни
            DBAdapter db = new DBAdapter(getActivity());
            db.open();

            String fields[] = new String[]{
                    "food_serving_size_gram"
            };
            String currentIdSQL = db.quoteSmart(currentFoodId);
            Cursor foodCursor = db.select("food", fields, "_id", currentIdSQL);

            // Преобразуване на курсора в strings
            String stringServingSizeGram = foodCursor.getString(0);
            db.close();


            // Преобразуване на курсора в double
            double doubleServingSizeGram = 0;
            try {
                doubleServingSizeGram = Double.parseDouble(stringServingSizeGram);
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }


            // Изчисляване на бр
            double doublePortionSizePcs = Math.round(doublePortionSizeGram / doubleServingSizeGram);


            // Актуализиране
            // Взимане на стойност на бр
            EditText editTextPortionSizePcs = (EditText) getActivity().findViewById(R.id.editTextPortionSizePcs);
            editTextPortionSizePcs.setText("" + doublePortionSizePcs);
        }

    } // editTextPortionSizeGramOnChange

    /* Добавете храна към дневника */
    public void addFoodToDiary(){
        // Искаме да добавим храна
        // Грешка
        int error = 0;

        // База данни
        DBAdapter db = new DBAdapter(getActivity());
        db.open();


        String fields[] = new String[] {
                "_id",
                "food_name",
                "food_manufactor_name",
                "food_description",
                "food_serving_size_gram",
                "food_serving_size_gram_mesurment",
                "food_serving_size_pcs",
                "food_serving_size_pcs_mesurment",
                "food_energy",
                "food_proteins",
                "food_carbohydrates",
                "food_fat",
                "food_energy_calculated",
                "food_proteins_calculated",
                "food_carbohydrates_calculated",
                "food_fat_calculated",
                "food_user_id",
                "food_barcode",
                "food_category_id",
                "food_image_a",
                "food_image_b",
                "food_image_c"
        };
        String currentIdSQL = db.quoteSmart(currentFoodId);
        Cursor foodCursor = db.select("food", fields, "_id", currentIdSQL);

        // Преобразуване на курсора в низове
        String stringId = foodCursor.getString(0);
        String stringName = foodCursor.getString(1);
        String stringManufactorName = foodCursor.getString(2);
        String stringDescription = foodCursor.getString(3);
        String stringServingSizeGram = foodCursor.getString(4);
        String stringServingSizeGramMesurment = foodCursor.getString(5);
        String stringServingSizePcs = foodCursor.getString(6);
        String stringServingSizePcsMesurment = foodCursor.getString(7);
        String stringEnergy = foodCursor.getString(8);
        String stringProteins = foodCursor.getString(9);
        String stringCarbohydrates = foodCursor.getString(10);
        String stringFat = foodCursor.getString(11);
        String stringEnergyCalculated = foodCursor.getString(12);
        String stringProteinsCalculated = foodCursor.getString(13);
        String stringCarbohydratesCalculated = foodCursor.getString(14);
        String stringFatCalculated = foodCursor.getString(15);
        String stringUserId = foodCursor.getString(16);
        String stringBarcode = foodCursor.getString(17);
        String stringCategoryId = foodCursor.getString(18);
        String stringImageA = foodCursor.getString(19);
        String stringImageB = foodCursor.getString(20);
        String stringImageC = foodCursor.getString(21);


        // Взимане на грам
        EditText editTextPortionSizeGram = (EditText)getActivity().findViewById(R.id.editTextPortionSizeGram);
        String fdServingSizeGram = editTextPortionSizeGram.getText().toString();
        String fdServingSizeGramSQL = db.quoteSmart(fdServingSizeGram);
        double doublePortionSizeGram = 0;
        try{
            doublePortionSizeGram = Double.parseDouble(fdServingSizeGram);
        }
        catch (NumberFormatException nfe){
            error = 1;
            Toast.makeText(getActivity(), "Please fill inn a number in gram", Toast.LENGTH_SHORT).show();
        }
        if(fdServingSizeGram.equals("")){
            error = 1;
            Toast.makeText(getActivity(), "Gram cannot be empty", Toast.LENGTH_SHORT).show();
        }



        // Дата
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // месец
        month = month+1; // Месецът започва с 0
        String stringMonth = "";
        if(month < 10){
            stringMonth = "0" + month;
        }
        else{
            stringMonth = "" + month;
        }
        // ден
        String stringDay = "";
        if(day < 10){
            stringDay = "0" + day;
        }
        else{
            stringDay = "" + day;
        }


        String stringFdDate = year + "-" + stringMonth + "-" + stringDay;
        String stringFdDateSQL = db.quoteSmart(stringFdDate);

        // Номер на ястие
        String stringFdMealNumber = currentMealNumber;
        String stringFdMealNumberSQL = db.quoteSmart(stringFdMealNumber);

        // ID на храната
        String stringFdFoodId = currentFoodId;
        String StringFdFoodIdSQL = db.quoteSmart(stringFdFoodId);

        // Порция
        String fdServingSizeGramMesurmentSQL = db.quoteSmart(stringServingSizeGramMesurment);

        // Порция бр
        double doubleServingSizeGram = 0;
        try {
            doubleServingSizeGram = Double.parseDouble(stringServingSizeGram);
        } catch (NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }
        double doublePortionSizePcs = Math.round(doublePortionSizeGram / doubleServingSizeGram);
        String stringFdServingSizePcs = "" + doublePortionSizePcs;
        String stringFdServingSizePcsSQL = db.quoteSmart(stringFdServingSizePcs);

        // Измерване на размера на порцията бр
        String stringFdServingSizePcsMesurmentSQL = db.quoteSmart(stringServingSizePcsMesurment);

        // Изчислена енергия
        // пример:
        //          energy
        // pr 100   152 kcal
        //
        // I eat 21 g
        //
        // energy = myConsumotion*kcal/100
        double doubleEnergyPerHundred = Double.parseDouble(stringEnergy);

        double doubleFdEnergyCalculated = Math.round((doublePortionSizeGram*doubleEnergyPerHundred)/100);
        String stringFdEnergyCalcualted = "" + doubleFdEnergyCalculated;
        String stringFdEnergyCalcualtedSQL = db.quoteSmart(stringFdEnergyCalcualted);

        // Изчислени протеини
        double doubleProteinsPerHundred = Double.parseDouble(stringProteins);

        double doubleFdProteinsCalculated = Math.round((doublePortionSizeGram*doubleProteinsPerHundred)/100);
        String stringFdProteinsCalcualted = "" + doubleFdProteinsCalculated;
        String stringFdProteinsCalcualtedSQL = db.quoteSmart(stringFdProteinsCalcualted);


        // Изчислени въглехидрати
        double doubleCarbohydratesPerHundred = Double.parseDouble(stringCarbohydrates);

        double doubleFdCarbohydratesCalculated = Math.round((doublePortionSizeGram*doubleCarbohydratesPerHundred)/100);
        String stringFdCarbohydratesCalcualted = "" + doubleFdCarbohydratesCalculated;
        String stringFdCarbohydratesCalcualtedSQL = db.quoteSmart(stringFdCarbohydratesCalcualted);

        // Изчислени мазнини
        double doubleFatPerHundred = Double.parseDouble(stringFat);

        double doubleFdFatCalculated = Math.round((doublePortionSizeGram*doubleFatPerHundred)/100);
        String stringFdFatCalcualted = "" + doubleFdFatCalculated;
        String stringFdFatCalcualtedSQL = db.quoteSmart(stringFdFatCalcualted);

        // Вмъкване в SQL
        if(error == 0){
            String inpFields = "_id, fd_date, fd_meal_number, fd_food_id," +
                    "fd_serving_size_gram, fd_serving_size_gram_mesurment," +
                    " fd_serving_size_pcs, fd_serving_size_pcs_mesurment," +
                    " fd_energy_calculated, fd_protein_calculated," +
                    " fd_carbohydrates_calculated, fd_fat_calculated";

            String inpValues = "NULL, " + stringFdDateSQL + ", " + stringFdMealNumberSQL + ", " + StringFdFoodIdSQL + ", " +
                    fdServingSizeGramSQL + ", " + fdServingSizeGramMesurmentSQL + ", " +
                    stringFdServingSizePcsSQL + ", " + stringFdServingSizePcsMesurmentSQL + ", " +
                    stringFdEnergyCalcualtedSQL + ", " + stringFdProteinsCalcualtedSQL + ", " +
                    stringFdCarbohydratesCalcualtedSQL + ", " + stringFdFatCalcualtedSQL;

            db.insert("food_diary", inpFields, inpValues);

            Toast.makeText(getActivity(), "Food diary updated", Toast.LENGTH_SHORT).show();


            // Променете фрагмента на HomeFragment
            Fragment fragment = null;
            Class fragmentClass = null;
            fragmentClass = HomeFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        }



        // затваряне db
        db.close();
    } // addFoodToDiary



    /*- Фрагментни методи -*/


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // TODO: преименувай Метода, актуализирайте аргумента и метода на куката в UI евента
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Този интерфейс трябва да бъде реализиран от дейности,
     * които съдържат този фрагмент, за да позволи взаимодействието в
     * този фрагмент да бъде съобщено на дейността и потенциално други фрагменти,
     * съдържащи се в тази дейност.
     * <p>
     * Виж Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> за повече информация.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Актуализирайте типа на аргумента и името
        void onFragmentInteraction(Uri uri);
    }
}
