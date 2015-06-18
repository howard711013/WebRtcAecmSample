package com.howard.webrtcaecmsample;

import java.util.ArrayList;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class PlayThread extends Thread{

	private static final int SAMPLE_RATE = 8000;
	private ArrayList<short[]> mBufferList;
	private AudioTrack mAudioTrack;
	private Object mKeyLock;
	public void StartPlay(ArrayList<short[]> buffers)
	{
		mBufferList = buffers;
		
		int minBufSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

		mAudioTrack = new AudioTrack(
				AudioManager.STREAM_MUSIC,
				SAMPLE_RATE,
				AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				minBufSize,
				AudioTrack.MODE_STREAM);

		start();
	}
	
	public void SetLockKey(Object key)
	{
		mKeyLock = key;
	}
	
	public void run()
	{
		mAudioTrack.play();

		for(short[] buf : mBufferList)
		{
			mAudioTrack.write(buf, 0, buf.length);
			
			if(mKeyLock!=null)
			{
				synchronized(mKeyLock)
				{
					try {
						mKeyLock.wait(1000);
					} catch (InterruptedException e) {
						// TODO 自動產生的 catch 區塊
						e.printStackTrace();
					}
				}
			}
		}
		mAudioTrack.stop();
		mKeyLock=null;
	}

}
