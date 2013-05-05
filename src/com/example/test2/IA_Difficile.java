package com.example.test2;

import java.util.Vector;

import android.widget.TextView;

public class IA_Difficile extends IA 
{
	protected Vector<Case> casesBateaux;
	protected Case case_visee;
	protected String orientation;
	protected String directionTir;
	
	public IA_Difficile(Jeu j, String pDiff)
	{
		super(j, pDiff);
		this.orientation = "";
		this.directionTir = "";
		this.casesBateaux = new Vector<Case>();
		this.case_visee = null;
	}
	
	public void lancer()
	{
		if(this.reinitialiser)
			reinitialiserVariables();
		
		if(super.case_precedente == null)
			super.lancer();
		else
		{
			if(super.bateau == null)
			{
				for(Bateau boat : super.tabBateaux) // Trouver le bateau touché
				{
					if(boat.getBoatAt(super.case_precedente.getCoordonnees()))
					{
						super.bateau = boat;
						break;
					}
				}				
			}
			
			int nb_alea = (int)(Math.random()*4)+1;
			/*
			 * 1 : Haut
			 * 2 : Droit
			 * 3 : Bas
			 * 4 : Gauche
			 */
			
			if(this.orientation.equals("")) // Bateau touché une seule fois
				trouverCaseAProximite(nb_alea);
			else // Tir en fonction de l'orientation
				tirerIntelligement();
		}
	}
	
	public void trouverCaseAProximite(int nb_alea)
	{
		int x=0, y=0;
		
		switch(nb_alea)
		{
			case 1 :
				x=0;
				y=1;
			break;
			case 2 :
				x=1;
				y=0;
			break;
			case 3 :
				x=0;
				y=-1;
			break;
			case 4 :
				x=-1;
				y=0;
			break;
			default :
				x=0;
				y=0;
		}
		
		String coordonnees = super.case_precedente.getCoordonnees();

		try
		{
			int chiffre = Integer.parseInt(coordonnees.substring(coordonnees.length()-1)) + x;
			int lettre = (int)coordonnees.substring(0, 1).charAt(0) + y;
			String newCoordonnees = (char)lettre + Integer.toString(chiffre);
			
			if(tirOk(newCoordonnees))
				tirer(x);
		}
		catch(StackOverflowError e)
		{
			this.reinitialiserVariables();
			lancer();
		}
	}
	
	protected boolean tirOk(String newCoordonnees)
	{
		for(Case c : super.tabCases)
		{
			/*
			 * Si la case correspond aux coordonnees entrés en paramètre
			 * 		Si la case est déjà touchée on relance
			 * 		Sinon
			 * 			S'il y a un bateau et qu'il correspond au bateau touché précédemment ou qu'il n'y a pas de bateau
			 * 			Sinon s'il y a un bateau qui ne correspond pas à celui touché précédemment, on relance 
			 */
			if(c.getCoordonnees().equalsIgnoreCase(newCoordonnees))
			{
				if(c.est_touche()) // On relance
					return nouveauTirageNbAlea();
				else // Case non touchée
				{
					if((c.getUnBateau() && super.bateau.appartientA(newCoordonnees)) || !c.getUnBateau())
					{
						this.case_visee = c;
						return true;
					}
					else if(c.getUnBateau() && !super.bateau.appartientA(newCoordonnees))
						return nouveauTirageNbAlea();
				}
			}
		}
		
		// S'il a parcouru toutes les cases et qu'aucune n'a correspondu
		return nouveauTirageNbAlea();
	}
	
	protected boolean nouveauTirageNbAlea()
	{
		int nb_alea = (int)(Math.random()*4)+1;
		trouverCaseAProximite(nb_alea);
		return false;
	}
	
	protected void tirer(int x)
	{		
		String coordonnees = this.case_visee.getCoordonnees();
		
		if(this.case_visee.getUnBateau() && this.casesBateaux.size() == 0) // Tir réussi, on renseigne l'orientation
			genererOrientation(x);
		
		TextView commandes = (TextView)this.jeu.findViewById(R.id.commandes);
		commandes.setText("L'ordinateur tir en " + coordonnees);
		super.action.setEtape(1);
		super.action.setCase(case_visee);
		super.handler.postDelayed(this.action, 1000);
	
		if(this.case_visee.getUnBateau())
			this.casesBateaux.add(this.case_visee);
	}

