package artificial.horizon.main;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.hardware.SensorListener;

public class ArtificialHorizonActivity extends Activity implements SensorListener {
	//logcat tag
    final String tag = "ArtificialHorizonActivity";
    
    SensorManager sm = null;

    //horizon view
    private ArtificialHorizon horizon;
    
    private short speed = 0;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //full screen mode
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// get phone scale factor
		//App was created on dimension 480 x 800 so other phone resolutions must be scaled
		DisplayMetrics displaymetrics = new DisplayMetrics(); 
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics); 
		int height = displaymetrics.heightPixels; 
		int width = displaymetrics.widthPixels;
		double h_factor = height/800.0;
		double w_factor = width/480.0;
		    
		// get reference to SensorManager
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		
        boolean quality = false;
        
        Bundle extras = getIntent().getExtras();
		if (extras != null) {
			quality = extras.getBoolean("QUALITY");
			speed = extras.getShort("SPEED");
		}
        
		horizon = new ArtificialHorizon(this, w_factor, h_factor, quality);
		setContentView(horizon);
    }
    
    public void onSensorChanged(int sensor, float[] values) {
        synchronized (this) {
            horizon.updateSensor(sensor, values);           
        }
    }
    
    public void onAccuracyChanged(int sensor, int accuracy) {
    	Log.d(tag,"onAccuracyChanged: " + sensor + ", accuracy: " + accuracy);
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1, 1, 0, "Debug").setIcon(R.drawable.ic_launcher_android);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(horizon != null){
			switch (item.getItemId()) {
			case 1:
				horizon.setDebug(!(horizon.isDebug()));
				if(horizon.isDebug()){
					item.setIcon(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.compass_base)));
					item.setTitle("Compass");
				}
				else{
					item.setIcon(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_android)));
					item.setTitle("Debug");
				}
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
    
    @Override
    protected void onResume() {
    	int sensorSpeed = SensorManager.SENSOR_DELAY_NORMAL;
        super.onResume();
        switch(speed){
        case 1:
        	sensorSpeed = SensorManager.SENSOR_DELAY_UI;
        	break;
        case 2:
        	sensorSpeed = SensorManager.SENSOR_DELAY_GAME;
        	break;
        case 3:
        	sensorSpeed = SensorManager.SENSOR_DELAY_FASTEST;
        	break;
        }
        
        // register this class as a listener for the orientation and accelerometer sensors
        sm.registerListener(this, SensorManager.SENSOR_ORIENTATION |SensorManager.SENSOR_ACCELEROMETER, sensorSpeed
                //sensor delay
                //SensorManager.SENSOR_DELAY_NORMAL
        		//other options
                	//SensorManager.SENSOR_DELAY_FASTEST
                	//SensorManager.SENSOR_DELAY_GAME
                	//SensorManager.SENSOR_DELAY_UI
        		);	
    }
    
    @Override
    protected void onStop() {
        // unregister listener
        sm.unregisterListener(this);
        super.onStop();
        finish();
    }    
}
