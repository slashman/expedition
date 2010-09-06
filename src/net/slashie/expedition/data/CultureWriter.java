package net.slashie.expedition.data;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;

import net.slashie.util.FileUtil;
import net.slashie.utils.ImageUtils;
import net.slashie.utils.Position;

public class CultureWriter {
	private Hashtable<String, String> charmap = new Hashtable<String, String>();
	
	
	
	public CultureWriter(Hashtable<String, String> charmap) {
		super();
		this.charmap = charmap;
	}

	private String handlesinglepixel(int pixel, int xpixel, int ypixel) {
		//int alpha = (pixel >> 24) & 0xff;
		int red   = (pixel >> 16) & 0xff;
		int green = (pixel >>  8) & 0xff;
		int blue  = (pixel      ) & 0xff;
		// Deal with the pixel as necessary...
		if (red+green+blue == 0)
			return null;
		String value = charmap.get(red+","+green+","+blue);
		if (value == null){
			System.err.println(red+","+green+","+blue+" not found. ("+xpixel+","+ypixel+")");
			System.exit(-1);
		}
		
		return value;
	 }
	
	public Map<Position, String> read(String sourcefile) throws Exception{
		try {
			BufferedImage image = ImageUtils.createImage(sourcefile);
			Map<Position, String> ret = new HashMap<Position, String>();
			int w = image.getWidth();
			int h = image.getHeight();
			
			int[] pixels = new int[w * h];
			PixelGrabber pg = new PixelGrabber(image, 0, 0, w, h, pixels, 0, w);
			try {
				pg.grabPixels();
			} catch (InterruptedException e) {
				System.err.println("interrupted waiting for pixels!");
				throw e;
			}
			if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
				throw new Exception("image fetch aborted or errored");
			}
			
			for (int xpixel = 0; xpixel < w; xpixel++) {
				for (int ypixel = 0; ypixel < h; ypixel++) {
					String culture = handlesinglepixel(pixels[ypixel * w + xpixel],xpixel, ypixel);
					if (culture != null){
						ret.put(new Position(xpixel, ypixel), culture);
					}
				}
			}
			return ret;
		
			
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}
}
