import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ConfusionMatrix {
	protected HashMap<Double,Integer> indexDict;
	protected double[][] cm;
	protected Set<Double> possibleLabels;
	
	public ConfusionMatrix(double[] predictedLabels, double[] actualLabels) {
		this.indexDict = new HashMap<Double,Integer>();
		this.possibleLabels = new HashSet<>();
		for(int i = 0; i < predictedLabels.length; i++) {
			this.possibleLabels.add(predictedLabels[i]);
			this.possibleLabels.add(actualLabels[i]);
		}
		Iterator<Double> possibleLabelsIterator = this.possibleLabels.iterator();
		Integer i = new Integer(-1);
		while(possibleLabelsIterator.hasNext()) {
			i = new Integer(i.intValue() + 1);
			this.indexDict.put(possibleLabelsIterator.next(), i);
		}
		this.cm = new double[possibleLabels.size()][possibleLabels.size()];
		this.fillMatrix(predictedLabels, actualLabels);
	}

	private void fillMatrix(double[] predictedLabels, double[] actualLabels) {
		for(int i = 0; i < predictedLabels.length; i++) {
			int predIndex = this.indexDict.get(predictedLabels[i]);
			int actualIndex = this.indexDict.get(actualLabels[i]);
			this.cm[predIndex][actualIndex] = this.cm[predIndex][actualIndex] +1;
		}
	}
	
	public void plot () {
		System.out.println("the confusion matrix:");
		Iterator<Double> possibleLabelsIterator = this.possibleLabels.iterator();
		for (int i = 0; i<this.cm.length; i++){
			System.out.println();
			System.out.print(possibleLabelsIterator.next().doubleValue() + "|| ");
			for (int j = 0; j<this.cm.length; j ++){ 
				System.out.print(this.cm[i][j] + " | ");
			}
		}
		System.out.println();
		System.out.print("-------");
		for (int i = 0; i<this.cm.length; i++){
			System.out.print("------");
		}
		possibleLabelsIterator = this.possibleLabels.iterator();
		System.out.println();
		System.out.print("    | ");
		for (int i = 0; i<this.cm.length; i++){
			System.out.print(possibleLabelsIterator.next().doubleValue() + " | ");
		}
		
		
	}
}
