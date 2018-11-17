package yonsei_church.yonsei.tv.adapter;

import android.content.Context;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yonsei_church.yonsei.tv.CardTVPresenter;

public abstract class PaginationAdapter extends ArrayObjectAdapter {

    public static final String KEY_TAG = "tag";
    public static final String KEY_ANCHOR = "anchor";
    public static final String KEY_NEXT_PAGE = "next_page";

    private Context mContext;
    private Integer mNextPage;
    private Presenter mPresenter;

    private String mRowTag;
    private String mAnchor;
    private int mLoadingIndicatorPosition;


    public PaginationAdapter(Context context, CardTVPresenter presenter) {
        mContext = context;
        mPresenter = presenter;
        mLoadingIndicatorPosition = -1;
        mNextPage = 1;

        setPresenterSelector();
    }

    public void setTag(String tag) {
        mRowTag = tag;
    }

    public void setNextPage(int page) {
        mNextPage = page;
    }

    public void setPresenterSelector() {
        setPresenterSelector(new PresenterSelector() {
            @Override
            public Presenter getPresenter(Object item) {
               /* if (item instanceof Presenter) {
                    return mLoadingPresenter;
                } else if (item instanceof Option) {
                    return mIconItemPresenter;
                } */
                return mPresenter;
            }
        });
    }

    public List<Object> getItems() {
        return unmodifiableList();
    }

    public boolean shouldShowLoadingIndicator() {
        return mLoadingIndicatorPosition == -1;
    }



    public void removeLoadingIndicator() {
        removeItems(mLoadingIndicatorPosition, 1);
        notifyItemRangeRemoved(mLoadingIndicatorPosition, 1);
        mLoadingIndicatorPosition = -1;
    }

    public void setAnchor(String anchor) {
        mAnchor = anchor;
    }

    public void addPosts(List<?> posts) {
        if (posts.size() > 0) {
            addAll(size(), posts);
        } else {
            mNextPage = 0;
        }
    }

    public boolean shouldLoadNextPage() {
        return shouldShowLoadingIndicator() && mNextPage != 0;
    }

    public Map<String, String> getAdapterOptions() {
        Map<String, String> map = new HashMap<>();
        map.put(KEY_TAG, mRowTag);
        map.put(KEY_ANCHOR, mAnchor);
        map.put(KEY_NEXT_PAGE, String.valueOf(mNextPage.toString()));
        return map;
    }


    public abstract void addAllItems(List<?> items);

    public abstract List<?> getAllItems();


}