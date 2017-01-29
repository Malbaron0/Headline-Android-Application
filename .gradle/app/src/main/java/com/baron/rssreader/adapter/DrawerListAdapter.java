package com.baron.rssreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baron.rssreader.model.vo.DrawerData;
import com.baron.rssreader.R;

import java.util.ArrayList;


public class DrawerListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<DrawerData> data;

    public DrawerListAdapter(Context context, ArrayList<DrawerData> objects) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        data = objects;
    }

    static class ViewHolder {
        int type;
        TextView title;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        DrawerData drawerData = data.get(position);
        if(drawerData.type == 1) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DrawerData drawerData = data.get(position);
        if(drawerData.type == 0) {
            if(viewHolder == null || viewHolder.type != 0) {
                convertView = inflater.inflate(R.layout.drawer_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.type = drawerData.type;
                viewHolder.title = (TextView) convertView.findViewById(R.id.navigation_drawer_list_item_title);
                convertView.setTag(viewHolder);
            }

            viewHolder.title.setText(drawerData.title);
            return convertView;
        }

        if(drawerData.type == 1) {
            if(viewHolder == null || viewHolder.type != 1) {
                convertView = inflater.inflate(R.layout.drawer_header, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.type = drawerData.type;
                viewHolder.title = (TextView) convertView.findViewById(R.id.navigation_drawer_list_header);
                convertView.setTag(viewHolder);
            }

            viewHolder.title.setText(drawerData.title);
            return convertView;
        }

        return convertView;
    }
}
