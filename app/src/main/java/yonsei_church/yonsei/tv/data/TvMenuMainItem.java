package yonsei_church.yonsei.tv.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TvMenuMainItem {
    @SerializedName("category")
    private String category;

    @SerializedName("key")
    private String key;

    @SerializedName("livebr")
    private String livebr;

    @SerializedName("items")
    private List<TvMenuSubItem> items;

    public String getCategory() {
        return category;
    }

    public String getKey() {
        return key;
    }

    public String getLivebr() {
        return livebr;
    }

    public List<TvMenuSubItem> getItems() {
        return items;
    }
}
