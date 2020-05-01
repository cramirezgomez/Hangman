import java.util.ArrayList;
import java.util.HashMap;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
	//--Things needed for the GUI--//
	
	//Things needed to display
	protected Stage window;
	protected HashMap<String, Scene> sceneMap;
	
	//Things needed for dimensions
	public final int WIDTH = 600;
	public final int HEIGHT = 500;
	
	//Things needed for the font
	public final Font TITLE_FONT = new Font("Gil Sans", 50);
	public final Font NARRATION_FONT = new Font("Gil Sans", 20);
	public final Font SMALLER_FONT = new Font("Gil Sans", 15);
	
	//Widgets needed in other functions
	Text wordText ;
	ImageView fruitImage;
	ImageView colorImage;
	ImageView animalImage;
	HashMap<Button, Double> buttonMap;
	Text narrationText;
	
	Text livesText1Cat;
	Text livesText2Cat;
	Text livesText3Cat;
	
	Text livesText1Guess;
	Text livesText2Guess;
	Text livesText3Guess;


	//--Things needed for the Client-Server connection--//
	public Client clientConnection = new Client();
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
		window.getIcons().add(new Image("Game Logo.png"));
		
		/*We create the scenes and put them here. 
		As the number of scenes grow we may need to move this
		 into a method to keep the clutter to a minimum
		*/
		sceneMap = new HashMap<String, Scene>();
		sceneMap.put("title page", titlePage());
		sceneMap.put("create start",  createStart());
		sceneMap.put("select category", selectCategory());
		sceneMap.put("guess letter", guessLetter());
		sceneMap.put("win layout", winLoseLayout(true));
		sceneMap.put("lose layout", winLoseLayout(false));
		
		//set the scene to the title page and show
		window.setScene(sceneMap.get("title page"));
		window.show();
		
		//disconnect client to server when exiting game
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

	//These functions are the scenes that the game uses
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
		
		//Main layout will show the image, title, and start button
		HBox layout = new HBox();
		layout.setSpacing(10);
		layout.setStyle("-fx-background-color: burlywood");
		
		//This is the image
		ImageView hangmanImage = new ImageView(new Image("Hangman Icon.png"));
		hangmanImage.setFitWidth(250);
		hangmanImage.setFitHeight(500);
		hangmanImage.setPreserveRatio(true);
		
		//This layout will show the title and button
		VBox titleLayout = new VBox();
		titleLayout.setSpacing(100);
		
		//This will be for the title
		Text title = new Text("Welcome to Word Guess!");
		title.setFont(TITLE_FONT);
		title.setWrappingWidth(300);
		title.setTextAlignment(TextAlignment.CENTER);
	
		//This will be for the button
		Button startGameButton = new Button("Start Game");
		startGameButton.setStyle("-fx-base: chocolate");
		startGameButton.setOnAction(e -> {
			//set the scene to createStart()
			window.setScene(sceneMap.get("create start"));
			window.show();
		});
	
		//Add title and button to title layout
		titleLayout.getChildren().addAll(title, startGameButton);
		titleLayout.setAlignment(Pos.CENTER);
		
		//Add image and title layout to main layout
		layout.getChildren().addAll(hangmanImage, titleLayout);
		layout.setAlignment(Pos.CENTER_LEFT);
		
		//Added fade in transition when the game starts
		FadeTransition fadeIn = fadeIn(3.0);
		fadeIn.setNode(layout);
		fadeIn.play();
		
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
		col1.setSpacing(10);
		col1.setAlignment(Pos.CENTER);
		
		VBox col2 = new VBox(btnConnect);
		col2.setAlignment(Pos.CENTER);
		
		HBox clientBox = new HBox(col1, col2);
		clientBox.setSpacing(20);
		clientBox.setAlignment(Pos.CENTER);
		clientBox.setStyle("-fx-background-color: burlywood");
		
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
		
		btnConnect.setGraphic(makeImageView(new Image("Confirm Icon.png"), 15, 15));
		btnConnect.setStyle("-fx-base: forestgreen");
		
		//Added fade in transition
		FadeTransition fade = fadeIn(1.0);
		fade.setNode(clientBox);
		fade.play();
		
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
		layout.setStyle("-fx-background-color: burlywood");
		
		//This VBox will hold the information regarding the player's life
		//and put them in the top
		VBox topLayout = new VBox();
		
		//this text is for the lives
		livesText1Cat = new Text("Fruit Lives: " + clientConnection.catLives.get(0));
		livesText2Cat = new Text("Color Lives: " + clientConnection.catLives.get(1));
		livesText3Cat = new Text("Animal Lives: " + clientConnection.catLives.get(2));
		livesText1Cat.setFont(SMALLER_FONT);
		livesText2Cat.setFont(SMALLER_FONT);
		livesText3Cat.setFont(SMALLER_FONT);
		
		//Add the text to the topLayout
		topLayout.getChildren().addAll(livesText1Cat, livesText2Cat, livesText3Cat);
		topLayout.setAlignment(Pos.TOP_RIGHT);
		
		//This VBox will hold the narration and buttons and put them in the 
		//center
		VBox centerLayout = new VBox();
		centerLayout.setSpacing(100);
	
		//this text is for the narration
		Text selectCategoryText = new Text("Select Category");
		selectCategoryText.setFont(NARRATION_FONT);
	
		//This HBox is for the buttons
		HBox buttonLayout = new HBox();
		
		//Create image-buttons for the categories
		fruitImage = makeImageView(new Image("Fruit Icon.png"), 200, 200);
		colorImage = makeImageView(new Image("Color Icon.png"), 200, 200);
		animalImage = makeImageView(new Image("Animal Icon.png"), 200, 200);
	
		//Create event handlers for when the player picks a category
		makeCategoryEvents();
	
		//Add the image-buttons to the button layout
		buttonLayout.getChildren().addAll(fruitImage, colorImage, animalImage);
		buttonLayout.setAlignment(Pos.CENTER);
	
		//Add the narration and buttonLayout in the center layout
		centerLayout.getChildren().addAll(selectCategoryText, buttonLayout);
		centerLayout.setAlignment(Pos.CENTER);
	
		//Add the layouts to their respected places in the border pane
		layout.setTop(topLayout);
		layout.setCenter(centerLayout);
		
		//Added fade in transition
		FadeTransition fade = fadeIn(1.0);
		fade.setNode(layout);
		fade.play();
		
		//create scene and return
		Scene scene = new Scene(layout, WIDTH, HEIGHT);
		return scene;
	}
	
	public Scene guessLetter() {
		/*This function will only have the user guess a letter.
		We will be using 26 buttons (man this is daunting) and so we
		will be having several helper functions to accommodate for this.
		In case the code gets confusing, I will indent the code to let you 
		see which node/layout belongs to which node/layout.
			________________________________
			|                       Lives: 3|
			| 	                 	Lives: 3|
			|						Lives: 3|
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
		layout.setStyle("-fx-background-color: burlywood");
			//This VBox will hold the information regarding the player's life
			//and put them in the top
			VBox topLayout = new VBox();
				livesText1Guess = new Text("Fruit Lives: " + clientConnection.catLives.get(0));
				livesText2Guess = new Text("Color Lives: " + clientConnection.catLives.get(1));
				livesText3Guess = new Text("Animal Lives: " + clientConnection.catLives.get(2));
				livesText1Guess.setFont(SMALLER_FONT);
				livesText2Guess.setFont(SMALLER_FONT);
				livesText3Guess.setFont(SMALLER_FONT);
			topLayout.getChildren().add(livesText1Guess);
			topLayout.getChildren().add(livesText2Guess);
			topLayout.getChildren().add(livesText3Guess);
			topLayout.setAlignment(Pos.TOP_RIGHT);
			
			//Create the centerLayout. This will tell the user the information to
			//play, and hold the buttons the user will need to play
			VBox centerLayout = new VBox();
			centerLayout.setSpacing(75);
				int guesses = clientConnection.guesses;
				narrationText = new Text("Guess a letter. You have " + guesses + " guesses left.");
				narrationText.setFont(NARRATION_FONT);
				narrationText.setWrappingWidth(300);
				narrationText.setTextAlignment(TextAlignment.CENTER);
				String word = clientConnection.curWord;
				wordText = new Text(word);
				wordText.setFont(NARRATION_FONT);
				wordText.setWrappingWidth(300);
				wordText.setTextAlignment(TextAlignment.CENTER);
				//Create a space for the user to place their answer.
				//This will be the 26 buttons and the confirm button.
				HBox userInputLayout = new HBox();
				userInputLayout.setSpacing(50);
					//Create confirm button
					Button confirmButton = new Button("Confirm");
					confirmButton.setGraphic(makeImageView(new Image("Confirm Icon.png"), 10, 10));
					confirmButton.setStyle("-fx-base: forestgreen");
					confirmButton.setDisable(true);
					confirmButton.setOnAction(e -> {
						confirmButton.setDisable(true);
						//sends an updated WordInfo to the server.
						clientConnection.send(wordInfo); 
						//search for the correct button to disable
						buttonMap.forEach((button, value) -> {
							if(button.getText().toLowerCase().charAt(0) == wordInfo.guess) {
								button.setDisable(true);
							}
						});
						
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
							buttonMap = makeAlphabetMap();
							ArrayList<Button> row1Button = makeButtonList(buttonMap, 1.0, 2.0);
							ArrayList<Button> row2Button = makeButtonList(buttonMap, 2.0, 3.0);
							ArrayList<Button> row3Button = makeButtonList(buttonMap, 3.0, 4.0);
							//Assign each button to one of three rows (much like a keyboard)
							//and have each button perform the same action event
							for(Button b : row1Button) {
								b.setStyle("-fx-base: sandybrown");
								row1.getChildren().add(b);
							}
							for(Button b : row2Button) {
								b.setStyle("-fx-base: sandybrown");
								row2.getChildren().add(b);
							}
							for(Button b : row3Button) {
								b.setStyle("-fx-base: sandybrown");
								row3.getChildren().add(b);
							}
							//setup event handlers for all the buttons
							buttonMap.forEach((button, value) -> {
								button.setOnAction(e->
								{
									confirmButton.setDisable(false);
									wordInfo = guessRequest(button.getText().toLowerCase().charAt(0));
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

	public Scene winLoseLayout(boolean didPlayerWin) {
		VBox layout = new VBox();
		layout.setSpacing(100);
		layout.setStyle("-fx-background-color: burlywood");
		
		Text winLoseText = new Text();
		winLoseText.setFont(NARRATION_FONT);
		winLoseText.setTextAlignment(TextAlignment.CENTER);
		winLoseText.setWrappingWidth(300);
		
		 if(didPlayerWin){
			winLoseText.setText("You won, congratulations!");
		 }
		 else{
			winLoseText.setText("You lost, better luck next time");
		}
		
		HBox buttonLayout = new HBox();
		buttonLayout.setSpacing(15);
		
		Button playAgainButton = new Button("Play Again");
		Button exitButton = new Button("Exit Game");
		
		playAgainButton.setStyle("-fx-base: chocolate");
		playAgainButton.setOnAction(e -> {
			selectPlayAgain();
		});
		exitButton.setStyle("-fx-base: firebrick");
		exitButton.setOnAction(e -> {
			selectQuit();
		});
		
		buttonLayout.getChildren().addAll(playAgainButton, exitButton);
		buttonLayout.setAlignment(Pos.CENTER);
		
		layout.getChildren().addAll(winLoseText, buttonLayout);
		layout.setAlignment(Pos.CENTER);
		
		//Added fade in transition when the game starts
		FadeTransition fade = fadeIn(1.0);
		fade.setNode(layout);
		fade.play();
		
		Scene scene = new Scene(layout, WIDTH, HEIGHT);
		return scene;
	}
	
	//Function called inside Platform Runlater
	void gameLogic(WordInfo input) {
		//keep track of categories cleared and guesses on client
		++clientConnection.serverResponses;
		//System.out.println("num of server responses: " + clientConnection.serverResponses);
		
		//welcome message was sent
		if(clientConnection.serverResponses == 1) 
		{
			//System.out.println("Recieved: welcome message");
		}
		//length of the word is being sent
		else if(input.wordLength != 0) {
			//System.out.println("Recieved: length of word is " + input.wordLength);
			clientConnection.lettersLeft = input.wordLength;
			clientConnection.curWord = "";
			for(int i = 0; i < input.wordLength; ++i) {
				clientConnection.curWord = clientConnection.curWord + "*";
			}
			wordText.setText(clientConnection.curWord);
		}
		//guess response is being done
		else
		{
			//our guesss was correct
			if(input.isCorrect){
				//System.out.println("Recieved: guess was correct");
				clientConnection.lettersLeft = clientConnection.lettersLeft - input.positions.size();
				input.positions.forEach(e -> {
					clientConnection.curWord = clientConnection.curWord.substring(0, e) + Character.toUpperCase(wordInfo.guess) +
											   clientConnection.curWord.substring(e + 1);
				});
				//word was guessed
				if(clientConnection.lettersLeft == 0) {
					//System.out.println("Recieved: word was guessed");
					switch(clientConnection.curCat)
					{
						case 1: 
							//System.out.println("1");
							fruitImage.setDisable(true);
							fruitImage.setOpacity(0.5);
							break;
						case 2:
							//System.out.println("2");
							colorImage.setDisable(true);
							colorImage.setOpacity(0.5);
							break;
						case 3:
							//System.out.println("3");
							animalImage.setDisable(true);
							animalImage.setOpacity(0.5);
							break;
						default:
							
							//System.out.println("error");
							//error
							break;
					}
					
					window.setScene(sceneMap.get("win layout"));
					
					enableKeyboard();
					clientConnection.resetGuesses();
					//game is won
					if(colorImage.isDisabled() && fruitImage.isDisabled() && animalImage.isDisabled())
					{
						//System.out.println("game was cleared");
						enableCategories();
					}
					//cat is won
					else {
						//System.out.println("cat cleared");
						
						window.setScene(sceneMap.get("select category"));
						window.show();
						
					}
					
				}
				//update current word
				wordText.setText(clientConnection.curWord);
				
				
			}
			//our guess was incorrect
			else {
				--clientConnection.guesses;
				narrationText.setText("Guess a letter. You have " + clientConnection.guesses + " guesses left.");
				//System.out.println("Recieved: guess was wrong");
			}
			
			//ran out of guesses
			if(clientConnection.guesses == 0) {
				int pos = clientConnection.curCat - 1;
				int val= clientConnection.catLives.get(pos) - 1;
				clientConnection.catLives.set(pos, val);

				
				
				livesText1Cat.setText("Fruit Lives: " + clientConnection.catLives.get(0));
				livesText2Cat.setText("Color Lives: " + clientConnection.catLives.get(1));
				livesText3Cat.setText("Animal Lives: " + clientConnection.catLives.get(2));
				livesText1Guess.setText("Fruit Lives: " + clientConnection.catLives.get(0));
				livesText2Guess.setText("Color Lives: " + clientConnection.catLives.get(1));
				livesText3Guess.setText("Animal Lives: " + clientConnection.catLives.get(2));
				
				enableKeyboard();
			   	clientConnection.resetGuesses();
			   	
				//ran out of lives
				if(clientConnection.catLives.get(0) == 0 || 
				   clientConnection.catLives.get(1) == 0 || 
				   clientConnection.catLives.get(2) == 0) 
				{
				   //System.out.println("Also Recieved: ran out of lives");
				   window.setScene(sceneMap.get("lose layout"));
				   enableCategories();
					
				}
				else 
				{
					//System.out.println("Also Recieved: ran out of guesses");
					window.setScene(sceneMap.get("select category"));
					window.show();
				}
			}
		}
	}
	
	
	//===========================HELPER FUNCTIONS=======================//
	//=======HELPER FUNCTIONS=======//
	//These are functions that are related to the GUI and help the layouts
	//							function properly.
	
	//These functions add effects to layouts
	
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
	//Helper functions that make special effects
	public FadeTransition fadeIn(double seconds) {
		FadeTransition fade = new FadeTransition();
		fade.setDuration(Duration.seconds(seconds));
		fade.setFromValue(0.01);
		fade.setToValue(1);
		
		return fade;
	}
	public FadeTransition fadeOut(double seconds) {
		FadeTransition fade = new FadeTransition();
		fade.setDuration(Duration.seconds(seconds));
		fade.setFromValue(1);
		fade.setToValue(0);
		
		return fade;
	}
	public DropShadow makeShadow() {
		DropShadow shadow = new DropShadow();
		shadow.setHeight(10);
		shadow.setWidth(10);
		shadow.setRadius(10);
		shadow.setOffsetX(5);
		shadow.setOffsetY(5);
		shadow.setBlurType(BlurType.GAUSSIAN);
		shadow.setColor(Color.CHOCOLATE);
		
		return shadow;
	}
	
	//These functions modify the nodes being used in certain layouts
	//Helper functions that effect the nodes in the layouts
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
	public ImageView makeImageView(Image image, double width, double height) {
		ImageView imageView = new ImageView(image);
		imageView.setFitWidth(width);
		imageView.setFitHeight(height);
		imageView.setPreserveRatio(true);
		return imageView;
	}
	public void makeCategoryEvents() {
		DropShadow shadow = makeShadow();
		
		fruitImage.setOnMouseClicked(e -> {
			clientConnection.curCat = 1;
			selectCategory(1);
		});
		fruitImage.setOnMouseEntered(e -> {
			fruitImage.setEffect(shadow);
		});
		fruitImage.setOnMouseExited(e -> {
			fruitImage.setEffect(null);
		});
		
		colorImage.setOnMouseClicked(e -> {
			clientConnection.curCat = 2;
			selectCategory(2);
		});
		colorImage.setOnMouseEntered(e -> {
			colorImage.setEffect(shadow);
		});
		colorImage.setOnMouseExited(e -> {
			colorImage.setEffect(null);
		});
		
		animalImage.setOnMouseClicked(e ->{
			clientConnection.curCat = 3;
			selectCategory(3);
		});
		animalImage.setOnMouseEntered(e -> {
			animalImage.setEffect(shadow);
		});
		animalImage.setOnMouseExited(e -> {
			animalImage.setEffect(null);
		});
	}
	void enableKeyboard() {
		buttonMap.forEach((button, value) -> {
				button.setDisable(false);
		});
	}
	void enableCategories() {
		fruitImage.setDisable(false);
		colorImage.setDisable(false);
		animalImage.setDisable(false);
	}
	
	//These functions send messages to server
	//Helper functions that send information to server
	public void selectCategory(int category){
		//This is a helper function used to send a category to the client
	
		wordInfo = categoryRequest(category);
		clientConnection.send(wordInfo);
	
		window.setScene(sceneMap.get("guess letter"));
		window.show();
	}
	//create new object to pick category
	public void selectPlayAgain() {
		wordInfo = playAgainRequest();
		clientConnection.send(wordInfo);
		clientConnection.resetVariables();
		try {
			start(window);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void selectQuit() {
		wordInfo = playAgainRequest();
		clientConnection.send(wordInfo);
		window.close();
	}
	
	//These functions create the object that will be sent to server
	WordInfo categoryRequest(int x){
		WordInfo tempObj = new WordInfo();
		tempObj.category = x;
		tempObj.serverMessage = "Picked category: ";
		return tempObj;
	}
	WordInfo guessRequest(char x){
		WordInfo tempObj = new WordInfo();
		tempObj.guess = x;
		tempObj.serverMessage = "Sent guess: ";
		return tempObj;
	}
	WordInfo playAgainRequest(){
		WordInfo tempObj = new WordInfo();
		tempObj.playAgain = true;
		tempObj.serverMessage = "Clicked play again";
		return tempObj;
	}
	WordInfo quitRequest(){
		WordInfo tempObj = new WordInfo();
		tempObj.quit = true;
		tempObj.serverMessage = "Clicked quit";
		return tempObj;
	}
		
}
