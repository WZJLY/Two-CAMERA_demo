package com.example.myapplication

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.Camera
import android.hardware.Camera.Parameters.FLASH_MODE_AUTO
import android.media.MediaRecorder
import android.os.Binder
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import java.io.File
import java.text.SimpleDateFormat
import java.util.*



class MyService : Service()  {
    private var surfaceView3: SurfaceView? = null
    private var callback: SurfaceHolder.Callback? = null
    private var isAdded:Boolean? = false
    private var camera2: Camera? = null
    private var mediaRecorder2: MediaRecorder? = null
    private var binder = MyBinder()
    private val TAG = "MyService"
    private var sSurfaceHolder: SurfaceHolder? = null
    override fun onBind(intent: Intent): IBinder {
        Log.w(TAG,"on bind")
        return binder
    }
    inner class MyBinder:Binder(){
        fun getService()=this@MyService

    }


    override fun onCreate() {

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.w(TAG,"on start command")
        Log.w(TAG,"MyService:"+this)
        Log.w(TAG,"name:"+intent?.getStringExtra("name"))
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.w(TAG,"on unbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        stopVideo()
    }


    fun createWindowView() {
        surfaceView3 = SurfaceView(this)
        sSurfaceHolder = surfaceView3?.holder
        sSurfaceHolder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        callback=MyServiceCallback()
        surfaceView3!!.holder.addCallback(callback)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        params = WindowManager.LayoutParams()
        //设置Window Type
        params!!.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        //设置悬浮框不可触摸
        params!!.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager
                .LayoutParams.FLAG_NOT_FOCUSABLE
        //悬浮框不可触摸，不接受任何时间，同时不影响后面得时间响应
        params!!.format = PixelFormat.RGB_888
        params!!.width = 1
        params!!.height = 1
        params!!.gravity = Gravity.LEFT
        params!!.x = 200
        params!!.y = 200
        windowManager!!.addView(surfaceView3, params)
        isAdded=true
    }

    companion object {
        private var windowManager: WindowManager? = null
        private var params: WindowManager.LayoutParams? = null
    }
    private inner class MyServiceCallback : SurfaceHolder.Callback   //回调类
    {

        override fun surfaceChanged(arg0: SurfaceHolder, arg1: Int, arg2: Int,
                                    arg3: Int) {
            // TODO Auto-generated method stub

        }

        override fun surfaceCreated(arg0: SurfaceHolder) {
            // TODO Auto-generated method stub
            try {
                if(Camera.getNumberOfCameras()==2)
                {
                    camera2 = Camera.open(1)
                    camera2!!.parameters.flashMode=FLASH_MODE_AUTO
                    startVideo()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        override fun surfaceDestroyed(arg0: SurfaceHolder) {
            // TODO Auto-generated method stub
        }

    }
    fun startVideo() {

        try {
            if(camera2!=null) {
                var formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                var curDate = Date(System.currentTimeMillis())
                var str = formatter.format(curDate)
                var file = File(Environment.getExternalStorageDirectory().toString()
                        + "/" + str + ".3gp")
                mediaRecorder2 = MediaRecorder()//媒体录制对象
                camera2!!.unlock()
                mediaRecorder2!!.setCamera(camera2)   //设置摄像
                mediaRecorder2!!.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder2!!.setVideoSource(MediaRecorder.VideoSource.CAMERA)
                mediaRecorder2!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)  //设置输出的文件的格式
                mediaRecorder2!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)   //设置编码
                mediaRecorder2!!.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                mediaRecorder2!!.setOutputFile(file.absolutePath)   //设置输出文件的路径
                mediaRecorder2!!.setVideoSize(320, 240)  //设置video的大小
                mediaRecorder2!!.setOrientationHint(180)//设置方向
                mediaRecorder2!!.setVideoFrameRate(5)
                mediaRecorder2!!.setPreviewDisplay(surfaceView3!!.holder.surface)
                mediaRecorder2!!.prepare()   //缓冲
                mediaRecorder2!!.start()   //开始录制
            }
        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
        }

    }
    fun stopVideo(){
        if(isAdded==true){
            mediaRecorder2?.stop()
            mediaRecorder2?.reset()
            mediaRecorder2?.release()
            camera2?.lock()
            camera2?.release()
            windowManager?.removeView(surfaceView3)
            Log.w(TAG,"on destroy")
            isAdded=false
        }
    }

}



