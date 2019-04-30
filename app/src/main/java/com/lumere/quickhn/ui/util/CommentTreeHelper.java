package com.lumere.quickhn.ui.util;

import android.content.Context;
import android.view.ViewGroup;

import com.lumere.quickhn.data.model.Item;
import com.lumere.quickhn.network.HnApiHelper;
import com.lumere.quickhn.ui.activity.ItemViewHolder;
import com.lumere.quickhn.ui.activity.MainActivity;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class CommentTreeHelper {

    private Context mContext;
    private HnApiHelper mHnApiHelper;

    private int commentsProcessed;
    private int desc = 0;
    private int totalDescendants = 0;

    private Map<Item, List<Item>> commentParentChildMap = new Hashtable<Item, List<Item>>();

    public CommentTreeHelper(Context context) {
        this.mContext = context;
        this.mHnApiHelper = new HnApiHelper(context);
        this.commentsProcessed = 0;
    }

    private void addToCommentParentChildMap(Item item, Item itemToAdd) {
        if (commentParentChildMap.get(item) == null) {
            commentParentChildMap.put(item, new ArrayList<>());
        }

        List<Item> items = commentParentChildMap.get(item);
        items.add(itemToAdd);

        commentParentChildMap.put(item, items);
    }

    public interface VoidCallback {
        void onSuccess();
    }

    public interface ObjectCallback<T> {
        void onSuccess(T object);
    }

    public void renderCommentTree(TreeNode root, long rootId, final ObjectCallback<Map<Item, List<Item>>> callback) {
        mHnApiHelper.getItemFromId(rootId, item -> {
            CommentTreeHelper.this.totalDescendants = item.getDescendants();
            startRead(rootId, () -> {
                this.startDfsComments(root, commentParentChildMap, (c) -> {

                });
            });
        });
    }

    public void renderCommentTree(long rootId, ViewGroup v) {
        TreeNode root = TreeNode.root();

        mHnApiHelper.getItemFromId(rootId, item -> {
            CommentTreeHelper.this.totalDescendants = item.getDescendants();
            startRead(rootId, () -> {
                this.startDfsComments(root, commentParentChildMap, (c) -> {
                    AndroidTreeView tView = new AndroidTreeView(this.mContext, root);
                    v.addView(tView.getView());
                });
            });
        });
    }

    private void startRead(long rootId, final VoidCallback callback) {
        if (this.desc == this.totalDescendants) {
            callback.onSuccess();
        }

        mHnApiHelper.getItemFromId(rootId, (Item item) -> {
            if (item.getChildren() != null && !item.getChildren().isEmpty()) {

                for (String childId : item.getChildren()) {
                    if (!childId.equals(item.getId())) {
                        mHnApiHelper.getItemFromId(Long.parseLong(childId), (childItem) -> {
                            addToCommentParentChildMap(item, childItem);
                            addToCommentParentChildMap(childItem, item);

                            item.addKid(childItem);
                            childItem.setParent(item);

                            this.desc++;
                            startRead(Long.parseLong(childId), callback);
                        });
                    }
                }
            }
        });
    }

    private void startDfsComments(TreeNode root, Map<Item, List<Item>> list,
                                  final MainActivity.ObjectCallback<Void> callback) {
        mHnApiHelper.getItemFromId(19639297, item -> {
            dfsComments(list, item, new Item(), root, callback);
        });
    }

    private void dfsComments(Map<Item, List<Item>> list, Item node, Item parent,
                             TreeNode treeNode, final MainActivity.ObjectCallback<Void> callback) {
        if (this.commentsProcessed == this.totalDescendants) {
            callback.onSuccess(null);
        }

        for (int i = 0; i < list.get(node).size(); i++) {
            if (!list.get(node).get(i).getId().equals(parent.getId())) {

                ItemViewHolder.Item j = new ItemViewHolder.Item();
                j.setScore(list.get(node).get(i).getScore());
                j.setTitle(list.get(node).get(i).getTitle());
                j.setText(list.get(node).get(i).getText());

                TreeNode item0 = new TreeNode(j).setViewHolder(new ItemViewHolder(this.mContext));

                treeNode.addChild(item0);
                this.commentsProcessed++;

                dfsComments(list, list.get(node).get(i), node, item0, callback);
            }
        }
    }

}
