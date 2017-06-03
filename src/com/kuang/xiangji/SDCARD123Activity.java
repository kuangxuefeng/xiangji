package com.kuang.xiangji;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class SDCARD123Activity extends Activity implements
		MediaScannerConnectionClient {
	public String[] allFiles;
	private String SCAN_PATH;
	private static final String FILE_TYPE = "image/*";
	private MediaScannerConnection conn;
	public static final String key_path = "key_path";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String path = getIntent().getStringExtra(key_path);

		File folder = new File(path);
		allFiles = folder.list();

		if (null == allFiles || allFiles.length < 1) {
			Toast.makeText(this, "无截图", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		SCAN_PATH = path + "/" + allFiles[allFiles.length - 1];
		startScan();
	}

	private void startScan() {
		if (conn != null) {
			conn.disconnect();
		}
		conn = new MediaScannerConnection(this, this);
		conn.connect();
	}

	@Override
	public void onMediaScannerConnected() {
		conn.scanFile(SCAN_PATH, FILE_TYPE);
	}

	@Override
	public void onScanCompleted(String path, Uri uri) {
		try {
			if (uri != null) {
				Intent intent = new Intent(Intent.ACTION_VIEW);// 改成Intent.ACTION_PICK的话，就是正常的打开所有图片的图库
				intent.setData(uri);
				startActivity(intent);
			}
		} finally {
			conn.disconnect();
			conn = null;
		}
		finish();
	}
}