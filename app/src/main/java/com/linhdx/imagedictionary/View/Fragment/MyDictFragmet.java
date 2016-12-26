package com.linhdx.imagedictionary.View.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.linhdx.imagedictionary.Adapter.RowMyDictAdapter;
import com.linhdx.imagedictionary.R;
import com.linhdx.imagedictionary.View.Activity.MainActivity;
import com.linhdx.imagedictionary.View.Interface.CreateListener;
import com.linhdx.imagedictionary.View.Interface.EditListener;
import com.linhdx.imagedictionary.View.Interface.ShowWordListener;
import com.linhdx.imagedictionary.entity.MyWord;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by shine on 20/12/2016.
 */

public class MyDictFragmet extends Fragment {

    ListView listMyDict;
    List<String> listWord;

    RowMyDictAdapter adapter;
    ImageView bar_imgBack;
    TextView bar_tvHeader;
    RelativeLayout rlEdit, rlAddNew;
    private boolean isEditMode;
    private CreateListener createListener;
    private EditListener editListener;
    private ShowWordListener showWordListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mydict, container, false);

    }

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initTab();
        initData();
        initListener();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            createListener =(CreateListener)context;
            editListener= (EditListener)context;
            showWordListener = (ShowWordListener)context;
        } catch (ClassCastException castEx){

        }
    }

    private void initData(){
        isEditMode = false;
        listWord = loadHistory();
        adapter = new RowMyDictAdapter(listWord, getContext(), getActivity(), MyDictFragmet.this);
        listMyDict.setAdapter(adapter);
    }



    public List<String> loadHistory() {
        List<String>tmp = new ArrayList<>();
        List<MyWord> myWords = MyWord.listAll(MyWord.class);
        for (MyWord word: myWords
             ) {
            tmp.add(word.getKeyword());
        }
        return tmp;
    }
    private void initView(View view){
        bar_imgBack =(ImageView)view.findViewById(R.id.imgBack);
        bar_tvHeader =(TextView)view.findViewById(R.id.tvHeader) ;
        rlEdit = (RelativeLayout)view.findViewById(R.id.mydict_edit);
        rlAddNew =(RelativeLayout)view.findViewById(R.id.mydict_add_new);
        listMyDict = (ListView)view.findViewById(R.id.listView);
    }
    private void initTab(){
        bar_imgBack.setVisibility(View.VISIBLE);
        bar_tvHeader.setText(getResources().getString(R.string.my_dict));
        bar_imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });
    }

    private void initListener(){
        rlEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditMode= !(isEditMode);
                adapter.setEditmode(isEditMode);
                adapter.notifyDataSetChanged();
            }
        });
        rlAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createListener.changeFragment(2);
            }
        });
    }

    public void delete(int position){
        List<MyWord> myWords = MyWord.listAll(MyWord.class);
        for (MyWord word: myWords
                ) {
            if(word.getKeyword().equals(listWord.get(position))){
                word.delete();
                Toast.makeText(getContext(), "Delete successful!", Toast.LENGTH_LONG).show();
                createListener.changeFragment(1);
                break;
            }
        }

    }

    public void editMyWord(int position) {
        List<MyWord> myWords = MyWord.listAll(MyWord.class);
        for (MyWord word: myWords
                ) {
            if(word.getKeyword().equals(listWord.get(position))){
                editListener.editWord(word);
                break;
            }
        }
    }

    public void showMyWord(int position){
        List<MyWord> myWords = MyWord.listAll(MyWord.class);
        for (MyWord word: myWords
                ) {
            if(word.getKeyword().equals(listWord.get(position))){
                showWordListener.showWord(word);
                break;
            }
        }
    }
}
