package com.shawn.multilistview.library;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * 一个动态创建并添加listview的布局
 * 
 * @author Shawn
 * @version 2015-06-16
 */
public class MultiTreeListView extends FrameLayout {
	private ChildRemoveCallback mChildRemoveCallback;
	private MultiListViewController controller;
	private OnDataClickListener onDataClickListener;
	private OnDataLongClickListener onDataLongClickListener;
	private OnDataItemViewDraw onDataItemViewDraw;
	private boolean parentGroupEnable = true;
	private boolean parentGroupMoveable = true;
	private float startX = 0;
	private boolean isScrollMode = false;
	private float maxX, minX = 0;
	private View moveView;
	private VelocityTracker mVelocityTracker;
	private float lastVelocity = 0;

	public MultiTreeListView(Context context) {
		super(context);
		init(getContext());
	}

	public MultiTreeListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(getContext());
	}

	public MultiTreeListView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(getContext());
	}

	private void init(Context context) {
		controller = new MultiListViewController(context, this);
	}

	protected void setChildRemoveCallback(
			ChildRemoveCallback childRemoveCallback) {
		this.mChildRemoveCallback = childRemoveCallback;
	}

	protected void clearChildRemoveCallback() {
		this.mChildRemoveCallback = null;
	}

	public void setOnDataClickListener(OnDataClickListener onDataClickListener) {
		this.onDataClickListener = onDataClickListener;
	}

	protected OnDataClickListener getOnDataClickListener() {
		return this.onDataClickListener;
	}

	public void setOnDataLongClickListener(
			OnDataLongClickListener onDataLongClickListener) {
		this.onDataLongClickListener = onDataLongClickListener;
	}

	protected OnDataLongClickListener getOnDataLongClickListener() {
		return this.onDataLongClickListener;
	}

	public void setOnDataItemViewDraw(OnDataItemViewDraw onDataItemViewDraw) {
		this.onDataItemViewDraw = onDataItemViewDraw;
	}

	protected OnDataItemViewDraw getOnDataItemViewDraw() {
		return this.onDataItemViewDraw;
	}

	/**
	 * 设置不是当前节点的listview是否有焦点
	 * 
	 * @param clickable
	 */
	public void setParentGroupEnable(boolean enable) {
		this.parentGroupEnable = enable;
	}

	/**
	 * 设置是否可通过滑动来回到上一级
	 * 
	 * @param moveable
	 */
	public void setParentGroupMoveable(boolean moveable) {
		this.parentGroupMoveable = moveable;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getX() < getChildAt(getChildCount() - 1).getX()
				&& !parentGroupEnable) {
			return true;
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (getChildCount() > 1) {
				startX = ev.getRawX();
				maxX = getMeasuredWidth();
				minX = getMeasuredWidth() / 3;
				moveView = getChildAt(getChildCount() - 1);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (getChildCount() <= 1 || !parentGroupMoveable) {
				break;
			}
			if (mVelocityTracker == null) {
	            mVelocityTracker = VelocityTracker.obtain();
	        }
			mVelocityTracker.addMovement(ev);
			mVelocityTracker.computeCurrentVelocity(100);
			lastVelocity = mVelocityTracker.getXVelocity();
			int xOffset = (int) (ev.getX() - startX);
			if (xOffset > 15 || isScrollMode) {
				isScrollMode = true;
				return true;
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (isScrollMode) {
				isScrollMode = false;
				return true;
			}
			break;
		default:
			break;
		}
		isScrollMode = false;
		return super.onInterceptTouchEvent(ev);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			float distance = event.getRawX() - startX + minX;
			if (distance > maxX) {
				startX = startX + distance - maxX;
				distance = maxX;
			}
			if (distance < minX) {
				startX = startX + distance - minX;
				distance = minX;
			}
			moveChild(moveView, distance);
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (moveView.getTranslationX() < (maxX - minX) / 4 + minX) {
				reset();
			} else if (moveView.getTranslationX() >= (maxX - minX) / 2 + minX
					|| lastVelocity > 80) {
				doneMove();
			} else {
				reset();
			}
			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	private void moveChild(View childView, float distance) {
		ViewHelper.setTranslationX(childView, distance);
		ViewHelper.setAlpha(getChildAt(indexOfChild(childView) - 1), distance
				/ maxX);
	}

	private void doneMove() {
		clearChildRemoveCallback();
		setChildRemoveCallback(new ChildRemoveCallback() {
			@Override
			public void onChildRemoved(int index) {
				controller.removeTitle(index);
				requestChildList(getChildCount() - 1, true);
			}
		});
		removeChildList(getChildCount() - 1);
	}

	private void reset() {
		requestChildList(getChildCount() - 1, true);
	}

	/**
	 * 返回当前触摸的视图在此容器中的位置
	 * 
	 * @param x
	 * @return
	 */
	private int getChildIndex(float x) {
		int childCount = getChildCount();
		for (int index = childCount - 1; index >= 0; index--) {
			if (x >= getChildAt(index).getTranslationX()) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * 填充数据
	 * 
	 * @param groups
	 * @param titleList
	 * @param titleListIndexText
	 */
	public void setGroupList(List<Group> groups, HorizontalListView titleList,
			String titleListIndexText) {
		controller.setGroupList(groups, titleList, titleListIndexText);
	}

	/**
	 * 动态添加listview
	 * 
	 * @param listview
	 */
	protected void addChilList(ListView listview) {
		addView(listview);
		int count = getChildCount();
		if (count < 2) {
			return;
		}
		requestChildList(count - 1, false);
		float distance = getMeasuredWidth() / 3;
		AnimatorSet addAnim = buildAddAnimation(listview, distance);
		addAnim.start();
	}

	/**
	 * 重新整理各个listview位置
	 * 
	 * @param lastIndex
	 * @param moveLastChild
	 */
	protected void requestChildList(int lastIndex, boolean moveLastChild) {
		if (moveLastChild) {
			float lastDistance = lastIndex == 0 ? 0 : getMeasuredWidth() / 3;
			AnimatorSet moveAnim = buildMoveAnimation(getChildAt(lastIndex),
					getChildAt(lastIndex).getTranslationX(), lastDistance);
			AnimatorSet alphaAnim = buildAlphaAnimation(getChildAt(lastIndex),
					ViewHelper.getAlpha(getChildAt(lastIndex)), 1f);
			moveAnim.playTogether(alphaAnim);
			moveAnim.start();
		}
		for (int i = lastIndex - 1; i >= 0; i--) {
			float distance = (getMeasuredWidth() / (3 * lastIndex)) * i;
			AnimatorSet anim = buildMoveAnimation(getChildAt(i), getChildAt(i)
					.getTranslationX(), distance);
			AnimatorSet alphaAnim = buildAlphaAnimation(getChildAt(i),
					ViewHelper.getAlpha(getChildAt(i)), (i + 1) * 1f
							/ (lastIndex + 1) * 1f);
			anim.playTogether(alphaAnim);
			anim.start();
		}
	}

	/**
	 * 移除指定listview
	 * 
	 * @param index
	 */
	protected void removeChildList(int index) {
		AnimatorSet removeAnim = buildRemoveAnimation(getChildAt(index), index);
		removeAnim.start();
	}

	/**
	 * 构建透明度变化的动画
	 * 
	 * @param target
	 * @param from
	 * @param to
	 * @return
	 */
	private AnimatorSet buildAlphaAnimation(View target, float from, float to) {
		AnimatorSet transAnimation = new AnimatorSet();
		transAnimation.playTogether(ObjectAnimator.ofFloat(target, "alpha",
				from, to));
		transAnimation.setDuration(300);
		return transAnimation;
	}

	/**
	 * 构建添加listview的动画
	 * 
	 * @param target
	 * @param distance
	 * @return
	 */
	private AnimatorSet buildAddAnimation(View target, float distance) {
		AnimatorSet transAnimation = new AnimatorSet();
		transAnimation.playTogether(ObjectAnimator.ofFloat(target,
				"translationX", getMeasuredWidth(), distance));
		transAnimation.setDuration(300);
		return transAnimation;
	}

	/**
	 * 构建移动listview位置的动画
	 * 
	 * @param target
	 * @param from
	 * @param to
	 * @return
	 */
	private AnimatorSet buildMoveAnimation(View target, float from, float to) {
		AnimatorSet transAnimation = new AnimatorSet();
		transAnimation.playTogether(ObjectAnimator.ofFloat(target,
				"translationX", from, to));
		transAnimation.setDuration(300);
		return transAnimation;
	}

	/**
	 * 构建移除listview的动画
	 * 
	 * @param target
	 * @param index
	 * @return
	 */
	private AnimatorSet buildRemoveAnimation(View target, int index) {
		AnimatorSet transAnimation = new AnimatorSet();
		transAnimation.playTogether(ObjectAnimator.ofFloat(target,
				"translationX", target.getTranslationX(), getMeasuredWidth()));
		transAnimation.addListener(new RemoveAnimListener(index));
		transAnimation.setDuration(300);
		return transAnimation;
	}

	/**
	 * 移除listview的动画监听
	 * 
	 * @author Shawn
	 * @version 2015-06-16
	 */
	private class RemoveAnimListener implements AnimatorListener {
		private int index;

		public RemoveAnimListener(int index) {
			this.index = index;
		}

		@Override
		public void onAnimationCancel(Animator arg0) {
			if (index < 0 || index >= MultiTreeListView.this.getChildCount()) {
				return;
			}
			MultiTreeListView.this.removeViewAt(index);
			if (mChildRemoveCallback != null) {
				mChildRemoveCallback.onChildRemoved(index);
			}
		}

		@Override
		public void onAnimationEnd(Animator arg0) {
			if (index < 0 || index >= MultiTreeListView.this.getChildCount()) {
				return;
			}
			MultiTreeListView.this.removeViewAt(index);
			if (mChildRemoveCallback != null) {
				mChildRemoveCallback.onChildRemoved(index);
			}
		}

		@Override
		public void onAnimationRepeat(Animator arg0) {
		}

		@Override
		public void onAnimationStart(Animator arg0) {
		}
	}

	/**
	 * listview被移除的回调
	 * 
	 * @author Shawn
	 * @version 2015-06-16
	 */
	protected interface ChildRemoveCallback {
		public void onChildRemoved(int index);
	}

	/**
	 * listview中数据项的视图绘制
	 * 
	 * @author Shawn
	 * @version 2015-06-16
	 */
	public interface OnDataItemViewDraw {
		public int setDataItemLayout();

		public void onDataItemViewDraw(GroupViewHolder viewHolder, Object data);
	}
}
