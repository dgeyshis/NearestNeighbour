public class metricFactory {
	

   public static Metric<?,?> getMetric(String metricType){
     
	   if(metricType == null){
         return null;}		
	   
      if(metricType.equalsIgnoreCase("euclidian")){
    	 Metric<double[],Double> metric = new euclidian();
         return metric;
      }
     
      return null;
  }


}