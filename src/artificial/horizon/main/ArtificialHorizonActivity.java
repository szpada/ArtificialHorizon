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
import android.hardware.SensorManager;
import android.hardware.SensorListener;

public class ArtificialHorizonActivity extends Activity implements SensorListener {
	//logcat tag
    final String tag = "ArtificialHorizonActivity";
    
    SensorManager sm = null;

    //horizon view
    private ArtificialHorizon horizon;
    
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
		
		horizon = new ArtificialHorizon(this, w_factor, h_factor);
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
		menu.add(1, 1, 0, "DEBUG").setIcon(R.drawable.artificial_horizon_plane_compass);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(horizon != null){
			switch (item.getItemId()) {
			case 1:
				horizon.setDebug(!(horizon.isDebug()));
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
    
    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and accelerometer sensors
        sm.registerListener(this, 
                SensorManager.SENSOR_ORIENTATION |SensorManager.SENSOR_ACCELEROMETER,
                //sensor delay
                //SensorManager.SENSOR_DELAY_NORMAL
        		//other options
                	//SensorManager.SENSOR_DELAY_FASTEST
                	//SensorManager.SENSOR_DELAY_GAME
                	SensorManager.SENSOR_DELAY_UI
        		);	
    }
    
    @Override
    protected void onStop() {
        // unregister listener
        sm.unregisterListener(this);
        super.onStop();
    }    
}
