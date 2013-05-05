package com.example.test2;

import java.util.Vector;

import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

public class RunnableIA implements Runnable 
{
	private Handler handler;
	private Jeu jeu;
	private int etape;
	private Case case_visee;
	private TextView commandes;
	private IA ia;
	private boolean rejouer;
	private boolean coule;
	private Bateau bateau;
	private String texteRejouer;
	
	public RunnableIA(Handler h, Jeu j, TextView cmd, IA pIA)
	{
		this.handler = h;
		this.jeu = j;
		this.etape = 0;
		this.case_visee = null;
		this.commandes = cmd;
		this.ia = pIA;
		this.rejouer = false;
		this.coule = false;
		this.bateau = null;
		this.texteRejouer = "L'ordinateur va rejouer";
	}
	
	@Override
	public void run() 
	{
		if(this.case_visee.getUnBateau() && !this.case_visee.est_touche() && this.etape == 1) // Premier tir, touché
		{
			this.case_visee.setBackgroundResource(R.drawable.border_touche);
			this.commandes.setText("Touché !");
			
			this.case_visee.setTouche();
			this.rejouer = true;

			for(Bateau boat : this.jeu.getBateaux("Joueur")) // Augmenter le nombre de cases touchées sur ces coordonnees
			{
				if(boat.getBoatAt(this.case_visee.getCoordonnees()))
				{
					coule = boat.augmenterCasesTouchees();
					if(coule)
					{
						bateau = boat;
						this.etape = 3;
						this.rejouer = false;
					}
					break;
				}
			}
			
			if(!coule)
				this.etape++;

			this.handler.postDelayed(this, 1000);
		}
		else if(!this.case_visee.est_touche() && this.etape == 1) // Premier tir, raté
		{
			this.case_visee.setBackgroundResource(R.drawable.border_rate);
			this.commandes.setText("Raté !");
			this.case_visee.setTouche();
			this.rejouer = false;
			this.etape++;
			this.handler.postDelayed(this, 1000);
		}
		else if((this.etape == 2 && this.case_visee.est_touche()) && this.rejouer) // Touché on rejoue
		{
			if(!this.commandes.getText().equals(this.texteRejouer))
			{
				this.commandes.setText(this.texteRejouer);
				this.handler.postDelayed(this, 1000);
			}
			else
				this.ia.lancer();
		}
		else if(this.etape == 2) // Raté, au joueur de jouer
		{
			this.commandes.setText("A vous de jouer");
			this.jeu.changerTour();
		}
		else if(this.etape == 3) // Coulé
		{
			this.commandes.setText("Coulé !");
			for(Case caseCoord : bateau.getCoordonnees())
				caseCoord.setBackgroundResource(R.drawable.border_coule);
			
			// Déterminer si la partie est finie
			int nbBateauxCoules = 0;
			this.ia.supprimerCasePrecedente();
			this.ia.reinitialiserVariables();
			Vector<Bateau> boats =  this.jeu.getBateaux("Joueur");
			for(Bateau boat : boats)
			{
				if(boat.estCoule())
					nbBateauxCoules++;
			}
			if(nbBateauxCoules == boats.size()) // Perdu
				this.etape++;
			else
			{
				this.rejouer = true;
				this.etape = 2;
			}
			
			this.handler.postDelayed(this, 1000);
		}
		else if(this.etape == 3 && this.rejouer)
			this.ia.lancer();
		else if(this.etape == 4) // Fini
		{
			commandes.setText("Vous avez perdu");
			Toast.makeText(this.jeu, "Appuyez sur retour pour retourner au menu et recommencer", Toast.LENGTH_LONG).show();
			this.jeu.afficherBateau();
		}
	}

	public void setCase(Case c)
	{
		this.case_visee = c;
	}

	public void setEtape(int i)
	{
		this.etape = i;
	}
}
