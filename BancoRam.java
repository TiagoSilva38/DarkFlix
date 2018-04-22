package com.devt.PlayerNet;

public class BancoRam
{
	public static boolean loadedSeries = false;
	public static int filterSeriesLength = 0;

	public static CharSequence lastSearch = null;
	
	public static void setSeriesLoaded(boolean mode){
		
		loadedSeries = mode;
	}
	
	public static void setFilterSeriesResultLength(int i){
		
		filterSeriesLength = i;
	}
	
	public static void setLastSearch(String str){
		
		if(str.length() > 0){
			lastSearch = str;
		}else{
			lastSearch = null;
		}
		
	}
}
