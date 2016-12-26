package com.linhdx.imagedictionary.View.Activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.linhdx.imagedictionary.Adapter.RowDictAdapter;
import com.linhdx.imagedictionary.R;
import com.linhdx.imagedictionary.entity.Dictionary;
import com.linhdx.imagedictionary.sqlite.SQLiteFactory;
import com.linhdx.imagedictionary.sqlite.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HistoryActivity extends Activity {
    SQLiteHelper dictSQL;
    ListView listHistory;
    List<String> listWord;
    private TextToSpeech textToSpeech;

    @Bind(R.id.imgBack)
    ImageView bar_imgBack;
    @Bind(R.id.tvHeader)
    TextView bar_tvHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initView();
        initTab();
        initData();
    }

    private void initView(){
        listHistory = (ListView)findViewById(R.id.listView);
        dictSQL = (SQLiteHelper) SQLiteFactory.getSQLiteHelper(HistoryActivity.this, "dict.db");
        if (dictSQL.openDatabase("dict.db")) {
            createHistoryTable();
            Toast.makeText(HistoryActivity.this, "Ket noi thanh cong den CSDL", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(HistoryActivity.this, "Khong the ket noi den CSDL", Toast.LENGTH_SHORT).show();
        }
    }
    private void initData(){
        listWord = loadHistory();
        listHistory.setAdapter(new RowDictAdapter(listWord, HistoryActivity.this, HistoryActivity.this));
    }

    private void createHistoryTable() {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS history(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "word TEXT NOT NULL UNIQUE, phonetic TEXT, summary TEXT, mean TEXT, count INTEGER DEFAULT 0)";
        dictSQL.getSQLiteDatabase().execSQL(CREATE_TABLE);
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

    private void initTab(){
        ButterKnife.bind(this);
        bar_imgBack.setVisibility(View.VISIBLE);
        bar_tvHeader.setText(getResources().getString(R.string.history));

        bar_imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryActivity.this, MainActivity.class));
            }
        });
    }

    public void speech(String toSpeak) {
        if (textToSpeech == null) {
           textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
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
