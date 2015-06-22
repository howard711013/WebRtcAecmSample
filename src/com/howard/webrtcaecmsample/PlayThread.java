package com.howard.webrtcaecmsample;

import java.util.ArrayList;

import com.tutk.webrtc.AEC;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class PlayThread extends Thread{

	private static final int SAMPLE_RATE = 8000;
	private ArrayList<short[]> mBufferList;
	private AudioTrack mAudioTrack;
	
	private AEC mAec;
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
	
	public void setAec(AEC aec)
	{
		mAec = aec;
	}
	
	public void run()
	{
		mAudioTrack.play();


		for(short[] buf : mBufferList)
		{
			long s_time = System.nanoTime()/1000/1000;
			if(mAec!=null)mAec.Capture(buf, 0);
			mAudioTrack.write(buf, 0, buf.length);
			long e_time = System.nanoTime()/1000/1000;
			Log.d("test2","write time : " + (int)(e_time-s_time));
			
		}
		mAudioTrack.stop();
	}

}
