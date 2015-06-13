package com.howard.webrtcaecmsample;

import java.util.ArrayList;

import com.tutk.webrtc.AECM;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.util.Log;

public class AecPlayThread extends Thread{

	private static final int SAMPLE_RATE = 8000;
	private ArrayList<short[]> mCaptureBufferList;
	private ArrayList<short[]> mPlayBufferList;
	
	private AudioTrack mAudioTrack;
	private AECM mAecm;
	private int mDelayTime;
	public void StartPlay(ArrayList<short[]> capture ,ArrayList<short[]> play , int delayTime)
	{
		mCaptureBufferList = capture;
		mPlayBufferList = play;
		mDelayTime=delayTime;
		
		int minBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

		mAudioTrack = new AudioTrack(
				AudioManager.STREAM_MUSIC,
				SAMPLE_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				minBufSize,
				AudioTrack.MODE_STREAM);

		start();
	}
	
	public void run()
	{
		mAecm = new AECM();
		mAecm.Create(SAMPLE_RATE);
		mAudioTrack.play();

		for(int i=0;i<mPlayBufferList.size();i++)
		{
			short[] capture = null;
			short[] play;
			short[] aec_out = new short[80];
			if(i+1 < mCaptureBufferList.size())capture = mCaptureBufferList.get(i+1);
			play = mPlayBufferList.get(i);
			
			if(capture!=null)
			{
long startTime = System.nanoTime()/1000/1000;				
				mAecm.Capture(capture);
				mAecm.Play(play, aec_out, mDelayTime);
long endTime = System.nanoTime()/1000/1000;				
Log.d("test","delay time = " + (int)(endTime - startTime));
				mAudioTrack.write(aec_out, 0, aec_out.length);
				
			}else{
				mAudioTrack.write(play, 0, play.length);
			}
		}

		mAudioTrack.stop();
		mAecm.release();
	}

}
