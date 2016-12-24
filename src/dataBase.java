import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class dataBase {
	
	Metric<?,?> metric;
	int numOfPoints;
	int dim;
	private HashMap<double[], Double> pointToLable = new HashMap<double[], Double>();
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
			 
			 pointToLable.put(point, lable);
			 i=0;
			 if (s.hasNextLine()) {s.nextLine();}
		 }
		 
		 s.close();
		 
		 System.out.println("data base contains: ");
		 for (Map.Entry<double[], Double> entry : pointToLable.entrySet()) {
			 
			 System.out.print("key: ");
			 for (int j=0;j<dim;j++){
				 System.out.print(entry.getKey()[j] + "  ");
			    }
			 System.out.print("  -->  ");
			 System.out.print("lable: " + entry.getValue());
			 System.out.println("");
			}		 
	}

	
	
	
}
