package com.havorld.customcamera.utils;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Surface;

public class PreviewSizeUtil {

	private static final String TAG = "Havorld";

	/**
	 * 获取最大的预览/图片尺寸
	 * 
	 * @return 预览尺寸集合
	 */
	public static Size getSupportSize(List<Size> listSize) {

		if (listSize == null || listSize.size() <= 0) {

			return null;
		}
		Size  largestSize = listSize.get(0);
		if (listSize.size() > 1) {
			Iterator<Camera.Size> iterator = listSize.iterator();
			while (iterator.hasNext()) {
				Camera.Size size = iterator.next();
				if (size.width > largestSize.width && size.height > largestSize.height) {
					largestSize = size;
				}
			}
		}  
		return largestSize;

	}

	/**
	 * 获取拍照时偏移方向的角度(此方法在手机方向没有锁定，为自由切换时易用)
	 * 
	 * @param activity
	 * @param cameraId
	 *            开启相机的id(0.后相机,1.前相机)
	 * @return 偏移的角度
	 */
	public static int getCameraDisplayOrientation(Activity activity,
			int cameraId) {

		CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		Log.e(TAG, "degrees:" + degrees);
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		Log.e(TAG, "result:" + result);
		return result;
	}

	/**
	 * 获取合适的预览尺寸(亲测可用)
	 * 
	 * @param parameters
	 * @param screenResolution
	 * @return point.x预览的宽,point.y预览的高
	 */
	public static Point getCameraResolution(Context context,
			Camera.Parameters parameters) {

		// 注意：大多会把相机屏幕翻转90度，所以设置相机宽高时对应的是高宽，此处在获取时做了颠倒
		Point screenResolution = new Point(ScreenUtil.getScreenHeight(context),
				ScreenUtil.getScreenWidth(context));
		String previewSizeValueString = parameters.get("preview-size-values");
		// saw this on Xperia
		if (previewSizeValueString == null) {
			previewSizeValueString = parameters.get("preview-size-value");
		}
		// 1920x1080,1440x1080,3840x2160,1280x720,960x720,864x480,800x480,768x432,720x480,640x480,576x432,480x320,384x288,352x288,320x240,240x160,176x144
		Log.e(TAG, "previewSizeValueString:" + previewSizeValueString);
		Point cameraResolution = null;

		if (previewSizeValueString != null) {
			cameraResolution = findBestPreviewSizeValue(previewSizeValueString,
					screenResolution);
		}

		if (cameraResolution == null) {
			// Ensure that the camera resolution is a multiple of 8, as the
			// screen may not be.
			cameraResolution = new Point((screenResolution.x >> 3) << 3,
					(screenResolution.y >> 3) << 3);

		}

		return cameraResolution;
	}

	private static final Pattern COMMA_PATTERN = Pattern.compile(",");

	private static Point findBestPreviewSizeValue(
			CharSequence previewSizeValueString, Point screenResolution) {
		int bestX = 0;
		int bestY = 0;
		int diff = Integer.MAX_VALUE;
		for (String previewSize : COMMA_PATTERN.split(previewSizeValueString)) {

			previewSize = previewSize.trim();
			int dimPosition = previewSize.indexOf('x');
			if (dimPosition < 0) {
				Log.e(TAG, "Bad preview-size: " + previewSize);
				continue;
			}

			int newX;
			int newY;
			try {
				newX = Integer.parseInt(previewSize.substring(0, dimPosition));
				newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
			} catch (NumberFormatException nfe) {
				Log.e(TAG, "Bad preview-size: " + previewSize);
				continue;
			}

			int newDiff = Math.abs(newX - screenResolution.x)
					+ Math.abs(newY - screenResolution.y);
			if (newDiff == 0) {
				bestX = newX;
				bestY = newY;
				break;
			} else if (newDiff < diff) {
				bestX = newX;
				bestY = newY;
				diff = newDiff;
			}

		}

		if (bestX > 0 && bestY > 0) {
			return new Point(bestX, bestY);
		}
		return null;
	}

}
