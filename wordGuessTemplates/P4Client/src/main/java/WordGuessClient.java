import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class WordGuessClient extends Application {
	Client clientConnection;
	ListView<String> tempList;
	int portNum;
	String IPAddress;
	HashMap<String, Scene> sceneMap;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("(Client) Word Guess!!!");
		sceneMap = new HashMap<String, Scene>();
		
		
		sceneMap.put("start",  createStart(primaryStage));
		sceneMap.put("game",  createGame(primaryStage));
		
		primaryStage.setScene(sceneMap.get("start"));
		primaryStage.show();
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
            	try {
            		clientConnection.disconnect();
            	}
            	catch(Exception e)
            	{
            		
            	}
            	
                Platform.exit();
                System.exit(0);
            }
        });
		
	
		
		
		
		
	}
	
public Scene createStart(Stage primaryStage) {
		
		Label labIP = new Label("IP Address");
		Label labPort = new Label("Port Number");
		
		TextField enterIP = new TextField();
		TextField enterPort = new TextField();
		enterIP.setText("127.0.0.1");
		enterPort.setText("5555");
		Button btnConnect = new Button("Connect");
		
		VBox col1 = new VBox(labIP, enterIP, labPort, enterPort);
		VBox col2 = new VBox(btnConnect);
		HBox clientBox = new HBox(col1, col2);
		//clientBox.setStyle("-fx-background-color: blue");
		
		btnConnect.setOnAction(e->{
			primaryStage.setScene(sceneMap.get("game"));
			
			IPAddress =enterIP.getText();
			portNum = Integer.parseInt(enterPort.getText());
			primaryStage.show();
			//Receive a message
			clientConnection = new Client(data->
			{
				Platform.runLater(()->
				{
					//keep track of categories cleared and guesses on client
					++clientConnection.serverResponses;
					WordInfo input = (WordInfo) data;
					tempList.getItems().add(input.serverMessage);
					
					//welcome message was sent
					if(clientConnection.serverResponses == 1) 
					{
						
					}
					//length of the word is being sent
					else if(input.wordLength != 0) {
						
					}
					//guess response is being done
					else
					{
						//our guesss was correct
						if(input.isCorrect){
							
						}
						//our guess was incorrect
						else {
							
						}
						
						//ran out of guesses
						if(clientConnection.guesses == 0) {
							
							//ran out of lives
							if(clientConnection.lives == 0) {
								
							}
						}
					}			
				});
			}, IPAddress, portNum);
			clientConnection.start();
			
		});
		return new Scene(clientBox, 600, 500);
		
	}

	public Scene createGame(Stage primaryStage) {
		
		TextField tempTxt = new TextField();
		Button sendGuess = new Button("Send Guess");
		Button sendCat = new Button("Send Cat");
		
		tempList = new ListView<String>();
		VBox clientBox = new VBox(10, tempList, tempTxt, sendGuess, sendCat );
		 
		sendGuess.setOnAction(e->
		{
			if(tempTxt.getText().length() == 1) {
				WordInfo curGuess = guessRequest(tempTxt.getText().charAt(0));
				clientConnection.send(curGuess); 
				tempTxt.clear();
			}
			else
			{
				tempTxt.clear();
				tempTxt.setText("Not one char");
			}
			
		});
		
		sendCat.setOnAction(e->
		{
			int x = Integer.parseInt(tempTxt.getText());
			if( x >= 1 && x <= 3) {
				WordInfo curCategory = categoryRequest(x);
				clientConnection.send(curCategory); 
				tempTxt.clear();
			}
			else
			{
				tempTxt.clear();
				tempTxt.setText("Not bewteen 1 and 3");
			}
			
		});
		
		return new Scene(clientBox, 600, 500);
		
	}
	
	//create new object to pick category
	WordInfo categoryRequest(int x){
		WordInfo tempObj = new WordInfo();
		tempObj.category = x;
		tempObj.serverMessage = "picked a category";
		return tempObj;
	}
	
	//create new object to guess
	WordInfo guessRequest(char x){
		WordInfo tempObj = new WordInfo();
		tempObj.guess = x;
		tempObj.serverMessage = "sent a guess";
		return tempObj;
	}
	
	//create new object to play again
	WordInfo playAgainRequest(){
		WordInfo tempObj = new WordInfo();
		tempObj.playAgain = true;
		tempObj.serverMessage = "clicked play again";
		return tempObj;
	}
	
	//create new object to quit
	WordInfo quitRequest(){
		WordInfo tempObj = new WordInfo();
		tempObj.quit = true;
		tempObj.serverMessage = "clicked quit";
		return tempObj;
	}
}
