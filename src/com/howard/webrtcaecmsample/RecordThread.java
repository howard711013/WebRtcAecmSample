package com.howard.webrtcaecmsample;

import java.util.ArrayList;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class RecordThread extends Thread{

	private static final int SAMPLE_RATE = 8000;
	private ArrayList<short[]> mBufferList;
	private boolean isRecording=false;
	
	private AudioRecord mAudioRecord;
	public void StartRecord()
	{
		int minBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

		mAudioRecord = new AudioRecord(
				MediaRecorder.AudioSource.MIC, 
				SAMPLE_RATE,
				AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufSize);
		
		
		mBufferList  = new ArrayList<short[]>();
		isRecording=true;
		start();
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
			short[] buf = new short[320];
			mAudioRecord.read(buf,0, buf.length);
			mBufferList.add(buf);
		}
		mAudioRecord.stop();
	}
}
