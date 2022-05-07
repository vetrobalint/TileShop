package com.example.tileshop;

public class TileItems {
    private String id;
    private String name;
    private String description;
    private String price;
    private float ratedInfo;
    private int image;
    private int count;

    public TileItems(){}

    public TileItems(String name, String description, String price, float ratedInfo, int image) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.ratedInfo = ratedInfo;
        this.image = image;
        this.count = 0;
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getPrice() {
        return price;
    }
    public float getRatedInfo() {
        return ratedInfo;
    }
    public int getImage() {
        return image;
    }
    public int getCount(){return count;}
    public String _getId(){return id;}
    public void setId(String id){this.id=id;}
}
