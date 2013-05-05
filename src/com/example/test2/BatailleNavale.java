package com.example.test2;


import com.example.test2.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

public class BatailleNavale extends Activity 
{
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView facile = (TextView)findViewById(R.id.facile);
        TextView difficile = (TextView)findViewById(R.id.difficile);
        TextView expert = (TextView)findViewById(R.id.expert);
        TextView bataille = (TextView)findViewById(R.id.bataille);
        TextView aide = (TextView)findViewById(R.id.aide);
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;

		if(width < height) // Au cas où on prendrai la résolution native du tél
			height = width;
		if(width < height)
			width = height;
		
		int tierHauteur = height/3;
        
        bataille.setHeight(tierHauteur);
        facile.setHeight(tierHauteur/3);
        difficile.setHeight(tierHauteur/3);
        expert.setHeight(tierHauteur/3);
        aide.setWidth(width-80);
        aide.setHeight(tierHauteur);
        
        EcouteurDifficulte ecouteur = new EcouteurDifficulte(this);
        facile.setOnClickListener(ecouteur);
        difficile.setOnClickListener(ecouteur);
        expert.setOnClickListener(ecouteur);
    }
}