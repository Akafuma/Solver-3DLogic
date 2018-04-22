import java.util.ArrayList;
import java.util.Stack;

public class Solver {
	private Instance instance;
	/*
	 * Conserver les chemins finis, lors d'un backtrack les recalculer
	 * si un chemin est fini, sa couleur doit etre exclu du domaine des variables
	 */
	/* ???
	 * Classe assignation :
	 * Var
	 * color
	 * 
	 * method
	 * apply()
	 */
	private int nbAssignation = 0;
	private int nbReassignation = 0;
	private int nbRemoval = 0;
	private long varPickupTime = 0;
	
	/*
	 * Liste des variables instanciés car la pile n'est pas parcourable
	 * à l'avenir, faire une classe perso, pour etre utilisé en pile, mais en ayant accès à toute les variables dedans
	 */
	//private ArrayList<Sommet> varInstancie = new ArrayList<Sommet>();
	
	//Utilisation d'une liste de variables prioritaire
	private ArrayList<Sommet> firstVar = new ArrayList<Sommet>();
	private ArrayList<Sommet> secondVar = new ArrayList<Sommet>();
	private ArrayList<Sommet> thirdVar = new ArrayList<Sommet>();

	private ArrayList<Sommet> variables = new ArrayList<Sommet>();
	private ArrayList<Sommet> sources = new ArrayList<Sommet>();
	private ArrayList<Sommet> uniqueSourceColor = new ArrayList<Sommet>();
	
	private int nbColor;
	
