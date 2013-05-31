package artificial.horizon.main;

import java.util.Currency;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ArtificialHorizon extends SurfaceView {//implements SensorListener{

	//width, height scale factor (for different phones)
	private float w_factor = 1.0f;
	private float h_factor = 1.0f;
	
	//Loop thread
	private ArtificialHorizonLoopThread thread;
	
	//paint for drawing everything
	private Paint paint = null;
	
	//sensor orientations
	private float sensorOrientation_X = 0.0f;
	private float sensorOrientation_Y = 0.0f;
	private float sensorOrientation_Z = 0.0f;
	
	//sensor orientations
	private float LAST_sensorOrientation_X = 0.0f;
	private float LAST_sensorOrientation_Y = 0.0f;
	private float LAST_sensorOrientation_Z = 0.0f;
	
	//accelerometers
	private float accelerometer_X = 0.0f;
	private float accelerometer_Y = 0.0f;
	private float accelerometer_Z = 0.0f;
	
	//timmer
	private long lastUpdate = 0;
	
	//counted frequency
	private int frequency = 0;
	
	//update counter
	private int updateCounter = 0;
	
	//debug mode
	private boolean debug = false;
	
	//BITMAPS
	private Bitmap BMP_back;
	private Bitmap BMP_plane;
	private Bitmap BMP_background;
	private Bitmap BMP_comapass;
	private Bitmap BMP_compassPlane;
	
	public ArtificialHorizon(Context context, double w_factor, double h_factor) {
        super(context);
        
        this.lastUpdate = System.currentTimeMillis();
        this.h_factor = (float)h_factor;
	   	this.w_factor = (float)w_factor;
        
        setFocusable(true);
        setFocusableInTouchMode(true);
        setLongClickable(true);

    	thread = new ArtificialHorizonLoopThread(this);
        getHolder().addCallback(new SurfaceHolder.Callback() {
               public void surfaceDestroyed(SurfaceHolder holder) {
                      boolean retry = true;
                      thread.setRunning(false);
                      while (retry) {
                             try {
                            	 thread.join();
                                   retry = false;
                             } catch (InterruptedException e) {}
                      }
               }
               public void surfaceCreated(SurfaceHolder holder) {
            	   prepareVariables();
            	   thread.setRunning(true);
            	   thread.start();
	            }
               
	            public void surfaceChanged(SurfaceHolder holder, int format,int width, int height) {
	            	
	            }
        }); 
    }
    
	public void prepareVariables(){
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		
		BMP_back = BitmapFactory.decodeResource(getResources(), R.drawable.artificial_horizon_back);
		BMP_background = BitmapFactory.decodeResource(getResources(), R.drawable.artificial_horizon_background);
		BMP_plane = BitmapFactory.decodeResource(getResources(), R.drawable.artificial_horizon_plane);
		BMP_comapass = BitmapFactory.decodeResource(getResources(), R.drawable.artificial_horizon_compass);
		BMP_compassPlane = BitmapFactory.decodeResource(getResources(), R.drawable.artificial_horizon_plane_compass);
	}
	
    @Override
    public void onDraw(Canvas canvas) {
    	//scale view
    	canvas.scale(this.w_factor, this.h_factor);
    	
    	//drawing background
    	paint.setColor(Color.WHITE);
    	canvas.drawRect(0, 0, 480, 800, this.paint);
    	
    	//float current_x = (sensorOrientation_X + LAST_sensorOrientation_X)/2;
    	//float current_y = (sensorOrientation_Y + LAST_sensorOrientation_Y)/2;
    	//float current_z = (sensorOrientation_Z + LAST_sensorOrientation_Z)/2;
    	
    	float current_x = sensorOrientation_X;
    	float current_y = sensorOrientation_Y;
    	float current_z = sensorOrientation_Z;
    	
    	drawSprite(canvas, 240, (int)(240.0f - current_y), 1, 1, BMP_background.getWidth(), BMP_background.getHeight(), 0, BMP_background, - current_z, 1.0f);
    	
    	drawSprite(canvas, 240, 240, 1, 1, BMP_back.getWidth(), BMP_back.getHeight(), 0, BMP_back, 0, 1.0f);
    	
    	drawSprite(canvas, 240, 240, 1, 1, BMP_plane.getWidth(), BMP_plane.getHeight(), 0, BMP_plane, 0, 1.0f);
    	
    	paint.setColor(Color.LTGRAY);
    	canvas.drawRect(0, 480, 480, 800, this.paint);
    	
    	if(!debug){
	    	drawSprite(canvas, 240, 630, 1, 1, BMP_comapass.getWidth(), BMP_comapass.getHeight(), 0, BMP_comapass, current_x, 1.0f);
	    	
    		drawSprite(canvas, 240, 630, 1, 1, BMP_compassPlane.getWidth(), BMP_compassPlane.getHeight(), 0, BMP_compassPlane, 0, 1.0f);
    	}
    	else{
    	
	    	//rect positions
	    	int orientationPosition_X = 100;
	    	int orientationPosition_Y = orientationPosition_X + 180;
	    	int orientationPosition_Z = orientationPosition_Y + 180;
	    	
	    	//X sensor rect
	    	//paint.setColor(Color.BLUE);
	    	//canvas.drawRect(orientationPosition_X, orientationPosition_X, orientationPosition_X + (int)sensorOrientation_X, orientationPosition_X + 10, paint);
	    	
	    	//Y sensor rect
	    	//paint.setColor(Color.GREEN);
	    	//canvas.drawRect(orientationPosition_X, orientationPosition_Y, orientationPosition_X + (int)sensorOrientation_Y, orientationPosition_Y + 10, paint);
	    	
	    	//Z sensor rect
	    	//paint.setColor(Color.RED);
	    	//canvas.drawRect(orientationPosition_X, orientationPosition_Z, orientationPosition_X + (int)sensorOrientation_Z, orientationPosition_Z + 10, paint);
	    	
	    	//Draw all data as text
	    	paint.setColor(Color.BLACK);
	    	paint.setTextSize(26.0f);
	    	
	    	canvas.drawText("sensor X : " + sensorOrientation_X, orientationPosition_X, orientationPosition_Z + 40, paint);
	    	canvas.drawText("sensor Y : " + sensorOrientation_Y, orientationPosition_X, orientationPosition_Z + 60, paint);
	    	canvas.drawText("sensor Z : " + sensorOrientation_Z, orientationPosition_X, orientationPosition_Z + 80, paint);
	    	
	    	canvas.drawText("accelerometer X : " + accelerometer_X, orientationPosition_X, orientationPosition_Z + 100, paint);
	    	canvas.drawText("accelerometer Y : " + accelerometer_Y, orientationPosition_X, orientationPosition_Z + 120, paint);
	    	canvas.drawText("accelerometer Z : " + accelerometer_Z, orientationPosition_X, orientationPosition_Z + 140, paint);
	    	
	    	canvas.drawText("frequency : " + frequency + "Hz", orientationPosition_X, orientationPosition_Z + 160, paint);
    	}
    	
    	LAST_sensorOrientation_X = sensorOrientation_X;
    	LAST_sensorOrientation_Y = sensorOrientation_Y;
    	LAST_sensorOrientation_Z = sensorOrientation_Z;
    }
   
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	int touchInd = event.getActionIndex();
    	float x = event.getX(touchInd)/ this.w_factor;
        float y = event.getY(touchInd) / this.h_factor;
    	
        return super.onTouchEvent(event);		
    }
  	
    public void drawSprite(Canvas canvas, int x, int y, int columns, int rows, int width, int height, int currentFrame, Bitmap bmp, float angle, float scale){
    	//save current canvas state
		canvas.save();
		//rotate canvas
		canvas.rotate(angle, x, y);
		//scale canvas
		canvas.scale(scale, scale, x, y);
		
    	int srcX = 0;
    	int srcY = 0;
    	int row;
    	srcX = (currentFrame % (columns)) * width;
    	if(rows % 2 == 0){
    		row = currentFrame / (rows);
    	}
    	else{
    		row = currentFrame / (rows + 1);
    	}
        srcY = row * height;
        
    	Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);
		Rect dst = new Rect(x - width/2, y - height/2, x + width/2, y + height/2);
		canvas.drawBitmap(bmp, src, dst, paint);
		
		//restore default canvas settings
		canvas.restore();
    }
    
    public void updateSensor(int sensor, float[] values){
    	//every second reset timmer
    	if(System.currentTimeMillis() - lastUpdate >= 1000){
    		//update lastUpdate time
    		lastUpdate = System.currentTimeMillis();
    		//counted frequency
    		frequency = updateCounter;
    		//set updateCounter to 0
    		updateCounter = 0;
    	}
    	else{
    		updateCounter++;
    	}
    	//read values from sensor
    	if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
    		//accelerometer values
    		accelerometer_X = values[0];
    		accelerometer_Y = values[1];
    		accelerometer_Z = values[2];
    	}
    	if (sensor == SensorManager.SENSOR_ORIENTATION) {
    		//sensor orientation values
    		Log.d("ArtificialHorizon", "onSensorChanged: " + sensor + ", x: " + values[0] + ", y: " + values[1] + ", z: " + values[2]);
    		sensorOrientation_X = values[0];
    		sensorOrientation_Y = values[1];
    		sensorOrientation_Z = values[2];
    	}
    }

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
    