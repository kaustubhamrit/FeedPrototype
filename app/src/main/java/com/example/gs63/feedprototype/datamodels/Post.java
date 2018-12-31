package com.example.gs63.feedprototype.datamodels;

import com.airbnb.lottie.L;

public class Post {

    private String postText;
    private String userId;
    private String imageUrl = "";
    private Long timeStamp;

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    private String pushKey;

    public Post() {
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = -1 * timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Post)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Post c = (Post) o;

        // Compare the data members and return accordingly
        return getTimeStamp().equals(c.getTimeStamp());
    }

}
