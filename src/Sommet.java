import java.util.ArrayList;

public class Sommet {
	private String name;
	private boolean marked = false;//DFS
	private boolean source;
	private int color;
	private boolean colored;
	private ArrayList<Sommet> voisins;
	private Domaine d;
	
	public Sommet(int c)
	{
		voisins = new ArrayList<Sommet>();
		color = c;
		if(c > 0)//Le sommet est une source
		{
			source = true;
			colored = true;
		}
		else//Le sommet est une case coloriable
		{
			source = false;
			colored = false;
		}		
	}	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setMarked(boolean b)
	{
		marked = b;
	}
	
	public boolean getMarked()
	{
		return marked;
	}
	
	public boolean isSource()
	{
		return source;
	}
	
	public int getColor()
	{
		return color;
	}
	
	public boolean isColored()
	{
		return colored;
	}
	
	public int nextColor(boolean[] state)
	{
		return d.nextColor(state);
	}

	public void setColor(int c)
	{
		if(source)//On ne colorie pas
		{
			System.out.println("Erreur coloriage d'un sommet source, exiting...");
			System.exit(1);
		}
		
		if(c < 0)
		{
			System.out.println("Illegal argument : Sommet " + getName() + " colorié en " + c);
			System.exit(1);
		}
		
		color = c;
		colored = true;
	}
	
	public void reset()
	{
		color = 0;
		colored = false;		
		d.reset();		
	}
	
	public void setDomaine(Domaine d)
	{
		this.d = d;
		d.setSommet(this);
	}
	
	public ArrayList<Sommet> getVoisins() {
		return voisins;
	}

	public void ajouteVoisin(Sommet v)
	{
		voisins.add(v);
	}
	
	public void printAdjacency()
	{
		System.out.print(getName() + " :");
		for(int i = 0; i < voisins.size(); i++)
		{
			System.out.print(" " + voisins.get(i).getName());
		}
		System.out.println();
	}
	
}
