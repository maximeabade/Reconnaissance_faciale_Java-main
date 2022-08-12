package base;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;

import java.util.HashMap;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;

import base.images.ImageBase;
import gestionBDD.CreationMap;
import gestionBDD.ElementEnregistreMap;


public class Main extends Application {

	public void start(Stage primaryStage) throws Exception {
		//titre de la fenetre
		primaryStage.setTitle("Reconnaissance faciale par ACP");
		//panneau de type Hbox
		HBox root = new HBox(6);
		
		//traitements liés à l'acp
		
		//récupérer toutes les images d'un dossier
		ImageBase[] images = new ImageBase[Base.NBIMAGES];
		File folder = new File("./Photo");
		images = Base.findAllFilesInFolder(folder);
		// pour chaque image on construit le vecteur à partir de sa matrice
		for(int i = 0 ; i < Base.NBIMAGES; i++) {
			if (images[i] != null){
				images[i].setVecteur();
			}	
		}
		// on crée une base avec les images
		Base b = new Base(images);
		// on calcule le visage moyen
		b.setVisageMoyen();
		b.getVisageMoyen().setMatrice();
		// on reconstruit l'image moyenne
		b.getVisageMoyen().construireImage("./Photo/M.jpg");
		// pour chaque image on construit le vecteur centré
		for(int i = 0 ; i < Base.NBIMAGES; i++) {
			if (images[i] != null){
				images[i].setVecteurCentre(b);
				images[i].setMatriceCentre();
				images[i].construireImageCentre("./images_resultats/image_centree_"+images[i].getNom());
			}	
		}
		b.setMatJuxtaposes();
		//on fait l'acp pour avoir la base
		b.setAcp();	
		b.getAcp().setProjection(12); //on sélectionne 12 caractéristiques
		File matriceCSV = new File("matriceFinale.csv");
		double[][] matriceRes = b.recupMatrice(matriceCSV);
		b.setMatFinale(matriceRes);
		
		//Eigenfaces
		GridPane pane1 = new GridPane();
		pane1.setVgap(5); 
	    pane1.setHgap(5);
		
		pane1.add(new Label("Voici les 5 premiers \neigenfaces :"), 0, 1);
		
		FileInputStream input1 = new FileInputStream("./images_resultats/eigenface0.jpg");
        Image image1 = new Image(input1, 200, 200, false, true);
        ImageView imageView1 = new ImageView(image1);
        pane1.add(imageView1,0,2);
        
        FileInputStream input2 = new FileInputStream("./images_resultats/eigenface1.jpg");
        Image image2 = new Image(input2, 200, 200, false, true);
        ImageView imageView2 = new ImageView(image2);
        pane1.add(imageView2,1,1);
                
        FileInputStream input3 = new FileInputStream("./images_resultats/eigenface2.jpg");
        Image image3 = new Image(input3, 200, 200, false, true);
        ImageView imageView3 = new ImageView(image3);
        pane1.add(imageView3,1,2);
        
        FileInputStream input4 = new FileInputStream("./images_resultats/eigenface3.jpg");
        Image image4 = new Image(input4, 200, 200, false, true);
        ImageView imageView4 = new ImageView(image4);
        pane1.add(imageView4,2,1);
        
        
        FileInputStream input5 = new FileInputStream("./images_resultats/eigenface4.jpg");
        Image image5 = new Image(input5, 200, 200, false, true);
        ImageView imageView5 = new ImageView(image5);
        pane1.add(imageView5,2,2);
		
		root.getChildren().add(pane1);
		
        //image moyenne
        VBox vbox = new VBox(2);
        
        Label labelImgMoy = new Label("Image moyenne : ");
        vbox.getChildren().add(labelImgMoy);
        
        FileInputStream input = new FileInputStream("./Photo/M.jpg");
        Image image = new Image(input, 200, 200, false, true);
        ImageView imageView = new ImageView(image);
        vbox.getChildren().add(imageView);
        
        root.getChildren().add(vbox);		
		
        //liste de l'evolution de l'erreur
        VBox vbox1 = new VBox(4);
        Label labelErreur = new Label("Choisissez le numéro de l'image dont\nvous voulez voir l'évolution de l'erreur : ");
        vbox1.getChildren().add(labelErreur);
        
        //choix de l'image
        ComboBox<Integer> comboBox = new ComboBox<Integer>();
        comboBox.getItems().addAll(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56);
        vbox1.getChildren().add(comboBox);
                
        comboBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				vbox1.getChildren().clear();
				vbox1.getChildren().add(labelErreur);
				vbox1.getChildren().add(comboBox);
				int numImageG = comboBox.getSelectionModel().getSelectedItem();
				double[] tabY = b.graphErreur(numImageG-1);
				// Création du graphique. 
		        NumberAxis xAxis = new NumberAxis();
		        xAxis.setLabel("Nombre de caractéristiques retenues");
		
		        NumberAxis yAxis = new NumberAxis();
		        yAxis.setLabel("Erreur de reconstruction\nde l'image choisie");
		
				LineChart<Number, Number> lineChart = new LineChart<Number,Number>(xAxis, yAxis);
		
		        Series<Number, Number> dataSeries1 = new XYChart.Series<Number,Number>();
		        dataSeries1.setName("Evolution de l'erreur de reconstruction\nen fonction du nombre de caractéristiques retenues");
		        
		        for(int i=0;i<tabY.length;i++) {
		        	dataSeries1.getData().add(new Data<Number, Number>(1+i*50, tabY[i]));
		        }
		        lineChart.getData().add(dataSeries1);
		        vbox1.getChildren().add(lineChart);
		        vbox1.getChildren().add(new Label("Nous avons choisi de retenir 12 caractéristiques."));
			}
        }); 
                
        vbox1.setMinWidth(400);
        vbox1.setMinHeight(500);
        root.getChildren().add(vbox1);
                
		//mise en place des éléments dans le pane
        GridPane pane = new GridPane();
		pane.setMinWidth(400);
		pane.add(new Label("Nous avons une base de 56 images de taille 30x30"),0,0);
		//pour saisir le nom de la photo à tester
        FileChooser fileChooser = new FileChooser();
        Button button = new Button("Choisir une image à tester");
        pane.add(button, 0, 1);
        button.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				File selectedFile = fileChooser.showOpenDialog(primaryStage);
				pane.getChildren().clear();
				pane.add(button, 0, 1);
				pane.add(new Label("Nous avons une base de 56 images de taille 30x30"),0,0,2,1);
				String test = selectedFile.getName(); //prendre le nom de l'image
				//recuperation de l'ID de l'image test
				String idImageAtester;
				idImageAtester = test.substring(2,5);
				int idIntATester = Integer.parseInt(idImageAtester);
				ImageBase imageTest = null;
				try {
					imageTest = new ImageBase(test);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				//on centre l'image
				imageTest.setVecteur();
				imageTest.setVecteurCentre(b);
				//on projette l'image selon la base trouvée grâce à l'ACP
				double[] vecTestProjete = b.getAcp().project(imageTest.getVecteurCentre());
				double erreurSeuil = 0.1;
				//on recupere les ID de la photo a tester et celle de l'image la plus proche
				double[] idAndErreur = new double[2];
				int idTrouvee;
				idAndErreur = b.comparer(vecTestProjete,erreurSeuil);
				idTrouvee = (int) idAndErreur[0];
				double erreur= idAndErreur[1];
				int nbImage = (int) idAndErreur[2];
				//verification de l'identite
				if (idTrouvee < 0){
					Label label = new Label("Pas de correspondance\navec les images \nde la base de données");
					pane.add(label, 0, 3);
					Label label3 = new Label("\n\nErreur : "+idAndErreur[2]);
			        pane.add(label3, 0, 4);
			        Label label5 = new Label("\n\nSeuil choisi : 0.1");
			        pane.add(label5, 0, 5);
				}else {
					HashMap<Integer, ElementEnregistreMap> mapDesVisages = new HashMap<Integer, ElementEnregistreMap>();
					try {
						//verifier le chemin relatif
						mapDesVisages = CreationMap.avoirMap("constructionMap.csv");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					ElementEnregistreMap Elem1 = mapDesVisages.get(idIntATester);
					ElementEnregistreMap Elem2 = mapDesVisages.get(idTrouvee);
					
					//afficher photo à tester
			        FileInputStream inputImageTeste = null;
					try {
						inputImageTeste = new FileInputStream("./Photo/"+test);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
			        Image imageTeste = new Image(inputImageTeste, 200, 200, false, true);
			        ImageView imageView2 = new ImageView(imageTeste);
			        pane.add(imageView2,0,2);
			        //afficher photo trouvée
			        FileInputStream inputImageTrouve = null;
					try {
						inputImageTrouve = new FileInputStream("./Photo/"+b.getImages()[nbImage].getNom()); //à changer par la photo trouvée
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
			        Image imageTrouve = new Image(inputImageTrouve, 200, 200, false, true);
			        ImageView imageView1 = new ImageView(imageTrouve);
			        pane.add(imageView1,1,2) ;
			        //afficher info photo à tester
			        Label label1 = new Label("Photo test\nNom : "+Elem1.getNom() + "\nPrénom : " + Elem1.getPrenom());
			        pane.add(label1,0,3);
			        //afficher info photo trouvé
			        Label label2 = new Label("Photo trouvée\nNom : "+Elem2.getNom() + "\nPrénom : " + Elem2.getPrenom());
			        pane.add(label2,1,3);
			        //affichage erreur
			        Label label3 = new Label("\n\nErreur : ");
			        pane.add(label3, 0,4);
			        Label label4 = new Label("\n\n"+erreur);
			        pane.add(label4, 1, 4);
			        Label label5 = new Label("\n\nSeuil choisi : ");
			        pane.add(label5, 0, 5);
			        Label label6 = new Label("\n\n0,1");
			        pane.add(label6, 1, 5);
				}
			}
        });       
        //ajouter au HBox
		root.getChildren().add(pane);
        
		//spécifie la scène à associer à la fenêtre
		primaryStage.setScene(new Scene(root));
		//affiche la fenêtre à l'écran
		primaryStage.show();
	}
	
	/**
	 * 
	 * @param args
	 * @throws IOException
	 * main : fonction principale
	 */
	public static void main(String[] args) throws IOException {
		//lancer l'interface graphique
		launch(args);
	}
}
