import java.util.ArrayList;
import java.util.HashMap;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
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
	public Client clientConnection = new Client();
	ListView<String> tempList;
	int portNum;
	String IPAddress;
	
	//WordInfo that will be sent to the server and back
	public WordInfo wordInfo = new WordInfo();
	
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
		sceneMap.put("guess letter", guessLetter());
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

	//TODO: Add graphics to code
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
		layout.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(layout, WIDTH, HEIGHT);
		return scene;
	}
	
	//TODO: add graphics to this
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
		col1.setSpacing(10);
		col1.setAlignment(Pos.CENTER);
		
		VBox col2 = new VBox(btnConnect);
		col2.setAlignment(Pos.CENTER);
		
		HBox clientBox = new HBox(col1, col2);
		clientBox.setSpacing(20);
		clientBox.setAlignment(Pos.CENTER);
		
		//Connect button event handler
		btnConnect.setOnAction(e->{
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
			
			//Change scene after client is updated
			window.setScene(sceneMap.get("select category"));
		});
		return new Scene(clientBox, 600, 500);
		
	}

	//TODO: Add graphics to code
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
	
		//This VBox will hold the information regarding the player's life
		//and put them in the top
		VBox topLayout = new VBox();
		
		//this text is for the lives
		Text livesText1 = new Text("Fruit Lives: " + clientConnection.catOneLives);
		Text livesText2 = new Text("Color Lives: " + clientConnection.catTwoLives);
		Text livesText3 = new Text("Animal Lives: " + clientConnection.catThreeLives);
		livesText1.setFont(SMALLER_FONT);
		livesText2.setFont(SMALLER_FONT);
		livesText3.setFont(SMALLER_FONT);
		
		//Add the text to the topLayout
		topLayout.getChildren().add(livesText1);
		topLayout.getChildren().add(livesText2);
		topLayout.getChildren().add(livesText3);
		
		topLayout.setAlignment(Pos.TOP_RIGHT);
		
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
		centerLayout.setAlignment(Pos.CENTER);
	
		//Add the text and other information in their respected positions
		layout.setTop(topLayout);
		layout.setCenter(centerLayout);
	
		//create scene and return
		Scene scene = new Scene(layout, WIDTH, HEIGHT);
		return scene;
	}
	
	//TODO: add graphics to code
	public Scene guessLetter() {
		/*This function will only have the user guess a letter.
		We will be using 26 buttons (man this is daunting) and so we
		will be having several helper functions to accommodate for this.
		In case the code gets confusing, I will indent the code to let you 
		see which node/layout belongs to which node/layout.
			________________________________
			|                       Lives: 3|
			| 	                 			|
			|								|
			|		 Guess a Letter			|
			|		You have _ guesses.		|
			|								|
			|    _ _ _ _ _ _ _ _ _ _ _ _    |
			| 	 _______________________	|
			|	|                		|	|
			|	|						|	|
			|	|						|	|
			|	-------------------------	|
			 ________________________________
		 */
		
		//Create layout that will house all of these nodes
		BorderPane layout = new BorderPane();
			//This VBox will hold the information regarding the player's life
			//and put them in the top
			VBox topLayout = new VBox();
				Text livesText1 = new Text("Fruit Lives: " + clientConnection.catOneLives);
				Text livesText2 = new Text("Color Lives: " + clientConnection.catTwoLives);
				Text livesText3 = new Text("Animal Lives: " + clientConnection.catThreeLives);
				livesText1.setFont(SMALLER_FONT);
				livesText2.setFont(SMALLER_FONT);
				livesText3.setFont(SMALLER_FONT);
			topLayout.getChildren().add(livesText1);
			topLayout.getChildren().add(livesText2);
			topLayout.getChildren().add(livesText3);
			topLayout.setAlignment(Pos.TOP_RIGHT);
			
			//Create the centerLayout. This will tell the user the information to
			//play, and hold the buttons the user will need to play
			VBox centerLayout = new VBox();
			centerLayout.setSpacing(100);
				int guesses = clientConnection.guesses;
				Text narrationText = new Text("Guess a letter. You have " + guesses + " guesses left.");
				narrationText.setFont(NARRATION_FONT);
				narrationText.setWrappingWidth(300);
				narrationText.setTextAlignment(TextAlignment.CENTER);
				//TODO: Find out how to get the word from the server
				String word = "______________";	//This will change when we get the word
				Text wordText = new Text(word);
				wordText.setFont(NARRATION_FONT);
				wordText.setWrappingWidth(300);
				wordText.setTextAlignment(TextAlignment.CENTER);
				//Create a space for the user to place their answer.
				//This will be the 26 buttons and the confirm button.
				HBox userInputLayout = new HBox();
				userInputLayout.setSpacing(50);
					//Create confirm button
					Button confirmButton = new Button("Confirm");
					confirmButton.setDisable(true);
					confirmButton.setOnAction(e -> {
						//TODO: send an updated WordInfo to the server.
						//	makeGuess(wordInfo.guess);
						clientConnection.send(wordInfo); 
						//TODO: put a transparent waiting background on top
						//	of the layout until the server spits back an updated
						//	WordInfo object.
					});
					//Create VBox that will house the rows
					VBox alphabetButtonLayout = new VBox();
					alphabetButtonLayout.setSpacing(6);
						//Create HBox that will house the buttons in each row
						HBox row1 = new HBox();
						HBox row2 = new HBox();
						HBox row3 = new HBox();
						row1.setSpacing(6);
						row2.setSpacing(6);
						row3.setSpacing(6);
						row1.setAlignment(Pos.CENTER);
						row2.setAlignment(Pos.CENTER);
						row3.setAlignment(Pos.CENTER);
							//Create the map with all the buttons
							HashMap<Button, Double> buttonMap = makeAlphabetMap();
							ArrayList<Button> row1Button = makeButtonList(buttonMap, 1.0, 2.0);
							ArrayList<Button> row2Button = makeButtonList(buttonMap, 2.0, 3.0);
							ArrayList<Button> row3Button = makeButtonList(buttonMap, 3.0, 4.0);
							//Assign each button to one of three rows (much like a keyboard)
							//and have each button perform the same action event
							for(Button b : row1Button) {
								/*b.setOnAction(e -> {
									confirmButton.setDisable(false);
									//TODO: Update WordInfo
								});*/
								row1.getChildren().add(b);
							}
							for(Button b : row2Button) {
								/*b.setOnAction(e -> {
									confirmButton.setDisable(false);
									//TODO: Update WordInfo
								});*/
								row2.getChildren().add(b);
							}
							for(Button b : row3Button) {
								/*b.setOnAction(e -> {
									confirmButton.setDisable(false);
									//TODO: Update WordInfo
								});*/
								row3.getChildren().add(b);
							}
							//setup event handlers for all the buttons
							buttonMap.forEach((button, value) -> {
								button.setOnAction(e->
								{
									confirmButton.setDisable(false);
									wordInfo = guessRequest(button.getText().toLowerCase().charAt(0));
									button.setDisable(true);
									//clientConnection.send(curGuess); 
								});
							});
							
							
					alphabetButtonLayout.getChildren().addAll(row1, row2, row3);
					alphabetButtonLayout.setAlignment(Pos.CENTER);
				userInputLayout.getChildren().addAll(alphabetButtonLayout, confirmButton);
				userInputLayout.setAlignment(Pos.CENTER);
				centerLayout.getChildren().addAll(narrationText, wordText, userInputLayout);	
				centerLayout.setAlignment(Pos.CENTER);
		layout.setTop(topLayout);
		layout.setCenter(centerLayout);
		
		Scene scene = new Scene(layout, WIDTH, HEIGHT);
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
	
	//Function called inside Platform Runlater
	void gameLogic(WordInfo input) {
		//keep track of categories cleared and guesses on client
		++clientConnection.serverResponses;
		System.out.println("num of server responses: " + clientConnection.serverResponses);
		tempList.getItems().add(input.serverMessage);
		
		//welcome message was sent
		if(clientConnection.serverResponses == 1) 
		{
			System.out.println("Recieved: welcome message");
		}
		//length of the word is being sent
		else if(input.wordLength != 0) {
			System.out.println("Recieved: length of word is " + input.wordLength);
		}
		//guess response is being done
		else
		{
			//our guesss was correct
			if(input.isCorrect){
				System.out.println("Recieved: guess was correct");
			}
			//our guess was incorrect
			else {
				System.out.println("Recieved: guess was wrong");
			}
			
			//ran out of guesses
			if(clientConnection.guesses == 0) {
				System.out.println("Also Recieved: ran out of guesses");
				//ran out of lives
				if(clientConnection.catOneLives == 0 || 
				   clientConnection.catTwoLives == 0 || 
				   clientConnection.catThreeLives == 0) {
					System.out.println("Also Recieved: ran out of lives");
				}
			}
		}
	}
	
	
	
	
	//=======HELPER FUNCTIONS=======//
	/*These are functions that are not specifically related to the GUI but
		help the layouts function properly.
	*/
	
	//Helper functions that effect the screen
	public VBox makeLoadingScreen() {
		//This will just be a loading layout that will appear when the user
		//makes a selection
		
		VBox layout = new VBox();
		layout.setOpacity(0.20);
		layout.setStyle("-fx-background-color: white");
		layout.setSpacing(100);
		
		Text loadingText = new Text("Loading");
		loadingText.setFont(NARRATION_FONT);
		
		//Create animation to show that you are waiting... I guess.
		//	This will be housed in a HBox
		HBox animationLayout = new HBox();
		animationLayout.setSpacing(20);
		
		Circle c1 = new Circle();
		Circle c2 = new Circle();
		Circle c3 = new Circle();
		c1.setRadius(10);
		c1.setFill(Color.BLACK);
		c2.setRadius(10);
		c2.setFill(Color.BLACK);
		c3.setRadius(10);
		c3.setFill(Color.BLACK);
		
		animationLayout.getChildren().addAll(c1, c2, c3);
		animationLayout.setAlignment(Pos.CENTER);
		
		layout.getChildren().addAll(loadingText, animationLayout);
		layout.setAlignment(Pos.CENTER);
		
		TranslateTransition t1 = new TranslateTransition();
		t1.setDuration(Duration.seconds(1.5));
		t1.setAutoReverse(true);
		t1.setByY(100);
		t1.setNode(c1);
		
		TranslateTransition t2 = new TranslateTransition();
		t2.setDuration(Duration.seconds(1.5));
		t2.setAutoReverse(true);
		t2.setByY(100);
		t2.setNode(c2);
		
		TranslateTransition t3 = new TranslateTransition();
		t3.setDuration(Duration.seconds(1.5));
		t3.setAutoReverse(true);
		t3.setByY(100);
		t3.setNode(c3);
		
		t1.play();
		t2.play();
		t3.play();
		
		return layout;
	}
	public HashMap<Button, Double> makeAlphabetMap(){
		//Helper function to make the alphabet buttons. They are 
		//categorized by the row and order they would appear in on the keyboard
		
		HashMap<Button, Double> alphabetMap = new HashMap<Button, Double>();
		
		//Row 1
		alphabetMap.put(new Button("Q"), 1.0);
		alphabetMap.put(new Button("W"), 1.1);
		alphabetMap.put(new Button("E"), 1.2);
		alphabetMap.put(new Button("R"), 1.3);
		alphabetMap.put(new Button("T"), 1.4);
		alphabetMap.put(new Button("Y"), 1.5);
		alphabetMap.put(new Button("U"), 1.6);
		alphabetMap.put(new Button("I"), 1.7);
		alphabetMap.put(new Button("O"), 1.8);
		alphabetMap.put(new Button("P"), 1.9);
		
		//Row 2
		alphabetMap.put(new Button("A"), 2.0);
		alphabetMap.put(new Button("S"), 2.1);
		alphabetMap.put(new Button("D"), 2.2);
		alphabetMap.put(new Button("F"), 2.3);
		alphabetMap.put(new Button("G"), 2.4);
		alphabetMap.put(new Button("H"), 2.5);
		alphabetMap.put(new Button("J"), 2.6);
		alphabetMap.put(new Button("K"), 2.7);
		alphabetMap.put(new Button("L"), 2.8);
		
		//Row 3
		alphabetMap.put(new Button("Z"), 3.0);
		alphabetMap.put(new Button("X"), 3.1);
		alphabetMap.put(new Button("C"), 3.2);
		alphabetMap.put(new Button("V"), 3.3);
		alphabetMap.put(new Button("B"), 3.4);
		alphabetMap.put(new Button("N"), 3.5);
		alphabetMap.put(new Button("M"), 3.6);
		
		
		return alphabetMap;
	}
	public ArrayList<Button> makeButtonList(HashMap <Button, Double> map, double floor, double ceiling){
		//Helper function that sorts the alphabet in it's proper order
		
		ArrayList<Button> list = new ArrayList<Button>();
		
		//Step 1: Iterate through hash map to find letters between threshold
		//		  and add them to the list
		map.forEach((button, value) -> {
			if(value >= floor && value < ceiling) {
				list.add(button);
			}
		});
		
		//Step 2: Sort the list to make the row for the keyboard
		for(int i = 0; i < list.size(); i++) {
			for(int j = i+1; j < list.size(); j++) {
				double firstValue = map.get(list.get(i));
				double secondValue = map.get(list.get(j));
				if(secondValue < firstValue) {
					Button tmp = list.get(j);
					list.set(j, list.get(i));
					list.set(i, tmp);
					
				}
			}
		}
		
		return list;
	}

	//Helper functions that send information to server
	public void selectCategory(int category){
		//This is a helper function used to send a category to the client
	
		wordInfo = categoryRequest(category);
		clientConnection.send(wordInfo);
	
		window.setScene(sceneMap.get("guess letter"));
		window.show();
	}
	public void makeGuess(char letter) {
		wordInfo = guessRequest(letter);
		clientConnection.send(wordInfo);
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
