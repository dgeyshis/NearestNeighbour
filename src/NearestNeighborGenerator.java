import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class NearestNeighborGenerator {
	
	public static void main(String[] args) throws FileNotFoundException {
		/**
		 * args[0] : path of the training set
		 * args[1] : type of data
		 * args[2] : metric type
		 * args[3] : delta
		 */
		final String trainingSetFilePath = args[0];
		final String dataType = args[1];
		final String metricType = args[2];
		final String deltaString = args[3];
		final double delta;
		final hashMultiMap map = new hashMultiMap("METRIC_TO_FORMAT.txt");
		dataBaseType1 db = null;
		
		
		
		delta = Double.parseDouble(deltaString);
		
		if (map.validateMetricFormat(metricType, dataType)){   
		      db = new dataBaseType1(trainingSetFilePath, metricType, dataType, delta);
		}
		


		
	}
	

	
	

}
