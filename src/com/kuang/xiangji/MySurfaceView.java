package com.kuang.xiangji;

import java.io.IOException;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MySurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {
	private SurfaceHolder holder;
	private Camera myCamera;
	private MyCallBack myCallBack;
	private boolean isBackCamera = true;
	
	public Camera getMyCamera(){
		return myCamera;
	}
	
	public boolean getIsBackCamera(){
		return isBackCamera;
	}
	
	public void setMyCallBack(MyCallBack cb){
		myCallBack = cb;
	}

	public MySurfaceView(Context context) {
		super(context);
		holder = getHolder();// ���surfaceHolder����
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// ��������
	}

	public MySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		holder = getHolder();// ���surfaceHolder����
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// ��������
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
        isBackCamera = !isBackCamera;
		// TODO Auto-generated method stub
		if (myCamera == null) {
			if (FindFrontCamera()!=-1) {
				changeCamera();
			}else{
				myCamera = Camera.open();// �������,���ܷ��ڹ��캯���У���Ȼ������ʾ����.
				try {
					myCamera.setPreviewDisplay(holder);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (null!=myCallBack) {
			myCallBack.mySurfaceCreated(myCamera);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		myCamera.startPreview();
		if (null!=myCallBack) {
			myCallBack.mySurfaceChanged(holder, format, width, height);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		myCamera.stopPreview();// ֹͣԤ��
		myCamera.release();// �ͷ������Դ
		myCamera = null;
	}
	
	public interface MyCallBack{
		void mySurfaceChanged(SurfaceHolder holder, int format, int width,
				int height);
		void mySurfaceCreated(Camera myCamera);
	}
	
	@TargetApi(9)  
	public int FindFrontCamera(){  
        int cameraCount = 0;  
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();  
        cameraCount = Camera.getNumberOfCameras(); // get cameras number  
                
        for ( int camIdx = 0; camIdx < cameraCount;camIdx++ ) {  
            Camera.getCameraInfo( camIdx, cameraInfo ); // get camerainfo  
            if ( cameraInfo.facing ==Camera.CameraInfo.CAMERA_FACING_FRONT ) {   
                // ��������ͷ�ķ�λ��Ŀǰ�ж���ֵ�����ֱ�ΪCAMERA_FACING_FRONTǰ�ú�CAMERA_FACING_BACK����  
               return camIdx;  
            }  
        }  
        return -1;  
    }  
    @TargetApi(9)  
    public int FindBackCamera(){  
        int cameraCount = 0;  
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();  
        cameraCount = Camera.getNumberOfCameras(); // get cameras number  
                
        for ( int camIdx = 0; camIdx < cameraCount;camIdx++ ) {  
            Camera.getCameraInfo( camIdx, cameraInfo ); // get camerainfo  
            if ( cameraInfo.facing ==Camera.CameraInfo.CAMERA_FACING_BACK ) {   
                // ��������ͷ�ķ�λ��Ŀǰ�ж���ֵ�����ֱ�ΪCAMERA_FACING_FRONTǰ�ú�CAMERA_FACING_BACK����  
               return camIdx;  
            }  
        }  
        return -1;  
    }
    
    public void changeCamera(){
    	if (FindFrontCamera()==-1) {
			return;
		}
    	if (null!=myCamera) {
    		myCamera.stopPreview();// ֹͣԤ��
    		myCamera.release();// �ͷ������Դ
    		myCamera = null;
		}
		if (isBackCamera) {
			myCamera = Camera.open(FindFrontCamera());//�򿪵�ǰѡ�е�����ͷ
			isBackCamera = false;
		} else {
			myCamera = Camera.open(FindBackCamera());//�򿪵�ǰѡ�е�����ͷ
			isBackCamera = true;
		}
		try {
			myCamera.setPreviewDisplay(holder);//ͨ��surfaceview��ʾȡ������
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myCamera.startPreview();//��ʼԤ��
    }
}