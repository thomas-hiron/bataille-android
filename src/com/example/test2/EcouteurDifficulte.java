package com.example.test2;


import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class EcouteurDifficulte implements OnClickListener 
{
	BatailleNavale bataille;
	private int etape;
	
	public EcouteurDifficulte(BatailleNavale b)
	{
		this.etape = 0;
		this.bataille = b;
	}
	
	@Override
	public void onClick(View v) 
	{
		if(this.etape == 0)
			etape0(((TextView)v).getText());
	}

	private void etape0(CharSequence difficulte)
	{
		Intent i = new Intent(this.bataille, Jeu.class);
		i.putExtra("difficulty", difficulte);
		this.bataille.startActivityForResult(i, 0);
	}
}