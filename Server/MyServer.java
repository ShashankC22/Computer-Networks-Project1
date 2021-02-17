import java.io.*;
import java.net.*;
import java.util.*;

public class MyServer implements Runnable {

	private Socket client;

	public MyServer(Socket client) {
		this.client = client;
	}

	
	public void run() {
		String incomingService;

		BufferedReader buffReader;
		try {
			buffReader = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
			incomingService = buffReader.readLine();

			System.out.println(incomingService);
			String service[] = incomingService.split(" ");
			String resourceRequested ;
			String dataFile = "";
			if (service[0].equalsIgnoreCase("get")) {
				resourceRequested= service[1].split("/")[1];
				System.out.println(resourceRequested);
				File f = new File(resourceRequested);
				if (f.exists() && !f.isDirectory()) {
					dataFile = new Scanner(new FileInputStream(resourceRequested)).useDelimiter("\\A").next();
					outgoingPacket(dataFile);
				} 
				else {
					sendError("File Not Found");
				}

			} else if (service[0].equalsIgnoreCase("put")) {

				incomingService = buffReader.readLine();
				incomingService = buffReader.readLine();
				incomingService = buffReader.readLine();
				while ((incomingService = buffReader.readLine()) != null) {
					if (incomingService.length()<=0)
						break;
					dataFile = dataFile+incomingService+"\n";
				}

				FileWriter fileWriter;
				fileWriter = new FileWriter(new File(service[1]));
				fileWriter.write(dataFile);
				outgoingPacket("File saved successfully to Server\n");
				fileWriter.close();

			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	void outgoingPacket(String response) throws IOException {
		PrintWriter output = new PrintWriter(client.getOutputStream());
		output.println("HTTP/1.1 200 OK");
		output.println("Host: localhost: " +client.getLocalPort());
		System.out.println(response);
		output.println(response);
		output.println("Client Connection Closing...");
		output.flush();
		output.close();
		this.client.close();

	}

	void sendError(String error) throws IOException {
		PrintWriter output = new PrintWriter(client.getOutputStream());
		output.println("HTTP/1.1 404 Not Found");
		output.println("Host: localhost: " + client.getLocalPort());
		System.out.println(client.getLocalPort()+"\n"+client.getLocalSocketAddress());
		System.out.println(error);
		output.println(error);
		output.println("Client Connection Closing...");
		output.flush();
		output.close();
		this.client.close();
	}

	public static void main(String[] args) throws IOException {
		if(args.length != 1){
			System.out.println("Invalid Input\n" + "Valid Input: Server <port>");
		} else if (args.length == 1) {
			ServerSocket hostMachine = new ServerSocket(Integer.parseInt(args[0]), 1, InetAddress.getByName("localhost"));
			System.out.println("Server waiting for connections on Socket : "+hostMachine.getLocalSocketAddress());
			hostMachine.getReuseAddress();
			while (true) {
				Socket clientSocket = hostMachine.accept();
				System.out.println("Connected to client "+clientSocket.toString()+"\n");
				new Thread(new MyServer(clientSocket)).start();;
			}

		} else {
			System.out.println("Invalid input : Server <port>");
		}
	}
}
