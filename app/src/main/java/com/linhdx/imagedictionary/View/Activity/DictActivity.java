package com.linhdx.imagedictionary.View.Activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.linhdx.imagedictionary.R;
import com.linhdx.imagedictionary.View.Interface.IDictionaryHandler;
import com.linhdx.imagedictionary.entity.Dictionary;


import java.util.ArrayList;
import java.util.Locale;


/**
 * Created by shine on 31/10/2016.
 */

public class DictActivity extends Activity implements IDictionaryHandler {
    private final int REQ_CODE_SPEECH_INPUT = 1;
    FrameLayout dict_container;
    private Dictionary dict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dict);
        dict_container = (FrameLayout)findViewById(R.id.dict_container);
        dict= Dictionary.getInstance().init(this).setDefaultUI(dict_container, getLayoutInflater());
    }

    @Override
    public void onClickSpeechToText() {
        promptSpeechInput();
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say something!!");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn\'t support speech input",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK){
            if(null != data){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                dict.setSearchView(result.get(0));
            }
        }
    }
}
