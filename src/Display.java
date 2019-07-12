import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

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
	
	public static void lineProfile(BufferedImage image, int lineLoc, int start, int end) {
		
		
		
		try {
			PrintWriter rgb = new PrintWriter("Cu-Pt day after2 RGB.txt");
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

class ImageComponent extends JComponent {
	private static final long serialVersionUID = 1L;
    private BufferedImage image;
    
    public int imageWidth;
	public int imageHeight;
	public int lineLoc;
	
	public int start;
	public int end;
    
    public ImageComponent() {
        try {
            File image2 = new File("C:\\Users\\legit\\eclipse-workspace\\RGB Scanner\\src\\Cu-Pt day after2.jpeg");
            image = ImageIO.read(image2);
            imageWidth = image.getWidth();
            imageHeight = image.getHeight();
            lineLoc = (int) Math.ceil((double) imageHeight / 2.0);
            start = 0;
            end = imageWidth;
            Display.lineProfile(image, lineLoc, start, end);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void paintComponent (Graphics g) {
        if(image == null) return;
        
        g.drawImage(image, 0, 0, this);
        g.drawLine(start, lineLoc, end, lineLoc);
    }
}

class Pixel {
	private int red;
	private int blue;
	private int green;
	
	private int x;
	private int y;
	
	public Pixel(int rgb, int x, int y) {
		this.red = (rgb >> 16) & 0xff;
		this.blue = (rgb >> 8) & 0xff;
		this.green = (rgb) & 0xff;
		
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
