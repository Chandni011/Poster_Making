package com.deificdigital.poster_making.classes;

public class EditOperation {
    public String type;  // e.g., "text", "image"
    public String content;  // text or image path
    public int color;
    public float size;
    public float x, y;  // Position
    public String font;  // Font name for text edits

    public EditOperation(String type, String content, int color, float size, float x, float y, String font) {
        this.type = type;
        this.content = content;
        this.color = color;
        this.size = size;
        this.x = x;
        this.y = y;
        this.font = font;
    }
}