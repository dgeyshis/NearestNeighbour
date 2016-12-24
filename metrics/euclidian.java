
public class euclidian implements Metric<double[], Double> {

	
	public Double calcDistance(double[] firstPoint, double[] secondPoint) {
		int len = firstPoint.length;
		double sum = 0;
		
		for (int i=0;i<len;i++){
			sum = Math.pow((firstPoint[i] - secondPoint[i]),2);			
		}
		
		sum = Math.sqrt(sum);	
		return sum;
	}


	public boolean operatorLessThenOrEqual(double p1, double p2) {
			
		if (p1<=p2){return true;}
		return false;
	}



	

}
