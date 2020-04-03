import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.Naming;
import java.util.Scanner;

public class Client {

	public static File writeStringToFile(String content) throws IOException {
		File file = new File("output.txt");
        FileWriter fr = new FileWriter(file, false);
        BufferedWriter br = new BufferedWriter(fr);
        PrintWriter pr = new PrintWriter(br);
        pr.println(content);
        pr.close();
        br.close();
        fr.close();
        return file;
	}
	
	public static String getCommand(File inputFile) throws FileNotFoundException {
		String s = "";
		Scanner sc = new Scanner(inputFile);
		if (sc.hasNext()) {
			s = sc.nextLine();
		}
		return s;
	}
	
	public static void main(String[] args) {
		try {
			ClientInterface h = (ClientInterface) Naming.lookup("rmi://192.168.43.102/15580/hello");
			while (true) {
				Scanner input = new java.util.Scanner(System.in);
				String command;
				System.out.println("Enter file name");
				command = input.nextLine();
				
				File inputFile = new File(command);
				File f = writeStringToFile(h.executeQuery(getCommand(inputFile)));
				Scanner sc = new Scanner(f);
				if (sc.hasNext()) {
					System.out.println(sc.nextLine());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
