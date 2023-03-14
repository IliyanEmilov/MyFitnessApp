package com.example.myfitnessapp;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Прост подклас {@link Fragment}.
 * Дейностите, които съдържат този фрагмент, трябва да изпълняват
 * Интерфейс {@link OnFragmentInteractionListener}
 * за обработка на събития на взаимодействие (interaction events).
 * Използва се метод {@link FoodFragment#newInstance} за
 * създаване на екземпляр на този фрагмент.
 */
public class FoodFragment extends Fragment {

    /*- 01 Class променливи  */
    private View mainView;
    private Cursor listCursor;

    // Бутони за действие на лентата с инструменти
    private MenuItem menuItemEdit;
    private MenuItem menuItemDelete;

    // Holder
    private String currentId = "";
    private String currentName;




    /*- 02 Фрагментни променливи  */
    // Необходим за изпълнение на фрагмента
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /*- 03 Конструктор */
    // Необходим за наличието на Fragment като клас
    public FoodFragment() {
        // Необходим празен публичен конструктор
    }


    /*- 04 Създаване на фрагмент */
    public static FoodFragment newInstance(String param1, String param2) {
        FoodFragment fragment = new FoodFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    /*- 05 на Създадена дейност */
    // Изпълняване на методи при стартиране
    // Задаване на елементи от менюто на лентата с инструменти
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* Задаване на заглавие */
        ((FragmentActivity)getActivity()).getSupportActionBar().setTitle("Food");


        // Създаване на меню
        setHasOptionsMenu(true);

        /* Вземане на данни от фрагмент */
        Bundle bundle = this.getArguments();
        if(bundle != null){
            currentId = bundle.getString("currentFoodId");

            // Трябва да стартирате, за да получите бутони
            // за редактиране и изтриване: onCreateOptionsMenu();
        }
        if(currentId.equals("")) {
            // Попълване на списъка с категории
            populateListFood();
        }
        else{
            preListItemClickedReadyCursor(); //Идваме от друг клас с currentFoodId
        }
    } // onActivityCreated


    /*- 06 При създаване на изглед (view) */
    // Задава главната променлива на View към изгледа,
    // така че можем да променяме изгледите във фрагмент
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_food, container, false);
        return mainView;
    }

    /*- 07 задаване на основен изглед  */
    // Промяна на метода на изглед във фрагмент
    private void setMainView(int id){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(id, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(mainView);
    }

    /*- 08 в менюто за създаване на опции */
    // Създаване на икона за действие в лентата с инструменти
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Inflate menu
        ((FragmentActivity)getActivity()).getMenuInflater().inflate(R.menu.menu_food, menu);

        // Присвояване на елементи от менюто към променливи
        menuItemEdit = menu.findItem(R.id.menu_action_food_edit);
        menuItemDelete = menu.findItem(R.id.menu_action_food_delete);

        // Скриване по подразбиране
        menuItemEdit.setVisible(false);
        menuItemDelete.setVisible(false);
    }

    /*- 09 на Опции Избран елемент- */
    // Щракната иконата за действие
    // Меню
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        int id = menuItem.getItemId();
        if (id == R.id.menu_action_food_add) {
            addFood();
        }
        if (id == R.id.menu_action_food_edit) {
            editFood();
        }
        if (id == R.id.menu_action_food_delete) {
            deleteFood();
        }
        return super.onOptionsItemSelected(menuItem);
    }


    /*- попълване на списък */
    public void populateListFood(){

        /* База данни */
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
        try{
            listCursor = db.select("food", fields, "", "", "food_name", "ASC");
        }
        catch (SQLException sqle){
            Toast.makeText(getActivity(), sqle.toString(), Toast.LENGTH_LONG).show();
        }


        // Намеране на ListView за попълване
        ListView lvItems = (ListView)getActivity().findViewById(R.id.listViewFood);


        // Настройване на адаптера на курсора,
        // като използвате курсора от последната стъпка
        FoodCursorAdapter continentsAdapter = new FoodCursorAdapter(getActivity(), listCursor);

        // Прикрепване на адаптера на курсора към ListView
        try{
            lvItems.setAdapter(continentsAdapter); // използва ContinensCursorAdapter
        }
        catch (Exception e){
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
        }


        // OnClick
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                listItemClicked(arg2);
            }
        });


        // затваряне на db
        db.close();

    }

    /*- преди щракване върху елемент от списъка Готов курсор  */
    // Идваме в друг клас и се нуждаем от курсора
    public void preListItemClickedReadyCursor(){

        /* База данни */
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

        String currentIdSQL = db.quoteSmart(currentId);

        try{
            listCursor = db.select("food", fields, "_id", currentIdSQL, "food_name", "ASC");
        }
        catch (SQLException sqle){
            Toast.makeText(getActivity(), sqle.toString(), Toast.LENGTH_LONG).show();
        }

        int simulateIndex = 0;
        listItemClicked(simulateIndex);

        // затваряне на db
        db.close();
    }

    /*- Щракнат елемент от списъка */
    public void listItemClicked(int listItemIDClicked) {

        /* промяна на layout */
        int id = R.layout.fragment_food_view;
        setMainView(id);

        // Показване на бутона за редактиране
        try {
            menuItemEdit.setVisible(true);
            menuItemDelete.setVisible(true);
        }
        catch (Exception e){

        }
        // Преместване на курсора до щракване ID
        listCursor.moveToPosition(listItemIDClicked);

        // Вземане на ID и име от курсора
        // Задаване на текущо име и идентификатор
        currentId = listCursor.getString(0);
        currentName = listCursor.getString(1);

        // Change title
        ((FragmentActivity)getActivity()).getSupportActionBar().setTitle(currentName);


        /* Вземане на данни от база данни */

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
        String currentIdSQL = db.quoteSmart(currentId);
        Cursor foodCursor = db.select("food", fields, "_id", currentIdSQL);

        // Преобразуване на курсора в стрингове
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


        // затваряне на db
        db.close();

        // Listener за добавяне на храна към дневника
        ImageView imageViewAddToDiary = (ImageView)getActivity().findViewById(R.id.imageViewAddToDiary);
        imageViewAddToDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFoodToDiarySelectMealNumber();
            }
        });
    }


    /*- Редактиране на храната */
    String selectedMainCategoryName = "";
    public void editFood(){

        /* провяна на layout */
        int id = R.layout.fragment_food_edit;
        setMainView(id);


        // Вземане на ID и име от курсора
        // Задаване на текущо име и идентификатор
        currentId = listCursor.getString(0);
        currentName = listCursor.getString(1);

        // Промяна на заглавието
        ((FragmentActivity)getActivity()).getSupportActionBar().setTitle("Edit " + currentName);


        /* Вземане на данни от база данни */

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
        String currentIdSQL = db.quoteSmart(currentId);
        Cursor foodCursor = db.select("food", fields, "_id", currentIdSQL);

        // Преобразуване на курсора в стривгове
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


        /* General */

        // име
        EditText editTextEditFoodName = (EditText) getView().findViewById(R.id.editTextEditFoodName);
        editTextEditFoodName.setText(stringName);

        // производител
        TextView editTextEditFoodManufactor = (TextView) getView().findViewById(R.id.editTextEditFoodManufactor);
        editTextEditFoodManufactor.setText(stringManufactorName);

        // Описание
        EditText editTextEditFoodDescription = (EditText) getView().findViewById(R.id.editTextEditFoodDescription);
        editTextEditFoodDescription.setText(stringDescription);

        // Баркод
        EditText editTextEditFoodBarcode = (EditText) getView().findViewById(R.id.editTextEditFoodBarcode);
        editTextEditFoodBarcode.setText(stringBarcode);

        /* В коя категория е храната и нейният родител */
        String spinnerFields[] = new String[] {
                "_id",
                "category_name",
                "category_parent_id"
        };
        // Намеране на категорията, която използва храната (трябва да е подкатегория)
        Cursor dbCursorCurrentFoodCategory = db.select("categories", spinnerFields, "_id", stringCategoryId, "category_name", "ASC");

        String currentFoodCategoryID = dbCursorCurrentFoodCategory.getString(2);


        /* Sub категории */
        Cursor dbCursorSub = db.select("categories", spinnerFields, "category_parent_id", currentFoodCategoryID, "category_name", "ASC");

        // Създаване на масив
        int dbCursorCount = dbCursorSub.getCount();
        String[] arraySpinnerCategoriesSub = new String[dbCursorCount];

        // разбиране на коя подкатегория е избрана
        int selectedSubCategoryIndex = 0;
        String selectedSubCategoryParentId = "0";

        // Преобразуване на курсора в стринг
        for(int x=0;x<dbCursorCount;x++){
            arraySpinnerCategoriesSub[x] = dbCursorSub.getString(1).toString();

            if(dbCursorSub.getString(0).toString().equals(stringCategoryId)){
                selectedSubCategoryIndex = x;
                selectedSubCategoryParentId = dbCursorSub.getString(2).toString();
            }

            dbCursorSub.moveToNext();
        }

        // Попълване на спинера
        Spinner spinnerSubCat = (Spinner) getActivity().findViewById(R.id.spinnerEditFoodCategorySub);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinnerCategoriesSub);
        spinnerSubCat.setAdapter(adapter);

        // Избиране на sub индекс
        spinnerSubCat.setSelection(selectedSubCategoryIndex);

        /* Основна категория */
        Cursor dbCursorMain = db.select("categories", spinnerFields, "category_parent_id", "0", "category_name", "ASC");

        // Създаване на масив
        dbCursorCount = dbCursorMain.getCount();
        String[] arraySpinnerMainCategories = new String[dbCursorCount];

        // Избиране на правилната майн категория
        int selectedMainCategoryIndex = 0;

        // Преобразуване на курсора в стринг
        for(int x=0;x<dbCursorCount;x++){
            arraySpinnerMainCategories[x] = dbCursorMain.getString(1).toString();


            if(dbCursorMain.getString(0).toString().equals(selectedSubCategoryParentId)){
                selectedMainCategoryIndex = x;
                selectedMainCategoryName = dbCursorMain.getString(1).toString();
            }
            dbCursorMain.moveToNext();
        }

        // Попълване на спинера
        Spinner spinnerCatMain = (Spinner) getActivity().findViewById(R.id.spinnerEditFoodCategoryMain);
        ArrayAdapter<String> adapterMain = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinnerMainCategories);
        spinnerCatMain.setAdapter(adapterMain);

        // Избиране на sub индекс
        spinnerCatMain.setSelection(selectedMainCategoryIndex);


        /* Serving Table */

        // Размер
        EditText editTextEditFoodSize = (EditText) getView().findViewById(R.id.editTextEditFoodSize);
        editTextEditFoodSize.setText(stringServingSize);

        // Измерване
        EditText editTextEditFoodMesurment = (EditText) getView().findViewById(R.id.editTextEditFoodMesurment);
        editTextEditFoodMesurment.setText(stringServingMesurment);

        // Номер
        EditText editTextEditFoodNumber = (EditText) getView().findViewById(R.id.editTextEditFoodNumber);
        editTextEditFoodNumber.setText(stringServingNameNumber);

        // Слово
        EditText editTextEditFoodWord = (EditText) getView().findViewById(R.id.editTextEditFoodWord);
        editTextEditFoodWord.setText(stringServingNameWord);

        /* Калории table */

        // Енергия
        EditText editTextEditFoodEnergyPerHundred = (EditText) getView().findViewById(R.id.editTextEditFoodEnergyPerHundred);
        editTextEditFoodEnergyPerHundred.setText(stringEnergy);

        // протеини
        EditText editTextEditFoodProteinsPerHundred = (EditText) getView().findViewById(R.id.editTextEditFoodProteinsPerHundred);
        editTextEditFoodProteinsPerHundred.setText(stringProteins);

        // Енергия
        EditText editTextEditFoodCarbsPerHundred = (EditText) getView().findViewById(R.id.editTextEditFoodCarbsPerHundred);
        editTextEditFoodCarbsPerHundred.setText(stringCarbohydrates);

        // Енергия
        EditText editTextEditFoodFatPerHundred = (EditText) getView().findViewById(R.id.editTextEditFoodFatPerHundred);
        editTextEditFoodFatPerHundred.setText(stringFat);

        /* майн категория listener */
        spinnerCatMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString(); //това е вашият избран артикул
                editFoodMainCategoryChanged(selectedItem);
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        /* SubmitButton listener */
        Button buttonEditFood = (Button)getActivity().findViewById(R.id.buttonEditFood);
        buttonEditFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonEditFoodSubmitOnClick();
            }
        });


        /* затваряне на db */
        db.close();


    } // editFood
    public void editFoodMainCategoryChanged(String selectedItemCategoryName){
        if(!(selectedItemCategoryName.equals(selectedMainCategoryName))){

            /* db */
            DBAdapter db = new DBAdapter(getActivity());
            db.open();

            // Намиране на ID на основната(main) категория
            String selectedItemCategoryNameSQL = db.quoteSmart(selectedItemCategoryName);
            String spinnerFields[] = new String[] {
                    "_id",
                    "category_name",
                    "category_parent_id"
            };
            Cursor findMainCategoryID = db.select("categories", spinnerFields, "category_name", selectedItemCategoryNameSQL);
            String stringMainCategoryID  = findMainCategoryID.getString(0).toString();
            String stringMainCategoryIDSQL = db.quoteSmart(stringMainCategoryID);


            /* Sub категории */
            Cursor dbCursorSub = db.select("categories", spinnerFields, "category_parent_id", stringMainCategoryIDSQL, "category_name", "ASC");

            // Създаване на масив
            int dbCursorCount = dbCursorSub.getCount();
            String[] arraySpinnerCategoriesSub = new String[dbCursorCount];


            // Преобразуване на курсора в стринг
            for(int x=0;x<dbCursorCount;x++){
                arraySpinnerCategoriesSub[x] = dbCursorSub.getString(1).toString();
                dbCursorSub.moveToNext();
            }

            // Попълване на спинера
            Spinner spinnerSubCat = (Spinner) getActivity().findViewById(R.id.spinnerEditFoodCategorySub);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, arraySpinnerCategoriesSub);
            spinnerSubCat.setAdapter(adapter);



            db.close();
        }
    }

    /*- Редактиране подаването на храна при щракване */
    private void buttonEditFoodSubmitOnClick(){
        /* db */
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        // Грешка?
        int error = 0;

        // DB fields
        long rowID = Long.parseLong(currentId);


        /* General */

        // Име
        EditText editTextEditFoodName = (EditText)getActivity().findViewById(R.id.editTextEditFoodName);
        String stringName = editTextEditFoodName.getText().toString();
        String stringNameSQL = db.quoteSmart(stringName);
        if(stringName.equals("")){
            Toast.makeText(getActivity(), "Please fill in a name.", Toast.LENGTH_SHORT).show();
            error = 1;
        }

        // производител
        EditText editTextEditFoodManufactor = (EditText)getActivity().findViewById(R.id.editTextEditFoodManufactor);
        String stringManufactor = editTextEditFoodManufactor.getText().toString();
        String stringManufactorSQL = db.quoteSmart(stringManufactor);
        if(stringManufactor.equals("")){
            Toast.makeText(getActivity(), "Please fill in a manufactor.", Toast.LENGTH_SHORT).show();
            error = 1;
        }

        // Описание
        EditText editTextEditFoodDescription = (EditText)getActivity().findViewById(R.id.editTextEditFoodDescription);
        String stringDescription = editTextEditFoodDescription.getText().toString();
        String stringDescriptionSQL = db.quoteSmart(stringDescription);

        // Баркод
        EditText editTextEditFoodBarcode = (EditText)getActivity().findViewById(R.id.editTextEditFoodBarcode);
        String stringBarcode = editTextEditFoodBarcode.getText().toString();
        String stringBarcodeSQL = db.quoteSmart(stringBarcode);

        /* Категория */
        // Sub Категория
        Spinner spinnerSubCat = (Spinner)getActivity().findViewById(R.id.spinnerEditFoodCategorySub);
        int intSubCategoryIndex = spinnerSubCat.getSelectedItemPosition();
        String stringSpinnerSubCategoryName = spinnerSubCat.getSelectedItem().toString();

        // Намеране на ID на родител от текста
        String stringSpinnerSubCategoryNameSQL = db.quoteSmart(stringSpinnerSubCategoryName);
        String spinnerFields[] = new String[] {
                    "_id",
                    "category_name",
                    "category_parent_id"
            };
        Cursor findstringSpinnerSubCategoryID = db.select("categories", spinnerFields, "category_name", stringSpinnerSubCategoryNameSQL);
        String stringSubCategoryID  = findstringSpinnerSubCategoryID.getString(0).toString();
        String stringSubCategoryIDSQL = db.quoteSmart(stringSubCategoryID);


        /* Serving Table */

        // Размер
        EditText editTextEditFoodSize = (EditText)getActivity().findViewById(R.id.editTextEditFoodSize);
        String stringSize = editTextEditFoodSize.getText().toString();
        String stringSizeSQL = db.quoteSmart(stringSize);
        double doubleServingSize = 0;
        if(stringSize.equals("")){
            Toast.makeText(getActivity(), "Please fill in a size.", Toast.LENGTH_SHORT).show();
            error = 1;
        }
        else{
            try {
                doubleServingSize = Double.parseDouble(stringSize);
            }
            catch(NumberFormatException nfe) {
                Toast.makeText(getActivity(), "Serving size is not number.", Toast.LENGTH_SHORT).show();
                error = 1;
            }
        }

        // Измерване
        EditText editTextEditFoodMesurment = (EditText)getActivity().findViewById(R.id.editTextEditFoodMesurment);
        String stringMesurment = editTextEditFoodMesurment.getText().toString();
        String stringMesurmentSQL = db.quoteSmart(stringMesurment);
        if(stringMesurment.equals("")){
            Toast.makeText(getActivity(), "Please fill in mesurment.", Toast.LENGTH_SHORT).show();
            error = 1;
        }

        // Номер
        EditText editTextEditFoodNumber = (EditText)getActivity().findViewById(R.id.editTextEditFoodNumber);
        String stringNumber = editTextEditFoodNumber.getText().toString();
        String stringNumberSQL = db.quoteSmart(stringNumber);
        if(stringNumber.equals("")){
            Toast.makeText(getActivity(), "Please fill in number.", Toast.LENGTH_SHORT).show();
            error = 1;
        }

        // дума
        EditText editTextEditFoodWord = (EditText)getActivity().findViewById(R.id.editTextEditFoodWord);
        String stringWord = editTextEditFoodWord.getText().toString();
        String stringWordSQL = db.quoteSmart(stringWord);
        if(stringWord.equals("")){
            Toast.makeText(getActivity(), "Please fill in word.", Toast.LENGTH_SHORT).show();
            error = 1;
        }


        /* Calories table */
        // Енергия
        EditText editTextEditFoodEnergyPerHundred = (EditText)getActivity().findViewById(R.id.editTextEditFoodEnergyPerHundred);
        String stringEnergyPerHundred = editTextEditFoodEnergyPerHundred.getText().toString();
        stringEnergyPerHundred = stringEnergyPerHundred.replace(",", ".");
        double doubleEnergyPerHundred = 0;
        if(stringEnergyPerHundred.equals("")){
            Toast.makeText(getActivity(), "Please fill in energy.", Toast.LENGTH_SHORT).show();
            error = 1;
        }
        else{
            try {
                doubleEnergyPerHundred = Double.parseDouble(stringEnergyPerHundred);
            }
            catch(NumberFormatException nfe) {
                Toast.makeText(getActivity(), "Energy is not a number.", Toast.LENGTH_SHORT).show();
                error = 1;
            }
        }
        String stringEnergyPerHundredSQL = db.quoteSmart(stringEnergyPerHundred);

        // Протеини
        EditText editTextEditFoodProteinsPerHundred = (EditText)getActivity().findViewById(R.id.editTextEditFoodProteinsPerHundred);
        String stringProteinsPerHundred = editTextEditFoodProteinsPerHundred.getText().toString();
        stringProteinsPerHundred = stringProteinsPerHundred.replace(",", ".");
        double doubleProteinsPerHundred = 0;
        if(stringProteinsPerHundred.equals("")){
            Toast.makeText(getActivity(), "Please fill in proteins.", Toast.LENGTH_SHORT).show();
            error = 1;
        }
        else{
            try {
                doubleProteinsPerHundred = Double.parseDouble(stringProteinsPerHundred);
            }
            catch(NumberFormatException nfe) {
                Toast.makeText(getActivity(), "Protein is not a number.\n" + "You wrote: " + stringProteinsPerHundred, Toast.LENGTH_SHORT).show();
                error = 1;
            }
        }
        String stringProteinsPerHundredSQL = db.quoteSmart(stringProteinsPerHundred);

        // Въглехидрати
        EditText editTextEditFoodCarbsPerHundred = (EditText)getActivity().findViewById(R.id.editTextEditFoodCarbsPerHundred);
        String stringCarbsPerHundred = editTextEditFoodCarbsPerHundred.getText().toString();
        stringCarbsPerHundred = stringCarbsPerHundred.replace(",", ".");
        double doubleCarbsPerHundred = 0;
        if(stringCarbsPerHundred.equals("")){
            Toast.makeText(getActivity(), "Please fill in carbs.", Toast.LENGTH_SHORT).show();
            error = 1;
        }
        else{
            try {
                doubleCarbsPerHundred = Double.parseDouble(stringCarbsPerHundred);
            }
            catch(NumberFormatException nfe) {
                Toast.makeText(getActivity(), "Carbs is not a number.\nYou wrote: " + stringCarbsPerHundred, Toast.LENGTH_SHORT).show();
                error = 1;
            }
        }
        String stringCarbsPerHundredSQL = db.quoteSmart(stringCarbsPerHundred);

        // мазнини
        EditText editTextEditFoodFatPerHundred = (EditText)getActivity().findViewById(R.id.editTextEditFoodFatPerHundred);
        String stringFatPerHundred = editTextEditFoodFatPerHundred.getText().toString();
        stringFatPerHundred = stringFatPerHundred.replace(",", ".");
        double doubleFatPerHundred = 0;
        if(stringFatPerHundred.equals("")){
            Toast.makeText(getActivity(), "Please fill in fat.", Toast.LENGTH_SHORT).show();
            error = 1;
        }
        else{
            try {
                doubleFatPerHundred = Double.parseDouble(stringFatPerHundred);
            }
            catch(NumberFormatException nfe) {
                Toast.makeText(getActivity(), "Carbs is not a number.", Toast.LENGTH_SHORT).show();
                error = 1;
            }
        }
        String stringFatPerHundredSQL = db.quoteSmart(stringFatPerHundred);


        /* Update */
        if(error == 0){

            /* Таблица с калории pеr meal */
            double energyCalculated = Math.round((doubleEnergyPerHundred*doubleServingSize)/100);
            double proteinsCalculated = Math.round((doubleProteinsPerHundred*doubleServingSize)/100);
            double carbsCalculated = Math.round((doubleCarbsPerHundred*doubleServingSize)/100);
            double fatCalculated = Math.round((doubleFatPerHundred*doubleServingSize)/100);

            String stringEnergyCalculated = "" + energyCalculated;
            String stringProteinsCalculated = "" + proteinsCalculated;
            String stringCarbsCalculated = "" + carbsCalculated;
            String stringfatCalculated = "" + fatCalculated;

            String stringEnergyCalculatedSQL = db.quoteSmart(stringEnergyCalculated);
            String stringProteinsCalculatedSQL = db.quoteSmart(stringProteinsCalculated);
            String stringCarbsCalculatedSQL = db.quoteSmart(stringCarbsCalculated);
            String stringfatCalculatedSQL = db.quoteSmart(stringfatCalculated);


            String fields[] = new String[] {
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
                    "food_barcode",
                    "food_category_id"
            };
            String values[] = new String[] {
                    stringNameSQL,
                    stringManufactorSQL,
                    stringDescriptionSQL,
                    stringSizeSQL,
                    stringMesurmentSQL,
                    stringNumberSQL,
                    stringWordSQL,
                    stringEnergyPerHundredSQL,
                    stringProteinsPerHundredSQL,
                    stringCarbsPerHundredSQL,
                    stringFatPerHundredSQL,
                    stringEnergyCalculatedSQL,
                    stringProteinsCalculatedSQL,
                    stringCarbsCalculatedSQL,
                    stringfatCalculatedSQL,
                    stringBarcodeSQL,
                    stringSubCategoryIDSQL
            };

            long longCurrentID = Long.parseLong(currentId);

            db.update("food", "_id", longCurrentID, fields, values);
            //принт
            Toast.makeText(getActivity(), "Changes saved", Toast.LENGTH_SHORT).show();

        } // error == 0

        db.close();
    } // buttonEditFoodSubmitOnClick



    /*- Изтриване на храна  */
    public void deleteFood(){

        /* промяна на layout */
        int id = R.layout.fragment_food_delete;
        setMainView(id);


        /* buttonCategoriesCancel listener */
        Button buttonCancel = (Button)getActivity().findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFoodCancel();
            }
        });


        /* buttonCategoriesConfirmDelete listener */
        Button buttonConfirmDelete = (Button)getActivity().findViewById(R.id.buttonConfirmDelete);
        buttonConfirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFoodConfirmDelete();
            }
        });

    } // deleteFood

    /*- Изтриване на анулиране на храна */
    public void deleteFoodCancel(){
        // Преместване на потребителя обратно в правилния дизайн
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new FoodFragment(), FoodFragment.class.getName()).commit();


    }

    /*- Изтриване на храната - Потвърдете изтриването */
    public void deleteFoodConfirmDelete(){

        /* db */
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        // Текущ ID to long
        long longCurrentID = Long.parseLong(currentId);

        // Готови променливи
        long currentIDSQL = db.quoteSmart(longCurrentID);

        // Изтрий
        db.delete("food", "_id", currentIDSQL);

        // затвори db
        db.close();

        // Даване на съобщение
        Toast.makeText(getActivity(), "Food deleted", Toast.LENGTH_LONG).show();

        // Преместване на потребителя обратно в правилния дизайн
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new FoodFragment(), FoodFragment.class.getName()).commit();


    }



    /*- Добавяне на храна */
    public void addFood(){
        /* Db */
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        /* промяна на layout */
        int id = R.layout.fragment_food_edit;
        setMainView(id);

        // Промяна на заглавието
        ((FragmentActivity)getActivity()).getSupportActionBar().setTitle("Add food");


        /* Main категория */
        String spinnerFields[] = new String[] {
                "_id",
                "category_name",
                "category_parent_id"
        };
        Cursor dbCursorMain = db.select("categories", spinnerFields, "category_parent_id", "0", "category_name", "ASC");

        // Създаване на масив
        int dbCursorCount = dbCursorMain.getCount();
        String[] arraySpinnerMainCategories = new String[dbCursorCount];

        // Конвертиране на курсора в стринг
        for(int x=0;x<dbCursorCount;x++){
            arraySpinnerMainCategories[x] = dbCursorMain.getString(1).toString();
            dbCursorMain.moveToNext();
        }

        // Попълване на спинера
        Spinner spinnerCatMain = (Spinner) getActivity().findViewById(R.id.spinnerEditFoodCategoryMain);
        ArrayAdapter<String> adapterMain = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinnerMainCategories);
        spinnerCatMain.setAdapter(adapterMain);


        /* Main категория listener */
        spinnerCatMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString(); //Това е вашият избран елемент
                editFoodMainCategoryChanged(selectedItem);
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        /* SubmitButton listener */
        Button buttonEditFood = (Button)getActivity().findViewById(R.id.buttonEditFood);
        buttonEditFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAddFoodSubmitOnClick();
            }
        });


        /* затваряне на db */
        db.close();
    } // addFood

    /*- Бутон Добавяне на храна при щракване */
    public void buttonAddFoodSubmitOnClick(){
        /* Db */
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        // Грешка?
        int error = 0;


        /* General */

        // Име
        EditText editTextEditFoodName = (EditText)getActivity().findViewById(R.id.editTextEditFoodName);
        String stringName = editTextEditFoodName.getText().toString();
        String stringNameSQL = db.quoteSmart(stringName);
        if(stringName.equals("")){
            Toast.makeText(getActivity(), "Please fill in a name.", Toast.LENGTH_SHORT).show();
            error = 1;
        }

        // Производство
        EditText editTextEditFoodManufactor = (EditText)getActivity().findViewById(R.id.editTextEditFoodManufactor);
        String stringManufactor = editTextEditFoodManufactor.getText().toString();
        String stringManufactorSQL = db.quoteSmart(stringManufactor);
        if(stringManufactor.equals("")){
            Toast.makeText(getActivity(), "Please fill in a manufactor.", Toast.LENGTH_SHORT).show();
            error = 1;
        }

        // Описание
        EditText editTextEditFoodDescription = (EditText)getActivity().findViewById(R.id.editTextEditFoodDescription);
        String stringDescription = editTextEditFoodDescription.getText().toString();
        String stringDescriptionSQL = db.quoteSmart(stringDescription);

        // Баркод
        EditText editTextEditFoodBarcode = (EditText)getActivity().findViewById(R.id.editTextEditFoodBarcode);
        String stringBarcode = editTextEditFoodBarcode.getText().toString();
        String stringBarcodeSQL = db.quoteSmart(stringBarcode);

        /* Категория */
        // Sub Категория
        Spinner spinnerSubCat = (Spinner)getActivity().findViewById(R.id.spinnerEditFoodCategorySub);
        int intSubCategoryIndex = spinnerSubCat.getSelectedItemPosition();
        String stringSpinnerSubCategoryName = spinnerSubCat.getSelectedItem().toString();

        // Намиране на родителски ID от текста
        String stringSpinnerSubCategoryNameSQL = db.quoteSmart(stringSpinnerSubCategoryName);
        String spinnerFields[] = new String[] {
                "_id",
                "category_name",
                "category_parent_id"
        };
        Cursor findstringSpinnerSubCategoryID = db.select("categories", spinnerFields, "category_name", stringSpinnerSubCategoryNameSQL);
        String stringSubCategoryID  = findstringSpinnerSubCategoryID.getString(0).toString();
        String stringSubCategoryIDSQL = db.quoteSmart(stringSubCategoryID);


        /* Serving Table */

        // Размер
        EditText editTextEditFoodSize = (EditText)getActivity().findViewById(R.id.editTextEditFoodSize);
        String stringSize = editTextEditFoodSize.getText().toString();
        String stringSizeSQL = db.quoteSmart(stringSize);
        double doubleServingSize = 0;
        if(stringSize.equals("")){
            Toast.makeText(getActivity(), "Please fill in a size.", Toast.LENGTH_SHORT).show();
            error = 1;
        }
        else{
            try {
                doubleServingSize = Double.parseDouble(stringSize);
            }
            catch(NumberFormatException nfe) {
                Toast.makeText(getActivity(), "Serving size is not number.", Toast.LENGTH_SHORT).show();
                error = 1;
            }
        }

        // Измерване
        EditText editTextEditFoodMesurment = (EditText)getActivity().findViewById(R.id.editTextEditFoodMesurment);
        String stringMesurment = editTextEditFoodMesurment.getText().toString();
        String stringMesurmentSQL = db.quoteSmart(stringMesurment);
        if(stringMesurment.equals("")){
            Toast.makeText(getActivity(), "Please fill in mesurment.", Toast.LENGTH_SHORT).show();
            error = 1;
        }

        // Номер
        EditText editTextEditFoodNumber = (EditText)getActivity().findViewById(R.id.editTextEditFoodNumber);
        String stringNumber = editTextEditFoodNumber.getText().toString();
        String stringNumberSQL = db.quoteSmart(stringNumber);
        if(stringNumber.equals("")){
            Toast.makeText(getActivity(), "Please fill in number.", Toast.LENGTH_SHORT).show();
            error = 1;
        }

        // Дума
        EditText editTextEditFoodWord = (EditText)getActivity().findViewById(R.id.editTextEditFoodWord);
        String stringWord = editTextEditFoodWord.getText().toString();
        String stringWordSQL = db.quoteSmart(stringWord);
        if(stringWord.equals("")){
            Toast.makeText(getActivity(), "Please fill in word.", Toast.LENGTH_SHORT).show();
            error = 1;
        }


        /* Калории table */
        // Енергия
        EditText editTextEditFoodEnergyPerHundred = (EditText)getActivity().findViewById(R.id.editTextEditFoodEnergyPerHundred);
        String stringEnergyPerHundred = editTextEditFoodEnergyPerHundred.getText().toString();
        stringEnergyPerHundred = stringEnergyPerHundred.replace(",", ".");
        double doubleEnergyPerHundred = 0;
        if(stringEnergyPerHundred.equals("")){
            Toast.makeText(getActivity(), "Please fill in energy.", Toast.LENGTH_SHORT).show();
            error = 1;
        }
        else{
            try {
                doubleEnergyPerHundred = Double.parseDouble(stringEnergyPerHundred);
            }
            catch(NumberFormatException nfe) {
                Toast.makeText(getActivity(), "Energy is not a number.", Toast.LENGTH_SHORT).show();
                error = 1;
            }
        }
        String stringEnergyPerHundredSQL = db.quoteSmart(stringEnergyPerHundred);

        // Протеини
        EditText editTextEditFoodProteinsPerHundred = (EditText)getActivity().findViewById(R.id.editTextEditFoodProteinsPerHundred);
        String stringProteinsPerHundred = editTextEditFoodProteinsPerHundred.getText().toString();
        stringProteinsPerHundred = stringProteinsPerHundred.replace(",", ".");
        double doubleProteinsPerHundred = 0;
        if(stringProteinsPerHundred.equals("")){
            Toast.makeText(getActivity(), "Please fill in proteins.", Toast.LENGTH_SHORT).show();
            error = 1;
        }
        else{
            try {
                doubleProteinsPerHundred = Double.parseDouble(stringProteinsPerHundred);
            }
            catch(NumberFormatException nfe) {
                Toast.makeText(getActivity(), "Protein is not a number.\n" + "You wrote: " + stringProteinsPerHundred, Toast.LENGTH_SHORT).show();
                error = 1;
            }
        }
        String stringProteinsPerHundredSQL = db.quoteSmart(stringProteinsPerHundred);

        // Въглехидрати
        EditText editTextEditFoodCarbsPerHundred = (EditText)getActivity().findViewById(R.id.editTextEditFoodCarbsPerHundred);
        String stringCarbsPerHundred = editTextEditFoodCarbsPerHundred.getText().toString();
        stringCarbsPerHundred = stringCarbsPerHundred.replace(",", ".");
        double doubleCarbsPerHundred = 0;
        if(stringCarbsPerHundred.equals("")){
            Toast.makeText(getActivity(), "Please fill in carbs.", Toast.LENGTH_SHORT).show();
            error = 1;
        }
        else{
            try {
                doubleCarbsPerHundred = Double.parseDouble(stringCarbsPerHundred);
            }
            catch(NumberFormatException nfe) {
                Toast.makeText(getActivity(), "Carbs is not a number.\nYou wrote: " + stringCarbsPerHundred, Toast.LENGTH_SHORT).show();
                error = 1;
            }
        }
        String stringCarbsPerHundredSQL = db.quoteSmart(stringCarbsPerHundred);

        // мазнинИ
        EditText editTextEditFoodFatPerHundred = (EditText)getActivity().findViewById(R.id.editTextEditFoodFatPerHundred);
        String stringFatPerHundred = editTextEditFoodFatPerHundred.getText().toString();
        stringFatPerHundred = stringFatPerHundred.replace(",", ".");
        double doubleFatPerHundred = 0;
        if(stringFatPerHundred.equals("")){
            Toast.makeText(getActivity(), "Please fill in fat.", Toast.LENGTH_SHORT).show();
            error = 1;
        }
        else{
            try {
                doubleFatPerHundred = Double.parseDouble(stringFatPerHundred);
            }
            catch(NumberFormatException nfe) {
                Toast.makeText(getActivity(), "Carbs is not a number.", Toast.LENGTH_SHORT).show();
                error = 1;
            }
        }
        String stringFatPerHundredSQL = db.quoteSmart(stringFatPerHundred);



        /* ВмъкВАНЕ */
        if(error == 0){

            /* Калории table pеr ястие */
            double energyCalculated = Math.round((doubleEnergyPerHundred*doubleServingSize)/100);
            double proteinsCalculated = Math.round((doubleProteinsPerHundred*doubleServingSize)/100);
            double carbsCalculated = Math.round((doubleCarbsPerHundred*doubleServingSize)/100);
            double fatCalculated = Math.round((doubleFatPerHundred*doubleServingSize)/100);

            String stringEnergyCalculated = "" + energyCalculated;
            String stringProteinsCalculated = "" + proteinsCalculated;
            String stringCarbsCalculated = "" + carbsCalculated;
            String stringfatCalculated = "" + fatCalculated;

            String stringEnergyCalculatedSQL = db.quoteSmart(stringEnergyCalculated);
            String stringProteinsCalculatedSQL = db.quoteSmart(stringProteinsCalculated);
            String stringCarbsCalculatedSQL = db.quoteSmart(stringCarbsCalculated);
            String stringfatCalculatedSQL = db.quoteSmart(stringfatCalculated);


            String fields =
                    "_id, " +
                            "food_name, " +
                            "food_manufactor_name, " +
                            "food_description, " +
                            "food_serving_size_gram, " +
                            "food_serving_size_gram_mesurment, " +
                            "food_serving_size_pcs, " +
                            "food_serving_size_pcs_mesurment, " +
                            "food_energy, " +
                            "food_proteins, " +
                            "food_carbohydrates, " +
                            "food_fat, " +
                            "food_energy_calculated, " +
                            "food_proteins_calculated, " +
                            "food_carbohydrates_calculated, " +
                            "food_fat_calculated, " +
                            "food_barcode, " +
                            "food_category_id";

            String values =
                    "NULL, " +
                            stringNameSQL + ", " +
                            stringManufactorSQL + ", " +
                            stringDescriptionSQL + ", " +
                            stringSizeSQL + ", " +
                            stringMesurmentSQL + ", " +
                            stringNumberSQL + ", " +
                            stringWordSQL + ", " +
                            stringEnergyPerHundredSQL + ", " +
                            stringProteinsPerHundredSQL + ", " +
                            stringCarbsPerHundredSQL + ", " +
                            stringFatPerHundredSQL + ", " +
                            stringEnergyCalculatedSQL + ", " +
                            stringProteinsCalculatedSQL + ", " +
                            stringCarbsCalculatedSQL + ", " +
                            stringfatCalculatedSQL + ", " +
                            stringBarcodeSQL + ", " +
                            stringSubCategoryIDSQL;


            db.insert("food", fields, values);

            // ипращане на съобщение
            Toast.makeText(getActivity(), "Food created", Toast.LENGTH_SHORT).show();

            // Преместване на потребителя обратно в правилния дизайн
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, new FoodFragment(), FoodFragment.class.getName()).commit();

        } // error == 0


        /* затваряне на db */
        db.close();
    } // buttonAddFoodSubmitOnClick

    /*- addFoodToDiarySelectMealNumber  */
    public void addFoodToDiarySelectMealNumber(){
        /* Промяна на layout */
        int newViewID = R.layout.fragment_home_select_meal_number;
        setMainView(newViewID);


        TextView textViewBreakfast = (TextView)getActivity().findViewById(R.id.textViewBreakfast);
        textViewBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFoodToDiarySelectedMealNumberMoveToAdd(0);
            }
        });

        TextView textViewLunch = (TextView)getActivity().findViewById(R.id.textViewLunch);
        textViewLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFoodToDiarySelectedMealNumberMoveToAdd(1);
            }
        });

        TextView textViewBeforeTraining = (TextView)getActivity().findViewById(R.id.textViewBeforeTraining);
        textViewBeforeTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFoodToDiarySelectedMealNumberMoveToAdd(2);
            }
        });

        TextView textViewAfterTraining = (TextView)getActivity().findViewById(R.id.textViewAfterTraining);
        textViewAfterTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFoodToDiarySelectedMealNumberMoveToAdd(3);
            }
        });

        TextView textViewDinner = (TextView)getActivity().findViewById(R.id.textViewDinner);
        textViewDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFoodToDiarySelectedMealNumberMoveToAdd(4);
            }
        });

        TextView textViewSnacks = (TextView)getActivity().findViewById(R.id.textViewSnacks);
        textViewSnacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFoodToDiarySelectedMealNumberMoveToAdd(5);
            }
        });

        TextView textViewSupper = (TextView)getActivity().findViewById(R.id.textViewSupper);
        textViewSupper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFoodToDiarySelectedMealNumberMoveToAdd(6);
            }
        });


    } // addFoodToDiarySelectMealNumber

    /*- addFoodToDiarySelectedMealNumberMoveToAdd  */
    public void addFoodToDiarySelectedMealNumberMoveToAdd(int mealNumber){

        /* Инициализиране на фрагмента */
        Fragment fragment = null;
        Class fragmentClass = null;
        fragmentClass = AddFoodToDiaryFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Изпращане на променлива
        Bundle bundle = new Bundle();
        bundle.putString("mealNumber", ""+mealNumber);
        bundle.putString("currentFoodId", ""+mealNumber);
        bundle.putString("action", "foodInCategoryListItemClicked");
        fragment.setArguments(bundle);

        // Трябва да премине номера на храната
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();


    }
    /*- фрагмент методи -*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
