package yonsei_church.yonsei.tv.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TvMenuMovSubItem {
    @SerializedName("category2")
    String category2;

    @SerializedName("key2")
    String key2;

    @SerializedName("videos")
    private List<VideoItem> videos;

    public String getCategory2() {
        return category2;
    }

    public String getKey2() {
        return key2;
    }

    public List<VideoItem> getVideos() {
        return videos;
    }
}
