import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Scanner;

public class NearestNeighborGenerator {
	
	public static void main(String[] args) throws FileNotFoundException {
		/**
		 * args[0] : path of the training set
		 * args[1] : path of the test set
		 * args[2] : type of data
		 * args[3] : metric type
		 * args[4] : delta
		 */
		final String trainingSetFilePath = args[0];
		System.out.println(trainingSetFilePath);
		final String testSetFilePath = args[1];
		final String dataType = args[2];
		final String metricType = args[3];
		final String deltaString = args[4];
		final double delta;
		final HashMultiMap map = new HashMultiMap("METRIC_TO_FORMAT.txt");
		DataBase db = null;
		delta = Double.parseDouble(deltaString);
		
		if (map.validateMetricFormat(metricType, dataType)){
			switch (dataType){
				case "vector1" :
					db = new DataBaseVector1(trainingSetFilePath, metricType, delta);
					break;
				default:
		             throw new IllegalArgumentException("Invalid data type: " + dataType);
			}
		}
		else {
			throw new IllegalArgumentException("The data type: " + dataType + 
					"cannot be used with the metric type:" + metricType);
		}
		db.makeTestSet(testSetFilePath);
		double[] ans = db.clasifyTestSet();
		System.out.println("the classified test set is: ");
		for (int i = 0; i< db.sizeOfTestSet; i++) {
			System.out.println(Arrays.toString((double[]) db.testSetPoints[i]) + ": " + ans[i]);
		}
	}

}
