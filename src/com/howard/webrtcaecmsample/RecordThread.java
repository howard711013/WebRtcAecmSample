package com.howard.webrtcaecmsample;

import java.util.ArrayList;

import com.tutk.webrtc.AEC;

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
	
	private AEC mAec;
	
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
	
	public void setAec(AEC aec)
	{
		mAec = aec;
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
			
			short[] out_buf = new short[320];
			if(mAec!=null)
			{
				mAec.Play(buf, out_buf, 0);
			}else{
				System.arraycopy(buf, 0, out_buf, 0, out_buf.length);
			}
			mBufferList.add(out_buf);
			
		}
		mAudioRecord.stop();
	}
}
