package com.devt.PlayerNet;
import android.content.*;
import android.content.pm.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import org.jsoup.*;
import org.jsoup.nodes.*;

public class Player extends AppCompatActivity
{

	VideoView video;
	MediaController mc;
	int pro = 0;
	boolean error = false;
	String nowLink;
	ProgressBar bar;
	int duration = 0;
	String PROGRESS_PERCENT = "PROGRESSO%";
	String PROGRESS = "PROGRESS";
	String DUARATION = "DURATION";
	static String link;
	static int lastProgress;
	
	DataBase db;
	
	
	final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
	| View.SYSTEM_UI_FLAG_FULLSCREEN
	| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);

		db = new DataBase(this,"player");
		
		

		// This work only for android 4.4+
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{

			getWindow().getDecorView().setSystemUiVisibility(flags);

			// Code below is to handle presses of Volume up or Volume down.
			// Without this, after pressing volume buttons, the navigation bar will
			// show up and won't hide
			final View decorView = getWindow().getDecorView();
			decorView
				.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
				{

					@Override
					public void onSystemUiVisibilityChange(int visibility)
					{
						if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
						{
							decorView.setSystemUiVisibility(flags);
						}
					}
				});
		}

		video = (VideoView)findViewById(R.id.player_video);
		mc = new MediaController(this);
		video.setMediaController(mc);
		bar = (ProgressBar)findViewById(R.id.player_progress);


		link = getIntent().getStringExtra("link");
		lastProgress = db.getInt(PROGRESS + link);


		
		//Log.i("Link Player",link);	


		new Thread(){

			public void run()
			{

				try
				{
					Document s = Jsoup.connect(link).get();

					String url = s.getElementsByClass("geral").get(0).getElementsByAttribute("download").get(0).attr("href");
					setWeb(url);
				}
				catch (IOException e)
				{Log.e("GET EP", e.toString());}
			}

			private void setWeb(final String hq)
			{
				runOnUiThread(new Runnable(){

						@Override
						public void run()
						{
							nowLink = hq;
							video.setVideoURI(Uri.parse(hq));
							video.start();

						}
					});
			}
		}.start();
		
		video.setOnErrorListener(new MediaPlayer.OnErrorListener(){

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra)
				{
					pro = mp.getCurrentPosition();
					bar.setVisibility(View.VISIBLE);
					if (extra == MediaPlayer.MEDIA_ERROR_SERVER_DIED || extra == MediaPlayer.MEDIA_ERROR_MALFORMED || extra == MediaPlayer.MEDIA_ERROR_UNSUPPORTED)
					{
						finish();
					}
					else
					{
						error = true;
						video.setVideoURI(Uri.parse(nowLink));
						video.start();
						bar.setVisibility(View.VISIBLE);

					}
					return true;
				}
			});

		video.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){

				@Override
				public void onPrepared(MediaPlayer mp)
				{
					//int position = sh.getInt(PROGRESS+link,0);
					if (lastProgress != 0)
					{
						mp.seekTo(lastProgress);
					}
					bar.setVisibility(View.GONE);
					duration = mp.getDuration();
					hideBar();
				}
			});
			
			
		video.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){

				@Override
				public void onCompletion(MediaPlayer mp)
				{
					finish();
					
				}
			});
	}

	
	public void hideBar(){
		
		new Handler().postDelayed(new Runnable(){

				@Override
				public void run()
				{
					
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
					{
						getWindow().getDecorView().setSystemUiVisibility(flags);
					}

					
					
				}
			},1000);
		
	}
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{

		super.onWindowFocusChanged(hasFocus);


		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus)
		{
			getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
	}
	
	public void saveState(){
		if (duration != 0)
		{
			pro = video.getCurrentPosition();
			db.setInt(PROGRESS_PERCENT + link, (pro * 100) / duration);
			db.setInt(PROGRESS + link, pro);
		}
		
	}
	@Override
	protected void onResume()
	{
		if (nowLink != null)
		{
			video.setVideoURI(Uri.parse(nowLink));
			video.start();
			bar.setVisibility(View.VISIBLE);
			lastProgress = db.getInt(PROGRESS + link);
		}
		super.onResume();
	}


	@Override
	protected void onPause()
	{
		saveState();
		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		saveState();
		super.onDestroy();
	}

	@Override
	public void onBackPressed()
	{
		saveState();
		super.onBackPressed();
	}
	
	

	@Override
	protected void onStop()
	{
		saveState();
		super.onStop();
	}
	
	
}
