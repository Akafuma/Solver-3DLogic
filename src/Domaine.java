public class Domaine {
	private int length;
	private int current;
	private int nextColor;
	private int[] domaine;
	
	public Domaine(int l)
	{
		length = l;
		domaine = new int[l + 1];
		
		for(int i = 0; i < domaine.length; i++)
		{
			domaine[i] = i;
		}
		current = 1;
		nextColor = -1;
	}
	
	public int nextColor()
	{
		if(current > length)
			return -1;
		
		return current++;
	}
	
	public void setNextColor(int c)
	{
		nextColor = c;
	}
	
	public int nextColor(boolean[] state)
	{
		
		if(nextColor > 0)
		{
			int r = nextColor;
			nextColor = -1;
			return r;
		}		
		
		for(int i = current; i < domaine.length; i++)
		{
			if(state[i] == false)
			{
				current++;
				return i;
			}
		}
		
		return -1;
	}
	
	public void reset()
	{
		current = 1;
	}
}
