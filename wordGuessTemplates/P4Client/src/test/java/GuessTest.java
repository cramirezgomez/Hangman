import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;



class GuessTest {

	WordInfo clientInfo;
	Client clientConnection;
	
	@BeforeEach
	void defaultN()
	{
		
		clientInfo = new WordInfo();
		
		clientConnection = new Client(data -> 
		{
			Platform.runLater(()->
			{
				clientInfo = (WordInfo) data;
			});
		}, "127.0.0.1", 5555);
		
	}
	
	//Test Defaults 
	@Test 
	void getClassName()
	{
		assertEquals("Client", clientConnection.getClass().getName(), "Not the right the name");
	}
	
	@Test
	void testIPadress()
	{
		assertEquals("127.0.0.1", clientConnection.IPAddress, "Not the right adress");
	}
	
	@Test
	void testPort()
	{
		assertEquals(5555, clientConnection.portNumber, "Not the right port number");
	}
	
	@Test
	void otherDefaults()
	{
		assertEquals(6, clientConnection.guesses, "Not the right amount of guesses");
		assertEquals(3, clientConnection.catCleared.size(), "Not the right size for catCleared");
		assertEquals(3, clientConnection.catLives.size(), "Not the right size for catLives");
		assertEquals(0, clientConnection.serverResponses, "ServerResponses is not 0");
	}
	
	//Test Reseting the variables 
	@Test 
	void testResetVariables()
	{
		clientConnection.guesses = 10;
		clientConnection.catCleared.remove(1);
		clientConnection.catLives.remove(1);
		
		clientConnection.resetVariables();
		
		assertEquals(6, clientConnection.guesses, "Not the right amount of guesses (Reset Variables)");
		assertEquals(3, clientConnection.catCleared.size(), "Not the right size for catCleared (Reset Variables)");
		assertEquals(3, clientConnection.catLives.size(), "Not the right size for catLives (Reset Variables)");
		
	}

	//Test Reseting Guesses
	@Test
	void testResetGuess()
	{
		clientConnection.guesses = 25;
		
		clientConnection.resetGuesses();
		
		assertEquals(6, clientConnection.guesses, "Not the right amount of guesses (Reset Guess)");
	}
	
	//Test Word Info 
	@Test 
	void testDefaultConstructor()
	{
		assertEquals(0, clientInfo.wordLength, "Not the right Word Lenght");
		assertEquals(false, clientInfo.isCorrect, "Not the right isCorrect");
		assertEquals(" ", clientInfo.serverMessage, "Not the right Server Message");
		assertEquals(' ', clientInfo.guess, "Not the right default guess");
		assertEquals(0, clientInfo.category, "Not the right default category");
		assertEquals(false, clientInfo.playAgain, "Not the right value for play again");
		assertEquals(false, clientInfo.quit, "Not the right value for quit");
		
	}
	
	
}
