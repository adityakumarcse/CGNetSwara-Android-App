package com.MSRi.ivr;
 
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ArrayAdapterItem extends ArrayAdapter<ListItem> {

    Context mContext;
    int layoutResourceId;
    ListItem data[] = null;

    public ArrayAdapterItem(Context mContext, int layoutResourceId, ListItem[] data) {
        super(mContext, layoutResourceId, data); 
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) { 
    	Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();
		 
		View rowView = inflater.inflate(R.layout.playlist_item, null);
		
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		TextView date = (TextView) rowView.findViewById(R.id.date);
		TextView title = (TextView) rowView.findViewById(R.id.title);
		 
        // Object item based on the position
        ListItem objectItem = data[position];
        
        // Set fields
        date.setText(objectItem.fileName);
        
        return convertView; 
    } 
}