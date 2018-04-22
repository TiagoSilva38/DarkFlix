package com.devt.PlayerNet;

import android.app.*;
import android.content.res.*;
import android.os.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import org.json.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.content.pm.*;
import android.app.AlertDialog;
import android.support.v4.view.*;
import android.content.*;


public class MainActivity extends AppCompatActivity 
{

	boolean homeLoaded = false;
	int ERROR_INTERNET = 1;
	int last = 0;
	ProgressDialog pd;
    HomeThread homeThread;
	Toolbar toolBar;
	DrawerLayout dl;
	List<JSONObject> listSeries;
	RecyclerView recRecom,recBest,recAll,rec_search;
	MyAdapter adpRecom,adpBest,adpAll;
	MyAdapterFilter adpSearch;
	Handler hand;
	ProgressBar progressLoadSeries;
	ActionBarDrawerToggle tg;
	MenuItem MenuItem;
	Boolean homeClick = false;
	ScrollView scrool;

	public void setHomeLoaded(boolean homeLoaded)
	{
		this.homeLoaded = homeLoaded;
	}

	public boolean isHomeLoaded()
	{
		return homeLoaded;
	}

	public void updateSearch()
	{
		hand.post(new Runnable(){

				@Override
				public void run()
				{
					if(BancoRam.lastSearch != null){
					adpSearch.getFilter().filter(BancoRam.lastSearch);
					}
				}
			});
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		listSeries = new ArrayList<>();

		dl = (DrawerLayout)findViewById(R.id.drawer_layout);
		toolBar = (Toolbar)findViewById(R.id.toolBar);
		setSupportActionBar(toolBar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		tg = new ActionBarDrawerToggle(this, dl, R.drawable.ic_drawerl, R.string.app_name){

			public void onDrawerClosed(View view)
			{

				supportInvalidateOptionsMenu();
			}

			public void onDrawerOpened(View view)
			{

				supportInvalidateOptionsMenu();
			}
		};

		dl.setDrawerListener(tg);

		
		//INICIALIZA LISTA ESQUERDA DRAWER LAYOUT

		String[] opts = {"Home","Series","Filmes","Animes","Downloads","Opções","Sobre"};
		ListView listOpts = (ListView)findViewById(R.id.left_drawer);
		listOpts.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, opts));

		//------------------------------------------------------------//

		scrool = (ScrollView)findViewById(R.id.mainScrollView);
		progressLoadSeries = (ProgressBar)findViewById(R.id.mainBarSeries);
		
		recRecom = (RecyclerView)findViewById(R.id.rec_reco);
		recBest = (RecyclerView)findViewById(R.id.rec_best);
		recAll = (RecyclerView)findViewById(R.id.rec_all);
		rec_search = (RecyclerView)findViewById(R.id.rec_search);

		LinearLayoutManager lm = new LinearLayoutManager(this);
		lm.setOrientation(LinearLayoutManager.HORIZONTAL);

		LinearLayoutManager lm1 = new LinearLayoutManager(this);
		lm1.setOrientation(LinearLayoutManager.HORIZONTAL);

		LinearLayoutManager lm2 = new LinearLayoutManager(this);
		lm2.setOrientation(LinearLayoutManager.HORIZONTAL);

		GridLayoutManager lm3 = new GridLayoutManager(this,2);
		lm3.setOrientation(LinearLayoutManager.VERTICAL);

		recRecom.setLayoutManager(lm);
		adpRecom = new MyAdapter(getApplicationContext());
		recRecom.setAdapter(adpRecom);


		adpBest = new MyAdapter(getApplicationContext());
		recBest.setAdapter(adpBest);
		recBest.setLayoutManager(lm1);

		adpAll = new MyAdapter(getApplicationContext());
		recAll.setAdapter(adpAll);
		recAll.setLayoutManager(lm2);

		adpSearch = new MyAdapterFilter(getApplicationContext());
		rec_search.setAdapter(adpSearch);
		rec_search.setLayoutManager(lm3);

		homeClick = true;
		
		setWellCome();
		
		//OPÇOES DA LISTA--------------------------------------------//

