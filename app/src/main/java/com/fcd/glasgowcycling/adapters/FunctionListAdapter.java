package com.fcd.glasgowcycling.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fcd.glasgowcycling.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chrissloey on 10/07/2014.
 */
public class FunctionListAdapter extends ArrayAdapter<FunctionItem> {

    private Context mContext;
    private List<FunctionItem> mValues;

    public FunctionListAdapter(Context context, List<FunctionItem> values) {
        super(context, R.layout.icon_cell, values);
        mContext = context;
        mValues = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FunctionItemHolder viewHolder;

        if (convertView==null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.icon_cell, parent, false);

            viewHolder = new FunctionItemHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (FunctionItemHolder) convertView.getTag();
        }

        FunctionItem functionItem = mValues.get(position);
        if(functionItem != null) {
            // get the TextView from the ViewHolder and then set the text (item name) and tag (item ID) values
            viewHolder.text.setText(functionItem.getText());

            int iconResource = functionItem.getIcon();
            viewHolder.icon.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), iconResource));
        }

        return convertView;
    }

    static class FunctionItemHolder {
        ImageView icon;
        TextView text;
    }
}
