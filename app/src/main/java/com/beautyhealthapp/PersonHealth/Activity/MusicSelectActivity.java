package com.beautyhealthapp.PersonHealth.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.LocationEntity.MusicInfo;
import com.beautyhealthapp.PersonHealth.Assistant.MusicListAdapter;
import com.beautyhealthapp.R;
import com.infrastructure.CWActivity.NavBarActivity;

import java.util.ArrayList;

/**
 * Created by lenovo on 2016/1/5.
 */
public class MusicSelectActivity extends NavBarActivity implements OnItemClickListener {
    private ListView musiclist;
    private ArrayList<MusicInfo> musicList;
    private MusicListAdapter adapter;
    public void setListItemes(ArrayList<MusicInfo> musicList) {
        musicList = musicList;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicselect);
        initNavBar("铃声选择", true, false);
        musicList = new ArrayList<MusicInfo>();
        fetchUIFromLayout();
        addMusicInList();
    }

    private void fetchUIFromLayout() {
        musiclist = (ListView) findViewById(R.id.lv_musicselect);
        adapter = new MusicListAdapter(this,musicList);
        musiclist.setAdapter(adapter);
        musiclist.setOnItemClickListener(this);
    }

    private void addMusicInList() {
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        // 遍历媒体数据库
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                // 歌曲文件的路径 ：MediaStore.Audio.Media.DATA
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                // 歌曲文件显示名字
                String disName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                MusicInfo musicInfo = new MusicInfo();
                musicInfo.musicName=disName;
                musicInfo.musicPath = url;
                musicList.add(musicInfo);
                cursor.moveToNext();
            }
            setListItemes(musicList);
            adapter.notifyDataSetChanged();
            cursor.close();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String fileName=musicList.get(position).musicPath;
        Intent in_result = new Intent();
        in_result.putExtra("fileName", fileName);
        setResult(-1, in_result);
        finish();
    }
}
