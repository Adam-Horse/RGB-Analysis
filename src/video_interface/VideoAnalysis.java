package video_interface;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import imaging.Bound;
import imaging.Pixel;

public class VideoAnalysis {

	public static void main(String[] args) {

		final JFileChooser opener = new JFileChooser();
		File[] images = null;
		
		//TODO set save location
		String saveLocation = null;

		opener.setMultiSelectionEnabled(false);
		opener.setDialogTitle("Select Directory Location");
		opener.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		opener.setCurrentDirectory(new File("C:\\Users\\legit\\Desktop\\"));

		//
		// disable the "All files" option.
		//
		opener.setAcceptAllFileFilterUsed(false);
		//
		if (opener.showOpenDialog(opener) == JFileChooser.APPROVE_OPTION) {
			images = opener.getSelectedFile().listFiles();
			System.out.println("Opening: " + opener.getSelectedFile().getAbsolutePath());
			System.out.println("Total amount of files: " + images.length);
			// TODO create save function
			// save(images, saveLocation, "Red Dyes", "Red Dye");
		} else {
			System.out.println("No Selection... Canceling script.");
		}
		
		
		
		final JFileChooser saver = new JFileChooser();
		ImageFrame frame;
		
		saver.setMultiSelectionEnabled(false);
		saver.setDialogTitle("Select Save Location");
		saver.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		//
		// disable the "All files" option.
		//
		saver.setAcceptAllFileFilterUsed(false);
		//
		if (saver.showOpenDialog(saver) == JFileChooser.APPROVE_OPTION) {
			saveLocation = saver.getSelectedFile().getAbsolutePath();
			//TODO Ask save location AFTER image analysis
			frame = new ImageFrame(images, saveLocation);
			
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		} else {
			System.out.println("No Selection... Canceling script.");
		}
		
		

	}

	
}

class ImageFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ImageComponent imgComponent;

	public ImageFrame(File[] image, String saveLoc) {
		
		
		imgComponent = new ImageComponent(image, saveLoc);
		
		// menuComponent = new ButtonMenu(imgComponent.getWidth());

		setTitle("RGB Test");
		setSize(imgComponent.getWidth(), imgComponent.getHeight());
		// System.out.println("" + imgComponent.getHeight());
		// TODO Use border layout, research later
		// Its not showing the whole image...
		// add(menuComponent, BorderLayout.PAGE_END);
		add(imgComponent);

	}

	// Saves image to location
	public void save(String saveLoc) {

		BufferedImage bImg = new BufferedImage(imgComponent.getWidth(), imgComponent.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D cg = bImg.createGraphics();
		imgComponent.paintAll(cg);

		try {
			if (ImageIO.write(bImg, "png", new File(saveLoc + "\\" + imgComponent.getName() + " Bounds.png"))) {
				System.out.println("Saved image as: " + imgComponent.getName() + ".png");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

class ImageComponent extends JPanel {
	private static final long serialVersionUID = 1L;
	BufferedImage firstImage;

	public int imageWidth;
	public int imageHeight;

	public String imageName;

	private Bound leftBound;
	private Bound rightBound;
	private Line2D profileLine;
	
	//line profile locations for EVERY single image in the chosen directory
	private int universialStart;
	private int universialEnd;
	private int universialY;
	

	public ImageComponent(File[] files, String saveLoc) {

		try {
			firstImage = ImageIO.read(files[59]);
			imageName = files[59].getName().substring(0, files[59].getName().length() - 4);
			imageWidth = firstImage.getWidth();
			imageHeight = firstImage.getHeight();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		setSize(imageWidth + 16, imageHeight + 39);
		
		

		// TODO Left-click = select, Right-click = reselect
		MouseAdapter ma = new MouseAdapter() {
			private Point leftStart;
			private Point currentPoint;
			private double startRadius = 5;

			@Override
			public void mouseMoved(MouseEvent e) {
				currentPoint = e.getPoint();

				if (leftBound == null) {
					// Establishes left bound
					leftBound = new Bound(currentPoint.getX(), 0, currentPoint.getX(), firstImage.getHeight(), false);
					leftBound.setStart(currentPoint, startRadius);
				} else if (leftBound.isSet() == false) {
					leftBound.setLine(currentPoint.getX(), 0, currentPoint.getX(), firstImage.getHeight());
					leftBound.setStart(currentPoint, startRadius);
					repaint();
				}

				if (leftBound.isSet() == true) {
					if (rightBound == null) {
						// Establishes right bound
						rightBound = new Bound(currentPoint.getX(), 0, currentPoint.getX(), firstImage.getHeight(), false);
						profileLine = new Line2D.Double(leftStart.getX(), leftStart.getY(), currentPoint.getX(),
								leftStart.getY());
					} else if (rightBound.isSet() == false) {
						rightBound.setLine(currentPoint.getX(), 0, currentPoint.getX(), firstImage.getHeight());
						profileLine = new Line2D.Double(leftStart.getX(), leftStart.getY(), currentPoint.getX(),
								leftStart.getY());
						repaint();
					}
				}

			}
			
			//This is when shit goes down
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (leftBound.isSet() == false) {
						// This stops the left bound from moving
						leftBound.setState(true);
						leftStart = e.getPoint();
						leftBound.setStart(leftStart, startRadius);
					} else if (leftBound.isSet() == true) {
						// This stops the right bound from moving
						rightBound.setState(true);
						// This is the white middle line
						profileLine = new Line2D.Double(leftStart.getX(),
														leftStart.getY(),
														rightBound.getX1(),
														leftStart.getY());
						repaint();
						//line profile ALL the images!
						universialY = (int) leftBound.getStart().getCenterY();
						universialStart = (int) leftBound.getStart().getCenterX();
						universialEnd = (int) rightBound.getX1();
						
						System.out.println("Start pixel: " + universialStart);
						System.out.println("End Pixel: " + universialEnd);
						System.out.println("Total pixels: " + (universialEnd - universialStart));
						System.out.println("Line Height: " + universialY);
						
						System.out.println();
						System.out.println("Saving at: " + saveLoc);
						
						for (File f : files) {
							try {
								lineProfile(ImageIO.read(f),
										universialY,
										universialStart,
										universialEnd,
										f.getName(),
										saveLoc);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
						}
						
					}
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					if (leftBound.isSet() == true) {
						// Resets left bound if already set
						leftBound.setState(false);
						leftBound.setLine(currentPoint.getX(), 0, currentPoint.getX(), firstImage.getHeight());
						leftBound.setStart(e.getPoint(), startRadius);
						leftStart = null;
						// Deletes other lines
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
	
	public int getUniversialY() {
		return universialY;
	}
	
	public int getUniversialStart() {
		return universialStart;
	}
	
	public int getUniversialEnd() {
		return universialEnd;
	}
	
	// Prints RGB data and saves it as csv file in new dir
	// TODO make everything print to console correctly
	public void lineProfile(BufferedImage image,
							int lineLoc,
							int start,
							int end,
							String fName,
							String saveLoc) {
		
		ArrayList<Pixel> pixels = new ArrayList<Pixel>();
		String name = fName.substring(0, fName.length() - 4);
		
		// Instead of printing to console, display some info in the UI
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(saveLoc + "\\" + name + ".csv"));
			CSVPrinter csvPrinter = new CSVPrinter(writer,
					CSVFormat.DEFAULT.withHeader("Red", "Green", "Blue", "X", "Y"));
			int i = 0;
			while (i + start < end) {
				pixels.add(new Pixel(image.getRGB(i + start, lineLoc), i + start, lineLoc));
				// print data in console
//				System.out.println("R: " + pixels.get(i).getRed() +
//								   " | G: " + pixels.get(i).getGreen() +
//								   " | B: " + pixels.get(i).getBlue() +
//								   " | at (" + pixels.get(i).getX() +
//								   ", " + pixels.get(i).getY() + ")");

				csvPrinter.printRecord(pixels.get(i).getRed(),
									   pixels.get(i).getGreen(),
									   pixels.get(i).getBlue(),
									   pixels.get(i).getX(),
									   pixels.get(i).getY());
				i++;

			}
			csvPrinter.flush();
			csvPrinter.close();
			// Change to not use static

			// Don't change anything before it closes!
			// rgb.close();
			System.out.println("Saved text file as: " + name + " RGB.csv");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	

	public String getName() {
		return imageName;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (firstImage == null)
			return;

		g.drawImage(firstImage, 0, 0, this);
		g.setColor(Color.YELLOW);
		// g.drawLine(start, lineLoc, end, lineLoc);

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