		listOpts.setOnItemClickListener(new AdapterView.OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> p1, View view, int id, long position)
				{
					switch (id)
					{

						case 0:

							scrool.setVisibility(View.VISIBLE);
							rec_search.setVisibility(View.GONE);
							if (last != 0 && isHomeLoaded() == false)
							{
								if (homeThread.isAlive())
								{
									homeThread.interrupt();
								}
								homeThread.start();
								setDialog();
								last = 0;
							}
							dl.closeDrawers();
							homeClick = true;
							return;

						case 1:
							scrool.setVisibility(View.GONE);
							rec_search.setVisibility(View.VISIBLE);
							homeClick = false;
							//if (last != 1)
							//{
							//	if (ts.isAlive())
							//	{
							//		ts.interrupt();
							//	}
							//	ts = new ThreadSeries(1);
							//	ts.start();
							//	setDialog();
								last = 1;
							//}
							dl.closeDrawers();
							return;

						case 2:
							rec_search.setVisibility(View.VISIBLE);
							scrool.setVisibility(View.GONE);
							return;

						case 3:
							scrool.setVisibility(View.GONE);
							rec_search.setVisibility(View.VISIBLE);
							return;

						case 4:
							Intent i = new Intent(getApplicationContext(),Downloads.class);
							startActivity(i);
							return;
					}
				}


			});

		//--------------------------------------------------------//

		hand = new Handler();
		homeThread = new HomeThread(this,hand,this);
		homeThread.start();
		setDialog();
		
	}

	private void setWellCome()
	{
		DataBase db = new DataBase(this,"WELLCOME");
		boolean isInstalled = db.getBoolean("wasInstalled");
		
		if(!isInstalled){
			AlertDialog alert = new AlertDialog.Builder(this)
			.setTitle("O QUE HÁ DE NOVO?")
			.setMessage("*Fixada a barra de pesquisa\n\n"+
			"*Agora você pode baixar episodios ao clicar e segurar sobre ele")
				.setPositiveButton("Entendi", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						
					}
				})
			.create();
			alert.show();
			db.setBoolean("wasInstalled",true);
		}
		
	}



	public void setDialog()
	{

		pd = new ProgressDialog(this);
		pd.setTitle("Carregando");
		pd.setCancelable(false);
		pd.show();
	}
	
	public void updateList(int tipo)
	{
		if(tipo == 0){

			adpRecom.notifyDataSetChanged();
			adpBest.notifyDataSetChanged();
			adpAll.notifyDataSetChanged();

		}else{

			adpSearch.notifyDataSetChanged();
		}
		pd.dismiss();
	}
	
	
	public void setRecycler(int mode,JSONObject json)
	{
		//Log.i("JSON",json.toString());
		switch (mode)
		{

			case 0:
				adpRecom.add(json);
				return;
			case 1:

				adpBest.add(json);
				return;
			case 2: 
				adpAll.add(json);
				return;
			case 3:
				adpSearch.add(json);
				return;
		}

	}
	
	public void updateProgress(){
		
		hand.post(new Runnable(){

				@Override
				public void run()
				{
					
					if(homeClick = false){
					
					if(BancoRam.filterSeriesLength == 0 && BancoRam.loadedSeries == false){

						progressLoadSeries.setVisibility(View.VISIBLE);
						rec_search.setVisibility(View.GONE);
						scrool.setVisibility(View.GONE);
					}else{
						progressLoadSeries.setVisibility(View.GONE);
						rec_search.setVisibility(View.VISIBLE);
						scrool.setVisibility(View.GONE);

					}
					
					}else{
						
						//progressLoadSeries.setVisibility(View.GONE);
						//rec_search.setVisibility(View.VISIBLE);
						//scrool.setVisibility(View.GONE);
						
					}
				}
			});
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
	{
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem = menu.findItem(R.id.action_search);
        final SearchView sv = (SearchView)MenuItem.getActionView();
        sv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		
		sv.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

				@Override
				public boolean onQueryTextSubmit(String text)
				{
					
					return false;
				}

				@Override
				public boolean onQueryTextChange(String text)
				{
					
					BancoRam.lastSearch = text;
				    
					if(text.length() == 0){
						
						if(homeClick){
						scrool.setVisibility(View.VISIBLE);
						rec_search.setVisibility(View.GONE);
						}
					}else{
						adpSearch.getFilter().filter(text);
						rec_search.setVisibility(View.VISIBLE);
						scrool.setVisibility(View.GONE);
					}
					
					updateProgress();
					
					return true;
				}
			});
		
		
		return true;
	}
	@Override
	protected void onPause()
	{
		super.onPause();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK && scrool.getVisibility() == View.GONE){
			rec_search.setVisibility(View.GONE);
			scrool.setVisibility(View.VISIBLE);
			return true;
		}else{
			
			return super.onKeyUp(keyCode, event);
		}
	}
	
	
}
