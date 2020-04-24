import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread{
	Socket socketClient;
	ObjectOutputStream out;
	ObjectInputStream in;
	String IPAddress;
	int portNumber;
	String clientNum;
	
	private Consumer<Serializable> callback;
	
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
