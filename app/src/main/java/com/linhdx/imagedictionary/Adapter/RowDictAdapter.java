package com.linhdx.imagedictionary.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.linhdx.imagedictionary.R;
import com.linhdx.imagedictionary.View.Activity.HistoryActivity;
import com.linhdx.imagedictionary.entity.DialogDict;
import com.linhdx.imagedictionary.entity.ImageResult;
import com.linhdx.imagedictionary.entity.ImageWrapper;
import com.linhdx.imagedictionary.entity.Word;
import com.linhdx.imagedictionary.network.SearchImageAPI;
import com.linhdx.imagedictionary.sqlite.SQLiteFactory;
import com.linhdx.imagedictionary.sqlite.SQLiteHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shine on 18/12/2016.
 */

public class RowDictAdapter extends BaseAdapter {
    List<String> list;
    private static LayoutInflater layoutInflater = null;
    Context context;
    Activity activity;
    private Word wordSearch;
    SearchImageAPI searchImageAPI;
    String  url="";
    ProgressDialog dialog;
    public RowDictAdapter(List<String> list, Context context, Activity activity) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        searchImageAPI = SearchImageAPI.retrofit.create(SearchImageAPI.class);
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
        ImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder= new Holder();
        View rowView;
        rowView = layoutInflater.inflate(R.layout.mydic_row_layout, null);
        holder.tv = (TextView)rowView.findViewById(R.id.textView1);
        holder.img = (ImageView)rowView.findViewById(R.id.imageView1);
        holder.tv.setText(list.get(position));
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HistoryActivity)context).speech(list.get(position));
            }
        });

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSQLDialog(activity, list.get(position));
            }
        });
        return rowView;
    }

    private void openSQLDialog(Activity activity, String txt){
        try {
            SQLiteHelper sqLiteHelper = (SQLiteHelper) SQLiteFactory.getSQLiteHelper(activity, "dict.db");
            Cursor c = sqLiteHelper.getOneRow("data", "word", txt.trim().toLowerCase());

            if (c.moveToNext()) {
                wordSearch = new Word(c.getString(c.getColumnIndex("word"))
                        , " /" + c.getString(c.getColumnIndex("phonetic")) + "/"
                        , c.getString(c.getColumnIndex("summary"))
                        , c.getString(c.getColumnIndex("mean")));
                getImage(wordSearch.getKeyword());
                ContentValues cv = new ContentValues();
                cv.put("word", wordSearch.getKeyword());
                cv.put("phonetic", wordSearch.getPhonetic());
                cv.put("summary", wordSearch.getSummary());
                cv.put("mean", wordSearch.getMean());
                sqLiteHelper.insert("history", cv);
            } else {
                wordSearch = new Word(c.getString(c.getColumnIndex("word"))
                        , " /" + c.getString(c.getColumnIndex("phonetic")) + "/"
                        , c.getString(c.getColumnIndex("summary"))
                        , c.getString(c.getColumnIndex("mean")));
                getImage(wordSearch.getKeyword());
            }
        } catch (SQLException e) {

        }
    }
    public String getImage(String keyWord){
        dialog = new ProgressDialog(context);
        dialog.show();
        Call<ImageWrapper> call = searchImageAPI.getImageResult(keyWord, 1, 0, "en-us","Moderate");
        call.enqueue(new Callback<ImageWrapper>() {
            @Override
            public void onResponse(Call<ImageWrapper> call, Response<ImageWrapper> response) {
                for(ImageResult item: response.body().getImageResults()) {
                    url= item.getThumbnailUrl();
                    dialog.dismiss();
                    showDialog(wordSearch, activity, url);
                }
            }

            @Override
            public void onFailure(Call<ImageWrapper> call, Throwable t) {
                Log.d("Result", "failed");
            }
        });
        return  url;
    }

    private void showDialog(Word word, Activity activity, String url) {
        DialogDict.getInstance().showDialog(activity
                , word.getKeyword()
                , word.getPhonetic()
                , word.getSummary()
                , word.getMean()
                , url);
    }
}
