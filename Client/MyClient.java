
import java.net.*;
import java.util.*;
import java.io.*;
public class MyClient {

	public static Socket socClient;

	public static void dataReceive() throws IOException {
		BufferedReader buffReader;
		buffReader = new BufferedReader(new InputStreamReader(socClient.getInputStream()));
		String dataLine = "";
		while ((dataLine = buffReader.readLine()) != null) {
			System.out.println(dataLine);
		}
	}

	public static void main(String[] args) throws IOException {
		String host = "", action = "", file = "";
		int portUsed = 0;
		if (args.length != 4) {
			System.out.println("invalid args \nPlease enter arguments as <hostname> <port> <command> <file>");
		} else {
			host = args[0];
			portUsed = Integer.parseInt(args[1]);
			action = args[2];
			file = args[3];

			socClient = new Socket(host, portUsed);
			PrintWriter output = new PrintWriter(MyClient.socClient.getOutputStream());

			if (action.equalsIgnoreCase("get")) {
				String dataFile;
				File f = new File(file);			
				if (f.exists() && !f.isDirectory()) {
					dataFile = new Scanner(new FileInputStream(f)).useDelimiter("\\A").next();
				} 
				output.println("GET " +"/"+ file + " HTTP/1.1\n");
				output.println("Host: " + host + ":" + portUsed);
				output.println("User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64; rv:36.0)");
				output.write("Accept: text/plain, text/html, text/*\r\n\r\n");
				output.flush();
				dataReceive();
			} else if (action.equalsIgnoreCase("put")) {
				String dataFile;
				dataFile = new Scanner(new FileInputStream(file)).useDelimiter("\\A").next();
				output.println("PUT " + file + " HTTP/1.1\n");
				output.println("Host: localhost:" + socClient.getLocalPort()+"\n");
				output.println(dataFile);
				System.out.println(dataFile);
				output.flush();
				dataReceive();

			}
			else 
				System.out.println("Invalid action Entered. Enter Valid GET/PUT action.");
		}
	}
}
