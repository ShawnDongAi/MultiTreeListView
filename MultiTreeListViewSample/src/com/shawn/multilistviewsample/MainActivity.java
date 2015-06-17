package com.shawn.multilistviewsample;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.shawn.multilistview.library.Group;
import com.shawn.multilistview.library.GroupViewHolder;
import com.shawn.multilistview.library.HorizontalListView;
import com.shawn.multilistview.library.MultiTreeListView;
import com.shawn.multilistview.library.MultiTreeListView.OnDataItemViewDraw;
import com.shawn.multilistview.library.OnDataClickListener;
import com.shawn.multilistview.library.OnDataLongClickListener;

public class MainActivity extends Activity {
	private HorizontalListView titleList;
	private MultiTreeListView listview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		titleList = (HorizontalListView) findViewById(R.id.title_list);
		listview = (MultiTreeListView) findViewById(R.id.list_container);

		listview.setOnDataItemViewDraw(new OnDataItemViewDraw() {
			@Override
			public int setDataItemLayout() {
				return R.layout.data_item;
			}

			@Override
			public void onDataItemViewDraw(GroupViewHolder viewHolder,
					Object data) {
				viewHolder.setText(R.id.data_title, data.toString());
			}
		});
		listview.setGroupList(initData(), titleList, "重置");
		// 设置不是当前节点的listview是否有焦点
		listview.setParentGroupEnable(true);
		// 设置是否可通过滑动来回到某一个上级
		listview.setParentGroupMoveable(true);

		listview.setOnDataClickListener(new OnDataClickListener() {
			@Override
			public void onDataClick(Object data) {
				Toast.makeText(MainActivity.this, data.toString() + " clicked",
						Toast.LENGTH_SHORT).show();
			}
		});
		listview.setOnDataLongClickListener(new OnDataLongClickListener() {
			@Override
			public void onDataLongClick(Object data) {
				Toast.makeText(MainActivity.this,
						data.toString() + " long clicked", Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	// 数据准备
	private List<Group> initData() {
		List<Group> groups = new ArrayList<Group>();
		for (int i = 0; i < 10; i++) {
			List<Group> child1s = new ArrayList<Group>();
			for (int j = 0; j < 10; j++) {
				List<Group> child2s = new ArrayList<Group>();
				for (int k = 0; k < 10; k++) {
					Group child2 = new Group(k + "", j + "", "data" + j + "_"
							+ k, Group.TYPE_DATA);
					child2s.add(child2);
				}
				Group child1 = new Group(j + "", i + "", "child" + i + "_" + j,
						Group.TYPE_GROUP);
				child1.setLeafs(child2s);
				child1s.add(child1);
			}
			Group parent = new Group(i + "", "", "group_" + i, Group.TYPE_GROUP);
			parent.setLeafs(child1s);
			groups.add(parent);
		}
		return groups;
	}
}
