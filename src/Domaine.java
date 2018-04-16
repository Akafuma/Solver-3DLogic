
public class Domaine {
	private int length;
	private int current;
	boolean[] mark;
	
	public Domaine(int l)
	{
		length = l;
		current = 1;
	}
	
	public int nextColor()
	{
		if(current > length)
			return -1;
		
		return current++;
	}
	
	public void reset()
	{
		current = 1;
	}
	
	public static void main(String[] args) {
		Domaine d = new Domaine(4);
		for(int i = 0; i < 5; i++)
		System.out.println(d.nextColor());
	}
}
