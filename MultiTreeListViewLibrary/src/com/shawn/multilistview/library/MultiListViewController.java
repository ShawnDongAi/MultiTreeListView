package com.shawn.multilistview.library;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.shawn.multilistview.library.MultiTreeListView.ChildRemoveCallback;
import com.shawn.multitreelistview.lib.R;

/**
 * listview控制器
 * 
 * @author Shawn
 * @version 2015-06-16
 */
public class MultiListViewController {
	private Context mContext;
	private MultiTreeListView listContainer;
	private GroupTitleAdapter titleAdapter;
	private HorizontalListView titleList;
	private Group clearGroup;

	public MultiListViewController(Context context,
			MultiTreeListView listViewContainer) {
		this.mContext = context;
		this.listContainer = listViewContainer;
	}
	
	protected void removeTitle(int index) {
		if (titleAdapter != null) {
			titleAdapter.remove(index);
			titleAdapter.notifyDataSetChanged();
			titleList.scrollToPosition(index - 1);
		}
	}

	/**
	 * 填充数据
	 * 
	 * @param groups
	 * @param titleListView
	 * @param titleListIndexText
	 */
	protected void setGroupList(List<Group> groups,
			HorizontalListView titleListView, String titleListIndexText) {
		clearGroup = new Group("", "", titleListIndexText, Group.TYPE_GROUP);
		titleAdapter = new GroupTitleAdapter(mContext);
		this.titleList = titleListView;
		titleAdapter.addGroup(clearGroup);
		titleList.setAdapter(titleAdapter);
		titleList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				if (position == titleAdapter.getCount() - 1) {
					return;
				}
				listContainer.clearChildRemoveCallback();
				for (int i = listContainer.getChildCount() - 1; i > position; i--) {
					listContainer.removeChildList(i);
				}
				listContainer.requestChildList(position, true);
				titleAdapter.remove(position + 1);
				titleAdapter.notifyDataSetChanged();
				titleList.scrollToPosition(position);
			}
		});

		listContainer.removeAllViews();
		packagingList(groups);
	}

	/**
	 * 组装listview
	 * 
	 * @param groups
	 */
	protected void packagingList(List<Group> groups) {
		ListView listview = (ListView) View.inflate(mContext,
				R.layout.base_listview, null);
		GroupAdapter adapter = new GroupAdapter(mContext,
				listContainer.getOnDataItemViewDraw());
		adapter.setGroups(groups);
		listview.setAdapter(adapter);
		int position = listContainer.getChildCount();
		listview.setTag(position);
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListAdapter adapter = (ListAdapter) parent.getAdapter();
				if (adapter instanceof GroupAdapter) {
					Group group = ((GroupAdapter) adapter)
							.getItem(position);
					if (group.getType() == Group.TYPE_DATA) {
						if (listContainer.getOnDataLongClickListener() != null) {
							listContainer.getOnDataLongClickListener()
									.onDataLongClick(group.getData());
						}
					}
				}
				return false;
			}
		});
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, View view,
					int position, long id) {
				int childPos = Integer.parseInt(parent.getTag().toString());
				ListAdapter adapter = (ListAdapter) parent.getAdapter();
				listContainer.clearChildRemoveCallback();
				if (adapter instanceof GroupAdapter) {
					final Group group = ((GroupAdapter) adapter)
							.getItem(position);
					if (group.getType() == Group.TYPE_DATA) {
						if (listContainer.getOnDataClickListener() != null) {
							listContainer.getOnDataClickListener().onDataClick(
									group.getData());
						}
						return;
					}
					listContainer
							.setChildRemoveCallback(new ChildRemoveCallback() {
								@Override
								public void onChildRemoved(int index) {
									int childPos = Integer.parseInt(parent
											.getTag().toString());
									if (index == childPos + 1) {
										packagingList(group.getLeafs());
										titleAdapter.remove(childPos + 1);
										titleAdapter.addGroup(group);
										titleAdapter.notifyDataSetChanged();
										titleList
												.scrollToPosition(childPos + 1);
									}
								}
							});
					for (int i = listContainer.getChildCount() - 1; i > childPos; i--) {
						listContainer.removeChildList(i);
					}
					if (childPos == listContainer.getChildCount() - 1) {
						packagingList(group.getLeafs());
						titleAdapter.addGroup(group);
						titleAdapter.notifyDataSetChanged();
						titleList.scrollToPosition(childPos + 1);
					}
				}
			}
		});
		listContainer.addChilList(listview);
	}
}
