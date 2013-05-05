package com.example.test2;

import java.util.Vector;

import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class EcouteurCase implements OnClickListener
{
	private Vector<Case> grilleAdverse;
	private Jeu jeu;
	private TextView commandes;
	private Handler handler;
	private RunnableJoueur action;
	
	public EcouteurCase(Joueur pJ, Jeu j, Vector<Case> ga)
	{
		this.jeu = j;
		this.grilleAdverse = ga;
		this.jeu.getBateaux("Ordinateur");
		this.commandes = (TextView)this.jeu.findViewById(R.id.commandes);
		this.handler = new Handler();
		this.action = new RunnableJoueur(pJ, this.jeu, this.handler, commandes);
	}
	
	@Override
	public void onClick(View v) 
	{
		Case c = (Case)v;
		enleverEcouteur();
				
		if(c.getUnBateau()) // Touché
		{
			c.setBackgroundResource(R.drawable.border_touche);
			this.commandes.setText("Touché !");
			c.setTouche();
			
//			timer.schedule(new TaskJoueur(this.joueur, coule, bateau, tabCases, tabBateaux, grilleAdverse, touche, panelJeu, panelCommandes), 1000);
		}
		else // Raté
		{
			c.setBackgroundResource(R.drawable.border_rate); // Tir dans la mer
			this.commandes.setText("Raté !");
			c.setTouche();
//			timer.schedule(new TaskJoueur(this.joueur, coule, bateau, tabCases, tabBateaux, grilleAdverse, touche, panelJeu, panelCommandes), 1000);
		}
		
		this.action.setCase(c);
		this.handler.postDelayed(this.action, 1000);
	}
	
	private void enleverEcouteur()
	{
		for(Case c : this.grilleAdverse)
			c.setOnClickListener(null);
	}
}
