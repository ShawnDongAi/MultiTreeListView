package com.shawn.multilistview.library;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shawn.multitreelistview.lib.R;

/**
 * 顶部导航适配器
 * @author Shawn
 * @version 2015-06-16
 */
public class GroupTitleAdapter extends BaseAdapter {

	private Context mContext;
	private List<Group> groupsList = new ArrayList<Group>();

	public GroupTitleAdapter(Context context) {
		this.mContext = context;
	}

	public void addGroup(Group group) {
		groupsList.add(group);
	}

	public void remove(int index) {
		for (int i = groupsList.size() - 1; i >= index; i--) {
			groupsList.remove(i);
		}
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext,
					R.layout.title_item, null);
			holder.title = (TextView) convertView.findViewById(R.id.user_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.title.setText(getItem(position).getData().toString());
		return convertView;
	}

	private class ViewHolder {
		private TextView title;
	}
}
