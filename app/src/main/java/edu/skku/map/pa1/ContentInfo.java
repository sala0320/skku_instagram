package edu.skku.map.pa1;

import java.util.HashMap;
import java.util.Map;

public class ContentInfo {
    public String username;
    public String contentimage;
    public String content;
    public String tags;
    public String check;
    public ContentInfo(){

    }
    public ContentInfo(String username, String contentimage, String content, String tags, String check){
        this.username = username;
        this.contentimage = contentimage;
        this.content = content;
        this.tags = tags;
        this.check = check;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("contentimage",contentimage);
        result.put("content",content);
        result.put("tags", tags);
        result.put("check",check);
        return result;
    }
}