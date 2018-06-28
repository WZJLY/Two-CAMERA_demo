package com.example.myapplication

import java.io.File

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.hardware.Camera
import android.media.MediaRecorder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceHolder.Callback
import android.view.SurfaceView
import android.view.ViewGroup
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : Activity() {
    private var surfaceView: SurfaceView? = null
    private var surfaceView2: SurfaceView? = null //视频显示区域的声明
    private var layout: RelativeLayout? = null   //相对布局的声明
    private var callback: Callback? = null   //
    private var camera: Camera? = null
    private var mediaRecorder: MediaRecorder? = null   //媒体录制

    private var mSurfaceHolder: SurfaceHolder? = null

    private var cameraacount = 0
    private var service:MyService?=null
    private var bound = false
    private var sc = MyseriviceConnection()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        layout = this.findViewById(R.id.layout) as RelativeLayout
        val intent = Intent(this,MyService::class.java)
        bindService(intent,sc,Context.BIND_AUTO_CREATE)

        surfaceView = this.findViewById(R.id.surfaceView) as SurfaceView
        mSurfaceHolder=surfaceView?.holder
        mSurfaceHolder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        callback = MyCallback()
        surfaceView!!.holder.addCallback(callback)
        start.setOnClickListener{

            start.isEnabled = false
            stop.isEnabled=true
            service!!.createWindowView()
            service!!.startVideo()
        }
        stop.setOnClickListener{
            Log.d("wzj","wzj")
            start!!.isEnabled = true
            stop!!.isEnabled = false
            service!!.stopVideo()

        }
    }

    private inner class MyCallback : Callback   //回调类
    {

        override fun surfaceChanged(arg0: SurfaceHolder, arg1: Int, arg2: Int,
                                    arg3: Int) {
            // TODO Auto-generated method stub

        }

        override fun surfaceCreated(arg0: SurfaceHolder) {
            // TODO Auto-generated method stub
            try {
                camera = Camera.open(0)
                camera!!.setPreviewDisplay(mSurfaceHolder)
                camera!!.startPreview()  //开始预览
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        override fun surfaceDestroyed(arg0: SurfaceHolder) {
                    // TODO Auto-generated method stub
                    if (camera != null) {
                        camera!!.stopPreview()   //停止预览
                        camera!!.release()      //释放资源
                        camera = null
            }
        }

    }



    override fun onTouchEvent(event: MotionEvent): Boolean {
        // TODO Auto-generated method stub
        layout!!.visibility = ViewGroup.VISIBLE   //当触屏的时候按钮可见
        return super.onTouchEvent(event)
    }
    inner class  MyseriviceConnection:ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val mybinder = p1 as MyService.MyBinder
            service=mybinder.getService()
            bound=true
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if(bound) unbindService(sc)
    }

}
