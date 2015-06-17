package com.shawn.multilistview.library;

import java.util.ArrayList;
import java.util.List;

/**
 * 分组项
 * 
 * @author Shawn
 * @version 2015-06-16
 */
public class Group {
	/**
	 * 该项为分组 {@link #type}
	 */
	public static final int TYPE_GROUP = 0;
	/**
	 * 该项为数据 {@link #type}
	 */
	public static final int TYPE_DATA = 1;
	private int type = TYPE_GROUP;
	private String id;
	private String parentID;
	private Object data;
	private List<Group> leafs = new ArrayList<Group>();

	public Group(String id, String parentID, Object data, int type) {
		this.id = id;
		this.parentID = parentID;
		this.data = data;
		this.type = type;
	}

	/**
	 * 获取当前item类型</br> {@link #TYPE_GROUP}为分组项,{@link #TYPE_DATA}为数据项
	 * 
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * 设置当前item类型</br> {@link #TYPE_GROUP}为分组项,{@link #TYPE_DATA}为数据项
	 * 
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * 数据唯一标识
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置数据唯一标识
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 该数据父节点的标识
	 */
	public String getParentID() {
		return parentID;
	}

	/**
	 * 设置该数据父节点的标识
	 */
	public void setParentID(String parentID) {
		this.parentID = parentID;
	}

	/**
	 * 具体数据
	 */
	public Object getData() {
		return data;
	}

	/**
	 * 设置具体数据
	 */
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * 叶节点列表
	 */
	public List<Group> getLeafs() {
		return leafs;
	}

	/**
	 * 设置叶节点列表
	 */
	public void setLeafs(List<Group> leafs) {
		this.leafs = leafs;
	}
}
