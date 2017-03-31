package com.havorld.customcamera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.havorld.customcamera.utils.PreviewSizeUtil;

public class CustomCameraActivity extends Activity implements OnClickListener,
		SurfaceHolder.Callback, Camera.PictureCallback {

	private Camera camera;
	private static final String TAG = "Havorld";
	private Camera.Parameters parameters;
	private int orientationDegrees = 90;
	private FrameLayout frameLayout;
	private ImageButton imageButton, reset, ok;
	/** ·��: /storage/emulated/0/Pictures/ */
	private String savePath;
	private String path;
	private SurfaceHolder surfaceHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		savePath = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
				+ File.separator;
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		surfaceView.setOnClickListener(this);

		frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
		imageButton = (ImageButton) findViewById(R.id.imageButton);
		reset = (ImageButton) findViewById(R.id.reset);
		ok = (ImageButton) findViewById(R.id.ok);

		frameLayout.setOnClickListener(this);
		imageButton.setOnClickListener(this);
		reset.setOnClickListener(this);
		ok.setOnClickListener(this);

		// ��SurfaceHolder,SurfaceHolder�൱��һ��������,����ͨ��CallBack������ SurfaceView�ϵı仯��
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		// Ϊ��ʵ����ƬԤ�����ܣ���Ҫ��SurfaceHolder����������ΪPUSH,������ͼ�������Camera����������ͼ�����Ƕ�����Surface��
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		startOrientationChangeListener();
	}

	private final void startOrientationChangeListener() {

		OrientationEventListener mOrEventListener = new OrientationEventListener(
				this) {
			@Override
			public void onOrientationChanged(int rotation) {

				if (((rotation >= 0) && (rotation <= 45)) || (rotation >= 315)) {

					orientationDegrees = 90;
				} else if ((rotation > 45) && (rotation < 135)) {

					orientationDegrees = 180;
				} else if ((rotation >= 135) && (rotation <= 225)) {

					orientationDegrees = 270;
				} else if ((rotation > 225) && (rotation < 315)) {

					orientationDegrees = 0;
				}

				// Log.e(TAG, "rotation��"+rotation);

			}
		};
		mOrEventListener.enable();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.e(TAG, "surfaceCreated");
		surfaceHolder = holder;
		initCamera();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.e(TAG, "surfaceChanged");

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.e(TAG, "surfaceDestroyed");

		// ��Surface�����ٵ�ʱ�򣬸÷���������
		// ��������Ҫ�ͷ�Camera��Դ
		releaseCamera();
	}

	@SuppressWarnings("deprecation")
	private void initCamera() {

		// �ж��Ƿ�������ͷ
		if (!getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_CAMERA))
			return;

		// ��ȡ����ͷ�ĸ���
		// int numberOfCameras = Camera.getNumberOfCameras();
		// ��������ͷ
		camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
		/**
		 * ��ȡ�豸Camera���Բ��������ڲ�ͬ���豸Camera�������ǲ�ͬ�ģ�����������ʱ��Ҫ�����ж��豸��Ӧ�����ԣ��ټ�������
		 */
		parameters = camera.getParameters();
		// ��ȡ��Ԥ���ĳߴ缯��
		List<Size> supportedPreviewSizes = parameters
				.getSupportedPreviewSizes();
		Size previewSize = PreviewSizeUtil
				.getSupportSize(supportedPreviewSizes);
		// ��ȡ������ͼƬ�Ĵ�С����
		List<Size> supportedPictureSizes = parameters
				.getSupportedPictureSizes();
		// ������������ͼƬ
		// Size pictureSize =
		// PreviewSizeUtil.getSupportSize(supportedPictureSizes);
		Size pictureSize = supportedPictureSizes.get((supportedPictureSizes
				.size() - 1) / 2);
		// ��ȡ�����õ�֡��
		List<Integer> supportedPreviewFrameRates = parameters
				.getSupportedPreviewFrameRates();
		Integer frameRates = supportedPreviewFrameRates
				.get((supportedPreviewFrameRates.size() - 1) / 2);
		// ����Camera�Ĳ���
		parameters.setPreviewSize(previewSize.width, previewSize.height);
		parameters.setPictureSize(pictureSize.width, pictureSize.height);
		// ����֡��(ÿ�������ͷ�����ü�������)
		parameters.setPreviewFrameRate(frameRates);

		// ����ͼƬ��ʽ
		parameters.setPictureFormat(ImageFormat.JPEG);
		// ������Ƭ����
		parameters.setJpegQuality(100);

		// ���Ȼ�ȡϵͳ�豸֧�ֵ�������ɫ��Ч������豸��֧����ɫ���Խ�����һ��null�� ����з������ǵ�������
		List<String> colorEffects = parameters.getSupportedColorEffects();
		Iterator<String> colorItor = colorEffects.iterator();
		while (colorItor.hasNext()) {
			String currColor = colorItor.next();
			if (currColor.equals(Camera.Parameters.EFFECT_SOLARIZE)) {
				// parameters.setColorEffect(Camera.Parameters.EFFECT_AQUA);
				break;
			}
		}

		// ��ȡ�Խ�ģʽ
		List<String> focusModes = parameters.getSupportedFocusModes();
		// [auto, infinity, macro, continuous-video, continuous-picture, manual]
		if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
			// �����Զ��Խ�
			parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
		}

		// ����������Զ�����
		List<String> flashModes = parameters.getSupportedFlashModes();
		if (flashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
			// �Զ�����
			parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
		}

		int orientationDegrees = PreviewSizeUtil.getCameraDisplayOrientation(
				this, Camera.CameraInfo.CAMERA_FACING_BACK);
		// ���������ͷ��ת�Ƕ�(Ĭ������ͷ�Ǻ���)
		camera.setDisplayOrientation(orientationDegrees);

		// ������Ƭ��������ת�Ƕ�(Ĭ������ͷ�Ǻ��)
		// parameters.set("rotation", orientationDegrees);

		if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {

			// Log.e(TAG, "����");
			// parameters.set("orientation", "portrait");
		} else {

			// Log.e(TAG, "����");
			// parameters.set("orientation", "landscape");
		}

		try {
			// ������ʾ
			camera.setPreviewDisplay(surfaceHolder);

		} catch (IOException exception) {

			releaseCamera();
		}
		// ���������Ҫ�ٴε���setParameter����������Ч
		camera.setParameters(parameters);

		// ��ʼԤ��
		camera.startPreview();

	}

	private void releaseCamera() {
		if (camera != null) {

			camera.setPreviewCallback(null);
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {

		// data��һ��ԭʼ��JPEGͼ�����ݣ�
		// ���������ǿ��Դ洢ͼƬ������Ȼ���Բ���MediaStore
		// ע�Ᵽ��ͼƬ���ٴε���startPreview()�ص�Ԥ��
		// Uri imageUri =
		// getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
		// new ContentValues());
		// try {
		// OutputStream os = getContentResolver().openOutputStream(imageUri);
		// os.write(data);
		// os.flush();
		// os.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		path = savePath + "XiuMF_" + System.currentTimeMillis() + ".jpg";
		File file = new File(path);
		OutputStream output = null;
		try {
			output = new FileOutputStream(file);
			output.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// ���պ�Ԥ����ֹͣ�����¿�����
		camera.startPreview();

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.frameLayout:
		case R.id.imageButton:

			if (parameters != null) {
				// ������Ƭ��������ת�Ƕ�(Ĭ������ͷ�Ǻ��)
				// parameters.setRotation(orientationDegrees);
				parameters.set("rotation", orientationDegrees);
				// ��ȡ��ǰ�ֻ���Ļ����
				camera.setParameters(parameters);
				Log.e(TAG, "orientationDegrees:" + orientationDegrees);
			}
			/**
			 * ShutterCallback shutter:���¿��ŵĻص� PictureCallback raw:ԭʼͼ�����ݵĻص�
			 * PictureCallback postview:ѹ��ͼ�� PictureCallback
			 * jpeg:ѹ����jpg��ʽ��ͼ�����ݵĻص�
			 */
			camera.takePicture(null, null, null, this);
			break;
		case R.id.ok:// ��ȡ��Ƭ
			Intent intent = getIntent();
			if (intent != null) {
				intent.putExtra("path", path);
				setResult(-1, intent);
			}
			finish();
			break;
		case R.id.reset: // ɾ����Ƭ
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file = null;
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseCamera();
	}
}
