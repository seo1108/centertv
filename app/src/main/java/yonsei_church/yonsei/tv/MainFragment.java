/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package yonsei_church.yonsei.tv;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yonsei_church.yonsei.tv.api.APIClient;
import yonsei_church.yonsei.tv.api.APIInterface;
import yonsei_church.yonsei.tv.data.TvMenuListModel;
import yonsei_church.yonsei.tv.data.TvMenuMainItem;
import yonsei_church.yonsei.tv.data.TvMenuSubItem;
import yonsei_church.yonsei.tv.data.VideoItem;

public class MainFragment extends BrowseFragment {
    private static final String TAG = "MainFragment";

    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private static final int GRID_ITEM_WIDTH = 200;
    private static final int GRID_ITEM_HEIGHT = 200;
    private static final int NUM_ROWS = 6;
    private static final int NUM_COLS = 15;

    private final Handler mHandler = new Handler();
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private String mBackgroundUri;
    private BackgroundManager mBackgroundManager;

    APIInterface apiInterface;
    List<TvMenuMainItem> mTvMenuMainItemList;
    TvMenuListModel mTvMenuListModel;
    List<TvMenuMainItem> menuList = null;
    List<TvMenuSubItem> subMenuList = null;
    List<VideoItem> videoList = null;

    ArrayObjectAdapter rowsAdapter;
    ArrayObjectAdapter updateRowAdapter;
    private String mKey;
    private String mPage;
/*    ArrayObjectAdapter rowsAdapter;
    CardTVPresenter cardPresenter;
    ArrayObjectAdapter cardRowAdapter;
    HeaderItem gridItemPresenterHeader;*/

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);

        prepareBackgroundManager();

        setupUIElements();

        requestTvMenu("");



        setupEventListeners();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBackgroundTimer) {
            Log.d(TAG, "onDestroy: " + mBackgroundTimer.toString());
            mBackgroundTimer.cancel();
        }
    }

    private void loadRows() {
        List<Movie> list = MovieList.setupMovies();

        ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        CardPresenter cardPresenter = new CardPresenter();

        int i;
        for (i = 0; i < NUM_ROWS; i++) {
            if (i != 0) {
                Collections.shuffle(list);
            }
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
            for (int j = 0; j < NUM_COLS; j++) {
                listRowAdapter.add(list.get(j % 5));
            }
            HeaderItem header = new HeaderItem(i, MovieList.MOVIE_CATEGORY[i]);
            rowsAdapter.add(new ListRow(header, listRowAdapter));
        }

        HeaderItem gridHeader = new HeaderItem(i, "PREFERENCES");

        GridItemPresenter mGridPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
        gridRowAdapter.add(getResources().getString(R.string.grid_view));
        gridRowAdapter.add(getString(R.string.error_fragment));
        gridRowAdapter.add(getResources().getString(R.string.personal_settings));
        rowsAdapter.add(new ListRow(gridHeader, gridRowAdapter));

        setAdapter(rowsAdapter);
    }

    private void prepareBackgroundManager() {

        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());

        mDefaultBackground = ContextCompat.getDrawable(getActivity(), R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupUIElements() {
        setBadgeDrawable(getActivity().getResources().getDrawable(
                R.drawable.ic_launcher));
        setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(ContextCompat.getColor(getActivity(), R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(ContextCompat.getColor(getActivity(), R.color.search_opaque));
    }

    private void setupEventListeners() {
        setOnSearchClickedListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Implement your own in-app search", Toast.LENGTH_LONG)
                        .show();
            }
        });

        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    private void updateBackground(String uri) {
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;
        Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .error(mDefaultBackground)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        mBackgroundManager.setDrawable(resource);
                    }
                });
        mBackgroundTimer.cancel();
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof VideoItem) {
                VideoItem movie = (VideoItem) item;
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.MOVIE, movie);

                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        DetailsActivity.SHARED_ELEMENT_NAME)
                        .toBundle();
                getActivity().startActivity(intent, bundle);
            } else if (item instanceof String) {
                if (((String) item).contains(getString(R.string.error_fragment))) {
                    Intent intent = new Intent(getActivity(), BrowseErrorActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), ((String) item), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(
                Presenter.ViewHolder itemViewHolder,
                Object item,
                RowPresenter.ViewHolder rowViewHolder,
                Row row) {
            if (item instanceof VideoItem) {
                final ListRow listRow = (ListRow) row;
                updateRowAdapter = (ArrayObjectAdapter) listRow.getAdapter();
                int selectedIndex = updateRowAdapter.indexOf(item);
                if (selectedIndex != -1 && (updateRowAdapter.size() - 1) == selectedIndex ) {
                    // The last item was selected
                    mKey = ((VideoItem) item).getKey();
                    mPage =  ((VideoItem) item).getPage();
                    Message msg = getTvListHandler.obtainMessage();
                    getTvListHandler.sendMessage(msg);
                }
            }
        }
    }

    final Handler getTvListHandler = new Handler() {
        public void handleMessage(Message msg) {
            final int curPage = Integer.parseInt(mPage) + 1;
            apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<List<VideoItem>> call = apiInterface.getTvList("010535483624931", curPage+"", mKey);
            call.enqueue(new retrofit2.Callback<List<VideoItem>>() {
                @Override
                public void onResponse(Call<List<VideoItem>> call, Response<List<VideoItem>> response) {
                    videoList = response.body();
                    for (int i = 0; i < videoList.size(); i++) {
                        updateRowAdapter.add(videoList.get(i));
                        videoList.get(i).setKey(mKey);
                        videoList.get(i).setPage(curPage+"");
                    }

                }
                @Override
                public void onFailure(Call<List<VideoItem>> call, Throwable t) {

                }
            });
        }
    };

    private class UpdateBackgroundTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateBackground(mBackgroundUri);
                }
            });
        }
    }

    private class GridItemPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackgroundColor(
                    ContextCompat.getColor(getActivity(), R.color.default_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ((TextView) viewHolder.view).setText((String) item);
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
        }
    }

    private void setCategories(List<TvMenuMainItem> list)  {
//        List<Movie> list = MovieList.setupMovies();

        ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        CardTVPresenter cardPresenter = new CardTVPresenter();

        int i;
        for (i = 0; i < list.size(); i++) {
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
           /* for (int j = 0; j < NUM_COLS; j++) {
                listRowAdapter.add(list.get(j % 5));
            }*/
            HeaderItem header = new HeaderItem(i, list.get(i).getCategory());
            rowsAdapter.add(new ListRow(header, listRowAdapter));
        }

        HeaderItem gridHeader = new HeaderItem(i, "PREFERENCES");

        /*GridItemPresenter mGridPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
        gridRowAdapter.add(getResources().getString(R.string.grid_view));
        gridRowAdapter.add(getString(R.string.error_fragment));
        gridRowAdapter.add(getResources().getString(R.string.personal_settings));
        rowsAdapter.add(new ListRow(gridHeader, gridRowAdapter)); */

        setAdapter(rowsAdapter);
    }

    public void requestTvMenu(String mt) {
        mt = "010535483624931";

        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<TvMenuMainItem>> call = apiInterface.getTvMenuMainItemList(mt);
        call.enqueue(new Callback<List<TvMenuMainItem>>() {
            @Override
            public void onResponse(Call<List<TvMenuMainItem>> call, Response<List<TvMenuMainItem>> response) {
                menuList = response.body();

                loadTVRows();
            }
            @Override
            public void onFailure(Call<List<TvMenuMainItem>> call, Throwable t) {

            }
        });
    }

    public List<VideoItem> requestVideo(String mt, String page, String key) {
        mt = "010535483624931";

        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<VideoItem>> call = apiInterface.getTvList(mt, page, key);
        call.enqueue(new Callback<List<VideoItem>>() {
            @Override
            public void onResponse(Call<List<VideoItem>> call, Response<List<VideoItem>> response) {
                videoList = response.body();

                loadTVRows();
            }
            @Override
            public void onFailure(Call<List<VideoItem>> call, Throwable t) {

            }
        });

        return videoList;
    }

    public void requestVideoByCategory(final String mt, final String page, final String key) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                apiInterface = APIClient.getSyncClient().create(APIInterface.class);
                //List<VideoItem> list = apiInterface.getTvListSycn(mt, page, key);
                /*apiInterface = APIClient.getClient().create(APIInterface.class);
                Call<List<VideoItem>> call = apiInterface.getTvList(mt, page, key);

                try {
                    videoList = call.execute().body();
                    return "done";
                } catch (IOException e) {
                    e.printStackTrace();
                }
*/
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }.execute();

    }

    private void loadTVRows() {
        rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        CardTVPresenter cardPresenter = new CardTVPresenter();
        //ArrayObjectAdapter cardRowAdapter = new ArrayObjectAdapter(cardPresenter);

        int headerIdx = 0;
        for (int i = 0; i < menuList.size(); i++) {
            HeaderItem header = new HeaderItem(headerIdx, menuList.get(i).getCategory());
            ArrayObjectAdapter cardRowAdapter = new ArrayObjectAdapter(cardPresenter);
            //PaginationAdapter cardRowAdapter = new PaginationAdapter(getContext(), cardPresenter);
            rowsAdapter.add(new ListRow(header, cardRowAdapter));
            headerIdx++;

            /*if (headerIdx > 3) break;*/

            if (null != menuList.get(i).getItems() && menuList.get(i).getItems().size() > 0) {
                for (int j = 0; j < menuList.get(i).getItems().size(); j++) {
                    subMenuList =  menuList.get(i).getItems();
                    final HeaderItem subHeader = new HeaderItem(headerIdx, "    " + subMenuList.get(j).getCategory2());
                    headerIdx++;
                    try {
                        final String key2 = subMenuList.get(j).getKey2();
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    apiInterface = APIClient.getClient().create(APIInterface.class);
                                    Call<List<VideoItem>> call = apiInterface.getTvList("010535483624931", "1", key2);
                                    List<VideoItem> result = call.execute().body();
                                    CardTVPresenter cardPresenter = new CardTVPresenter();

                                    ArrayObjectAdapter cardSubRowAdapter = new ArrayObjectAdapter(cardPresenter);
                                    if (null != result && result.size() > 0) {
                                        for (int k = 0; k < result.size(); k++) {
                                            cardSubRowAdapter.add(result.get(k));
                                            result.get(k).setKey(key2);
                                            result.get(k).setPage("1");
                                        }


                                        //new NetworkCall().execute(call);
                                        rowsAdapter.add(new ListRow(subHeader, cardSubRowAdapter));
                                    }
                                } catch (Exception e) {

                                    e.printStackTrace();
                                } finally {

                                }
                            }
                        });

                        t.start();

                        try {
                            t.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
            }
        }

        setAdapter(rowsAdapter);
    }

    public void addAllItems(List<?> items) {

    }


    public List<?> getAllItems() {
        return null;
    }

    private class NetworkCall extends AsyncTask<Call, Void, String> {

        @Override
        protected String doInBackground(Call[] params) {
            try {
                Call<List<VideoItem>> call = params[0];
                Response<List<VideoItem>> response = call.execute();
                videoList = response.body();
                return response.body().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }



}
