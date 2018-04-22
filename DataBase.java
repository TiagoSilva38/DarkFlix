package com.devt.PlayerNet;
import android.content.*;

public class DataBase
{
	SharedPreferences sh;
	SharedPreferences.Editor edit;
	public DataBase(Context context,String banco){
		
		sh = context.getSharedPreferences(banco,0);
		edit = sh.edit();
	}
	
	public int getInt(String key){
		
		return sh.getInt(key,0);
	}
	
	public String getString(String key){
		
		return sh.getString(key,null);
	}
	
	public boolean getBoolean(String key){
		
		return sh.getBoolean(key,false);
	}
	
	public void setString(String key, String value){
		
		edit.putString(key,value);
		edit.commit();
	}
	
	public void setInt(String key, int value){

		edit.putInt(key,value);
		edit.commit();
	}
	
	public void setBoolean(String key, boolean value){

		edit.putBoolean(key,value);
		edit.commit();
	}
}
