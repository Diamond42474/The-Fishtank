package Brain;

public class Learning_Tracker {
	private static Q_Learning q_learning = new Q_Learning();
	 public static int ticks_since_last_train = 0;
	 public static int ticks_since_last_target_net_update =0;
	 public static void train() {
		 if(ticks_since_last_train>q_learning.parameters.train) {
			 System.out.println("Training Started...");
			 Brain_Collection.net1.compute.training.Train();
			 Brain_Collection.net2.compute.training.Train();
			 ticks_since_last_train=0;
			 System.out.println("Training Done...");
		 }else {
			 ticks_since_last_train++;
		 }
		 if(ticks_since_last_target_net_update>q_learning.parameters.target_update) {
			 Brain_Collection.net1.update_target_net();
			 Brain_Collection.net2.update_target_net();
			 ticks_since_last_target_net_update=0;
		 }else {
			 ticks_since_last_target_net_update++;
		 }
	 }
}
