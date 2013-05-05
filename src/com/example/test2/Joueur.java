package com.example.test2;

import java.util.Vector;

import android.widget.TextView;

public class Joueur 
{
	private Jeu jeu;

	private Vector<Case> grilleAdverse;
	private EcouteurCase ecouteurCase;
	
	public Joueur(Jeu j)
	{
		this.jeu = j;
		this.grilleAdverse = this.jeu.getCases("Ordinateur");
		this.ecouteurCase = new EcouteurCase(this, this.jeu, this.grilleAdverse);
	}
	
	public void jouer()
	{
		TextView commandes = (TextView)this.jeu.findViewById(R.id.commandes);
		commandes.setText("A vous de jouer");
		for(Case c : this.grilleAdverse)
		{
			if(!c.est_touche())
				c.setOnClickListener(this.ecouteurCase);
		}
	}
}
