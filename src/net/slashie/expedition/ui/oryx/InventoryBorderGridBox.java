package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.awt.Cursor;
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
				itemWidth, gridX, gridY, box, null, ExpeditionOryxUI.BTN_SPLIT_UP, ExpeditionOryxUI.BTN_SPLIT_DOWN, ExpeditionOryxUI.HAND_CURSOR);
	}

	public void draw(int boxX, int boxY, Image box) {
		this.boxX = boxX;
		this.boxY = boxY;
		this.box = box;
		super.draw(false);
		si.drawImage(ExpeditionOryxUI.UI_WIDGETS_LAYER, boxX, boxY, box);
		si.commitLayer(ExpeditionOryxUI.UI_WIDGETS_LAYER);
	}
	
	@Override
	public void draw(boolean refresh) {
		draw(boxX, boxY, box);
	}
	
	@Override
	public int getDrawingLayer() {
		return ExpeditionOryxUI.UI_WIDGETS_LAYER;
	}
	
	@Override
	protected Cursor getDefaultCursor() {
		return ((ExpeditionOryxUI)ExpeditionOryxUI.getUI()).POINTER_CURSOR;
	}
	
	@Override
	protected Cursor getHandCursor() {
		return ((ExpeditionOryxUI)ExpeditionOryxUI.getUI()).HAND_CURSOR;
	}
}
