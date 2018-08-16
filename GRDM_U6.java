package gdm;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class GRDM_U6 implements PlugInFilter {
	private ImagePlus imp;

	public static void main(String args[]) {

		IJ.open("C:\\\\Users/laris/Pictures/component.jpg");

		GRDM_U6 pw = new GRDM_U6();
		pw.imp = IJ.getImage();
		ImageProcessor ip = pw.imp.getProcessor();
		pw.run(ip);

	}

	public int setup(String arg, ImagePlus imp) {
		if (arg.equals("about")) {
			showAbout();
			return DONE;
		}
		return DOES_RGB + NO_CHANGES;
		// kann RGB-Bilder und veraendert das Original nicht
	}

	public void run(ImageProcessor ip) {

		String[] dropdownmenue = { "Kopie", "Pixelwiederholung", "Bilinear" };

		GenericDialog gd = new GenericDialog("scale");
		gd.addChoice("Methode", dropdownmenue, dropdownmenue[0]);
		gd.addNumericField("Hoehe:", 500, 0);
		gd.addNumericField("Breite:", 400, 0);

		gd.showDialog();

		int height_n = (int) gd.getNextNumber(); // _n fuer das neue skalierte Bild
		int width_n = (int) gd.getNextNumber();
		String method = gd.getNextChoice();

		int width = ip.getWidth(); // Breite bestimmen
		int height = ip.getHeight(); // Hoehe bestimmen

		// height_n = height;
		// width_n = width;

		ImagePlus neu = NewImage.createRGBImage("Skaliertes Bild", width_n, height_n, 1, NewImage.FILL_BLACK);

		ImageProcessor ip_n = neu.getProcessor();

		int[] pix = (int[]) ip.getPixels();
		int[] pix_n = (int[]) ip_n.getPixels();

		// Schleife ueber das neue Bild
		if (method.equals("Kopie")) {
			for (int y_n = 0; y_n < height_n; y_n++) {
				for (int x_n = 0; x_n < width_n; x_n++) {

					int y = y_n;
					int x = x_n;

					if (y < height && x < width) {
						int pos_n = y_n * width_n + x_n;
						int pos = y * width + x;

						pix_n[pos_n] = pix[pos];
					}
				}
			}
		}

		if (method.equals("Pixelwiederholung")) {
			double XGitter = ((double) width - 1) / ((double) width_n - 1);
			double YGitter = ((double) height - 1) / ((double) height_n - 1);
			for (int y_n = 0; y_n < height_n; y_n++) {
				for (int x_n = 0; x_n < width_n; x_n++) {
					int pos_n = y_n * width_n + x_n;
					int x = (int) Math.round(XGitter * x_n);
					int y = (int) Math.round(YGitter * y_n);

					int pos = y * width + x;
					pix_n[pos_n] = pix[pos];
				}
			}

		}

		if (method.equals("Bilinear")) {
			double XGitter = ((double) width - 1) / ((double) width_n - 1);
			double YGitter = ((double) height - 1) / ((double) height_n - 1);
			for (int y_n = 0; y_n < height_n; y_n++) {
				for (int x_n = 0; x_n < width_n; x_n++) {
					int pos_n = y_n * width_n + x_n;
					int Ax = (int) Math.round(XGitter * x_n);
					int Ay = (int) Math.round(YGitter * y_n);
					double h = XGitter * x_n - Ax;
					double v = YGitter * y_n - Ay;

					int A = pix[Ay * width + Ax];
					int B, C, D;
					
					if (Ax == width - 1)
						B = A;
					else
						B = pix[Ay * width + Ax + 1];
					if (Ay == height - 1)
						C = A;
					else
						C = pix[(Ay + 1) * width + Ax];
					if (Ax == width - 1 && Ay == height - 1)
						D = A;
					else if (Ax == width - 1 && Ay != height - 1)
						D = C;
					else if (Ax != width - 1 && Ay == height - 1)
						D = B;
					else
						D = pix[(Ay + 1) * width + Ax + 1];

					int rA = (A >> 16) & 0xff;
					int gA = (A >> 8) & 0xff;
					int bA = A & 0xff;
					int rB = (B >> 16) & 0xff;
					int gB = (B >> 8) & 0xff;
					int bB = B & 0xff;
					int rC = (B >> 16) & 0xff;
					int gC = (B >> 8) & 0xff;
					int bC = C & 0xff;
					int rD = (C >> 16) & 0xff;
					int gD = (D >> 8) & 0xff;
					int bD = D & 0xff;

					int rn = (int) Math
							.round(rA * (1 - h) * (1 - v) + rB * h * (1 - v) + rC * (1 - h) * v + rD * h * v);
					int gn = (int) Math
							.round(gA * (1 - h) * (1 - v) + gB * h * (1 - v) + gC * (1 - h) * v + gD * h * v);
					int bn = (int) Math
							.round(bA * (1 - h) * (1 - v) + bB * h * (1 - v) + bC * (1 - h) * v + bD * h * v);

					if (rn > 255) rn = 255;
					if (gn > 255) gn = 255;
					if (bn > 255) bn = 255;
					if (rn > 255) rn = 255;
					if (gn > 255) gn = 255;
					if (bn > 255) bn = 255;
					if (rn < 0)   rn = 0;
					if (gn < 0)   gn = 0;
					if (bn < 0)   bn = 0;

					pix_n[pos_n] = (0xff << 24) | (rn << 16) | (gn << 8) | (bn);
				}
			}

		}

		// neues Bild anzeigen
		neu.show();
		neu.updateAndDraw();

	}

	void showAbout() {
		IJ.showMessage("");
	}
}
