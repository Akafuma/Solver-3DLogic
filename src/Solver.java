import java.util.ArrayList;
import java.util.Stack;

public class Solver {
	private Instance instance;
	
	private int nbAssignation = 0;
	private int nbReassignation = 0;
	
	private ArrayList<Sommet> variables = new ArrayList<Sommet>();
	private ArrayList<Sommet> sources = new ArrayList<Sommet>();
	private int nbColor;
	
	//boolean[colors+1]
			//Sommet[colors+1] : on y stocke une source
			//Sommet[nbVar] : les variables sur lesquelles énumère les possibilités
	
	public Solver(Instance inst)
	{
		instance = inst;
	}
	
	//Renvoie true si un chemin est possible pour la source, sinon false
	private boolean path(Sommet source)//DFS
	{
		if(source.isSource() == false || source == null)//Argument checking
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
				
				//L'autre sommet source est accessible, on arrête la recherche
				if(s.isSource() && s.getColor() == sourceColor && s != source)
					return true;
				
				voisins = s.getVoisins();
				for(int i = 0; i < voisins.size(); i++)
				{
					v = voisins.get(i);
					//On va ajouter les voisins si ils ne sont pas marqués et si ils ont la meme couleur que la couleur source ou si ils ne sont pas encore coloriés
					if(v.getMarked() == false && (v.getColor() == sourceColor || v.getColor() == 0))
						stack.push(v);
				}
				
			}
		}

		return false;
	}
	
	private boolean isLinked(Sommet source)//Vérifie si un chemin coloré relie les sources
	{
		if(source.isSource() == false || source == null)//Argument checking
		{
			System.out.println("Erreur fonction isLinked, l'argument n'est pas une source\nexiting..");
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
				
				//L'autre sommet source est accessible, on arrête la recherche
				if(s.isSource() && s.getColor() == sourceColor && s != source)
					return true;
				
				voisins = s.getVoisins();
				for(int i = 0; i < voisins.size(); i++)
				{
					v = voisins.get(i);
					//On va ajouter les voisins si ils ne sont pas marqués et si ils ont la meme couleur que la couleur source
					if(v.getMarked() == false && v.getColor() == sourceColor)
						stack.push(v);
				}
				
			}
		}

		return false;
	}
	
	private boolean satisfiesConstraints()
	{
		for(int i = 0; i < sources.size(); i++)
		{
			if(!path(sources.get(i)))
				return false;
		}
		return true;
	}

	private boolean isSolution()
	{
		for(int i = 0; i < sources.size(); i++)
		{
			if(!isLinked(sources.get(i)))//Si une source n'est pas relié : pas une solution
					return false;
		}
		return true;
	}

	private Sommet nextVar(ArrayList<Sommet> var)
	{
		for(int i = 0; i < var.size(); i++)
		{
			if(var.get(i).getColor() == 0)
				return var.get(i);
		}
		
		return null;
	}

	private void solve()
	{
		Sommet s = null;
		int color = 0;
		boolean over = false;
		boolean REASSIGN = false;
		Stack<Sommet> stack = new Stack<Sommet>(); //Pile d'instanciation
		
		while(!over)
		{	
			if(!satisfiesConstraints())//Si les contraintes ne sont pas satisfaites
			{
				System.out.println("Contrainte enfreinte");
				s = stack.pop(); //on ignore s = null car toute instance a une solution
				color = s.d.nextColor();
				
				if(color < 0)//Fin du domaine
				{
					//On peut sortir le while du if et supprimer le if/else car inutile
					while(color < 0)//Backtracking
					{
						System.out.print("Backtrack : ");
						s.d.reset();//On reset le domaine de la variable dépilé
						s.setColor(0);//On n'oublie pas de décolorier la variable dépilé
						
						s = stack.pop();//On récupère la prochaine variable d'instanciation
						color = s.d.nextColor();
					}
					
					REASSIGN = true;
				}
				else
				{
					System.out.print("Reassignation : ");
					REASSIGN = true;
				}
				//Reassignation
				//On set la couleur à 0 pour etre eligible
				//
				//BACKTRACK
			}
			else if(isSolution())//FIN
			{
				System.out.println("Solution trouvé");
				over = true;
				continue;
				//On affiche la solution
			}
			
			if(!REASSIGN)//On choisit la prochaine variable à instancier
			{
				s = nextVar(variables);
				color = s.d.nextColor();
				nbAssignation++;
			}
			else//On réassigne la variable
			{
				REASSIGN = false;
				nbReassignation++;
			}
			
			System.out.println("Assignation de la variable " + s.getName() + " couleur : " + color);
			s.setColor(color);
			stack.push(s);
		}
	}
	
	private void init()
	{
		//On s'assure que le graphe est généré
		instance.buildGraph();
		
		Sommet s;
		ArrayList<Sommet> sommets = instance.getSommets();
		
		//On sépare les sommets sources des sommets variables
		for(int i = 0; i < sommets.size(); i++)
		{
			s = sommets.get(i);
			if(s.isSource())
				sources.add(s);
			else
				variables.add(s);			
		}
		
		nbColor = sources.size() / 2;
		boolean[] mark = new boolean[nbColor + 1];
		
		//On a besoin que d'une seule source pour chaque couleur, on supprime celle en trop
		for(int i = 0; i < sources.size(); i++)
		{
			s = sources.get(i);
			if(!mark[s.getColor()])
			{
				mark[s.getColor()] = true;
				sources.remove(i);
			}
		}
		
		//On initialise le domaine des sommets variables
		for(int i = 0; i < variables.size(); i++)
		{
			variables.get(i).d = new Domaine(nbColor);
		}
	}
	
	//Méthode pour lancer la résolution de l'instance
	public void start()//
	{
		long start, end, duration;
		
		init();
		start = System.nanoTime();
		solve();
		end = System.nanoTime();
		
		instance.printSolution();
		
		duration = end - start;
		System.out.println("Solution trouvé en " + (duration / 1000000000.0) + " s");		
		
		System.out.println("Nombre d'assignation : " + nbAssignation);
		System.out.println("Nombre de réassignation : " + nbReassignation);
		System.out.println("Total : " + (nbAssignation + nbReassignation));
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Instance instance = new Instance();
		instance.loadFromFile("instances/level5.txt");
		//instance.print();
		Solver solver = new Solver(instance);
		solver.start();
	}

}
