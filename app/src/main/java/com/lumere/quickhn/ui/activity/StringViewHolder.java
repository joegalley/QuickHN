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

public class StringViewHolder extends TreeNode.BaseNodeViewHolder<StringViewHolder.Item> {


    private Context mContext;

    public StringViewHolder(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public View createNodeView(TreeNode node, Item value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_item_view, null, false);

        ((TextView) view.findViewById(R.id.title)).setText(Integer.toString(value.getScore()));

        return view;
    }


    @Setter
    @Getter
    @NoArgsConstructor
    public static class Item {
        private int score;
    }

}
