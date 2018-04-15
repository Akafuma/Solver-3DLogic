import java.util.ArrayList;
import java.util.Stack;

public class Solver {
	private Instance instance;
	//boolean[colors+1]
			//Sommet[colors+1] : on y stocke une source
			//Sommet[nbVar] : les variables sur lesquelles énumère les possibilités
	
	public Solver(Instance inst)
	{
		instance = inst;
	}
	
	public boolean path(Sommet source)//DFS
	{
		if(source.isSource() == false)//Argument checking
		{
			System.out.println("Erreur fonction path, l'argument n'est pas une source\nexiting..");
			System.exit(1);
		}
		
		ArrayList<Sommet> sommets = instance.getSommets();
		ArrayList<Sommet> voisins;
		
		//Initialisation à faux du marquage
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
				
				if(s.isSource() && s.getColor() == sourceColor && s != source)
					return true;
				
				voisins = s.getVoisins();
				for(int i = 0; i < voisins.size(); i++)
				{
					v = voisins.get(i);
					if(v.getMarked() == false && (v.getColor() == sourceColor || v.getColor() == 0))
						stack.push(v);
				}
				//On va ajouter les voisins si ils ont la meme couleur que la couleur source ou si ils ne sont pas coloriés et si ils ne sont pas marqués
			}
		}

		return false;
	}
	
	public boolean isLinked(Sommet source)//Vérifie si un chemin coloré relie les sources
	{
		return false;
	}
	
	public void solve()
	{
		
	}
	
	public void init()
	{
		
	}
	
	public void start()//Seule methode public
	{
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Instance i = new Instance();
		i.loadFromFile("instances/level1.txt");
		i.buildGraph();
		
		Solver solver = new Solver(i);
		System.out.println(solver.path(i.right[2][2]));
		
	}

}
