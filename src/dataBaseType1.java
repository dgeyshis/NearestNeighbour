import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class dataBaseType1 {
	
	//change lableling gamma net using new information
	private double[][] trainingSetPoints;
	private double[] trainingSetLabels;
	private double[][] gammaNetPoints;
	private double[] gammaNetLabels;
//	private double[][] dataSetPoints;
	private double[] dataSetLabels;
	//Metric<?, ?> metric;
	euclidian metric;
	int dim;
	int sizeOfTrainingSet;
	int sizeOfGammaNet;
	final String REGEX_DATABASE = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";  //regular expression for double
	final Pattern pattern1  = Pattern.compile(REGEX_DATABASE);
	double scale;
	double delta;
	
	public dataBaseType1 (String trainingSetFilePath, String metricType, String format, double _delta) throws FileNotFoundException{
		
		this.delta = _delta; 
		data db = data.TRAINING_SET;

		makeTrainingSet(trainingSetFilePath, format);
		makeMetric(metricType);
		printDataBase(db);
	
		
		makeClassifier();
		
		db = data.GAMMA_NET;
	//	System.out.println("");
		printDataBase(db);
		printAllDistances();
	
	}
	
	// the classifier is gammaNet 
	// dataSetPoints : collection on points to classify
	// dataSetLabels : assigned labels for dataSetPoints points
	// this method will put the assigned labels in dataSetLabeles
	double[] classify(double[][] dataSetPoints , int dataSetSize){
		
		double[] dataSetLabels = new double[dataSetSize];
		double minDistance;
		int minIndex;
		double currDistance;
		
		for (int i=1;i<dataSetSize;i++){
			
			minDistance = metric.calcDistance(dataSetPoints[i], gammaNetPoints[0]);
			minIndex = 0;
			for (int j=0;j<sizeOfGammaNet;j++){
				currDistance = metric.calcDistance(dataSetPoints[i], gammaNetPoints[j]);
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
		
		double pen;
		pen = trainingSetSize * gammaNetSize * alpha * diffLabels * delta * epsilone;
		return pen;
	}
	public void makeClassifier(){
		int diffLabelCount = calcDiffLabelCount();
		double maxDistance = getMaxDistance();
		double currScale = maxDistance*2;
		double mintpenalty;
		double currPenalty;
		double epsilon;
		double[] assignedPointsToTrainingSetByGammaNet = new double[sizeOfTrainingSet];
		double alpha = sizeOfTrainingSet/(sizeOfTrainingSet-sizeOfGammaNet);
		
		//first iteration 
		
		scale = currScale;
		makeGammaNet();		
		assignedPointsToTrainingSetByGammaNet = classify(trainingSetPoints, sizeOfTrainingSet);
		epsilon = calcClassifierError(trainingSetPoints, trainingSetLabels,assignedPointsToTrainingSetByGammaNet,sizeOfTrainingSet);
		mintpenalty = calcPenalty(sizeOfTrainingSet,sizeOfGammaNet,alpha,diffLabelCount,delta,epsilon);
		
		
		System.out.println("");
		System.out.println("current scale: " + currScale);
		System.out.println("best scale til now: " + scale);
		System.out.println("penalty for current scale: " + mintpenalty);
		System.out.println("size of gamma net: " + sizeOfGammaNet);
		System.out.println(""); 
		int k = 0;
		currScale /= 2;		
		while (currScale>0 && sizeOfGammaNet != sizeOfTrainingSet && k<30){
			
			assignedPointsToTrainingSetByGammaNet = classify(trainingSetPoints, sizeOfTrainingSet);
			epsilon = calcClassifierError(trainingSetPoints, trainingSetLabels,assignedPointsToTrainingSetByGammaNet,sizeOfTrainingSet);
			currPenalty = calcPenalty(sizeOfTrainingSet,sizeOfGammaNet,alpha,diffLabelCount,delta,epsilon);
			
			System.out.println("");
			System.out.println("current scale: " + currScale);
			System.out.println("best scale til now: " + scale);
			System.out.println("penalty for current scale: " + currPenalty);
			System.out.println("smallest penalty: " + mintpenalty);
			System.out.println("size of gamma net: " + sizeOfGammaNet);
			System.out.println("");
			
			if (currPenalty<mintpenalty){
				mintpenalty = currPenalty;
				scale = currScale;
			}
			
			currScale /= 2;
			k++;
		}		
	}
	
	
	double calcClassifierError(double[][] dataSetPoints , double[] originalLbels, double[] assignedLabels, int size){
		
		double error = 0;
		for (int i=0;i<size;i++){
			
			if (originalLbels[i]!=assignedLabels[i]){
				error++;
			}			
		}
		error = error/size;
		
		return error;
		
	}
	
	double getMaxDistance(){
		
		double max = 0;
		
		for (int i=0;i<sizeOfTrainingSet;i++){
			double currDistance = metric.calcDistance(trainingSetPoints[0],trainingSetPoints[i]);
			if (max<currDistance) {max = currDistance;}
		}
				
		return max;
	}
	
	int calcDiffLabelCount(){
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
		//metric = metricFactory.getMetric(metricType);
		metric = new euclidian();
	}
	

	public void makeTrainingSet(String trainingSetFilePath, String format) throws FileNotFoundException{
		 Scanner s = new Scanner(new FileReader(trainingSetFilePath));
		 sizeOfTrainingSet = Integer.parseInt(s.findInLine(pattern1));
		 dim = Integer.parseInt(s.findInLine(pattern1));
		 

		 trainingSetPoints = new double[sizeOfTrainingSet][dim];
		 trainingSetLabels = new double[sizeOfTrainingSet];
		 		
		 String inLine;
		 int currPoint=0;
		 int currAttribute=0;
		 s.nextLine();
		 
		 while (s.hasNextLine()){
			 
			 trainingSetLabels[currPoint] = Double.parseDouble(s.findInLine(pattern1));
			 			 
			 while((inLine=s.findInLine(pattern1))!=null) {
				 trainingSetPoints[currPoint][currAttribute] = Double.parseDouble(inLine);	
				 currAttribute++;
				}
			 
			 currPoint++;
			 currAttribute=0;
			 
	
			 if (s.hasNextLine()) {s.nextLine();}
		 }
		 
		 s.close();
		 	 
		
	}

	public enum data {
	    TRAINING_SET,GAMMA_NET
	}	
	
	public double getDistance(int i, int j, data db){
		
		double[][] currDB = null;
		
		switch (db){
		
		case TRAINING_SET:
			currDB = trainingSetPoints;
			break;
			
		case GAMMA_NET:
			currDB = gammaNetPoints;
			break;
			
		default:
			System.out.println("wrong db choise");
		}
		
		if (trainingSetPoints==null){
			System.out.println("wrong db choise");
			return -1;
		}
		
		return ((euclidian)metric).calcDistance(currDB[i], currDB[j]);

	}
	
	
	
	
	public void makeGammaNet(){
		
	/**
	 * tmpPoint : temporary array for the gamma net elements
	 * tmpLabels : labels for tmpPoins elements
	 * restPoints : temporary array for all of the elements of the training set
	 * restLabels: labels for restPoint elements
	*/
		ArrayList<double[]> tmpPoints = new ArrayList<double[]>();
		ArrayList<Double> tmpLables = new ArrayList<Double>();
		ArrayList<double[]> restPoints = new ArrayList<double[]>();
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
		for (int i=0; i<sizeOfTrainingSet;i++){
			for (int j=0;j<sizeOfGammaNet;j++){

				if (((euclidian)metric).calcDistance(trainingSetPoints[i], tmpPoints.get(j))<scale){
					gammaElem = false;
					break;
				}
			}
			
			if (gammaElem==true){
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
		gammaNetPoints = new double[tmpPoints.size()][dim];
		gammaNetLabels = new double[tmpPoints.size()];
		for (int i=0; i<tmpPoints.size();i++){
			gammaNetPoints[i] = tmpPoints.get(i);
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
		
		
	    
	    switch (db){
		
			case TRAINING_SET:
				System.out.println();
				System.out.println("training set- ");
				System.out.println("size: " + sizeOfTrainingSet);
			    System.out.println("dim: " + dim);
				 for (int i =0; i<sizeOfTrainingSet;i++){
				 System.out.println("point " + i + " : " + Arrays.toString(trainingSetPoints[i]) + ", lable: " + trainingSetLabels[i]);				 
				 }
				break;
				
			case GAMMA_NET:
				 System.out.println();
				 System.out.println("gamma net- ");
				 System.out.println();
				 System.out.println("scale: " + scale);
				 System.out.println("size: " + sizeOfGammaNet);
				 System.out.println("dim: " + dim);
				 for (int i =0; i<sizeOfGammaNet;i++){
				 System.out.println("point " + i + " : " + Arrays.toString(gammaNetPoints[i]) + ", lable: " + gammaNetLabels[i]);				 
				 }
				break;
				
			default:
				System.out.println("wrong db choise");
			}	    

	}

public void printAllDistances(){
	
	System.out.println();
	System.out.println("Distances: ");
	for (int i=0; i<sizeOfTrainingSet;i++){
		for (int j=0; j<sizeOfTrainingSet;j++){
			System.out.print(i + " to " + j + ": ");
			System.out.println(((euclidian)metric).calcDistance(trainingSetPoints[i], trainingSetPoints[j]));
						
		}
		
	}
}
		
}




