import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public abstract class DataBase<T> {
	
	/**
	 * trainingSetPoints : a collection of training set points such that dataSetPoints[i] is the i'th point
	 * trainingSetLabels : training set labels such that trainingSetLabels[i] is the label of trainingSetPoints[i]
	 * gammaNetPoints : gamma net point
	 * gammaNetPoints : gamma net labels such that gammaNetPoints[i] is the label of gammaNetPoints[i]
	 * metric : distance function 
	 * dimTrainingSetTrainingSet : dimTrainingSetension of each point (gammaNet point, trainingSet point etc)
	 * delta : a value in range [0..1], is used to calculate the penalty of some scale, input by user
	 * sizeOfTrainingSet
	 * sizeOfGammaNet
	 * scale : the scale which is used to build the gamma net
	 */
	T[] testSetPoints;
	protected double[] testSetLabels;
	protected T[] trainingSetPoints;
	protected double[] trainingSetLabels;
	protected ArrayList<T> gammaNetPoints;
	protected double[] gammaNetLabels;
	Metric<T> metric;
	int dimTrainingSet;
	int dimTestSet;
	int sizeOfTrainingSet;
	int sizeOfTestSet;
	int sizeOfGammaNet;
	double scale;
	double delta;
	
	public DataBase (String trainingSetFilePath, String metricType, double _delta) throws FileNotFoundException{
		
		   /**
		   * This method is the constructor for dataBaseType1
		   * @param trainingSetFilePath : The path to the input file with the training sample information
		   * @param metricType : Metric type
		   * @param format : The format of the input file
		   * @param _delta : a Value between 0 and 1, this is a user input
		   */
		
		this.delta = _delta; 
		data db = data.TRAINING_SET;
		makeTrainingSet(trainingSetFilePath);
		shuffleTrainSet();
		makeMetric(metricType);
//		printDataBase(db);
		makeClassifier();
		db = data.GAMMA_NET;
//		printDataBase(db);
//		printAllDistances();
	}
	
	private void shuffleTrainSet() {
	 Random rnd = ThreadLocalRandom.current();
	    for (int i = trainingSetPoints.length - 1; i > 0; i--)
	    {
	      int index = rnd.nextInt(i + 1);
	      // Simple swap
	      T currPoint = trainingSetPoints[index];
	      double currLabel = trainingSetLabels[index];
	      trainingSetPoints[index] = trainingSetPoints[i];
	      trainingSetLabels[index] = trainingSetLabels[i];
	      trainingSetLabels[i] = currLabel;
	      trainingSetPoints[i] = currPoint;
	    }
				
	}

	double[] classify(T[] dataSetPoints , int dataSetSize){
		
		/**
		 * This method calculates labels to an array of unlabeled points using the gammaNet class member and scale
		 * @param dataSetPoints : a collection of points such that dataSetPoints[i] is the i'th point
		 * @param dataSetSize : number of point is dataSetPoints
		 * @return double[] such that the i'th point is the returned array is the calculated label for the point dataSetPoints[i]
		 */
		
		double[] dataSetLabels = new double[dataSetSize];
		double minDistance;
		int minIndex;
		double currDistance;
		for (int i = 1; i < dataSetSize; i++){
			minDistance = metric.calcDistance(dataSetPoints[i], gammaNetPoints.get(0));
			minIndex = 0;
			for (int j = 0; j < sizeOfGammaNet; j++){
				currDistance = metric.calcDistance(dataSetPoints[i], gammaNetPoints.get(j));
				if (currDistance<minDistance){
					minDistance = currDistance;
					minIndex = j;
				}
			}
			
			dataSetLabels[i] = gammaNetLabels[minIndex];
		}
	return dataSetLabels;
	}
	
	
	double calcPenalty(int trainingSetSize, int gammaNetSize,  double alpha, int diffLabels, double delta, double epsilone){
		
		/**
		 * This method calculates the penalty of some scale (and the gamma net created using that scale) on the training sample
		 * @param trainingSetSize
		 * @param gammaNetSize
		 * @param alpha
		 * @param diffLabels : number of different labeles in the training set
		 * @param delta : trainingSetSize/(trainingSetSize - gammaNetSize)
		 * @param epsilon : the error of the classifier (which is the gamma net) on the training set
		 */
		
		double pen = alpha*epsilone + 
				2/3*(((gammaNetSize + 1)*Math.log(trainingSetSize*gammaNetSize)+Math.log(1/delta))/
						(trainingSetSize-gammaNetSize)) +
				Math.sqrt(9*(alpha*epsilone*((gammaNetSize - 1)*Math.log(trainingSetSize*diffLabels)+Math.log(1/delta))) /
						(2*(trainingSetSize - gammaNetSize)));
		return pen;
	}
	
	public void makeClassifier(){
		int diffLabelCount = calcDiffLabelCount();
		double maxDistance = getMaxDistance();
		double currScale = maxDistance * 2;
		double mintpenalty;
		double currPenalty;
		double epsilon;
		double[] assignedPointsToTrainingSetByGammaNet = new double[sizeOfTrainingSet];
		double alpha = sizeOfTrainingSet/(sizeOfTrainingSet-sizeOfGammaNet);
		
		//first iteration 
		
		scale = currScale;
		makeGammaNet(currScale);		
		assignedPointsToTrainingSetByGammaNet = classify(trainingSetPoints, sizeOfTrainingSet);
		epsilon = calcClassifierError(trainingSetLabels,assignedPointsToTrainingSetByGammaNet);
		mintpenalty = calcPenalty(sizeOfTrainingSet,sizeOfGammaNet,alpha,diffLabelCount,delta,epsilon);
		currScale /= 2;		
		while (currScale>0 && sizeOfGammaNet != sizeOfTrainingSet ){
			makeGammaNet(currScale);	
			assignedPointsToTrainingSetByGammaNet = classify(trainingSetPoints, sizeOfTrainingSet);
			epsilon = calcClassifierError(trainingSetLabels,assignedPointsToTrainingSetByGammaNet);
			currPenalty = calcPenalty(sizeOfTrainingSet,sizeOfGammaNet,alpha,diffLabelCount,delta,epsilon);	
			if (currPenalty<mintpenalty){
				mintpenalty = currPenalty;
				scale = currScale;
			}
			currScale /= 2;
		}		
	}
	
	double calcClassifierError(double[] originalLabels, double[] assignedLabels){
		
		/**
		 * This method calculates the error of the classifier
		 * @param  dataSetPoints : points that were labeled using the classifier (the gamma net)
		 * @param originalLbels : the labels for each point in dataSetPoints such that the i'th label is the label of dataSetPoints[i]
		 * (this labels are the true labels for each point, they are not calculated, rather they are given as user input
		 * @param assignedLabels : the labels that were assigned by the classifier to the point in dataSetPoints.
		 * the i'th label in assignedLabels is the calculated label for the point dataSetPoints[i]
		 * @param size : number of points in dataSetPoints
		 * @return double, the error of the labels in assignedLabels on the points in dataSetPoints
		 * the formula for the error is - sum of I[originalLbels[i]!=assignedLabels[i]]\size, 0<=i<=size 
		 */
		int size = originalLabels.length;
		double error = 0;
		for (int i = 0; i < size; i++){
			if (originalLabels[i] != assignedLabels[i]){
				error++;
			}			
		}
		error = error / size;
		
		return error;
	}
	
	double getMaxDistance(){
		
		/**
		 * @return double, The maximal distance (according to metric) between trainingSetPoints[0] and any other point trainingSetPoints[i]
		 * in trainingSetPoints, 0<=i<=sizeOfTrainingSet
		 */
		
		double max = 0;
		for (int i=0; i < sizeOfTrainingSet; i++){
			double currDistance = metric.calcDistance(trainingSetPoints[0], trainingSetPoints[i]);
			if (max < currDistance) {
				max = currDistance;
				}
		}
		return max;
	}
	
	int calcDiffLabelCount(){
		
		/**
		 * @return int , number of different labels used to label the points in trainingSetPoints
		 */
		
		Map<Double,Boolean> lableToCount = new HashMap<Double,Boolean>();
		for (int i=0;i<sizeOfTrainingSet;i++){
			double currLabel = trainingSetLabels[i];
			if (!lableToCount.containsKey(currLabel)){
				lableToCount.put(currLabel, true);
			}
		}
	return lableToCount.size();		
	}
	
	public void makeMetric(String metricType){	
		
		/**
		 * This method creates an instance of the required metric based on user input
		 * @param metricType : the name of the required metric
		 */
		
		metric = MetricFactory.getMetric(metricType);
	}
	

	public abstract void makeTrainingSet(String trainingSetFilePath) throws FileNotFoundException;
	public abstract void makeTestSet(String testSetFilePath) throws FileNotFoundException;
	public double[] clasifyTestSet(){
		return classify(testSetPoints, sizeOfTestSet);
	}
	
	public enum data {
	    TRAINING_SET,GAMMA_NET
	}	
	
	@SuppressWarnings("unchecked")
	public double getDistance(int i, int j, data db){

		/**
		 * This method calculates the distance between point i and point j according to the class member metric
		 * @param i : index of the first point
		 * @param j : index of the second point
		 * @param db : an enum with the value TRAINING_SET or GAMMA_NET
		 * used to differentiate between the data base with the requested points 
		 */
		
		T[] currDB = null;
		switch (db){
		case TRAINING_SET:
			currDB = trainingSetPoints;
			break;
		case GAMMA_NET:
			currDB = (T[]) gammaNetPoints.toArray();
			break;
		default:
			System.out.println("wrong db choise");
		}
		
		if (trainingSetPoints == null){
			System.out.println("wrong db choise");
			return -1;
		}
		return metric.calcDistance(currDB[i], currDB[j]);

	}
	
	
	public void makeGammaNet(double scale){
		
	/**
	 * This method creates a gamma net according to the training set a scale
	 * after using this method:
	 * The class member gammaNetPoints will point to an array of points which are the gamma net elements
	 * The class member gammaNetLabels will point to an array of labels which are the gamma net labels
	 * The class member sizeOfGammaNet will indicate the number of elements in the gamma net created
	 * @param scale
	 * information regarding the method variables:
	 * tmpPoint : temporary array for the gamma net elements
	 * tmpLabels : labels for tmpPoins elements
	 * restPoints : temporary array for all of the elements of the training set
	 * restLabels: labels for restPoint elements
	*/
		ArrayList<T> tmpPoints = new ArrayList<T>();
		ArrayList<Double> tmpLables = new ArrayList<Double>();
		ArrayList<T> restPoints = new ArrayList<T>();
		ArrayList<Double> restLables = new ArrayList<Double>();
		boolean gammaElem = true;
		Map<Double,Integer> lableToCount;
		ArrayList<Map<Double,Integer>> labelToCountArray = new ArrayList<Map<Double,Integer>>();;
		double minDistanceFromGammaPoint;
		double currDistance;
		int minDistanceFromGammaPointIndex;
		sizeOfGammaNet = 0;
		int currCount;
		//sets which points are in the gamma net
		for (int i = 0; i < sizeOfTrainingSet; i++){
			for (int j = 0; j < sizeOfGammaNet; j++){

				if (metric.calcDistance(trainingSetPoints[i], tmpPoints.get(j))<scale){
					gammaElem = false;
					break;
				}
			}
			
			if (gammaElem == true){
				tmpPoints.add(trainingSetPoints[i]);
				labelToCountArray.add(new HashMap<Double,Integer>());
				sizeOfGammaNet++;
			}

			restPoints.add(trainingSetPoints[i]);
			restLables.add(trainingSetLabels[i]);
			gammaElem = true;
			
		}
				
		//sets labels for gammaNet elements
		
		//create label count for each element in the gamma net
		for (int i=0;i<restPoints.size();i++){
			
			minDistanceFromGammaPointIndex = 0;
			minDistanceFromGammaPoint = metric.calcDistance(restPoints.get(i), tmpPoints.get(0));		
			for (int j=0;j<sizeOfGammaNet;j++){
				currDistance = metric.calcDistance(restPoints.get(i), tmpPoints.get(j));
				if (currDistance<minDistanceFromGammaPoint){
					minDistanceFromGammaPointIndex = j;
					minDistanceFromGammaPoint = currDistance;
				}
			}
			
			lableToCount = labelToCountArray.get(minDistanceFromGammaPointIndex);

			if (lableToCount.containsKey(restLables.get(i))){
				currCount = lableToCount.get(restLables.get(i));
				lableToCount.put(restLables.get(i) , ++currCount);
			}
			else{
				lableToCount.put(restLables.get(i) , 1);
			}
		}		
	
		//assign majority label for each gamma net element
		for (int i=0;i<sizeOfGammaNet;i++){
			lableToCount = labelToCountArray.get(i);
			
			Double maxLable = (Double)null;
			Integer maxCount = (Integer)null; ;

			for(Map.Entry<Double, Integer> entry : lableToCount.entrySet()) {
				maxLable = entry.getKey();
				maxCount = entry.getValue();
				break;
			}

			for (Map.Entry<Double, Integer> currEntry : lableToCount.entrySet()) {
			    if (maxCount<currEntry.getValue()){
			    	maxLable = currEntry.getKey();
			    	maxCount = currEntry.getValue();
			    }
			}
			
			tmpLables.add(maxLable);		
		}
		
	//	create gammaNet arrays
		gammaNetPoints = new ArrayList<T>(tmpPoints.size());
		gammaNetLabels = new double[tmpPoints.size()];
		for (int i=0; i < tmpPoints.size(); i++){
			gammaNetPoints.add(tmpPoints.get(i));
			if ( tmpLables.get(i) == null){
				gammaNetLabels[i] = 1;
			}
			else{
				gammaNetLabels[i] = tmpLables.get(i);
			}
			
		}	
}
	
	
///////////////////////////////////////////////////////////////
///////////////////////////  Prints ///////////////////////////
///////////////////////////////////////////////////////////////
	
public void printDataBase(data db){
		
	/**
	 * This method prints the points and there labels of db, for debbuging purposes
	 * @param db, an enum which indicates what db to print, the training set or the gamme net
	 */
		
	    
	    switch (db){
		
			case TRAINING_SET:
				System.out.println();
				System.out.println("training set- ");
				System.out.println("size: " + sizeOfTrainingSet);
			    System.out.println("dimTrainingSet: " + dimTrainingSet);
				 for (int i = 0; i < sizeOfTrainingSet; i++){
				 System.out.println("point " + i + " : " + Arrays.toString((double[])trainingSetPoints[i]) + ", lable: " + trainingSetLabels[i]);				 
				 }
				break;
				
			case GAMMA_NET:
				 System.out.println();
				 System.out.println("gamma net- ");
				 System.out.println("scale: " + scale);
				 System.out.println("size: " + sizeOfGammaNet);
				 System.out.println("dimTrainingSet: " + dimTrainingSet);
				 for (int i = 0; i < sizeOfGammaNet; i++){
				 System.out.println("point " + i + " : " + Arrays.toString((double[])gammaNetPoints.get(i)) + ", lable: " + gammaNetLabels[i]);				 
				 }
				break;
				
			default:
				System.out.println("wrong db choise");
			}	    

	}

public void printAllDistances(){
	
	/**
	 * This method print the distance from point i to j (for each 0<=i,j<=sizeOfTrainingSet) according to class memeber metric
	 * for debugging purposes
	 */
	
	System.out.println();
	System.out.println("Distances: ");
	for (int i=0; i<sizeOfTrainingSet;i++){
		for (int j=0; j<sizeOfTrainingSet;j++){
			System.out.print(i + " to " + j + ": ");
			System.out.println(metric.calcDistance(trainingSetPoints[i], trainingSetPoints[j]));
						
		}
		
	}
}
		
}




