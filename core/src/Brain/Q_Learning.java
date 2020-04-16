package Brain;

import java.util.Random;

public class Q_Learning {
	Parameters parameters = new Parameters();
	class Parameters{
		double max_value = 0;
		double gamma = 0.99;
		double eps_start = 1;
		double eps_end = 0.01;
		double eps_decay = 0.0001;
		
		int target_update = 1000;
		int train = 500;
		double memory_size = 10000000;
		double lr = 0.0001;
		int batch_size = 512;
		final int iterations = 512;
	}
	boolean use_NN(int tick) {
		Random r = new Random();
		double rate = parameters.eps_end + (parameters.eps_start-parameters.eps_end)*Math.exp(-tick*parameters.eps_decay);
		double random = r.nextDouble();
		if(random>rate) {
			return true;
		}else {
			return false;
		}
	}
}
