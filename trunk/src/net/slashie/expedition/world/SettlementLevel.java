package net.slashie.expedition.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.slashie.expedition.domain.Store;
import net.slashie.utils.Position;

@SuppressWarnings("serial")
public class SettlementLevel extends ExpeditionMicroLevel {
	private List<Store> stores = new ArrayList<Store>();
	private Map<String, Store> storesMap = new HashMap<String, Store>(); 
	
	public void addStore(Position position, Store store){
		storesMap.put(position.toString(), store);
		stores.add(store);
	}
	
	public Store getStoreAt(Position position){
		return storesMap.get(position.toString());
	}
	
	public void restockStores(){
		for (Store store: stores){
			store.restock();
		}
	}
}
