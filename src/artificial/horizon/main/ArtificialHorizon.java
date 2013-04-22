package artificial.horizon.main;

import java.util.Currency;

import android.content.Context;
import android.graphics.Bitmap;
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
	
	public ArtificialHorizon(Context context, double w_factor, double h_factor) {
        super(context);
        
        this.lastUpdate = System.currentTimeMillis();
        this.h_factor = (float)h_factor;
	   	this.w_factor = (float)w_factor;
        
        setFocusable(true);
        setFocusableInTouchMode(true);
        setLongClickable(true);
        //this.setOnTouchListener(this);
    	thread = new ArtificialHorizonLoopThread(this);
        getHolder().addCallback(new SurfaceHolder.Callback() {
               //@Override
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
               //@Override
               public void surfaceCreated(SurfaceHolder holder) {
            	   prepareVariables();
            	   thread.setRunning(true);
            	   thread.start();
	            }
	            //@Override
	            public void surfaceChanged(SurfaceHolder holder, int format,int width, int height) {
	            	
	            }
        }); 
    }
    
	public void prepareVariables(){
		paint = new Paint();
	}
	
    @Override
    public void onDraw(Canvas canvas) {
    	//scale view
    	canvas.scale(this.w_factor, this.h_factor);
    	
    	//drawing background
    	paint.setColor(Color.WHITE);
    	canvas.drawRect(0, 0, 480, 800, this.paint);
    	
    	//rect positions
    	int orientationPosition_X = 240;
    	int orientationPosition_Y = orientationPosition_X + 180;
    	int orientationPosition_Z = orientationPosition_Y + 180;
    	
    	//X sensor rect
    	paint.setColor(Color.BLUE);
    	canvas.drawRect(orientationPosition_X, orientationPosition_X, orientationPosition_X + (int)sensorOrientation_X, orientationPosition_X + 10, paint);
    	
    	//Y sensor rect
    	paint.setColor(Color.GREEN);
    	canvas.drawRect(orientationPosition_X, orientationPosition_Y, orientationPosition_X + (int)sensorOrientation_Y, orientationPosition_Y + 10, paint);
    	
    	//Z sensor rect
    	paint.setColor(Color.RED);
    	canvas.drawRect(orientationPosition_X, orientationPosition_Z, orientationPosition_X + (int)sensorOrientation_Z, orientationPosition_Z + 10, paint);
    	
    	paint.setColor(Color.BLACK);
    	canvas.drawText("sensor X : " + sensorOrientation_X, orientationPosition_X, orientationPosition_Z + 40, paint);
    	canvas.drawText("sensor Y : " + sensorOrientation_Y, orientationPosition_X, orientationPosition_Z + 60, paint);
    	canvas.drawText("sensor Z : " + sensorOrientation_Z, orientationPosition_X, orientationPosition_Z + 80, paint);
    	
    	canvas.drawText("accelerometer X : " + accelerometer_X, orientationPosition_X, orientationPosition_Z + 100, paint);
    	canvas.drawText("accelerometer Y : " + accelerometer_Y, orientationPosition_X, orientationPosition_Z + 120, paint);
    	canvas.drawText("accelerometer Z : " + accelerometer_Z, orientationPosition_X, orientationPosition_Z + 140, paint);
    	
    	canvas.drawText("frequency : " + frequency + "Hz", orientationPosition_X, orientationPosition_Z + 160, paint);
    }
   
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	int touchInd = event.getActionIndex();
    	float x = event.getX(touchInd)/ this.w_factor;
        float y = event.getY(touchInd) / this.h_factor;
    	
        return super.onTouchEvent(event);		
    }
  	
    //width -> szerokosc bitmapy podzielona przez rows
    public void drawSprite(Canvas canvas, int x, int y, int columns, int rows, int width, int height, int currentFrame, Bitmap bmp, float angle, float scale){
    	//rotacja
		canvas.save();
		canvas.rotate(angle, x, y);
		
		//skala
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
		
		canvas.restore();
    }
    
    public void updateSensor(int sensor, float[] values){
    	
    	//every second reset timmer
    	if(System.currentTimeMillis() - lastUpdate >= 1000){
    		lastUpdate = System.currentTimeMillis();
    		frequency = updateCounter;
    		updateCounter = 0;
    	}
    	else{
    		updateCounter++;
    	}
    	
    	if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
    		accelerometer_X = values[0];
    		accelerometer_Y = values[1];
    		accelerometer_Z = values[2];
    	}
    	if (sensor == SensorManager.SENSOR_ORIENTATION) {
    		Log.d("ArtificialHorizon", "onSensorChanged: " + sensor + ", x: " + values[0] + ", y: " + values[1] + ", z: " + values[2]);
    		sensorOrientation_X = values[0];
    		sensorOrientation_Y = values[1];
    		sensorOrientation_Z = values[2];
    	}
    }
}
    