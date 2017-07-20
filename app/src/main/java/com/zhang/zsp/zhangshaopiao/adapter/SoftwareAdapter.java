package com.zhang.zsp.zhangshaopiao.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.zhang.zsp.zhangshaopiao.R;
import com.zhang.zsp.zhangshaopiao.model.AppInfo;
import com.zhang.zsp.zhangshaopiao.views.RippleView;

import java.util.ArrayList;
import java.util.List;

import utils.StorageUtil;
import utils.T;

public class SoftwareAdapter extends BaseAdapter {

    public List<AppInfo> mlistAppInfo;
    LayoutInflater infater = null;
    private Context mContext;
    public static List<Integer> clearIds;


    public SoftwareAdapter(Context context, List<AppInfo> apps) {
        infater = LayoutInflater.from(context);
        mContext = context;
        clearIds = new ArrayList<Integer>();
        this.mlistAppInfo = apps;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mlistAppInfo.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mlistAppInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = infater.inflate(R.layout.listview_software, null);
            holder = new ViewHolder();
            holder.appIcon = (ImageView) convertView.findViewById(R.id.app_icon);
            holder.appName = (TextView) convertView.findViewById(R.id.app_name);
            holder.size = (TextView) convertView.findViewById(R.id.app_size);
            holder.ripleUninstall = (RippleView) convertView.findViewById(R.id.riple_uninstall);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final AppInfo item = (AppInfo) getItem(position);
        if (item != null) {
            holder.appIcon.setImageDrawable(item.getAppIcon());
            holder.appName.setText(item.getAppName());

            if (item.isInRom()) {
                holder.size.setText(StorageUtil.convertStorage(item.getPkgSize()));
            } else {
                holder.size.setText(StorageUtil.convertStorage(item.getPkgSize()));
            }
            //holder.size.setText(StorageUtil.convertStorage(item.getUid()));
        }
        holder.ripleUninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.getPackname().equals(mContext.getPackageName())){
                    T.showShort(mContext,"你要杀了自己？" + "！！！ 不要这么残忍！");
                    return;
                }
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setAction("android.intent.action.DELETE");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + item.getPackname()));
                mContext.startActivity(intent);
            }
        });


        return convertView;
    }


    class ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView size;
        RippleView ripleUninstall;

        String packageName;
    }

}