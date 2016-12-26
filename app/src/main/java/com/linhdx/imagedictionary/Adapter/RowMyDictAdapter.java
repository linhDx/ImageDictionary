package com.linhdx.imagedictionary.Adapter;

import android.app.Activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linhdx.imagedictionary.R;
import com.linhdx.imagedictionary.View.Activity.HistoryActivity;
import com.linhdx.imagedictionary.View.Fragment.MyDictFragmet;
import com.linhdx.imagedictionary.entity.MyWord;
import com.linhdx.imagedictionary.sqlite.SQLiteFactory;
import com.linhdx.imagedictionary.sqlite.SQLiteHelper;


import java.util.List;
import java.util.Locale;


/**
 * Created by shine on 22/12/2016.
 */

public class RowMyDictAdapter extends BaseAdapter {
    List<String> list;
    private static LayoutInflater layoutInflater = null;
    Context context;
    Activity activity;
    private boolean isEditmode;
    private TextToSpeech textToSpeech;
    private MyDictFragmet myDictFragmet;

    public RowMyDictAdapter(List<String> list, Context context, Activity activity, MyDictFragmet myDictFragmet) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        this.myDictFragmet = myDictFragmet;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public class Holder{
        TextView tv;
        ImageView img1;
        ImageView img2;

        private void editmode(){
            img2.setVisibility(View.VISIBLE);
        }
        private void normalmode(){
            img2.setVisibility(View.GONE);
        }
    }
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        Holder holder= new Holder();
        View rowView = convertView;
        if(rowView == null) {
            rowView = layoutInflater.inflate(R.layout.mydict2_row_layout, null);
            holder.tv = (TextView) rowView.findViewById(R.id.textView1);
            holder.img1 = (ImageView) rowView.findViewById(R.id.imageView1);
            holder.img2 = (ImageView) rowView.findViewById(R.id.imageView2);
            rowView.setTag(holder);
        } else {
            holder = (Holder) rowView.getTag();
            if(isEditmode){
                holder.editmode();
            } else {
                holder.normalmode();
            }

        }
        holder.tv.setText(list.get(position));
        holder.img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speech(list.get(position));
            }
        });

        holder.img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDictFragmet.delete(position);
            }
        });
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditmode){
                    myDictFragmet.editMyWord(position);
                } else {
                    myDictFragmet.showMyWord(position);
                }
            }
        });
        return rowView;
    }

    public void setEditmode(boolean editmode) {
        isEditmode = editmode;
    }

    public void speech(String toSpeak) {
        if (textToSpeech == null) {
            textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
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
