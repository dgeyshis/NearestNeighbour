
public class euclidian implements Metric<double[]> {

	public Double calcDistance(double[] firstPoint, double[] secondPoint) {
		int len = firstPoint.length;
		double sum = 0;
		
		for (int i=0;i<len;i++){
			sum = Math.pow((firstPoint[i] - secondPoint[i]),2);			
		}
		
		sum = Math.sqrt(sum);	
		return sum;
	}

}
