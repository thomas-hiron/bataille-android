package com.example.test2;

import java.util.Vector;

public class Bateau 
{
	private int nb_cases;
	private Vector<Case> coordonnees;
	private int cases_touchees;
	private boolean EST_CLIQUE;
	private boolean ORIENTATION_VERTICALE;
	private int resid;
	
	public Bateau(int pTaille)
	{
		this.nb_cases = pTaille;
		this.cases_touchees = 0;
		this.coordonnees = new Vector<Case>();
		this.EST_CLIQUE = false;
	}
	
	public void setCoordonnees(Case c)
	{
		this.coordonnees.add(c);
	}
	
	public int getNbCases()
	{
		return this.nb_cases;
	}
	
	public boolean getBoatAt(String pCoordonnees) 
	{
		boolean ok = false;
		for(Case c : this.coordonnees)
		{
			if(c.getCoordonnees().equalsIgnoreCase(pCoordonnees))
				ok = true;
		}
		
		return ok;	
	}
	
	public boolean augmenterCasesTouchees() 
	{
		this.cases_touchees++;
//		System.out.println(this.nb_cases + " " + this.cases_touchees);
		if(this.nb_cases == this.cases_touchees)
			return true;
		else
			return false;
	}

	public Vector<Case> getCoordonnees()
	{
		return this.coordonnees;
	}
	
	public boolean estCoule()
	{
		if(this.nb_cases == this.cases_touchees)
			return true;
		else
			return false;
	}

	public boolean appartientA(String coord)
	{
		for(Case c : this.coordonnees)
		{
			if(c.getCoordonnees().equalsIgnoreCase(coord))
				return true;
		}
		return false;
	}

	public boolean estClique() 
	{
		return this.EST_CLIQUE;
	}
	
	public void setClique(boolean b) 
	{
		this.EST_CLIQUE = b;
	}

	public boolean getOrientation() 
	{
		return this.ORIENTATION_VERTICALE;
	}

	public void setOrientation(int indexCaseSuiv) 
	{
		if(indexCaseSuiv == 1)
			this.ORIENTATION_VERTICALE = false;
		else
			this.ORIENTATION_VERTICALE = true;	
	}

	public void setNewCoordonnees(Vector<Case> nouvellesCoordonnees) 
	{
		this.coordonnees = nouvellesCoordonnees;
	}

	public void setResid(int i) 
	{
		this.resid = i;
	}

	public int getResid() 
	{
		return this.resid;
	}

	public Case getCaseTouchee() 
	{
		for(Case c : this.getCoordonnees())
		{
			if(c.est_touche())
				return c;
		}
		
		return null;
	}
}
