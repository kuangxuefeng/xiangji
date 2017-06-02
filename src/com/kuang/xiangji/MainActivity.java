package com.kuang.xiangji;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.kuang.xiangji.MySurfaceView.MyCallBack;

public class MainActivity extends Activity implements OnClickListener,
		AutoFocusCallback {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	SimpleDateFormat sdfFile = new SimpleDateFormat("yyyyMMddHHmmss");

	MySurfaceView mySurface;// surfaceView����
	SurfaceHolder holder;// surfaceHolder����
	Camera myCamera;// �������
	String filePath = "/sdcard/wjh.jpg";// ��Ƭ����·��
	boolean isClicked = false;// �Ƿ�����ʶ
	private TextView tv_time;

	/**
	 * Ԥ���Ĳ�����myParameters.setPreviewSize(1280, 720��

ͼƬ������myParameters.setPictureSize(2048, 1152); //1280, 720

ͼƬ���ճߴ�:��600 * ��800
	 */
	// ����jpegͼƬ�ص����ݶ���
	PictureCallback jpeg = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			try {// ���ͼƬ
				Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
				
//				String SavePath = getSDCardPath() + "/AndyDemo/ScreenImage/a";
//				File path = new File(SavePath);
//				// �ļ�
//				String filepath = SavePath + "/Screen22_"
//						+ sdfFile.format(new Date()) + ".png";
//				File file = new File(filepath);
//				if (!path.exists()) {
//					path.mkdirs();
//				}
//				if (!file.exists()) {
//					file.createNewFile();
//				}
//				
//	            BufferedOutputStream bos =      
//	                new BufferedOutputStream(new FileOutputStream(file));      
//	            bm.compress(Bitmap.CompressFormat.PNG, 100, bos);//��ͼƬѹ��������      
//	            bos.flush();//���      
//	            bos.close();//�ر� 
	            
	            try {
//					Bitmap bitmap = BitmapFactory.decodeFile(filepath);
					saveImage(bm);
				} catch (Exception e) {
					e.printStackTrace();
				}
	            
	            myCamera.startPreview();// ����Ԥ��
				isClicked = false;
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mySurface = (MySurfaceView) findViewById(R.id.my_camera);
		tv_time = (TextView) findViewById(R.id.tv_time);
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					runOnUiThread(new Runnable() {
						public void run() {
							tv_time.setText(getTimeString());
						}
					});
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

		// //��þ��
		// holder = mySurface.getHolder();
		// //��������
		// holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// ���ü���
		mySurface.setOnClickListener(this);
		mySurface.setMyCallBack(new MyCallBack() {

			@Override
			public void mySurfaceChanged() {
				tv_time.setText(sdf.format(new Date()));
			}

			@Override
			public void mySurfaceCreated(Camera myCamera) {
				MainActivity.this.myCamera = myCamera;
			}
		});
	}

	@Override
	public void onClick(View paramView) {
		// GetandSaveCurrentImage();
		if (!isClicked) {
			myCamera.autoFocus(this);// �Զ��Խ�
			isClicked = true;
		} else {
			myCamera.startPreview();// ����Ԥ��
			isClicked = false;
		}
	}

	private void saveImage(Bitmap bmp) {
		Bitmap Bmp = Bitmap.createBitmap(1280, 720, Config.ARGB_8888);
		Bmp = bmp.copy(Config.ARGB_8888, true);
		
		Paint paint = new Paint();
		paint.setTextSize(100);
		paint.setColor(Color.RED);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
		Canvas canvas = new Canvas(Bmp);
		canvas.drawText(getTimeString(), Bmp.getWidth()/2, Bmp.getHeight()/2, paint);

		String SavePath = getSDCardPath() + "/AndyDemo/ScreenImage";

		// 3.����Bitmap
		try {
			File path = new File(SavePath);
			// �ļ�
			String filepath = SavePath + "/ScreenIm_"
					+ sdfFile.format(new Date()) + ".png";
			File file = new File(filepath);
			if (!path.exists()) {
				path.mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}

			FileOutputStream fos = null;
			fos = new FileOutputStream(file);
			if (null != fos) {
				Bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();

				Toast.makeText(this, "�����ļ��ѱ�����SDCard/AndyDemo/ScreenImage/��",
						Toast.LENGTH_LONG).show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡ�ͱ��浱ǰ��Ļ�Ľ�ͼ
	 */
	private void GetandSaveCurrentImage() {
		// 1.����Bitmap
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int w = display.getWidth();
		int h = display.getHeight();

		Bitmap Bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);

		// 2.��ȡ��Ļ
		View decorview = this.getWindow().getDecorView();
		decorview.setDrawingCacheEnabled(true);
		Bmp = decorview.getDrawingCache();

		Paint paint = new Paint();
		paint.setTextSize(5);
		paint.setColor(Color.RED);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
		Canvas canvas = new Canvas(Bmp);
		canvas.drawText(getTimeString(), 20, 20, paint);

		String SavePath = getSDCardPath() + "/AndyDemo/ScreenImage";

		// 3.����Bitmap
		try {
			File path = new File(SavePath);
			// �ļ�
			String filepath = SavePath + "/Screen_1.png";
			File file = new File(filepath);
			if (!path.exists()) {
				path.mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}

			FileOutputStream fos = null;
			fos = new FileOutputStream(file);
			if (null != fos) {
				Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();

				Toast.makeText(this, "�����ļ��ѱ�����SDCard/AndyDemo/ScreenImage/��",
						Toast.LENGTH_LONG).show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡSDCard��Ŀ¼·������
	 * 
	 * @return
	 */
	private String getSDCardPath() {
		File sdcardDir = null;
		// �ж�SDCard�Ƿ����
		boolean sdcardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdcardExist) {
			sdcardDir = Environment.getExternalStorageDirectory();
		}
		return sdcardDir.toString();
	}

	@Override
	public void onAutoFocus(boolean paramBoolean, Camera paramCamera) {
		if (paramBoolean) {
			// ���ò���,������
			Camera.Parameters params = myCamera.getParameters();
			params.setPictureFormat(PixelFormat.JPEG);
			params.setPreviewSize(1280, 720);
			myCamera.setParameters(params);
			myCamera.takePicture(null, null, jpeg);
		}
	}
	
	private String getTimeString() {
		return "����(���ߣ���)�� " + sdf.format(new Date());
	}
}
