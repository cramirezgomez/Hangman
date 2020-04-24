import java.io.Serializable;
import java.util.ArrayList;

public class WordInfo implements Serializable{
	
	private static final long serialVersionUID = 8073692471669315543L;

	//Update the client
	int wordLength;
	Boolean isCorrect;
	int guessesLeft;
	String serverMessage;
	ArrayList<Integer> positions;
	int wordsWrong;
	
	
	//Update the server 
	char guess;
	int category;
	Boolean playAgain;
	Boolean quit;
	
	WordInfo(){
		wordLength = 0;
		isCorrect = false;
		guessesLeft = 6;
		serverMessage = " ";
		positions = new ArrayList<Integer>();
		wordsWrong = 0;
		guess = ' ';
		category = 0;
		playAgain = false;
		quit = false;
	}
	
	
	
	
	
}