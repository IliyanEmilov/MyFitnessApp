package com.example.myfitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SignUp extends AppCompatActivity {


    /* променливи */
    private String[] arraySpinnerDOBDay = new String[31];
    private String[] arraySpinnerDOBYear = new String[100];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);


        /* Попълване числата за датата на раждане */
        int human_counter = 0;
        for(int x=0;x<31;x++){
            human_counter=x+1;
            this.arraySpinnerDOBDay[x] = "" + human_counter;
        }
        Spinner spinnerDOBDay = (Spinner) findViewById(R.id.spinnerDOBDay);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinnerDOBDay);
        spinnerDOBDay.setAdapter(adapter);

        /* Попълване цифрите за година на раждане */
        // взимане на текущата година, месец и ден
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int end = year-100;
        int index = 0;
        for(int x=year;x>end;x--){
            this.arraySpinnerDOBYear[index] = "" + x;

            index++;
        }

        Spinner spinnerDOBYear = (Spinner) findViewById(R.id.spinnerDOBYear);
        ArrayAdapter<String> adapterYear = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinnerDOBYear);
        spinnerDOBYear.setAdapter(adapterYear);

        // Скриване на съобщението за грешка
        TextView textViewErrorMessage = (TextView)findViewById(R.id.textViewErrorMessage);
        textViewErrorMessage.setVisibility(View.GONE);

        /* Скриване на полето за icnhes */
        EditText editTextHeightInches = (EditText)findViewById(R.id.editTextHeightInches);
        editTextHeightInches.setVisibility(View.GONE);


        /* Listener Mesurment spinner */
        Spinner spinnerMesurment = (Spinner)findViewById(R.id.spinnerMesurment);
        spinnerMesurment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        Button buttonSignUp = (Button)findViewById(R.id.buttonSignUp);
        buttonSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                signUpSubmit();
            }
        });


    } // protected void onCreate

    /*- Промени в измерването */
    public void mesurmentChanged() {

        // Mesurment spinner
        Spinner spinnerMesurment = (Spinner)findViewById(R.id.spinnerMesurment);
        String stringMesurment = spinnerMesurment.getSelectedItem().toString();


        EditText editTextHeightCm = (EditText)findViewById(R.id.editTextHeightCm);
        EditText editTextHeightInches = (EditText)findViewById(R.id.editTextHeightInches);
        String stringHeightCm = editTextHeightCm.getText().toString();
        String stringHeightInches = editTextHeightInches.getText().toString();

        double heightCm = 0;
        double heightFeet = 0;
        double heightInches = 0;

        TextView textViewCm = (TextView)findViewById(R.id.textViewCm);
        TextView textViewKg = (TextView)findViewById(R.id.textViewKg);

        if(stringMesurment.startsWith("I")){
            // Империал
            editTextHeightInches.setVisibility(View.VISIBLE);
            textViewCm.setText("feet and inches");
            textViewKg.setText("pound");

            // Намеране на футове
            try {
                heightCm = Double.parseDouble(stringHeightCm);
            }
            catch(NumberFormatException nfe) {

            }
            if(heightCm != 0){
                // Преобразуване CM във футове
                // feet = cm * 0.3937008)/12
                heightFeet = (heightCm * 0.3937008)/12;
                // heightFeet = Math.round(heightFeet);
                int intHeightFeet = (int) heightFeet;

                editTextHeightCm.setText("" + intHeightFeet);

            }

        }
        else{
            // метричен(метров)
            editTextHeightInches.setVisibility(View.GONE);
            textViewCm.setText("cm");
            textViewKg.setText("kg");

            // Промяна на футове и инчове в cm

            // Преобразуване на футове
            try {
                heightFeet = Double.parseDouble(stringHeightCm);
            }
            catch(NumberFormatException nfe) {

            }

            // Преобразуване на инчове
            try {
                heightInches = Double.parseDouble(stringHeightInches);
            }
            catch(NumberFormatException nfe) {

            }

            // Трябва да се преобразува, тъй като искаме да запазим числото в cm
            // cm = ((foot * 12) + inches) * 2.54
            if(heightFeet != 0 && heightInches != 0) {
                heightCm = ((heightFeet * 12) + heightInches) * 2.54;
                heightCm = Math.round(heightCm);
                editTextHeightCm.setText("" + heightCm);
            }
        }



        // Тегло
        EditText editTextWeight = (EditText)findViewById(R.id.editTextWeight);
        String stringWeight = editTextWeight.getText().toString();
        double doubleWeight = 0;

        try {
            doubleWeight = Double.parseDouble(stringWeight);
        }
        catch(NumberFormatException nfe) {
        }

        if(doubleWeight != 0) {

            if (stringMesurment.startsWith("I")) {
                // кг в паундове
                doubleWeight = Math.round(doubleWeight / 0.45359237);
            } else {
                // паундове в кг
                doubleWeight = Math.round(doubleWeight * 0.45359237);
            }
            editTextWeight.setText("" + doubleWeight);
        }

    } // public voild messuredChanged

    /*- Sign up Submit ---------------------------------------------- */
    public void signUpSubmit() {
        // Грешка
        ImageView imageViewError = (ImageView)findViewById(R.id.imageViewError);
        TextView textViewErrorMessage = (TextView)findViewById(R.id.textViewErrorMessage);
        String errorMessage = "";

        // Email
        TextView textViewEmail = (TextView)findViewById(R.id.textViewEmail);
        EditText editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        String stringEmail = editTextEmail.getText().toString();
        if(stringEmail.isEmpty() || stringEmail.startsWith(" ")){
            errorMessage = "Please fill inn an e-mail address.";
        }

        // Дата на раждане
        Spinner spinnerDOBDay = (Spinner)findViewById(R.id.spinnerDOBDay);
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
            errorMessage = "Please select a day for your birthday.";
        }

        // Дата на раждане Месец
        Spinner spinnerDOBMonth = (Spinner)findViewById(R.id.spinnerDOBMonth);
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
        Spinner spinnerDOBYear = (Spinner)findViewById(R.id.spinnerDOBYear);
        String stringDOBYear = spinnerDOBYear.getSelectedItem().toString();
        int intDOBYear = 0;
        try {
            intDOBYear = Integer.parseInt(stringDOBYear);
        }
        catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
            errorMessage = "Please select a year for your birthday.";
        }

        // Събиране на датата на раждане заедно
        String dateOfBirth = intDOBYear + "-" + stringDOBMonth + "-" + stringDOBDay;


        // Пол
        RadioGroup radioGroupGender = (RadioGroup)findViewById(R.id.radioGroupGender);
        int radioButtonID = radioGroupGender.getCheckedRadioButtonId(); // взимане на избран радио бутон от radioGroup
        View radioButtonGender = radioGroupGender.findViewById(radioButtonID);
        int position = radioGroupGender.indexOfChild(radioButtonGender); // Ако се иска позиция на Radiobutton

        String stringGender = "";
        if(position == 0){
            stringGender = "male";
        }
        else{
            stringGender = "female";
        }

        /* Височина */
        EditText editTextHeightCm = (EditText)findViewById(R.id.editTextHeightCm);
        EditText editTextHeightInches = (EditText)findViewById(R.id.editTextHeightInches);
        String stringHeightCm = editTextHeightCm.getText().toString();
        String stringHeightInches = editTextHeightInches.getText().toString();

        double heightCm = 0;
        double heightFeet = 0;
        double heightInches = 0;
        boolean metric = true;

        // Метрични или имперски?
        Spinner spinnerMesurment = (Spinner)findViewById(R.id.spinnerMesurment);
        String stringMesurment = spinnerMesurment.getSelectedItem().toString();

        int intMesurment = spinnerMesurment.getSelectedItemPosition();
        if(intMesurment == 0){
            stringMesurment = "metric";
        }
        else{
            stringMesurment = "imperial";
            metric = false;
        }

        if(metric == true) {

            // Преобразуване в CM
            try {
                heightCm = Double.parseDouble(stringHeightCm);
                heightCm = Math.round(heightCm);
            }
            catch(NumberFormatException nfe) {
                errorMessage = "Height (cm) has to be a number.";
            }
        }
        else {

            // Преобразуване в футове
            try {
                heightFeet = Double.parseDouble(stringHeightCm);
            }
            catch(NumberFormatException nfe) {
                errorMessage = "Height (feet) has to be a number.";
            }

            // Преобразуване в инча
            try {
                heightInches = Double.parseDouble(stringHeightInches);
            }
            catch(NumberFormatException nfe) {
                errorMessage = "Height (inches) has to be a number.";
            }

            // Трябва да се преобразува за да запазим числото в cm
            // cm = ((foot * 12) + inches) * 2.54
            heightCm = ((heightFeet * 12) + heightInches) * 2.54;
            heightCm = Math.round(heightCm);
        }

        // Тегло
        EditText editTextWeight = (EditText)findViewById(R.id.editTextWeight);
        String stringWeight = editTextWeight.getText().toString();
        double doubleWeight = 0;
        try {
            doubleWeight = Double.parseDouble(stringWeight);
        }
        catch(NumberFormatException nfe) {
            errorMessage = "Weight has to be a number.";
        }
        if(metric == true) {
        }
        else{
            // Империал
            // Паунд в кг
            doubleWeight = Math.round(doubleWeight*0.45359237);
        }


        // Ниво на активност
        Spinner spinnerActivityLevel = (Spinner)findViewById(R.id.spinnerActivityLevel);
        //  0: Little to no exercise
        // 1: Light exercise (1–3 days per week)
        // 2: Moderate exercise (3–5 days per week)
        // 3: Heavy exercise (6–7 days per week)
        // 4: Very heavy exercise (twice per day, extra heavy workouts)
        int intActivityLevel = spinnerActivityLevel.getSelectedItemPosition();

        // Error handling
        if(errorMessage.isEmpty()){
            // Поставяне на данни в база данни
            imageViewError.setVisibility(View.GONE);
            textViewErrorMessage.setVisibility(View.GONE);


            // Вмъкване в база данни
            DBAdapter db = new DBAdapter(this);
            db.open();


            // Quote smart
            String stringEmailSQL = db.quoteSmart(stringEmail);
            String dateOfBirthSQL = db.quoteSmart(dateOfBirth);
            String stringGenderSQL = db.quoteSmart(stringGender);
            double heightCmSQL = db.quoteSmart(heightCm);
            int intActivityLevelSQL = db.quoteSmart(intActivityLevel);
            double doubleWeightSQL = db.quoteSmart(doubleWeight);
            String stringMesurmentSQL = db.quoteSmart(stringMesurment);

            // Input за потребителите
            String stringInput = "NULL, " + stringEmailSQL + "," + dateOfBirthSQL + "," + stringGenderSQL + "," + heightCmSQL + "," + stringMesurmentSQL;
            db.insert("users",
                    "_id, user_email, user_dob, user_gender, user_height, user_mesurment",
                    stringInput);

            // Input за цел
            SimpleDateFormat tf = new SimpleDateFormat("yyyy-MM-dd");
            String goalDate = tf.format(Calendar.getInstance().getTime());

            String goalDateSQL = db.quoteSmart(goalDate);

            stringInput = "NULL, " + doubleWeightSQL + "," + goalDateSQL + "," + intActivityLevelSQL;
            db.insert("goal",
                    "_id, goal_current_weight, goal_date, goal_activity_level",
                    stringInput);



            db.close();

            // Преместване на потребителя обратно към MainActivity
            Intent i = new Intent(SignUp.this, SignUpGoal.class);
            startActivity(i);
        }
        else {
            // Има грешка
            textViewErrorMessage.setText(errorMessage);
            imageViewError.setVisibility(View.VISIBLE);
            textViewErrorMessage.setVisibility(View.VISIBLE);
        }
    }

} // public class SignUp
