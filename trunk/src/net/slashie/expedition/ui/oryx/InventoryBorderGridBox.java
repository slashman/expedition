package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.swing.BorderedGridBox;

public class InventoryBorderGridBox extends BorderedGridBox{
	private static final long serialVersionUID = 1L;
	private int boxX;
	private int boxY;
	private Image box;

	public InventoryBorderGridBox(BufferedImage border1, BufferedImage border2,
			BufferedImage border3, BufferedImage border4,
			SwingSystemInterface g, Color backgroundColor, Color borderIn,
			Color borderOut, int borderWidth, int outsideBound, int inBound,
			int insideBound, int itemHeight, int itemWidth, int gridX,
			int gridY, BufferedImage box) {
		super(border1, border2, border3, border4, g, backgroundColor, borderIn,
				borderOut, borderWidth, outsideBound, inBound, insideBound, itemHeight,
				itemWidth, gridX, gridY, box, null);
	}

	
	public void draw(int boxX, int boxY, Image box) {
		this.boxX = boxX;
		this.boxY = boxY;
		this.box = box;
		super.draw(false);
		si.drawImage(boxX, boxY, box);
		si.refresh();
	}
	
	@Override
	public void draw(boolean refresh) {
		draw(boxX, boxY, box);
	}
}
