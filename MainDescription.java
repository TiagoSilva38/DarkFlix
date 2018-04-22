package com.devt.PlayerNet;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.squareup.picasso.*;
import java.io.*;
import java.util.*;
import org.json.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import android.support.v7.widget.Toolbar;

public class MainDescription extends AppCompatActivity
{

	boolean isFirst = true;
	boolean isFirstAudio = true;
	Spinner spinner,spnAudio;
	ArrayAdapter<JSONObject> adapter;
	String link,nome;
	int type,audio;
	TextView dis;
	Toolbar bar;
	SharedPreferences sh;
	SharedPreferences.Editor edit;
	
	ListView rec;
	Thread te,tt;
	ImageView imgCapa;
	TextView info_lancamento,info_audio,info_emissora,info_categoria,info_sinopse;
	String PREFIX = "http://www.thenightseries.net/p/blogger.php?";
	int lastTemp = 1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		sh = getSharedPreferences("serie", 0);
		
		edit = sh.edit();
		
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_description);

		spnAudio = (Spinner)findViewById(R.id.main_descriptionSpinnerAudio);
		spinner = (Spinner)findViewById(R.id.main_descriptionSpinner);
		
		info_lancamento = (TextView)findViewById(R.id.infoserieLancamento);
		info_audio = (TextView)findViewById(R.id.infoserieAudio);
		info_emissora = (TextView)findViewById(R.id.infoserieEmissora);
		info_categoria = (TextView)findViewById(R.id.infoserieCategorias);
		info_sinopse = (TextView)findViewById(R.id.infoserieSinopse);
		
		imgCapa = (ImageView)findViewById(R.id.main_description_capa);
		imgCapa.setScaleType(ImageView.ScaleType.FIT_XY);
		
		rec = (ListView)findViewById(R.id.rec);
		dis = (TextView)findViewById(R.id.disponibility);
		
		
		rec.setOnItemClickListener(new AdapterView.OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> p1, View view, int position, long id)
				{
					//Toast.makeText(getApplicationContext(),view.getTag().toString(),Toast.LENGTH_SHORT).show();
					Intent i = new Intent(MainDescription.this,Player.class);
					i.putExtra("link",view.getTag().toString());
					
					startActivity(i);
				}
			});
		rec.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

				@Override
				public boolean onItemLongClick(AdapterView<?> p1, View view, int p3, long p4)
				{
					TextView text = (TextView)view.findViewById(R.id.rec_title);
					
					String nome = text.getText().toString();
					new ServiceDownload(getApplicationContext(),view.getTag().toString(),nome);
					//Toast.makeText(getApplicationContext(),nome,Toast.LENGTH_SHORT).show();
					
					return true;
				}
			});
		//LinearLayoutManager lm2 = new LinearLayoutManager(this);
		//lm2.setOrientation(LinearLayoutManager.VERTICAL);
		//rec.setLayoutManager(lm2);

		adapter = new RecAdapter(this);
		rec.setAdapter(adapter);

		link = getIntent().getStringExtra("link");
		type = getIntent().getIntExtra("tipo", 0);
		nome = getIntent().getStringExtra("nome");
		audio = getIntent().getIntExtra("audio", 2);

		if (link != null)
		{
			edit.putString("link", link);
			edit.putInt("tipo", type);
			edit.putString("nome", nome);
			edit.putInt("audio", audio);
			edit.commit();
		}

		link = sh.getString("link", "");
		type = sh.getInt("tipo", 0);
		nome = sh.getString("nome", "");
		audio = sh.getInt("audio", 2);

		bar = (Toolbar)findViewById(R.id.toolBar);
		setSupportActionBar(bar);
		getSupportActionBar().setTitle(nome);


		//Toast.makeText(this,"AUDIO "+audio,Toast.LENGTH_SHORT).show();
		te = new ThreadEP(link);
		te.start();

		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> p1, View view, int po, long p4)
				{
					//Toast.makeText(getApplicationContext(), ((TextView)view).getText().toString(), Toast.LENGTH_SHORT).show();
					if(isFirst){
						isFirst = false;
						return;
						
					}else{
						adapter.clear();
					setTemporada(po+1,1);
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> p1)
				{
					return;
				}
			});
			
			
		spnAudio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> p1, View view, int po, long p4)
				{
					//Toast.makeText(getApplicationContext(), ((TextView)view).getText().toString(), Toast.LENGTH_SHORT).show();
					if(isFirstAudio){
						isFirstAudio = false;
						return;

					}else{
						adapter.clear();
						setTemporada(lastTemp,po+1);
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> p1)
				{
					return;
				}
			});
			
	}

	public void setTemporada(int temp, int aud)
	{
		lastTemp = temp;
		dis.setVisibility(View.GONE);
		rec.setVisibility(View.VISIBLE);
		Document doc = Jsoup.parse(sh.getString("HTMLSERIE", null));
		String capa = doc.getElementsByClass("serie-capa").get(0).getElementsByTag("img").get(0).attr("src");
		Picasso.with(this).load(capa).into(imgCapa);
		Elements temporadas = doc.getElementsByClass("tabs").get(0).getElementsByTag("li");
		TextView[] texts = {
			info_lancamento,
			info_emissora,
			info_audio,
			info_categoria
		};
		//INFORMAÇOES DA SERIE
		Elements infos = doc.getElementsByClass("serie-infos").get(0).getElementsByTag("li");
		for(int i = 1; i < infos.toArray().length; i++){
			
			texts[i-1].setText(infos.get(i).text().split(":")[1]);
		}
		
		String sinopse = doc.getElementsByClass("content clearfix").get(0).getElementsByTag("p").get(0).text();
		info_sinopse.setText(sinopse);
		
		
		Element dublado = doc.getElementsByClass("check_lista lista_personalizada").get((temp * 2) - 2);
		Element legendado = doc.getElementsByClass("check_lista lista_personalizada").get((temp * 2) - 1);

		Element tp = null;
		if (aud == 1)
		{
			tp = dublado;
			
		}
		else if (aud == 2)
		{

			tp = legendado;
		}

		List<String> list = new ArrayList<String>();

		for (Element e : temporadas)
		{

			list.add(e.text());

		}
		if(isFirst){
		setSpinnerAudio(audio-1);
		setSpinner(list);
		}
		
		int size = tp.getElementsByTag("li").toArray().length;
		
		if(size == 1){
			
			rec.setVisibility(View.GONE);
			dis.setVisibility(View.VISIBLE);
			return;
		}
		
		for (Element li : tp.getElementsByTag("li"))
		{

			
			Element a = li.getElementsByTag("a").get(0);
			String ti = a.attr("title");
			String titulo = ti.substring(ti.indexOf("-")+1);
			//String ep = a.text();
			String link = a.attr("href");
			String id = link.substring(link.indexOf("?")+1);

			JSONObject json = new JSONObject();

			try
			{
				json.put("title", titulo);
				json.put("id", PREFIX + id);
				json.put("type",1);
				//Log.i("JSON EP", json.toString());
			}
			catch (JSONException e)

			{Log.e("JSON EPISODIO READ", e.toString());}

			adapter.add(json);
			adapter.notifyDataSetChanged();
			
		}


	}

	private void setSpinnerAudio(int audio)
	{
		String[][] sp = {
			{"DUBLADO"},
			{"LEGENDADO"},
			{"DUBLADO","LEGENDADO"}
		};
		ArrayAdapter adp = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,sp[audio]);
	    adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnAudio.setAdapter(adp);
		
	}

	public void setSpinner(List<String> temp)
	{

		ArrayAdapter<String> ad = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item);
		ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(ad);
		for (String t : temp)
		{

			ad.add(t + " Temporada");
			ad.notifyDataSetChanged();
		}
}

		class ThreadEP extends Thread{

			String link;
			Document doc;
			public ThreadEP(String link)
			{

				this.link = link;

			}

			public void run()
			{


				try
				{
					doc = Jsoup.connect(link).get();
					edit.putString("HTMLSERIE", doc.html());
					edit.commit();
					toRun();
				}
				catch (IOException e)
				{Log.e("JSOUP EPISODIOS", e.toString());}

			}

			private void toRun()
			{
				runOnUiThread(new Runnable(){

						@Override
						public void run()
						{
							setTemporada(1, 1);
							te.interrupt();
						}
					});
			}
		}
}

