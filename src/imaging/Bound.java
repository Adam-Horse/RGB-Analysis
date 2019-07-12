package imaging;

import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public class Bound extends Line2D.Double {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean isSet;
	private Ellipse2D start;
	
	public Bound(boolean state) {
		super();
		this.isSet = state;
	}
	
	public Bound(double x1, double y1, double x2, double y2, boolean state) {
		super(x1, y1, x2, y2);
		this.isSet = state;
	}
	
	public boolean isSet() {
		return isSet;
	}
	
	public void setState(boolean state) {
		isSet = state;
	}
	
	public void setStart(Point p, double r) {
		// the subtraction from r centers the circle
		start = new Ellipse2D.Double(p.getX() - r, p.getY() - r, r * 2, r * 2);
	}
	
	
	public Ellipse2D getStart() {
		return start;
	}
	
}