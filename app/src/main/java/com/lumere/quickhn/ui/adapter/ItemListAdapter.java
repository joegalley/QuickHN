package com.lumere.quickhn.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lumere.quickhn.R;
import com.lumere.quickhn.data.model.Item;

import java.net.URI;
import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    private List<Item> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView points;
        public TextView time;
        public TextView title;
        public TextView author;
        public TextView domain;
        public TextView comments;
        public LinearLayout commentView;

        ViewHolder(View view) {
            super(view);
            points = view.findViewById(R.id.points);
            time = view.findViewById(R.id.time);
            title = view.findViewById(R.id.title);
            author = view.findViewById(R.id.author);
            domain = view.findViewById(R.id.domain);
            comments = view.findViewById(R.id.comments);
            commentView = view.findViewById(R.id.commentArea);
        }
    }

    public ItemListAdapter(List<Item> dataSet) {
        mDataset = dataSet;
    }

    @Override
    public ItemListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_view, parent, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = mDataset.get(position);

        holder.points.setText(String.format(String.valueOf(item.getScore()) + "%s", "points"));
        holder.time.setText(String.valueOf(item.getCreationTime()));
        holder.title.setText(item.getTitle());
        holder.author.setText(item.getBy());

        URI uri = item.getUri();
        if (null != uri) {
            holder.domain.setText(item.getUri().toString());
        }

        List<String> children = item.getChildren();
        if (null != children) {
            holder.comments.setText(String.valueOf(item.getChildren().size()));
        }

        holder.commentView.setOnClickListener(view -> {
            Toast.makeText(view.getContext(), "item click", Toast.LENGTH_SHORT).show();

        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}