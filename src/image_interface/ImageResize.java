package image_interface;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class ImageResize {

	public static void main(String[] args) {

		System.out.println("Resize is running");

		final JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		
		chooser.setDialogTitle("Select Files");
		chooser.setCurrentDirectory(new File("./Unedited Images/"));
		int returnVal = chooser.showOpenDialog(chooser);
		File[] files = null;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			files = chooser.getSelectedFiles();
		} else {
			System.out.println(("User clicked CANCEL"));
			System.exit(1);
		}
		
		
		//TODO Resize files here
		final JFileChooser saver = new JFileChooser();
		ArrayList<BufferedImage> images = resize(files);
		String saveLocation = null;
		
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
			System.out.println("Save Directory: " + saveLocation);
			save(images, saveLocation, "Red Dyes", "Red Dye");
		} else {
			System.out.println("No Selection... Canceling script.");
		}
		
		//TODO Save files here!
		
		

	}

	public static ArrayList<BufferedImage> resize(File[] files) {
		ArrayList<BufferedImage> resizedFiles = new ArrayList<BufferedImage>();

		// Loops through all chosen files
		for (File f : files) {
			BufferedImage image = null;
			String imagePath = f.getAbsolutePath();
			// Opens files and reads image
			try {
				File file = new File(imagePath);
				image = ImageIO.read(file);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Scales Buffered Image and Casts the result into a Buffered Image
			BufferedImage newImage = image;
			//TODO figure out how to use AffineTransform to change images and save them accordingly
			AffineTransform at = new AffineTransform();
			
			at.scale(0.25, 0.25);
		    at.rotate(Math.PI / 2, newImage.getWidth() / 2, newImage.getHeight() / 2);
		    
		    AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		    newImage = op.filter(newImage, null);
			
			resizedFiles.add(newImage);
		}

		return resizedFiles;
	}
	
	
	
	public static void save(ArrayList<BufferedImage> images, String location, String dirName, String fileNames) {

		if (new File(location + "\\" + dirName).mkdirs()) {
			System.out.println("Created directory named: " + dirName);
		} else {
			System.out.println("Rewriting dir: " + dirName);
		}
		
		int counter = 1;
		for (BufferedImage bImg : images) {
			String fileName = fileNames + " " + counter;
			try {
				if (ImageIO.write(bImg, "png", new File(location + "\\" + dirName + "\\" + fileName + " Resized.png"))) {
					
					System.out.println("Saved image as: " + fileName + " Resized.png");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			counter++;
		}

	}
	
	
	/**
	 * Converts a given Image into a BufferedImage
	 *
	 * @param img The Image to be converted
	 * @return The converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image img) 
	{
	    if (img instanceof BufferedImage) {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}

}
