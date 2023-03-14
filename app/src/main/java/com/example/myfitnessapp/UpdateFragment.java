package com.example.myfitnessapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Прост подклас {@link Fragment}.
 * Дейностите, които съдържат този фрагмент, трябва да изпълняват
 * Интерфейс {@link OnFragmentInteractionListener}
 * за обработка на събития на взаимодействие (interaction events).
 * Използва се метод {@link UpdateFragment#newInstance} за
 * създаване на екземпляр на този фрагмент.
 */
public class UpdateFragment extends Fragment {
    // параметрите за инициализация на фрагмента, напр. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public UpdateFragment() {
        // Необходим празен публичен конструктор
    }

    /**
     * Използвайте този фабричен метод,
     * за да създадете нов екземпляр на този фрагмент,
     * като използвате предоставените параметри.
     * @param param1 Параметър 1.
     * @param param2 Параметър 2.
     * @return Нов екземпляр на фрагмент TestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateFragment newInstance(String param1, String param2) {
        UpdateFragment fragment = new UpdateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    /*- 05 на Създадена дейност */
    // Изпълнение на методи при стартиране
    // Задаване на елементи от менюто на лентата с инструменти
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* Задаване на заглавие */
        ((FragmentActivity) getActivity()).getSupportActionBar().setTitle("Update");

    } // onActivityCreated


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout за този фрагмент
        return inflater.inflate(R.layout.fragment_update, container, false);
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
        void onFragmentInteraction(Uri uri);
    }
}
