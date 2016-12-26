package com.linhdx.imagedictionary.View.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.linhdx.imagedictionary.R;
import com.linhdx.imagedictionary.View.Interface.CreateListener;
import com.linhdx.imagedictionary.entity.MyWord;
import com.linhdx.imagedictionary.util.DbBitmapUtility;

import java.util.Locale;

/**
 * Created by shine on 25/12/2016.
 */

public class ShowWordFragment extends Fragment {

    private MyWord myEditWord;
    private ImageButton imgBack, imgSpeake;
    TextView tvTitle,tvPhonetic, tvSummaryy, tvMean, tvNote;
    ImageView imgWord;
    private CreateListener createListener;
    private TextToSpeech textToSpeech;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if( bundle!= null && bundle.getString("word")!= null){
            Gson gson = new Gson();
            myEditWord = gson.fromJson(bundle.getString("word"), MyWord.class);
        }
        return inflater.inflate(R.layout.myword_item_fragment,container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
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

    private void initView(View view){
        imgBack= (ImageButton)view.findViewById(R.id.dict_dialog_back);
        imgSpeake= (ImageButton)view.findViewById(R.id.ib_play);
        tvTitle = (TextView)view.findViewById(R.id.tv_title);
        tvPhonetic = (TextView)view.findViewById(R.id.tv_phonetic);
        tvSummaryy = (TextView)view.findViewById(R.id.tv_summary);
        tvMean = (TextView)view.findViewById(R.id.tv_mean);
        tvNote = (TextView)view.findViewById(R.id.tv_note);
        imgWord = (ImageView) view.findViewById(R.id.img_word);
    }

    private void initData(){
        if(myEditWord!= null && myEditWord.getKeyword() != null){
            tvTitle.setText(myEditWord.getKeyword().toString());
            tvPhonetic.setText(myEditWord.getPhonetic().toString());
            tvSummaryy.setText(myEditWord.getSummary().toString());
            tvMean.setText(myEditWord.getMean().toString());
            tvNote.setText(myEditWord.getNote().toString());
            imgWord.setImageBitmap(DbBitmapUtility.getImage(myEditWord.getImage()));
        }
    }

    private void initListener(){
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createListener.changeFragment(1);
            }
        });

        imgSpeake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speech(tvTitle.getText().toString());
            }
        });
    }

    public void speech(String toSpeak) {
        if (textToSpeech == null) {
            textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status!= TextToSpeech.ERROR){
                        textToSpeech.setLanguage(Locale.US);
                    }
                }
            });
        }
        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }
}
