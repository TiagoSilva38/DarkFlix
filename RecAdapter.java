package com.devt.PlayerNet;
import android.content.*;
import android.widget.*;
import android.view.*;
import org.json.*;
import android.util.*;
import android.graphics.*;

public class RecAdapter extends ArrayAdapter<JSONObject>
{
	
	SharedPreferences shPlayer;
	String PROGRESS_PERCENT = "PROGRESSO%";
	public RecAdapter(Context c){
		
		super(c,R.layout.rec_adapter);
		shPlayer = c.getSharedPreferences("player",0);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		
		View view = null;
		
		view = LayoutInflater.from(getContext()).inflate(R.layout.rec_adapter,parent,false);
		
		JSONObject json = getItem(position);
		
		TextView titulo = (TextView)view.findViewById(R.id.rec_title);
		ProgressBar bar = (ProgressBar)view.findViewById(R.id.recadapterProgressVideo);
		bar.setMax(100);
		bar.getProgressDrawable().setColorFilter(Color.RED,android.graphics.PorterDuff.Mode.SRC_IN);
		
		try
		{
			titulo.setText(json.getString("title"));
		    view.setTag(json.getString("id"));
			int progress = shPlayer.getInt(PROGRESS_PERCENT+json.getString("id"),0);
			if(progress == 0){
				bar.setVisibility(View.GONE);
			}else{
				bar.setProgress(progress);
			}
		}
		catch (JSONException e)
		{
			Log.e("JSON ADAPTER",e.toString());
		}
		
		
		return view;
	}

	@Override
	public int getItemViewType(int position)
	{
		JSONObject json = getItem(position);
		int type = 1;
		try
		{
			 type = json.getInt("type");
		}
		catch (JSONException e)
		{Log.e("GET TYPE ADAPTER",e.toString());}
		return type;
	}
	
}
