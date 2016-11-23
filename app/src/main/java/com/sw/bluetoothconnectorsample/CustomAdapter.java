package com.sw.bluetoothconnectorsample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by loretta on 11/23/16.
 */

public class CustomAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<Beacon> mData;

    public CustomAdapter(Context context, Collection<Beacon> data) {
        mInflater = LayoutInflater.from(context);
        setData(data);
    }

    public void setData(Collection<Beacon> data){
        mData = (data == null) ? new ArrayList<Beacon>() : new ArrayList<>(data);
        Collections.sort(mData, new Comparator<Beacon>() {
            @Override
            public int compare(Beacon o1, Beacon o2) {
                if (o1.getDistance() > o2.getDistance())
                    return 1;
                else if (o1.getDistance() < o2.getDistance())
                    return -1;
                else
                    return 0;
            }
        });
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData == null || mData.size() <= position ? null : mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_beacon, null);
            holder = new Holder();
            holder.idTv = (TextView) convertView.findViewById(R.id.idtv);
            holder.distanceTv = (TextView) convertView.findViewById(R.id.distancetv);
            convertView.setTag(holder);
        } else
            holder = (Holder) convertView.getTag();
        holder.idTv.setText(mData.get(position).getId2() + "_" + mData.get(position).getId3());
        holder.distanceTv.setText(mData.get(position).getDistance()+"");
        return convertView;
    }

    class Holder {
        TextView idTv, distanceTv;
    }
}
