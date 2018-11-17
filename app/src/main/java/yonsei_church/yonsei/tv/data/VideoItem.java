package yonsei_church.yonsei.tv.data;

import com.google.gson.annotations.SerializedName;

public class VideoItem {
    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("title")
    private String title;

    @SerializedName("url")
    private String url;

    @SerializedName("key")
    private String key;

    public String getThumbnail() {
        return thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}