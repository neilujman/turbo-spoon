package manageBDDpays;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class Traiteur{
	private String ligne;
	
	public Traiteur(String ligne){
		this.ligne = ligne;
		
		
	}
	public String[] traitement(){
		String[] combinaison = new String[4];
		
		StringTokenizer st = new StringTokenizer(ligne, ";");
		int i = 0;
//		while (st.hasMoreTokens()) {
//			combinaison[i] = st.nextToken();
//			i++;
//	    }
		combinaison[0] = st.nextToken();
		// combinaison[1] traité par regex
		String tmp = st.nextToken();
		Pattern p = Pattern.compile("'");
		Matcher m = p.matcher(tmp);
		tmp = m.replaceAll("''");
		combinaison[1] = tmp;
		combinaison[2] = st.nextToken();
		combinaison[3] = st.nextToken();
		return combinaison;
	}
	
}


//requete = "INSERT INTO client VALUES (3,'client 3','prenom 3')";
class Requeteur{
	private String[] combinaison;
	
	Requeteur(String[] combinaison){
		this.combinaison = combinaison;	
	}
	
	public String creer(){
		String requete = null;
		
		String valeur0 = combinaison[0];
		String valeur1 = combinaison[1];
		String valeur2 = new Convertisseur(combinaison[2]).convertirEnDate();
		String valeur3 = new Convertisseur(combinaison[3]).convertirEnDate();
		
		requete = "INSERT INTO Pays(Code, Libellé, Début, Fin) VALUES "
				+ "('" + valeur0 + "', '" + valeur1 + "', '" + valeur2 + "', '" + valeur3 + "')";
		
		return requete;
		
	}
}

/**
 * On sait que les dates fournies par le fichier pays sont de la forme
 * 196108 ou 999912
 * On les convertira en 1961-08-01 ou 9999-12-01 
 * @author neilujman
 *
 */
class Convertisseur{
	private String dateEnTxt;
	Convertisseur(String dateEnTxt){
		this.dateEnTxt = dateEnTxt;
	}
	public String convertirEnDate(){
		String date = null;
		// begin inclusive, end exclusive
		date = dateEnTxt.substring(0, 4) + "-" + dateEnTxt.substring(4,6) + "-01";
		return date;
	}
}


public class InsertBDD {

	public static void main(String args[]){
		Connection con = null;
		String requete = "";
		Statement stmt = null;
		String source = "/home/neilujman/Documents/informatique/java/jdbc/NATIONAL_NC8PAYSE/PAYS.txt";
		
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
						
						String[] treatedLine = new Traiteur(ligne).traitement();
						
						requete = new Requeteur(treatedLine).creer();
						
						stmt = con.createStatement();
						int nb = stmt.executeUpdate(requete);
						System.out.println(requete);

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
