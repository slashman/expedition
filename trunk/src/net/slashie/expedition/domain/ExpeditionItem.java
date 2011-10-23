package net.slashie.expedition.domain;

import net.slashie.expedition.item.StorageType;
import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.serf.baseDomain.AbstractItem;
import net.slashie.serf.ui.Appearance;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.serf.ui.consoleUI.CharAppearance;

@SuppressWarnings("serial")
public class ExpeditionItem extends AbstractItem implements Cloneable{
	private String classifierId;
	private String description;
	private String longDescription;
	private GoodType goodType;
	private int palosStoreValue;
	private int baseTradingValue;
	private transient Appearance appearance;
	private String appearanceId;
	private transient Appearance dialogAppearance;
	private String dialogAppearanceId;
	private int weight;
	private String pluralDescription;
	private StorageType storageType;
	
	public int getWeight() {
		return weight;
	}

	public ExpeditionItem(String classifierId, String description, String pluralDescription, String longDescription, String appearanceId, int weight, GoodType goodType, 
			int palosStoreValue, int baseTradingValue, StorageType storageType) {
		super(appearanceId);
		this.dialogAppearanceId = "DIALOG_"+appearanceId;
		this.classifierId = classifierId;
		this.description = description;
		this.appearanceId = appearanceId;
		this.longDescription = longDescription;
		this.weight = weight;
		this.pluralDescription = pluralDescription;
		this.goodType = goodType;
		this.palosStoreValue = palosStoreValue;
		this.baseTradingValue = baseTradingValue;
		this.storageType = storageType;
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
	
	public Appearance getDialogAppearance() {
		if (dialogAppearance == null){
			dialogAppearance = AppearanceFactory.getAppearanceFactory().getAppearance(dialogAppearanceId);
		}
		return dialogAppearance;
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
		return getBaseID();
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
			return "UNIT_"+getBaseID();
		else {
			return getGoodType().toString();
		}
	}

	public int getPalosStoreValue() {
		return palosStoreValue;
	}

	public int getBaseTradingValue() {
		return baseTradingValue;
	}

	public String getLongDescription() {
		return longDescription;
	}


	public String getBaseID() {
		return classifierId;
	}

	
	@Override
	public boolean equals(Object obj) {
		return obj != null && ((ExpeditionItem)obj).getFullID().equals(getFullID());
	}

	
	public StorageType getStorageType() {
		return storageType;
	}
}
