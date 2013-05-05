package com.example.test2;

import android.content.Context;
import android.widget.Button;

public class Case extends Button
{
	private String contenu;
	private boolean AJOUTER_ECOUTEUR;
	private String coordonnees;
	private boolean unBateau;
	private boolean est_touche;
	
	public Case(Context context, boolean pEcouteur, String pContenu) 
	{
		super(context);
		
		if(pEcouteur)
			this.setBackgroundResource(R.drawable.border_mer);
		else
			this.setBackgroundResource(R.drawable.border);
			
		this.contenu = pContenu;
		this.AJOUTER_ECOUTEUR = pEcouteur;
		this.coordonnees = "";
		this.unBateau = false;
		this.est_touche = false;
		
		// Ajout des coordonnées
		if(AJOUTER_ECOUTEUR)
			this.coordonnees = this.contenu;
		else
			this.setText(pContenu);
	}
	
	public String getCoordonnees()
	{
		return this.coordonnees;
	}
	
	public boolean getEcouteur()
	{
		return this.AJOUTER_ECOUTEUR;
	}
	
	public boolean getUnBateau()
	{
		return this.unBateau;
	}
	
	public void setBateau()
	{
		this.unBateau = true;
	}

	public boolean est_touche() 
	{
		return this.est_touche;
	}

	public void setTouche() 
	{
		this.est_touche = true;	
	}
	
	public void enleverBateau()
	{
		this.unBateau = false;
	}
}
