package com.example.test2;

import java.util.Vector;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CaseAdapter extends BaseAdapter {
    private Context mContext;
    private int largeur;
    private Vector<Case> lesCases;
    private Vector<Case> toutesLesCases;
    private String label;
    private int[] backgrounds = {
    		R.drawable.border_grey1,
    		R.drawable.border_grey2,
    		R.drawable.border_grey3,
    		R.drawable.border_grey4,
    		R.drawable.border_grey5,
    	};
    
    public CaseAdapter(Context context, int w, String pLabel) 
    {
    	this.lesCases = new Vector<Case>();
    	this.toutesLesCases = new Vector<Case>();
        this.mContext = context;
        this.largeur = w;
        this.label = pLabel;
        
        Case c;
        boolean AJOUTER_ECOUTEUR = true;
		for(int i = 0 ; i < 11 ; i++)
		{
			for(int j = 0 ; j < 11 ; j++)
			{
				if(i == 0 && j == 0) // Case haut gauche => Vide
					c = new Case(mContext, !AJOUTER_ECOUTEUR, "");
				else if(i == 0 && j > 0) // Première ligne sans case haut gauche
					c = new Case(mContext, !AJOUTER_ECOUTEUR, Integer.toString(j-1));
				else if(j == 0 && i > 0) // Première colonne
					c = new Case(mContext, !AJOUTER_ECOUTEUR, Character.toString((char)(65+(i-1))));
				else // Toutes les autres cases
					c = new Case(mContext, AJOUTER_ECOUTEUR, Character.toString((char)(65+(i-1))) +  Integer.toString(j-1));
				
				if(c.getEcouteur())
					this.lesCases.add(c);
				this.toutesLesCases.add(c);
			}
		}
    }

    public View getView(int position, View convertView, ViewGroup parent) 
    {
    	Case c = this.toutesLesCases.get(position);
		c.setWidth(largeur/11);
		c.setHeight(largeur/11);
    	    	
    	return c;
    }

    public int getCount() 
    {
        return this.toutesLesCases.size();
    }

    public Case getItem(int position) 
    {
        return this.toutesLesCases.get(position);
    }

    public long getItemId(int position) 
    {
        return 0;
    }
    
    public Vector<Case> getCases()
    {
    	return this.lesCases;
    }
    
    // Création des bateaux
    public Vector<Bateau> creer_bateaux()
	{
		int[] tabTaille = {2,3,3,4,5};
		Vector<Bateau> tabBateaux = new Vector<Bateau>();
		
		// On créer les 5 bateaux
		for(int taille : tabTaille)
		{
			Bateau boat = new Bateau(taille);
			tabBateaux.add(boat);
		}
		
		// On les place
		placerBateaux(tabBateaux);
		
		return tabBateaux;
	}
	
	private void placerBateaux(Vector<Bateau> tabBateaux) 
	{
		for(Bateau boat : tabBateaux)
		{
			boat.setResid(this.backgrounds[tabBateaux.indexOf(boat)]);
			Vector<Case> cases = new Vector<Case>();
			while(cases.size() == 0)
				cases = placerUnBateau(boat);

			for(Case c : cases)
			{
				boat.setCoordonnees(c);
				c.setBateau();
				if(!this.label.equalsIgnoreCase("ordinateur")) // Afficher seulement les bateaux du joueur
					c.setBackgroundResource(this.backgrounds[tabBateaux.indexOf(boat)]);
			}
		}
	}

	private Vector<Case> placerUnBateau(Bateau boat)
	{
		int nb_cases = boat.getNbCases();
		Vector<Case> cases_a_retourner = new Vector<Case>();
		int indexCaseSuiv = Math.random() < 0.5 ? 1 : 10;

		for(int i = 0 ; i < nb_cases ; i++)
		{
			boat.setOrientation(indexCaseSuiv);
			String coordonnees = "";
			if(i == 0) // Premiere case
				coordonnees = this.lesCases.get((int)(Math.random()*100)).getCoordonnees();
			else // Cases suivantes
			{
				// Calcul même colonne ou ligne
				if(!memeColonneOuLigne(this.lesCases.indexOf(cases_a_retourner.get(cases_a_retourner.size()-1)), indexCaseSuiv))
					coordonnees = "";
				else
					coordonnees = this.lesCases.get(this.lesCases.indexOf(cases_a_retourner.get(cases_a_retourner.size()-1))+indexCaseSuiv).getCoordonnees();
			}
			
			Case case_a_ajouter = ilYAUnBateau(coordonnees);
			if(case_a_ajouter == null)
			{
				cases_a_retourner.removeAllElements();
				return cases_a_retourner;
			}
			
			// Il n'y a pas de bateau dans cette case
			cases_a_retourner.add(case_a_ajouter);
		}

		return cases_a_retourner;
	}

	private Case ilYAUnBateau(String pCoordonnees)
	{
		for(Case c : this.lesCases)
		{
			if(c.getUnBateau() && c.getCoordonnees().equalsIgnoreCase(pCoordonnees))
				return null;
			else if(c.getCoordonnees().equalsIgnoreCase(pCoordonnees))
				return c;
		}

		return null;
	}
	
	private boolean memeColonneOuLigne(int indexCasePrecedente, int vecteurOrientation)
	{
		if(indexCasePrecedente+vecteurOrientation >= 100)
			return false;
		
		boolean case_meme_rang = true;
		if(vecteurOrientation == 1) // Horizontal
		{
			if((int)indexCasePrecedente/10 != (int)(indexCasePrecedente+1)/10)
				case_meme_rang = false;
		}
		else // Vertical
		{
			if((int)indexCasePrecedente%10 != (int)(indexCasePrecedente+10)%10)
				case_meme_rang = false;
		}
		
		return case_meme_rang;
	}
}