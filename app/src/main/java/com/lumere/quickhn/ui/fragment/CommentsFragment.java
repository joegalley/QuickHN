package com.lumere.quickhn.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lumere.quickhn.R;
import com.lumere.quickhn.data.model.Item;
import com.lumere.quickhn.ui.adapter.CommentListAdapter;

import java.util.List;

public class CommentsFragment extends Fragment {

    protected RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        List<Item> items = getArguments().getParcelableArrayList("items");

        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        mRecyclerView = view.findViewById(R.id.commentList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new CommentListAdapter(getActivity(), items));

        return view;
    }
}