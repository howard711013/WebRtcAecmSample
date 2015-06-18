package com.howard.webrtcaecmsample;

import java.util.ArrayList;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class RecordThread extends Thread{

	private static final int SAMPLE_RATE = 8000;
	private ArrayList<short[]> mBufferList;
	private boolean isRecording=false;
	
	private AudioRecord mAudioRecord;
	private int mDelayTimeMs;
	
	private Object mKeyLock;
	public void StartRecord(int time_ms)
	{
		int minBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

		mAudioRecord = new AudioRecord(
				MediaRecorder.AudioSource.MIC, 
				SAMPLE_RATE,
				AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufSize);
		
		mDelayTimeMs = time_ms;
		mBufferList  = new ArrayList<short[]>();
		isRecording=true;
		start();
	}
	
	public void SetLockKey(Object key)
	{
		mKeyLock = key;
	}
	
	public void StopRecord()
	{
		isRecording=false;
	}
	
	public ArrayList<short[]> getBufferList()
	{
		return mBufferList;
	}
	
	public void run()
	{
		mAudioRecord.startRecording();
		

		while(isRecording)
		{
			long s_time = System.nanoTime()/1000/1000;

			try {
				Thread.sleep(mDelayTimeMs);
			} catch (InterruptedException e) {
				// TODO 自動產生的 catch 區塊
				e.printStackTrace();
			}
			short[] buf = new short[320];
			mAudioRecord.read(buf,0, buf.length);
			long e_time = System.nanoTime()/1000/1000;
			Log.d("test2","time = " + (e_time - s_time));
			mBufferList.add(buf);
			
			if(mKeyLock!=null)
			{
				synchronized(mKeyLock)
				{
					mKeyLock.notifyAll();
				}
			}
		}
		mAudioRecord.stop();
		mKeyLock=null;
	}
}
