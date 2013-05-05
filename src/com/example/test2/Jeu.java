package com.example.test2;

import java.util.Vector;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Jeu extends Activity 
{
    private Vector<Case> cases_ordi;
    private Vector<Case> cases_joueur;
    private String difficulte;
	private Vector<Bateau> bateaux_ordi;
	private Vector<Bateau> bateaux_joueur;
	private boolean AU_TOUR_DE_LORDI;
	private Vector<TextView> labels;
	private IA ia;
	private Joueur joueur;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jeu);

		// Récupération de la difficulté
		Bundle b = getIntent().getExtras();
	    this.difficulte = b.getString("difficulty");
				
		TextView commandes = (TextView)findViewById(R.id.commandes);
		commandes.setText("Cliquez ici pour valider le placement de vos bateaux");
		
		RelativeLayout ordi = (RelativeLayout)findViewById(R.id.relative_ordi);
		GridView grid_ordi = (GridView)findViewById(R.id.grid_ordi);
		RelativeLayout joueur = (RelativeLayout)findViewById(R.id.relative_joueur);
		GridView grid_joueur = (GridView)findViewById(R.id.grid_joueur);

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;
		
		if(width < height) // Au cas où on prendrai la résolution native de tél
			width = height;		
		
		int newWidth = (width-80)/2;
		ordi.getLayoutParams().width = newWidth;
		ordi.getLayoutParams().height = newWidth+20; // Problème de padding
		joueur.getLayoutParams().width = newWidth;
		joueur.getLayoutParams().height = newWidth+20;

		// Création des cases
		CaseAdapter adapterOrdi = new CaseAdapter(this, newWidth, "ordinateur");
	    grid_ordi.setAdapter(adapterOrdi);
		CaseAdapter adapterJoueur = new CaseAdapter(this, newWidth, "joueur");
	    grid_joueur.setAdapter(adapterJoueur);

	    cases_ordi = adapterOrdi.getCases();
	   	cases_joueur = adapterJoueur.getCases();
	    
	    // Création des bateaux
	    this.bateaux_ordi = adapterOrdi.creer_bateaux();
	    this.bateaux_joueur = adapterJoueur.creer_bateaux();
	    
	    // Ajout de l'écouteur
		commandes.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v) 
				{
					((TextView)v).setOnClickListener(null);
					demarrerJeu();
				}
			}
		);
		
		// Déplacement des bateaux
		EcouteurBateaux ecouteurBateaux = new EcouteurBateaux(this);
		for(Case c : this.cases_joueur)
			c.setOnClickListener(ecouteurBateaux);
	}
	
	private void demarrerJeu()
	{
		// Enlever écouteurs bateaux
		for(Case c : this.cases_joueur)
			c.setOnClickListener(null);
		
		// Remettre les backgrounds des bateaux
		for(Bateau b : this.bateaux_joueur)
		{
			for(Case c : b.getCoordonnees())
				c.setBackgroundResource(b.getResid());
		}
		
		// Premier à jouer
		int nb_alea = (int)(Math.random()*2);
		nb_alea = 1;

		labels = new Vector<TextView>();
		labels.add((TextView)findViewById(R.id.text_joueur));
		labels.add((TextView)findViewById(R.id.text_ordi));
		
		if(nb_alea == 1)
			this.AU_TOUR_DE_LORDI = true;
		else
			this.AU_TOUR_DE_LORDI = false;

	    boolean estUnOrdinateur;
	    if(nb_alea == 1) // Ordi
	    	estUnOrdinateur = true;
	    else
	    	estUnOrdinateur = false;
	    
	    labels.get(nb_alea).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
	    
	    ia = null;
		joueur = new Joueur(this);
		if(difficulte.equalsIgnoreCase("Facile"))
			ia = new IA_Facile(this);
		else if(difficulte.equalsIgnoreCase("Difficile"))
			ia = new IA_Difficile(this, "Difficile");
		else if(difficulte.equalsIgnoreCase("Expert"))
			ia = new IA_Expert(this);

	    if(estUnOrdinateur) // Ordi
	    	ia.lancer();
	    else
	    	joueur.jouer();
	}

	public void changerTour()
	{
		this.AU_TOUR_DE_LORDI = !this.AU_TOUR_DE_LORDI;
		if(this.AU_TOUR_DE_LORDI) // Au tour de l'ordi
		{
			this.labels.get(1).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
			this.labels.get(0).setPaintFlags(Paint.LINEAR_TEXT_FLAG);
			
			this.ia.lancer();
		}
		else // Au tour du joueur
		{
			this.labels.get(0).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
			this.labels.get(1).setPaintFlags(Paint.LINEAR_TEXT_FLAG);

			this.joueur.jouer();
		}
	}
	
	public Vector<Case> getCases(String p)
	{
		if(p.equalsIgnoreCase("joueur"))
			return this.cases_joueur;
		else
			return this.cases_ordi;
	}
	
	public Vector<Bateau> getBateaux(String camp)
	{
		if(camp.equalsIgnoreCase("joueur"))
			return this.bateaux_joueur;
		else
			return this.bateaux_ordi;
	}
	
	public Vector<Object> getCasesAEntourer(String coordonnees)
	{
		Vector<Object> objets = new Vector<Object>();
		Vector<Case> lesCoordoonees = null;
		
		for(Bateau boat : this.bateaux_joueur)
		{
			if(boat.getBoatAt(coordonnees))
			{
				lesCoordoonees = boat.getCoordonnees();
				objets.add(boat);
				break;
			}
		}
		
		objets.add(lesCoordoonees);
		return objets;
	}
	
	public int getIndex(Case c)
	{
		return this.cases_joueur.indexOf(c);
	}
	
	public void verifierDeplacement(int vecteur, Bateau bateau)
	{
		/*
		 * On prend le bateau cliqué
		 * On vérifie que toutes les nouvelles cases ne sont pas occupées
		 * 		On modifie
		 */
		int compteur = 0;
		String orientation = "";
		Vector<Case> lesCases = bateau.getCoordonnees();
		
		if(!bateau.getOrientation())
			orientation = "Horizontal";	
		
		// Calcul du nouveau rang en se basant sur la première case
		int ligne = 0;
		if(this.cases_joueur.indexOf(lesCases.get(0)) + vecteur >= 0 && this.cases_joueur.indexOf(lesCases.get(0)) + vecteur <= 99)
		{
			Case uneNouvelleCase = this.cases_joueur.get(this.cases_joueur.indexOf(lesCases.get(0)) + vecteur);
			if(orientation.equalsIgnoreCase("Horizontal")) // Horizontal, trouver si la case est sur la même ligne
				ligne = (int)this.cases_joueur.indexOf(uneNouvelleCase)/10;
		}
		
		for(Case c : lesCases)
		{
			int nouvelIndex = this.cases_joueur.indexOf(c) + vecteur;
			if(nouvelIndex >= 0 && nouvelIndex <= 99)
			{
				if((this.cases_joueur.get(nouvelIndex).getUnBateau() && lesCases.indexOf(this.cases_joueur.get(nouvelIndex)) == -1) 
						|| ((int)this.cases_joueur.indexOf(this.cases_joueur.get(nouvelIndex))/10 != ligne) && orientation.equalsIgnoreCase("Horizontal"))
					compteur++;
			}
			else
				compteur++;
		}

		if(compteur == 0) // Nouveau placement ok
		{
			for(Case c : bateau.getCoordonnees())
			{
				c.enleverBateau();
				c.setBackgroundResource(R.drawable.border_mer);
			}
			
			// Mettre nouveau fond			
			Vector<Case> nouvellesCoordonnees = new Vector<Case>();

			for(Case c : bateau.getCoordonnees())
			{
				int nouvelIndex = this.cases_joueur.indexOf(c) + vecteur;
				Case nouvelleCase = this.cases_joueur.get(nouvelIndex);
				nouvelleCase.setBackgroundResource(bateau.getResid());
				nouvellesCoordonnees.add(nouvelleCase);
				nouvelleCase.setBateau();
			}
			
			bateau.setNewCoordonnees(nouvellesCoordonnees);
		}

		bateau.setClique(false);
		for(Case c : bateau.getCoordonnees())
			c.setBackgroundResource(bateau.getResid());
	}
	
	public void afficherBateau()
	{
		for(Bateau b : this.bateaux_ordi)
		{
			for(Case c : b.getCoordonnees())
			{
				if(!c.est_touche())
					c.setBackgroundResource(b.getResid());
			}
		}
	}
}
