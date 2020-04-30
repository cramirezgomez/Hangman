import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;

class GuessTest {

	WordInfo serverInfo;
	Server serverConnection;
	
	Server.ClientThread clientT;
	
	@BeforeEach
	void defaultN()
	{

		serverConnection = new Server(data -> 
		{
			try{
			Platform.runLater(()->
			{
				serverInfo = (WordInfo) data;
			});
			}
			catch(Exception e){}
		}, 5555);

		clientT = serverConnection.new ClientThread();
		
	}
	
	//Test Play Again (1)
	@Test 
	void testPlayAgain()
	{
		assertEquals(1, clientT.execLogic(playAgainRequest()), "Player was not able to play again"); 
	}
	
	//Test Quit Game (2)
	@Test 
	void testQuitGame()
	{
		assertEquals(2, clientT.execLogic(quitRequest()), "Player was not able to play again"); 
	}
	
	//Test Guess (3)
	@Test 
	void testGuess()
	{
		assertEquals(3, clientT.execLogic(guessRequest('m')), "Player was not able to play again"); 
	}
	
	//Test Categories (4)
	@Test
	void testCategory1()
	{	
		assertEquals(4, clientT.execLogic(categoryRequest(1)), "Category 1 was not picked"); 
	}
	
	@Test
	void testCategory2()
	{	
		assertEquals(4, clientT.execLogic(categoryRequest(2)), "Category 2 was not picked"); 
	}
	
	@Test
	void testCategory3()
	{	
		assertEquals(4, clientT.execLogic(categoryRequest(3)), "Category 3 was not picked"); 
	}
	
	//Test word Length for Categories
	@Test
	void testWordLength()
	{
		clientT.pickWordFromBank(1); 
		WordInfo lengthInfo = clientT.prepareLength();
		
		assertEquals(lengthInfo.wordLength, clientT.curWord.length(), "Word Lenght and Current Word Lenght are not the same");
	}
	
	//Test Correct Positions 
	@Test 
	void testCorrectPositions()
	{
		clientT.curWord = "apple";
		WordInfo results = clientT.handleGuess(guessRequest('p'));
		
		assertEquals(2, results.positions.size(), "Not the right number of positions");
	}
	
	//Test incorrect Positions 
	@Test 
	void testincorrectPositions()
	{
		clientT.curWord = "apple";
		WordInfo results = clientT.handleGuess(guessRequest('m'));
			
		assertEquals(0, results.positions.size(), "Should be no positions for that guess");
	}
	
	//Test Word Info 
	//@Test
	//void 
	
	//----------------------------------------------------------------------------
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
	//----------------------------------------------------------------------------
	
}
