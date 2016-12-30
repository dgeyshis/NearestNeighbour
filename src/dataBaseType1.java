import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class dataBaseType1 {

	private double[][] trainingSetPoints;
	private double[] trainingSetLables;
	private double[][] gammaNetPoints;
	private double[] gammaNetLables;
	//Metric<?, ?> metric;
	int dim;
	int sizeOfTrainingSet;
	int sizeOfGammaNet;
	final String REGEX_DATABASE = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";  //regular expression for double
	final Pattern pattern1  = Pattern.compile(REGEX_DATABASE);
	double scale = 30;
	
	public dataBaseType1 (String trainingSetFilePath, String metricType, String format) throws FileNotFoundException{
	
		
	//	metric = metricFactory.getMetric(metricType);
		//double x = ((euclidian)metric).calcDistance(trainingSetPoints[0],trainingSetPoints[0]);
	//	double x = metric.calcDistance(trainingSetPoints[0],trainingSetPoints[0]);
		Metric<double[],Double> metric1 = new euclidian();
	//	double x = metric1.calcDistance(trainingSetPoints[1],trainingSetPoints[2]);
		
		
	}
}
		//System.out.println(x);
				
//		makeTrainingSet(trainingSetFilePath, format);
//		makeMetric(metricType);
//		printDataBase(0);
//	
//		
//		makeGammaNet();		
//		System.out.println("");
	//	printDataBase(1);
	
//	}
	
//	public void makeMetric(String metricType){	
//		metric = metricFactory.getMetric(metricType);
//	}
	
	
//	public void makeTrainingSet(String trainingSetFilePath, String format) throws FileNotFoundException{
//		 Scanner s = new Scanner(new FileReader(trainingSetFilePath));
//		 sizeOfTrainingSet = Integer.parseInt(s.findInLine(pattern1));
//		 dim = Integer.parseInt(s.findInLine(pattern1));
//		 
//
//		 trainingSetPoints = new double[sizeOfTrainingSet][dim];
//		 trainingSetLables = new double[sizeOfTrainingSet];
//		 		
//		 String inLine;
//		 int currPoint=0;
//		 int currAttribute=0;
//		 s.nextLine();
//		 
//		 while (s.hasNextLine()){
//			 
//			 trainingSetLables[currPoint] = Double.parseDouble(s.findInLine(pattern1));
//			 			 
//			 while((inLine=s.findInLine(pattern1))!=null) {
//				 trainingSetPoints[currPoint][currAttribute] = Double.parseDouble(inLine);	
//				 currAttribute++;
//				}
//			 
//			 currPoint++;
//			 currAttribute=0;
//			 
//	
//			 if (s.hasNextLine()) {s.nextLine();}
//		 }
//		 
//		 s.close();
//		 	 
//		
//	}
//	
//	public enum data {
//	    TRAINING_SET,GAMMA_NET
//	}	
//	
//	public double getDistance(int i, int j, data db){
//		
//		double[][] currDB = null;
//		
//		switch (db){
//		
//		case TRAINING_SET:
//			currDB = trainingSetPoints;
//			break;
//			
//		case GAMMA_NET:
//			currDB = gammaNetPoints;
//			break;
//			
//		default:
//			System.out.println("wrong db choise");
//		}
//		
//		if (trainingSetPoints==null){
//			System.out.println("wrong db choise");
//			return -1;
//		}
//		
//		return ((euclidian)metric).calcDistance(currDB[i], currDB[j]);
//
//	}
//	
//	
//	
//	
//	
//	public void makeGammaNet(){
//		ArrayList<double[]> tmpPoints = new ArrayList<double[]>();
//		ArrayList<Double> tmpLables = new ArrayList<Double>();
//		ArrayList<double[]> restPoints = new ArrayList<double[]>();
//		ArrayList<Double> restLables = new ArrayList<Double>();
//		boolean gammaElem = true;
//		Map<Double,Integer> lableToCount =new HashMap<Double,Integer>();  
//		sizeOfGammaNet = 0;
//		int currCount;
//		
//		//sets which points are in the gamma net
//		for (int i=0; i<sizeOfTrainingSet;i++){
//			System.out.println("i " + i);
//			for (int j=0;j<sizeOfGammaNet;j++){
//				System.out.println("j: " + j);
//				if (((euclidian)metric).calcDistance(trainingSetPoints[i], tmpPoints.get(j))<scale){
//					gammaElem = false;
//					break;
//				}
//			}
//			
//			if (gammaElem==true){
//				System.out.println("gamma net elem");
//				tmpPoints.add(trainingSetPoints[i]);
//				sizeOfGammaNet++;
//			}
//			else{
//				restPoints.add(trainingSetPoints[i]);
//				restLables.add(trainingSetLables[i]);
//			}
//			
//			gammaElem = true;
//			
//		}
//				
//		
//		//sets labels for gammaNet elements
//		for (int i=0;i<tmpPoints.size();i++){
//			for (int j=0;j<restPoints.size();j++){
//				if (((euclidian)metric).calcDistance(tmpPoints.get(i), restPoints.get(j))<scale){
//					if (lableToCount.containsKey(restLables.get(j))){
//						currCount = lableToCount.get(restLables.get(j));
//						lableToCount.put(restLables.get(j), ++currCount);
//					}
//					else{
//						lableToCount.put(restLables.get(j), 1);
//					}
//				}
//			}
//			
//			Double maxLable = (Double)null;
//			Integer maxCount = (Integer)null; ;
//
//			for(Map.Entry<Double, Integer> entry : lableToCount.entrySet()) {
//				maxLable = entry.getKey();
//				maxCount = entry.getValue();
//				break;
//			}
//
//			
//			for (Map.Entry<Double, Integer> currEntry : lableToCount.entrySet()) {
//			    if (maxCount<currEntry.getValue()){
//			    	maxLable = currEntry.getKey();
//			    	maxCount = currEntry.getValue();
//			    }
//			}
//			
//			
//			tmpLables.add(maxLable);
//						
//		}
//		
//		
//		//create gammaNet arrays
////		gammaNetPoints = new double[tmpPoints.size()][dim];
////		gammaNetLables = new double[tmpPoints.size()];
////		for (int i=0; i<tmpPoints.size();i++){
////			gammaNetPoints[i] = tmpPoints.get(i);
////			gammaNetLables[i] = tmpLables.get(i);
////		}
//				
//		
//	}
//	
//	
//public void printDataBase(int who){
//		
//		System.out.println("size: " + sizeOfTrainingSet);
//	    System.out.println("dim: " + dim);
//		 
//		if (who==0){
//			 System.out.println();
//			 for (int i =0; i<sizeOfTrainingSet;i++){
//			 System.out.println("point " + i + " : " + Arrays.toString(trainingSetPoints[i]) + ", lable: " + trainingSetLables[i]);				 
//			 }
//		}
//		else{
//			 System.out.println();
//			 for (int i =0; i<sizeOfGammaNet;i++){
//			 System.out.println("point " + i + " : " + Arrays.toString(gammaNetPoints[i]) + ", lable: " + gammaNetPoints[i]);				 
//			 }
//		}
//	}
//	
//	
//}



