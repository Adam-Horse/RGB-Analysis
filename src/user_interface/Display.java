package user_interface;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
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
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import imaging.Bound;
import imaging.Pixel;

public class Display {
	static ArrayList<Pixel> pixels = new ArrayList<Pixel>();
	static ImageFrame frame;
	
	
	public static void main(String[] args) {
		
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("./images"));
    	int returnVal = fc.showOpenDialog(fc);
    	String filePath = null;
    	if (returnVal == JFileChooser.APPROVE_OPTION) {
    		filePath = fc.getSelectedFile().getAbsolutePath();
    	} else {
    		System.out.println(("User clicked CANCEL"));
    		System.exit(1);
    	}
		
    	frame = new ImageFrame(filePath);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		
	}
	
	//Prints RGB data and saves it as text file in new dir
	//TODO make everything print to console correctly
	public static void lineProfile(BufferedImage image, int lineLoc, int start, int end, String name) {
		
		frame.save();
		
		//Instead of printing to console, display some info in the UI
		try {
			PrintWriter rgb = new PrintWriter("./Data/" + name + "/" + name + " RGB.txt");
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
			System.out.println();
			System.out.println("Start pixel: " + start);
			System.out.println("End Pixel: " + end);
			System.out.println("Total pixels: " + (end - start));
			System.out.println("Line Height: " + lineLoc);
			System.out.println();
			//Change to not use static
			
			//Don't change anything before it closes!
			rgb.close();
			System.out.println("Saved text file as: " + name + " RGB.txt");
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
	
	ImageComponent imgComponent;
	ButtonMenu menuComponent;
	
	public ImageFrame(String imagePath) {
		
		imgComponent = new ImageComponent(imagePath);
		//menuComponent = new ButtonMenu(imgComponent.getWidth());
		
		setTitle("RGB Test");
		setSize(imgComponent.getWidth(), imgComponent.getHeight());
		//System.out.println("" + imgComponent.getHeight());
		//TODO Use border layout, research later
		//Its not showing the whole image...
		//add(menuComponent, BorderLayout.PAGE_END);
		add(imgComponent);
		
		
	}
	
	//Creates dir and saves image in it
	public void save() {
    	
		if (new File("./Data/" + imgComponent.getName()).mkdirs()) {
			System.out.println("Created directory named: " + imgComponent.getName());
		}
		
    	BufferedImage bImg = new BufferedImage(imgComponent.getWidth(), imgComponent.getHeight(), BufferedImage.TYPE_INT_RGB);
    	Graphics2D cg = bImg.createGraphics();
    	imgComponent.paintAll(cg);
    	
    	try {
    		if (ImageIO.write(bImg, "png", new File("./Data/" + imgComponent.getName() + "/" + imgComponent.getName() + " Bounds.png"))) {
    			System.out.println("Saved image as: " + imgComponent.getName() + ".png");
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
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
    
	public String imageName;
	
	private Bound leftBound;
	private Bound rightBound;
	private Line2D profileLine;
	
	
    public ImageComponent(String imagePath) {
    	
    	
        try {
        	File file = new File(imagePath);
            image = ImageIO.read(file);
            imageName = file.getName().substring(0, file.getName().indexOf("."));
            imageWidth = image.getWidth();
            imageHeight = image.getHeight();
            lineLoc = (int) Math.ceil((double) imageHeight / 2.0);
            start = 0;
            end = imageWidth;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        setSize(imageWidth + 16, imageHeight + 39);
        
        //TODO Left-click = select, Right-click = deselect
        MouseAdapter ma = new MouseAdapter() {
        	private Point leftStart;
        	private Point currentPoint;
        	private double startRadius = 5;
        	
        	@Override
        	public void mouseMoved(MouseEvent e) {
        		currentPoint = e.getPoint();
        		
        		if (leftBound == null) {
        			//Establishes left bound
	       			leftBound = new Bound(currentPoint.getX(), 0, currentPoint.getX(), image.getHeight(), false);
	       			leftBound.setStart(currentPoint, startRadius);
	       		} else if (leftBound.isSet() == false) {
	        		leftBound.setLine(currentPoint.getX(), 0, currentPoint.getX(), image.getHeight());
	        		leftBound.setStart(currentPoint, startRadius);
	        		repaint();
	        	}
        		
        		if (leftBound.isSet() == true) {
	        		if (rightBound == null) {
	        			//Establishes right bound
		       			rightBound = new Bound(currentPoint.getX(), 0, currentPoint.getX(), image.getHeight(), false);
		       			profileLine = new Line2D.Double(leftStart.getX(), leftStart.getY(), currentPoint.getX(), leftStart.getY());
		       		} else if (rightBound.isSet() == false) {
		        		rightBound.setLine(currentPoint.getX(), 0, currentPoint.getX(), image.getHeight());
		        		profileLine = new Line2D.Double(leftStart.getX(), leftStart.getY(), currentPoint.getX(), leftStart.getY());
		        		repaint();
		        	}
        		}
        		
        	}
        	
        	public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                	if (leftBound.isSet() == false) {
                		//This stops the left bound from moving
	                	leftBound.setState(true);
	                	leftStart = e.getPoint();
	                	leftBound.setStart(leftStart, startRadius);
                	} else if (leftBound.isSet() == true) {
                		//This stops the right bound from moving
                		rightBound.setState(true);
                		//This is the white middle line
                		profileLine = new Line2D.Double(leftStart.getX(), leftStart.getY(), rightBound.getX1(), leftStart.getY());
                		repaint();
                		Display.lineProfile(image, (int) leftBound.getStart().getCenterY(),
                								   (int) leftBound.getStart().getCenterX(),
                								   (int) rightBound.getX1(), imageName);
                	}
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                	if (leftBound.isSet() == true) {
                		//Resets left bound if already set
                		leftBound.setState(false);
                		leftBound.setLine(currentPoint.getX(), 0, currentPoint.getX(), image.getHeight());
                		leftBound.setStart(e.getPoint(), startRadius);
                		leftStart = null;
                		//Deletes other lines
                		profileLine = null;
                		rightBound = null;
                		repaint();
                	}
                }
                
            }
        	
        };
        
        addMouseListener(ma);
        addMouseMotionListener(ma);
        
        
        
    }
    
    public String getName() {
    	return imageName;
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
            g2d.setColor(Color.WHITE);
            g2d.draw(profileLine);
            g2d.dispose();
        }
        
    }
}

class ButtonMenu extends JPanel {

	private JButton save;
	private JButton clear;
	
	public ButtonMenu(int width) {
		setSize(width, 100);

        save = new JButton("Save");
        clear = new JButton("Clear");
		
		add(save);
        add(clear);
	}
}

