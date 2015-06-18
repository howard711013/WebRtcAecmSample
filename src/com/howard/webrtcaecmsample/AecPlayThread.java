package com.howard.webrtcaecmsample;

import java.util.ArrayList;

import com.tutk.webrtc.AEC;
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
	private AEC mAec;
	private int mDelayTimeAec;
	public void StartPlay(ArrayList<short[]> capture ,ArrayList<short[]> play , int t_aec_ms)
	{
		mCaptureBufferList = capture;
		mPlayBufferList = play;
		mDelayTimeAec=t_aec_ms;
		
		int minBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

		mAudioTrack = new AudioTrack(
				AudioManager.STREAM_MUSIC,
				SAMPLE_RATE,
				AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				minBufSize,
				AudioTrack.MODE_STREAM);

		start();
	}
	
	public void run()
	{
		mAec = new AEC();
		mAec.Create(SAMPLE_RATE);
		mAudioTrack.play();

		for(int i=0;i<mPlayBufferList.size();i++)
		{
			short[] capture = null;
			short[] play;
			short[] aec_out = new short[320];
			if(i < mCaptureBufferList.size())capture = mCaptureBufferList.get(i);
			play = mPlayBufferList.get(i);
			
			if(capture!=null)
			{
				Log.d("test","capture : " + mAec.Capture(capture , 0));
				Log.d("test","play :" + mAec.Play(play, aec_out, mDelayTimeAec));
				mAudioTrack.write(aec_out, 0, aec_out.length);
				
			}else{
				mAudioTrack.write(play, 0, play.length);
			}
		}

		mAudioTrack.stop();
		mAec.release();
	}

}
