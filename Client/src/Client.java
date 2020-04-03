import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) {
		try {
			IGraph h = (IGraph) Naming.lookup("rmi://192.168.1.109/4116/hello");
			while (true) {
				Scanner input = new java.util.Scanner(System.in);
				String command = "A";
				System.out.println("Enter command");
				command = input.next();
				System.out.println("Please enter x and y");
				int x = input.nextInt();
				int y = input.nextInt();
				if (command.equals("A")) {
					System.out.println("Adding edge");
					h.addEdge(x, y);
				} else if (command.equals("D")) {
					System.out.println("Removing edge");
					h.removeEdge(x, y);
				} else {
					int f = h.getShortestPath(x, y);
					File file = new File("output.txt");
			        FileWriter fr = new FileWriter(file, false);
			        BufferedWriter br = new BufferedWriter(fr);
			        PrintWriter pr = new PrintWriter(br);
			        pr.println(f);
			        pr.close();
			        br.close();
			        fr.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
