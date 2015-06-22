package com.howard.webrtcaecmsample;

import com.tutk.webrtc.AEC;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;

public class MainActivity extends Activity implements OnCheckedChangeListener, OnClickListener {

	public static final int SAMPLE_RATE = 8000;
	
	private RecordThread mRecordThread1;
	private RecordThread mRecordThread2;
	
	private PlayThread mPlayThread1;
	private PlayThread mPlayThread2;
	
	private EditText mDelayTimeAec;
	private EditText mDelayTimeRec2;
	private Switch mRecord1;
	private Switch mRecord2;
	private Button mPlay1;
	private Button mPlay2;
	private AEC mAec;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		mRecord1 =(Switch)this.findViewById(R.id.sw_record1);
		mRecord2 =(Switch)this.findViewById(R.id.sw_record2);
		mPlay1 =(Button)this.findViewById(R.id.bt_play1);
		mPlay2 =(Button)this.findViewById(R.id.bt_play2);
		mDelayTimeRec2=(EditText)this.findViewById(R.id.txt_delaytime_rec2);
		mDelayTimeAec = (EditText)this.findViewById(R.id.txt_delaytime_aec);
		
		mRecord1.setOnCheckedChangeListener(this);
		mRecord2.setOnCheckedChangeListener(this);
		
		mPlay1.setOnClickListener(this);
		mPlay2.setOnClickListener(this);
		
		mAec = new AEC();
		mAec.Create(SAMPLE_RATE);
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
					mRecordThread1.StartRecord(0);
				}else{
					mRecordThread1.StopRecord();
				}
			}
			break;
			case R.id.sw_record2: // aec record
			{
				if(isChecked)
				{
					String delayTime = mDelayTimeRec2.getText().toString();
					int delay = 0;
					if(delayTime.isEmpty()==false)delay = Integer.parseInt(delayTime);
					mRecordThread2 = new RecordThread();
					mRecordThread2.setAec(mAec);
					
					mRecordThread2.StartRecord(delay);
					
					
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
				mPlayThread1.setAec(mAec);
				mPlayThread1.StartPlay(mRecordThread1.getBufferList());
			}
			break;
			case R.id.bt_play2: // aec play
			{
				mPlayThread2 = new PlayThread();
				mPlayThread2.StartPlay(mRecordThread2.getBufferList());
			}
			break;
		}
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		mAec.release();
	}
	
	
	

}
