package com.hasanalpzengin.typeandtalk;


public class Favorite {
    private String text, category;
    private int favorite;

    public Favorite(String text, String category, int favorite) {
        this.text = text;
        this.category = category;
        this.favorite = favorite;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }
}
