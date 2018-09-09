package com.lumere.quickhn.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lumere.quickhn.R;
import com.lumere.quickhn.data.model.Item;
import com.lumere.quickhn.ui.activity.MainActivity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    private Context mContext;
    private List<Item> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView points;
        public TextView elapsedTime;
        public TextView title;
        public TextView author;
        public TextView domain;
        public TextView descendants;
        public LinearLayout commentView;

        ViewHolder(View view) {
            super(view);
            points = view.findViewById(R.id.points);
            elapsedTime = view.findViewById(R.id.elapsedTime);
            title = view.findViewById(R.id.title);
            author = view.findViewById(R.id.author);
            domain = view.findViewById(R.id.domain);
            descendants = view.findViewById(R.id.descendants);
            commentView = view.findViewById(R.id.commentArea);
        }
    }

    public ItemListAdapter(Context context, List<Item> dataSet) {
        mContext = context;
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
        holder.elapsedTime.setText(item.getElapsedTime());
        holder.title.setText(item.getTitle());
        holder.author.setText(item.getBy());

        URI uri = item.getUri();
        if (null != uri) {
            String host = uri.getHost();
            if (host.startsWith("www.")) {
                host = host.substring(4);
            }
            holder.domain.setText(host);
        }

        int descendants = item.getDescendants();
        holder.descendants.setText(String.valueOf(item.getDescendants()));

        holder.commentView.setOnClickListener(view -> {
            //Toast.makeText(view.getContext(), "item click", Toast.LENGTH_SHORT).show();

            if (mContext instanceof MainActivity) {
                ((MainActivity) mContext).showComments(Arrays.asList(121016L, 121109L, 121168L));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}