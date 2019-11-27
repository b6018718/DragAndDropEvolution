import org.gicentre.utils.stat.BarChart;

import processing.core.PApplet;

public class UiBarChart {
	BarChart barChart;
	RectObj position;
	Animal an;
	PApplet pro;
	
	public UiBarChart(RectObj position, Animal an, PApplet pro) {
		this.barChart = new BarChart(pro);
		//this.barChart = barChart;
		this.position = position;
		this.an = an;
		this.pro = pro;
		
		// Set the data into the chart
		setData();
		pro.textSize(pro.width/60);
		barChart.setAxisValuesColour(pro.color(30,30,30));
		barChart.setBarLabels(new String[] {"Lifespan","Hunger","Speed", "Egg"});
		barChart.setMinValue(0);
		barChart.setMaxValue(1);
		//textFont(createFont("Serif",10),10);
		barChart.showCategoryAxis(true);
		// Bar colours and appearance
		barChart.setBarGap(1);
		   
		// Bar layout
		barChart.transposeAxes(true);
	}
	
	public void setData() {
		barChart.setData(new float[] {an.normaliseLifeSpan(), an.normaliseHunger(), an.normaliseSpeed(), an.normaliseEggTime() });
	}
	
	public void show() {
		setData();
		pro.textSize(pro.width/60);
		barChart.draw(position.x, position.y, position.width, position.height);
	}
}
