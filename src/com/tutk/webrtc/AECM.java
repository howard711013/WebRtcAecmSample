package com.tutk.webrtc;

import android.util.Log;

public class AECM {
	
	private static final String TAG ="WebRtc_AECM";
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
    private native int nativePlay(int handle , short[] noisy , short[] noisyClean , short[] out , int frame_size , int delay_ms);
    
    
// JAVA CODE
	public static final long SAMPLE_RATE_8K = 8000;
	public static final long SAMPLE_RATE_16K = 16000;
	public static final long SAMPLE_RATE_32K = 32000;
	
	private static final int BUFFER_SIZE_8K = 80;
	private static final int BUFFER_SIZE_16K = 160;
	private static final int BUFFER_SIZE_32K = 160;
	
    private boolean isInit = false;
    private int mSampleRate;
    private int mCaptureDelayTime;
    public AECM()
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
		int ret = nativeCapture(mHandle,input,input.length);
		return ret==0;
	}
	
	public void SetCaptureDelayTime(int delay_ms)
	{
		mCaptureDelayTime = delay_ms;
	}
	
	public boolean Play(short[] noisy , short[] out , int delay_ms)
	{
		return Play(noisy,null,out,delay_ms);
	}
	public boolean Play(short[] noisy , short[] noisy_clean , short[] out , int delay_ms)
	{
		int ret = nativePlay(mHandle,noisy,noisy_clean,out,noisy.length,delay_ms);
		return ret ==0;
	}
    
}
