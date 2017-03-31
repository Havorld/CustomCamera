package com.havorld.customcamera.utils;

import java.io.IOException;

import android.media.ExifInterface;

public class ImageViewUtil {

	 
	/**
	 * 将图片的旋转角度置为0 ，此方法可以解决某些机型拍照后图像，出现了旋转情况
	 * 
	 * @Title: setPictureDegreeZero
	 * @param path
	 * @return void
	 * @date 2012-12-10 上午10:54:46
	 */
	private void setPictureDegreeZero(String path) {
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			// 修正图片的旋转角度，设置其不旋转。这里也可以设置其旋转的角度，可以传值过去，
			// 例如旋转90度，传值ExifInterface.ORIENTATION_ROTATE_90，需要将这个值转换为String类型的
			exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, "no");
			exifInterface.saveAttributes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
