import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Stack;

public class Solver {
	private Instance instance;
	private int nbAssignation = 0;
	private int nbSquareDetected = 0;
	
	//Verif
	private long varPickupTime = 0;
	private int nbRemoval = 0;

	private ArrayList<Sommet> varStack = new ArrayList<Sommet>();
	
	//Listes de variables prioritaire
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
					if(v.getMarked() == false && (v.getColor() == sourceColor || v.isColored() == false))//v.getColor() == 0
						stack.push(v);
				}
				
			}
		}

		return false;
	}
	
	//Vérifie si un chemin coloré relie les sources
	private boolean isLinked(Sommet source)
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
	
	/*
	 * Vérifie la contrainte de connexité
	 */
	private boolean satisfiesConstraints()
	{
		for(int i = 0; i < uniqueSourceColor.size(); i++)
		{
			if(!path(uniqueSourceColor.get(i)))
				return false;
		}
		return true;
	}
	
	/*
	 * Vérifie la contrainte de construction d'une solution
	 */
	private boolean satisfiesLocalBuild(Sommet s)
	{
		if(s == null)//Cas de la première itération
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
		
		if(containsSquare(s))
			return false;
		
		return true;
	}
	
	/*
	 * On vérifie si s et ses voisins ne forment pas un carré de meme couleur
	 */
	private boolean containsSquare(Sommet s)
	{
		int color = s.getColor();
		if(color > 0  && s.isSource() == false)//Une source ne peut pas faire de carré
		{
			ArrayList<Sommet> sameColor = new ArrayList<Sommet>();
			
			for(int i = 0; i < s.getVoisins().size(); i++)
			{
				Sommet v = s.getVoisins().get(i);
				if(v.getColor() == color)
					sameColor.add(v);
			}
			
			if(sameColor.size() == 2)
			{
				Sommet w1 = null, w2 = null;
				Sommet v;
				v = sameColor.get(0);
				for(int i = 0; i < v.getVoisins().size(); i++)
				{
					Sommet w = v.getVoisins().get(i);
					if(w.getColor() == color && w != s)
					{
						w1 = w;
						break;
					}
				}
				
				v = sameColor.get(1);
				for(int i = 0; i < v.getVoisins().size(); i++)
				{
					Sommet w = v.getVoisins().get(i);
					if(w.getColor() == color && w != s)
					{
						w2 = w;
						break;
					}
				}
				
				if(w1 != null && w2 != null && w1 == w2)
				{
					nbSquareDetected++;
					return true;
				}
			}
		}

		return false;
	}
	
	/* 
	 * On vérifie si le sommet s satisfait les contraintes :
	 * 		- Son nombre de voisins de la meme couleur que lui est inférieur ou égale à 1/2
	 * 		- La somme des voisins libre et des voisins de la meme couleur que lui est supérieur ou égale à 1/2
	 */
	private boolean validBuild(Sommet s)
	{		
		if(s.getColor() > 0)//Si le sommet est à 0 les contraintes ne s'appliquent pas
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

	/*
	 * Vérifie si l'assignation est une solution
	 * Linéaire en nombre de couleur
	 */
	private boolean isSolution()
	{
		for(int i = 1; i < colorLinked.length; i++)
		{
			if(colorLinked[i] == false)
				return false;
		}
		
		return true;
	}
	
	/*
	 * On a empilé ou dépilé une variable de couleur color,
	 * on vérifie si cela change l'état du lien de la couleur
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
	 * Renvoie une variable à instancier 
	 */
	private Sommet nextVar()
	{
		long start = System.nanoTime();
		Sommet s;
		Iterator<Sommet> ite;
		while(firstVar.size() > 0)
		{
			ite = firstVar.iterator();
			s = ite.next();
			if(s.isColored())//variable déjà instancié
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
			if(s.isColored())//variable déjà instancié
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
			if(s.isColored())//variable déjà instancié
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
		
		//Back up 
		for(int i = 0; i < variables.size(); i++)
		{
			if(variables.get(i).isColored() == false)
				return variables.get(i);
		}		
		
		return null;
	}
	
	/*
	 * Lorsque l'on instancie un sommet, seul ce sommet et ses voisins voient leurs états changer ( ie : leurs nombres de voisins non instanciés a changé )
	 * On ajoute les voisins de ce sommet dans les listes de priorités
	 */
	private void addPriority(Sommet s)//s le dernier sommet instancié
	{
		int color = s.getColor();		
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

	private void solve()
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
				s = stack.pop(); //on ignore s = null car toute instance a une solution
				varStack.remove(varStack.size() - 1);
				lastColor = s.getColor();//On récupère la couleur de la variable sur laquelle on échoue				
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
			}
			else if(isSolution())//FIN
			{
				over = true;
				continue;
			}
			
			if(REASSIGN == false)//On choisit la prochaine variable à instancier
			{
				s = nextVar();				
				color = s.nextColor(colorLinked);
				nbAssignation++;
			}
			
			s.setColor(color);
			stack.push(s);			
			varStack.add(s);
			
			updateColorLinked(color);
			
			if(REASSIGN)
			{
				REASSIGN = false;
				nbAssignation++;
				updateColorLinked(lastColor);
			}
			
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
		colorLinked[0] = true;
		
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
		System.out.println("Nombre de variables : " + variables.size());
		System.out.println("Nombre de couleurs : " + nbColor);
		System.out.println("Nombre d'affectation : " + nbAssignation);
		System.out.println("Nombre de carrés détectés : " + nbSquareDetected);
		System.out.println();
	}
	
	//Méthode pour lancer la résolution de l'instance
	public void start()
	{
		long start, end, duration;
		System.out.println("init...");
		init();
		
		System.out.println("solving...");	
		
		start = System.nanoTime();
		solve();
		end = System.nanoTime();		
		
		duration = end - start;
		System.out.println("Solution trouvé en " + (duration / 1000000000.0) + " s");
		
		showBenchmark();
		instance.printSolution();		
	}
	
	public void showStack()
	{
		System.out.println("Show stack");
		for(int i = 0; i < varStack.size(); i++)
		{
			Sommet s = varStack.get(i);
			System.out.println(s.getName() + " colored " + s.getColor() + " isColored ? " + s.isColored());
		}
		
		System.out.println("Nombre de suppression d'une liste : " + nbRemoval);
		System.out.println("Temps passé dans la méthode nextVar : " + varPickupTime);
	}

	public static void main(String[] args)
	{		
		Instance instance = new Instance();
		instance.loadFromFile("instances/level27.txt");
		Solver solver = new Solver(instance);
		solver.start();
	}

}
