package cn.swust.jiur.entity;

import com.alibaba.fastjson.annotation.JSONField;

import org.json.JSONArray;

import java.util.List;

public class Profile {
    @JSONField(name = "_id")
    private String id;
    @JSONField(name = "name")
    private String categoryName;
    @JSONField(name = "img")
    private String url;
    private List<String> tags;
    private String type;
    public Profile() {
    }

    public Profile(String id, String categoryName, String url, String type) {
        this.id = id;
        this.categoryName = categoryName;
        this.url = url;
        this.type = type;
    }

    public Profile(String url, List<String> tags) {
        this.url = url;
        this.tags = tags;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
