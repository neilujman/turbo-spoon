package manageBDDexport2014;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

class Fenetre extends JFrame implements ActionListener{
	private JTextField saisie;
	private JButton bouton;
	private BaseDonnees bd;
	
	Fenetre(){
		String driver = "derby";
		String nomBD = "douanesdb";
		bd = new BaseDonnees(driver, nomBD);
		
		
		
		setTitle("Douanes");
		setSize(300,200);
		Container contenu = getContentPane();
		contenu.setLayout(new FlowLayout());
		
		saisie = new JTextField(20);
		contenu.add(saisie);
		//saisie.addActionListener(this);
		
		bouton = new JButton("Validez");
		contenu.add(bouton);
		bouton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==bouton){
			String code = saisie.getText();
			System.out.println(bd.donneValeur("export2014", code));
			
		}
	}
}

/**
 * La classe BaseDonnees permet de
 * faire des manipulations sur la
 * base particulière des douanes
 * @author neilujman
 *
 */
class BaseDonnees {
	String driver;
	String nom;
	Connection con;
	
	BaseDonnees(String driver, String nom) {
		this.driver = driver;
		this.nom = nom;
		con = null;
		// == chargement du pilote ==
		String classePilote = null;
		switch(driver){
		case "derby" :// en fait derby n'est pas un pilote mais un sgbd et jdbc un pilote
			classePilote = "org.apache.derby.jdbc.ClientDriver";
		}
		 
		try{
			Class.forName(classePilote);
			System.out.println("Driver trouvé.");
		} catch(ClassNotFoundException e){
			System.err.println("Driver non trouvé.");
			System.exit(1);
		}
		
		// == connection à la base de données ==
		try{
			String urlBDD = "jdbc:derby:/home/neilujman/Documents/informatique/java/jdbc/douanesdb";
			con = DriverManager.getConnection(urlBDD);
			System.out.println("Connexion à la BDD réussie.");
		}catch (SQLException e){
			e.printStackTrace();
			System.err.println("Pas de connection à la BDD.");
			System.exit(2);

		}
		
	}
	public HashMap<Date, Integer> donneValeur(String table, String nc8) {
		// on sait qu'il y a treize date dans une table ordinaire
		// prévoir le cas d'une table partielle, eg export0716
		// où 0716 veut dire juillet 2016
		// Par exemple dans la table export2014,
		// les dates s'étalonnent de janvier 2014 à janvier 2015 inclus
		// Il peut aussi avoir le cas où il y eût un mois pour lequel
		// il n'y a pas eu d'exportation à un nc8 donné.
		
//		ResultSet rs = stmt.executeQuery("SELECT * FROM employe");
		
		HashMap<Date, Integer> valeurs = new HashMap<Date, Integer>();
		
		ResultSet rs = null;
		
		Requeteur requete = new Requeteur(table, nc8);
		
		Statement stmt = null;
		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Problème de création de Statement.");
		}
		try {
			rs = stmt.executeQuery(requete.creer());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Pb d'exécution de requête.");
		}
		
		//parcours des données retournées
		Date tmp1;
		int tmp2; // résultat intermédiaire dans la lecture du ResultSet rs
		Integer tmp2I;
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int nbCols = rsmd.getColumnCount();
			while (rs.next()) {
				
					tmp1 = rs.getDate(1);
					tmp2 = rs.getInt(2);
					
					//System.out.print(tmp1 + " " + tmp2);
					valeurs.put(tmp1, Integer.valueOf(tmp2));
				
				System.out.println();
			}
			rs.close();
		} catch (SQLException e) {
			System.out.println("Pb dans le ResultSetMetaData");
		}

		
		return valeurs;
		
	}
	
	/**
	 * Cette classe sert à faire la recherche
	 * d'une valeur en fonction de son code nc8,
	 * la date sera aussi associé.
	 * @author neilujman
	 *
	 */
	class Requeteur {
		String table;
		String nc8;
		
		Requeteur(String table, String nc8) {
			this.table = table;
			this.nc8 = nc8;
		}
		
		// la requete permet de connaitre la somme de tous les pays pour qui il y ait eu une exportation d'un
		// produit de code nc8 donné
	
		public String creer() {
			String requete = null;
			requete = "select temps, sum(valeur) from " + table + " where nc8 = '" + nc8 + "' group by temps";
			return requete;
		}
		
	}
	
	
}


/**
 * Ici, on voudrait afficher une courbe des variations d'un produit exporté
 * Dans un premier temps, on va afficher les exportations en brut sous la 
 * forme d'une table.
 * L'entréé est à donner sous la forme d'un code à huit chiffres.
 * Un projet ou une branche permettra de vérifier que le code est bien
 * à chiffres et qu'il appartient bien à la base ad hoc (construction
 * future d'un autre projet).
 * 
 * A noter que l'on travaille dans le cas particulier de la table export2014
 * 
 * @author neilujman
 *
 */

public class GuiBDD {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Fenetre fen = new Fenetre();
		fen.setVisible(true);
	}

}
