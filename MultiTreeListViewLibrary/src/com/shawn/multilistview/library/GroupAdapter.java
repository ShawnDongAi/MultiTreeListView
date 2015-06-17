package com.shawn.multilistview.library;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shawn.multilistview.library.MultiTreeListView.OnDataItemViewDraw;
import com.shawn.multitreelistview.lib.R;

/**
 * listview分组及数据适配器
 * 
 * @author Shawn
 * @version 2015-06-16
 */
public class GroupAdapter extends BaseAdapter {
	private Context mContext;
	private List<Group> groupsList = new ArrayList<Group>();
	private OnDataItemViewDraw onDataItemViewOnDraw;

	public GroupAdapter(Context context, OnDataItemViewDraw onDataItemViewOnDraw) {
		this.mContext = context;
		this.onDataItemViewOnDraw =onDataItemViewOnDraw;
	}

	public void setGroups(List<Group> groups) {
		this.groupsList = groups;
	}

	public void addGroup(Group group) {
		groupsList.add(group);
	}

	public void clear() {
		groupsList.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return groupsList.size();
	}

	@Override
	public Group getItem(int position) {
		return groupsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return groupsList.get(position).getType();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (getItemViewType(position) == Group.TYPE_GROUP) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(mContext, R.layout.group_item, null);
				holder.title = (TextView) convertView
						.findViewById(R.id.group_title);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.title.setText(getItem(position).getData().toString());
			return convertView;
		} else {
			final GroupViewHolder viewHolder = getViewHolder(position,
					convertView, parent);
			onDataItemViewOnDraw.onDataItemViewDraw(viewHolder, getItem(position).getData());
			return viewHolder.getConvertView();
		}
	}

	private class ViewHolder {
		private TextView title;
	}

	private GroupViewHolder getViewHolder(int position, View convertView,
			ViewGroup parent) {
		return GroupViewHolder.get(mContext, convertView, parent, onDataItemViewOnDraw.setDataItemLayout(),
				position);
	}
}
