package com.example.derrickngatia.reviseuploader;

/**
 * Created by DERRICK NGATIA on 10/15/2017.
 */

public class image {
    public String imageName;
    public String imageURL;

    public image() {
    }

    public image(String imageName, String imageURL) {
        this.imageName = imageName;
        this.imageURL = imageURL;
    }


    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
