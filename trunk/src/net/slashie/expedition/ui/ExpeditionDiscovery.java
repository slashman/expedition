package net.slashie.expedition.ui;

public class ExpeditionDiscovery {
	public enum Discovery{
		Ruin("RUIN"),
		Plant("PLANT");
		
		String discovery;
		private Discovery(String discovery){
			this.discovery = discovery;
		}
	}
	
	private String discoveryText;
	private Discovery discoveryType;
	private String time;
	private int fame;
	private boolean reported;
	
	public ExpeditionDiscovery(String discoveryText, Discovery discoveryType, String time, int fame) {
		this.discoveryText = discoveryText;
		this.discoveryType = discoveryType;
		this.time = time;
		this.fame = fame;
		this.reported = false;
	}
	
	public String getDiscoveryText() {
		return discoveryText;
	}
	public void setDiscoveryText(String discoveryText) {
		this.discoveryText = discoveryText;
	}
	public Discovery getDiscoveryType() {
		return discoveryType;
	}
	public void setDiscoveryType(Discovery discoveryType) {
		this.discoveryType = discoveryType;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getFame() {
		return fame;
	}
	public void setFame(int fame) {
		this.fame = fame;
	}
	public boolean isReported() {
		return reported;
	}
	public void setReported(boolean reported) {
		this.reported = reported;
	}
}
