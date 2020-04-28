import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Client extends Thread{
	
	Socket socketClient;
	ObjectOutputStream out;
	ObjectInputStream in;
	
	String IPAddress;
	int portNumber;
	
	String clientNum;
	int guesses;
	
	ArrayList<Boolean> catCleared;
	int lives;
	int serverResponses;
	
	private Consumer<Serializable> callback;
	
	//Added this constructor so that our methods that use the Client
	//aren't null before the player starts the game.
	Client(){
		this.guesses = 6;
		
		this.catCleared = new ArrayList<Boolean>();
		this.catCleared.add(false);
		this.catCleared.add(false);
		this.catCleared.add(false);
		
		serverResponses = 0;
		this.lives = 3;
		
	}
	
	//default contructor with values used in class
	Client(Consumer<Serializable> call)
	{
		callback = call;
		this.IPAddress = "127.0.0.1";
		this.portNumber = 5555;
	}
	
	//constructor with custom ip and port
	Client(Consumer<Serializable> call, String ip, int port)
	{
		callback = call;
		this.IPAddress = ip;
		this.portNumber = port;
		this.guesses = 6;
		
		this.catCleared = new ArrayList<Boolean>();
		this.catCleared.add(false);
		this.catCleared.add(false);
		this.catCleared.add(false);
		
		serverResponses = 0;
		this.lives = 3;
	}
	
	void resetVariables() 
	{
		this.catCleared.clear();
		this.catCleared.add(false);
		this.catCleared.add(false);
		this.catCleared.add(false);
		this.guesses = 6;
		this.lives = 3;
		
		//might need this or not
		//serverResponses = 0;
	}
	
	void resetGuesses() 
	{
		this.guesses = 6;
	}
			
	
	public void run()
	{
		try
		{
			//create a new client object with in/output streams
			socketClient = new Socket(this.IPAddress, this.portNumber);
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());
			socketClient.setTcpNoDelay(true);
			
			WordInfo message = (WordInfo) in.readObject();
			callback.accept(message);
		}
		catch(Exception e) {}
		
		//infinite loop to receive WordInfo
		while(true)
		{
			try 
			{
				WordInfo message = (WordInfo) in.readObject();
				callback.accept(message);
			}
			catch(Exception e) {
			}
		}
	}
	
	//send WordInfo to server
	public void send(WordInfo data)
	{
		try 
		{
			out.writeObject(data);
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	//close connection and stream
	public void disconnect()
	{
		try 
		{
			out.close();
			in.close();
			socketClient.close();
		}
		catch(Exception e)
		{
			
		}
	}

}
