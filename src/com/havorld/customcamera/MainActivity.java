package com.havorld.customcamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnClickListener {
	private static final String TAG = "Havorld";
	private ImageView iv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.btn).setOnClickListener(this);
		iv = (ImageView) findViewById(R.id.iv);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btn:
			intent = new Intent(this, CustomCameraActivity.class);
			// 返回能处理该Intent的第一个Activity
			// ComponentName resolveActivity =
			// intent.resolveActivity(getPackageManager());
			startActivityForResult(intent, 1);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1 && data != null) {
			String path = data.getStringExtra("path");
			Log.e(TAG, "path:" + path);
			if (path != null) {
				Bitmap bitmap = BitmapFactory.decodeFile(path);
				iv.setImageBitmap(bitmap);
			}
		}
	}

}
