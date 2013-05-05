package com.example.test2;

import java.util.Vector;

import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;

public class EcouteurBateaux implements OnClickListener
{
	private Bateau bateau;
	private Case caseCliquee;
	private Jeu jeu;
	
	public EcouteurBateaux(Jeu j)
	{
		this.caseCliquee = null;
		this.bateau = null;
		this.jeu = j;
	}

	@Override
	public void onClick(View v) 
	{
		Case laCase = (Case)v;

		if(laCase.getUnBateau())
		{
			
			laCase.getCoordonnees();
			this.caseCliquee = laCase;
			Vector<Object> objets = this.jeu.getCasesAEntourer(laCase.getCoordonnees());
			this.bateau = (Bateau) objets.get(0);
			Vector<Case> lesCases = (Vector<Case>) objets.get(1);

			// Décliquer le bateau si l'on reclique dessus
			if(this.bateau.estClique())
			{
				for(Case c : bateau.getCoordonnees())
					c.setBackgroundResource(bateau.getResid());
				this.bateau.setClique(false);
				return;
			}
			else
			{
				for(Bateau b : this.jeu.getBateaux("Joueur"))
				{
					for(Case c : b.getCoordonnees())
						c.setBackgroundResource(b.getResid());
				}
				
				this.bateau.setClique(true);
			}
			
			for(Case c : lesCases)
				c.setBackgroundColor(Color.BLACK);
		}
		else if(this.bateau != null) // On déplace un bateau s'il a été cliqué
		{
			if(this.bateau.estClique()) // On déplace
			{
				// Calcul du vecteur
				int indexDepart = this.jeu.getIndex(this.caseCliquee);
				int indexArrivee = this.jeu.getIndex(laCase);
				
				int vecteur = indexArrivee - indexDepart;
				
				this.jeu.verifierDeplacement(vecteur, this.bateau);
			}
		}
	}
}
