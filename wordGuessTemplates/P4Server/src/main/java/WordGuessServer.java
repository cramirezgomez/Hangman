import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class WordGuessServer extends Application {

HashMap<String, Scene> sceneMap;
	
	//Start Scene Utilities 
	TextField portNumber;
	Button portButton, startServer;
	Label writePort;
	
	TextField portNumInfo;
	
	int socketPort;
	
	VBox v1, v3;
	HBox h2;

	//Server Scene Utilities 
	ListView<String> listItems;
	Server serverConnection;
	//MorraInfo turnInfo; //<---
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("(Server) Let's Play Morra!!!");		
		
		sceneMap = new HashMap<String, Scene>();
		
		//---Start GUI--//
		socketPort = 0;
		portNumber = new TextField();
		portNumInfo = new TextField();
		portNumInfo.setMaxWidth(200);
		portNumInfo.setEditable(false);
		
		portButton = new Button("Enter");
		startServer = new Button("Start Server");
		startServer.setDisable(true);
		
//		remove
//		later
//      ||||||
//      VVVVVV
		portNumber.setText("5555"); //Used to avoid entering 5555 all the time 
		//-------------//
		
		//---Server GUI--//
		listItems = new ListView<String>();
		listItems.setStyle("-fx-font-size: 25;"+"-fx-border-size: 20;"+ 
				"-fx-border-color: red;" + "-fx-opacity: 0.80;");
		listItems.setMaxSize(500, 500);
		//-------------//
		
		//Event Handlers 
		portButton.setOnAction(new EventHandler<ActionEvent> (){
			public void handle(ActionEvent action){
				
					 try {
					 socketPort = Integer.parseInt(portNumber.getText());
					 }
					 catch(Exception e){}
					 
					 if(socketPort > 5554 && socketPort < 65535){
						 
						 System.out.println("This is the Socket Number: " + socketPort);
						 portNumber.clear();
						 portNumInfo.setText("Your current Port Number: " + socketPort);
						 startServer.setDisable(false);
					 }
					 else
					 {
						 portNumInfo.setText("Invalid Port Number");
					 }
			}
		});
		
		startServer.setOnAction(new EventHandler<ActionEvent> (){
			public void handle(ActionEvent action)
			{
				primaryStage.setTitle("This is the Server");
				primaryStage.setScene(sceneMap.get("server"));
				
				serverConnection = new Server(data -> 
				{
					Platform.runLater(()->
					{
						/*
							-> Here we would update the server on the status of certain players 
							-> Should'nt be as much information as the last project
						*/
						
						WordInfo input = (WordInfo) data;
						listItems.getItems().add(input.serverMessage);
						
						//Display Client Guess
						if(input.guess != ' ') {
							listItems.getItems().add("Current Guess: " + input.guess);
						}
						//Display Client Category 
						if(input.category != 0) {
							listItems.getItems().add("Category picked: " + input.category);
						}
						
					});
				}, socketPort);;
			}
		});
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
		
		//Add Scenes to Hash Map 
		sceneMap.put("server",  createServerGui());
		sceneMap.put("start",  createStartGui());
		
		primaryStage.setTitle("Start the Server");
		primaryStage.setScene(sceneMap.get("start"));
		primaryStage.show();
	}
	
	//Create a server start scene (Scene 1)
	public Scene createStartGui()
	{
		BorderPane startPane = new BorderPane();
		writePort = new Label("Enter a Port Number (5555 - 65535)");
		writePort.setStyle("-fx-font-weight: bold");
		
		h2 = new HBox(10, portNumber, portButton);
		h2.setAlignment(Pos.CENTER);
		v1 = new VBox(5, writePort, h2, portNumInfo);
		v1.setAlignment(Pos.CENTER);
		v3 = new VBox(25, v1, startServer);
		v3.setAlignment(Pos.CENTER);
		
		startPane.setCenter(v3);
		
		/*
		//Create an object for the background image    
		 Image dogEin = new Image("startGui.jpg");
			
		    //Set background size 
			BackgroundSize aSize = new BackgroundSize(1.0, 1.0, true, true, false, false);
			startPane.setBackground(new Background(new BackgroundImage(dogEin,
		            BackgroundRepeat.NO_REPEAT,
		            BackgroundRepeat.NO_REPEAT,
		            BackgroundPosition.CENTER,
		            aSize )));
		*/
		return new Scene(startPane, 850, 750);
	}
	
	//Create a server Scene with server information 
	public Scene createServerGui()
	{	
		BorderPane serverPane = new BorderPane();
		serverPane.setCenter(listItems);
		
		/*//Create an object for the background image    
		Image edwardServer = new Image("serverGui.jpg");
		
	    //Set background size 
		BackgroundSize bSize = new BackgroundSize(1.0, 1.0, true, true, false, false);
		serverPane.setBackground(new Background(new BackgroundImage(edwardServer,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundPosition.CENTER,
	            bSize )));
		*/
		return new Scene(serverPane, 850, 750);
	}

}
