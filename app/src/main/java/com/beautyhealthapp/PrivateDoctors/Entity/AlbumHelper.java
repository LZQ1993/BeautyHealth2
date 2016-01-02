package com.beautyhealthapp.PrivateDoctors.Entity;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
/**
 * 从Android2.2开始系统新增了一个缩略图ThumbnailUtils类，
 * 位于framework的 android.media.ThumbnailUtils位置，
 * 可以帮助我们从mediaprovider中获取系统中的视频或图片文件的缩略图，
 */

public class AlbumHelper {
	final String TAG = getClass().getSimpleName();
	Context context;
	ContentResolver cr;
	boolean hasBuildImagesBucketList = false;
	
	HashMap<String, String> thumbnailList = new HashMap<String, String>();  //缩略图

	List<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();  
	HashMap<String, ImageBucket> bucketList = new HashMap<String, ImageBucket>();  

	

	private static AlbumHelper instance;

	private AlbumHelper() {
	}

	public static AlbumHelper getHelper() {
		if (instance == null) {
			instance = new AlbumHelper();
		}
		return instance;
	}

	public void init(Context context) {
		if (this.context == null) {
			this.context = context;
			cr = context.getContentResolver();
		}
	}
	/**
     * 表thumbnails和images通过thumbnails.image_id与images._id关联的，
     * 通过images的_id，就可以找出来thumbnails表中的图片和images表中图片的映射关系了。
     * 原始图片的位置就是images表中的_data字段的值(绝对路径，大小，时长等等)。
     */
	private void getThumbnail() {
		
		String[] projection = { Thumbnails._ID, Thumbnails.IMAGE_ID,
				Thumbnails.DATA }; //缩略图与原图的映射
		Cursor cursor = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, projection,
				null, null, null);
		getThumbnailColumnData(cursor);
	}
	//获取缩略图
	private void getThumbnailColumnData(Cursor cur) {
		if (cur.moveToFirst()) {
			int _id;
			int image_id;
			String image_path;
			int _idColumn = cur.getColumnIndex(Thumbnails._ID);
			int image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID);
			int dataColumn = cur.getColumnIndex(Thumbnails.DATA);

			do {
				// Get the field values
				_id = cur.getInt(_idColumn);
				image_id = cur.getInt(image_idColumn);
				image_path = cur.getString(dataColumn);

				// Do something with the values.
				// Log.i(TAG, _id + " image_id:" + image_id + " path:"
				// + image_path + "---");
				// HashMap<String, String> hash = new HashMap<String, String>();
				// hash.put("image_id", image_id + "");
				// hash.put("path", image_path);
				// thumbnailList.add(hash);
				thumbnailList.put("" + image_id, image_path);
			} while (cur.moveToNext());
		}
	}

	void getAlbum() {
		String[] projection = { Albums._ID, Albums.ALBUM, Albums.ALBUM_ART,
				Albums.ALBUM_KEY, Albums.ARTIST, Albums.NUMBER_OF_SONGS };
		Cursor cursor = cr.query(Albums.EXTERNAL_CONTENT_URI, projection, null,
				null, null);
		getAlbumColumnData(cursor);

	}

	private void getAlbumColumnData(Cursor cur) {
		if (cur.moveToFirst()) {
			int _id;
			String album;
			String albumArt;
			String albumKey;
			String artist;
			int numOfSongs;

			int _idColumn = cur.getColumnIndex(Albums._ID);
			int albumColumn = cur.getColumnIndex(Albums.ALBUM);
			int albumArtColumn = cur.getColumnIndex(Albums.ALBUM_ART);
			int albumKeyColumn = cur.getColumnIndex(Albums.ALBUM_KEY);
			int artistColumn = cur.getColumnIndex(Albums.ARTIST);
			int numOfSongsColumn = cur.getColumnIndex(Albums.NUMBER_OF_SONGS);

			do {
				// Get the field values
				_id = cur.getInt(_idColumn);
				album = cur.getString(albumColumn);
				albumArt = cur.getString(albumArtColumn);
				albumKey = cur.getString(albumKeyColumn);
				artist = cur.getString(artistColumn);
				numOfSongs = cur.getInt(numOfSongsColumn);

				// Do something with the values.
				Log.i(TAG, _id + " album:" + album + " albumArt:" + albumArt
						+ "albumKey: " + albumKey + " artist: " + artist
						+ " numOfSongs: " + numOfSongs + "---");
				HashMap<String, String> hash = new HashMap<String, String>();
				hash.put("_id", _id + "");
				hash.put("album", album);
				hash.put("albumArt", albumArt);
				hash.put("albumKey", albumKey);
				hash.put("artist", artist);
				hash.put("numOfSongs", numOfSongs + "");
				albumList.add(hash);

			} while (cur.moveToNext());

		}
	}

	//获取实际图片
	void buildImagesBucketList() {
		
		
		long startTime = System.currentTimeMillis();
		getThumbnail();
		String columns[] = new String[] { Media._ID, Media.BUCKET_ID,
				Media.PICASA_ID, Media.DATA, Media.DISPLAY_NAME, Media.TITLE,
				Media.SIZE, Media.BUCKET_DISPLAY_NAME };
		Cursor cur = cr.query(Media.EXTERNAL_CONTENT_URI, columns, null, null,
				null);
		if (cur.moveToFirst()) {
			int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
			int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
			int photoNameIndex = cur.getColumnIndexOrThrow(Media.DISPLAY_NAME);
			int photoTitleIndex = cur.getColumnIndexOrThrow(Media.TITLE);
			int photoSizeIndex = cur.getColumnIndexOrThrow(Media.SIZE);
			int bucketDisplayNameIndex = cur
					.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
			int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);
			int picasaIdIndex = cur.getColumnIndexOrThrow(Media.PICASA_ID);
			int totalNum = cur.getCount();
			
			if(hasBuildImagesBucketList){
				
				bucketList.clear();				
			}

			do {
				String _id = cur.getString(photoIDIndex);
				String name = cur.getString(photoNameIndex);
				String path = cur.getString(photoPathIndex);
				String title = cur.getString(photoTitleIndex);
				String size = cur.getString(photoSizeIndex);
				String bucketName = cur.getString(bucketDisplayNameIndex);
				String bucketId = cur.getString(bucketIdIndex);
				String picasaId = cur.getString(picasaIdIndex);
				

				Log.i(TAG, _id + ", bucketId: " + bucketId + ", picasaId: "
						+ picasaId + " name:" + name + " path:" + path
						+ " title: " + title + " size: " + size + " bucket: "
						+ bucketName + "---");

				ImageBucket bucket = bucketList.get(bucketId);
				
				if (bucket == null) {
					bucket = new ImageBucket();
					bucketList.put(bucketId, bucket);
					bucket.imageList = new ArrayList<ImageItem>();
				    bucket.bucketName = bucketName;
				}
				
				bucket.count++;
				
				ImageItem imageItem = new ImageItem();
				imageItem.imageId = _id;
				imageItem.imagePath = path;
				imageItem.name=name;
				imageItem.thumbnailPath = thumbnailList.get(_id);
				bucket.imageList.add(imageItem);
				

			} while (cur.moveToNext());
		}
		
		//Iterator是Java迭代器最简单的实现，为List设计的ListIterator具有更多的功能，它可以从两个方向遍历List，也可以从List中插入和删除元素。
        ///获得HashMap的迭代器，用作遍历HashMap中的每一个键值对
		Iterator<Entry<String, ImageBucket>> itr = bucketList.entrySet()
				.iterator();
		while (itr.hasNext()) {
			Entry<String, ImageBucket> entry = (Entry<String, ImageBucket>) itr
					.next();
			ImageBucket bucket = entry.getValue();
			Log.d(TAG, entry.getKey() + ", " + bucket.bucketName + ", "
					+ bucket.count + " ---------- ");
			for (int i = 0; i < bucket.imageList.size(); ++i) {
				ImageItem image = bucket.imageList.get(i);
				Log.e("------------- ",i+"***"+ image.imageId + "-- " + image.imagePath+"--" + image.name + "--"
						 + image.thumbnailPath);
			}
		}
		hasBuildImagesBucketList = true;
		long endTime = System.currentTimeMillis();
		Log.d(TAG, "use time: " + (endTime - startTime) + " ms");
	}


	public List<ImageBucket> getImagesBucketList(boolean refresh) {
		//if (refresh || (!refresh && !hasBuildImagesBucketList)) {
		//	buildImagesBucketList();
		//}
		buildImagesBucketList();
		List<ImageBucket> tmpList = new ArrayList<ImageBucket>();
		
		Iterator<Entry<String, ImageBucket>> itr = bucketList.entrySet()
				.iterator();
		while (itr.hasNext()) {
			Entry<String, ImageBucket> entry = (Entry<String, ImageBucket>) itr
					.next();
			tmpList.add(entry.getValue());
		}
		return tmpList;
	}
	 //缩略图和原始图的对应
	String getOriginalImagePath(String image_id) {
		String path = null;
		Log.i(TAG, "---(^o^)----" + image_id);
		String[] projection = { Media._ID, Media.DATA };
		Cursor cursor = cr.query(Media.EXTERNAL_CONTENT_URI, projection,
				Media._ID + "=" + image_id, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			path = cursor.getString(cursor.getColumnIndex(Media.DATA));

		}
		return path;
	}

}
