package com.devt.PlayerNet;
import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.util.*;
import java.io.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import android.widget.*;

public class ServiceDownload
{

	String link,nome;
	Handler hand;
	Context context;

	public void setContext(Context context)
	{
		this.context = context;
	}

	public Context getContext()
	{
		return context;
	}

	public void setLink(String link)
	{
		this.link = link;
	}

	public String getLink()
	{
		return link;
	}

	public final void setNome(String nome)
	{
		this.nome = nome;
	}

	public final String getNome()
	{
		return nome;
	}

	
	public ServiceDownload(Context context,String link, String nome)
	{
		hand = new Handler();
		setLink(link);
		setNome(nome);
		setContext(context);
		Toast.makeText(context,"SEU DOWNLOAD VAI INICIAR",Toast.LENGTH_SHORT).show();
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					try
					{
						Document s = Jsoup.connect(getLink()).get();

						String url = s.getElementsByClass("geral").get(0).getElementsByAttribute("download").get(0).attr("href");
						setDownload(url);
					}
					catch (IOException e)
					{Log.e("GET EP", e.toString());}
				}

				private void setDownload(final String url)
				{
					hand.post(new Runnable(){

							@Override
							public void run()
							{
								Toast.makeText(getContext(),"DOWNLOAD INICIADO\nPor favor, não feche o aplicativo",Toast.LENGTH_SHORT).show();
								
								DownloadManager dm = (DownloadManager)getContext().getSystemService(getContext().DOWNLOAD_SERVICE);
							    DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));
								req.setDescription("PlayerNet está fazendo download");
								req.setTitle(getNome());
								
								req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
								req.setShowRunningNotification(true);
								File f = new File("Android/data/com.devt.darkflix/downloads");
								 
								if(!f.exists())
								Log.i("DIRETORIO",f.getAbsolutePath());
				
								if(getNome().startsWith(" ")){
									setNome(getNome().replaceFirst(" ",""));
								}  
								req.setDestinationUri(Uri.parse("Android/data/com.devt.darkflix/downloads/"+getNome()+".mp4"));
								//Log.i("DOWNLOAD",f.getAbsolutePath());
								dm.enqueue(req);
								 
								
							}
						});
				}
			}).start();
	}
}
