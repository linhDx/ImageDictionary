package com.linhdx.imagedictionary.View.Activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.google.gson.Gson;
import com.linhdx.imagedictionary.R;
import com.linhdx.imagedictionary.View.Fragment.MyDictFragmet;
import com.linhdx.imagedictionary.View.Fragment.NewWordFragment;
import com.linhdx.imagedictionary.View.Fragment.ShowWordFragment;
import com.linhdx.imagedictionary.View.Interface.CreateListener;
import com.linhdx.imagedictionary.View.Interface.EditListener;
import com.linhdx.imagedictionary.View.Interface.ShowWordListener;
import com.linhdx.imagedictionary.entity.MyWord;


public class MyDictActivity extends FragmentActivity implements CreateListener, EditListener, ShowWordListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dict);

        Gson gson = new Gson();
        MyWord myWord = gson.fromJson(getIntent().getStringExtra("Object"), MyWord.class);
        if(myWord != null){
            Log.d("AAA", myWord.getKeyword());
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(R.id.mydict_container, new MyDictFragmet()).commit();

            editWord(myWord);
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(R.id.mydict_container, new MyDictFragmet()).commit();
        }

    }



    @Override
    public void changeFragment(int number) {
        Fragment fragment;
        if(number==1){
            fragment = new MyDictFragmet();
        } else {
            fragment = new NewWordFragment();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.mydict_container, fragment).commit();
    }

    @Override
    public void editWord(MyWord myWord) {
        Fragment fragment;
        fragment = new NewWordFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        String j = gson.toJson(myWord);
        args.putString("word", j);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.mydict_container, fragment).commit();
    }

    @Override
    public void showWord(MyWord myWord) {
        Fragment fragment = new ShowWordFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        String j = gson.toJson(myWord);
        args.putString("word", j);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.mydict_container, fragment).commit();
    }
}
