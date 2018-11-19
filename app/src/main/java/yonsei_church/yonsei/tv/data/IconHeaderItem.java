package yonsei_church.yonsei.tv.data;

import android.support.v17.leanback.widget.HeaderItem;

public class IconHeaderItem extends HeaderItem {


    private static final String TAG = IconHeaderItem.class.getSimpleName();
    public static final int ICON_NONE = -1;

    /** Hold an icon resource id */
    private int mIconResId = ICON_NONE;

    private int mTextSize;

    public IconHeaderItem(long id, String name, int iconResId, int textSize) {
        super(id, name);
        mIconResId = iconResId;
        mTextSize = textSize;
    }

    public IconHeaderItem(long id, String name) {
        this(id, name, ICON_NONE, 10);
    }

    public IconHeaderItem(String name) {
        super(name);
    }

    public int getIconResId() {
        return mIconResId;
    }

    public void setIconResId(int iconResId) {
        this.mIconResId = iconResId;
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
    }
}