package yonsei_church.yonsei.tv.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TvMenuListModel extends ResponseModel {
    public List<TvMenuMainItem> getTvMenuMainItemList() {
        return tvMenuMainItemList;
    }

    private List<TvMenuMainItem> tvMenuMainItemList;
}