	protected void tirerIntelligement()
	{
		int nb_new_case = 0;
		if(this.casesBateaux.size() == 0)
		{
			lancer();
			return;
		}
		
		if(this.orientation.equalsIgnoreCase("Vertical")) // Tir vertical, on cherche les cases au dessus et au dessous
		{
			if(this.directionTir.equalsIgnoreCase("Haut"))
				nb_new_case = this.tabCases.indexOf(this.casesBateaux.get(this.casesBateaux.size()-1)) - 10;
			else
				nb_new_case = this.tabCases.indexOf(this.casesBateaux.get(this.casesBateaux.size()-1)) + 10;
		}
		else // Tir horizontal, on cherche les cases à gauche et à droite
		{
			if(this.directionTir.equalsIgnoreCase("Gauche"))
				nb_new_case = this.tabCases.indexOf(this.casesBateaux.get(this.casesBateaux.size()-1)) - 1;
			else
				nb_new_case = this.tabCases.indexOf(this.casesBateaux.get(this.casesBateaux.size()-1)) + 1;
		}
		
		if(nb_new_case < 0 || nb_new_case > 99) // Case hors limite, on envoie l'index de la première case touchée et on change directionTir dans tirerIntelligementInc()
			nb_new_case = this.tabCases.indexOf(this.case_precedente);

		tirerIntelligementInc(nb_new_case);
	}
	
	protected void tirerIntelligementInc(int nb_new_case)
	{
		// On regarde si la nouvelle case à été touchée
		this.case_visee = this.tabCases.get(nb_new_case);
		
		int index_case_precedente = this.tabCases.indexOf(this.case_precedente);
		int index_case_visee = this.tabCases.indexOf(this.case_visee);
		boolean case_meme_rang = true;
		
		// S'assure que la case visée est dans la même ligne/colonne que la première case touchée
		if(this.orientation.equalsIgnoreCase("Vertical"))
		{
			if((int)index_case_precedente%10 != (int)index_case_visee%10)
				case_meme_rang = false;
		}
		else // Horizontal, trouver si la case est sur la même ligne
		{
			if((int)index_case_precedente/10 != (int)index_case_visee/10)
				case_meme_rang = false;
		}

		boolean appartientAuBateau = super.bateau.appartientA(this.case_visee.getCoordonnees());
		if(!this.case_visee.est_touche() && case_meme_rang && (!this.case_visee.getUnBateau() || appartientAuBateau)) // pas touchée et pas de bateau différent, on tir
			tirer(0); // 0 car il faut mettre un nombre
		else if(this.case_visee.est_touche() || !case_meme_rang || !appartientAuBateau) // Touchée, on change de direction et on relance
		{
			if(this.directionTir.equalsIgnoreCase("Gauche"))
				this.directionTir = "Droite";
			else if(this.directionTir.equalsIgnoreCase("Droite"))
				this.directionTir = "Gauche";
			else if(this.directionTir.equalsIgnoreCase("Haut"))
				this.directionTir = "Bas";
			else if(this.directionTir.equalsIgnoreCase("Bas"))
				this.directionTir = "Haut";
			
			this.casesBateaux.add(super.case_precedente); // Empecher StackoverflowError
			tirerIntelligement();
		}
	}
	
	private void genererOrientation(int x)
	{
		if(x != 0)
		{
			this.orientation = "Horizontal";
			if(this.tabCases.indexOf(this.case_precedente) > this.tabCases.indexOf(this.case_visee))
				this.directionTir = "Gauche";
			else
				this.directionTir = "Droite";
		}
		else
		{
			this.orientation = "Vertical";
			if(this.tabCases.indexOf(this.case_precedente) > this.tabCases.indexOf(this.case_visee))
				this.directionTir = "Haut";
			else
				this.directionTir = "Bas";
		}
	}
	
	public void reinitialiserVariables()
	{
		this.casesBateaux.removeAllElements();
		this.case_visee = null;
		this.orientation = "";
		this.directionTir = "";
		this.reinitialiser = false;
		
		super.case_precedente = null;
		super.bateau = null; // Difficile uniquement
	}
}
