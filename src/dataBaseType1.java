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
	//Metric<?, ?> metric;
	euclidian metric;
	int dim;
	int sizeOfTrainingSet;
	int sizeOfGammaNet;
	final String REGEX_DATABASE = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";  //regular expression for double
	final Pattern pattern1  = Pattern.compile(REGEX_DATABASE);
	double scale = 16;
	
	public dataBaseType1 (String trainingSetFilePath, String metricType, String format) throws FileNotFoundException{
	
		data db = data.TRAINING_SET;

		makeTrainingSet(trainingSetFilePath, format);
		makeMetric(metricType);
		printDataBase(db);
	
		db = data.GAMMA_NET;
		makeGammaNet();		
		System.out.println("");
		printDataBase(db);
	//	printA/llDistances();
	
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
		Map<Double,Integer> lableToCount =new HashMap<Double,Integer>();  
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
				restPoints.add(trainingSetPoints[i]);
				restLables.add(trainingSetLabels[i]);
				sizeOfGammaNet++;
			}
			else{
				restPoints.add(trainingSetPoints[i]);
				restLables.add(trainingSetLabels[i]);
			}
			
			gammaElem = true;
			
		}
				
		
		//sets labels for gammaNet elements
		for (int i=0;i<tmpPoints.size();i++){
			for (int j=0;j<restPoints.size();j++){
				if (((euclidian)metric).calcDistance(tmpPoints.get(i), restPoints.get(j))<scale){
					if (lableToCount.containsKey(restLables.get(j))){
						currCount = lableToCount.get(restLables.get(j));
						lableToCount.put(restLables.get(j), ++currCount);
					}
					else{
						lableToCount.put(restLables.get(j), 1);
					}
				}
			}
			
		//	System.out.println("");
		//	System.out.println("lable to count for point " + i + ": ");
		//	System.out.println(Arrays.asList(lableToCount));
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
			lableToCount.clear();
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




