package base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;

import base.images.ImageBase;
import smile.projection.*;

public class Base {
	private ImageBase[] images; //les images appartenant à la base
	private ImageBase visageMoyen; //l'image du visage moyen
	private double[][] matJuxtaposes; //la matrice des vecteurs centrés de toutes les images juxtaposés, sur laquelle on va appliquer l'ACP
	private PCA acp; //l'ACP de la base
	private double[][] matFinale; //la matrice obtenue en ayant fait l'acp sur matJuxtaposes
	
	protected final static int NBIMAGES = 57; //nombre d'images de la base d'apprentissage
	protected final static double PASDECORRESPONDANCE = -1; //constante de non correspondance entre un visage à tester et ceux de la base
	
	/**
	 * constructeur
	 */
	public Base(ImageBase[] images) {
		this.images = images;
		this.visageMoyen = null;
	}

	/**
	 * @return les images
	 */
	public ImageBase[] getImages() {
		return images;
	}

	/**
	 * @param images les images à mettre
	 */
	public void setImages(ImageBase[] images) {
		this.images = images;
	}

	/**
	 * @return le visageMoyen
	 */
	public ImageBase getVisageMoyen() {
		return visageMoyen;
	}
	
	/**
	 * @return matJuxtaposes
	 */
	public double[][] getMatJuxtaposes() {
		return matJuxtaposes;
	}
	
	/**
	 * calcule la matrice en juxtaposant tous les vecteurs centrés de toutes les images
	 */
	public void setMatJuxtaposes() {
		int nbLignes = this.images[0].getVecteur().length; //taille des vecteurs
		int nbColonnes = this.images.length; //nombre d'images
		double[][] matE = new double[nbLignes][nbColonnes];
		for (int i=0; i<nbLignes; i++) {
			for (int j=0; j<nbColonnes;j++) {
				matE[i][j]=this.images[j].getVecteurCentre()[i];
			}
		}
		this.matJuxtaposes=matE;
	}

	/**
	 * @param visageMoyen le visage moyen à mettre
	 * calcule le visage moyen à partir de tous les vecteurs d'images
	 */
	public void setVisageMoyen() {
		double[] tvec;
		tvec = this.images[0].getVecteur(); //vecteur d'une image
		int taille = tvec.length;
		double[] vect = new double[taille];
		for (int i=0;i<taille;i++) {
			vect[i]=0;
		}
		//pour chaque composante des vecteurs images
		for (int i=0;i<taille;i++) {
			for (int j=0; j<this.images.length;j++) {
				//on fait la somme
				tvec=this.images[j].getVecteur();
				vect[i]+=tvec[i];
			}
			//que l'on divise pas le nombre d'images
			vect[i]=vect[i]/this.images.length;
		}
		this.visageMoyen = new ImageBase(null,vect);
	}
	
	
	/**
	 * création de l'acp sur la matrice des vecteurs juxtaposés
	 */
	public void setAcp() {
		this.acp = new PCA(retourner(this.matJuxtaposes));
	}
	
	/**
	 * 
	 * @return acp : l'analyse en composantes principales de la matrice juxtaposée
	 */
	
	public PCA getAcp() {
		return this.acp;
	}
	
	/**
	 * affichage d'une base avec sa matrice juxtaposée et sa matrice finale
	 */
	public String toString() {
		String msg = "";
		if(this.getMatJuxtaposes()!=null) {
			msg+="Matrice des vecteurs centrés juxtaposés : \n";
			for(int i=0; i<this.matJuxtaposes.length;i++) {
				for (int j=0;j<this.matJuxtaposes[i].length;j++) {
					msg+=this.matJuxtaposes[i][j]+" ";
				}
				msg+="\n";
			}
		}
		if(this.getMatFinale()!=null) {
			msg+="Matrice finale : \n";
			for(int i=0; i<this.matFinale.length;i++) {
				for (int j=0;j<this.matFinale[i].length;j++) {
					msg+=this.matFinale[i][j]+" ";
				}
				msg+="\n";
			}
		}
		return msg;
	}
	/**
	 * permet de mettre la matrice finale G à laquelle on pourra comparer la projection d'une image
	 */
	public void setMatFinale() {
		this.matFinale=retourner(this.acp.project(retourner(this.matJuxtaposes)));
	}
	
	/**
	 * 
	 * @param mat la matrice à set comme matrice finale
	 * permet de mettre la matrice finale G à laquelle on pourra comparer la projection d'une image à partir d'une matrice donnée
	 */
	public void setMatFinale(double[][] mat) {
		this.matFinale=retourner(mat);
	}
	
	/**
	 * 
	 * @return la matrice composée de chaque vecteur image projeté dans la base de l'acp
	 */
	public double[][] getMatFinale(){
		return this.matFinale;
	}
	
