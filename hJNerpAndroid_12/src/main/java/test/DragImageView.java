package test;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class DragImageView extends ImageView implements OnTouchListener {
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	
	private float initialx;
	private float initialy;
	
	public DragImageView(Context context,AttributeSet attrs){
		super(context,attrs);
		this.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View v,MotionEvent event){
		this.setScaleType(ScaleType.MATRIX);
		ImageView view = (ImageView) v;
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			matrix.set(getImageMatrix());
			savedMatrix.set(matrix);
			initialx = event.getX();
			initialy = event.getY();
			break;
		
		case MotionEvent.ACTION_MOVE:
			matrix.set(savedMatrix);
			matrix.postTranslate(event.getX() - initialx, event.getY() - initialy);
			break;
		}
		view.setImageMatrix(matrix);
		return true;
	}
}
