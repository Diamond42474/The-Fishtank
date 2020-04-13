package Brain;

import java.util.ArrayList;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer.Builder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import Brain.Neural_Network.Experience.Settings;

public class Neural_Network {
	private Parameters parameters = new Parameters();
	private MultiLayerNetwork net;
	private MultiLayerNetwork target_net;
	public Compute compute = new Compute();
	public Experience experience = new Experience();
	private ArrayList<Brain.Experience> experiences = new ArrayList<Brain.Experience>();
	
	public class Experience{
		Settings settings = new Settings();
		class Settings{
			final int max_Storage = 10000;
		}
		void manage() {
			if(experiences.size()>settings.max_Storage) {
				experiences.remove(0);
			}
		}
		public void add_experience(Brain.Experience exp) {
			experiences.add(exp);
			manage();
		}
	}
	
	public class Compute{
		public double[] calculate(double[] input) {
			INDArray in = Nd4j.zeros(1, parameters.inputs);
			for(int i = 0;i<parameters.inputs;i++) {
				in.putScalar(new int[]{0, i},input[i]);
			}
			INDArray out = net.output(in);
			double[] out2 = new double[parameters.outputs];
			for(int i=0;i<parameters.outputs;i++) {
				out2[i]=out.getDouble(i);
			}
			return out2;
		}
		public int get_action(double[] state) {
			double[] outputs = calculate(state);
			int action = 0;
			double value = 0;
			for(int i = 0;i<outputs.length;i++) {
				System.out.println(outputs[i]);
				if(outputs[i]>value) {
					value=outputs[i];
					action=i;
				}
			}
			return action;
		}
	}

	private class Parameters {
		final int inputs = 2;
		final int outputs = 4;
		final int hidden_layers = 2;
		final int hidden_layer_width = 3;
	}

	void setup() {
		
		NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();
		builder.updater(new Nesterovs(0.1));
		builder.seed(42474);
		
		ListBuilder listBuilder = builder.list();
		DenseLayer.Builder InputLayerBuilder = new DenseLayer.Builder();
		InputLayerBuilder.nIn(parameters.inputs);
		InputLayerBuilder.nOut(parameters.hidden_layer_width);
		InputLayerBuilder.activation(Activation.SIGMOID);
		InputLayerBuilder.weightInit(WeightInit.SIGMOID_UNIFORM);

		listBuilder.layer(0, InputLayerBuilder.build());

		for (int i = 1; i <= parameters.hidden_layers; i++) {
			DenseLayer.Builder HiddenLayerBuilder = new DenseLayer.Builder();
			HiddenLayerBuilder.nIn(parameters.hidden_layer_width);
			HiddenLayerBuilder.nOut(parameters.hidden_layer_width);
			HiddenLayerBuilder.activation(Activation.SIGMOID);
			HiddenLayerBuilder.weightInit(WeightInit.SIGMOID_UNIFORM);
			listBuilder.layer(i, HiddenLayerBuilder.build());
		}

		Builder OutputLayerBuilder = new OutputLayer.Builder(LossFunctions.LossFunction.XENT);
		OutputLayerBuilder.nIn(parameters.hidden_layer_width);
		OutputLayerBuilder.nOut(parameters.outputs);
		OutputLayerBuilder.activation(Activation.SIGMOID);
		OutputLayerBuilder.weightInit(WeightInit.SIGMOID_UNIFORM);
		listBuilder.layer(parameters.hidden_layers + 1, OutputLayerBuilder.build());
		OutputLayerBuilder.updater(new Nesterovs(0.1));

		MultiLayerConfiguration configure = listBuilder.build();
		MultiLayerNetwork neto = new MultiLayerNetwork(configure);
		neto.init();
		net = neto;
		target_net = neto;
		
	}
}
