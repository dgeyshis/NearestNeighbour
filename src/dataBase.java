import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

public class dataBase {
	
	int numOfPoints;
	int dim;
	private HashMap<double[], Double> pointToLable = new HashMap<double[], Double>();
	final String REGEX_DATABASE = "[0-9].[0-9]+";
	final Pattern pattern1  = Pattern.compile(REGEX_DATABASE);
	
	public dataBase (String trainingSetFilePath, String metric, String format) throws FileNotFoundException{
		
		Scanner s = new Scanner(new FileReader(trainingSetFilePath));
		 numOfPoints = Integer.parseInt(s.findInLine(pattern1));
		 dim = Integer.parseInt(s.findInLine(pattern1));
		 double lable;
		 double[] point;
		 String str;
		 int i=0;
		 
		 
		 System.out.println("num of point: " + numOfPoints);
		 System.out.println("dim: " + dim);
		 
//		 while (s.hasNext()){
//			 lable = Double.parseDouble(s.findInLine(pattern1));
//			 point = new double[dim];
//			 
//			 while((str=s.findInLine(pattern1))!=null) {
//					point[i] = Double.parseDouble(str);
//					i++;
//				}
//			 
//			 pointToLable.put(point, lable);
//			 i=0;
//			 s.next();
//		 }
		 
		 s.close();
		
	}

}
