package Brain;

public class Brain_Collection {
	public static Neural_Network net1;
	public static Neural_Network net2;
	
	public static void setup() {
		Neural_Network n1 = new Neural_Network();
		n1.setup();
		net1=n1;
		
		Neural_Network n2 = new Neural_Network();
		n2.setup();
		net2=n2;
	}
}
