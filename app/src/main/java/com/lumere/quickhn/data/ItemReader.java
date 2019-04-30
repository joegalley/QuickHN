package com.lumere.quickhn.data;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lumere.quickhn.data.model.Item;
import com.lumere.quickhn.data.model.ItemType;
import com.lumere.quickhn.network.VolleyManager;
import com.lumere.quickhn.ui.activity.ItemViewHolder;
import com.lumere.quickhn.ui.activity.MainActivity;
import com.unnamed.b.atv.model.TreeNode;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ItemReader {
    private static final int secInWeek = 604800;
    private static final int secInDay = 86400;
    private static final int secInHour = 3600;
    private static final int secInMin = 60;

    private int total;
    MainActivity.ObjectCallback<Void> callback;
    int read;

    private Context mContext;

    public ItemReader(Context context) {
        this.mContext = context;
    }

    private void dfsItems2(Item item, TreeNode root, final ObjectCallback<Void> callback, int total) {
        read++;
        Log.d("what2", "reading " + item.getId() + " count " + read);

        if (read == total) {
            Log.d("what2", "going to finish");
            callback.onSuccess(null);
        }

        if (null != item.getChildren() && !item.getChildren().isEmpty()) {
            //processed = item.getChildren().size() + processed;
            for (String childId : item.getChildren()) {
                getItemFromId(Long.parseLong(childId), ii -> {
                    item.addKid(ii);
                    // list.get(Integer.valueOf(item.getId())).add(Integer.valueOf(ii.getId()));

                    // Log.d("what2", String.format("added kid %s to %s", ii.getId(), item.getId()));
                    TreeNode treeNode = new TreeNode(ii).setViewHolder(new ItemViewHolder(mContext));

                    dfsItems2(ii, treeNode, null, total);
                });
            }
        } else {
            Log.d("what2", "done");
            return;
        }
    }


    public interface ObjectCallback<T> {
        void onSuccess(T object);
    }

    private void getItemFromId(long id, final ObjectCallback<Item> callback) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                "https://hacker-news.firebaseio.com/v0/item/" + String.valueOf(id) + ".json",
                null,
                response -> {
                    try {
                        Item item = new Item();

                        if (response.has("by")) {
                            item.setId(response.getString("id"));
                        }
                        if (response.has("by")) {
                            item.setBy(response.getString("by"));
                        }
                        if (response.has("descendants")) {
                            item.setDescendants(response.getInt("descendants"));
                        }
                        if (response.has("score")) {
                            item.setScore(response.getInt("score"));
                        }
                        if (response.has("time")) {
                            long posted = response.getLong("time");
                            long now = System.currentTimeMillis() / 1000;

                            long timeElapsed = now - posted;

                            int hours = (int) ((timeElapsed / secInHour) % 60);
                            int minutes = (int) ((timeElapsed / secInMin) % 60);

                            item.setCreationTime(response.getLong("time"));

                            item.setElapsedTime(hours + "h " + minutes + "m");
                        }
                        if (response.has("title")) {
                            item.setTitle(response.getString("title"));
                        }
                        if (response.has("text")) {
                            item.setText(response.getString("text"));
                        }
                        if (response.has("type")) {
                            String type = response.getString("type");
                            switch (type.toLowerCase()) {
                                case "job":
                                    item.setType(ItemType.JOB);
                                    break;
                                case "story":
                                    item.setType(ItemType.STORY);
                                    break;
                                case "comment":
                                    item.setType(ItemType.COMMENT);
                                    break;
                                case "poll":
                                    item.setType(ItemType.POLL);
                                    break;
                                case "pollopt":
                                    item.setType(ItemType.POLLOPT);
                                    break;
                                default:
                                    break;
                            }
                        }
                        if (response.has("url")) {
                            Uri uri = Uri.parse(response.getString("url"));
                            item.setUri(uri);
                        }
                        if (response.has("kids")) {
                            ArrayList<String> list = new ArrayList<String>();
                            ArrayList<Long> longList = new ArrayList<Long>();

                            JSONArray jsonArray = response.getJSONArray("kids");
                            if (jsonArray != null) {
                                int len = jsonArray.length();
                                for (int i = 0; i < len; i++) {
                                    list.add(jsonArray.get(i).toString());
                                    longList.add(jsonArray.getLong(i));
                                }

                                item.setChildren(list);
                            }
                        }
                        callback.onSuccess(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("error", error.toString()));

        VolleyManager.getInstance(mContext).addToRequestQueue(request);
    }
}
