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
		sceneMap.put("select category", selectCategory());
		sceneMap.put("listen", listenForServer());
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
		/*Input ip address and port number
	
			________________________________
			|                               |
			|	IP Address					|
			|	__________					|
			|	__________		|Connect|	|
			|								|
			|	PortNumber					|
			|   __________                  |
			| 	__________  				|
			|	      						|
			|								|
			|								|
			|								|
			________________________________
		*/
	
		//Widgets for screen
		Label labIP = new Label("IP Address");
		Label labPort = new Label("Port Number");
		TextField enterIP = new TextField();
		TextField enterPort = new TextField();
		Button btnConnect = new Button("Connect");
		
		//set default values in box for faster testing
		enterIP.setText("127.0.0.1");
		enterPort.setText("5555");
		
		//create boxes to format things
		VBox col1 = new VBox(labIP, enterIP, labPort, enterPort);
		VBox col2 = new VBox(btnConnect);
		HBox clientBox = new HBox(col1, col2);
		
		//Connect button event handler
		btnConnect.setOnAction(e->{
			//change scenes
			//window.setScene(sceneMap.get("game"));
			window.setScene(sceneMap.get("select category"));
			
			//get our input for client
			IPAddress = enterIP.getText();
			portNum = Integer.parseInt(enterPort.getText());
			window.show();
			
			//Received a message from the server
			clientConnection = new Client(data->
			{
				Platform.runLater(()->
				{
					gameLogic((WordInfo) data);
								
				});
			}, IPAddress, portNum);
			clientConnection.start();
			
		});
		return new Scene(clientBox, 600, 500);
		
	}

public Scene selectCategory(){
	/*This will be the scene that lets the client
	pick the specific category that they want
			________________________________
			|                       Lives: 3|
			| 	                 			|
			|								|
			|		Select a Category		|
			|								|
			|								|
			|    |Fruit| |Color| |Animal|   |
			| 								|
			|	                			|
			|								|
			|								|
			|								|
			 ________________________________
	*/
	
	//BorderPane will have the top display the Lives and the 
	//center display the narration and buttons
	BorderPane layout = new BorderPane();

	//this text is for the lives
	Text livesText = new Text("Lives: " + clientConnection.lives);
	livesText.setFont(SMALLER_FONT);
	livesText.setTextAlignment(TextAlignment.CENTER);

	//This VBox will hold the narration and buttons and put them in the 
	//center
	VBox centerLayout = new VBox();
	centerLayout.setSpacing(200);

	//this text is for the narration
	Text selectCategoryText = new Text("Select Category");
	selectCategoryText.setFont(NARRATION_FONT);

	//This HBox is for the buttons
	HBox buttonLayout = new HBox();
	buttonLayout.setSpacing(15);

	//Create the buttons for the categories
	Button fruitBtn = new Button("Fruit");
	Button colorBtn = new Button("Color");
	Button animalBtn = new Button("Animal");

	//Create event handlers for when the player picks a category
	//(selectCategory(int category) is a helper function)
	fruitBtn.setOnAction(e -> {
		selectCategory(1);
	});

	colorBtn.setOnAction(e -> {
		selectCategory(2);
	});

	animalBtn.setOnAction(e -> {
		selectCategory(3);
	});

	//Add the buttons in the HBox
	buttonLayout.getChildren().addAll(fruitBtn, colorBtn, animalBtn);
	buttonLayout.setAlignment(Pos.CENTER);

	//Add the narration and buttonLayout in the VBox
	centerLayout.getChildren().addAll(selectCategoryText, buttonLayout);

	//Add the text and other information in their respected positions
	layout.setTop(livesText);
	layout.setCenter(centerLayout);

	//create scene and return
	Scene scene = new Scene(layout, WIDTH, HEIGHT);
	return scene;
}
public void selectCategory(int category){
	//This is a helper function used to send a category to the client

	WordInfo wordInfo = categoryRequest(category);
	clientConnection.send(wordInfo);

	window.setScene(sceneMap.get("listen"));
	window.show();

}

public Scene listenForServer(){
	/*This scene will display until the server has
	sent the WordInfo back to the client for the game
			________________________________
			|                               |
			| 	                 			|
			|								|
			|		                    	|
			|								|
			|	   Waiting on Server...		|
			|    						    |
			| 								|
			|	                			|
			|								|
			|								|
			|								|
			 ________________________________
	*/

	//VBox that will handle the text (and possibly any animation that I want to include)
	VBox layout = new VBox();
	layout.setSpacing(20);
	
	//This text lets the user know that we are waiting on server
	Text waitingOnServerText = new Text("Waiting on Server");
	waitingOnServerText.setFont(NARRATION_FONT);
	waitingOnServerText.setWrappingWidth(300);
	waitingOnServerText.setTextAlignment(TextAlignment.CENTER);

	/*TODO: Create something that gets the WordInfo from the client
	and moves to the next scene. For example:
	
	while(true){
		if(clientConnection.readObject() != null){
			//This means client has recieved the object
			window.setScene(sceneMap.get("guess a letter"));
			window.show();
		}
	}
	
	*/

	//Add text to the layout
	layout.getChildren().addAll(waitingOnServerText);
	layout.setAlignment(Pos.CENTER);

	//Add layout to the scene and return
	Scene scene(layout, WIDHT, HEIGHT);
	return scene;
}

	//temporary scene for testing the server
	public Scene createGame() {
		
		//Widgets
		TextField tempTxt = new TextField();
		Button sendGuess = new Button("Send Guess");
		Button sendCat = new Button("Send Cat");
		tempList = new ListView<String>();
		VBox clientBox = new VBox(10, tempList, tempTxt, sendGuess, sendCat );
		 
		//button for sending a letter
		sendGuess.setOnAction(e->
		{
			if(tempTxt.getText().length() == 1) {
				//create a new object with the guess and send
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
		
		//button for sending a category
		sendCat.setOnAction(e->
		{
			int x = Integer.parseInt(tempTxt.getText());
			if( x >= 1 && x <= 3) {
				//create a new object with category and send
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
	
	//Function called inside Platform Runlater
	void gameLogic(WordInfo input) {
		//keep track of categories cleared and guesses on client
		++clientConnection.serverResponses;
		System.out.println("num of server responses: " + clientConnection.serverResponses);
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
	}
}
