package com.example.test2;
import java.util.Vector;

import android.os.Handler;
import android.widget.TextView;

public class IA
{
	protected Case case_precedente;
	protected Bateau bateau; // Difficile uniquement
	protected Vector<Case> bateauxEnAttente; // Expert uniquement
	protected Vector<Case> tabCases;
	protected Vector<Bateau> tabBateaux;
	protected int niveauRouge;
	protected boolean REJOUER;
	protected Jeu jeu;
	protected Handler handler;
	protected RunnableIA action;
	protected boolean reinitialiser;
	private String difficulte;
	
	public IA(Jeu j, String pDiff)
	{
		this.REJOUER = true;
		this.case_precedente = null;
		this.handler = new Handler();
		this.niveauRouge = 240;
		this.jeu = j;
		this.difficulte = pDiff;
		this.reinitialiser = false;
		this.action = new RunnableIA(this.handler, this.jeu, (TextView)this.jeu.findViewById(R.id.commandes), this);

		this.tabCases = this.jeu.getCases("Joueur");
		this.tabBateaux = this.jeu.getBateaux("Joueur");
	}
	
	public void lancer()
	{
		if(this.case_precedente == null)
		{
			int case_alea;
			if(this.difficulte.equalsIgnoreCase("Expert")) // Expert
				case_alea = viser_intelligement();
			else
				case_alea = (int)(Math.random()*100);
			Case c = this.tabCases.get(case_alea);
			String coordonnees = c.getCoordonnees();
			TextView commandes = (TextView)this.jeu.findViewById(R.id.commandes);
			
//			if(!c.getUnBateau())
//			{
//				lancer();
//				return;
//			}
			
			if(!c.est_touche())
			{
				if(c.getUnBateau())
					this.case_precedente = c;

				commandes.setText("L'ordinateur tir en " + coordonnees);
				this.action.setEtape(1);
				this.action.setCase(c);
				this.handler.postDelayed(this.action, 1000);
			}
			else if(c.est_touche()) // Déjà touché, on relance
			{
				try {
					lancer();
				}
				catch(StackOverflowError error) {
					lancer();
				}
			}
		}
	}
	
	private int viser_intelligement()
	{
		int case_alea = (int)(Math.random()*100); // On pioche une case
		Case c = this.tabCases.get(case_alea);
		if(c.est_touche())
			return viser_intelligement();
		else
		{
			int height = 1, width = 1, index = this.tabCases.indexOf(c);
			height += nb_cases(index, "top");
			height += nb_cases(index, "bottom");
			width += nb_cases(index, "left");
			width += nb_cases(index, "right");
			
			int tailleMin = getMinSize();
			System.out.println("height : " + height + ", width : " + width + ", tailleMin : " + tailleMin);
			if(height >= tailleMin || width >= tailleMin)
				return case_alea;
			else
				return viser_intelligement();
		}
	}
	
	private int nb_cases(int index, String direction)
	{
		int dir, ligne = (int)index/10, compteur = 0;
		Case c = null;
		if(direction.equalsIgnoreCase("top"))
			dir = -10;
		else if(direction.equalsIgnoreCase("bottom"))
			dir = 10;
		else if(direction.equalsIgnoreCase("left"))
			dir = -1;
		else
			dir = 1;
		
		while(true)
		{
			try
			{
				c = this.tabCases.get(index+dir);
				index += dir;
				if((ligne != (int)index/10 && (dir == -1 || dir == 1)) || c.est_touche())
					return compteur;
				compteur++;
			}
			catch(ArrayIndexOutOfBoundsException error)
			{
				return compteur;
			}
		}
	}
	
	private int getMinSize()
	{
		int min = 0;
		for(Bateau b : this.tabBateaux)
		{
			if(!b.estCoule())
			{
				if(b.getNbCases() >= min);
					min = b.getNbCases();
			}
		}
		
		return min;
	}

	public void supprimerCasePrecedente() 
	{
		this.case_precedente = null;
	}

	public void reinitialiserVariables()
	{
		this.reinitialiser = true;		
	}
}
