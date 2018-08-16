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
public class GRDM_U3 implements PlugIn {

	ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;

	String[] items = { "Original", "Rot-Kanal", "Graustufen", "Negativ", "Bin‰rbild", "Bin‰rbild5", "Bin‰rbild10",
			"Fehlerdiffusion", "Sepia", "6Farben", "Floyd" };

	public static void main(String args[]) {

		IJ.open("C:\\\\Users/laris/Pictures/Bear.jpg");

		GRDM_U3 pw = new GRDM_U3();
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

			// Array zum Zur√ºckschreiben der Pixelwerte
			int[] pixels = (int[]) ip.getPixels();

			if (method.equals("Original")) {

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;

						pixels[pos] = origPixels[pos];
					}
				}
			}

			if (method.equals("Rot-Kanal")) {

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						int argb = origPixels[pos]; // Lesen der Originalwerte

						int r = (argb >> 16) & 0xff;
						// int g = (argb >> 8) & 0xff;
						// int b = argb & 0xff;

						int rn = r;
						int gn = 0;
						int bn = 0;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255
						// begrenzt werden

						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
					}
				}
			}

			if (method.equals("Graustufen")) {

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						int argb = origPixels[pos]; // Lesen der Originalwerte

						int r = (argb >> 16) & 0xff;
						int g = (argb >> 8) & 0xff;
						int b = argb & 0xff;

						double a[] = transformation(r, g, b);
						double Y = a[0];
						double Cb = a[1];
						double Cr = a[2];

						int rn = (int) Y;
						int gn = (int) Y;
						int bn = (int) Y;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255
						// begrenzt werden

						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
					}
				}
			}

			if (method.equals("Negativ")) {

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						int argb = origPixels[pos]; // Lesen der Originalwerte

						int r = (argb >> 16) & 0xff;
						int g = (argb >> 8) & 0xff;
						int b = argb & 0xff;

						int rn = 255 - r;
						int gn = 255 - g;
						int bn = 255 - b;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255
						// begrenzt werden

						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
					}
				}
			}

			if (method.equals("Bin‰rbild")) {

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						int argb = origPixels[pos]; // Lesen der Originalwerte

						int r = (argb >> 16) & 0xff;
						int g = (argb >> 8) & 0xff;
						int b = argb & 0xff;

						double a[] = transformation(r, g, b);
						double Y = a[0];
						int rn, gn, bn;

						if (Y < 127)
							rn = bn = gn = 0;
						else
							rn = bn = gn = 255;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255
						// begrenzt werden

						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
					}
				}
			}

			if (method.equals("Bin‰rbild5")) {

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						int argb = origPixels[pos]; // Lesen der Originalwerte

						int r = (argb >> 16) & 0xff;
						int g = (argb >> 8) & 0xff;
						int b = argb & 0xff;

						double a[] = transformation(r, g, b);
						double Y = a[0];
						int rn = 0, gn = 0, bn = 0;

						if (Y < 51)
							rn = bn = gn = 0;
						else if (Y < 102)
							rn = gn = bn = 63;
						else if (Y < 153)
							rn = gn = bn = 126;
						else if (Y < 204)
							rn = gn = bn = 189;
						else if (Y <= 255)
							rn = gn = bn = 255;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255
						// begrenzt werden

						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
					}
				}
			}

			if (method.equals("Bin‰rbild10")) {

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						int argb = origPixels[pos]; // Lesen der Originalwerte

						int r = (argb >> 16) & 0xff;
						int g = (argb >> 8) & 0xff;
						int b = argb & 0xff;

						double a[] = transformation(r, g, b);
						double Y = a[0];
						int rn = 0, gn = 0, bn = 0;

						if (Y < 25)
							rn = bn = gn = 0;
						else if (Y < 51)
							rn = gn = bn = 31;
						else if (Y < 76)
							rn = gn = bn = 62;
						else if (Y < 102)
							rn = gn = bn = 93;
						else if (Y < 153)
							rn = gn = bn = 124;
						else if (Y < 178)
							rn = gn = bn = 155;
						else if (Y < 204)
							rn = gn = bn = 187;
						else if (Y < 229)
							rn = gn = bn = 219;
						else if (Y <= 255)
							rn = gn = bn = 255;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255
						// begrenzt werden

						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
					}
				}
			}

			if (method.equals("Fehlerdiffusion")) {
				int diffusion = 0;
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						int argb = origPixels[pos]; // Lesen der Originalwerte

						int r = (argb >> 16) & 0xff;
						int g = (argb >> 8) & 0xff;
						int b = argb & 0xff;

						double a[] = transformation(r, g, b);
						double Y = a[0];
						int rn, gn, bn;
						Y = Y + diffusion;

						if (Y < 127) {
							diffusion = (int) (Y);
							rn = bn = gn = 0;
						} else {
							diffusion = (int) (Y - 255);
							rn = bn = gn = 255;
						}

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255
						// begrenzt werden

						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
					}
				}
			}

			if (method.equals("Sepia")) {
				int sepiaDepth = 20;
				int sepiaIntensity = 30;
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						int argb = origPixels[pos]; // Lesen der Originalwerte

						int r = (argb >> 16) & 0xff;
						int g = (argb >> 8) & 0xff;
						int b = argb & 0xff;

						double a[] = transformation(r, g, b);
						double Y = a[0];
						double Cb = a[1];
						double Cr = a[2];

						int rn = (int) Y;
						int gn = (int) Y;
						int bn = (int) Y;

						rn = rn + (sepiaDepth * 2);
						gn = gn + sepiaDepth;
						bn = bn - sepiaIntensity;
						if (rn > 255)
							rn = 255;
						if (gn > 255)
							gn = 255;
						if (bn > 255)
							bn = 255;
						if (rn < 0)
							rn = 0;
						if (gn < 0)
							gn = 0;
						if (bn < 0)
							bn = 0;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255
						// begrenzt werden

						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
					}
				}
			}

			if (method.equals("6Farben")) {

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						int argb = origPixels[pos]; // Lesen der Originalwerte

						int r = (argb >> 16) & 0xff;
						int g = (argb >> 8) & 0xff;
						int b = argb & 0xff;
						int rn = 0, bn = 0, gn = 0;
						if (((r > 41 && r <= 62) && g > 80) || ((r > 60 && r <= 100) && b > 100)) {
							rn = 52;// blau
							gn = 104;
							bn = 140;
						} else if ((r > 41 && r <= 62) && g < 80) {
							rn = 29;// schwarz
							gn = 33;
							bn = 32;
						}

						else if (r > 62 && r <= 94) {
							rn = 72;// grau-schwarz
							gn = 71;
							bn = 68;
						} else if (r > 94 && r <= 134) {
							rn = 116;// braun
							gn = 101;
							bn = 90;
						} else if (r > 134 && r <= 180) {
							rn = 151;// grau
							gn = 149;
							bn = 149;
						} else if (r > 180 && r <= 256) {
							rn = 208;// weiss
							gn = 206;
							bn = 206;
						}

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255
						// begrenzt werden

						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
					}
				}
			}

			if (method.equals("Floyd")) {
				int border = Math.round((255 * 3) / 2);
				// array f¸r die Fehler
				int[] errorPerPixel = new int[width * height];
				for (int i = 0; i < (width * height); i++) {
					errorPerPixel[i] = 0;
				}
				for (int y = 0; y < height; y++) {

					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						int argb = origPixels[pos]; // Lesen der Originalwerte

						int r = (argb >> 16) & 0xff;
						int g = (argb >> 8) & 0xff;
						int b = argb & 0xff;

						double a[] = transformation(r, g, b);
						double Y = a[0];
						int rn = 255, gn = 255, bn = 255;
						if ((r + g + b + errorPerPixel[pos]) < border) {
							rn = 0;
							gn = 0;
							bn = 0;
						}

						int error = ((r + g + b + errorPerPixel[pos]) - (rn + gn + bn));
						if (x < width - 1) {
							errorPerPixel[(y) * width + (x + 1)] += 7 * error / 16;
						}

						if (y < height - 1) {
							errorPerPixel[(y + 1) * width + (x - 1)] += 3 * error / 16;
							errorPerPixel[(y + 1) * width + (x)] += 5 * error / 16;

							if (x < width - 1) {
								errorPerPixel[(y + 1) * width + (x + 1)] += 1 * error / 16;
							}
						}
						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255
						// begrenzt werden

						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
					}
				}
			}

		}

		public double[] transformation(int r, int g, int b) {
			double Y = 0.299 * r + 0.587 * g + 0.114 * b;
			double Cb = -0.168736 * r - 0.331264 * g + 0.5 * b;
			double Cr = 0.5 * r - 0.418688 * g - 0.081312 * b;
			double a[] = { Y, Cb, Cr };
			return a;
		}

		public int[] retransformation(double Y, double Cb, double Cr) {
			int r = (int) (Y + 1.402 * Cr);
			int g = (int) (Y - 0.3441 * Cb - 0.7141 * Cr);
			int b = (int) (Y + 1.772 * Cb);
			int a[] = { r, g, b };
			return a;
		}

	} // CustomWindow inner class
}