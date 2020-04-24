import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;



public class Server {
	
	    //Creates a server 
		int count = 1;	
		ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
		TheServer server;
		int portNum;
		private Consumer<Serializable> callback;
		
		//Default Constructor with no custom port number 
		Server(Consumer<Serializable> call)
		{
			this.callback = call;
			portNum = 5555;
			this.server = new TheServer();
			this.server.start();
		}
		
		//Default Constructor with custom port number 
		Server(Consumer<Serializable> call, int port)
		{
			this.callback = call;
			this.portNum = port;
			this.server = new TheServer();
			this.server.start();
		}
		
		
		//Creates a Server Thread 
		public class TheServer extends Thread
		{
			public void run()
			{
				try(ServerSocket mysocket = new ServerSocket(portNum);)
				{
					System.out.println("Server is waiting for a client!");
					//Looking for clients to connect 
					while(true)
					{
						ClientThread c = new ClientThread(mysocket.accept(), count);
						//callback.accept("client has connected to server: " + "client #" + count);
						WordInfo temp = new WordInfo();
						temp.serverMessage = "client has connected to server: " + "client #" + count;
						callback.accept(temp);
						clients.add(c);
						c.start();
						
						count++;
					}
				}
				catch(Exception e) 
				{
					WordInfo temp = new WordInfo();
					temp.serverMessage = "Server socket didn't launch";
					callback.accept(temp);
				}
			}
		}
		
		//Create a Client Thread
		public class ClientThread extends Thread
		{
			Socket connection;
			int count;
			
			ObjectInputStream in;
			ObjectOutputStream out;
			
			/*
				-> Here we would create the 3 Lists that would represent 
				each category
				
				-> Each Category would contain 3 Words 
				
				-> The reason it would be in here is because each client would get their own unique
				set of Lists. This will allow us to delete the words from the list once they are 
				used up and not mess around with the words of other clients. 
			
			*/
			ArrayList<String> wordBank1;
			ArrayList<String> wordBank2;
			ArrayList<String> wordBank3;
			String curWord;
			WordInfo curTurn;
			
			ClientThread(Socket s, int count)
			{
				this.connection = s;
				this.count = count;
				wordBank1 = new ArrayList<String>();
				wordBank2 = new ArrayList<String>();
				wordBank3 = new ArrayList<String>();
				resetWordBanks();
			}
			
			void resetWordBanks()
			{
				wordBank1.clear();
				wordBank2.clear();
				wordBank3.clear();
				
				wordBank1.add("apple");
				wordBank1.add("grapes");
				wordBank1.add("pineapple");
				
				wordBank2.add("blue");
				wordBank2.add("orange");
				wordBank2.add("red");
				
				wordBank3.add("tiger");
				wordBank3.add("zebra");
				wordBank3.add("bear");
				
				curWord = " ";
				curTurn = new WordInfo();
			}
			
			//update one client
			public void sendClientResponse(WordInfo message) 
			{
				try 
				{
					this.out.writeObject(message);
				}
				catch(Exception e) {}
			}
			
			
			//Called when start() is called 
			public void run()
			{
				try
				{
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);
				}
				catch(Exception e)
				{
					System.out.println("Streams not open");
				}
				
				
				WordInfo newClient = new WordInfo();
				newClient.serverMessage = "new client on server: client #"+count;
				sendClientResponse(newClient);
				
				
				//while loop to read in from clients and update others
				while(true)
				{
					
					//Game Logic will be implemented here 
					try 
					{
						WordInfo temp =(WordInfo) in.readObject();
						temp.serverMessage = "client: " + count + ": " + temp.serverMessage;
						callback.accept(temp);
						execLogic(temp);
				    	
					}
					//If someone Disconnects from the game
					catch(Exception e) 
					{
						WordInfo temp = new WordInfo();
						temp.serverMessage = "OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!";
						callback.accept(temp);
				    	clients.remove(this);
				    	break;
					}
				}
			}
			
			//Check if right or wrong and and update curTurn in server
			void handleGuess(WordInfo input) {
			}
			
			
			//pick a word from the category being sent, remove from bank
			void pickWordFromBank(int cat) 
			{
				
			}
			
			//sending word length
			WordInfo prepareLength() {
				WordInfo len = new WordInfo();
				len.wordLength = curWord.length();
				return len; 
			}
			
			
			
			
			//outer game logic function
			void execLogic(WordInfo input) {
				//client wants to play again
				if(input.playAgain) {
					
				}
				//client wants to quit
				if(input.quit) {
					
				}
				//client guessed a word
				if(input.guess != ' ') {
					handleGuess(input);
					sendClientResponse(curTurn);
				}
				if(input.category != 0) {
					pickWordFromBank(input.category); 
					WordInfo lengthInfo = prepareLength();
					sendClientResponse(lengthInfo);
				}
			}
			
		}

}
