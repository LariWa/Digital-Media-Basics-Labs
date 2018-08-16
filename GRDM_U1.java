package gdm;

import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

//erste Uebung (elementare Bilderzeugung)

public class GRDM_U1 implements PlugIn {

	final static String[] choices = { "Schwarzes Bild", "Gelbes Bild", "Schwarz/Weiss Verlauf",
			"Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf", "Französische Fahne", "Bahamische Fahne",
			"Japanische Fahne", "Japanische Fahne mit weichen Kanten" };

	private String choice;

	public static void main(String args[]) {
		ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen
		ij.exitWhenQuitting(true);

		GRDM_U1 imageGeneration = new GRDM_U1();
		imageGeneration.run("");
	}

	public void run(String arg) {

		int width = 566; // Breite
		int height = 400; // Hoehe

		// RGB-Bild erzeugen
		ImagePlus imagePlus = NewImage.createRGBImage("GLDM_U1", width, height, 1, NewImage.FILL_BLACK);
		ImageProcessor ip = imagePlus.getProcessor();

		// Arrays fuer den Zugriff auf die Pixelwerte
		int[] pixels = (int[]) ip.getPixels();

		dialog();

		////////////////////////////////////////////////////////////////
		// Hier bitte Ihre Aenderungen / Erweiterungen

		if (choice.equals("Schwarzes Bild")) {
			generateBlackImage(width, height, pixels);
		}

		if (choice.equals("Gelbes Bild")) {
			generateYellowImage(width, height, pixels);
		}

		if (choice.equals("Französische Fahne")) {
			generateFranceFlag(width, height, pixels);
		}

		if (choice.equals("Schwarz/Weiss Verlauf")) {
			generateSWV(width, height, pixels);
		}

		if (choice.equals("Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf")) {
			generateSRSBV(width, height, pixels);
		}

		if (choice.equals("Bahamische Fahne")) {
			generateBahamas(width, height, pixels);
		}

		if (choice.equals("Japanische Fahne")) {
			generateJapanese(width, height, pixels);
		}

		if (choice.equals("Japanische Fahne mit weichen Kanten")) {
			generateJapaneseUeber(width, height, pixels);
		}

		////////////////////////////////////////////////////////////////////

		// neues Bild anzeigen
		imagePlus.show();
		imagePlus.updateAndDraw();
	}

	private void generateBlackImage(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen

				int r = 0;
				int g = 0;
				int b = 0;

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void generateYellowImage(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen

				int r = 255;
				int g = 255;
				int b = 0;

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void generateFranceFlag(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				int r, g, b;

				if (x < width / 3) {
					r = 0;
					g = 0;
					b = 255;
				}

				else if (x <= 2 * width / 3 && x >= width / 3) {
					r = 255;
					g = 255;
					b = 255;
				}

				else {
					r = 255;
					g = 0;
					b = 0;
				}

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void generateSWV(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte

		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				int r = x * 255 / width;
				int g = x * 255 / width;
				int b = x * 255 / width;

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void generateSRSBV(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte

		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				int r = x * 255 / width;
				int g = 0;
				int b = y * 255 / height;

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void generateBahamas(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte

		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				int r, g, b;

				if (y < height / 3) {
					r = 0;
					g = 139;
					b = 139;
				}

				else if (y <= 2 * height / 3 && y >= height / 3) {
					r = 255;
					g = 255;
					b = 0;
				}

				else {
					r = 0;
					g = 139;
					b = 139;
				}

				if (y <= height / 2)
					if (x < y) {
						r = 0;
						b = 0;
						g = 0;
					}
				if (y > height / 2) {
					if (x < height - y) {
						r = 0;
						b = 0;
						g = 0;
					}
				}

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void generateJapanese(int width, int height, int[] pixels) {
		int xm = width / 2;
		int ym = height / 2;

		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int g = 255;
				int b = 255;
				int r = 255;
				int t = 255;
				int pos = y * width + x; // Arrayposition bestimmen

				int d = (int) Math.sqrt(Math.pow((xm - x), 2) + Math.pow((ym - y), 2));
				if (d < width / 6) {
					g = 0;
					b = 0;

				}

				// Werte zurueckschreiben
				pixels[pos] = 0x00000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void generateJapaneseUeber(int width, int height, int[] pixels) {
		int xm = width / 2;
		int ym = height / 2;

		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int g = 255;
				int b = 255;
				int r = 255;
				int t = 255;
				int pos = y * width + x; // Arrayposition bestimmen

				int d = (int) Math.sqrt(Math.pow((xm - x), 2) + Math.pow((ym - y), 2));
				if (d < width / 6) {
					g = 0;
					b = 0;

				}
				if (d == (width / 6 - 1)) {
					g = 232;
					b = 230;
				}
				if (d == (width / 6 - 2)) {
					g = 209;
					b = 210;
				}
				if (d == (width / 6 - 3)) {
					g = 185;
					b = 185;
				}
				if (d == (width / 6 - 4)) {
					g = 162;
					b = 160;
				}
				if (d == (width / 6 - 5)) {
					g = 139;
					b = 140;
				}
				if (d == (width / 6 - 6)) {
					g = 116;
					b = 120;
				}
				if (d == (width / 6 - 7)) {
					g = 93;
					b = 90;
				}
				if (d == (width / 6 - 8)) {
					g = 70;
					b = 70;
				}
				if (d == (width / 6 - 9)) {
					g = 46;
					b = 40;
				}
				if (d == (width / 6 - 10)) {
					g = 23;
					b = 20;
				}

				/*
				 * if (d==(width/6-1)) {t= 230;} if (d==(width/6-2)) {t= 220;} if
				 * (d==(width/6-3)){t= 200;} if (d==(width/6-4)){t= 180;} if (d==(width/6-5))
				 * {t= 160;} if (d==(width/6-6)) {t= 140;} if (d==(width/6-7)) {t= 120;} if
				 * (d==(width/6-8)) {t= 100;} if (d==(width/6-9)){t= 80;} if (d==(width/6-10))
				 * {t= 60;}
				 */

				// Werte zurueckschreiben
				pixels[pos] = 0x00000000 | (t << 24) | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void dialog() {
		// Dialog fuer Auswahl der Bilderzeugung
		GenericDialog gd = new GenericDialog("Bildart");

		gd.addChoice("Bildtyp", choices, choices[0]);

		gd.showDialog(); // generiere Eingabefenster

		choice = gd.getNextChoice(); // Auswahl uebernehmen

		if (gd.wasCanceled())
			System.exit(0);
	}
}
