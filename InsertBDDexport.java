package manageBDDimportexport;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

//import manageBDDpays.Convertisseur;

//import manageBDDpays.Requeteur;
//import manageBDDpays.Traiteur;

class Traiteur{
	private String ligne;
	Traiteur(String ligne){
		this.ligne = ligne;
	}
	public String[] traiter(){
		String[] combinaison = new String[7];
		StringTokenizer st = new StringTokenizer(ligne, ";");
		String rejete = st.nextToken(); // indicatif du type de flux que l'on rejette
		int i = 0;
		while (st.hasMoreTokens()) {
			combinaison[i] = st.nextToken();
			i++;
	    }
		
		return combinaison;
	}
}

class Requeteur{
private String[] combinaison;
	
	Requeteur(String[] combinaison){
		this.combinaison = combinaison;	
	}
	
	public String creer(){
		String requete = null;
		
		String temps = new Convertisseur(combinaison[0],combinaison[1]).convertirEnDate();
		String nc8 = combinaison[2];
		String pays = combinaison[3];
		String valeur = combinaison[4];
		String masse = combinaison[5];
		String usup = combinaison[6];
		requete = "INSERT INTO Export(Temps, NC8, Pays, Valeur, Masse, USUP) VALUES "
				+ "('" + temps + "', '" + nc8 + "', '" + pays + "', " + valeur 
				+ ", " + masse + ", " + usup + ")";
		
		return requete;
	}
}

class Convertisseur{
	private String mois, annee;
	Convertisseur(String mois, String annee){
		this.mois = mois;
		this.annee = annee;
	}
	public String convertirEnDate(){
		String date = null;
		// begin inclusive, end exclusive
		date = annee + "-" + mois + "-01";
		return date;
	}
}

public class InsertBDDexport {

	public static void main(String[] args) {
		// TODO Auto-generated method stub


		Connection con = null;
		String requete = "";
		Statement stmt = null;
		String source = "/home/neilujman/Documents/informatique/java/jdbc/NATIONAL_NC8PAYSE/NATIONAL_NC8PAYSE.txt";

		// chargement du pilote
		try{
			Class.forName("org.apache.derby.jdbc.ClientDriver");
			System.out.println("Driver trouvé.");
		} catch(ClassNotFoundException e){
			System.err.println("Driver non trouvé.");
			System.exit(1);
		}

		// connection à la base de données
		try{
			String urlBDD = "jdbc:derby:/home/neilujman/Documents/informatique/java/jdbc/douanesdb";
			con = DriverManager.getConnection(urlBDD);
			System.out.println("Connexion à la BDD réussie.");
		}catch (SQLException e){
			e.printStackTrace();
			System.err.println("Pas de connection à la BDD.");
			System.exit(2);

		}

		try{
			BufferedReader fichier = new BufferedReader(new FileReader(source));
			System.out.println("Fichier "+source+" trouvé.");
			String ligne;

			while ((ligne = fichier.readLine()) != null){

				String[] treatedLine = new Traiteur(ligne).traiter();

				requete = new Requeteur(treatedLine).creer();

				stmt = con.createStatement();
				int nb = stmt.executeUpdate(requete);
				//System.out.println(requete);

			}

			fichier.close();
			System.out.println("Création finie.");

		} catch(FileNotFoundException e){
			System.err.println("Fichier non trouvé.");
			System.exit(4);

		} catch(SQLException e){
			e.printStackTrace();
			System.err.println("Requête de création non traitée.");
			System.exit(3);
		} catch(IOException e){
			System.out.println("Problème de ligne.");
			System.exit(5);
		}
	}

}
