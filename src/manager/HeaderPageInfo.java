package manager;

import java.util.ArrayList;
import java.util.List;

public class HeaderPageInfo {
	
	private int nbPagesDeDonnees;
	private List<Info> infos;
	
	
	public int getNbPagesDeDonnees() {
		return nbPagesDeDonnees;
	}
	public void setNbPagesDeDonnees(int nbPagesDeDonnees) {
		this.nbPagesDeDonnees = nbPagesDeDonnees;
	}
	public List<Info> getInfos() {
		return infos;
	}
	public void setInfos(List<Info> infos) {
		this.infos = infos;
	}
	public HeaderPageInfo(int nbPagesDeDonnees, List<Info> infos) {
		
		this.nbPagesDeDonnees = nbPagesDeDonnees;
		this.infos = infos;
	}

	public HeaderPageInfo() {
		infos = new ArrayList<Info>();
		nbPagesDeDonnees = 0;
	}
	
	public void incrementNbPagesDeDonnees(int arg){
		this.nbPagesDeDonnees+=arg;
	}
	
	public void addInfo(int idxPages, int nbSlotsAvailable) {
		addInfo(new Info(idxPages,nbSlotsAvailable));
	}
	
	public void addInfo(Info info) {
		infos.add(info);
	}
	
}
