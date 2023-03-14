package com.example.myfitnessapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


/**
 * {@link Fragment} subclass.
 * Дейностите, които съдържат този фрагмент, трябва да имплементират
 * интерфейса {@link OnFragmentInteractionListener} за обработка на
 * събития на взаимодействие(interaction events).
 * Използва се метод {@link ProfileFragment#newInstance} за
 * създаване на екземпляр на този фрагмент.
 */
public class ProfileFragment extends Fragment {
    /*- 01 Class променливи  */
    private View mainView;


    // Бутони за действие на лентата с инструменти
    private MenuItem menuItemEdit;
    private MenuItem menuItemDelete;

    /*- 02 Фрагментни променливи */
    // Необходими за изпълнение на фрагмента
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /*- 03 Конструктор */
    // Необходим за наличието на Fragment като клас
    public ProfileFragment() {
        // Необходим празен public конструктор
    }

    /*- 04 Създаване на фрагмент */
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /*- на Създадена дейност */
    // Изпълнение на методи при стартиране
    // на Зададени елементи от менюто на лентата с инструменти
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* Задаване на заглавие */
        ((FragmentActivity)getActivity()).getSupportActionBar().setTitle("Profile");

        // вземане на данни от Db и дисплей
        initalizeGetDataFromDbAndDisplay();

