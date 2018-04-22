package com.devt.PlayerNet;

import android.*;
import android.content.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.devt.PlayerNet.*;
import java.util.*;
import org.json.*;
import android.util.*;

public class AdapterEpisodio extends RecyclerView.Adapter<AdapterEpisodio.Holder>
{

	Context context;
	List<JSONObject> list;
	public AdapterEpisodio(Context context)
	{

		this.list = new ArrayList<>();
		this.context = context;
	}

	public void clear()
	{
		list.clear();
	}
	@Override
	public Holder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(context).inflate(R.layout.card_episodio, parent, false);

		return new Holder(view);
	}

	@Override
	public void onBindViewHolder(Holder holder, int position)
	{
		JSONObject ob = list.get(position);
		try
		{
			String nome = ob.getString("title");
			//String link = ob.getString("link");
			holder.nome.setText(nome);
		}
		catch (JSONException e)
		{Log.e("JSON BIND",e.toString());}

	}

	@Override
	public int getItemCount()
	{

		return list.size();
	}

	public void add(JSONObject ob)
	{

		list.add(ob);
	}

	class Holder extends RecyclerView.ViewHolder
	{

		TextView nome;

		public Holder(View view)
		{

			super(view);
			nome = (TextView)view.findViewById(R.id.card_episodio_title);

		}
	}
}
