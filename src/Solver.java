import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Stack;

public class Solver {
	private Instance instance;
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
	
	private ArrayList<Sommet> varStack = new ArrayList<Sommet>();
	
	/*
	 * Liste des variables instanci�s car la pile n'est pas parcourable
	 * � l'avenir, faire une classe perso, pour etre utilis� en pile, mais en ayant acc�s � toute les variables dedans
	 */
	//private ArrayList<Sommet> varInstancie = new ArrayList<Sommet>();
	
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
					if(v.getMarked() == false && (v.getColor() == sourceColor || v.isColored() == false))//v.getColor() == 0
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
	
	private boolean satisfiesConstraints()
	{
		for(int i = 0; i < uniqueSourceColor.size(); i++)
		{
			if(!path(uniqueSourceColor.get(i)))
				return false;
		}
		return true;
	}
	
	private boolean satisfiesLocalBuild(Sommet s)
	{
		if(s == null)//Cas de la premi�re it�ration
		{
			return true;
		}
		
		if(validBuild(s) == false)
			return false;
		
		for(int i = 0; i < s.getVoisins().size(); i++)
		{
			if(validBuild(s.getVoisins().get(i)) == false)
				return false;
		}
		
		return true;
	}
	
	/* 
	 * D�tection d'un carr� ?
	 * 
	 * 1 1
	 * 1 1
	 * 
	 * Mes 2 voisins de meme couleur ont un autre voisin commun de meme couleur
	 */
	private boolean validBuild(Sommet s)
	{		
		if(s.getColor() > 0)//Si le sommet est � 0 les contraintes ne s'appliquent pas
		{
			int threshold;
			int free = 0;
			int sameColor = 0;
			
			//Distinction source et variables
			if(s.isSource())//Une source a maximum un voisin de sa couleur
				threshold = 1;
			else//Un sommet a maximum 2 voisins de sa couleur
				threshold = 2;
	
			for(int i = 0; i < s.getVoisins().size(); i++)
			{
				Sommet v = s.getVoisins().get(i);
				if(v.isColored())
				{
					if(s.getColor() == v.getColor())
						sameColor++;
				}
				else
					free++;
			}
			
			if(sameColor > threshold)
				return false;
			else if(sameColor + free < threshold)//on sait que : sameColor <= threshold
				return false;
		}
		
		return true;
	}

