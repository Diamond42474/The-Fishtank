package Brain;

import java.util.ArrayList;
import java.util.Random;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer.Builder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class Neural_Network {
	public Parameters parameters = new Parameters();
	private MultiLayerNetwork net;
	private MultiLayerNetwork target_net;
	public Compute compute = new Compute();
	public Experience experience = new Experience();
	private  ArrayList<Brain.Experience> experiences = new ArrayList<Brain.Experience>();
	Q_Learning q_learning = new Q_Learning();
	public void update_target_net() {
		target_net=net;
	}
	public class Experience{
		void manage() {
			if(experiences.size()>q_learning.parameters.memory_size) {
				experiences.remove(0);
			}
		}
		public void add_experience(Brain.Experience exp) {
			experiences.add(exp);
			manage();
		}
	}
	
	public class Compute{
		public Training training = new Training();
		public class Training{
			double[] Bellman(Brain.Experience exp) {
				double[] net_out = calculate_net(exp.state);
				int target_out_max = get_action_target_net(exp.state_prime);
				double target_max = calculate_target_net(exp.state_prime)[target_out_max];
				double[] out = new double[parameters.outputs];
				for(int i=0;i<out.length;i++) {
					if(i==exp.action) {
						out[i]=(target_max*q_learning.parameters.gamma)+exp.reward;
					}else {
						out[i]=net_out[i];
					}
				}
				return out;
			}
			double Sigmoid_Converter(double val) {
				if(Math.abs(val)>q_learning.parameters.max_value) {
					q_learning.parameters.max_value=Math.abs(val);
				}
				return val/q_learning.parameters.max_value;
			}
			public void Train() {
				if(experiences.size()>q_learning.parameters.batch_size) {
					DataSet ds = DS_Maker();
					for(int i=0;i<q_learning.parameters.batch_size;i++) {
						net.fit(ds);
					}
				}
			}
			DataSet DS_Maker() {
				INDArray input = Nd4j.zeros(q_learning.parameters.batch_size, parameters.inputs);
				INDArray labels = Nd4j.zeros(q_learning.parameters.batch_size, parameters.outputs);
				ArrayList<Brain.Experience> pre_experiences = experiences;
				ArrayList<Brain.Experience> experiences_batch = new ArrayList<Brain.Experience>();
				Random r = new Random();
				for(int i=0;i<q_learning.parameters.batch_size;i++) {
					int index = r.nextInt(pre_experiences.size()-1);
					experiences_batch.add(pre_experiences.get(index));
					pre_experiences.remove(index);
				}
				
				for(int i=0;i<q_learning.parameters.batch_size;i++) {
					for(int ie=0;ie<parameters.inputs;ie++) {
						input.putScalar(new int[] {i,ie}, experiences_batch.get(i).state[ie]);
					}
					double[] bell_out = Bellman(experiences_batch.get(i));
					for(int ie=0;ie<parameters.outputs;ie++) {
						labels.putScalar(new int[] {i,ie}, bell_out[ie]);
					}
				}
				
				return new DataSet(input,labels);
			}
		}
		public double[] calculate_net(double[] input) {
			INDArray in = Nd4j.zeros(1, parameters.inputs);
			for(int i = 0;i<parameters.inputs;i++) {
				in.putScalar(new int[]{0, i},input[i]);
			}
			INDArray out = net.output(in);
			double[] out2 = new double[parameters.outputs];
			for(int i=0;i<parameters.outputs;i++) {
				out2[i]=out.getDouble(i);
			}
			//System.out.println(out);
			return out2;
		}
		public double[] calculate_target_net(double[] input) {
			INDArray in = Nd4j.zeros(1, parameters.inputs);
			for(int i = 0;i<parameters.inputs;i++) {
				in.putScalar(new int[]{0, i},input[i]);
			}
			INDArray out = target_net.output(in);
			double[] out2 = new double[parameters.outputs];
			for(int i=0;i<parameters.outputs;i++) {
				out2[i]=out.getDouble(i);
			}
			return out2;
		}
		public int get_action_target_net(double[] state) {
			double[] outputs = calculate_target_net(state);
			int action = 0;
			double value = 0;
			for(int i = 0;i<outputs.length;i++) {
				if(outputs[i]>value) {
					value=outputs[i];
					action=i;
				}
			}
			return action;
		}
		public int get_action_net(double[] state, int tick) {
			if(q_learning.use_NN(tick)) {
				double[] outputs = calculate_net(state);
				int action = 0;
				double value = 0;
				for(int i = 0;i<outputs.length;i++) {
					if(outputs[i]>value) {
						value=outputs[i];
						action=i;
					}
				}
				return action;
			}else {
				Random r = new Random();
				return r.nextInt(parameters.outputs);
			}
		}
	}

	public  class Parameters {
		public final int inputs = 28;
		final int outputs = 6;
		final int hidden_layers = 3;
		final int hidden_layer_width = 7;
	}

	public Neural_Network() {
		
		NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();
		//builder.updater(new Nesterovs(q_learning.parameters.lr));
		builder.updater(new Adam(q_learning.parameters.lr));
		//builder.setOptimizationAlgo(OptimizationAlgorithm.LINE_GRADIENT_DESCENT);
		builder.seed(42474);
		
		WeightInit Weight_Init = WeightInit.XAVIER;
		
		ListBuilder listBuilder = builder.list();
		DenseLayer.Builder InputLayerBuilder = new DenseLayer.Builder();
		InputLayerBuilder.nIn(parameters.inputs);
		InputLayerBuilder.nOut(parameters.hidden_layer_width);
		InputLayerBuilder.activation(Activation.SIGMOID);
		InputLayerBuilder.weightInit(Weight_Init);

		listBuilder.layer(0, InputLayerBuilder.build());

		for (int i = 1; i <= parameters.hidden_layers; i++) {
			DenseLayer.Builder HiddenLayerBuilder = new DenseLayer.Builder();
			HiddenLayerBuilder.nIn(parameters.hidden_layer_width);
			HiddenLayerBuilder.nOut(parameters.hidden_layer_width);
			HiddenLayerBuilder.activation(Activation.SIGMOID);
			HiddenLayerBuilder.weightInit(Weight_Init);
			listBuilder.layer(i, HiddenLayerBuilder.build());
		}

		Builder OutputLayerBuilder = new OutputLayer.Builder(LossFunctions.LossFunction.MSE);
		OutputLayerBuilder.nIn(parameters.hidden_layer_width);
		OutputLayerBuilder.nOut(parameters.outputs);
		OutputLayerBuilder.activation(Activation.SIGMOID);
		OutputLayerBuilder.weightInit(Weight_Init);
		listBuilder.layer(parameters.hidden_layers + 1, OutputLayerBuilder.build());
		//OutputLayerBuilder.updater(new Nesterovs(q_learning.parameters.lr));

		MultiLayerConfiguration configure = listBuilder.build();
		MultiLayerNetwork neto = new MultiLayerNetwork(configure);
		neto.init();
		net = neto;
		target_net = neto;
		net.setListeners(new ScoreIterationListener(10));
		target_net.setListeners(new ScoreIterationListener(10));
		
	}
}
