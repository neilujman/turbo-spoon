package manageBDDimportexport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;



/**
 * Explication des champs
• Le flux correspond au mouvement de la marchandise. Ce champ est valorisé à I pour
Importation et à E pour Exportation
• Mois : c'est le mois durant lequel a eu lieu le mouvement de marchandise.
• Année: c'est l'année durant laquelle a eu lieu le mouvement de marchandise.
• Code NC8 : C'est le code de la nomenclature combinée à 8 chiffres (NC8), utilisée pour les
obligations déclaratives des opérateurs auprès de la douane, permet une connaissance
détaillée du commerce extérieur de la France : elle compte en effet un peu moins de 10 000
rubriques.
• Code Pays : C'est un code à deux caractères alphabétiques attribué à chaque pays par la
Commission des communautés européennes.
◦ À l'importation, les marchandises sont relevées au compte du pays d'origine.
◦ À l'exportation, les envois sont imputés au compte de la destination finale déclarée.
• Valeur : La valeur des échanges est exprimée en euros.
• Masse : Les masses sont exprimées en kilogrammes.
• USUP : Unité supplémentaire.
 * @author neilujman
 *
 */

public class CreateBDDimportexport {

	public static void main(String args[]){

		Connection con = null;
		String requete = "";
		Statement stmt = null;

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

			switch (args[1]) {// nécessite java7 pour un String en switch
			case "import" :
				// creation de la table import
				requete = "CREATE TABLE Import"
						+ "(Id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,"
						+ "Temps DATE, NC8 CHAR(8), "
						+ "Pays CHAR(2), "
						+ "Valeur INTEGER, Masse INTEGER, USUP INTEGER)";
				stmt = con.createStatement();
				boolean resultCreateQuery = stmt.execute(requete);
				System.out.println(resultCreateQuery + " Table Import créée.");

				break;

			case "export" :
				// creation de la table export
				requete = "CREATE TABLE Export"
						+ "(Id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,"
						+ "Temps DATE, NC8 CHAR(8), "
						+ "Pays CHAR(2), "
						+ "Valeur INTEGER, Masse INTEGER, USUP INTEGER)";
				stmt = con.createStatement();
				resultCreateQuery = stmt.execute(requete);
				System.out.println(resultCreateQuery + " Table Export créée.");

				break;
			default :
				System.out.println("Entrer import ou export en argument!");
			}
		} catch(SQLException e){
			e.printStackTrace();
			System.err.println("Requête de création non traitée.");
			System.exit(3);
		}


	}


}