	/**
	 * 
	 * @param folder le dossier où sont stockées les images
	 * @return un tableau d'images sous forme de matrices
	 * @throws IOException
	 * fonction pour récupérer toutes les images d'un dossier
	 */
	public static ImageBase[] findAllFilesInFolder(File folder) throws IOException {
		ImageBase[] images = new ImageBase[NBIMAGES];
		int i = 0;
		for (File file : folder.listFiles()) {
			if (!file.isDirectory()) {
				if (file.getName().contains("A")){
					// on créer un objet image pour toutes les images d'apprentissage
					ImageBase im = new ImageBase(file.getName());
					images[i] = im;
					i++;
				}
				
			} else {
				findAllFilesInFolder(file);
			}
		}
		return(images);
	}
	
	/**
	 * 
	 * @param mat
	 * @return la transposée de la matrice mat
	 */
	public static double[][] retourner(double[][] mat) {
		double[][] matRes = new double[mat[0].length][mat.length];
		for (int i=0; i<mat[0].length;i++) {
			for (int j=0;j<mat.length;j++) {
				matRes[i][j]=mat[j][i];
			}
		}
		return matRes;
	}
	
	/**
	 * 
	 * @param vecTest le vecteur (qui doit être centré) avec lequel on va tester la correspondance
	 * @param erreurSeuil notre seuil d'erreur fixé. Au-dessus de ce seuil, une image n'est pas reconnue
	 * @return le nom de l'image et l'erreur sous forme
	 * trouve (ou pas) la personne correpondant à l'image demandée
	 */
	public double[] comparer(double[] vecTest, double erreurSeuil) {
		String nom="";
		int id;
		double minErreur=1000000000;
		int j;
		double[] tabRes = new double[3];
		for (int i=0;i<this.matFinale[0].length;i++) {
			double erreur=0;
			for (j=0;j<this.matFinale.length;j++) {
				//calcul erreur quadratique
				erreur+=Math.pow(vecTest[j]-matFinale[j][i],2);
			}
			//l'erreur prend la moyenne des erreurs de chaque caractéristique d'un vecteur
			erreur=erreur/vecTest.length;
			//si l'erreur trouvée à ce vecteur est la plus petite
			if (erreur<minErreur) {
				//elle est gardée
				minErreur=erreur;
				nom=this.images[i].getNom();
				tabRes[2]=i;
			}
		}
		//si l'erreur est plus grande que le seuil
		if(minErreur>erreurSeuil) {
			//on renvoie :
			tabRes[0] = -1;
			tabRes[1] =PASDECORRESPONDANCE;
			tabRes[2]=minErreur;
		}else {
			id = Integer.parseInt(nom.substring(2,5));
			//sinon on renvoie le nom et l'erreur
			tabRes[0]=id;
			tabRes[1]=minErreur;
		}
		return (tabRes);
	}
	
