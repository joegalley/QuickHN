package com.lumere.quickhn.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lumere.quickhn.R;
import com.lumere.quickhn.data.model.Item;

import java.util.List;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {

    private List<Item> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView time;
        public TextView text;
        public TextView author;
        public TextView children;

        ViewHolder(View view) {
            super(view);
            time = view.findViewById(R.id.time);
            author = view.findViewById(R.id.author);
            text = view.findViewById(R.id.text);
        }
    }

    public CommentListAdapter(Context context, List<Item> dataSet) {
        mDataset = dataSet;
    }

    @Override
    public CommentListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_comment_view, parent, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = mDataset.get(position);

        holder.text.setText(item.getText());
        holder.author.setText(item.getBy());

        long posted = item.getCreationTime();
        long now = System.currentTimeMillis() / 1000;

        long timeElapsed = now - posted;

        int secInHour = 60 * 60;
        int secInMin = 60;

        int hours = (int) ((timeElapsed / secInHour) % 60);
        int minutes = (int) ((timeElapsed / secInMin) % 60);

        String elapsedTime = hours + "h " + minutes + "m";
        holder.time.setText(elapsedTime);

        List<String> children = item.getChildren();
        if (null != children) {

        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}