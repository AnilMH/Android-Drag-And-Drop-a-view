package com.example.draganddrop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author anil.mh
 * 
 *         In this class all 3 functions are add 1. Tap and Drag 2. Tap and Tap
 *         text view to Edit text 3. Tap and tap edit text to Text view
 * 
 */
public class MainActivity extends Activity { 

	private RelativeLayout relativeLayoutQustions;
	private RelativeLayout relativeLayoutOptions;

	private ClipData clipData;
	private ClipboardManager myClipboard;
	String tapedText = "";
	View tapedView = null;
	View oldTextView;
	View oldEditView;

	View viewText;
	View viewEditText;
	View emptyView;

	private static final float SCALE_FACTOR = 4.0f / 3;

	public enum TypeOfMove {
		RESET_TEXT, RESET_EDIT_TEXT, ADD_LISTENER, INIT_EDIT_TEXT, ACTION_TEXT_DROPPED, ACTION_TEX_ON_MOVE
	}

	public static class QuestionViewHolder {
		public TypeOfMove type;
		public int styleResourceID;
		public OnTouchListener touchListener;
		public OnClickListener clickListener;
		public TextView viewBeingDragged;

	}

	// QuestionViewHolder questionEditTextDefault;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		relativeLayoutQustions = (RelativeLayout) findViewById(R.id.relativeLayoutQustions);
		relativeLayoutOptions = (RelativeLayout) findViewById(R.id.relativeLayoutOptions);

		relativeLayoutQustions.setOnDragListener(dragAndDropForLayout);
		myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

		QuestionViewHolder questionEditTextDefault = new QuestionViewHolder();
		questionEditTextDefault.type = TypeOfMove.INIT_EDIT_TEXT;
		questionEditTextDefault.clickListener = onClickEditText;
		findAndApplyStyleOrData(relativeLayoutQustions, questionEditTextDefault);

		questionEditTextDefault = new QuestionViewHolder();
		questionEditTextDefault.type = TypeOfMove.ADD_LISTENER;
		questionEditTextDefault.clickListener = onclickTextViewColorChange;
		questionEditTextDefault.touchListener = onTouchLisTextviews;

