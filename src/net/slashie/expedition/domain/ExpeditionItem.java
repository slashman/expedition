package net.slashie.expedition.domain;

import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.serf.baseDomain.AbstractItem;
import net.slashie.serf.ui.Appearance;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.serf.ui.consoleUI.CharAppearance;

public class ExpeditionItem extends AbstractItem implements Cloneable{
	private String classifierId;
	private String description;
	private String longDescription;
	private GoodType goodType;
	private int europeValue;
	private int americaValue;
	private transient Appearance appearance;
	private String appearanceId;
	private int weight;
	private String pluralDescription;
	// Determines by how many individual units are the europe and america values calculated
	private int valuePack = 1;
	
	public int getWeight() {
		return weight;
	}

	public ExpeditionItem(String classifierId, String description, String pluralDescription, String longDescription,
			String appearanceId, int weight, GoodType goodType, int europeValue, int americaValue) {
		super(appearanceId);
		this.classifierId = classifierId;
		this.description = description;
		this.appearanceId = appearanceId;
		this.longDescription = longDescription;
		this.weight = weight;
		this.pluralDescription = pluralDescription;
		this.goodType = goodType;
		this.europeValue = europeValue;
		this.americaValue = americaValue;
	}
	
	public GoodType getGoodType() {
		return goodType;
	}

	
	
	public void setDescription(String description) {
		this.description = description;
	}

	public void setAppearanceId(String appearanceId) {
		this.appearanceId = appearanceId;
	}

	@Override
	public Appearance getAppearance() {
		if (appearance == null){
			appearance = AppearanceFactory.getAppearanceFactory().getAppearance(appearanceId);
		}
		return appearance;
	}

	@Override
	public String getDescription() {
		return description;
	}
	
	public String getPluralDescription(){
		return pluralDescription;
		
	}

	@Override
	public String getFullID() {
		return classifierId;
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public void onItemCounterZeroed(String key) {
		//Not used
	}

	/**
	 * This is a method coupled with an specific UI implementation,
	 * dependency must be undone, when there is time
	 */
	public char getMenuChar() {
		if (getAppearance() instanceof CharAppearance)
			return ((CharAppearance)getAppearance()).getChar();
		else
			return '-';
	}

	/**
	 * This is a method coupled with an specific UI implementation,
	 * dependency must be undone, when there is time
	 */
	public int getMenuColor() {
		if (getAppearance() instanceof CharAppearance)
			return ((CharAppearance)getAppearance()).getColor();
		else
			return ConsoleSystemInterface.WHITE;
	}

	public String getFullDescription() {
		return getDescription();
	}
	
	@Override
	public ExpeditionItem clone()  {
		try {
			return (ExpeditionItem) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	
	public String getGroupClassifier() {
		if (getGoodType() == GoodType.PEOPLE)
			return "UNIT_"+((ExpeditionUnit)this).getBasicId();
		else {
			return getGoodType().toString();
		}
	}

	public double getEuropeValue() {
		return (double)europeValue / (double)valuePack;
	}

	public double getAmericaValue() {
		return (double)americaValue / (double)valuePack;
	}

	public int getValuePack() {
		return valuePack;
	}

	public void setValuePack(int valuePack) {
		this.valuePack = valuePack;
	}

	public String getLongDescription() {
		return longDescription;
	}


	public String getBaseID() {
		return getFullID();
	}

}
