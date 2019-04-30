package com.lumere.quickhn.network;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lumere.quickhn.R;
import com.lumere.quickhn.data.model.BoardType;
import com.lumere.quickhn.data.model.Item;
import com.lumere.quickhn.data.model.ItemType;
import com.lumere.quickhn.ui.activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import butterknife.BindInt;

public class HnApiHelper {

    @BindInt(R.integer.itemCount)
    private int maxItems;

    private Context mContext;

    private static final int secInWeek = 604800;
    private static final int secInDay = 86400;
    private static final int secInHour = 3600;
    private static final int secInMin = 60;

    public HnApiHelper(Context context) {
        this.mContext = context;
    }

    public void getItemFromId(long id, final MainActivity.ObjectCallback<Item> callback) {
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

        VolleyManager.getInstance(this.mContext).addToRequestQueue(request);
    }

    public interface ListCallback<T> {
        void onSuccess(List<T> object);
    }

    public void getParentItemIdsForBoard(BoardType boardType, final ListCallback<Long> listCallback) {
        String url = "";

        switch (boardType) {
            case TOP:
                url = "https://hacker-news.firebaseio.com/v0/topstories.json";
                break;
            case NEW:
                url = "https://hacker-news.firebaseio.com/v0/newstories.json";
                break;
            case SHOW:
                url = "https://hacker-news.firebaseio.com/v0/showstories.json";
                break;
            case ASK:
                url = "https://hacker-news.firebaseio.com/v0/askstories.json";
                break;
            case BEST:
                url = "https://hacker-news.firebaseio.com/v0/beststories.json";
                break;
            case JOB:
                url = "https://hacker-news.firebaseio.com/v0/jobstories.json";
                break;
        }

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    List<Long> itemIds = new ArrayList<>();
                    for (int i = 0; i < response.length() - 1; i++) {
                        try {
                            itemIds.add(response.getLong(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    listCallback.onSuccess(itemIds);
                },
                error -> Log.d("error", error.toString()));

        VolleyManager.getInstance(this.mContext).addToRequestQueue(request);
    }

    public void getItemsFromIdArray(List<Long> idList, MainActivity.ListCallback<Item> listCallback) {
        List<Item> itemList = new ArrayList<>();

        int itemCount = idList.size() > maxItems ? maxItems : idList.size();

        CountDownLatch countDownLatch = new CountDownLatch(itemCount);

        for (int i = 0; i < itemCount; i++) {
            getItemFromId(idList.get(i), item -> {
                itemList.add(item);
                countDownLatch.countDown();
            });
        }

        final Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.post(() -> listCallback.onSuccess(itemList));
        }).start();
    }

}
