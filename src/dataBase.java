import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;


//need to generalize from <double[],double> to <T,P>
public class dataBase {
	
	Metric metric;
	int numOfPoints;
	int dim;
	double scale;
	private HashMap<double[], Double> trainingSet = new HashMap<double[], Double>();
	private HashMap<double[], Double> trainingSetNoGammaPoints = new HashMap<double[], Double>();
	private HashMap<double[], Double> gammaNet = new HashMap<double[], Double>();
	private HashMap<Double, Integer> lableCount = new HashMap<Double, Integer>();

	final String REGEX_DATABASE = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";
	final Pattern pattern1  = Pattern.compile(REGEX_DATABASE);
	
	public dataBase (String trainingSetFilePath, String metricType, String format) throws FileNotFoundException{
		
		metricFactory metricFactory = new metricFactory();		
    	metric = metricFactory.getMetric(metricType);
		 
		 Scanner s = new Scanner(new FileReader(trainingSetFilePath));
		 numOfPoints = Integer.parseInt(s.findInLine(pattern1));
		 dim = Integer.parseInt(s.findInLine(pattern1));
		 double lable;
		 double[] point;
		 String str;
		 int i=0;
		 
		 
		 System.out.println("num of point: " + numOfPoints);
		 System.out.println("dim: " + dim);
		
		 s.nextLine();
		 while (s.hasNextLine()){
			 			
			 lable = Double.parseDouble(s.findInLine(pattern1));
			 point = new double[dim];
			 
			 
			 while((str=s.findInLine(pattern1))!=null) {
				 	
					point[i] = Double.parseDouble(str);
					i++;				
				}
			 
			 trainingSet.put(point, lable);
			 i=0;
			 if (s.hasNextLine()) {s.nextLine();}
		 }
		 
		 s.close();
		 
		 System.out.println("");
		 System.out.println("training set: ");
		 for (Map.Entry<double[], Double> entry : trainingSet.entrySet()) {
			 
			 System.out.print("key: ");
			 for (int j=0;j<dim;j++){
				 System.out.print(entry.getKey()[j] + "  ");
			    }
			 System.out.print("  -->  ");
			 System.out.print("lable: " + entry.getValue());
			 System.out.println("");
			}		 
		 
		 makeGammaNet();
	}
	
	
	public void addPointAndLable(double[] point,Double lable){
		trainingSet.put(point, lable);
	}
	
	public void addPoint(double[] point){
		trainingSet.put(point, null);
	}
	
	public Metric getMetric(){
		return metric;
	}
	

	public <P,T> T calcDistance(P p1, P p2){
		return (T) metric.calcDistance(p1, p2);
    }
	
	boolean covered = false;
	
	public void makeGammaNet(){
		
		scale = 3;
		//check for points in gamma net
		 for (Map.Entry<double[], Double> TS : trainingSet.entrySet()) {
			 
			 for (Map.Entry<double[], Double> GN : gammaNet.entrySet()){
				 if (((euclidian)metric).operatorLessThenOrEqual(((euclidian)metric).calcDistance(TS.getKey(), GN.getKey()), scale)){
					 covered = true;
					 break;
				 }
			 }
			 
			 if (covered == false){
				 gammaNet.put(TS.getKey(), null);
				 trainingSetNoGammaPoints.put(TS.getKey(), TS.getValue());
			 }
			 
			 covered=false;
		 }
		 
//		assign labels to gamma net points
		 for (Map.Entry<double[], Double> GN : gammaNet.entrySet()) {
			 
			 for (Map.Entry<double[], Double> TS : trainingSetNoGammaPoints.entrySet()){
				 if (((euclidian)metric).operatorLessThenOrEqual(((euclidian)metric).calcDistance(TS.getKey(), GN.getKey()), scale)){
					 if (lableCount.containsKey(TS.getValue())){
						 lableCount.put(TS.getValue(), lableCount.get(TS.getValue()) + 1);
					 }
					 else{
						 lableCount.put(TS.getValue(), 0);
					 }
				 }			 
			 }
			 
			 double maxLable=0;
			 for (Map.Entry<Double, Integer> LC : lableCount.entrySet()){
				 if (LC.getValue()>maxLable){
					 maxLable = LC.getValue();
				 }
			 }
			 
			 for (Map.Entry<Double, Integer> LC : lableCount.entrySet()){
				 if (LC.getValue()==maxLable){
					 gammaNet.put(GN.getKey(), maxLable);
				 }
			 }
			 
			 lableCount.clear();
			 
		 }
		 
		 System.out.println("");
		 System.out.println("gamma net contains: ");
		 for (Map.Entry<double[], Double> GN : gammaNet.entrySet()) {
			 
			 System.out.print("key: ");
			 for (int j=0;j<dim;j++){
				 System.out.print(GN.getKey()[j] + "  ");
			    }
			 System.out.print("  -->  ");
			 System.out.print("lable: " + GN.getValue());
			 System.out.println("");
			}		 
		
		
	}

	
}