package gestionBDD;

public class ElementEnregistreMap {
	
	private String nom;
	private String prenom;
	private String etat;
	
	public ElementEnregistreMap(String a, String b, String c) {
		nom =a;
		prenom=b;
		etat=c;
	}
	
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getPrenom() {
		return prenom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	public String getEtat() {
		return etat;
	}
	public void setEtat(String etat) {
		this.etat = etat;
	}	
	
	@Override
	public String toString() {
		String str = "";
		str = str+nom+","+prenom+","+etat;
		return str;
	}
	
	public ElementEnregistreMap() {
		this("","","");
	}
}
