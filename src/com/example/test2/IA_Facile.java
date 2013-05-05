package com.example.test2;

public class IA_Facile extends IA
{
	public IA_Facile(Jeu jeu)
	{
		super(jeu, "Facile");
	}
	
	public void lancer()
	{
		super.case_precedente = null; // Utile pour la difficulte difficile
		super.lancer();
	}
}
