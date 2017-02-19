import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

// maps a metric type to an array of formats the metric can accept
public class HashMultiMap {
	private HashMap<String, ArrayList<String>> metricToFormat = new HashMap<String, ArrayList<String>>();
	public HashMultiMap(String filePath){
		final String REGEX_DATABASE = "[a-zA-Z0-9]+";
		final Pattern pattern1  = Pattern.compile(REGEX_DATABASE);
		try {
			Scanner s = new Scanner(new FileReader(filePath));
			while (s.hasNextLine()){
				String str;
				String metric = s.findInLine(pattern1);
				ArrayList<String> formats = new ArrayList<String>();
				while((str=s.findInLine(pattern1))!=null) {
					formats.add(str);
				}
				
				if (metric != null){
					metricToFormat.put(metric, formats);
				}

				if (s.hasNextLine()) {s.nextLine();}
			}
			s.close();
			} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		
		}
	
	public boolean validateMetric (String metric) {
		return (this.metricToFormat.containsKey(metric));
	}
	
	public boolean validateMetricFormat (String metric, String format) {
		System.out.println("valid matric to formt: ");
		System.out.println(metricToFormat);
		System.out.println("");
		return validateMetric(metric) && (this.metricToFormat.get(metric).contains(format));
	}

	
}