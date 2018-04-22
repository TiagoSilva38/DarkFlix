package com.devt.PlayerNet;

import android.content.*;
import android.widget.*;
import com.squareup.picasso.*;
import java.util.*;
import org.json.*;

public class MyAdapterFilter extends MyAdapter
{
	ArrayList<JSONObject> mlistFilter;
	public MyAdapterFilter(Context context){
		
		super(context);
	}
	public Filter getFilter(){
		
		return new Filter(){

			@Override
			protected Filter.FilterResults performFiltering(CharSequence charseq)
			{
				String nome = charseq.toString();
				if(nome.isEmpty()){

					listFilter = list;
				}else{

					mlistFilter = new ArrayList<>();
					for(JSONObject ob : list){

						if(ob.toString().toLowerCase().contains(nome)){
							mlistFilter.add(ob);
						}
					}

					
					
						BancoRam.filterSeriesLength = mlistFilter.size();
					
				}

				FilterResults filter = new FilterResults();
				filter.values = mlistFilter;
				return filter;
			}

			@Override
			protected void publishResults(CharSequence p1, Filter.FilterResults filter)
			{
				listFilter = (ArrayList<JSONObject>)filter.values;
				notifyDataSetChanged();
			}
		};

	}
	@Override
	public void onBindViewHolder(Holder holder, int position)
	{
		JSONObject ob = listFilter.get(position);
		holder.info.setText(ob.toString());
		try
		{
			Picasso.with(context).cancelRequest(holder.img);
			Picasso.with(context).load(ob.getString("capa")).into(holder.img);

		}
		catch (JSONException e)
		{
			System.out.println("PICASSO ERROR: " + e.toString());
		}}

	@Override
	public int getItemCount()
	{

		return listFilter.size();
	}
	
}
