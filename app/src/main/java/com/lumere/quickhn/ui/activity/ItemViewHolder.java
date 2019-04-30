package com.lumere.quickhn.ui.activity;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lumere.quickhn.R;
import com.lumere.quickhn.data.model.Item;
import com.lumere.quickhn.data.model.ItemType;
import com.unnamed.b.atv.model.TreeNode;

import org.w3c.dom.Text;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ItemViewHolder extends TreeNode.BaseNodeViewHolder<ItemViewHolder.Item> {


    private Context mContext;

    public ItemViewHolder(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public View createNodeView(TreeNode node, Item item) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_item_view, null, false);

        ((TextView) view.findViewById(R.id.descendants)).setText(String.valueOf(item.getDescendants()));


        //  ((TextView) view.findViewById(R.id.commentArea)).setText(item.getText());
        ((TextView) view.findViewById(R.id.points))
                .setText(String.format(String.valueOf(item.getScore()) + " %s", "points"));
        ((TextView) view.findViewById(R.id.elapsedTime)).setText(item.getElapsedTime());
        ((TextView) view.findViewById(R.id.title)).setText(item.getText());
        ((TextView) view.findViewById(R.id.author)).setText(item.getBy());

        Uri uri = item.getUri();
        if (null != uri) {
            String host = uri.getHost();
            if (host.startsWith("www.")) {
                host = host.substring(4);
            }
            ((TextView) view.findViewById(R.id.domain)).setText(host);
        }

        ((TextView) view.findViewById(R.id.descendants)).setText(String.valueOf(item.getDescendants()));


        /*
        view.findViewById(R.id.commentArea).setOnClickListener(v -> {
            if (mContext instanceof MainActivity) {
                ((MainActivity) mContext).showComments(item);
            }
        });
        */

        return view;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Item {
        private String id;
        private boolean deleted;
        private ItemType type;
        private String by;
        private long creationTime;
        private String text;
        private com.lumere.quickhn.data.model.Item parent;
        private String poll;
        private List<String> children;
        private Uri uri;
        private int score;
        private String title;
        private String parts;
        private int descendants;
        private String elapsedTime;
        private List<com.lumere.quickhn.data.model.Item> kids;
        private boolean visited;
    }
}
