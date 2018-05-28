public class Main {

	public static void main(String[] args) {
		
		if(args.length <= 0)
		{
			System.out.println("Erreur, besoin d'une instance en entrée");
			System.exit(0);
		}
		
		String file = args[0];
		
		Instance instance = new Instance();
		instance.loadFromFile(file);
		
		Solver solver = new Solver(instance);
		solver.start();		
	}

}
