package com.howard.webrtcaecmsample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class MainActivity extends Activity implements OnCheckedChangeListener, OnClickListener {

	private RecordThread mRecordThread1;
	private RecordThread mRecordThread2;
	
	private PlayThread mPlayThread1;
	private PlayThread mPlayThread2;
	
	private AecPlayThread mAecPlayThread;
	
	private Switch mRecord1;
	private Switch mRecord2;
	private Button mPlay1;
	private Button mPlay2;
	private Button mAecPlay;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		mRecord1 =(Switch)this.findViewById(R.id.sw_record1);
		mRecord2 =(Switch)this.findViewById(R.id.sw_record2);
		mPlay1 =(Button)this.findViewById(R.id.bt_play1);
		mPlay2 =(Button)this.findViewById(R.id.bt_play2);
		mAecPlay = (Button)this.findViewById(R.id.bt_aec_play);
		
		mRecord1.setOnCheckedChangeListener(this);
		mRecord2.setOnCheckedChangeListener(this);
		
		mPlay1.setOnClickListener(this);
		mPlay2.setOnClickListener(this);
		mAecPlay.setOnClickListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int id = buttonView.getId();
		
		switch(id)
		{
			case R.id.sw_record1:
			{
				if(isChecked)
				{
					mRecordThread1 = new RecordThread();
					mRecordThread1.StartRecord();
				}else{
					mRecordThread1.StopRecord();
				}
			}
			break;
			case R.id.sw_record2:
			{
				if(isChecked)
				{
					mPlayThread1 = new PlayThread();
					mPlayThread1.StartPlay(mRecordThread1.getBufferList());
					mRecordThread2 = new RecordThread();
					mRecordThread2.StartRecord();
				}else{
					mRecordThread2.StopRecord();
				}
			}
			break;			
		}
		
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id)
		{
			case R.id.bt_play1:
			{
				mPlayThread1 = new PlayThread();
				mPlayThread1.StartPlay(mRecordThread1.getBufferList());
			}
			break;
			case R.id.bt_play2:
			{
				mPlayThread2 = new PlayThread();
				mPlayThread2.StartPlay(mRecordThread2.getBufferList());				
			}
			break;
			case R.id.bt_aec_play:
			{
				mAecPlayThread = new AecPlayThread();
				mAecPlayThread.StartPlay(mRecordThread1.getBufferList(), mRecordThread2.getBufferList());
			}
		}
		
	}
	
	
	

}
