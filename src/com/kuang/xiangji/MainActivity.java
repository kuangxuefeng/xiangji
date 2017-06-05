package com.kuang.xiangji;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kuang.xiangji.MySurfaceView.MyCallBack;

public class MainActivity extends Activity implements OnClickListener,
		AutoFocusCallback, MediaScannerConnectionClient {
	private static final String TAG = "MainActivity";

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	SimpleDateFormat sdfFile = new SimpleDateFormat("MMddHHmmss");

	// (176*144,320*240,352*288,480*360,640*480)
	private int imageW = 640;
	private int imageH = 480;

	MySurfaceView mySurface;// surfaceView����
	SurfaceHolder holder;// surfaceHolder����
	Camera myCamera;// �������
	String filePath = "/sdcard/wjh.jpg";// ��Ƭ����·��
//	boolean isClicked = false;// �Ƿ�����ʶ
	private TextView tv_time;
	private ImageView iv_change, iv_photo, iv_pai;
	private float myAlpha = 1f;

	private Typeface tf = null;
	private static final long dateOut = 20170615L;

	public String[] allFiles;
	private String SCAN_PATH;
	private static final String FILE_TYPE = "image/*";
	private MediaScannerConnection conn;

	/**
	 * Ԥ���Ĳ�����myParameters.setPreviewSize(1280, 720��
	 * 
	 * ͼƬ������myParameters.setPictureSize(2048, 1152); //1280, 720
	 * 
	 * ͼƬ���ճߴ�:��600 * ��800
	 */
	// ����jpegͼƬ�ص����ݶ���
	PictureCallback jpeg = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			try {// ���ͼƬ

				Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
				Log.e(TAG, "bm=" + bm);
				myCamera.startPreview();// ����Ԥ��
				saveImage(bm);
				initImage();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);


		AssetManager as = this.getAssets();
		tf = Typeface.createFromAsset(as, "xiangsu.TTF");

		mySurface = (MySurfaceView) findViewById(R.id.my_camera);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_time.setTypeface(tf);
		tv_time.setAlpha(myAlpha);

		iv_change = (ImageView) findViewById(R.id.iv_change);
		iv_change.setOnClickListener(this);

		iv_photo = (ImageView) findViewById(R.id.iv_photo);
		iv_photo.setOnClickListener(this);

		iv_pai = (ImageView) findViewById(R.id.iv_pai);
		iv_pai.setOnClickListener(this);

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
			public void mySurfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				if (true) {
					return;
				}
				// �Ѿ����Surface��width��height������Camera�Ĳ���
				Camera.Parameters parameters = myCamera.getParameters();
				parameters.setPreviewSize(width, height);

				List<Size> vSizeList = parameters.getSupportedPictureSizes();

				for (int num = 0; num < vSizeList.size(); num++) {
					Size vSize = vSizeList.get(num);
				}
				if (MainActivity.this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
					// ���������
					// parameters.set("orientation", "portrait");
					// ��2.2���Ͽ���ʹ��
					myCamera.setDisplayOrientation(90);
				} else {
					// parameters.set("orientation", "landscape");
					// ��2.2���Ͽ���ʹ��
					myCamera.setDisplayOrientation(0);
				}
			}

			@Override
			public void mySurfaceCreated(Camera myCamera) {

				if (MainActivity.this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
					// ���������
					// parameters.set("orientation", "portrait");
					// ��2.2���Ͽ���ʹ��
					myCamera.setDisplayOrientation(90);
				} else {
					// parameters.set("orientation", "landscape");
					// ��2.2���Ͽ���ʹ��
					myCamera.setDisplayOrientation(0);
				}
				MainActivity.this.myCamera = myCamera;
			}
		});
		initImage();
		checkDate();
	}

	private void checkDate() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				long timeL = dateOut;
				try {
					timeL = Long.parseLong(getTimeCurr("yyyyMMdd"));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				if (timeL >= dateOut) {
					runOnUiThread(new Runnable() {
						public void run() {
							Intent intent = new Intent(MainActivity.this,
									ShowInfoActivity.class);
							startActivity(intent);
							finish();
						}
					});
				}
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.my_camera:
		case R.id.iv_pai:
			myCamera.autoFocus(this);// �Զ��Խ�
			// if (!isClicked) {
			// myCamera.autoFocus(this);// �Զ��Խ�
			// isClicked = true;
			// } else {
			// myCamera.startPreview();// ����Ԥ��
			// isClicked = false;
			// }
			break;

		case R.id.iv_change:
			if (mySurface.FindFrontCamera() == -1) {
				Toast.makeText(this, "��ǰ������ͷ", Toast.LENGTH_SHORT).show();
				return;
			}
			mySurface.changeCamera();
			myCamera = mySurface.getMyCamera();
			if (MainActivity.this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
				// ���������
				// parameters.set("orientation", "portrait");
				// ��2.2���Ͽ���ʹ��
				myCamera.setDisplayOrientation(90);
			} else {
				// parameters.set("orientation", "landscape");
				// ��2.2���Ͽ���ʹ��
				myCamera.setDisplayOrientation(0);
			}
			break;

		case R.id.iv_photo:
			Log.e(TAG, "R.id.iv_photo");
			// Intent intent=new Intent(Intent.ACTION_VIEW);
			// File picFile = new File(getBasePath());
			// //�ƶ����ݵ�����Ϊͼ��
			// // intent.setDataAndType(Uri.fromFile(picFile), "image/*");
			// intent.setData(Uri.fromFile(picFile));
			// //�ƶ�����ϵͳ���ݵ�action
			// // intent.setAction(Intent.ACTION_GET_CONTENT);
			// //��ʾϵͳ���
			// startActivity(intent);

			// Intent in = new Intent(this, SDCARD123Activity.class);
			// in.putExtra(SDCARD123Activity.key_path, getBasePath());
			// startActivity(in);

			String path = getBasePath();

			File folder = new File(path);
			allFiles = folder.list();

			if (null == allFiles || allFiles.length < 1) {
				Toast.makeText(this, "�޽�ͼ", Toast.LENGTH_SHORT).show();
				return;
			}

			SCAN_PATH = path + "/" + allFiles[allFiles.length - 1];
			startScan();
			break;
		default:
			break;
		}

	}

	private void saveImage(Bitmap bmp) {
		Bitmap bmpCopy = Bitmap.createBitmap(imageW, imageH, Config.ARGB_8888);
		bmpCopy = bmp.copy(Config.ARGB_8888, true);

		Paint paint = new Paint();
		Log.e(TAG, "Bmp.getHeight()=" + bmpCopy.getHeight());
		paint.setTextSize(bmpCopy.getHeight() / 20);
		paint.setColor(Color.WHITE);
		paint.setTypeface(tf);
		paint.setAlpha((int) (255 * myAlpha));
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

		// ����������
		Matrix matrix = new Matrix();
		// ����ԭͼ
		matrix.postScale(1f, 1f);
		// ����Ϊ����������ת
		if (mySurface.getIsBackCamera()) {
			matrix.postRotate(90);
		} else {
			matrix.postRotate(-90);
		}
		// bmp.getWidth(), 500�ֱ��ʾ�ػ���λͼ���
		Bitmap dstbmp = Bitmap.createBitmap(bmpCopy, 0, 0, bmpCopy.getWidth(),
				bmpCopy.getHeight(), matrix, true);
		Canvas canvas = new Canvas(dstbmp);

		canvas.drawText(getTimeString(), dstbmp.getWidth() / 2,
				dstbmp.getHeight() / 2, paint);

		String SavePath = getBasePath();

		// 3.����Bitmap
		try {
			File path = new File(SavePath);
			// �ļ�
			String filepath = SavePath + "/img_" + sdfFile.format(new Date())
					+ ".jpg";
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
				dstbmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();

				Toast.makeText(this, "�����ļ��ѱ�����" + getBasePath() + "��",
						Toast.LENGTH_LONG).show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getBasePath() {
		String savePath = getSDCardPath() + "/DCIM/feng";// /feng/ScreenImage  /DCIM/Camera
															// camera
		return savePath;
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
		Log.e(TAG, "�Խ������" + paramBoolean);
		if (paramBoolean || (!mySurface.getIsBackCamera())) {
			Toast.makeText(this, "����", Toast.LENGTH_SHORT).show();
			// ���ò���,������
			Camera.Parameters params = myCamera.getParameters();
			params.setPictureFormat(PixelFormat.JPEG);
			params.setPreviewSize(imageW, imageH);
			myCamera.setParameters(params);
			myCamera.takePicture(null, null, jpeg);
		} else {
			Toast.makeText(this, "�Խ�ʧ�ܣ����������գ�", Toast.LENGTH_SHORT).show();
			myCamera.startPreview();// ����Ԥ��
		}
	}

	private String getTimeString() {
		return "EXP " + sdf.format(new Date());
	}

	private String getTimeCurr(String format) {
		URLConnection uc = null;
		Date date = null;
		try {
			URL url = new URL("http://www.baidu.com");// ȡ����Դ����
			uc = url.openConnection();
			uc.connect(); // ��������
			long ld = uc.getDate(); // ȡ����վ����ʱ��
			date = new Date(ld); // ת��Ϊ��׼ʱ�����
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null == date) {
			date = new Date();
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String re = sdf.format(date);
		Log.e(TAG, "re=" + re);
		return re;
	}

	private void startScan() {
		try {
			if (conn != null) {
				conn.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
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
				Intent intent = new Intent(Intent.ACTION_VIEW);// �ĳ�Intent.ACTION_PICK�Ļ������������Ĵ�����ͼƬ��ͼ��
				intent.setData(uri);
				startActivity(intent);
			}
		} catch(Exception e){
			Log.e(TAG, "ACTION_VIEW  Exception");
			e.printStackTrace();
			Intent intent = new Intent(Intent.ACTION_VIEW);// �ĳ�Intent.ACTION_PICK�Ļ������������Ĵ�����ͼƬ��ͼ��
			intent.setData(Uri.fromFile(new File(SCAN_PATH)));
			startActivity(intent);
		} finally {
			conn.disconnect();
			conn = null;
		}
	}

	private void initImage() {
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				
//			}
//		});
		String path = getBasePath();
		File folder = new File(path);
		allFiles = folder.list();
		if (null == allFiles || allFiles.length < 1) {
			Log.e(TAG, "null == allFiles || allFiles.length < 1");
			return;
		} else {
			File picFile = new File(path + "/" + allFiles[allFiles.length - 1]);
			Uri uri = Uri.fromFile(picFile);
			iv_photo.setImageURI(uri);
		}
	}
	
	@Override
	protected void onDestroy() {
		if (conn != null) {
			conn.disconnect();
		}
		super.onDestroy();
	}
}
