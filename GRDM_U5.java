package gdm;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Opens an image window and adds a panel below the image
 */
public class GRDM_U5 implements PlugIn {

	ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;

	String[] items = { "Original", "Filter 1", "Filter 2", "Filter 3" };

	public static void main(String args[]) {

		IJ.open("C:\\\\Users/laris/Pictures/sail.jpg");
		// IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");

		GRDM_U5 pw = new GRDM_U5();
		pw.imp = IJ.getImage();
		pw.run("");
	}

	public void run(String arg) {
		if (imp == null)
			imp = WindowManager.getCurrentImage();
		if (imp == null) {
			return;
		}
		CustomCanvas cc = new CustomCanvas(imp);

		storePixelValues(imp.getProcessor());

		new CustomWindow(imp, cc);
	}

	private void storePixelValues(ImageProcessor ip) {
		width = ip.getWidth();
		height = ip.getHeight();

		origPixels = ((int[]) ip.getPixels()).clone();
	}

	class CustomCanvas extends ImageCanvas {

		CustomCanvas(ImagePlus imp) {
			super(imp);
		}

	} // CustomCanvas inner class

	class CustomWindow extends ImageWindow implements ItemListener {

		private String method;

		CustomWindow(ImagePlus imp, ImageCanvas ic) {
			super(imp, ic);
			addPanel();
		}

		void addPanel() {
			// JPanel panel = new JPanel();
			Panel panel = new Panel();

			JComboBox cb = new JComboBox(items);
			panel.add(cb);
			cb.addItemListener(this);

			add(panel);
			pack();
		}

		public void itemStateChanged(ItemEvent evt) {

			// Get the affected item
			Object item = evt.getItem();

			if (evt.getStateChange() == ItemEvent.SELECTED) {
				System.out.println("Selected: " + item.toString());
				method = item.toString();
				changePixelValues(imp.getProcessor());
				imp.updateAndDraw();
			}

		}

		private void changePixelValues(ImageProcessor ip) {
			// Array zum Zurückschreiben der Pixelwerte
			int[] pixels = (int[]) ip.getPixels();
			
			if (method.equals("Original")) {
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;

						pixels[pos] = origPixels[pos];
					}
				}
			}