	/**
	 * 
	 * @param nomCSV
	 * fonction pour sauvegarder la matrice dans un fichier csv
	 */
	public void enregistrerMatrice(String nomCSV) {
	    File file = new File(nomCSV);
	  
	    try {
	        FileWriter outputfile = new FileWriter(file);
	  
	        CSVWriter writer = new CSVWriter(outputfile);
	  
	        List<String[]> data = new ArrayList<String[]>();
	        for(int i=0; i<this.matFinale[0].length; i++) {
	        	String[] ligne = new String[this.matFinale[0].length];
	        	for(int j=0; j<this.matFinale.length; j++) {
	        		ligne[j] = String.valueOf(this.matFinale[j][i]);
	        	}
	        	data.add(ligne);
	        }
	        writer.writeAll(data);
	  
	        // closing writer connection
	        writer.close();
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * 
	 * @param file le fichier csv qui contient la matrice à récupérer
	 * @return la matrice contenue dans le fichier
	 * @throws IOException
	 */
	public double[][] recupMatrice(File file) throws IOException {

        List<String> result = new ArrayList<String>();
        double[][] matrice = new double[57][12];

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        
        int j = 0;

        for (String line = br.readLine(); line != null; line = br.readLine()) {
        	// on split la ligne pour récupérer chaque valeur
        	String valeurs[] = line.split(",");
        	// pour chaque valeur
        	for(int i=0; i<valeurs.length; i++) {
        		// on retire les guillemets
        		valeurs[i] = valeurs[i].replace("\"", "");
        		// si on n'a pas un carctère null, on stock la valeur dans la matrice
        		if(Double.valueOf(valeurs[i]) != null) {
        			matrice[j][i] = Double.valueOf(valeurs[i]);
        		}
        		
        	}
            result.add(line);
            j++;
        }

        br.close();
        fr.close();

        return matrice;
    }
	
	/**
	 * 
	 * @param numLoading le numéro du loading (=vrai eigenface) que l'on veut avoir
	 * @return l'eigenface numéro num
	 */
	public double[] recupLoading(int numLoading) {
		double[][] loadings = this.getAcp().getLoadings();
		double[] load = new double[ImageBase.TAILLE*ImageBase.TAILLE];
		for (int j=0;j<loadings.length;j++) {
			load[j] = Math.abs(loadings[j][numLoading]);
		}
		return load;
	}
	
	/**
	 * 
	 * @throws IOException
	 * enregistre les 5 premiers eigenfaces sous forme d'image
	 */
	public void enregistreEigenfaces() throws IOException {
		for (int i=0; i<5;i++) {
			ImageBase eigenface = new ImageBase(null, this.recupLoading(i));
			eigenface.setMatrice();
			eigenface.construireImage("eigenface"+i+".jpg");
		}
	}
	
	/**
	 * 
	 * @param v : le vecteur de l'image projeté dans la base
	 * @param k : le nombre de caractéristiques retenues
	 * @return le vecteur reconstruit à partir de v
	 * permet de reconstruire un vecteur sur lequel on a appliqué l'acp
	 */
	public double[] reconstruire(double[] v, int k) {
		double[][] loadings = this.getAcp().getLoadings();
		//matrice des k premières colonnes des Loadings, de taille 900 * k
		double[][] u = new double[ImageBase.TAILLE*ImageBase.TAILLE][k];
		for (int i=0; i<k;i++) {
			for (int j=0;j<loadings.length;j++) {
				u[j][i] = loadings[j][i];
			}
		}
		
		double[] vecReconstruit = new double[ImageBase.TAILLE*ImageBase.TAILLE];
		//vecReconstruit = u*v
		for (int i=0; i<ImageBase.TAILLE*ImageBase.TAILLE;i++) {
			vecReconstruit[i] = 0;
			for (int j=0;j<k;j++) {
				vecReconstruit[i] += u[i][j]*v[j];
			}
		}
		//on décentre
		for (int i=0; i<vecReconstruit.length;i++) {
			vecReconstruit[i]+=this.visageMoyen.getVecteur()[i];
		}
		return vecReconstruit;
	}
	
	/**
	 * 
	 * @param nbEigenfaces : le nombre d'eigenfaces que l'on veut montrer
	 * @return la chaîne permettant d'afficher ces eigenfaces
	 * prend dans la matrice finale les nbEigenfaces eigenfaces que l'on veut afficher
	 */
	public String premiersEigenfaces(int nbEigenfaces) {
		String res="";
		int numEigenface =1;
		for (int colonne=0;colonne<nbEigenfaces;colonne++) {
			res+=numEigenface+") ";
			for (int ligne=0;ligne<this.matFinale.length;ligne++) {
				res+=this.matFinale[ligne][colonne]+" ";
			}
			numEigenface++;
			res+="\n";
		}
		return res;
	}
	
	/**
	 * 
	 * @param num le numero de lu vecteur projeté que l'on veut avoir
	 * @return le vecteur projeté numero num sous forme de de liste
	 */
	public ArrayList<Double> eigenface(int num) {
		ArrayList<Double> eigenfaceL = new ArrayList<Double>();
		for(int ligne=0;ligne<this.matFinale.length;ligne++) {
			eigenfaceL.add(this.matFinale[ligne][num]);
		}
		return eigenfaceL;
	}
	
	/**
	 * 
	 * @param im : le numéro de l'image (entre 0 et 56)
	 * affiche l'évolution de l'erreur de reconstruction pour k (nombre de caractéritiques retenues) allant de 1 à 20
	 */
	public double[] graphErreur(int im) {
		int k;
		double[] tabErreur = new double[18];
		int l=0;
		for(k=1;k<=251;k+=50) {
			//projection en fonction de k
			this.acp.setProjection(k);
			this.setMatFinale();
			double[] imProj = new double[k];
			//on récupère le vecteur de projection de cette image
			for (int i=0;i<k;i++) {
				imProj[i]=this.getMatFinale()[i][im];
			}
			double[] imRecup = reconstruire(imProj, k);
			double[] x = this.images[im].getVecteur();
			//calculer l'erreur et la stocker dans le tableau
			double erreurTemp=0;
			for (int i=0;i<imRecup.length;i++) {
				erreurTemp+=Math.pow(x[i]-imRecup[i], 2);
			}
			//calcul de la moyenne sur toutes les caractéristiques
			tabErreur[l]=erreurTemp/imRecup.length;
			l++;
		}
		return tabErreur;
	}
	
}
