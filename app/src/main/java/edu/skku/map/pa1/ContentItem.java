package edu.skku.map.pa1;

import java.util.HashMap;
import java.util.Map;

public class ContentItem {
    public String contentimage;
    public String username;
    public String content;
    public String tags;
    public String check;

    public ContentItem() {

    }

    public ContentItem(String username, String contentimage, String content, String tags, String check) {
        this.username = username;
        this.contentimage = contentimage;
        this.content = content;
        this.tags = tags;
        this.check = check;
    }

    public String getContentimage() {
        return contentimage;
    }

    public void setContentimage(String contentimage) {
        this.contentimage = contentimage;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }
}