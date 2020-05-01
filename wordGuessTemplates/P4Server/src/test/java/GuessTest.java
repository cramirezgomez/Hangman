import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
	
	@Test
	void getServerName()
	{
		assertEquals("Server", serverConnection.getClass().getName());
	}
	
	@Test
	void getClintThreadName()
	{
		assertEquals("Server$ClientThread", clientT.getClass().getName());
	}
	
	//Test Server Port 
	@Test 
	void portTest()
	{
		assertEquals(5555, serverConnection.portNum, "Not the right port number");
	}
	
	//Test Word Bank Reset
	@Test 
	void wordBankResetTest()
	{
		clientT.wordBank1.remove(5);
		
		clientT.wordBank2.remove(5);
		clientT.wordBank2.remove(5);
		
		clientT.wordBank3.remove(5);
		clientT.wordBank3.remove(5);
		clientT.wordBank3.remove(5);
		
		clientT.curWord = "Test";
		
		clientT.resetWordBanks();
		
		assertEquals(10, clientT.wordBank1.size(), "Word Bank 1 does not have the default amount of elements");
		assertEquals(10, clientT.wordBank2.size(), "Word Bank 2 does not have the default amount of elements");
		assertEquals(10, clientT.wordBank3.size(), "Word Bank 3 does not have the default amount of elements");
		assertEquals(" ", clientT.curWord, "Current Word is not blank");
		
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
		assertEquals(3, clientT.execLogic(guessRequest('m')), "Player was not able to make a guess"); 
	}
	
	//Test Categories (4)
	@Test
	void testCategory1()
	{	
		assertEquals(4, clientT.execLogic(categoryRequest(1)), "Category 1 was not picked"); 
		assertEquals(9, clientT.wordBank1.size(), "Word Bank 1 was not reduced (Category)");
	}
	
	@Test
	void testCategory2()
	{	
		assertEquals(4, clientT.execLogic(categoryRequest(2)), "Category 2 was not picked"); 
		assertEquals(9, clientT.wordBank2.size(), "Word Bank 2 was not reduced (Category)");
	}
	
	@Test
	void testCategory3()
	{	
		assertEquals(4, clientT.execLogic(categoryRequest(3)), "Category 3 was not picked"); 
		assertEquals(9, clientT.wordBank3.size(), "Word Bank 3 was not reduced (Category)");
	}
	
	//Test word Length for Categories
	@Test
	void testWordLength1()
	{
		clientT.pickWordFromBank(1); 
		WordInfo lengthInfo = clientT.prepareLength();
		
		assertEquals(lengthInfo.wordLength, clientT.curWord.length(), "Word Lenght and Current Word Lenght are not the same (1)");
		assertEquals(9, clientT.wordBank1.size(), "Word Bank 1 was not reduced (Lenght)");
	}
	
	@Test
	void testWordLength2()
	{
		clientT.pickWordFromBank(2); 
		WordInfo lengthInfo = clientT.prepareLength();
		
		assertEquals(lengthInfo.wordLength, clientT.curWord.length(), "Word Lenght and Current Word Lenght are not the same (2)");
		assertEquals(9, clientT.wordBank2.size(), "Word Bank 2 was not reduced (Lenght)");
	}
	
	@Test
	void testWordLength3()
	{
		clientT.pickWordFromBank(3); 
		WordInfo lengthInfo = clientT.prepareLength();
		
		assertEquals(lengthInfo.wordLength, clientT.curWord.length(), "Word Lenght and Current Word Lenght are not the same (3)");
		assertEquals(9, clientT.wordBank3.size(), "Word Bank 3 was not reduced (Lenght)");
	}
	
	//Test Correct Guess 
	@Test 
	void testCorrectGuess()
	{
		clientT.curWord = "apple";
		WordInfo results = clientT.handleGuess(guessRequest('p'));
		
		assertEquals(2, results.positions.size(), "Not the right number of positions");
	}
	
	//Test incorrect Guess 
	@Test 
	void testincorrectGuess()
	{
		clientT.curWord = "apple";
		WordInfo results = clientT.handleGuess(guessRequest('m'));
			
		assertEquals(0, results.positions.size(), "Should be no positions for that guess");
	}
	
	//Test Correct Index Positions
	@ParameterizedTest
	@ValueSource(ints = { 0, 1, 2, 3}) 
	void correctIndexes(int input)
	{
		clientT.curWord = "mmmmllll";
		WordInfo results = clientT.handleGuess(guessRequest('m'));
		
		assertEquals(input, results.positions.get(input), "Not the right Indexes");
		
	}
	
	
	//Testing Functions taken from the Server Class
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
