package yonsei_church.yonsei.tv.api;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;
import retrofit2.Call;
import yonsei_church.yonsei.tv.data.TvMenuListModel;
import yonsei_church.yonsei.tv.data.TvMenuMainItem;
import yonsei_church.yonsei.tv.data.TvMenuModel;
import yonsei_church.yonsei.tv.data.VideoItem;

public interface CommonAPI {
    /*@GET("/tv_menu.php")
    void tv_menu(@Query("mt") String mt);*/

    @GET("/tv_menu.php")
    Call<List<TvMenuMainItem>> getTvMenuMainItem();

    @GET("/tv_list.php")
    Call<List<VideoItem>> getTvList();

    @GET("/tv_menu_mov.php")
    Call<List<TvMenuMainItem>> getTvMenuMovMainItem();
}
