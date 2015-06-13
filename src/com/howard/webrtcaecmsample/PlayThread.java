package com.howard.webrtcaecmsample;

import java.util.ArrayList;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

public class PlayThread extends Thread{

	private static final int SAMPLE_RATE = 8000;
	private ArrayList<short[]> mBufferList;
	
	private AudioTrack mAudioTrack;
	public void StartPlay(ArrayList<short[]> buffers)
	{
		mBufferList = buffers;
		
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
		mAudioTrack.play();

		for(short[] buf : mBufferList)
		{
			mAudioTrack.write(buf, 0, buf.length);
		}
		mAudioTrack.stop();
	}

}
