package com.linhdx.imagedictionary.entity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.linhdx.imagedictionary.R;
import com.linhdx.imagedictionary.View.Activity.DictActivity;
import com.linhdx.imagedictionary.View.Activity.MainActivity;
import com.linhdx.imagedictionary.View.Activity.MyDictActivity;
import com.linhdx.imagedictionary.View.Interface.IDictionaryHandler;
import com.linhdx.imagedictionary.network.SearchImageAPI;
import com.linhdx.imagedictionary.sqlite.SQLiteFactory;
import com.linhdx.imagedictionary.sqlite.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shine on 31/10/2016.
 */

public class Dictionary implements AdapterView.OnItemClickListener {
    private ImageButton btnVoice;
    AutoCompleteTextView autoSearch;
    private ListView lvHistory;
    private static Dictionary ourInstance = new Dictionary();
    private Context context;
    private Activity activity;
    String  url="";

    SQLiteHelper dictSQL;
    ArrayAdapter<String> adapter, his_adapter;
    List<String> list, listHistory;
    private IDictionaryHandler dictionaryHandler;
    SearchImageAPI searchImageAPI;
    private Word wordSearch;
    ProgressDialog dialog;

    public static Dictionary getInstance() {
        return ourInstance;
    }

    private Dictionary() {
    }

    public Dictionary init(Activity activity) {
        this.context = activity;
        this.activity = activity;
        this.dictionaryHandler = (IDictionaryHandler) activity;
        // init database
        dictSQL = (SQLiteHelper) SQLiteFactory.getSQLiteHelper(context, "dict.db");
        if (dictSQL.openDatabase("dict.db")) {
            createHistoryTable();
            Toast.makeText(context, "Ket noi thanh cong den CSDL", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Khong the ket noi den CSDL", Toast.LENGTH_SHORT).show();
        }

        //init search image
        searchImageAPI = SearchImageAPI.retrofit.create(SearchImageAPI.class);
        return this;
    }

    private void createHistoryTable() {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS history(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "word TEXT NOT NULL UNIQUE, phonetic TEXT, summary TEXT, mean TEXT, count INTEGER DEFAULT 0)";
        dictSQL.getSQLiteDatabase().execSQL(CREATE_TABLE);
    }

    public Dictionary setDefaultUI(ViewGroup container, LayoutInflater inflater) {
        if (container == null) {
            throw new NullPointerException("Container or inflater null");
        }
        if (inflater == null) {
            throw new NullPointerException("Container or inflater null");
        }

        View rootview = inflater.inflate(R.layout.default_dictionary_layout, container, true);
        AutoCompleteTextView auto = (AutoCompleteTextView) rootview.findViewById(R.id.auto_search);
        ImageButton bvoice = (ImageButton) rootview.findViewById(R.id.btn_voice);
        ListView lview = (ListView) rootview.findViewById(R.id.lv_history);
        setAutoView(auto);
        setBtnVoiceView(bvoice);
        setListView(lview);
        initEvent();
        return this;
    }

    private void initEvent() {
        listHistory = loadHistory();

        his_adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_activated_1, listHistory);
        lvHistory.setAdapter(his_adapter);
        lvHistory.setOnItemClickListener(this);

        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, list);
        autoSearch.setAdapter(adapter);
        autoSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                list = dictSQL.getLikeWord("data", "word", autoSearch.getText().toString(), 10);
                adapter.clear();
                adapter.addAll(list);
                adapter.notifyDataSetChanged();
                autoSearch.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        autoSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String txt = parent.getItemAtPosition(position).toString();
                autoSearch.setText(txt);
                autoSearch.selectAll();
                showDialogAndAddToHistory(activity, txt);
                his_adapter.clear();
                his_adapter.addAll(loadHistory());
                his_adapter.notifyDataSetChanged();
            }
        });
    }

    private void setListView(ListView lview) {
        this.lvHistory = lview;
    }

    private void setBtnVoiceView(ImageButton bvoice) {
        this.btnVoice = bvoice;
        bvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dictionaryHandler.onClickSpeechToText();
            }
        });
    }

    private void setAutoView(AutoCompleteTextView auto) {
        this.autoSearch = auto;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SQLiteHelper sqLiteHelper = (SQLiteHelper) SQLiteFactory.getSQLiteHelper(activity, "dict.db");
        Cursor c = sqLiteHelper.getOneRow("data", "word", his_adapter.getItem(position).trim().toLowerCase());

        if (c.moveToNext()) {
            wordSearch = new Word(c.getString(c.getColumnIndex("word"))
                    , " /" + c.getString(c.getColumnIndex("phonetic")) + "/"
                    , c.getString(c.getColumnIndex("summary"))
                    , c.getString(c.getColumnIndex("mean")));
            getImage(his_adapter.getItem(position).trim().toLowerCase());
        }
    }

    public List<String> loadHistory() {
        List<String> tmp = new ArrayList<>();
        Cursor c = dictSQL.queryAll("history", "id", "DESC");
        while (c.moveToNext()) {
            tmp.add(c.getString(1));
        }
        c.close();
        return tmp;
    }

    public void showDialogAndAddToHistory(Activity activity, String txt) {
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

    private void showDialog(Word word, Activity activity, String url) {
        DialogDict.getInstance().showDialog(activity
                , word.getKeyword()
                , word.getPhonetic()
                , word.getSummary()
                , word.getMean()
                , url);

    }
//
//    private void showDialog(String title, Activity activity, String url) {
//        DialogDict.getInstance().showDialog(activity
//                , title
//                , " /" + title + "/"
//                , "Từ khóa không tồn tại"
//                , ""
//                ,"");
//    }


    public void setSearchView(String txt) {
        autoSearch.setText(txt);
        autoSearch.clearFocus();
        showDialogAndAddToHistory(activity, txt);
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

    public void changeActivity(MyWord myWord){
        Intent intent = new Intent(activity, MyDictActivity.class);
        Gson gson = new Gson();
        String j = gson.toJson(myWord);
        intent.putExtra("Object", j);
        activity.startActivity(intent);
    }
}
