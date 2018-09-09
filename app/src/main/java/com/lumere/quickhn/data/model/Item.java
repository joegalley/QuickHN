package com.lumere.quickhn.data.model;

import java.net.URI;
import java.util.List;

import lombok.Data;

@Data
public class Item {
    private String id;
    private boolean deleted;
    private ItemType type;
    private String by;
    private long creationTime;
    private String text;
    private Item parent;
    private String poll;
    private List<String> children;
    private URI uri;
    private int score;
    private String title;
    private String parts;
    private int descendants;
    private String elapsedTime;
}