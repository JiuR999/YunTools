package cn.swust.jiur.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.swust.jiur.entity.Music;

public class JsonUtil {
    public static List<Music> jsonArrayToList(JSONArray jsonArray){
        List<Music> music = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String name = jsonObject.optString("name");
                String cover = jsonObject.optString("name");
                String id = jsonObject.optString("id");
                String downUrl = jsonObject.optString("downUrl");
                String url = jsonObject.optString("url");
                String wordUrl = jsonObject.optString("wordUrl");
                Music music1 = new Music(name,cover,id,downUrl,url,wordUrl);
                music.add(music1);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return music;
    }
}
