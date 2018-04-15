import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Instance {
	private int size; //Doit etre donné dans le fichier inst
	private int nbColors;
	private int nbVariables;
	private ArrayList<Sommet> sommets = new ArrayList<Sommet>();
	//Sommet au lieu de int
	Sommet[][] top;
	Sommet[][] left;
	Sommet[][] right;
	
	public int getSize() {
		return size;
	}

	public int getNbColors() {
		return nbColors;
	}

	public int getNbVariables() {
		return nbVariables;
	}

	public ArrayList<Sommet> getSommets() {
		return sommets;
	}

	public void loadFromFile(String filename)
	{
		try
		{
			String line;
			Scanner scline;
			int value;
			Sommet s;
			boolean flagT = false, flagL = false, flagR = false;
			
			Scanner sc = new Scanner(new File(filename));
			line = sc.nextLine();
			
			scline = new Scanner(line);
			size = scline.nextInt();
			scline.close();
			
			top = new Sommet[size][size];			
			left = new Sommet[size][size];			
			right = new Sommet[size][size];			
			
			while(sc.hasNextLine())
			{
				line = sc.nextLine();
				
				if(line.compareTo("T :") == 0)
				{
					flagT = true;
					for(int i = 0; i < size; i++)
					{
						line = sc.nextLine();
						scline = new Scanner(line);
						for(int j = 0; j < size; j++)
						{
							value = scline.nextInt();
							if(value == 0) //Le sommet est coloriable
							{
								nbVariables++;
								s = new Sommet(false, 0);
							}
							else if(value > 0)//Le sommet est une source							
							{
								nbColors++;
								s = new Sommet(true, value);
							}
							else//Le sommet n'est une case noire : on l'oublie
								s = null;
							
							if(s != null)
							{
								s.setName("t"+i+","+j);
								sommets.add(s);
							}
							
							top[i][j] = s;
						}
						scline.close();
					}
				}
				else if(line.compareTo("L :") == 0)
				{
					flagL = true;
					for(int i = 0; i < size; i++)
					{
						line = sc.nextLine();
						scline = new Scanner(line);
						for(int j = 0; j < size; j++)
						{
							value = scline.nextInt();
							if(value == 0)
							{
								nbVariables++;
								s = new Sommet(false, 0);
							}
							else if(value > 0)
							{
								nbColors++;
								s = new Sommet(true, value);
							}
							else
								s = null;
							
							if(s != null)
							{
								s.setName("l"+i+","+j);
								sommets.add(s);
							}
							
							left[i][j] = s;
						}
						scline.close();
					}
				}
				else if(line.compareTo("R :") == 0)
				{
					flagR = true;
					for(int i = 0; i < size; i++)
					{
						line = sc.nextLine();
						scline = new Scanner(line);
						for(int j = 0; j < size; j++)
						{
							value = scline.nextInt();
							if(value == 0)
							{
								nbVariables++;
								s = new Sommet(false, 0);
							}
							else if(value > 0)
							{
								nbColors++;
								s = new Sommet(true, value);
							}
							else
								s = null;
							
							if(s != null)
							{
								s.setName("r"+i+","+j);
								sommets.add(s);
							}
							
							right[i][j] = s;
						}
						scline.close();
					}
				}
			}			
			sc.close();
			
			nbColors = nbColors / 2;
			
			if(!flagT || !flagL || !flagR)
			{
				System.out.println("Erreur dans l'écriture du fichier");
				System.exit(1);
			}
		}
		catch(FileNotFoundException e)
		{
			System.out.println(e);
			System.exit(1);
		}
		
	}
	
	private void linkVertices(Sommet s, Sommet v)
	{
		if(v != null)
		{
			if(!(s.isSource() && v.isSource()))//Si deux sommets sources sont adjacents, ils ne sont pas voisins
				s.ajouteVoisin(v);
		}
	}
	
	public void buildGraph()
	{
		linkTop();
		linkLeft();
		linkRight();
	}
	
	private void linkTop()
	{
		Sommet s, v;
		for(int r = 0; r < size; r++)
		{
			for(int c = 0; c < size; c++)
			{
				s = top[r][c];
				
				if(s == null)
					continue;
				
				if(r != 0)//Ajout de la case au dessus
				{
					v = top[r - 1][c];
					linkVertices(s, v);
				}
				
				if(c != 0)//Ajout de la case à gauche
				{
					v = top[r][c - 1];
					linkVertices(s, v);						
				}
				
				//Ajout de la case à droite
				if(c == size - 1)
				{
					v = right[0][size - r - 1];
					linkVertices(s, v);					
				}
				else
				{
					v = top[r][c + 1];
					linkVertices(s, v);
				}
				
				//Ajout de la case en dessous
				if(r == size - 1)
				{
					v = left[0][c];
					linkVertices(s, v);
				}
				else
				{
					v = top[r + 1][c];
					linkVertices(s, v);
				}
			}
		}
	}
	
	private void linkLeft()
	{
		Sommet s, v;
		for(int r = 0; r < size; r++)
		{
			for(int c = 0; c < size; c++)
			{
				s = left[r][c];
				
				if(s == null)
					continue;
				
				if(r != 0)//Ajout de la case au dessus
				{
					v = left[r - 1][c];
					linkVertices(s, v);
				}
				else//Ajout de ligne du bas de Top car r == 0
				{
					v = top[size - 1][c];
					linkVertices(s, v);
				}
				
				if(c != 0)//Ajout de la case à gauche
				{
					v = left[r][c - 1];
					linkVertices(s, v);						
				}
				
				//Ajout de la case à droite
				if(c == size - 1)//cas de la dernière colonne, on prends sur Right
				{
					v = right[r][0];
					linkVertices(s, v);					
				}
				else//Sinon on prends la case directement à droite
				{
					v = left[r][c + 1];
					linkVertices(s, v);
				}
				
				//Ajout de la case en dessous
				if(r != size - 1)
				{
					v = left[r + 1][c];
					linkVertices(s, v);
				}
			}
		}
	}
	
	private void linkRight()
	{
		Sommet s, v;
		for(int r = 0; r < size; r++)
		{
			for(int c = 0; c < size; c++)
			{
				s = right[r][c];
				
				if(s == null)
					continue;
				
				if(r != 0)//Ajout de la case au dessus
				{
					v = right[r - 1][c];
					linkVertices(s, v);
				}
				else//Cas r == 0, on prends chez top
				{
					v = top[size - c - 1][size - 1];
					linkVertices(s, v);
				}
				
				if(c != 0)//Ajout de la case à gauche
				{
					v = right[r][c - 1];
					linkVertices(s, v);						
				}
				else//cas c == 0, on prends chez left
				{
					v = left[r][size - 1];
					linkVertices(s, v);
				}
				
				//Ajout de la case à droite
				if(c != size - 1)
				{
					v = right[r][c + 1];
					linkVertices(s, v);					
				}
				
				//Ajout de la case en dessous
				if(r != size - 1)
				{
					v = right[r + 1][c];
					linkVertices(s, v);
				}
			}
		}
	}
	
	public void print()
	{
		System.out.println("Instance :\n size = " + size + "\n nbColors = " + nbColors + "\n nbVariables = " + nbVariables);
		System.out.println("Top :");
		for(int i = 0; i < size; i++)
		{
			for(int j = 0; j < size; j++)
			{
				if(top[i][j] != null)
					System.out.print(" " + top[i][j].getColor());
				else
					System.out.print(" null");
			}
			System.out.println();
		}
		
		System.out.println("Left :");
		for(int i = 0; i < size; i++)
		{
			for(int j = 0; j < size; j++)
			{
				if(left[i][j] != null)
					System.out.print(" " + left[i][j].getColor());
				else
					System.out.print(" null");
			}
			System.out.println();
		}
		
		System.out.println("Right :");
		for(int i = 0; i < size; i++)
		{
			for(int j = 0; j < size; j++)
			{
				if(right[i][j] != null)
					System.out.print(" " + right[i][j].getColor());
				else
					System.out.print(" null");
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args) {
		Instance i = new Instance();
		i.loadFromFile("instances/level1.txt");
		System.out.println("Loaded");
		i.print();
		i.buildGraph();
		
		for(int r = 0; r < i.getSize(); r++)
		{
			for(int j = 0; j < i.getSize(); j++)
			{
				Sommet s = i.top[r][j];
				if(s != null)
					s.printAdjacency();
			}
		}
		
		for(int r = 0; r < i.getSize(); r++)
		{
			for(int j = 0; j < i.getSize(); j++)
			{
				Sommet s = i.left[r][j];
				if(s != null)
					s.printAdjacency();
			}
		}
		
		for(int r = 0; r < i.getSize(); r++)
		{
			for(int j = 0; j < i.getSize(); j++)
			{
				Sommet s = i.right[r][j];
				if(s != null)
					s.printAdjacency();
			}
		}
		
		System.out.println("NbSommets : " + i.sommets.size());
	}
}
