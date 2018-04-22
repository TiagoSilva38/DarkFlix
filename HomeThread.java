package com.devt.PlayerNet;

import android.content.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import java.io.*;
import org.json.*;
import org.jsoup.*;
import org.jsoup.nodes.*;

public class HomeThread extends Thread
{

	MainThread main;
	MainActivity ma;
	Context context;
	Handler hand;
	int type;
	Document doc;
	
	public HomeThread(Context context, Handler hand, MainActivity ma){
		
		this.hand = hand;
		this.context = context;
		main = new MainThread(context,hand);
		this.ma = ma;
		
		
	}

	
	public void run()
	{

		
		if(type == 0){
			home();
			series();
		}else if(type == 1){

			series();
		}else if(type == 2){

			filmes();
		}
		else if(type == 3){

			animes();
		}
	}

	private void home()
	{


		try
		{

			//CONEXÃO COM SITE E LEITURA DAS SERIES ATUALIZADAS
			doc = Jsoup.connect("http://www.thenightseries.net/ultimas-atualizacoes").get();

			//Element sr = doc.getElementsByClass("container").get(1);
			for (Element el : doc.getElementsByClass("item"))
			{

				getSerie(el, 0, 0);

			}

			update(0);

			//-----------------------------------------------------//


			//LEITURA DE ANIMES PRINCIPAIS//
			doc = Jsoup.connect("http://www.thenightseries.net/category/animes-e-desenhos/").get();
			Element box = doc.getElementsByClass("listagem row").get(0);

			for (Element serie : box.getElementsByClass("item"))
			{

				getSerie(serie, 1, 0);

			}


			update(0);
			//-----------------------------------------------------//


			//MAIS VISTAS 
			doc = Jsoup.connect("http://www.thenightseries.net/category/series").get();
			box = doc.getElementsByClass("listagem row").get(0);

			for (Element serie : box.getElementsByClass("item"))
			{

				getSerie(serie, 2, 0);

			}

			update(0);
			//TODAS AS SERIES
			ma.setHomeLoaded(true);
			//--------------------------------------------------------//
		}
		catch (IOException e)
		{
			System.out.println("HTML ERROR: " + e.toString());
			main.error(e.getMessage());
		}
	}//FIM DO METODO HOME

	private void series(){

		try{
			for(int i = 1; i <= 39; i++){
				if(i == 1){
					doc = Jsoup.connect("http://www.thenightseries.net/category/series/").get();
				}else{
					doc = Jsoup.connect("http://www.thenightseries.net/category/series/page/"+i).get();
				}
				Element box = doc.getElementsByClass("listagem row").get(0);

				for (Element serie : box.getElementsByClass("item"))
				{

					getSerie(serie, 3, 0);

				}

				update(1);
				if(i < 39){
					BancoRam.loadedSeries = false;
				}else{
					BancoRam.loadedSeries = true;
				}
				
				if(BancoRam.lastSearch != null){
				ma.updateSearch();
				ma.updateProgress();
				}
			}

		}catch(IOException e){
			Log.e("METODO SERIES",e.toString());
			setError(e.getMessage());
		}

	}//FIM DO METODO SERIES

	private void setError(final String error)
	{
		main.error(error);
	}
	private void filmes(){


	}//FIM DO METODO FILMES

	private void animes(){


	}//FIM DO METODO ANIMES


	private void getSerie(Element item, int mode, int tipo)
	{
		String link = item.getElementsByTag("a").get(0).attr("href");
		String capa = item.getElementsByTag("img").get(0).attr("src");
		String nome = item.getElementsByClass("titulo").get(0).text();
		String text = item.getElementsByClass("tags rodape").get(0).text();
		int audio = 2;
		if(text.contains("DUB")&&!text.contains("LEG")){

			audio = 1;
		}else if(text.contains("LEG")&&!text.contains("DUB")){
			audio = 2;
		}else if(text.contains("/")){
			audio = 3;
		}

		JSONObject json = new JSONObject();
		try
		{
			json.put("audio",audio);
			json.put("link", link);
			json.put("capa", capa);
			json.put("nome", nome);
			json.put("type", tipo);
		}
		catch (JSONException e)
		{Log.e("JSON", e.toString());}

		//System.out.println(json.toString());

		////listSeries.add(json);
		ma.setRecycler(mode,json);
		
		
		}
	
		public void update(final int mode){
			
			hand.post(new Runnable(){

					@Override
					public void run()
					{
						ma.updateList(mode);
					}
				});
		}
}
