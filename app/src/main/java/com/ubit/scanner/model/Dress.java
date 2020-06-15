package com.ubit.scanner.model;

public class Dress {

    private String code;
    private String price;
    private String image;

    public Dress(String code, String price, String image) {
        this.code = code;
        this.price = price;
        this.image = image;
    }

    public Dress() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
