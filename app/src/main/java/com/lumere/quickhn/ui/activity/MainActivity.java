package com.lumere.quickhn.ui.activity;

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

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.lumere.quickhn.R;
import com.lumere.quickhn.data.model.BoardType;
import com.lumere.quickhn.data.model.Item;
import com.lumere.quickhn.network.HnApiHelper;
import com.lumere.quickhn.network.VolleyManager;
import com.lumere.quickhn.ui.fragment.CommentsFragment;
import com.lumere.quickhn.ui.fragment.ItemListFragment;
import com.lumere.quickhn.ui.fragment.TreeViewFragment;
import com.lumere.quickhn.ui.util.CommentTreeHelper;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String[] boards;

    private CommentTreeHelper mCommentTreeHelper;
    private HnApiHelper mHnApiHelper;

    private static final int secInWeek = 604800;
    private static final int secInDay = 86400;
    private static final int secInHour = 3600;
    private static final int secInMin = 60;

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

        this.mCommentTreeHelper = new CommentTreeHelper(this);
        this.mHnApiHelper = new HnApiHelper(this);

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
        mHnApiHelper.getParentItemIdsForBoard(boardType, ids ->
                mHnApiHelper.getItemsFromIdArray(ids, items -> {
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("items", (ArrayList<? extends Parcelable>) items);

                    ItemListFragment itemListFragment = new ItemListFragment();
                    itemListFragment.setArguments(bundle);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, itemListFragment, "itemListFragment")
                            .addToBackStack("itemListFragment")
                            .commit();
                }));
    }

    public void showComments(Item item) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new TreeViewFragment(), "commentsFragment")
                .addToBackStack("commentsFragment")
                .commit();

        FrameLayout containerView = this.findViewById(R.id.fragment_container);

        mCommentTreeHelper.renderCommentTree(Long.parseLong("19639297"), containerView);
    }

    public interface ObjectCallback<T> {
        void onSuccess(T object);
    }

    public interface ListCallback<T> {
        void onSuccess(List<T> items);
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