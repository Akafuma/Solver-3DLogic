import java.util.ArrayList;
import java.util.Stack;

public class Solver {
	private Instance instance;
	//boolean[colors+1]
			//Sommet[colors+1] : on y stocke une source
			//Sommet[nbVar] : les variables sur lesquelles �num�re les possibilit�s
	
	public Solver(Instance inst)
	{
		instance = inst;
	}
	
	//Transform into path(Sommet source, Sommet cible)
	private boolean path(Sommet source)//DFS
	{
		if(source.isSource() == false || source == null)//Argument checking
		{
			System.out.println("Erreur fonction path, l'argument n'est pas une source\nexiting..");
			System.exit(1);
		}
		
		ArrayList<Sommet> sommets = instance.getSommets();
		ArrayList<Sommet> voisins;
		
		//Initialisation � faux du marquage
		for(int i = 0; i < sommets.size(); i++)
		{
			sommets.get(i).setMarked(false);
		}
		
		Stack<Sommet> stack = new Stack<Sommet>();
		stack.push(source);
		int sourceColor = source.getColor();
		Sommet s, v;
		
		while(!stack.empty())
		{
			s = stack.pop();
			if(s.getMarked() == false)
			{
				s.setMarked(true);
				
				//L'autre sommet source est accessible, on arr�te la recherche
				if(s.isSource() && s.getColor() == sourceColor && s != source)
					return true;
				
				voisins = s.getVoisins();
				for(int i = 0; i < voisins.size(); i++)
				{
					v = voisins.get(i);
					//On va ajouter les voisins si ils ne sont pas marqu�s et si ils ont la meme couleur que la couleur source ou si ils ne sont pas encore colori�s
					if(v.getMarked() == false && (v.getColor() == sourceColor || v.getColor() == 0))
						stack.push(v);
				}
				
			}
		}

		return false;
	}
	
	private boolean isLinked(Sommet source)//V�rifie si un chemin color� relie les sources
	{
		if(source.isSource() == false || source == null)//Argument checking
		{
			System.out.println("Erreur fonction isLinked, l'argument n'est pas une source\nexiting..");
			System.exit(1);
		}
		
		ArrayList<Sommet> sommets = instance.getSommets();
		ArrayList<Sommet> voisins;
		
		//Initialisation � faux du marquage
		for(int i = 0; i < sommets.size(); i++)
		{
			sommets.get(i).setMarked(false);
		}
		
		Stack<Sommet> stack = new Stack<Sommet>();
		stack.push(source);
		int sourceColor = source.getColor();
		Sommet s, v;
		
		while(!stack.empty())
		{
			s = stack.pop();
			if(s.getMarked() == false)
			{
				s.setMarked(true);
				
				//L'autre sommet source est accessible, on arr�te la recherche
				if(s.isSource() && s.getColor() == sourceColor && s != source)
					return true;
				
				voisins = s.getVoisins();
				for(int i = 0; i < voisins.size(); i++)
				{
					v = voisins.get(i);
					//On va ajouter les voisins si ils ne sont pas marqu�s et si ils ont la meme couleur que la couleur source
					if(v.getMarked() == false && v.getColor() == sourceColor)
						stack.push(v);
				}
				
			}
		}

		return false;
	}
	
	public void solve()
	{
		//On r�cup�re les variables, les domaines
		//on commence l'�num�ration
		
		/*
		 * 	On assigne une couleur � une variable, on applique la m�thode path sur chaque source/couleur si un seul renvoie faux : on assigne une autre couleur
		 * 																								 sinon on passe � la variable suivante
		 * 					Dans le cas o� la variable a �t� colori� de toute les couleurs et le test path �choue : on backtrack
		 */
	}
	
	public void init()
	{
		
	}
	
	//M�thode pour lancer la r�solution de l'instance
	public void start()//
	{
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Instance i = new Instance();
		i.loadFromFile("instances/level1.txt");
		i.buildGraph();
		
		Solver solver = new Solver(i);
		System.out.println(solver.isLinked(i.left[2][1]));
		System.out.println("On colorie l1,1 en 3");
		i.left[1][1].setColor(3);
		System.out.println(solver.isLinked(i.left[2][1]));
		
	}

}
