/**
 * this class only use 8000Hz sample , 1 channel
 * 
 * Designer : Howard.chu
 * Time : 2015.06.17
 */

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
    private static final int MIN_BUFFER_TIME_MS = 10;
    
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
    
    private long mCaptureTime;

    
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
    
    public boolean Capture(short[] input , long time_ms)
    {
        
        mLock.lock();
        mCaptureBuffer = input;
        mCaptureTime= time_ms;
        mLock.unlock();
        return true;
    }
    
    boolean isCapture = false;
    public boolean Play(short[] noisy , short[] out , long delay_time)
    {
        mLock.lock();
        int ret = -1;
        int size = noisy.length;
        int total_time = (int) (delay_time - mCaptureTime);
		int residue = total_time%MIN_BUFFER_TIME_MS;
		int timeBlockCount = total_time/MIN_BUFFER_TIME_MS;
Log.d("test","total_time = " + total_time + "," + timeBlockCount + "," + residue);        
		
        for(int i=0;i<size/BUFFER_SIZE ; i++)
        {
        	short[] buf_cap = getCaptureBlockBuffer(mCaptureBuffer ,timeBlockCount + i ,BUFFER_SIZE);
            //short[] buf_cap = new short[BUFFER_SIZE];
            //System.arraycopy(mCaptureBuffer, BUFFER_SIZE*i, buf_cap, 0, BUFFER_SIZE);
            nativeCapture(mHandle,buf_cap,BUFFER_SIZE);

            short[] buf = new short[BUFFER_SIZE];
            short[] buf_out =new short[BUFFER_SIZE];
            System.arraycopy(noisy, BUFFER_SIZE*i, buf, 0, BUFFER_SIZE);
            
            ret = nativePlay(mHandle,buf,buf_out,BUFFER_SIZE,residue);
            System.arraycopy(buf_out, 0, out, BUFFER_SIZE*i, BUFFER_SIZE);
        }
        Log.d("test2","nativePlay = " + ret);
        mLock.unlock();
        return ret ==0;		
    }

    /**
     * 
     * @param cap_buf capture buffer
     * @param block the capture buffer block number
     * @param length the block length
     * @return a capture block buffer
     */
    private short[] getCaptureBlockBuffer(short[] cap_buf , int block , int length)
    {
    	short[] block_buf = new short[length];
    	int cap_length = cap_buf.length;
    	
    	if((length*block + length) > cap_length)
    	{
    		if(length*block < cap_length)
    		{
    			int size =(length*block + length) - cap_length;
    			System.arraycopy(cap_buf, length*block, block_buf, 0, size);
    		}
    	}else{
    		System.arraycopy(cap_buf, length*block, block_buf, 0, length);
    	}
    	return block_buf;
    }
}