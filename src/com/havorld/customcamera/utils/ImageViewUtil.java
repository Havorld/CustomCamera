package com.havorld.customcamera.utils;

import java.io.IOException;

import android.media.ExifInterface;

public class ImageViewUtil {

	 
	/**
	 * ��ͼƬ����ת�Ƕ���Ϊ0 ���˷������Խ��ĳЩ�������պ�ͼ�񣬳�������ת���
	 * 
	 * @Title: setPictureDegreeZero
	 * @param path
	 * @return void
	 * @date 2012-12-10 ����10:54:46
	 */
	private void setPictureDegreeZero(String path) {
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			// ����ͼƬ����ת�Ƕȣ������䲻��ת������Ҳ������������ת�ĽǶȣ����Դ�ֵ��ȥ��
			// ������ת90�ȣ���ֵExifInterface.ORIENTATION_ROTATE_90����Ҫ�����ֵת��ΪString���͵�
			exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, "no");
			exifInterface.saveAttributes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
