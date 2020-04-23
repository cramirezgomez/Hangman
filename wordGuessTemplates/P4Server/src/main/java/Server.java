import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;


public class Server {
	
	    //Creates a server 
		TheServer server;
		
		//We do not need to keep track of an individual player 
		MorraInfo infoP1;
		
		//Keeps track of the number of players 
		int numPlayers;
		
		int portNum;
		private Consumer<Serializable> callback;
		
		//Default Constructor with no custom port number 
		Server(Consumer<Serializable> call)
		{
			this.callback = call;
			portNum = 5555;
			this.server = new TheServer();
			this.server.start();
			
			infoP1 = null; //Why was this made null?
			
			numPlayers = 0;
		}
		
		//Default Constructor with custom port number 
		Server(Consumer<Serializable> call, int port)
		{
			this.callback = call;
			this.portNum = port;
			this.server = new TheServer();
			this.server.start();
			numPlayers = 0;
		}
		
		
		//Creates a Server Thread 
		public class TheServer extends Thread
		{
			public void run()
			{
				try(ServerSocket mysocket = new ServerSocket(portNum);)
				{
					//Looking for clients to connect 
					while(true)
					{
							int tempNum = 0;
							
							ClientThread c = new ClientThread(mysocket.accept(), tempNum);
						
							c.count = 1;
							System.out.println("open:p1");
							
							MorraInfo temp = new MorraInfo();
							
							temp.serMess = "new client " + c.count;
							
							numPlayers++;
							temp.numPlayers = numPlayers;
							
							callback.accept(temp);
							
							playerOne = c; //<---
							c.start();
							System.out.println("recieved p1");	
						
					}
				}
				catch(Exception e) 
				{
					MorraInfo temp = new MorraInfo();
					
					temp.serMess = "Server socket didn't laucnh ";
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
			
			ClientThread(Socket s, int count)
			{
				this.connection = s;
				this.count = count;
			}
			
			//Send Information back to the client 
			//Updates Clients with a MorraInfo object
			public void updateClients(MorraInfo message)
			{
					try
					{
						message.curPlayer = 1;
						playerOne.out.writeObject(message);
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
				
				
				MorraInfo newClient = new MorraInfo();
				newClient.numPlayers = numPlayers;
				newClient.serMess = "new client on server: client #"+count;
				updateClients(newClient);
				
				
				//while loop to read in from clients and update others
				while(true)
				{
					
				
					MorraInfo resultsInfo = new MorraInfo();
					
					//Game Logic will be implemented here 
					try 
					{
						if(count == 1)
						{
							infoP1 = (MorraInfo) in.readObject();
							
							System.out.println("playAGain: " + infoP1.playAgain);
							if(infoP1.playAgain == 1)
							{
								infoP1.p1Plays = -1;
								infoP1.playAgain = 0;
								infoP1.serMess = "Player 1 wants to play again";
								callback.accept(infoP1);
								updateClients(infoP1);
								infoP1 = null;
								System.out.println("play again p1");
							}
							else if(infoP1.playAgain == -1)
							{
								infoP1.p1Plays = -1;
								infoP1.playAgain = 0;
								infoP1.serMess = "Player 1 quit";
								callback.accept(infoP1);
								infoP1 = null;
								System.out.println("quit p1");
							}
							
						}
					
						if(infoP1 != null && infoP2 != null)
						{
							
							callback.accept(resultsInfo);
							
							updateClients(resultsInfo);
							
							infoP1 = null;
							infoP2 = null;
						}
					}
					//If someone Disconnects from the game
					//Should be updated for the new game
					catch(Exception e) 
					{
						
						MorraInfo connLoss = new MorraInfo();
						connLoss.serMess = "Client #"+count+" has left the server!";
						numPlayers--;
						connLoss.numPlayers = numPlayers;
						callback.accept(connLoss);
						updateClients(connLoss);
						if(count == 1) {
							playerOne = null;
							System.out.println("disconnect p1");
						}
			
						break;
					}
				}
				
			}
		}

}
