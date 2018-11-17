package yonsei_church.yonsei.tv.data;

import com.google.gson.annotations.SerializedName;

public class TvMenuSubItem {
    @SerializedName("category2")
    String category2;

    @SerializedName("key2")
    String key2;

    public String getCategory2() {
        return category2;
    }

    public String getKey2() {
        return key2;
    }
}
