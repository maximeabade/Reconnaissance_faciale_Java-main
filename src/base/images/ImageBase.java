package base.images;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import base.Base;

public class ImageBase{
	private double[][] matrice; //l'image sous forme de matrice (chaque pixel = une case entre 0 et 1) de taille TAILLE
	private double[][] matriceCentre; // la matrice qui représente l'image centrée
	private double[] vecteur; //l'image sous forme de vecteur de taille TAILLE*TAILLE
	private double[] vecteurCentre; //le vecteur centré de l'image
	public final static int TAILLE = 30; //la longueur d'une image (nb de lignes ou colonnes de la matrice)
	private String nom; //nom de l'image (ex : A_001.jpg)
	
	/**
	 * 
	 * @param matrice la matrice correspondant à l'image
	 * @param vecteur le vecteur correspondant à l'image
	 * constructeur de l'image via une matrice ou un vecteur (l'un des deux doit être à null)
	 */
	public ImageBase(double[][] matrice, double[] vecteur) {
		this.matrice = matrice;
		this.vecteur=vecteur;
	}
	
	/**
	 * 
	 * @param nom
	 * @throws IOException
	 * constructeur de l'image via son nom
	 */
	public ImageBase(String nom) throws IOException {
		this.nom = nom;
		this.recupImage(nom);
	}
	
	/**
	 * 
	 * @param nom
	 * donne le nom à une image
	 */
	public void setNom(String nom) {
		this.nom=nom;
	}
	
	/**
	 * 
	 * @return le nom de l'image
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * @return la matrice
	 */
	public double[][] getMatrice() {
		return matrice;
	}

	/**
	 * @param matrice la matrice à donner à l'image
	 */
	public void setMatrice(double[][] matrice) {
		this.matrice = matrice;
	}
	
	/**
	 * passe le vecteur en matrice
	 */
	public void setMatrice() {
		double[][] mat = new double[TAILLE][TAILLE];
		int i=0;
		for (int j=0; j<TAILLE;j++) {
			for (int k=0;k<TAILLE;k++) {
				mat[j][k]=this.vecteur[i];
				i++;
			}
		}
		this.setMatrice(mat);
	}
	
	/**
	 * @return le vecteur
	 */
	public double[] getVecteur() {
		return vecteur;
	}

	/**
	 * @param vecteur le vecteur à donner
	 */
	public void setVecteur(double[] vecteur) {
		this.vecteur = vecteur;
	}
	
	/**
	 * passe la matrice en vecteur
	 */
	public void setVecteur() {
		int taille=this.matrice.length*this.matrice.length;
		int k=0;
		double[] vect = new double[taille];
		for (int i=0; i<this.matrice.length;i++) {
			for (int j=0; j<this.matrice.length;j++) {
				vect[k]=this.matrice[i][j];
				k++;
			}
		}
		this.vecteur=vect;
	}
	
	/**
	 * @return le vecteurCentre
	 */
	public double[] getVecteurCentre() {
		return vecteurCentre;
	}
	
	/**
	 * 
	 * @param b base dans laquelle l'image appartient 
	 * calcule le vecteur centré d'une image
	 */
	
	public void setVecteurCentre(Base b) {
		int l = TAILLE*TAILLE;
		double[] vectC = new double[l];
		//pour chaque case
		for (int i =0; i<l;i++) {
			//vecteur centré = vecteur de l'image - vecteur moyen (en valeur absolue)
			vectC[i]= Math.abs(this.vecteur[i] - b.getVisageMoyen().getVecteur()[i]);
		}
		this.vecteurCentre=vectC;
	}
	
	/**
	 * @return la matrice centée
	 */
	public double[][] getMatriceCentre() {
		return matriceCentre;
	}
	
	/**
	 * @param matrice la matrice à donner à l'image
	 */
	public void setMatriceCentre(double[][] matrice) {
		this.matriceCentre = matrice;
	}
	
	/**
	 * passe le vecteur en matrice
	 */
	public void setMatriceCentre() {
		double[][] mat = new double[TAILLE][TAILLE];
		int i=0;
		for (int j=0; j<TAILLE;j++) {
			for (int k=0;k<TAILLE;k++) {
				mat[j][k]=this.vecteurCentre[i];
				i++;
			}
		}
		this.setMatriceCentre(mat);
	}
	
