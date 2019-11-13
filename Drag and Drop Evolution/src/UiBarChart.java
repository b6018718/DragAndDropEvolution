import org.gicentre.utils.stat.BarChart;

public class UiBarChart {
	BarChart barChart;
	RectObj position;
	Animal an;
	
	public UiBarChart(BarChart barChart, RectObj position, Animal an) {
		this.barChart = barChart;
		this.position = position;
		this.an = an;
		
		barChart.setData(new float[] {an.normaliseLifeSpan(), an.normaliseHunger(), an.normaliseSpeed() });
		barChart.setBarLabels(new String[] {"Energy","Hunger","Speed"});
		barChart.setMinValue(0);
		barChart.setMaxValue(1);
		
		//textFont(createFont("Serif",10),10);
		barChart.showCategoryAxis(true);
		// Bar colours and appearance
		barChart.setBarGap(4);
		   
		// Bar layout
		barChart.transposeAxes(true);
	}
	
	public void show() {
		barChart.setData(new float[] {an.normaliseLifeSpan(), an.normaliseHunger(), an.normaliseSpeed() });
		barChart.draw(position.x, position.y, position.width, position.height);
	}
}
