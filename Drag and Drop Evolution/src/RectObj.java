
public class RectObj {
	float x, y, width, height, topX, topY;
	 
	RectObj(float x, float y, float width, float height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		topX = x + width;
		topY = y + height;
	}
}
