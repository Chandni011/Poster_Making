package com.deificdigital.poster_making.models;

//package com.deificdigital.poster_making.models;

public class FontModel {
    private String fontName;
    private String fontResName; // Resource name for the font in res/font

    public FontModel(String fontName, String fontResName) {
        this.fontName = fontName;
        this.fontResName = fontResName;
    }

    public String getFontName() {
        return fontName;
    }

    public String getFontResName() {
        return fontResName;
    }
}
