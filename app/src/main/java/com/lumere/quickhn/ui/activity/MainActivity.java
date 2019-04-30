package com.lumere.quickhn.ui.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lumere.quickhn.R;
import com.lumere.quickhn.data.model.BoardType;
import com.lumere.quickhn.data.model.Item;
import com.lumere.quickhn.data.model.ItemType;
import com.lumere.quickhn.network.VolleyManager;
import com.lumere.quickhn.ui.fragment.CommentsFragment;
import com.lumere.quickhn.ui.fragment.ItemListFragment;
import com.lumere.quickhn.ui.fragment.TreeViewFragment;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String[] boards;

    private static final int secInWeek = 604800;
    private static final int secInDay = 86400;
    private static final int secInHour = 3600;
    private static final int secInMin = 60;

    @BindInt(R.integer.itemCount)
    protected int maxItems;

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

    int read = 0;

    Map<Item, List<Item>> list = new Hashtable<Item, List<Item>>();

    public void addToListItem(Item item, Item itemToAdd) {
        if (list.get(item) == null) {
            list.put(item, new ArrayList<>());
        }

        List<Item> items = list.get(item);
        items.add(itemToAdd);

        list.put(item, items);

    }

    public void startRead(long rootId, final ObjectCallback<Map<Item, List<Item>>> callback) {
        if (read == 25) {
            callback.onSuccess(list);
        }
        getItemFromId(rootId, (Item item) -> {
            if (item.getChildren() != null && !item.getChildren().isEmpty()) {

                for (String childId : item.getChildren()) {
                    if (!childId.equals(item.getId())) {
                        getItemFromId(Long.parseLong(childId), (childItem) -> {

                            Log.d("myLogger2", "parent: " + item.getId());
                            Log.d("myLogger2", "child: " + childId);
                            addToListItem(item, childItem);
                            addToListItem(childItem, item);

                            item.addKid(childItem);
                            childItem.setParent(item);

                            read++;
                            startRead(Long.parseLong(childId), callback);
                        });
                    }
                }
            }
        });
    }

    int proc = 0;

    public void startDfs92(TreeNode root, Map<Item, List<Item>> list,
                           final ObjectCallback<Void> callback) {

        getItemFromId(19639297, item -> {
            ItemViewHolder.Item j = new ItemViewHolder.Item();
            j.setScore(item.getScore());
            j.setTitle(item.getTitle());
            j.setText(item.getText());

            root.addChild(new TreeNode(j).setViewHolder(new ItemViewHolder(this)));

            dfs92(list, item, new Item(), root, callback);
        });
    }

    public void dfs92(Map<Item, List<Item>> list, Item node, Item parent,
                      TreeNode treeNode, final ObjectCallback<Void> callback) {
        Log.d("myLogger", Integer.toString(proc));

        if (proc == 25) {
            callback.onSuccess(null);
        }

        for (int i = 0; i < list.get(node).size(); i++) {
            if (!list.get(node).get(i).getId().equals(parent.getId())) {

                ItemViewHolder.Item j = new ItemViewHolder.Item();
                j.setScore(list.get(node).get(i).getScore());
                j.setTitle(list.get(node).get(i).getTitle());
                j.setText(list.get(node).get(i).getText());

                TreeNode item0 = new TreeNode(j).setViewHolder(new ItemViewHolder(MainActivity.this));

                treeNode.addChild(item0);
                proc++;

                dfs92(list, list.get(node).get(i), node, item0, callback);
            }
        }
    }

    private void showBoard(BoardType boardType) {
        getParentItemsForBoard(boardType, ids ->
                getItemsFromIdArray(ids, items -> {
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("items", (ArrayList<? extends Parcelable>) items);

                    List<ItemViewHolder.Item> itemsToShow = new ArrayList<>();

                    int iz = 0;
                    for (Item item : items) {
                        ItemViewHolder.Item itemToShow = new ItemViewHolder.Item();
                        itemToShow.setText(item.getText());
                        itemToShow.setBy(Integer.toString(iz));
                        itemToShow.setCreationTime(item.getCreationTime());
                        itemToShow.setUri(item.getUri());
                        itemToShow.setScore(item.getScore());
                        itemToShow.setChildren(item.getChildren());
                        itemsToShow.add(itemToShow);
                        iz++;
                    }
                    /*
                    ItemListFragment itemListFragment = new ItemListFragment();
                    itemListFragment.setArguments(bundle);

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, itemListFragment, "itemListFragment")
                            .addToBackStack("itemListFragment");
                    ft.commit();
                    */


                    Fragment tvFragment = new TreeViewFragment();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, tvFragment, "tvFragment")
                            .addToBackStack("tvFragment");
                    ft.commit();

                    FrameLayout containerView = this.findViewById(R.id.fragment_container);

                    TreeNode root = TreeNode.root();

                    startRead(Long.parseLong("19639297"), (Map<Item, List<Item>> myList) -> {
                        startDfs92(root, myList, a -> {
                            AndroidTreeView tView = new AndroidTreeView(this, root);
                            containerView.addView(tView.getView());
                        });
                    });
                }));
    }

    public void showComments(Item item) {
        List<String> children = item.getChildren();

        List<Long> childIds = new ArrayList<>();

        if (children.size() > 0) {
            for (String id : children) {
                childIds.add(Long.valueOf(id));
            }
        }

        getItemsFromIdArray(childIds, items -> {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("items", (ArrayList<? extends Parcelable>) items);

            CommentsFragment commentsFragment = new CommentsFragment();
            commentsFragment.setArguments(bundle);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, commentsFragment, "commentsFragment");
            ft.addToBackStack("commentsFragment");
            ft.commit();
        });
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
                            item.setId(response.getString("id"));
                        }
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
                            Uri uri = Uri.parse(response.getString("url"));
                            item.setUri(uri);
                        }
                        if (response.has("kids")) {
                            ArrayList<String> list = new ArrayList<String>();
                            ArrayList<Long> longList = new ArrayList<Long>();

                            JSONArray jsonArray = response.getJSONArray("kids");
                            if (jsonArray != null) {
                                int len = jsonArray.length();
                                for (int i = 0; i < len; i++) {
                                    list.add(jsonArray.get(i).toString());
                                    longList.add(jsonArray.getLong(i));
                                }

                                item.setChildren(list);
                            }
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

        int itemCount = idList.size() > maxItems ? maxItems : idList.size();

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

    private void getParentItemsForBoard(BoardType boardType,
                                        final ListCallback<Long> listCallback) {
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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