		findAndApplyStyleOrData(relativeLayoutOptions, questionEditTextDefault);

	}

	/*
	 * TO change the Background color of Text View and Edit text
	 */
	private OnClickListener onclickTextViewColorChange = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (oldTextView != v) {

				if (viewEditText instanceof EditText) {
					((EditText) viewEditText).setText(((TextView) v).getText().toString());

					QuestionViewHolder questionEditTextDefault = new QuestionViewHolder();
					questionEditTextDefault.type = TypeOfMove.RESET_EDIT_TEXT;
					questionEditTextDefault.styleResourceID = R.drawable.edittext_style;

					findAndApplyStyleOrData(relativeLayoutQustions, questionEditTextDefault);
					viewEditText = null;
					oldTextView = null;
					viewText = null;
				} else {

					QuestionViewHolder questionEditTextDefault = new QuestionViewHolder();
					questionEditTextDefault.type = TypeOfMove.RESET_TEXT;
					questionEditTextDefault.styleResourceID = R.drawable.edittext_style;

					findAndApplyStyleOrData(relativeLayoutOptions, questionEditTextDefault);
					viewText = v;
					v.setBackgroundResource(R.drawable.button_style_onclick);
					oldTextView = v;
					oldEditView = null;

				}
			} else {
				oldTextView = v;
				viewText = null;
				v.setBackgroundResource(R.drawable.edittext_style);
			}

		}
	};

	/*
	 * TO add the values if user Tap and Tap
	 */
	private OnClickListener onClickEditText = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (oldEditView != v) {

				if (viewText != null) {

					((EditText) v).setText(((TextView) viewText).getText().toString());

					QuestionViewHolder questionEditTextDefault = new QuestionViewHolder();
					questionEditTextDefault.type = TypeOfMove.RESET_TEXT;
					questionEditTextDefault.styleResourceID = R.drawable.edittext_style;

					findAndApplyStyleOrData(relativeLayoutOptions, questionEditTextDefault);
					oldEditView = null;
					viewEditText = null;
					viewText = null;
					oldTextView = null;

				} else {

					QuestionViewHolder questionEditTextDefault = new QuestionViewHolder();
					questionEditTextDefault.type = TypeOfMove.RESET_EDIT_TEXT;
					questionEditTextDefault.styleResourceID = R.drawable.edittext_style;

					findAndApplyStyleOrData(relativeLayoutQustions, questionEditTextDefault);
					viewEditText = v;
					v.setBackgroundResource(R.drawable.button_style_onclick);
					oldEditView = v;
					oldTextView = null;

				}
			} else {
				oldEditView = v;
				viewEditText = null;
				v.setBackgroundResource(R.drawable.edittext_style);
			}

		}
	};

	/*
	 * TO Drag the text view elements
	 */
	private OnTouchListener onTouchLisTextviews = new OnTouchListener() {

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			if (MotionEvent.ACTION_MOVE == event.getAction()) {

				QuestionViewHolder questionEditTextDefault = new QuestionViewHolder();
				questionEditTextDefault.type = TypeOfMove.RESET_TEXT;
				questionEditTextDefault.styleResourceID = R.drawable.edittext_style;

				findAndApplyStyleOrData(relativeLayoutOptions, questionEditTextDefault);
				clipData = ClipData.newPlainText("", v.getTag(R.id.viewTag).toString());
				myClipboard.setPrimaryClip(clipData);
				View.DragShadowBuilder shadow = new View.DragShadowBuilder(v);
				shadow = new MyDragShadowBuilder(v);
				float x = event.getX();
				float y = event.getY();
				v.setTag(R.id.offsetX, x);
				v.setTag(R.id.offsetY, y);
				v.startDrag(clipData, shadow, v, 0);

			}
			return false;

		}
	};

	/**
	 * TO change the view Background color and insit the Click Listener for edit
	 * text
	 * 
	 * @param dstView
	 * @param isType
	 */
	private void findAndApplyStyleOrData(View dstView, QuestionViewHolder questionViewHolder) {

		try {

			if (dstView instanceof ViewGroup) {
				ViewGroup viewGroup = (ViewGroup) dstView;
				for (int i = 0; i < viewGroup.getChildCount(); i++) {
					View childView = viewGroup.getChildAt(i);
					findAndApplyStyleOrData(childView, questionViewHolder);
				}
			} else if (dstView instanceof View) {

				switch (questionViewHolder.type) {
				case RESET_TEXT:

					if ((dstView instanceof TextView)) {
						TextView prospectView = (TextView) dstView;
						prospectView.setBackgroundResource(questionViewHolder.styleResourceID);

					}

					break;
				case RESET_EDIT_TEXT:

					if (dstView instanceof EditText) {
						EditText prospectView = (EditText) dstView;

						prospectView.setBackgroundResource(questionViewHolder.styleResourceID);

					}

					break;
				case ADD_LISTENER:
					if (dstView instanceof TextView) {
						TextView prospectView = (TextView) dstView;

						prospectView.setTag(R.id.viewTag, prospectView.getText().toString());
						prospectView.setOnTouchListener(questionViewHolder.touchListener);
						prospectView.setOnClickListener(questionViewHolder.clickListener);

					}

					break;
				case INIT_EDIT_TEXT:

					if (dstView instanceof EditText) {
						EditText prospectView = (EditText) dstView;
						prospectView.setFocusable(false);
						prospectView.setLines(1);
						prospectView.setSingleLine(true);
						prospectView.setTextSize(14);
						prospectView.setEms(6);
						prospectView.setOnClickListener(questionViewHolder.clickListener);

					}

					break;
				case ACTION_TEX_ON_MOVE:
				case ACTION_TEXT_DROPPED:
					dropTextInsideProspectView(dstView, questionViewHolder);
					break;
				default:
					break;
				}

			}
		} catch (Exception ex) {

		}
	}

	private void dropTextInsideProspectView(View dstView, QuestionViewHolder viewHolder) {

		TextView viewBeingDragged = viewHolder.viewBeingDragged;
		EditText prospectView = (EditText) dstView;

		int[] location = getScreenLocation(prospectView);

		float xOfProspect = location[0];

		float yOfProspect = location[1];

		float lastX = Float.parseFloat(viewBeingDragged.getTag(R.id.lastLocationX).toString());

		float lastY = Float.parseFloat(viewBeingDragged.getTag(R.id.lastLocationY).toString());
		float locationOfDraggedviewX = lastX + prospectView.getTotalPaddingLeft() + prospectView.getTotalPaddingRight();
		float locationOfDraggedviewY = lastY + prospectView.getTotalPaddingBottom() + prospectView.getTotalPaddingTop();

		float positionOfShadowX = 0;
		float positionOfShadowY = 0;
		float scaleFactor = 1;
		try {

			int orientation = getResources().getConfiguration().orientation;
			// if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			positionOfShadowX = lastX - (Float.parseFloat(viewBeingDragged.getTag(R.id.offsetX).toString()));
			positionOfShadowY = lastY;

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// find if the two rectangles intersect
		CustomRectangle prospectEditTextRect = new CustomRectangle(new CustomPoint(xOfProspect, yOfProspect),
				prospectView.getWidth(), prospectView.getHeight());

		scaleFactor = SCALE_FACTOR;
		CustomRectangle shadowRectangle = new CustomRectangle(new CustomPoint(positionOfShadowX, positionOfShadowY),
				viewBeingDragged.getWidth() * scaleFactor, viewBeingDragged.getHeight() * scaleFactor);

		boolean isWithin = shadowRectangle.intersectsRectangle(prospectEditTextRect)
				|| prospectEditTextRect.intersectsRectangle(shadowRectangle);

		if (isWithin) {

			if (viewHolder.type == TypeOfMove.ACTION_TEX_ON_MOVE) {
				prospectView.setBackgroundResource(R.drawable.edittext_style_backround);
			} else if (viewHolder.type == TypeOfMove.ACTION_TEXT_DROPPED) {
				prospectView.setText(viewBeingDragged.getText());

				QuestionViewHolder questionEditTextDefault = new QuestionViewHolder();
				questionEditTextDefault.type = TypeOfMove.RESET_EDIT_TEXT;
				questionEditTextDefault.styleResourceID = R.drawable.edittext_style;
				findAndApplyStyleOrData(relativeLayoutQustions, questionEditTextDefault);

				questionEditTextDefault = new QuestionViewHolder();
				questionEditTextDefault.type = TypeOfMove.RESET_TEXT;
				questionEditTextDefault.styleResourceID = R.drawable.edittext_style;
				findAndApplyStyleOrData(relativeLayoutOptions, questionEditTextDefault);
				tapedText = "";
			}
		} else {
			if (viewHolder.type == TypeOfMove.ACTION_TEX_ON_MOVE) {
				prospectView.setBackgroundResource(R.drawable.edittext_style);
			}
		}

	}

	public static class CustomRectangle {
		public CustomPoint topLeft;
		public CustomPoint topRight;
		public CustomPoint bottomLeft;
		public CustomPoint bottomRight;
		public float width;
		public float height;

		public CustomRectangle(CustomPoint top, float width, float height) {
			this.width = width;
			this.height = height;
			this.topLeft = top;
			this.topRight = getTopRight();
			this.bottomLeft = getBottomLeft();
			this.bottomRight = getBottomRght();
		}

		public boolean containsPoint(CustomPoint p) {
			return isInRange(p.x, topLeft.x, width) && isInRange(p.y, topLeft.y, height);
		}

		private CustomPoint getTopRight() {
			return new CustomPoint(topLeft.x + width, topLeft.y);
		}

		private CustomPoint getBottomLeft() {
			return new CustomPoint(topLeft.x, topLeft.y + height);
		}

		private CustomPoint getBottomRght() {
			return new CustomPoint(topLeft.x + width, topLeft.y + height);
		}

		public boolean intersectsRectangle(CustomRectangle rect) {
			return (containsPoint(rect.topLeft)) || containsPoint(rect.topRight) || containsPoint(rect.bottomLeft)
					|| containsPoint(rect.bottomRight);
		}

		@Override
		public String toString() {
			return "tl ==> " + topLeft + " tr ==> " + topRight + " br ==> " + bottomRight + " bl ==> " + bottomLeft
					+ " width ==> " + width + " height ==> " + height;
		}

	}

	public static class CustomPoint {
		public float x;
		public float y;

		public CustomPoint(float x, float y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "(" + x + "," + y + ")";
		}

	}

	private int[] getScreenLocation(View prospectView) {
		int[] location = { 0, 0 };
		prospectView.getLocationOnScreen(location);
		return location;
	}

	private static boolean isInRange(float toFind, float start, float dimension) {
		if (toFind >= start && toFind <= dimension + start) {
			return true;
		}

		return false;
	}

	private OnDragListener dragAndDropForLayout = new OnDragListener() {

		@Override
		public boolean onDrag(View view, DragEvent dragevent) {
			final int action = dragevent.getAction();
			float x = dragevent.getX();
			float y = dragevent.getY();
			View view12 = (View) dragevent.getLocalState();

			System.out.println("x,y== (" + x + "," + y + ")");
			switch (action) {
			case DragEvent.ACTION_DRAG_LOCATION:
				System.out.println("ACTION_DRAG_LOCATION");
				view12.setTag(R.id.lastLocationX, x);
				view12.setTag(R.id.lastLocationY, y);
				System.out.println("ACTION_DRAG_ENTERED");

				if (view12 instanceof TextView) {
					QuestionViewHolder holder = new QuestionViewHolder();
					holder.type = TypeOfMove.ACTION_TEX_ON_MOVE;
					holder.viewBeingDragged = (TextView) view12;
					findAndApplyStyleOrData(view, holder);
				}
				return true;
			case DragEvent.ACTION_DRAG_STARTED:
				view12.setTag(R.id.lastLocationX, 0);
				view12.setTag(R.id.lastLocationY, 0);
				System.out.println("ACTION_DRAG_STARTED");
				return true;

			case DragEvent.ACTION_DRAG_EXITED:
				System.out.println("ACTION_DRAG_EXITED");
				return true;

			case DragEvent.ACTION_DRAG_ENTERED: {

				return true;
			}

			case DragEvent.ACTION_DROP: {
				System.out.println("ACTION_DROP");
				view12.setTag(R.id.lastLocationX, x);
				view12.setTag(R.id.lastLocationY, y);
				return true;
			}

			case DragEvent.ACTION_DRAG_ENDED: {
				System.out.println("ACTION_DRAG_ENDED");
				if (view12 instanceof TextView) {
					QuestionViewHolder holder = new QuestionViewHolder();
					holder.type = TypeOfMove.ACTION_TEXT_DROPPED;
					holder.viewBeingDragged = (TextView) view12;
					findAndApplyStyleOrData(view, holder);
				}
				return (true);

			}

			default:
				break;
			}
			return false;
		}
	};

	/**
	 * TO incre the drag layout view
	 * 
	 * @author anil.mh
	 * 
	 */
	private static class MyDragShadowBuilder extends View.DragShadowBuilder {

		private Point mScaleFactor;

		public MyDragShadowBuilder(View v) {
			super(v);

		}

		@Override
		public void onProvideShadowMetrics(Point size, Point touch) {
			float width;
			float height;
			width = getView().getWidth() * SCALE_FACTOR;
			height = getView().getHeight() * SCALE_FACTOR;
			size.set((int) width, (int) height);
			mScaleFactor = size;
			touch.set((int) (width / 2), (int) (height / 2));
		}

		@Override
		public void onDrawShadow(Canvas canvas) {

			canvas.scale(mScaleFactor.x / (float) getView().getWidth(), mScaleFactor.y / (float) getView().getHeight());
			getView().draw(canvas);
		}

	}

}
