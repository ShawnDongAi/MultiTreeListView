package com.shawn.multilistview.library;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupViewHolder {

	private final SparseArray<View> mViews;
	private int mPosition;
	private View mConvertView;

	private GroupViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
		mPosition = position;
		this.mViews = new SparseArray<View>();
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
		// setTag
		mConvertView.setTag(this);
	}

	/**
	 * 拿到一个ViewHolder对象
	 * 
	 * @param context
	 * @param convertView
	 * @param parent
	 * @param layoutId
	 * @param position
	 * @return
	 */
	public static GroupViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
		if (convertView == null) {
			return new GroupViewHolder(context, parent, layoutId, position);
		}
		return (GroupViewHolder) convertView.getTag();
	}

	/**
	 * 通过控件的Id获取对于的控件，如果没有则加入views
	 * 
	 * @param viewId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends View> T getView(int viewId) {
		View view = mViews.get(viewId);
		if (view == null) {
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}

	public View getConvertView() {
		return mConvertView;
	}

	/**
	 * 为TextView设置字符串
	 * 
	 * @param viewId
	 * @param text
	 * @return
	 */
	public GroupViewHolder setText(int viewId, String text) {
		TextView view = getView(viewId);
		view.setText(text);
		return this;
	}

	/**
	 * 为ImageView设置图片
	 * 
	 * @param viewId
	 * @param drawableId
	 * @return
	 */
	public GroupViewHolder setImageResource(int viewId, int drawableId) {
		ImageView view = getView(viewId);
		view.setImageResource(drawableId);

		return this;
	}

	/**
	 * 为ImageView设置图片
	 * 
	 * @param viewId
	 * @param drawableId
	 * @return
	 */
	public GroupViewHolder setImageBitmap(int viewId, Bitmap bm) {
		ImageView view = getView(viewId);
		view.setImageBitmap(bm);
		return this;
	}

	/**
	 * 显示文字draw图片
	 * 
	 * @param viewId
	 * @param imgId
	 * @param bounds
	 * @return
	 */
	public GroupViewHolder setTextImage(int viewId, int imgId, int bounds) {
		TextView view = getView(viewId);
		Resources res = view.getResources();
		Drawable drawable = res.getDrawable(imgId);
		// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		switch (bounds) {
		case 0:
			view.setCompoundDrawables(drawable, null, null, null); // 设置左图标
			break;
		case 1:
			view.setCompoundDrawables(null, drawable, null, null); // 设置上图标
			break;
		case 2:
			view.setCompoundDrawables(null, null, drawable, null); // 设置右图标
			break;
		case 3:
			view.setCompoundDrawables(null, null, null, drawable); // 设置下图标
			break;
		default:
			break;
		}
		return this;
	}

	/**
	 * 设置是否显示
	 * 
	 * @param viewId
	 * @param visibility
	 * @return
	 */
	public GroupViewHolder setVisibility(int viewId, int visibility) {
		View view = getView(viewId);
		view.setVisibility(visibility);
		return this;
	}

	public int getPosition() {
		return mPosition;
	}

}
