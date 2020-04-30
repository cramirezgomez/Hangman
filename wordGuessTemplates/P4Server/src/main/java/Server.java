import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;



public class Server {
	
	    //Client count starts at 1
		int count = 1;
		
		//Hold the client objects in this list 
		ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
		TheServer server;
		
		int portNum;
		private Consumer<Serializable> callback;
		

		
//-------Default Constructors-------------------------------------------------------------------------------------------------------------	
		
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
		
//-------Server Thread--------------------------------------------------------------------------------------------------------------------
		
		//Creates a Server Thread 
		public class TheServer extends Thread
		{
			public void run()
			{
				//Listens for Connections (Looping)
				try(ServerSocket mysocket = new ServerSocket(portNum);)
				{
					System.out.println("Server is waiting for a client!");
					
					//When a client connects a new client thread is made for them 
					while(true)
					{
						ClientThread c = new ClientThread(mysocket.accept(), count);
						
						//callback.accept("client has connected to server: " + "client #" + count);
						
						WordInfo temp = new WordInfo();
						temp.serverMessage = "client has connected to server: " + "client #" + count;
						callback.accept(temp);
						
						//Add a new client to the client list
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
		
//--------------Client Thread-------------------------------------------------------------------------------------------------------------
		
		//Create a Client Thread
		public class ClientThread extends Thread
		{
			//Server connection 
			Socket connection;
			int count;
			
			//Streams
			ObjectInputStream in;
			ObjectOutputStream out;
			
			//Word Categories 
			ArrayList<String> wordBank1;
			ArrayList<String> wordBank2;
			ArrayList<String> wordBank3;
			
			String curWord; //Current Word being guessed 
			
			//Default constructor 
			ClientThread(Socket s, int count)
			{
				this.connection = s;
				this.count = count;
				
				wordBank1 = new ArrayList<String>();
				wordBank2 = new ArrayList<String>();
				wordBank3 = new ArrayList<String>();
				
				//Reset and Refill Word Categories 
				resetWordBanks();
			}
			
			//Testing 
			ClientThread()
			{
				wordBank1 = new ArrayList<String>();
				wordBank2 = new ArrayList<String>();
				wordBank3 = new ArrayList<String>();
				
				//Reset and Refill Word Categories 
				resetWordBanks();
			}
			
			//Reset Word Categories 
			void resetWordBanks()
			{
				wordBank1.clear();
				wordBank2.clear();
				wordBank3.clear();
				
				//Fruits
				wordBank1.add("apple");
				wordBank1.add("grapes");
				wordBank1.add("pineapple");
				
				//Colors 
				wordBank2.add("blue");
				wordBank2.add("orange");
				wordBank2.add("red");
				
				//Animals 
				wordBank3.add("tiger");
				wordBank3.add("zebra");
				wordBank3.add("bear");
				
				//Shuffle the words inside each category 
				Collections.shuffle(wordBank1); 
				Collections.shuffle(wordBank2); 
				Collections.shuffle(wordBank3); 
				
				curWord = " "; //Clear Word 
			}
			
			//update one client
			public void sendClientResponse(WordInfo message) 
			{
				try 
				{
					//Send server information to client 
					this.out.writeObject(message);
				}
				catch(Exception e) {}
			}
			
			
			//Called when start() is called 
			public void run()
			{
				try
				{
					//Create a connection between server and client 
					//Create streams between server and client 
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
					
					/* NOTE:
					 * There is 3 different objects of WordInfo created
					 *
					 * 	1. temp
					 *	2. curTurn
					 *  3. newClient
					 * 
					 * 	Observe if they cause problems
					 * 	
					 */
					
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
			WordInfo handleGuess(WordInfo input) 
			{
				WordInfo results = new WordInfo();
				//Find the letters in the word that the user guessed
				//Store the index positions of that guess in the array 
				for(int i = 0; i < curWord.length(); i++)
				{
					
					if(curWord.charAt(i) == input.guess)
					{
						results.positions.add(i);
						results.isCorrect = true;
					}
				}
				return results;
			}
			
			//pick a word from the category being sent, remove from bank
			void pickWordFromBank(int cat) 
			{
				if(cat == 1)
				{
					curWord = wordBank1.get(wordBank1.size() - 1);
					wordBank1.remove(wordBank1.size() - 1);
				}
				else if(cat == 2)
				{
					curWord = wordBank2.get(wordBank2.size() - 1);
					wordBank2.remove(wordBank2.size() - 1);
				}
				else if(cat == 3)
				{
					curWord = wordBank3.get(wordBank3.size() - 1);
					wordBank3.remove(wordBank3.size() - 1);
				}	
			}
			
			//sending word length
			WordInfo prepareLength() 
			{
				WordInfo len = new WordInfo();
				len.wordLength = curWord.length();
				return len; 
			}
			
			//outer game logic function
			int execLogic(WordInfo input) 
			{
				//client wants to play again
				if(input.playAgain) 
				{
					//If the player wants to play again reset their information
					//and refill the word banks
					resetWordBanks();
					return 1;
				}
				//client wants to quit
				if(input.quit) 
				{
					try 
					{
						out.close();
						in.close();
						connection.close();
						
						//Remove the client from the clients array 
						clients.remove(count - 1);
						return 2;
					}
					catch(Exception e){ return 2;}
					
				}
				//client guessed a char
				if(input.guess != ' ') 
				{
					sendClientResponse(handleGuess(input));
					return 3;
				}
				if(input.category != 0) 
				{
					pickWordFromBank(input.category); 
					WordInfo lengthInfo = prepareLength();
					sendClientResponse(lengthInfo);
					
					return 4;
				}
				 
				return 0;
			}
			
		}

}