	private boolean isSolution()
	{
		for(int i = 1; i < colorLinked.length; i++)
		{
			if(colorLinked[i] == false)
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
	
	/*
	 * On regarde si la couleur color est termin�
	 */
	private void updateColorLinked(int color)
	{
		if(color == 0)
			return;
		
		Sommet source;
		for(int i = 0; i < uniqueSourceColor.size(); i++)
		{
			source = uniqueSourceColor.get(i);
			if(source.getColor() == color)
				colorLinked[color] = isLinked(source);
		}
	}

	/*
	 * Attention
	 */
	//Un sommet peut faire partie des 3 listes, on devrait donc remove des 3 listes � chaque fois
	private Sommet nextVar()
	{
		long start = System.nanoTime();
		Sommet s;
		Iterator<Sommet> ite;
		while(firstVar.size() > 0)
		{
			ite = firstVar.iterator();
			s = ite.next();
			if(s.isColored())//variable d�j� instanci�
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
			if(s.isColored())//variable d�j� instanci�
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
			if(s.isColored())//variable d�j� instanci�
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
		
		/*
		//dead code
		for(int i = 0; i < var.size(); i++)
		{
			if(var.get(i).getColor() == 0)
				return var.get(i);
		}
		*/	
		
		return null;
	}
	
	/*
	 * Lorsque l'on instancie un sommet, seul ce sommet et ses voisins voient leurs �tats changer ( ie : leurs nombres de voisins non instanci�s a chang� )
	 */
	private void addPriority(Sommet s)//s le dernier sommet instanci�
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
			if(v.isColored() == false)
				uncolored.add(v);
			else
				colored.add(v);
		}
		
		//On traite s
		// Note : Si colorLinked[color] est vrai alors on vient de finir le chemin de la couleur,
		// 		dans ce cas, les sommets voisins non instanci�s ne nous int�resse pas
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
		
		//On traite les voisins instanci�s de s
		for(int i = 0; i < colored.size(); i++)
		{
			v = colored.get(i);
			color = v.getColor();
			uncolored = new ArrayList<Sommet>();
			
			if(!colorLinked[color])//De meme, on traite seulement si la couleur n'est pas fini
			{
				for(int j = 0; j < v.getVoisins().size(); j++)//On r�cup�re les voisins non instanci�s de v
				{
					w = v.getVoisins().get(j);
					if(w.isColored() == false)
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
		int color = 0, lastColor = 0;
		boolean over = false;
		boolean REASSIGN = false;
		Stack<Sommet> stack = new Stack<Sommet>(); //Pile d'instanciation
		
		while(!over)
		{				
			if(!satisfiesConstraints() || !satisfiesLocalBuild(s))//Si les contraintes ne sont pas satisfaites
			{
				//System.out.println("Contrainte enfreinte");
				s = stack.pop(); //on ignore s = null car toute instance a une solution
				varStack.remove(varStack.size() - 1);
				lastColor = s.getColor();//On r�cup�re la couleur de la variable sur laquelle on �choue				
				color = s.nextColor(colorLinked);
				
				while(color < 0)
				{
					s.reset();
					updateColorLinked(lastColor);
					
					s = stack.pop();
					varStack.remove(varStack.size() - 1);
					lastColor = s.getColor();
					color = s.nextColor(colorLinked);
				}
				
				REASSIGN = true;
				
				/*
				if(color < 0)//Fin du domaine
				{					
					while(color < 0)//Backtracking
					{
						//System.out.print("Backtrack : ");
						s.reset();//Reset de la variable d�pil�
						nbBacktrack++;
						
						if(s.d.INIT == true)
						{
							System.out.println("O my GOD");
							System.exit(1);
						}
						
						updateColorLinked(lastColor);
						
						s = stack.pop();//On r�cup�re la prochaine variable d'instanciation	
						lastColor = s.getColor();
						
						color = s.nextColor(colorLinked);
					}
					
					REASSIGN = true;
				}
				else
				{
					//System.out.print("Reassignation : ");
					REASSIGN = true;
				}
				*/
				assert REASSIGN == true;
			}
			else if(isSolution())//FIN
			{
				over = true;
				continue;
			}
			
			if(REASSIGN == false)//On choisit la prochaine variable � instancier
			{
				s = nextVar();
				
				if(s == null)
				{
					System.out.println("s == null");
					showStack();
					instance.printSolution();
					System.exit(1);
				}
				
				if(s.d.INIT == true)//L'init devrait �tre fausse
				{
					System.out.println("What the heck?");
					System.out.println(s.getName());
					System.out.println(s.getColor());
					System.out.println("Colored ? " + s.isColored());
					System.out.println("Last var " + stack.peek().getName());
					s.d.showDebug();
					instance.printSolution();
					System.exit(1);
				}
				
				color = s.nextColor(colorLinked);
				
				/*
				 * Probl�me avec init2,
				 * Soit on essaie le backtrack, soit on laisse le domaine plein
				 */
				if(color == -1)
				{
					System.out.println(s.getName() + ", color :" + color);
					s.d.showDebug();
					instance.printSolution();
					System.exit(1);
				}

				nbAssignation++;
			}
			
			//System.out.println("Assignation de la variable " + s.getName() + " couleur : " + color);
			s.setColor(color);
			stack.push(s);
			
			varStack.add(s);
			
			updateColorLinked(color);
			
			if(REASSIGN)
			{
				REASSIGN = false;
				nbReassignation++;
				updateColorLinked(lastColor);
			}
			
			addPriority(s);
			//instance.printSolution();
		}
	}
	
	private void init()
	{
		//On s'assure que le graphe est g�n�r�
		instance.buildGraph();
		
		Sommet s;
		ArrayList<Sommet> sommets = instance.getSommets();
		
		//On s�pare les sommets sources des sommets variables
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
		colorLinked[0] = true;
		
		//On construit une liste o� l'on a juste une source pour chaque couleur
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
		System.out.println("Nombre de r�assignation : " + nbReassignation);
		System.out.println("Nombre de backtrack : " + nbBacktrack);
		System.out.println("Nombre de suppression d'une liste prioritaire : " + nbRemoval);
		System.out.println();
	}
	
	//M�thode pour lancer la r�solution de l'instance
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
		System.out.println("Solution trouv� en " + (duration / 1000000000.0) + " s");
		
		System.out.println("Nombre de variables : " + variables.size());
		System.out.println("Nombre de couleurs : " + nbColor);
		System.out.println("Nombre d'assignation : " + nbAssignation);
		System.out.println("Nombre de r�assignation : " + nbReassignation);
		System.out.println("Nombre de backtrack : " + nbBacktrack);
		System.out.println("Nombre de suppression d'une liste prioritaire : " + nbRemoval);
		System.out.println("Temps pass� dans la m�thode nextVar : " + (varPickupTime / 1000000000.0) + " s");
	}
	
	public void showStack()
	{
		System.out.println("Show stack");
		for(int i = 0; i < varStack.size(); i++)
		{
			Sommet s = varStack.get(i);
			System.out.println(s.getName() + " colored " + s.getColor() + " isColored ? " + s.isColored());
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		
		Instance instance = new Instance();
		instance.loadFromFile("instances/level16.txt");
		Solver solver = new Solver(instance);
		solver.start();
		
		System.exit(0);
		
		solver.init();
		Sommet s = solver.sources.get(0).getVoisins().get(0);
		s.setColor(2);
		
		instance.printSolution();		
		System.out.println(solver.validBuild(s));
		
		s.getVoisins().get(0).setColor(2);
		//s.getVoisins().get(1).setColor(c);
		s.getVoisins().get(2).setColor(2);

		instance.printSolution();		
		System.out.println(solver.validBuild(s));
	}

}
