package artificial.horizon.main;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.hardware.SensorManager;
import android.hardware.SensorListener;

public class ArtificialHorizonActivity extends Activity implements SensorListener {
    final String tag = "IBMEyes";
    SensorManager sm = null;
    TextView xViewA = null;
    TextView yViewA = null;
    TextView zViewA = null;
    TextView xViewO = null;
    TextView yViewO = null;
    TextView zViewO = null;

    //horizon view
    private ArtificialHorizon horizon;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        		requestWindowFeature(Window.FEATURE_NO_TITLE);

		/**
		 * get phone scale factor
		 * 
		 *  App was created on dimension 480 x 800 so other phone resolutions must be scaled
		 *  
		 */
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
        
       
//        setContentView(R.layout.main);
//        xViewA = (TextView) findViewById(R.id.xbox);
//        yViewA = (TextView) findViewById(R.id.ybox);
//        zViewA = (TextView) findViewById(R.id.zbox);
//        xViewO = (TextView) findViewById(R.id.xboxo);
//        yViewO = (TextView) findViewById(R.id.yboxo);
//        zViewO = (TextView) findViewById(R.id.zboxo);
    }
    public void onSensorChanged(int sensor, float[] values) {
        synchronized (this) {
            //Log.d(tag, "onSensorChanged: " + sensor + ", x: " + values[0] + ", y: " + values[1] + ", z: " + values[2]);
            
            horizon.updateSensor(sensor, values);
//            if (sensor == SensorManager.SENSOR_ORIENTATION) {
//                xViewO.setText("Orientation X: " + values[0]);
//                yViewO.setText("Orientation Y: " + values[1]);
//                zViewO.setText("Orientation Z: " + values[2]);
//            }
//            if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
//                xViewA.setText("Accel X: " + values[0]);
//                yViewA.setText("Accel Y: " + values[1]);
//                zViewA.setText("Accel Z: " + values[2]);
//            }            
        }
    }
    
    public void onAccuracyChanged(int sensor, int accuracy) {
    	Log.d(tag,"onAccuracyChanged: " + sensor + ", accuracy: " + accuracy);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
      // register this class as a listener for the orientation and accelerometer sensors
        sm.registerListener(this, 
                SensorManager.SENSOR_ORIENTATION |SensorManager.SENSOR_ACCELEROMETER,
                SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
    protected void onStop() {
        // unregister listener
        sm.unregisterListener(this);
        super.onStop();
    }    
}
