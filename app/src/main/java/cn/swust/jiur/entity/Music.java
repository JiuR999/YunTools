package cn.swust.jiur.entity;

public class Music {
    private String name;
    private String cover;
    private String id;
    private String downUrl;
    private String url;
    private String wordUrl;

    public Music(String name, String cover, String id, String downUrl, String url, String wordUrl) {
        this.name = name;
        this.cover = cover;
        this.id = id;
        this.downUrl = downUrl;
        this.url = url;
        this.wordUrl = wordUrl;
    }

    public Music(String name, String cover, String downUrl) {
        this.name = name;
        this.cover = cover;
        this.downUrl = downUrl;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getWordUrl() {
        return wordUrl;
    }
    public void setWordUrl(String wordUrl) {
        this.wordUrl = wordUrl;
    }
    public String getCover() {
        return cover;
    }
    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }
}
