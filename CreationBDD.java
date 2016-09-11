package manageBDDpays;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * La table Pays doit être dropée.
 * 
 * 
 * @author neilujman
 *
 */
public class CreationBDD {

	public static void main(String args[]){
		
		Connection con = null;
		String requete = "";
		
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
			requete = "CREATE TABLE Pays"
					+ "(Id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,"
					+ "Code CHAR(2), Libellé VARCHAR(80), "
					+ "Début DATE, "
					+ "Fin DATE)";
			Statement stmt = con.createStatement();
			boolean resultCreateQuery = stmt.execute(requete);
			System.out.print(resultCreateQuery + " Table créée.");
			
		} catch(SQLException e){
			e.printStackTrace();
			System.err.println("Requête de création non traitée.");
			System.exit(3);
		}
		
		
	}

}
