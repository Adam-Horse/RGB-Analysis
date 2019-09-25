package imaging;

public class Pixel {
	private int red;
	private int blue;
	private int green;
	
	private int x;
	private int y;
	
	public Pixel(int rgb, int x, int y) {
		this.red = (rgb >> 16) & 0xff;
		this.green = (rgb >> 8) & 0xff;
		this.blue = (rgb) & 0xff;
		
		this.x = x;
		this.y = y;
	}
	
	public int getRed() {
		return red;
	}
	
	public int getBlue() {
		return blue;
	}
	
	public int getGreen() {
		return green;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
}
