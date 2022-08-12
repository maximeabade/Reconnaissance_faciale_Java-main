package gestionBDD;

import java.io.*;
import java.util.Scanner;
import java.util.HashMap;

public class CreationMap {
	
	public static HashMap<Integer, ElementEnregistreMap> avoirMap(String cheminPourCSV) throws FileNotFoundException {
		
		Integer clef;
		String lecture;
		HashMap<Integer, ElementEnregistreMap> mapDesVisages = new HashMap<Integer, ElementEnregistreMap>();
		
		
		//on creer un objet File qui va etre scanne
		
		File getCSV = new File(cheminPourCSV);
		Scanner sc = new Scanner(getCSV);
		
		//nom explicite de la fonction
		sc.useDelimiter(",");
		
		//nom encore explicite 
		while (sc.hasNext()) {
			/**
			*on connait l'ordre des éléments présents dans le .csv qui sont dans l'ordre suivant:
			*nombre, string, string, string
			*/

			//on recreer un element a chaque iteration afin d'eviter tout probleme de pointeur
			ElementEnregistreMap element = new ElementEnregistreMap();

			//on lit le nom qui est sous forme de string que l'on va donc mettre sous forme d'Integer
			lecture =  sc.next();
			lecture = lecture.substring(1,lecture.length());
			clef = Integer.parseInt(lecture);

			//on lit le nom
			lecture =  sc.next();
			element.setNom(lecture);

			//on lit le prenom
			lecture =  sc.next();
			element.setPrenom(lecture);

			//on lit l'emotion/ la chararcteristique de la photo
			lecture =  sc.next();
			element.setEtat(lecture);

			//on enregistre l'element dans la map
			mapDesVisages.put(clef, element);
		}
		//on ferme le document
		sc.close();

		//on renvoie la map
		return mapDesVisages;
	}

}
