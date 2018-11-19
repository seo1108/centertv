package yonsei_church.yonsei.tv.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import yonsei_church.yonsei.tv.data.TvMenuMainItem;
import yonsei_church.yonsei.tv.data.VideoItem;

public interface APIInterface {
    @GET("tv_menu.php")
    Call<List<TvMenuMainItem>> getTvMenuMainItemList(@Query("mt") String mt);

    @GET("tv_menu_mov.php")
    Call<List<TvMenuMainItem>> getTvMenuMovMainItemList(@Query("mt") String mt);

    @GET("tv_list.php")
    Call<List<VideoItem>> getTvList(@Query("mt") String mt
            , @Query("pg") String pg
            , @Query("key") String key);
   /* List<VideoItem> getTvList(@Query("mt") String mt
            , @Query("page") String page
            , @Query("key") String key);*/


    @GET("tv_list.php")
    String getTvListSync(@Query("mt") String mt
            , @Query("pg") String pg
            , @Query("key") String key);
}
