import java.util.ArrayList;

public class Domaine {
	private int length;
	private int current;

	private ArrayList<Integer> domaine;
	 boolean INIT = false;
	private Sommet s;
	
	public Domaine(int l)
	{
		length = l;
		current = 0;
	}
	
	public void setSommet(Sommet s)
	{
		this.s = s;
	}
	
	/*
	 * On attribue prioritairement des couleurs voisines
	 */
	private void init(boolean[] state)
	{
		if(!INIT)
		{						
			//Calcul des couleurs voisines du sommet		
			ArrayList<Integer> head = new ArrayList<Integer>();
			ArrayList<Integer> tail = new ArrayList<Integer>();
			
			boolean[] couleurVoisin = new boolean[length + 1];
			for(int i = 0; i < s.getVoisins().size(); i++)
			{
				Sommet v = s.getVoisins().get(i);
				if(v.getColor() > 0 && !state[v.getColor()])
				{
					couleurVoisin[v.getColor()] = true;
				}
			}
			
			//On assigne en priorité une couleur voisine
			for(int i = 1; i < length + 1; i++)
			{
				if(couleurVoisin[i])
					head.add(i);
				else
				{
					if(!state[i])
						tail.add(i);
				}
			}
			
			head.addAll(tail);
			domaine = head;
			domaine.add(0);
			
			INIT = true;
		}
	}
	
	public int nextColor(boolean[] state)
	{
		init(state);
		
		if(current < domaine.size())
			return domaine.get(current++);
		
		return -1;
	}
	
	public void showDebug()
	{
		System.out.println("Domaine : ");
		for(int i = 0; i < domaine.size(); i++)
		{
			System.out.print(domaine.get(i) + " ");
		}
		System.out.println();
		System.out.println("current = " + current);
		System.out.println("INIT = " + INIT);
	}

	public void reset()
	{
		current = 0;
		INIT = false;
	}
}
