package com.devt.PlayerNet;
import android.os.*;
import android.support.v7.app.*;
import android.widget.*;
import java.io.*;

public class Downloads extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.downloads);
	
	File f = new File(Environment.getExternalStorageDirectory()+"/Android/data/com.devt.darkflix/downloads");
	if(f.isDirectory())
    Toast.makeText(this,"é um diretorio",Toast.LENGTH_SHORT).show();
	else
		Toast.makeText(this,"NAO É UM DIRETORIO",Toast.LENGTH_SHORT).show();
		
	}

}
