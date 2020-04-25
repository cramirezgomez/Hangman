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

import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

public class WordGuessClient extends Application {
	//Things needed for the GUI
	protected Stage window;
	protected HashMap<String, Scene> sceneMap;
	public final int WIDTH = 600;
	public final int HEIGHT = 500;
	public final Font TITLE_FONT = new Font("Gil Sans", 50);
	public final Font NARRATION_FONT = new Font("Gil Sans", 20);
	public final Font SMALLER_FONT = new Font("Gil Sans", 15);


	//Things needed for the Client-Server connection
	Client clientConnection;
	ListView<String> tempList;
	int portNum;
	String IPAddress;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		/*Set the window to the primaryStage.
		 This is to ensure that we can call the window 
		 whenever we need to for other functions. 
		*/
		window = primaryStage;
		window.setTitle("(Client) Word Guess!!!");
		
		/*We create the scenes and put them here. 
		As the number of scenes grow we may need to move this
		 into a method to keep the clutter to a minimum
		*/
		sceneMap = new HashMap<String, Scene>();
		sceneMap.put("title", titlePage());
		sceneMap.put("start",  createStart());
		sceneMap.put("game",  createGame());
		
		window.setScene(sceneMap.get("title"));
		window.show();
		
		window.setOnCloseRequest(new EventHandler<WindowEvent>() {
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


public Scene titlePage() {
	/*This will just be the start page, so for now, 
	it will just have the title and the button to 
	start the game. May add further things like 
	background image and fade ins later
			________________________________
			|                               |
			| 	  Welcome to Word Guess!	|
			|								|
			|								|
			|								|
			|								|
			|                               |
			| 								|
			|	      |Start Game|			|
			|								|
			|								|
			|								|
			________________________________
	*/

	VBox layout = new VBox();
	layout.setSpacing(200);
	
	//This will be for the title
	Text title = new Text("Welcome to Word Guess!");
	title.setFont(TITLE_FONT);
	title.setWrappingWidth(300);
	title.setTextAlignment(TextAlignment.CENTER);

	//This will be for the button
	Button startGameButton = new Button("Start Game");
	startGameButton.setOnAction(e -> {
		//set the scene to createStart()
		window.setScene(sceneMap.get("start"));
		window.show();
	});

	//Add these two nodes to the layout and return
	layout.getChildren().addAll(title, startGameButton);
	Scene scene = new Scene(layout, WIDTH, HEIGHT);
	return scene;
}
	
public Scene createStart() {
		
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
			window.setScene(sceneMap.get("game"));
			
			IPAddress = enterIP.getText();
			portNum = Integer.parseInt(enterPort.getText());
			
			window.show();
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

	public Scene createGame() {
		
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
