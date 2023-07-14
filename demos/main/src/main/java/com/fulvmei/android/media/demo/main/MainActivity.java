package com.fulvmei.android.media.demo.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import com.fulvmei.android.media.demo.main.bean.Media;
import com.fulvmei.android.media.demo.main.bean.MediaGroup;
import com.fulvmei.android.media.demo.main.ui.GroupListAdapter;


public class MainActivity extends AppCompatActivity  {

    private MediaGroupListAdapter mediaGroupListAdapter;
    private List<MediaGroup> mediaGroupList;

    RecyclerView recyclerView;

    List<MediaGroup> dataList;
    GroupListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        dataList = getMediaGroupList();

        listAdapter = new GroupListAdapter();
        listAdapter.submitList(dataList);
        recyclerView.setAdapter(listAdapter);
    }

    private List<MediaGroup> getMediaGroupList() {
        List<MediaGroup> mediaGroups = new ArrayList<>();
        JSONArray ja = null;
        try {
            ja = new JSONArray(getMediaGroupListString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (ja != null) {
            for (int i = 0; i < ja.length(); i++) {
                try {
                    mediaGroups.add(parsedMediaGroup(ja.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return mediaGroups;
    }

    private MediaGroup parsedMediaGroup(JSONObject jo) {
        MediaGroup mediaGroup = null;
        if (jo != null) {
            mediaGroup = new MediaGroup();
            mediaGroup.setName(jo.optString("name"));
            mediaGroup.setMediaList(parsedMediaList(jo.optJSONArray("media_list")));
        }
        return mediaGroup;
    }

    private ArrayList<Media> parsedMediaList(JSONArray ja) {
        ArrayList<Media> mediaList = null;
        if (ja != null) {
            mediaList = new ArrayList<>();
            for (int i = 0; i < ja.length(); i++) {
                try {
                    mediaList.add(parsedMedia(ja.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return mediaList;
    }

    private Media parsedMedia(JSONObject jo) {
        Media media = null;
        if (jo != null) {
            media = new Media();
            media.setName(jo.optString("name"));
            media.setPath(jo.optString("path"));
            media.setType(jo.optString("type"));
            media.setTag(jo.optString("tag"));
        }
        return media;
    }

    private String getMediaGroupListString() {
        InputStream inputStream = null;
        String media_list = null;
        try {
            inputStream = getAssets().open("media_list.json");
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            media_list = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return media_list;
    }
}
