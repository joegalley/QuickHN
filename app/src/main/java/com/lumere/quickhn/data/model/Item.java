package com.lumere.quickhn.data.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Item implements Parcelable {
    private String id;
    private boolean deleted;
    private ItemType type;
    private String by;
    private long creationTime;
    private String text;
    private Item parent;
    private String poll;
    private List<String> children;
    private Uri uri;
    private int score;
    private String title;
    private String parts;
    private int descendants;
    private String elapsedTime;
    private List<Item> kids;
    private boolean visited;

    public void addKid(Item item) {
        if (null == this.kids || this.kids.isEmpty()) {
            this.kids = new ArrayList<>();
        }
        this.kids.add(item);
    }

    public Item(Parcel in) {
        id = in.readString();
        deleted = in.readByte() != 0;
        type = ItemType.valueOf(in.readString());
        by = in.readString();
        creationTime = in.readLong();
        text = in.readString();
        parent = in.readParcelable(Item.class.getClassLoader());
        poll = in.readString();
        children = in.createStringArrayList();
        uri = Uri.parse(in.readString());
        score = in.readInt();
        title = in.readString();
        parts = in.readString();
        descendants = in.readInt();
        elapsedTime = in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeByte((byte) (deleted ? 1 : 0));
        parcel.writeString(null != type ? type.name() : null);
        parcel.writeString(by);
        parcel.writeLong(creationTime);
        parcel.writeString(text);
        parcel.writeParcelable(parent, 0);
        parcel.writeString(poll);
        parcel.writeStringList(children);
        parcel.writeString(null != uri ? uri.toString() : null);
        parcel.writeInt(score);
        parcel.writeString(title);
        parcel.writeString(parts);
        parcel.writeInt(descendants);
        parcel.writeString(elapsedTime);
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(this.getId());
    }

    @Override
    public boolean equals(Object other) {
        if (null == other) {
            return false;
        }

        if (!(other instanceof Item)) {
            return false;
        }

        return ((Item) other).getId().equals(this.getId());
    }


}