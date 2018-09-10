package com.lumere.quickhn.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lumere.quickhn.R;
import com.lumere.quickhn.data.model.BoardType;
import com.lumere.quickhn.data.model.Item;
import com.lumere.quickhn.data.model.ItemType;
import com.lumere.quickhn.network.VolleyManager;
import com.lumere.quickhn.ui.adapter.CommentListAdapter;
import com.lumere.quickhn.ui.adapter.ItemListAdapter;
import com.lumere.quickhn.ui.fragment.CommentsFragment;
import com.lumere.quickhn.ui.fragment.ItemListFragment;

import org.json.JSONException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.val;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String[] boards;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private static final int secInWeek = 60 * 60 * 24 * 7;
    private static final int secInDay = 60 * 60 * 24;
    private static final int secInHour = 60 * 60;
    private static final int secInMin = 60;

    @BindInt(R.integer.itemCount)
    protected int itemCount;

    /*
    @BindView(R.id.itemList)
    protected RecyclerView mRecyclerView;
*/
    @BindView(R.id.boards_select)
    Spinner boardsSpinner;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        boards = getResources().getStringArray(R.array.boards);

        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.boards,
                        android.R.layout.simple_spinner_item);

        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        boardsSpinner.setAdapter(staticAdapter);


        boardsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch ((String) parent.getItemAtPosition(position)) {
                    case "TOP":
                        showBoard(BoardType.TOP);
                        break;
                    case "NEW":
                        showBoard(BoardType.NEW);
                        break;
                    case "SHOW":
                        showBoard(BoardType.SHOW);
                        break;
                    case "ASK":
                        showBoard(BoardType.ASK);
                        break;
                    case "BEST":
                        showBoard(BoardType.BEST);
                        break;
                    case "JOB":
                        showBoard(BoardType.JOB);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void showBoard(BoardType boardType) {
        getItemIds(boardType, ids ->
                getItemsFromIdArray(ids, items -> {
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("items", (ArrayList<? extends Parcelable>) items);

                    ItemListFragment itemListFragment = new ItemListFragment();
                    itemListFragment.setArguments(bundle);

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, itemListFragment, "itemListFragment");
                    ft.commit();
                }));
    }

    public void showComments(Item item) {
        Bundle bundle = new Bundle();
        //bundle.putParcelableArrayList("list", myList);

        CommentsFragment commentsFragment = new CommentsFragment();
        commentsFragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, commentsFragment, "commentsFragment");
        ft.commit();

        /*
        getItemsFromIdArray(commentIds, items -> {
            mAdapter = new CommentListAdapter(items);
            mRecyclerView.setAdapter(mAdapter);
        });
        */
    }

    public interface ObjectCallback<T> {
        void onSuccess(T object);
    }

    public interface ListCallback<T> {
        void onSuccess(List<T> items);
    }

    private void getItemFromId(long id, final ObjectCallback<Item> callback) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                "https://hacker-news.firebaseio.com/v0/item/" + String.valueOf(id) + ".json",
                null,
                response -> {
                    try {
                        Item item = new Item();

                        if (response.has("by")) {
                            item.setBy(response.getString("by"));
                        }
                        if (response.has("descendants")) {
                            item.setDescendants(response.getInt("descendants"));
                        }
                        if (response.has("score")) {
                            item.setScore(response.getInt("score"));
                        }
                        if (response.has("time")) {
                            long posted = response.getLong("time");
                            long now = System.currentTimeMillis() / 1000;

                            long timeElapsed = now - posted;

                            int weeks = (int) ((timeElapsed / secInWeek) % 7);
                            int days = (int) ((timeElapsed / secInDay) % 24);
                            int hours = (int) ((timeElapsed / secInHour) % 60);
                            int minutes = (int) ((timeElapsed / secInMin) % 60);

                            item.setCreationTime(response.getLong("time"));

                            item.setElapsedTime(hours + "h " + minutes + "m");
                        }
                        if (response.has("title")) {
                            item.setTitle(response.getString("title"));
                        }
                        if (response.has("text")) {
                            item.setText(response.getString("text"));
                        }
                        if (response.has("type")) {
                            String type = response.getString("type");
                            switch (type.toLowerCase()) {
                                case "job":
                                    item.setType(ItemType.JOB);
                                    break;
                                case "story":
                                    item.setType(ItemType.STORY);
                                    break;
                                case "comment":
                                    item.setType(ItemType.COMMENT);
                                    break;
                                case "poll":
                                    item.setType(ItemType.POLL);
                                    break;
                                case "pollopt":
                                    item.setType(ItemType.POLLOPT);
                                    break;
                                default:
                                    break;
                            }
                        }
                        if (response.has("url")) {
                            URI uri = null;
                            try {
                                uri = new URI(response.getString("url"));
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                            //item.setUri(uri);
                        }
                        callback.onSuccess(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("error", error.toString()));

        VolleyManager.getInstance(MainActivity.this).addToRequestQueue(request);
    }

    private void getItemsFromIdArray(List<Long> idList, ListCallback<Item> listCallback) {
        List<Item> itemList = new ArrayList<>();

        CountDownLatch countDownLatch = new CountDownLatch(itemCount);

        for (int i = 0; i < itemCount; i++) {
            getItemFromId(idList.get(i), item -> {
                itemList.add(item);
                countDownLatch.countDown();
            });
        }

        final Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.post(() -> listCallback.onSuccess(itemList));
        }).start();
    }

    private void getItemIds(BoardType boardType, final ListCallback<Long> listCallback) {
        String url = "";

        switch (boardType) {
            case TOP:
                url = "https://hacker-news.firebaseio.com/v0/topstories.json";
                break;
            case NEW:
                url = "https://hacker-news.firebaseio.com/v0/newstories.json";
                break;
            case SHOW:
                url = "https://hacker-news.firebaseio.com/v0/showstories.json";
                break;
            case ASK:
                url = "https://hacker-news.firebaseio.com/v0/askstories.json";
                break;
            case BEST:
                url = "https://hacker-news.firebaseio.com/v0/beststories.json";
                break;
            case JOB:
                url = "https://hacker-news.firebaseio.com/v0/jobstories.json";
                break;
        }

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    List<Long> itemIds = new ArrayList<>();
                    for (int i = 0; i < response.length() - 1; i++) {
                        try {
                            itemIds.add(response.getLong(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    listCallback.onSuccess(itemIds);
                },
                error -> Log.d("error", error.toString()));

        VolleyManager.getInstance(MainActivity.this).addToRequestQueue(request);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}