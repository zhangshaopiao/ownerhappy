package com.zhang.zsp.zhangshaopiao.adapter;

import java.util.List;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class ViewPagerAdapter extends PagerAdapter{
	private final static String TAG = "ViewPagerAdapter";
	private List<View> views;
	private Context mContext;

	public ViewPagerAdapter(List<View> views,Context c) {
		this.views = views;
		mContext = c;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public Object instantiateItem(View collection, int position) {
		View view = views.get(position);
		((ViewPager) collection).addView(view);
		return view;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (object);
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

}
