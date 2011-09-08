package net.slashie.expedition.world;

import java.util.HashMap;
import java.util.Map;

import net.slashie.expedition.domain.Store;

public class StoreFactory {

	private static StoreFactory singleton;

	public static void initializeSingleton(StoreFactory singleton){
		StoreFactory.singleton = singleton;
	}
	
	public static StoreFactory getSingleton() {
		return singleton;
	}

	private Map<String, Store> storesMap = new HashMap<String, Store>();	

	public void addStore(String id, Store store) {
		storesMap.put(id, store);
	}
	
	public Store createStore(String id){
		return storesMap.get(id).clone();
	}

}
