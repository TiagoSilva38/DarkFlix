package com.devt.PlayerNet;
import android.content.*;
import android.widget.*;
import android.view.*;
import org.json.*;
import android.util.*;

public class ListViewAdapter extends ArrayAdapter<JSONObject>
{
	public ListViewAdapter(Context c)
	{

		super(c, R.layout.simple_listview);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_listview, parent, false);

		JSONObject ob = getItem(position);
		TextView text = (TextView)view.findViewById(R.id.simplelistview_title);
		TextView link = (TextView)view.findViewById(R.id.simplelistview_link);

		try
		{
			text.setText(ob.getString("tipo"));
			link.setText(ob.getString("linkEp"));
			Log.i("Tipo", ob.getString("tipo"));
			Log.i("Link", ob.getString("linkEp"));
		}
		catch (JSONException e)
		{Log.e("GETVIEW", e.toString());}


		return view;
	}


}
