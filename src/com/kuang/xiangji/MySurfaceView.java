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
		holder = getHolder();// 获得surfaceHolder引用
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 设置类型
	}

	public MySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		holder = getHolder();// 获得surfaceHolder引用
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 设置类型
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
        isBackCamera = !isBackCamera;
		// TODO Auto-generated method stub
		if (myCamera == null) {
			if (FindFrontCamera()!=-1) {
				changeCamera();
			}else{
				myCamera = Camera.open();// 开启相机,不能放在构造函数中，不然不会显示画面.
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
		myCamera.stopPreview();// 停止预览
		myCamera.release();// 释放相机资源
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
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置  
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
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置  
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
    		myCamera.stopPreview();// 停止预览
    		myCamera.release();// 释放相机资源
    		myCamera = null;
		}
		if (isBackCamera) {
			myCamera = Camera.open(FindFrontCamera());//打开当前选中的摄像头
			isBackCamera = false;
		} else {
			myCamera = Camera.open(FindBackCamera());//打开当前选中的摄像头
			isBackCamera = true;
		}
		try {
			myCamera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myCamera.startPreview();//开始预览
    }
}