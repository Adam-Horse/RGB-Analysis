package user_interface;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import imaging.Bound;
import imaging.Pixel;

public class Display {
	static ArrayList<Pixel> pixels = new ArrayList<Pixel>();
	
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				
				ImageFrame frame = new ImageFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
				
			}
			
		});
		
		
	}
	
	public static void lineProfile(BufferedImage image, int lineLoc, int start, int end, String name) {
		
		try {
			PrintWriter rgb = new PrintWriter(name + " RGB.txt");
			int i = 0;
			while (i + start < end) {
				pixels.add(new Pixel(image.getRGB(i + start, lineLoc), i + start, lineLoc));
				System.out.println("R: " + pixels.get(i).getRed() +
						   " | G: " + pixels.get(i).getGreen() +
						   " | B: " + pixels.get(i).getBlue() +
						   " | at (" + pixels.get(i).getX() + ", " + pixels.get(i).getY() + ")");
				rgb.println(pixels.get(i).getX() + ";" +
						    pixels.get(i).getY() + ";" +
						    pixels.get(i).getRed() + ";" +
						    pixels.get(i).getBlue() + ";" +
						    pixels.get(i).getGreen());
				i++;
			}
			rgb.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class ImageFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ImageFrame() {
		
		ImageComponent component = new ImageComponent();
		
		setTitle("RGB Test");
		setSize(component.imageWidth + 16, component.imageHeight + 39);
		
		add(component);
	}
}

class ImageComponent extends JPanel {
	private static final long serialVersionUID = 1L;
    private BufferedImage image;
    
    public int imageWidth;
	public int imageHeight;
	public int lineLoc;
	
	public int start;
	public int end;
    
	public String imageName = "Cu-Pt day after";
	
	private Bound leftBound;
	private Bound rightBound;
	private Line2D profileLine;
	
    public ImageComponent() {
        try {
            File image2 = new File("src\\" + imageName + ".jpeg");
            image = ImageIO.read(image2);
            imageWidth = image.getWidth();
            imageHeight = image.getHeight();
            lineLoc = (int) Math.ceil((double) imageHeight / 2.0);
            start = 0;
            end = imageWidth;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        
        //TODO Left-click = select, Right-click = deselect
        MouseAdapter ma = new MouseAdapter() {
        	private Point clickPoint;
        	private Point currentPoint;
        	private double startRadius = 5;
        	
        	@Override
        	public void mouseMoved(MouseEvent e) {
        		currentPoint = e.getPoint();
        		
        		if (leftBound == null) {
	       			leftBound = new Bound(currentPoint.getX(), 0, currentPoint.getX(), image.getHeight(), false);
	       			leftBound.setStart(currentPoint, startRadius);
	       		} else if (leftBound.isSet() == false) {
	        		leftBound.setLine(currentPoint.getX(), 0, currentPoint.getX(), image.getHeight());
	        		leftBound.setStart(currentPoint, startRadius);
	        		repaint();
	        	}
        		
        		if (leftBound.isSet() == true) {
	        		if (rightBound == null) {
		       			rightBound = new Bound(currentPoint.getX(), 0, currentPoint.getX(), image.getHeight(), false);
		       		} else if (rightBound.isSet() == false) {
		        		rightBound.setLine(currentPoint.getX(), 0, currentPoint.getX(), image.getHeight());
		        		repaint();
		        	}
        		}
        		
        	}
        	
        	public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                	if (leftBound.isSet() == false) {
	                	leftBound.setState(true);
	                	clickPoint = e.getPoint();
	                	leftBound.setStart(clickPoint, startRadius);
                	} else if (leftBound.isSet() == true) {
                		rightBound.setState(true);
                		profileLine = new Line2D.Double(clickPoint.getX(), clickPoint.getY(), rightBound.getX1(), clickPoint.getY());
                		repaint();
                		Display.lineProfile(image, (int) leftBound.getStart().getCenterY(),
                								   (int) leftBound.getStart().getCenterX(),
                								   (int) rightBound.getX1(), imageName);
                		System.out.println((int) profileLine.getX1());
                	}
                }
            }
        	
        };
        
        addMouseListener(ma);
        addMouseMotionListener(ma);
        
    }
    public void paintComponent (Graphics g) {
    	super.paintComponent(g);
        if(image == null) return;
        
        g.drawImage(image, 0, 0, this);
        g.setColor(Color.YELLOW);
        //g.drawLine(start, lineLoc, end, lineLoc);
        
        
        if (leftBound != null) {
            g.setColor(UIManager.getColor("List.selectionBackground"));
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.SrcOver.derive(0.5f));
            g2d.draw(leftBound);
            g2d.dispose();
            g2d = (Graphics2D) g.create();
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(3));
            g2d.draw(leftBound);
            g2d.setColor(Color.WHITE);
            g2d.fill(leftBound.getStart());
            g2d.dispose();
            g2d.draw(leftBound.getStart());
            g2d.dispose();
        }
        
        if (rightBound != null && leftBound.isSet() == true) {
            g.setColor(UIManager.getColor("List.selectionBackground"));
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.SrcOver.derive(0.5f));
            g2d.draw(rightBound);
            g2d.dispose();
            g2d = (Graphics2D) g.create();
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(3));
            g2d.draw(rightBound);
            g2d.dispose();
        }
        
        if (profileLine != null) {
            g.setColor(UIManager.getColor("List.selectionBackground"));
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.SrcOver.derive(0.5f));
            g2d.draw(profileLine);
            g2d.dispose();
            g2d = (Graphics2D) g.create();
            g2d.setColor(Color.YELLOW);
            g2d.draw(profileLine);
            g2d.dispose();
        }
        
    }
}

