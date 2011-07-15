package net.slashie.expedition.game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.slashie.expedition.domain.Expedition;
import net.slashie.serf.game.GameSessionInfo;
import net.slashie.utils.DESEncrypter;
import net.slashie.utils.FileUtil;
import net.slashie.utils.SerializableChecker;


public class GameFiles {

	public static void saveMemorialFile(Expedition player){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			String now = sdf.format(new Date());
			BufferedWriter fileWriter = FileUtil.getWriter("memorials/"+player.getName()+"("+now+").life");
			GameSessionInfo gsi = player.getGameSessionInfo();
			fileWriter.close();
		} catch (IOException ioe){
			ExpeditionGame.crash("Error writing the memorial file", ioe);
		}
		
	}
	
	public static void saveGame(ExpeditionGame g, Expedition p){
		String filename = "savegame/"+p.getName()+".sav";
		p.setSelector(null);
		try {
			SerializableChecker sc = new SerializableChecker();
			sc.writeObject(g);
			sc.close();
			
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filename));
			os.writeObject(g);
			os.close();
			
			
		} catch (IOException ioe){
			ExpeditionGame.crash("Error saving the game", ioe);
		}
	}
	
	public static void permadeath(Expedition p){
		String filename = "savegame/"+p.getName()+".sav";
		if (FileUtil.fileExists(filename)) {
			FileUtil.deleteFile(filename);
		}
	}
	
	public static void saveChardump(Expedition player){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss");
			String now = sdf.format(new Date());
			BufferedWriter fileWriter = FileUtil.getWriter("memorials/"+player.getName()+" {Alive}("+now+").life");
			GameSessionInfo gsi = player.getGameSessionInfo();
			gsi.setDeathLevelDescription(player.getLevel().getDescription());
			fileWriter.close();
		} catch (IOException ioe){
			ExpeditionGame.crash("Error writing the chardump", ioe);
		}
	}
		
	public static String decode(String serial){
		DESEncrypter encr = new DESEncrypter("Thank you for supporting slashware interactive!");
		return encr.decrypt(serial);
	}
	
	public static String encode(String supporter, String level){
		DESEncrypter encr = new DESEncrypter("Thank you for supporting slashware interactive!");
		return encr.encrypt(supporter+","+level);
	}

	public static class LicenseInfo {
		public String licensee;
		public String licenseLevel;
	}
	
	public static LicenseInfo getLicenseInfo(){
		String registrant = null;
    	String supporterLevel = "Unregistered";
    	try {
    		BufferedReader r = FileUtil.getReader("registration.key");
    		String key = r.readLine();
    		r.close();
    		String decoded = GameFiles.decode(key);
    		registrant = decoded.split(",")[0];
    		supporterLevel = decoded.split(",")[1];
    	} catch (FileNotFoundException e) {
    		registrant = null;
		} catch (Exception e) {
			e.printStackTrace();
			registrant = null;
		}
		LicenseInfo ret = new LicenseInfo();
		ret.licensee = registrant;
		ret.licenseLevel = supporterLevel;
		return ret;
	}
	
	public static void main (String [] args){
		System.out.println(encode("-","Unregistered"));
	}
}
