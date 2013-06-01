package artificial.horizon.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainMenuActivity extends Activity{
	//antialiasing
	private boolean quality;
	//bitmap filtering
	private boolean filter;
	
	
	private short sensorSpeed;
	
	private short smooth;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		//full screen mode
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	            WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    		
		setContentView(R.layout.main);
    	
		ImageButton start = (ImageButton)findViewById(R.id.start);
		start.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				Intent i = new Intent(MainMenuActivity.this, ArtificialHorizonActivity.class);
				Log.d("MainMenuActivity", "SMOOTH : " + smooth);
    			i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    			i.putExtra("QUALITY", quality);
    			i.putExtra("FILTER", filter);
    			i.putExtra("SPEED", sensorSpeed);
    			i.putExtra("SMOOTHNESS", (short)(smooth + 1));
    			startActivity(i);
				return false;
			}
		});
		
		CheckBox antialiasing = (CheckBox)findViewById(R.id.antialiasing);
		quality = antialiasing.isChecked();
		antialiasing.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) quality = true;
				else quality = false;
			}
		});
		
		CheckBox filteringBitmap = (CheckBox)findViewById(R.id.filtering);
		filter = filteringBitmap.isChecked();
		filteringBitmap.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) filter = true;
				else filter = false;
			}
		});
		
		SeekBar sensorType = (SeekBar)findViewById(R.id.sensor_type);
		sensorSpeed = (short)sensorType.getProgress();
		sensorType.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				sensorSpeed = (short) progress;
			}
		});
		
		SeekBar smooothness = (SeekBar)findViewById(R.id.smoothness);
		smooth = (short)smooothness.getProgress();
		smooothness.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				smooth = (short) progress;
			}
		});
	}
	
	@Override
	public void onStop(){
		super.onStop();
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
}
