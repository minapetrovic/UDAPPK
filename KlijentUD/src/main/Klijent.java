package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;

public class Klijent {

	public static Socket clientSocket;
	public static String serverHost;
	public static int serverPort;
	public static BufferedReader serverInput = null;
	public static PrintStream serverOutput = null;
	public static InputStream input = null;
	public static String username;

	public static BufferedReader konzola = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) {

		try {
			clientSocket = new Socket("localhost", 44115);
			serverInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			serverOutput = new PrintStream(clientSocket.getOutputStream());
			input = clientSocket.getInputStream();
			boolean value = true;
			String msg = serverInput.readLine();
			System.out.println(msg);

			System.out.println("Hello, welcome to UDAPP!");
			while (value) {
				System.out.println("Enter following numbers for different action:\n"
						+ "0 - register; 1 - login; 2 - logout; 3 - upload; 4 - download; 5 - list; 6 - EXIT.");
				value = choose();
			}
			clientSocket.close();
		} catch (UnknownHostException e) {
			System.out.println("UNKNOWN HOST");
			System.exit(0);
		} catch (IOException e) {
			System.out.println("SERVER IS DOWN");
			System.exit(0);
		}

	}

	private static boolean choose() {
		int num;

		try {
			num = Integer.parseInt(konzola.readLine().toString());
		} catch (NumberFormatException e) {
			System.out.println("Enter a number!");
			return true;
		} catch (IOException e) {
			System.out.println("Error while reading from console!");
			return true;
		}

		switch (num) {
		case 0:
			register();
			break;
		case 1:
			login();
			break;
		case 2:
			logout();
			break;
		case 3:
			upload();
			break;
		case 4:
			download();
			break;
		case 5:
			list();
			break;
		case 6:
			serverOutput.println(6);
			System.out.println("Goodbye!");
			return false;
		default:
			System.out.println("NOT DEFINED! Please, enter a valid number!");
			break;
		}
		return true;
	}

	private static void list() {
		if (username == null) {
			System.out.println("You cannot list files if you're not logged in!");
			return;
		}
		serverOutput.println(5);
		String msg = "";
		System.out.println("Requested file list:");
		try {
			do {
				msg = serverInput.readLine();
				System.out.println(msg);
			} while (msg.equals("-END-"));
		} catch (IOException e) {
			System.out.println("Error while reading list.");
		}
	}

	private static void download() {
		serverOutput.println(4);
		System.out.println("Please enter the private key to download file.");
		try {
			String key = konzola.readLine();
			serverOutput.println(key);
			if (serverInput.readLine().equals("Valid key.")) {
				System.out.println("Valid key.");
				RandomAccessFile randomAccessFile = new RandomAccessFile(key + ".txt", "rw");
				int n, p;
				byte[] buffer = new byte[1024];
				do {
					n = input.read(buffer);
					if (n == -1) {
						break;
					}
					randomAccessFile.write(buffer, 0, n);
				} while (input.available() != 0);
				randomAccessFile.close();
				System.out.println("File successfully saved!");
			}
		} catch (IOException e) {
			System.out.println("Error while downloading files.");
		}
	}

	private static void upload() {
		if (username == null) {
			System.out.println("You cannot upload files if you're not logged in!");
			return;
		}
		serverOutput.println(3);

		System.out.println(
				"You have selected to upload a file. Please enter your text. Length of your text should be maximum 500 characters. "
						+ "If you want to cancel update at any time, please enter the following set of characters: >>QUIT");
		try {
			String upload = konzola.readLine();

			if (upload.contains(">>QUIT")) {
				System.out.println("Client has forecefully canceled upload.");
				serverOutput.println(">>QUIT");
				return;
			}
			if (upload.length() > 500) {
				System.out.println(
						"You have entered more than 500 characters. Your text will include only first 500 characters.");
				upload = upload.substring(0, 500);
			}
			serverOutput.println(upload);
			System.out.println(
					"You have successfully uploaded your file.\nYour private key is " + serverInput.readLine());
		} catch (IOException e) {
			System.out.println("Error while uploading files.");
		}
	}

	private static void logout() {
		if (username == null) {
			System.out.println("You cannot log out if you're not logged in!");
			return;
		}
		try {
			serverOutput.println(2);
			System.out.println(serverInput.readLine());
			username = null;
		} catch (IOException e) {
			System.out.println("Error while logging out!");
		}
	}

	private static void login() {
		if (username != null) {
			System.out.println("User: " + username + " already logged in!");
			return;
		}
		serverOutput.println(1);
		System.out.println("Enter username and password in the following form (user,pass) to login:");
		try {
			String userpass = konzola.readLine();
			serverOutput.println(userpass);
			String in = serverInput.readLine();
			if (in.equals("Login successful!")) {
				System.out.println(in);
				username = (userpass.split(","))[0];
			} else {
				System.out.println(in);
			}

		} catch (IOException e) {
			System.out.println("Error while login!");
		}
	}

	private static void register() {
		serverOutput.println(0);

		System.out.println("Enter username and password in the following form (user,pass) to register:");
		try {
			String userpass = konzola.readLine();
			serverOutput.println(userpass);
			String in = serverInput.readLine();
			if (in.equals("New user created!")) {
				System.out.println(in);
				username = (userpass.split(","))[0];
			} else {
				System.out.println(in);
			}

		} catch (IOException e) {
			System.out.println("Error while registration!");
		}
	}
}
