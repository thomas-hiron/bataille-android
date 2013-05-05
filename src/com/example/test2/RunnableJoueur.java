package com.example.test2;

import java.util.Vector;

import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

public class RunnableJoueur implements Runnable 
{
	private Jeu jeu;
	private Case case_visee;
	private Vector<Bateau> tabBateaux;
	private Bateau bateau;
	private int etape;
	private Handler handler;
	private Joueur joueur;
	private TextView commandes;
	
	public RunnableJoueur(Joueur pJ, Jeu j, Handler h, TextView pCom)
	{
		this.etape = 1;
		this.case_visee = null;
		this.jeu = j;
		this.tabBateaux = this.jeu.getBateaux("Ordinateur");
		this.bateau = null;
		this.handler = h;		
		this.joueur = pJ;
		this.commandes = pCom;
	}

	@Override
	public void run() 
	{
		boolean coule = false;
		if(this.etape == 0) // Relancer après avoir coulé un bateau
		{
			this.joueur.jouer();
			this.etape++;
		}
		else if(etape == 1)
		{
			if(case_visee.getUnBateau()) // Touché
			{			
				// Trouver le bateau touché
				for(Bateau boat : this.tabBateaux) // Augmenter le nombre de cases touchées sur ces coordonnees
				{
					if(boat.getBoatAt(case_visee.getCoordonnees()))
					{
						coule = boat.augmenterCasesTouchees();
						if(coule)
							bateau = boat;
						break;
					}
				}
				
				if(!coule)
					this.joueur.jouer();
				else
					this.etape++;
			}
			else // Raté
				this.jeu.changerTour();
		}
		if(etape == 2) // Coulé
		{
			this.commandes.setText("Coulé !");

			for(Case toutesCases : bateau.getCoordonnees())
				toutesCases.setBackgroundResource(R.drawable.border_coule);
			
			// Déterminer si la partie est finie
			int nbBateauxCoules = 0;
			for(Bateau boat : this.tabBateaux)
			{
				if(boat.estCoule())
					nbBateauxCoules++;
			}
			
			if(nbBateauxCoules == this.tabBateaux.size()) // Gagné
			{
				this.etape++;
				this.handler.postDelayed(this, 1000);
			}
			else
			{
				this.etape = 0; // Rejouer
				this.handler.postDelayed(this, 1000);
			}
		}
		else if(this.etape == 3)
		{
			this.commandes.setText("Vous avez gagné !");
			Toast.makeText(this.jeu, "Appuyez sur retour pour retourner au menu et recommencer", Toast.LENGTH_LONG).show();
		}
	}

	public void setCase(Case c)
	{
		this.case_visee = c;
	}
}