	private boolean colorLinked[];//Taille nbColor+1, colorLinked[i] = true si il y a un chemin pour la couleur i, false sinon
	
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
		for(int i = 0; i < uniqueSourceColor.size(); i++)
		{
			if(!path(uniqueSourceColor.get(i)))
				return false;
		}
		return true;
	}

	private boolean isSolution()
	{
		for(int i = 0; i < uniqueSourceColor.size(); i++)
		{
			if(!isLinked(uniqueSourceColor.get(i)))//Si une source n'est pas relié : pas une solution
					return false;
		}
		return true;
	}
	
	private void checkLinkedPaths()
	{
		for(int i = 0; i < uniqueSourceColor.size(); i++)
		{
			if(isLinked(uniqueSourceColor.get(i)))
				colorLinked[uniqueSourceColor.get(i).getColor()] = true;
			else
				colorLinked[uniqueSourceColor.get(i).getColor()] = false;;
		}
	}

	private Sommet nextVar(ArrayList<Sommet> var)
	{
		long start = System.nanoTime();
		Sommet s;
		while(firstVar.size() > 0)
		{
			s = firstVar.get(0);
			if(s.getColor() > 0)//variable déjà instancié
			{
				firstVar.remove(0);
				nbRemoval++;
			}
			else
			{
				//System.out.print("From first ");
				long end = System.nanoTime();
				varPickupTime += (end - start);
				firstVar.remove(0);
				return s;
			}
		}
		
		while(secondVar.size() > 0)
		{
			s = secondVar.get(0);
			if(s.getColor() > 0)//variable déjà instancié
			{
				secondVar.remove(0);
				nbRemoval++;
			}
			else
			{
				//System.out.print("From second ");
				long end = System.nanoTime();
				varPickupTime += (end - start);
				secondVar.remove(0);
				return s;
			}
		}
		
		while(thirdVar.size() > 0)
		{
			s = thirdVar.get(0);
			if(s.getColor() > 0)//variable déjà instancié
			{
				thirdVar.remove(0);
				nbRemoval++;
			}
			else
			{
				//System.out.print("From third ");
				long end = System.nanoTime();
				varPickupTime += (end - start);
				thirdVar.remove(0);
				return s;				
			}
		}
		
		for(int i = 0; i < var.size(); i++)
		{
			if(var.get(i).getColor() == 0)
				return var.get(i);
		}
		
		return null;
	}
	
	//Rework this
	//Iterate sur sommet
	private void addPrio()//il faut itérer aussi sur les sources
	{
		ArrayList<Sommet> uncolored;
		for(int i = 0; i < variables.size(); i++)//Pour chaque variable instancié
		{
			Sommet s = variables.get(i);
			if(s.getColor() > 0)
			{
				uncolored = new ArrayList<Sommet>();
				Sommet v;
				for(int j = 0; j < s.getVoisins().size(); j++)//Pour chaque sommet voisin
				{
					v = s.getVoisins().get(j);					
					
					if(v.getColor() == 0)//si un voisin n'est pas colorié, on l'ajoute à la liste
						uncolored.add(v);
				}
				
				if(uncolored.size() == 1 && !colorLinked[s.getColor()])//isLinked
				{
					v = uncolored.get(0);
					v.setNextColor(s.getColor());
					firstVar.add(v);
				}
				else if(uncolored.size() == 2)
				{
					secondVar.addAll(uncolored);
				}
				else if(uncolored.size() == 3)
				{
					thirdVar.addAll(uncolored);
				}
			}
		}//ENDFOR
		
		//Pour chaque sources
		for(int i = 0; i < sources.size(); i++)
		{
			Sommet s = sources.get(i);
			uncolored = new ArrayList<Sommet>();
			Sommet v;
			for(int j = 0; j < s.getVoisins().size(); j++)//Pour chaque sommet voisin
			{
				v = s.getVoisins().get(j);					
				
				if(v.getColor() == 0)//si un voisin n'est pas colorié, on l'ajoute à la liste
					uncolored.add(v);
			}
			
			if(uncolored.size() == 1 && !colorLinked[s.getColor()])
			{
				v = uncolored.get(0);
				v.setNextColor(s.getColor());
				firstVar.add(v);
			}
		}
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
				//System.out.println("Contrainte enfreinte");
				s = stack.pop(); //on ignore s = null car toute instance a une solution
				color = s.d.nextColor(colorLinked);
				
				if(color < 0)//Fin du domaine
				{
					//On peut sortir le while du if et supprimer le if/else car inutile
					while(color < 0)//Backtracking
					{
						//System.out.print("Backtrack : ");
						s.d.reset();//On reset le domaine de la variable dépilé
						s.setColor(0);//On n'oublie pas de décolorier la variable dépilé
						checkLinkedPaths();//Le backtrack peut entrainer un unlink d'une couleur
						//On réajouterai s dans les variables
						
						s = stack.pop();//On récupère la prochaine variable d'instanciation
						color = s.d.nextColor(colorLinked);
					}
					
					REASSIGN = true;
				}
				else
				{
					//System.out.print("Reassignation : ");
					REASSIGN = true;
				}
			}
			else if(isSolution())//FIN
			{
				//System.out.println("Solution trouvé");
				over = true;
				continue;
			}
			
			if(!REASSIGN)//On choisit la prochaine variable à instancier
			{
				s = nextVar(variables);
				color = s.d.nextColor(colorLinked);

				nbAssignation++;
			}
			else//On réassigne la variable
			{
				REASSIGN = false;
				nbReassignation++;
			}
			
			//System.out.println("Assignation de la variable " + s.getName() + " couleur : " + color);
			s.setColor(color);
			stack.push(s);
			
			checkLinkedPaths();//On regarde si on a terminé un chemin avec cette assignation
			addPrio();//modification à faire
		}
	}
	
	private void init()
	{
		//On s'assure que le graphe est généré
		instance.buildGraph();
		
		Sommet s;
		ArrayList<Sommet> sommets = instance.getSommets();
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		
		//On sépare les sommets sources des sommets variables
		for(int i = 0; i < sommets.size(); i++)
		{
			s = sommets.get(i);
			if(s.isSource())
			{
				sources.add(s);
				if(s.getVoisins().size() == 1)//switch into case 1: 2: 3: default: WTF
				{
					firstVar.add(s.getVoisins().get(0));
					tmp.add(s.getColor());
				}
			}
			else
				variables.add(s);			
		}
		
		nbColor = sources.size() / 2;
		boolean[] mark = new boolean[nbColor + 1];
		colorLinked = new boolean[nbColor + 1];
		
		//On construit une liste où l'on a juste une source pour chaque couleur
		for(int i = 0; i < sources.size(); i++)
		{
			s = sources.get(i);
			if(!mark[s.getColor()])
			{
				mark[s.getColor()] = true;
				uniqueSourceColor.add(s);
			}
		}
		
		//On initialise le domaine des sommets variables
		for(int i = 0; i < variables.size(); i++)
		{
			variables.get(i).d = new Domaine(nbColor);
		}
		
		//Forçage de la couleur pour les variables ou il n'y a pas à choisir
		for(int i = 0; i < firstVar.size(); i++)
		{
			s = firstVar.get(i);
			s.setNextColor(tmp.get(i));
			System.out.println(s.getName() + " force color " + tmp.get(i));
		}
	}
	
	//Méthode pour lancer la résolution de l'instance
	public void start()//
	{
		long start, end, duration;
		System.out.println("init...");
		init();
		System.out.println("solving...");
		start = System.nanoTime();
		solve();
		end = System.nanoTime();
		
		//instance.printSolution();
		//instance.print();
		
		duration = end - start;
		System.out.println("Solution trouvé en " + (duration / 1000000000.0) + " s");
		
		System.out.println("Nombre de variables : " + variables.size());
		System.out.println("Nombre de couleurs : " + nbColor);
		System.out.println("Nombre d'assignation : " + nbAssignation);
		System.out.println("Nombre de réassignation : " + nbReassignation);
		System.out.println("Nombre de suppression d'une liste prioritaire : " + nbRemoval);
		System.out.println("Temps passé dans la méthode nextVar : " + (varPickupTime / 1000000000.0) + " s");
	}

	public static void main(String[] args) {
		
		Instance instance = new Instance();
		instance.loadFromFile("instances/level10.txt");
		Solver solver = new Solver(instance);
		solver.start();
	}

}
