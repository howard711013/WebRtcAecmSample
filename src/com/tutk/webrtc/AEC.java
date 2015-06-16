package com.tutk.webrtc;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.util.Log;

public class AEC {
    
    private static final String TAG ="WebRtc_AEC";
    //NATIVE CODE
    static {
        
        try {
            System.loadLibrary("WebRtc");
        }
        catch (UnsatisfiedLinkError ule){
        }
    }
    
    private int mHandle;
    private native int nativeCreate();
    private native int nativeInit(int handle , long sampleRate);
    private native int nativeFree(int handle);
    private native int nativeCapture(int handle , short[] input , int frame_size);
    private native int nativePlay(int handle , short[] noisy , short[] out , int frame_size , int delay_ms);
    
    
    // JAVA CODE
    private static final int BUFFER_SIZE = 80;
    
    private boolean isInit = false;
    private int mSampleRate;
    private short[] mCaptureBuffer;
    private Lock mLock = new ReentrantLock();
    public AEC()
    {
        isInit = false;
    }
    
    public void release()
    {
        nativeFree(mHandle);
    }
    public boolean isInit()
    {
        return isInit;
    }
    
    private long mRenderTime;
    private long mAnalyzeTime;
    private long mProcessTime;
    private long mCaptureTime;

    /**
     * 
     * @param t_render is the time the first sample of the same frame is rendered by the audio hardware ,if not set , set the value to -1.
     * @param t_analyze is the time a frame is passed to AnalyzeReverseStream() , if not set , set the value to -1.
     * @param t_process s the time the same frame is passed to ProcessStream() ,if not set , set the value to -1.
     * @param t_capture is the time the first sample of a frame is captured by the audio hardware ,if not set , set the value to -1.
     */
    public void setDelayTime(long t_render, long t_analyze , long t_process , long t_capture)
    {
    	mLock.lock();
    	if(t_render !=-1)mRenderTime = t_render;
    	if(t_analyze!=-1)mAnalyzeTime = t_analyze;
    	if(t_process!=-1)mProcessTime = t_process;
    	if(t_capture!=-1)mCaptureTime = t_capture;
    	mLock.unlock();
    	
    }
    
    public boolean Create(int sampleRate)
    {
        mSampleRate = sampleRate;
        mHandle = nativeCreate();
        if(mHandle==-1)return false;
        
        int ret = -1;
        ret = nativeInit(mHandle,sampleRate);
        
        if(ret==-1)return false;
        isInit=true;
        return true;
    }
    
    public boolean Capture(short[] input)
    {
        
        mLock.lock();
        mCaptureBuffer = input;
        mLock.unlock();
        return true;
    }
    
    public boolean Play(short[] noisy , short[] out , int delay_time)
    {
        mLock.lock();
        int ret = -1;
        int size = noisy.length;
        int total_time = (int) ((mRenderTime - mAnalyzeTime) + (mProcessTime - mCaptureTime));
Log.d("test","total_time = " + total_time);        
        
        for(int i=0;i<size/BUFFER_SIZE ; i++)
        {
            
            short[] buf_cap = new short[BUFFER_SIZE];
            
            System.arraycopy(mCaptureBuffer, BUFFER_SIZE*i, buf_cap, 0, BUFFER_SIZE);
            nativeCapture(mHandle,buf_cap,BUFFER_SIZE);
            
            short[] buf = new short[BUFFER_SIZE];
            short[] buf_out =new short[BUFFER_SIZE];
            System.arraycopy(noisy, BUFFER_SIZE*i, buf, 0, BUFFER_SIZE);
            
            ret = nativePlay(mHandle,buf,buf_out,BUFFER_SIZE,delay_time);
            System.arraycopy(buf_out, 0, out, BUFFER_SIZE*i, BUFFER_SIZE);
        }
        Log.d("test2","nativePlay = " + ret);
        mLock.unlock();
        return ret ==0;		
    }
}