package com.linhdx.imagedictionary.View.Fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.linhdx.imagedictionary.R;

import com.linhdx.imagedictionary.View.Interface.CreateListener;
import com.linhdx.imagedictionary.View.Interface.EditListener;
import com.linhdx.imagedictionary.entity.MyWord;
import com.linhdx.imagedictionary.sqlite.SQLiteFactory;
import com.linhdx.imagedictionary.sqlite.SQLiteHelper;
import com.linhdx.imagedictionary.util.DbBitmapUtility;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shine on 20/12/2016.
 */

public class NewWordFragment extends Fragment implements EditListener{


    TextView bar_tvHeader;
    TextView tvEdit;
    TextView tvSave;

    EditText edWord, edPhonetic, edSummary, edMean, edNote;
    RelativeLayout rlCamera, rlSelectImg;
    ImageView imgPicture;
    MyWord myEditWord;
    Bitmap bitmap;
    private CreateListener createListener;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private int PICK_IMAGE_REQUEST = 2;
    private boolean isEditmode= false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if( bundle!= null && bundle.getString("word")!= null){
            Gson gson = new Gson();
            myEditWord = gson.fromJson(bundle.getString("word"), MyWord.class);
        }
        return inflater.inflate(R.layout.fragment_newword, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initTab();
        initListener();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            createListener = (CreateListener) context;
        } catch (ClassCastException castEx) {

        }
    }

    private void initView(View view) {
        bar_tvHeader = (TextView) view.findViewById(R.id.tvHeader);
        tvEdit = (TextView) view.findViewById(R.id.tvEdit);
        tvSave = (TextView) view.findViewById(R.id.tvSave);

        edWord = (EditText) view.findViewById(R.id.nw_word);
        edPhonetic = (EditText) view.findViewById(R.id.nw_phonetic);
        edSummary = (EditText) view.findViewById(R.id.nw_summary);
        edMean = (EditText) view.findViewById(R.id.nw_mean);
        edNote =(EditText)view.findViewById(R.id.nw_note);

        rlCamera = (RelativeLayout) view.findViewById(R.id.nw_camera);
        rlSelectImg = (RelativeLayout) view.findViewById(R.id.nw_select_img);
        imgPicture = (ImageView) view.findViewById(R.id.img_my_picture);
        if(myEditWord!= null && myEditWord.getKeyword() != null){
            isEditmode = true;
            editWord(myEditWord);
        }
    }



    private void initTab() {
        tvEdit.setVisibility(View.VISIBLE);
        tvSave.setVisibility(View.VISIBLE);
        bar_tvHeader.setText("Add new my word");

    }


    private void initListener() {
        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createListener.changeFragment(1);
            }
        });
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditmode){
                    updateToDB(myEditWord.getId());
                } else
                saveWordToDB();
            }
        });
        rlCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        rlSelectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
    }

    private void updateToDB(Long id) {
        MyWord edit = MyWord.findById(MyWord.class, id);
        if(edit!= null){
        edit.setKeyword(edWord.getText().toString());
        edit.setSummary(edSummary.getText().toString());
        edit.setPhonetic(edPhonetic.getText().toString());
        edit.setMean(edMean.getText().toString());
        edit.setNote(edNote.getText().toString());
        edit.setImage(DbBitmapUtility.getBytes(bitmap));
        edit.save();
        createListener.changeFragment(1);}
        else {
            saveWordToDB();
        }
    }

    private void saveWordToDB() {
        byte[] image;
        image = DbBitmapUtility.getBytes(bitmap);
        MyWord myWord = new MyWord(edWord.getText().toString(),edPhonetic.getText().toString(), edSummary.getText().toString()
                , edMean.getText().toString(), edNote.getText().toString(), image);
        myWord.save();
        createListener.changeFragment(1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("AAA", resultCode + ":" + requestCode);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            imgPicture.setImageBitmap(bitmap);
        }

        if (requestCode == PICK_IMAGE_REQUEST) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                imgPicture.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void editWord(MyWord myWord) {
        edWord.setText(myWord.getKeyword());
        edNote.setText(myWord.getNote());
        edPhonetic.setText(myWord.getPhonetic());
        edSummary.setText(myWord.getSummary());
        edMean.setText(myWord.getMean());
        bitmap = DbBitmapUtility.getImage(myWord.getImage());
        imgPicture.setImageBitmap(bitmap);
    }
}
