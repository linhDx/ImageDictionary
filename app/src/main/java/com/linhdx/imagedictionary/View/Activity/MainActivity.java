package com.linhdx.imagedictionary.View.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import com.linhdx.imagedictionary.R;
import com.linhdx.imagedictionary.entity.ImageResult;
import com.linhdx.imagedictionary.entity.ImageWrapper;
import com.linhdx.imagedictionary.network.SearchImageAPI;
import com.linhdx.imagedictionary.sqlite.SQLiteFactory;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity {

    SearchImageAPI searchImageAPI;

    @Bind(R.id.bnt_dict)
    Button bntDict;
    @Bind(R.id.bnt_quiz)
    Button bntQuiz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SQLiteFactory.getSQLiteHelper(getApplicationContext(), "dict.db").openDatabase("dict.db");
        ButterKnife.bind(this);
        searchImageAPI = SearchImageAPI.retrofit.create(SearchImageAPI.class);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @OnClick(R.id.bnt_dict)
    public void openDictActivity(){
        Log.d("AAA", "click dict");
        startActivity(new Intent(getApplicationContext(), DictActivity.class));
    }

    @OnClick(R.id.bnt_quiz)
    public void getImage(){
        Call<ImageWrapper> call = searchImageAPI.getImageResult("flower", 2, 0, "en-us","Moderate");
        call.enqueue(new Callback<ImageWrapper>() {
            @Override
            public void onResponse(Call<ImageWrapper> call, Response<ImageWrapper> response) {
                for(ImageResult item: response.body().getImageResults()) {
                    Log.d("Result", item.getThumbnailUrl() + ";\n ");
                }
            }

            @Override
            public void onFailure(Call<ImageWrapper> call, Throwable t) {
                Log.d("Result", "failed");
            }
        });
    }
}
