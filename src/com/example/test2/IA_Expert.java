package com.example.test2;

import java.util.Vector;

public class IA_Expert extends IA_Difficile
{	
	public IA_Expert(Jeu j)
	{
		super(j, "Expert");
		this.bateauxEnAttente = new Vector<Case>();
	}
	
	public void lancer()
	{
		nettoyerTabBateaux();
		if(super.case_precedente == null && this.bateauxEnAttente.size() > 0) // Un bateau a été coulé
			this.case_precedente = this.bateauxEnAttente.get(0);
		
		super.lancer();

		if(this.case_visee != null && this.case_visee.getUnBateau() && !estPresenteDansUnBateau(this.case_visee))
			this.bateauxEnAttente.add(this.case_visee);
		else if(this.case_visee == null && this.case_precedente != null && this.case_precedente.getUnBateau() && !estPresenteDansUnBateau(this.case_precedente))
			this.bateauxEnAttente.add(this.case_precedente);
	}

	@Override
	protected boolean tirOk(String newCoordonnees)
	{
		for(Case c : super.tabCases)
		{
			/*
			 * Si la case correspond aux coordonnees entrés en paramètre
			 * 		Si la case est déjà touchée on relance ou si il n'y a pas de places pour un bateau !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			 * 		
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
					this.case_visee = c;					
					return true;
				}
			}
		}
		
		// S'il a parcouru toutes les cases et qu'aucune n'a correspondu
		return nouveauTirageNbAlea();
	}
	
	@Override
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

		if(!this.case_visee.est_touche() && case_meme_rang) // pas touchée et pas de bateau différent, on tir
			tirer(0); // 0 car il faut mettre un nombre
		else if(this.case_visee.est_touche() || !case_meme_rang) // Touchée, on change de direction et on relance
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
			
			try
			{
				tirerIntelligement();
			}
			catch(StackOverflowError e)
			{
				if(this.bateauxEnAttente.size() > 0)
				{
					this.reinitialiserVariables();
					this.case_precedente = this.bateauxEnAttente.get(0);
				}
				lancer();
			}
			
		}
	}
	
	private boolean estPresenteDansUnBateau(Case case_visee) 
	{
		Bateau boat = null;
		if(this.bateauxEnAttente.size() == 0)
			return false;
		
		for(Bateau b : super.tabBateaux) // On récupère le bateau visé
		{
			boolean trouve = false;
			boat = b;
			for(Case c1 : b.getCoordonnees())
			{
				if(c1.getCoordonnees().equalsIgnoreCase(case_visee.getCoordonnees()))
					trouve = true;
			}
			if(trouve)
				break;
		}
		if(boat == null)
			return false;
		
		for(Case c1 : boat.getCoordonnees()) // Pour chaque case du bateau visé on regarde si une case en attente est présente dans le bateau
		{
			for(Case c2 : this.bateauxEnAttente)
			{
				if(c1.getCoordonnees().equalsIgnoreCase(c2.getCoordonnees()))
					return true;
			}
		}
		return false;
	}
	
	public void nettoyerTabBateaux()
	{
		for(Bateau b : this.tabBateaux)
		{
			if(!b.estCoule())
				continue;
			for(Case c1 : b.getCoordonnees())
			{
				for(Case c2 : this.bateauxEnAttente)
				{
					if(c1.getCoordonnees().equalsIgnoreCase(c2.getCoordonnees()))
					{
						this.bateauxEnAttente.remove(this.bateauxEnAttente.indexOf(c2));
						return;
					}
				}
			}
		}
	}
}