        // Създаване на меню
        // setHasOptionsMenu(true);
    } // onActivityCreated


    /*- 06 При създаване на изглед(view) */
    // Задаване на главната променлива на View към изгледа,
    // така че да можем да се променят изгледите във фрагмент
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_profile, container, false);
        return mainView;
    }


    /*- 07 задаване на main изглед */
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
        MenuInflater menuInflater = ((FragmentActivity)getActivity()).getMenuInflater();
        inflater.inflate(R.menu.menu_goal, menu);

        // Присвояване на елементи от менюто към променливи
        menuItemEdit = menu.findItem(R.id.menu_action_food_edit);

    }

    /*- 09 на Опции Избран елемент */
    // Щракване върху иконата за действие върху менюто
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        int id = menuItem.getItemId();
        return super.onOptionsItemSelected(menuItem);
    }

    /*- Взимане на данни от db и дисплей */
    public void initalizeGetDataFromDbAndDisplay(){

        /*  Взимане на данни от база данни */
        // db
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        /* Взимане на ред номер едно от потребителите */
        long rowID = 1;
        String fields[] = new String[] {
                "_id",
                "user_dob",
                "user_gender",
                "user_height",
                "user_mesurment"
        };
        Cursor c = db.select("users", fields, "_id", rowID);
        String stringUserDob = c.getString(1);
        String stringUserGender  = c.getString(2);
        String stringUserHeight = c.getString(3);
        String stringUserMesurment = c.getString(4);

        /* Ден на раждане */
        String[] items1 = stringUserDob.split("-");
        String stringUserDobYear  = items1[0];
        String stringUserDobMonth = items1[1];
        String stringUserDobYDay  = items1[2];

        /* Ден на раждане: Ден */

        // Попълване на числата за рождени дни
        int spinnerDOBDaySelectedIndex = 0;
        String[] arraySpinnerDOBDay = new String[31];
        int human_counter = 0;
        for(int x=0;x<31;x++){
            human_counter=x+1;
            arraySpinnerDOBDay[x] = "" + human_counter;

            if(stringUserDobYDay.equals("0" + human_counter) || stringUserDobYDay.equals(""+human_counter)){
                spinnerDOBDaySelectedIndex = x;
            }

        }
        Spinner spinnerDOBDay = (Spinner) getActivity().findViewById(R.id.spinnerEditProfileDOBDay);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinnerDOBDay);
        spinnerDOBDay.setAdapter(adapter);

        spinnerDOBDay.setSelection(spinnerDOBDaySelectedIndex); // Select index


        /* Роден: Месец */
        int intUserDobMonth = 0;
        stringUserDobYDay.replace("0", "");
        try {
            intUserDobMonth = Integer.parseInt(stringUserDobMonth);
        }
        catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }
        intUserDobMonth = intUserDobMonth-1;
        Spinner spinnerDOBMonth = (Spinner) getActivity().findViewById(R.id.spinnerEditProfileDOBMonth);
        spinnerDOBMonth.setSelection(intUserDobMonth); // Select index

        /* Роден: Година */
        // Попълване на цифрите за година на раждане

        int spinnerDOBYearSelectedIndex = 0;

        // взимане на текущата година, месец и ден
        String[] arraySpinnerDOBYear = new String[100];
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int end = year-100;
        int index = 0;
        for(int x=year;x>end;x--){
            arraySpinnerDOBYear[index] = "" + x;

            if(stringUserDobYear.equals(""+x)){
                spinnerDOBYearSelectedIndex = index;
            }
            index++;
        }
        Spinner spinnerDOBYear = (Spinner)getActivity().findViewById(R.id.spinnerEditProfileDOBYear);
        ArrayAdapter<String> adapterYear = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinnerDOBYear);
        spinnerDOBYear.setAdapter(adapterYear);
        spinnerDOBYear.setSelection(spinnerDOBYearSelectedIndex); // Избиране на индекс


        /* Пол */
        RadioButton radioButtonGenderMale = (RadioButton)getActivity().findViewById(R.id.radioButtonGenderMale);
        RadioButton radioButtonGenderFemale = (RadioButton)getActivity().findViewById(R.id.radioButtonGenderFemale);
        if(stringUserGender.startsWith("m")){
            radioButtonGenderMale.setChecked(true);
            radioButtonGenderFemale.setChecked(false);
        }
        else{
            radioButtonGenderMale.setChecked(false);
            radioButtonGenderFemale.setChecked(true);
        }

        /* Височина */
        EditText editTextEditProfileHeightCm = (EditText)getActivity().findViewById(R.id.editTextEditProfileHeightCm);
        EditText editTextEditProfileHeightInches = (EditText)getActivity().findViewById(R.id.editTextEditProfileHeightInches);
        TextView textViewEditProfileCm = (TextView)getActivity().findViewById(R.id.textViewEditProfileCm);
        if(stringUserMesurment.startsWith("m")) {
            editTextEditProfileHeightInches.setVisibility(View.GONE);
            editTextEditProfileHeightCm.setText(stringUserHeight);
        }
        else{
            textViewEditProfileCm.setText("feet and inches");
            double heightCm = 0;
            double heightFeet = 0;
            double heightInches = 0;

            // Намиране нависочина
            try {
                heightCm = Double.parseDouble(stringUserHeight);
            }
            catch(NumberFormatException nfe) {

            }
            if(heightCm != 0){
                // Преобразуване на CM в feet
                // feet = cm * 0.3937008)/12
                heightFeet = (heightCm * 0.3937008)/12;
                // heightFeet = Math.round(heightFeet);
                int intHeightFeet = (int) heightFeet;

                editTextEditProfileHeightCm.setText("" + intHeightFeet);

            }

        }




        /* Измерване */
        Spinner spinnerEditProfileMesurment = (Spinner)getActivity().findViewById(R.id.spinnerEditProfileMesurment);
        if(stringUserMesurment.startsWith("m")) {
            spinnerEditProfileMesurment.setSelection(0); // Избор на индекс

        }
        else{
            spinnerEditProfileMesurment.setSelection(1); // Избор на индекс
        }



        /* Listener Mesurment spinner */
        spinnerEditProfileMesurment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                mesurmentChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // mesurmentChanged();
            }
        });


        /* Listener buttonSignUp */
        Button buttonEditProfileSubmit = (Button)getActivity().findViewById(R.id.buttonEditProfileSubmit);
        buttonEditProfileSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                editProfileSubmit();
            }
        });



        // затваряне на db
        db.close();

    }


    /*- Промени в измерването */
    public void mesurmentChanged() {

        // Mesurment spinner
        Spinner spinnerMesurment = (Spinner)getActivity().findViewById(R.id.spinnerEditProfileMesurment);
        String stringMesurment = spinnerMesurment.getSelectedItem().toString();


        EditText editTextEditProfileHeightCm = (EditText)getActivity().findViewById(R.id.editTextEditProfileHeightCm);
        EditText editTextEditProfileHeightInches = (EditText)getActivity().findViewById(R.id.editTextEditProfileHeightInches);

        TextView textViewEditProfileCm = (TextView)getActivity().findViewById(R.id.textViewEditProfileCm);



        if(stringMesurment.startsWith("M")) {
            // метров
            editTextEditProfileHeightInches.setVisibility(View.GONE);
            textViewEditProfileCm.setText("cm");
        }
        else{
            // Империал
            editTextEditProfileHeightInches.setVisibility(View.VISIBLE);
            textViewEditProfileCm.setText("feet and inches");

        }


    } // public voild messuredChanged


    /*- редактиране на - профил изпращане */
    private void editProfileSubmit(){
        /*  Взимане на данни от база данни */
        // db
        DBAdapter db = new DBAdapter(getActivity());
        db.open();


        /* Грешка? */
        int error = 0;


        // Дата на раждане
        Spinner spinnerDOBDay = (Spinner)getActivity().findViewById(R.id.spinnerEditProfileDOBDay);
        String stringDOBDay = spinnerDOBDay.getSelectedItem().toString();
        int intDOBDay = 0;
        try {
            intDOBDay = Integer.parseInt(stringDOBDay);

            if(intDOBDay < 10){
                stringDOBDay = "0" + stringDOBDay;
            }

        }
        catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
            error = 1;
            Toast.makeText(getActivity(), "Please select a day for your birthday.", Toast.LENGTH_SHORT).show();
        }


        // Дата на раждане Месец
        Spinner spinnerDOBMonth = (Spinner)getActivity().findViewById(R.id.spinnerEditProfileDOBMonth);
        String stringDOBMonth = spinnerDOBMonth.getSelectedItem().toString();
        int positionDOBMonth = spinnerDOBMonth.getSelectedItemPosition();
        int month = positionDOBMonth+1;
        if(month < 10){
            stringDOBMonth = "0" + month;
        }
        else{
            stringDOBMonth = "" + month;
        }

        // Година на раждане
        Spinner spinnerDOBYear = (Spinner)getActivity().findViewById(R.id.spinnerEditProfileDOBYear);
        String stringDOBYear = spinnerDOBYear.getSelectedItem().toString();
        int intDOBYear = 0;
        try {
            intDOBYear = Integer.parseInt(stringDOBYear);
        }
        catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
            error = 1;
            Toast.makeText(getActivity(), "Please select a year for your birthday.", Toast.LENGTH_SHORT).show();
        }

        // Събиране на датата на раждане заедно
        String dateOfBirth = intDOBYear + "-" + stringDOBMonth + "-" + stringDOBDay;
        String dateOfBirthSQL = db.quoteSmart(dateOfBirth);


        // Пол
        RadioGroup radioGroupGender = (RadioGroup)getActivity().findViewById(R.id.radioGroupGender);
        int radioButtonID = radioGroupGender.getCheckedRadioButtonId(); // get selected radio button from radioGroup
        View radioButtonGender = radioGroupGender.findViewById(radioButtonID);
        int position = radioGroupGender.indexOfChild(radioButtonGender); // If you want position of Radiobutton

        String stringGender = "";
        if(position == 0){
            stringGender = "male";
        }
        else{
            stringGender = "female";
        }
        String genderSQL = db.quoteSmart(stringGender);



        /* Височина */
        EditText editTextHeightCm = (EditText)getActivity().findViewById(R.id.editTextEditProfileHeightCm);
        EditText editTextHeightInches = (EditText)getActivity().findViewById(R.id.editTextEditProfileHeightInches);
        String stringHeightCm = editTextHeightCm.getText().toString();
        String stringHeightInches = editTextHeightInches.getText().toString();

        double heightCm = 0;
        double heightFeet = 0;
        double heightInches = 0;
        boolean metric = true;



        // Метрични(Метров) или имперски?
        Spinner spinnerMesurment = (Spinner)getActivity().findViewById(R.id.spinnerEditProfileMesurment);
        String stringMesurment = spinnerMesurment.getSelectedItem().toString();

        int intMesurment = spinnerMesurment.getSelectedItemPosition();
        if(intMesurment == 0){
            stringMesurment = "metric";
        }
        else{
            stringMesurment = "imperial";
            metric = false;
        }
        String mesurmentSQL = db.quoteSmart(stringMesurment);

        if(metric == true) {

            // Преобразуване на CM
            try {
                heightCm = Double.parseDouble(stringHeightCm);
                heightCm = Math.round(heightCm);
            }
            catch(NumberFormatException nfe) {
                error = 1;
                Toast.makeText(getActivity(), "Height (cm) has to be a number.", Toast.LENGTH_SHORT).show();
            }
        }
        else {

            // Преобразуване на футове
            try {
                heightFeet = Double.parseDouble(stringHeightCm);
            }
            catch(NumberFormatException nfe) {
                error = 1;
                Toast.makeText(getActivity(), "Height (feet) has to be a number.", Toast.LENGTH_SHORT).show();
            }

            // Конвертиране на инчове
            try {
                heightInches = Double.parseDouble(stringHeightInches);
            }
            catch(NumberFormatException nfe) {
                error = 1;
                Toast.makeText(getActivity(), "Height (inches) has to be a number.", Toast.LENGTH_SHORT).show();
            }

            // Трябва да се преобразува, искаме да запазим числото в cm
            // cm = ((foot * 12) + inches) * 2.54
            heightCm = ((heightFeet * 12) + heightInches) * 2.54;
            heightCm = Math.round(heightCm);
        }
        stringHeightCm = "" + heightCm;
        String heightCmSQL = db.quoteSmart(stringHeightCm);



        if(error == 0){

            long id = 1;

            String fields[] = new String[] {
                    "user_dob",
                    "user_gender",
                    "user_height",
                    "user_mesurment"
            };
            String values[] = new String[] {
                    dateOfBirthSQL,
                    genderSQL,
                    heightCmSQL,
                    mesurmentSQL
            };

            db.update("users", "_id", id, fields, values);

            Toast.makeText(getActivity(), "Changes saved", Toast.LENGTH_SHORT).show();

        } // error == 0



        // затваряне на db
        db.close();

    } // editProfileSubmit


    /*- Фрагментни методи -*/


    /*- При създаване */
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
