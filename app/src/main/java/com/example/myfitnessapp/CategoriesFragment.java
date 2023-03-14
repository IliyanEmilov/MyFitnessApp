package com.example.myfitnessapp;

import android.content.Context;
import android.database.Cursor;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * {@link Fragment} subclass.
 * Дейностите, които съдържат този фрагмент, трябва да имплементират
 * интерфейса {@link OnFragmentInteractionListener} за обработка на
 * събития на взаимодействие(interaction events).
 * Използва се метод {@link CategoriesFragment#newInstance} за
 * създаване на екземпляр на този фрагмент.
 */
public class CategoriesFragment extends Fragment {


    /*- 01 Променливи на класа */
    private View mainView;
    private Cursor listCursorCategory;
    private Cursor listCursorFood;

    // Бутони за действие на лентата с инструменти(тоолбар)
    private MenuItem menuItemEdit;
    private MenuItem menuItemDelete;

    // Държач за бутони на лентата с инструменти
    private String currentCategoryId;
    private String currentCategoryName;

    private String currentFoodId;
    private String currentFoodName;




    /*- 02 Фрагментни променливи */
    // Необходими за стартиране на фрагмента
    // параметрите за инициализация на фрагмента, напр. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;



    /*- 03 Конструктор */
    // Необходим за наличието на Fragment като клас
    public CategoriesFragment() {
        // Необходим празен публичен конструктор
    }


    /*- 04 Създаване на фрагмент */
    public static CategoriesFragment newInstance(String param1, String param2) {
        CategoriesFragment fragment = new CategoriesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    /*- 05 на Създадена дейност */
    // Изпълнявайте методи при стартиране
    // Задаване на елементи от менюто на лентата с инструменти
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* Задаване на заглавие */
        ((FragmentActivity)getActivity()).getSupportActionBar().setTitle("Categories");

        // Попълнете списъка с категории
        populateList("0", ""); // Parent

        // Създаване на меню
        setHasOptionsMenu(true);


    } // onActivityCreated


    /*- 06 При създаване на view  */
    //Задава главната променлива на View към изгледа,
    // така че можем да променяме изгледите във фрагмент
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_categories, container, false);
        return mainView;
    }

    /*- 07 задаване на основен изглед (view) */
    // Промяна на метода на изглед във фрагмент
    private void setMainView(int id){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(id, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(mainView);
    }

    /*- 08 в менюто за опции за създаване  */
    // Създаване на икона за действие в лентата с инструменти(toolbar)
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


    /*- 09 в Опции Избран елемент */
    // Щракната иконата за действие
    // Меню
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        int id = menuItem.getItemId();
        if (id == R.id.action_add) {
            createNewCategory();
        }
        else if (id == R.id.action_edit) {
            editCategory();
        }
        else if (id == R.id.action_delete) {
            deleteCategory();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /*- попълване на списък */
    public void populateList(String parentID, String parentName){

        /* База данни */
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        // Вземете категории
        String fields[] = new String[] {
                "_id",
                "category_name",
                "category_parent_id"
        };
        listCursorCategory = db.select("categories", fields, "category_parent_id", parentID, "category_name", "ASC");

        // Създаване на масив
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
        ListView lv = (ListView)getActivity().findViewById(R.id.listViewCategories);
        lv.setAdapter(adapter);

        // OnClick
        if(parentID.equals("0")) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    listItemClicked(arg2);
                }
            });
        }

        // Затваряне на db
        db.close();


        // Премахване или показване на бутона за редактиране
        if(parentID.equals("0")){
            // Премахване на бутона за редактиране
            //Item menuItemEdit = (Item) getActivity().findViewById(R.id.action_edit);

            // menuItemEdit.setVisible(false);
            // menuItemDelete.setVisible(false);

        }
        else{
            // Показване на бутона за редактиране
            menuItemEdit.setVisible(true);
            menuItemDelete.setVisible(true);

        }


    } // populateList



    /*- Щракване върху елемент от списъка */
    public void listItemClicked(int listItemIDClicked){

        // Преместване на курсора до щракване ID
        listCursorCategory.moveToPosition(listItemIDClicked);

        // Вземане на ID и име от курсора
        // Задаване на текущо име и идентификатор
        currentCategoryId = listCursorCategory.getString(0);
        currentCategoryName = listCursorCategory.getString(1);
        String parentID = listCursorCategory.getString(2);

        // Промяна на заглавието
        ((FragmentActivity)getActivity()).getSupportActionBar().setTitle(currentCategoryName);


        // Преминаване към подклас
        populateList(currentCategoryId, currentCategoryName);


        // Показване на храна в категория
        showFoodInCategory(currentCategoryId, currentCategoryName, parentID);


    } // listItemClicked


    /*- Създаване на нова категория */
    public void createNewCategory(){
        /* Промяна на оформлението(layout) */
        int id = R.layout.fragment_categories_add_edit;
        setMainView(id);

        /* База данни */
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        /* Попълнете спинера с категории */
        String fields[] = new String[] {
                "_id",
                "category_name",
                "category_parent_id"
        };
        Cursor dbCursor = db.select("categories", fields, "category_parent_id", "0", "category_name", "ASC");

        // Създаване на масив
        int dbCursorCount = dbCursor.getCount();
        String[] arraySpinnerCategories = new String[dbCursorCount+1];

        // Това е родител
        arraySpinnerCategories[0] = "-";

        // Преобразуване на курсора в стринг
        for(int x=1;x<dbCursorCount+1;x++){
            arraySpinnerCategories[x] = dbCursor.getString(1).toString();
            dbCursor.moveToNext();
        }

        // Попълване на спинера
        Spinner spinnerParent = (Spinner) getActivity().findViewById(R.id.spinnerCategoryParent);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinnerCategories);
        spinnerParent.setAdapter(adapter);



        /* SubmitButton listener */
        Button buttonHome = (Button)getActivity().findViewById(R.id.buttonCategoriesSubmit);
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewCategorySubmitOnClick();
            }
        });

        /* затваряне на db */
        db.close();

    }
    /*- Създаване на нова категория Изпратете при щракване ----------------------------------------------- */
    public void createNewCategorySubmitOnClick() {
        /* База данни */
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        // Грешка?
        int error = 0;

        // Име
        EditText editTextName = (EditText)getActivity().findViewById(R.id.editTextName);
        String stringName = editTextName.getText().toString();
        if(stringName.equals("")){
            Toast.makeText(getActivity(), "Please fill in a name.", Toast.LENGTH_SHORT).show();
            error = 1;
        }


        // Родител
        Spinner spinner = (Spinner)getActivity().findViewById(R.id.spinnerCategoryParent);
        String stringSpinnerCategoryParent = spinner.getSelectedItem().toString();
        String parentID;
        if(stringSpinnerCategoryParent.equals("-")){
            parentID = "0";
        }
        else{
            // Намиране на ID на родител от текста
            String stringSpinnerCategoryParentSQL = db.quoteSmart(stringSpinnerCategoryParent);
            String fields[] = new String[] {
                    "_id",
                    "category_name",
                    "category_parent_id"
            };
            Cursor findParentID = db.select("categories", fields, "category_name", stringSpinnerCategoryParentSQL);
            parentID = findParentID.getString(0).toString();


        }

        if(error == 0){
            // Готови променливи
            String stringNameSQL = db.quoteSmart(stringName);
            String parentIDSQL = db.quoteSmart(parentID);

            // Вмъкване в база данни
            String input = "NULL, " + stringNameSQL + ", " + parentIDSQL;
            db.insert("categories", "_id, category_name, category_parent_id", input);

            // Даване на обратна връзка
            Toast.makeText(getActivity(), "Category created", Toast.LENGTH_LONG).show();

            // Преместване на потребителя обратно към правилния дизайн
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, new CategoriesFragment(), CategoriesFragment.class.getName()).commit();

        }

        /* затваряне db */
        db.close();
    } // createNewCategorySubmitOnClick



    /*- Редактиране на категория  */
    public void editCategory(){
        // Edit Name = currentName
        // Edit ID   = currentID

        /* Промяна на layout */
        int id = R.layout.fragment_categories_add_edit;
        setMainView(id);

        /* База данни */
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        /* Поискване на ID на родител*/
        Cursor c;
        String fieldsC[] = new String[] { "category_parent_id" };
        String currentIdSQL = db.quoteSmart(currentCategoryId);
        c = db.select("categories", fieldsC, "_id", currentIdSQL);
        String currentParentID = c.getString(0);
        int intCurrentParentID = 0;
        try {
            intCurrentParentID = Integer.parseInt(currentParentID);
        }
        catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }

        /* Попълване на име */
        EditText editTextName = (EditText) getActivity().findViewById(R.id.editTextName);
        editTextName.setText(currentCategoryName);


        /* Попълване на спинера с категории */
        String fields[] = new String[] {
                "_id",
                "category_name",
                "category_parent_id"
        };
        Cursor dbCursor = db.select("categories", fields, "category_parent_id", "0", "category_name", "ASC");

        // Създаване на масив
        int dbCursorCount = dbCursor.getCount();
        String[] arraySpinnerCategories = new String[dbCursorCount+1];

        // Това е родител
        arraySpinnerCategories[0] = "-";

        // Преобразуване на курсора в стринг
        int correctParentID = 0;
        for(int x=1;x<dbCursorCount+1;x++){
            arraySpinnerCategories[x] = dbCursor.getString(1).toString();

            if(dbCursor.getString(0).toString().equals(currentParentID)){
                correctParentID = x;
            }

            // Преминете към следващия
            dbCursor.moveToNext();
        }

        // Попълване на спинера
        Spinner spinnerParent = (Spinner) getActivity().findViewById(R.id.spinnerCategoryParent);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinnerCategories);
        spinnerParent.setAdapter(adapter);

        // Избиране на актуалния спинър се елемент, който е родител на currentID
        spinnerParent.setSelection(correctParentID);

        /* затваряне db */
        db.close();


        /* SubmitButton listener */
        Button buttonHome = (Button)getActivity().findViewById(R.id.buttonCategoriesSubmit);
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCategorySubmitOnClick();
            }
        });

    }

    /*-Редактиране на категория - изпращане при щракване */
    public void editCategorySubmitOnClick(){
        /* База данни */
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        // Грешка?
        int error = 0;

        // Име
        EditText editTextName = (EditText)getActivity().findViewById(R.id.editTextName);
        String stringName = editTextName.getText().toString();
        if(stringName.equals("")){
            Toast.makeText(getActivity(), "Please fill in a name.", Toast.LENGTH_SHORT).show();
            error = 1;
        }


        // Родител
        Spinner spinner = (Spinner)getActivity().findViewById(R.id.spinnerCategoryParent);
        String stringSpinnerCategoryParent = spinner.getSelectedItem().toString();
        String parentID;
        if(stringSpinnerCategoryParent.equals("-")){
            parentID = "0";
        }
        else{
            // Намеране на ID на родител от текста
            String stringSpinnerCategoryParentSQL = db.quoteSmart(stringSpinnerCategoryParent);
            String fields[] = new String[] {
                    "_id",
                    "category_name",
                    "category_parent_id"
            };
            Cursor findParentID = db.select("categories", fields, "category_name", stringSpinnerCategoryParentSQL);
            parentID = findParentID.getString(0).toString();


        }

        if(error == 0){
            // Текущият ID to long
            long longCurrentID = Long.parseLong(currentCategoryId);

            // Готови променливи
            long currentIDSQL = db.quoteSmart(longCurrentID);
            String stringNameSQL = db.quoteSmart(stringName);
            String parentIDSQL = db.quoteSmart(parentID);

            // Вмъкване в база данни
            String input = "NULL, " + stringNameSQL + ", " + parentIDSQL;
            db.update("categories", "_id", currentIDSQL, "category_name", stringNameSQL);
            db.update("categories", "_id", currentIDSQL, "category_parent_id", parentIDSQL);

            // Даване на обратна връзка
            Toast.makeText(getActivity(), "Changes saved", Toast.LENGTH_LONG).show();

            // Преместване на потребителя обратно към правилния дизайн
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, new CategoriesFragment(), CategoriesFragment.class.getName()).commit();

        }

        /* затваряне на db */
        db.close();
    } // editCategorySubmitOnClick


    /*- Изтриване на категория */
    public void deleteCategory(){

        /* промяна на layout */
        int id = R.layout.fragment_categories_delete;
        setMainView(id);

        /* SubmitButton listener */
        Button buttonCancel = (Button)getActivity().findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategoryCancelOnClick();
            }
        });

        Button buttonConfirmDelete = (Button)getActivity().findViewById(R.id.buttonConfirmDelete);
        buttonConfirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategoryConfirmOnClick();
            }
        });


    }
    public void deleteCategoryCancelOnClick(){
        // Преместване на потребителя обратно към правилния дизайн
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new CategoriesFragment(), CategoriesFragment.class.getName()).commit();

    }
    public void deleteCategoryConfirmOnClick(){
        // Изтриване от SQL

        /* База данни */
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        // Актуален ID to long
        long longCurrentID = 0;
        try {
            longCurrentID = Long.parseLong(currentCategoryId);
        }
        catch (NumberFormatException e){
            Toast.makeText(getActivity(), "Error: " + e.toString(), Toast.LENGTH_LONG).show();
        }
        // Готови променливи
        long currentIDSQL = db.quoteSmart(longCurrentID);

        // Изтрий
        db.delete("categories", "_id", currentIDSQL);

        // затваряне db
        db.close();

        // Даване на съобщение
        Toast.makeText(getActivity(), "Category deleted", Toast.LENGTH_LONG).show();

        // Преместване на потребителя обратно към правилния дизайн
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new CategoriesFragment(), CategoriesFragment.class.getName()).commit();

    }



    /*- Показване на храна в категория  */
    public void showFoodInCategory(String categoryId, String categoryName, String categoryParentID){
        if(!(categoryParentID.equals("0"))) {

            /* Промяна на layout */
            int id = R.layout.fragment_food;
            setMainView(id);

            /* База данни */
            DBAdapter db = new DBAdapter(getActivity());
            db.open();

            // Взимане на категории
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

            // Намиране на ListView за попълване
            ListView lvItemsFood = (ListView)getActivity().findViewById(R.id.listViewFood);

            // Настройване на адаптера на курсора, като използвате курсора от последната стъпка
            FoodCursorAdapter continentsAdapter = new FoodCursorAdapter(getActivity(), listCursorFood);

            // Прикрепване на адаптера на курсора към ListView
            lvItemsFood.setAdapter(continentsAdapter); // използва ContinensCursorAdapter


            // OnClick
            lvItemsFood.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    foodListItemClicked(arg2);
                }
            });


            // затваряне на db
            db.close();

        } //categoryParentID.equals
    } // showFoodInCategory

    /*- Щракнат елемент от списъка с храни */
    private void foodListItemClicked(int intFoodListItemIndex){
        // Трябва да използваме
        currentFoodId = listCursorFood.getString(0);
        currentFoodName = listCursorFood.getString(1);

        /* Промяна на фрагмента на FoodView */

        /* Инициализиране на фрагмента */
        Fragment fragment = null;
        Class fragmentClass = null;
        fragmentClass = FoodFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Изпращане на променлива
        Bundle bundle = new Bundle();
        bundle.putString("currentFoodId", ""+currentFoodId);
        fragment.setArguments(bundle);

        // Трябва да се предаде номер на ястие
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();



    }


    /*- Фрагментни методи -*/


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

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

    public interface OnFragmentInteractionListener {
        // TODO: Актуализирай типа на аргумента и името
        void onFragmentInteraction(Uri uri);
    }
}
