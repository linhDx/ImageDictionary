package com.linhdx.imagedictionary.network;

import com.linhdx.imagedictionary.Const;
import com.linhdx.imagedictionary.entity.ImageResult;
import com.linhdx.imagedictionary.entity.ImageWrapper;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchImageAPI {
    OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder ongoing = chain.request().newBuilder();
            ongoing.addHeader("Ocp-Apim-Subscription-Key", Const.HEADER);
            return chain.proceed(ongoing.build());
        }
    }).build();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Const.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build();

    @GET(Const.BASE_URL)
    Call<ImageWrapper> getImageResult(@Query("q") String keyWord, @Query("count") int count, @Query("offset") int offset,
                                      @Query("mkt") String mkt, @Query("safeSearch") String safeSearch);

}
