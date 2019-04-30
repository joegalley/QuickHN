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
import com.lumere.quickhn.ui.adapter.ItemListAdapter;

import java.util.List;

public class ItemListFragment extends Fragment {
    private RecyclerView.Adapter mAdapter;

    // @BindView(R.id.itemList)
    protected RecyclerView mRecyclerView;

    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        List<Item> items = getArguments().getParcelableArrayList("items");
        mAdapter = new ItemListAdapter(getActivity(), items);

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        mRecyclerView = view.findViewById(R.id.itemList);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(mAdapter);

        return view;
    }
}