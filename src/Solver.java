import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
	int shutdown = 1;
	private int nbAssignation = 0;
	private int nbReassignation = 0;
	private int nbBacktrack = 0;
	private int nbRemoval = 0;
	private long varPickupTime = 0;
	
	/*
	 * Liste des variables instanciés car la pile n'est pas parcourable
	 * à l'avenir, faire une classe perso, pour etre utilisé en pile, mais en ayant accès à toute les variables dedans
	 */
	//private ArrayList<Sommet> varInstancie = new ArrayList<Sommet>();
	
	//Note : optimiser les tailles de listes avec le constructeur
	//Utilisation d'une liste de variables prioritaire
	private LinkedHashSet<Sommet> firstVar;
	private LinkedHashSet<Sommet> secondVar;
	private LinkedHashSet<Sommet> thirdVar;

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

	//Un sommet peut faire partie des 3 listes, on devrait donc remove des 3 listes à chaque fois
	private Sommet nextVar(ArrayList<Sommet> var)
	{
		long start = System.nanoTime();
		Sommet s;
		Iterator<Sommet> ite;
		while(firstVar.size() > 0)
		{
			ite = firstVar.iterator();
			s = ite.next();
			if(s.getColor() > 0)//variable déjà instancié
			{
				firstVar.remove(s);
				nbRemoval++;
			}
			else
			{
				long end = System.nanoTime();
				varPickupTime += (end - start);
				firstVar.remove(s);
				return s;
			}
		}
		
		while(secondVar.size() > 0)
		{
			ite = secondVar.iterator();
			s = ite.next();
			if(s.getColor() > 0)//variable déjà instancié
			{
				secondVar.remove(s);
				nbRemoval++;
			}
			else
			{
				long end = System.nanoTime();
				varPickupTime += (end - start);
				secondVar.remove(s);
				return s;
			}
		}
		
		while(thirdVar.size() > 0)
		{
			ite = thirdVar.iterator();
			s = ite.next();
			if(s.getColor() > 0)//variable déjà instancié
			{
				thirdVar.remove(s);
				nbRemoval++;
			}
			else
			{
				long end = System.nanoTime();
				varPickupTime += (end - start);
				thirdVar.remove(s);
				return s;				
			}
		}
		
		//dead code
		for(int i = 0; i < var.size(); i++)
		{
			if(var.get(i).getColor() == 0)
				return var.get(i);
		}
		
		return null;
	}
	
	/*
	 * Lorsque l'on instancie un sommet, seul ce sommet et ses voisins voient leurs états changer ( ie : leurs nombres de voisins non instanciés a changé )
	 */
	private void addPriority(Sommet s)//s le dernier sommet instancié
	{
		//DEBUG
		int color = s.getColor();
		if(color == -1)
		{
			System.out.println(s.getName());
			System.exit(1);
		}
		
		Sommet v, w;
		ArrayList<Sommet> uncolored = new ArrayList<Sommet>();
		ArrayList<Sommet> colored = new ArrayList<Sommet>();
		for(int i = 0; i < s.getVoisins().size(); i++)
		{
			v = s.getVoisins().get(i);
			if(v.getColor() == 0)
				uncolored.add(v);
			else
				colored.add(v);
		}
		
		//On traite s
		// Note : Si colorLinked[color] est vrai alors on vient de finir le chemin de la couleur,
		// 		dans ce cas, les sommets voisins non instanciés ne nous intéresse pas
		if(!colorLinked[color])
		{			
			if(uncolored.size() == 1)
			{
				v = uncolored.get(0);
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
		
		//On traite les voisins instanciés de s
		for(int i = 0; i < colored.size(); i++)
		{
			v = colored.get(i);
			color = v.getColor();
			uncolored = new ArrayList<Sommet>();
			
			if(!colorLinked[color])//De meme, on traite seulement si la couleur n'est pas fini
			{
				for(int j = 0; j < v.getVoisins().size(); j++)//On récupère les voisins non instanciés de v
				{
					w = v.getVoisins().get(j);
					if(w.getColor() == 0)
						uncolored.add(w);
				}
				
				//On ajoute dans les files
				if(uncolored.size() == 1)
				{
					w = uncolored.get(0);
					firstVar.add(w);
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
		}
		
	}

	private void solve() throws FileNotFoundException
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
				checkLinkedPaths();				
				color = s.nextColor(colorLinked);
				
				if(color < 0)//Fin du domaine
				{					
					while(color < 0)//Backtracking
					{
						//System.out.print("Backtrack : ");
						s.reset();//Reset de la variable dépilé
						nbBacktrack++;
						s = stack.pop();//On récupère la prochaine variable d'instanciation						
						checkLinkedPaths();
						
						color = s.nextColor(colorLinked);
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
			
			/*
			shutdown--;
			if(shutdown == 0)
				;//System.exit(1);
			*/
			
			checkLinkedPaths();//On regarde si on a terminé un chemin avec cette assignation
			addPriority(s);
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
			{
				sources.add(s);
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
			variables.get(i).setDomaine(new Domaine(nbColor));
		}
		
		firstVar = new LinkedHashSet<Sommet>(variables.size());
		secondVar = new LinkedHashSet<Sommet>(variables.size());
		thirdVar = new LinkedHashSet<Sommet>(variables.size());		
		
		for(int i = 0; i < sources.size(); i++)
		{
			s = sources.get(i);
			
			if(s.getVoisins().size() == 1)//switch into case 1: 2: 3: default: WTF
			{
				Sommet v = s.getVoisins().get(0);
				firstVar.add(v);
			}
			else if(s.getVoisins().size() == 2)
			{
				secondVar.addAll(s.getVoisins());
			}
			else if(s.getVoisins().size() == 3)
			{
				thirdVar.addAll(s.getVoisins());
			}
			else//
				;
		}
	}
	
	public void showBenchmark()
	{
		System.out.println("Nombre d'assignation : " + nbAssignation);
		System.out.println("Nombre de réassignation : " + nbReassignation);
		System.out.println("Nombre de backtrack : " + nbBacktrack);
		System.out.println("Nombre de suppression d'une liste prioritaire : " + nbRemoval);
		System.out.println();
	}
	
	//Méthode pour lancer la résolution de l'instance
	public void start() throws FileNotFoundException
	{
		long start, end, duration;
		System.out.println("init...");
		init();
		
		//System.exit(1);
		
		System.out.println("solving...");
		start = System.nanoTime();
		solve();
		end = System.nanoTime();
		
		instance.printSolution();
		//instance.print();
		
		duration = end - start;
		System.out.println("Solution trouvé en " + (duration / 1000000000.0) + " s");
		
		System.out.println("Nombre de variables : " + variables.size());
		System.out.println("Nombre de couleurs : " + nbColor);
		System.out.println("Nombre d'assignation : " + nbAssignation);
		System.out.println("Nombre de réassignation : " + nbReassignation);
		System.out.println("Nombre de backtrack : " + nbBacktrack);
		System.out.println("Nombre de suppression d'une liste prioritaire : " + nbRemoval);
		System.out.println("Temps passé dans la méthode nextVar : " + (varPickupTime / 1000000000.0) + " s");
	}

	public static void main(String[] args) throws FileNotFoundException {
		
		Instance instance = new Instance();
		instance.loadFromFile("instances/level1.txt");
		Solver solver = new Solver(instance);
		solver.start();
	}

}
