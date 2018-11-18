package yonsei_church.yonsei.tv.api;

import com.jaredsburrows.retrofit2.adapter.synchronous.SynchronousCallAdapterFactory;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import yonsei_church.yonsei.tv.app.AppConst;

public class APIClient {
    private static Retrofit retrofit = null;

     public static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(AppConst.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    public static Retrofit getSyncClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(AppConst.DOMAIN)
                .addCallAdapterFactory(SynchronousCallAdapterFactory.create())
                .build();

        return retrofit;
    }

    public static Retrofit getVimeoClient(String vimeoId) {

        retrofit = new Retrofit.Builder()
                .baseUrl("http://player.vimeo.com/video/" + vimeoId + "/config")
                .addCallAdapterFactory(SynchronousCallAdapterFactory.create())
                .build();

        return retrofit;
    }
}
