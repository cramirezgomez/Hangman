import java.io.Serializable;
import java.util.ArrayList;

public class WordInfo implements Serializable{
	
	private static final long serialVersionUID = 8073692471669315543L;

	//Update the client
	int wordLength;
	Boolean isCorrect;
	String serverMessage;
	ArrayList<Integer> positions;
	
	
	//Update the server 
	char guess;
	int category;
	Boolean playAgain;
	Boolean quit;
	
	WordInfo(){
		wordLength = 0;
		isCorrect = false;
		serverMessage = " ";
		positions = new ArrayList<Integer>();
		guess = ' ';
		category = 0;
		playAgain = false;
		quit = false;
	}

	WordInfo(int wordLength, Boolean isCorrect, String serverMessage, ArrayList<Integer> positions,
			char guess, int category, Boolean playAgain, Boolean quit){
		this.wordLength = wordLength;
		this.isCorrect = isCorrect;
		this.serverMessage = serverMessage;
		this.positions = positions;

		this.guess = guess;
		this.category = category;
		this.playAgain = playAgain;
		this.quit = quit;

	}
	
	
	
	
	
}
