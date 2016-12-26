package com.linhdx.imagedictionary.entity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.linhdx.imagedictionary.R;
import com.linhdx.imagedictionary.View.Activity.MyDictActivity;
import com.linhdx.imagedictionary.util.DbBitmapUtility;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

/**
 * Created by shine on 31/10/2016.
 */

public class DialogDict {
    private static final DialogDict ourInstance = new DialogDict();
    private TextToSpeech textToSpeech;

    public static DialogDict getInstance() {
        return ourInstance;
    }

    private DialogDict() {
    }

    /**
     * Show a material diaog using materiadialog library
     *
     * @param activity: context of this
     */
    public void showDialog(final Activity activity, final String title, final String phonetic, final String summary, final String mean, String url) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dict_dialog);
        ((TextView) dialog.findViewById(R.id.tv_title)).setText(title);
        ((TextView) dialog.findViewById(R.id.tv_phonetic)).setText(phonetic);
        ((TextView) dialog.findViewById(R.id.tv_summary)).setText(summary);
        ((TextView) dialog.findViewById(R.id.tv_mean)).setText(mean.replaceAll("&quot;", "\""));
        final ImageView imageView = (ImageView) dialog.findViewById(R.id.img_word);
        ImageButton imgback = (ImageButton) dialog.findViewById(R.id.dict_dialog_back);
        ImageButton imgAdd = (ImageButton) dialog.findViewById(R.id.ib_add);

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setDrawingCacheEnabled(true);
                imageView.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageInByte = baos.toByteArray();
                MyWord myWord = new MyWord(title, phonetic, summary, mean,"", imageInByte);
                Dictionary.getInstance().changeActivity(myWord);
            }
        });
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Picasso.with(activity).load(url).placeholder(R.drawable.ic_clear).error(R.drawable.ic_mic).into(imageView);
        dialog.findViewById(R.id.ib_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textToSpeech == null) {
                    textToSpeech = new TextToSpeech(activity.getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status != TextToSpeech.ERROR) {
                                textToSpeech.setLanguage(Locale.US);
                            }
                        }
                    });
                }
                textToSpeech.speak(title, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
    }

    private void saveToMyList(){

    }
}