			if (method.equals("Filter 1")) {
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						int kernel[][] = getKernel(x, y);
						int rn = (kernel[0][0]+kernel[1][0]+kernel[2][0]
								 +kernel[3][0]+kernel[4][0]+kernel[5][0]
								 +kernel[6][0]+kernel[7][0]+kernel[8][0])/9;
						
						 int gn =(kernel[0][1]+kernel[1][1]+kernel[2][1]
								 +kernel[3][1]+kernel[4][1]+kernel[5][1]
								 +kernel[6][1]+kernel[7][1]+kernel[8][1])/9;
						
						 int bn =(kernel[0][2]+kernel[1][2]+kernel[2][2]
								 +kernel[3][2]+kernel[4][2]+kernel[5][2]
								 +kernel[6][2]+kernel[7][2]+kernel[8][2])/9;
						

						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
					}
				}
			}

			if (method.equals("Filter 2")) {
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						 int kernel[][] = getKernel(x, y);
//						 int rn = kernel[0][0]*-1+kernel[1][0]*-2+kernel[2][0]*-1
//								 +kernel[3][0]*-2+kernel[4][0]*12+kernel[5][0]*-2
//								 +kernel[6][0]*-1+kernel[7][0]*-2+kernel[8][0]*-1;
//						
//						 int gn = kernel[0][1]*-1+kernel[1][1]*-2+kernel[2][1]*-1
//								 +kernel[3][1]*-2+kernel[4][1]*12+kernel[5][1]*-2
//								 +kernel[6][1]*-1+kernel[7][1]*-2+kernel[8][1]*-1;
//						
//						 int bn = kernel[0][2]*-1+kernel[1][2]*-2+kernel[2][2]*-1
//								 +kernel[3][2]*-2+kernel[4][2]*12+kernel[5][2]*-2
//								 +kernel[6][2]*-1+kernel[7][2]*-2+kernel[8][2]*-1;
						int rn = kernel[1][0] * -1 + kernel[3][0] * -1 + kernel[4][0] * 4 + kernel[5][0] * -1 + kernel[7][0] * -1;
						int gn = kernel[1][1] * -1 + kernel[3][1] * -1 + kernel[4][1] * 4 + kernel[5][1] * -1 + kernel[7][1] * -1;
						int bn = kernel[1][2] * -1 + kernel[3][2] * -1 + kernel[4][2] * 4 + kernel[5][2] * -1 + kernel[7][2] * -1;
						
						if(rn>255)rn=255;
						if(gn>255)gn=255;
						if(bn>255)bn=255;
						if(rn>255)rn=255;
						if(gn>255)gn=255;
						if(bn>255)bn=255;
						if(rn<0)rn=0;
						if(gn<0)gn=0;
						if(bn<0)bn=0;

						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;

					}
				}
			}

			if (method.equals("Filter 3")) {
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						int kernel[][] = getKernel(x, y);

						int rn =  -1 * kernel[0][0]/9 + -1 * kernel[1][0]/9 + -1 * kernel[2][0]/9
								+ -1 * kernel[3][0]/9 + 17 * kernel[4][0]/9 + -1 * kernel[5][0]/9 
								+ -1 * kernel[6][0]/9 + -1 * kernel[7][0]/9 + -1 * kernel[8][0]/9;
						
						int gn =  -1 * kernel[0][1]/9 + -1 * kernel[1][1]/9 + -1 * kernel[2][1]/9
								+ -1 * kernel[3][1]/9 + 17 * kernel[4][1]/9 + -1 * kernel[5][1]/9 
								+ -1 * kernel[6][1]/9 + -1 * kernel[7][1]/9 + -1 * kernel[8][1]/9;
						
						int bn =  -1 * kernel[0][2]/9 + -1 * kernel[1][2]/9 + -1 * kernel[2][2]/9
								+ -1 * kernel[3][2]/9 + 17 * kernel[4][2]/9 + -1 * kernel[5][2]/9 
								+ -1 * kernel[6][2]/9 + -1 * kernel[7][2]/9 + -1 * kernel[8][2]/9;
						
						if(rn>255)rn=255;
						if(gn>255)gn=255;
						if(bn>255)bn=255;
						if(rn<0)rn=0;
						if(gn<0)gn=0;
						if(bn<0)bn=0;

						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
					}
				}
			}
		}

		public int[] getRGB(int rgb) {
			int r = (rgb >> 16) & 0xff;
			int g = (rgb >> 8) & 0xff;
			int b = rgb & 0xff;
			int[] result = { r, g, b };
			return result;
		}

		public int[][] getKernel(int x, int y) {
			int lorgb = 0, morgb = 0, rorgb = 0, lmrgb = 0, mmrgb = 0, rmrgb = 0, lurgb = 0, murgb = 0, rurgb = 0;

			if ((y - 1) < 0 && (x - 1) < 0)
				lorgb = origPixels[(y) * width + x];
			else if ((y - 1) < 0 && (x + 1) == width)
				rorgb = origPixels[(y) * width + x];
			else if ((y + 1) == height && (x - 1) < 0)
				lurgb = origPixels[(y) * width + x];
			else if ((y + 1) == height && (x + 1) == width)
				rurgb = origPixels[(y) * width + x];

			else if ((y - 1) < 0) {
				lorgb = origPixels[(y) * width + x - 1];
				morgb = origPixels[(y) * width + x];
				rorgb = origPixels[(y) * width + x - 1];
			} else if ((y + 1) == height) {
				lurgb = origPixels[(y) * width + x - 1];
				murgb = origPixels[(y) * width + x];
				rurgb = origPixels[(y) * width + x - 1];
			} else if ((x - 1) < 0) {
				lorgb = origPixels[(y) * width + x];
				lmrgb = origPixels[(y) * width + x];
				lurgb = origPixels[(y) * width + x];
			} else if ((x + 1) == width) {
				rorgb = origPixels[(y) * width + x];
				rmrgb = origPixels[(y) * width + x];
				rurgb = origPixels[(y) * width + x];
			} else {
				lorgb = origPixels[(y - 1) * width + x - 1];
				morgb = origPixels[(y - 1) * width + x];
				rorgb = origPixels[(y - 1) * width + x + 1];
				lmrgb = origPixels[(y) * width + x - 1];
				mmrgb = origPixels[(y) * width + x];
				rmrgb = origPixels[(y) * width + x + 1];
				lurgb = origPixels[(y + 1) * width + x - 1];
				murgb = origPixels[(y + 1) * width + x];
				rurgb = origPixels[(y + 1) * width + x + 1];
			}

			int[] lo = getRGB(lorgb);
			int[] mo = getRGB(morgb);
			int[] ro = getRGB(rorgb);
			int[] lm = getRGB(lmrgb);
			int[] mm = getRGB(mmrgb);
			int[] rm = getRGB(rmrgb);
			int[] lu = getRGB(lurgb);
			int[] mu = getRGB(murgb);
			int[] ru = getRGB(rurgb);
			int[][] result = { lo, mo, ro, lm, mm, rm, lu, mu, ru };
			return result;
		}

	} // CustomWindow inner class
}