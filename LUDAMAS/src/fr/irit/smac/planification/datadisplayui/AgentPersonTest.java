package fr.irit.smac.planification.datadisplayui;

public class AgentPersonTest {
	
	private String nom;
	private String prenom;
	private int age;
	
	public AgentPersonTest(String nom, String prenom, int age) {
		this.nom = nom;
		this.prenom = prenom;
		this.age = age;
	}
	
	public String getNom() {
		return nom;
	}
	
	public String getPrenom() {
		return prenom;
	}
	
	public int getAge() {
		return age;
	}
	
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public void setPrenom(String nom) {
		this.prenom = nom;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
}
