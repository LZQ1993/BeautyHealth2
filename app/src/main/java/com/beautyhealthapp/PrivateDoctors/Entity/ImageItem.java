package com.beautyhealthapp.PrivateDoctors.Entity;

import android.graphics.Bitmap;

import java.io.IOException;
import java.io.Serializable;


public class ImageItem implements Serializable {
	public String imageId;   //图片ID
	public String name;
	public String thumbnailPath; //缩略图路径
	public String imagePath; //图片路径
	private Bitmap bitmap;
	public boolean isSelected = false;

	public String getImageId() {
		return imageId;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getThumbnailPath() {
		return thumbnailPath;
	}
	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public Bitmap getBitmap() {		
		if(bitmap == null){
			try {
				bitmap = Bimp.revitionImageSize(imagePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public boolean equals(Object obj){  
		
		if(this == obj)                      
			return true;
		if(obj == null)         
			return false;
		if( !(obj instanceof ImageItem))
			return false;
		final ImageItem imageItem = (ImageItem)obj;
		if(getName().equals(imageItem.getName()))
			return true;
		 return false;
	}
	
	
	
	
}
