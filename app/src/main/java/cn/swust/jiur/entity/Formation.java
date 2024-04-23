package cn.swust.jiur.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Formation {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int Level;
    private String content;
    private String link;
    private String img;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return Level;
    }

    public void setLevel(int level) {
        Level = level;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
