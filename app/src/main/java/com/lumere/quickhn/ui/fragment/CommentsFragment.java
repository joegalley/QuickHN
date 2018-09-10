package com.lumere.quickhn.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lumere.quickhn.R;
import com.lumere.quickhn.data.model.Item;
import com.lumere.quickhn.ui.activity.MainActivity;
import com.lumere.quickhn.ui.adapter.CommentListAdapter;
import com.lumere.quickhn.ui.adapter.ItemListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class CommentsFragment extends Fragment {
    private RecyclerView.Adapter mAdapter;

    //@BindView(R.id.commentList)
    protected RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //savedInstanceState.getLongArray("a");

        List<Item> items = new ArrayList<Item>();
        Item item1 = new Item();
        item1.setText("abc");
        item1.setBy("Bob");

        mAdapter = new CommentListAdapter(items);


        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        //RecyclerView recyclerView = view.findViewById(R.id.commentList);
        //recyclerView.setAdapter(mAdapter);

        return view;
    }
}