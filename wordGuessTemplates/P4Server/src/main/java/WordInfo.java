import java.io.Serializable;

public class WordInfo implements Serializable{
	
	private static final long serialVersionUID = 8073692471669315543L;

	//Update the client
	int wordLenght;
	Boolean isCorrect;
	int GuessLeft;
	
	//Update the server 
	char guess;
	String category;
	
	Boolean playAgain;
	Boolean quitAgain;
	
	
}
