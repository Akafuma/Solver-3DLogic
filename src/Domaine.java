import java.util.ArrayList;

public class Domaine {
	private int length;
	private int current;

	private ArrayList<Integer> domaine;
	private boolean INIT = false;
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
	
	public int nextColor()
	{
		if(current > length)
			return -1;
		
		return current++;
	}
	
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
					couleurVoisin[v.getColor()] = true;
			}
			
			//On assigne en priorité une couleur voisine
			for(int i = 1; i < length + 1; i++)
			{
				if(couleurVoisin[i])
					head.add(i);
				else
					tail.add(i);
			}
			
			head.addAll(tail);
			domaine = head;
			
			INIT = true;
		}
	}
	
	//current = 0
	public int nextColor(boolean[] state)
	{
		init(state);
		
		if(current < domaine.size())
			return domaine.get(current++);
		
		return -1;
	}
	
	//Add a special backtrack code ???
	//Ordre de préférence, en créant une liste lors du première appel, on ordonne en premier les couleurs voisines, puis les autres
	//boolean à utiliser pour savoir si la liste doit être utiliser, A RESET
	//Source de bug mais efficace sur 12 et 13
	/*
	public int nextColor(boolean[] state)
	{
		
		if(nextColor > 0)
		{
			int r = nextColor;
			nextColor = -1;//conserver car preuve de forcage, lors du backtrack vérif forcage si forcage alors reset plus BT
			return r;
		}
		
		
		boolean[] couleurVoisin = new boolean[length + 1];
		for(int i = 0; i < s.getVoisins().size(); i++)
		{
			Sommet v = s.getVoisins().get(i);
			if(v.getColor() > 0)
				couleurVoisin[v.getColor()] = true;
		}
		
		
		for(int i = current; i < domaine.length; i++)
		{
			if(state[i] == false && couleurVoisin[i] == true)//On veut, si la couleur n'est pas fini ET un voisin a cette couleur
			{
				current = i + 1;
				return i;
			}
		}
		
		return -1;
	}
	*/

	public void reset()
	{
		current = 0;
		INIT = false;
	}
}
