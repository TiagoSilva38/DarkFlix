package com.devt.PlayerNet;
import android.content.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import org.json.*;


public class MainThread extends MainActivity
{
	Handler hand;
	Context context;
	public MainThread(Context context, Handler hand){
		
		this.context = context;
		this.hand = hand;
		
	}

	
	public void error(String error)
	{
		Toast.makeText(context,"NÃO FOI POSSIVEL CONECTAR, ERRO: "+error,Toast.LENGTH_SHORT).show();
		pd.dismiss();
	}

	
	
}
