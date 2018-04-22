package com.devt.PlayerNet;
import android.content.*;
import android.support.v7.widget.*;
import android.view.*;
import java.util.*;
import org.json.*;
import android.widget.*;
import com.squareup.picasso.*;
import android.widget.Filter.*;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.Holder>
{

	Context context;
	List<JSONObject> list;
	List<JSONObject> listFilter;
	public MyAdapter(Context context)
	{

		this.list = new ArrayList<JSONObject>();
		this.listFilter = new ArrayList<JSONObject>();
		this.context = context;
	}
	
	@Override
	public Holder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);

		return new Holder(view);
	}

	@Override
	public void onBindViewHolder(Holder holder, int position)
	{
		JSONObject ob = list.get(position);
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

		return list.size();
	}

	public void add(JSONObject ob)
	{

		list.add(ob);
		listFilter.add(ob);
	}

	class Holder extends RecyclerView.ViewHolder
	{

		TextView info;
		ImageView img;

		public Holder(View view)
		{

			super(view);
			info = (TextView)view.findViewById(R.id.item_info);
			img = (ImageView)view.findViewById(R.id.item_img);


			view.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(View view)
					{
						Intent i = new Intent(view.getContext(), MainDescription.class);
					    TextView v = (TextView)view.findViewById(R.id.item_info);
						try
						{
							JSONObject json = new JSONObject(v.getText().toString());
							i.putExtra("link", json.getString("link"));
							i.putExtra("nome", json.getString("nome"));
							i.putExtra("tipo", json.getInt("type"));
							i.putExtra("audio",json.getInt("audio"));
							context.startActivity(i);
							//Toast.makeText(context,json.get("type").toString(),Toast.LENGTH_SHORT).show();
						}
						catch (JSONException e)
						{System.out.println(e.toString());}

					}
				});

		}
	}
}