	/**
	 * affiche une image avec son nom, sa matrice, son vecteur ou son vecteur centré s'ils existent
	 */
	public String toString() {
		String msg = "";
		//affichage du nom
		if(this.nom != null){
			msg += "nom : " + this.nom + "\n";
		}
		//affichage de la matrice
		if(this.matrice!=null) {
			msg+="Matrice : \n";
			for(int i=0; i<this.matrice.length;i++) {
				for (int j=0;j<this.matrice.length;j++) {
					msg+=this.matrice[i][j]+" ";
				}
				msg+="\n";
			}
		}
		//affichage du vecteur
		if(this.vecteur!=null) {
			msg+="Vecteur : \n";
			for (int j=0;j<this.vecteur.length;j++) {
				msg+=this.vecteur[j]+" ";
			}
			msg+="\n";
		}
		//affichage du vecteur centré
		if(this.vecteurCentre!=null) {
			msg+="Vecteur centré : \n";
			for (int j=0;j<this.vecteurCentre.length;j++) {
				msg+=this.vecteurCentre[j]+" ";
			}
		}
		return msg;
	}
	
	/**
	 * 
	 * @param nomPhoto le nom de la photo
	 * @throws IOException
	 * récupère une image et la transforme en matrice, chaque pixel étant entre 0 et 1
	 */
	public void recupImage(String nomPhoto) throws IOException{

		// on crée une matrice null par défaut
		double [][] mat = null;

		try {
			// récupération du fichier image
			File fichier = new File("./Photo/" + nomPhoto);
			BufferedImage image = ImageIO.read(fichier);

			// on crée la matrice correspondant à l'image
			mat = new double[TAILLE][TAILLE];

			for(int colonne = 0; colonne < TAILLE; colonne++){
				for(int ligne = 0; ligne < TAILLE; ligne++){
					// on récupère les composantes RGB du pixel
					int couleur = image.getRGB(colonne, ligne);
					// on récupère la composante bleue, on aurait pu aussi choisir la rouge ou la verte
					int blue = couleur & 0xff;
					// on divise par 255 pour avoir une valeur entre 0 et 1
					float coeff = (float)blue/255;
					// on ajoute le pixel à la matrice
					mat[colonne][ligne] = coeff;
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.setMatrice(mat);
	}
	
	/**
	 * 
	 * @param nomPhoto
	 * @throws IOException
	 * construit l'image associée à une matrice
	 */
	public void construireImage(String nomPhoto) throws IOException{
		
		// on crée la nouvelle image
		BufferedImage nvImage = new BufferedImage(TAILLE, TAILLE, BufferedImage.TYPE_INT_RGB);

		try {

			// on récupère la matrice de l'image de base
			double [][] mat = this.getMatrice();
			// on parcourt l'image pour lui modifier ses pixels
			for(int colonne = 0; colonne < TAILLE; colonne++){
				for(int ligne = 0; ligne < TAILLE; ligne++){
					// on récupère la nuance de gris de l'image de base
					double nuance = mat[colonne][ligne];
					int g = (int)(255*nuance);
					// on colore chaque pixel
					nvImage.setRGB(colonne, ligne, new Color(g,g,g).getRGB());
				}
			}

			ImageIO.write(nvImage, "JPG", new File(nomPhoto));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param nomPhoto
	 * @throws IOException
	 * construit l'image centrée associée à une matrice
	 */
	public void construireImageCentre(String nomPhoto) throws IOException{
		
		// on crée la nouvelle image
		BufferedImage nvImage = new BufferedImage(TAILLE, TAILLE, BufferedImage.TYPE_INT_RGB);

		try {

			// on récupère la matrice de l'image de base
			double [][] mat = this.getMatriceCentre();
			// on parcourt l'image pour lui modifier ses pixels
			for(int colonne = 0; colonne < TAILLE; colonne++){
				for(int ligne = 0; ligne < TAILLE; ligne++){
					// on récupère la nuance de gris de l'image de base
					double nuance = mat[colonne][ligne];
					int g = (int)(255*nuance);
					// on colore chaque pixel
					nvImage.setRGB(colonne, ligne, new Color(g,g,g).getRGB());
				}
			}

			ImageIO.write(nvImage, "JPG", new File(nomPhoto));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
