import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ClientClass extends UnicastRemoteObject implements Serializable, ClientInterface {

	private static final long serialVersionUID = 1L;
	Graph g = new Graph();
    static Semaphore rm = new Semaphore(1);
    static Semaphore wm = new Semaphore(1);
    volatile static int rc = 0;
    boolean ready = false;
	protected ClientClass() throws RemoteException {
		try {
            List<String> allLines = Files.readAllLines(Paths.get("input.txt"));
            for (String line : allLines) {
                if(!line.equals("S")){
                    char[] parts = line.toCharArray();
                    g.addEdge(Integer.parseInt(Character.toString(parts[0])),
                            Integer.parseInt(Character.toString(parts[2])));
                }
            }
            ready = true;
            System.out.println("Ready");
        } catch (Exception e) {
            e.printStackTrace();
        }

	}

	@Override
	public String executeQuery(String query) {
		Thread q = new QueryExecutor(query);
		q.start();
		try {
			q.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return ((QueryExecutor)q).val;
	}

	class QueryExecutor extends Thread {
		public String val = "";
		
		String query;
		
		public QueryExecutor(String query) {
			this.query=query;
		}
		
		
		@Override 
		public void run() {
			long threadId = Thread.currentThread().getId();
			try {
                String command = query;
				String[] parts = command.split(" ");
				System.out.println(command);
                String operation = parts[0];
                int s = Integer.parseInt(parts[1]);
                int e = Integer.parseInt(parts[2]);
				switch (operation) {
                case "A":
                    wm.acquire();
                    g.addEdge(s, e);
                    wm.release();
                    break;
                case "Q":
                    rm.acquire();
                    rc++;
                    if(rc == 1) {
                        wm.acquire();
                    }
                    rm.release();
                    File file = new File("output" + String.valueOf(threadId)+".txt");
                    FileWriter fr = new FileWriter(file, false);
                    BufferedWriter br = new BufferedWriter(fr);
                    PrintWriter pr = new PrintWriter(br);
                    int value =g.getShortestPath(s, e); 
                    System.out.println(value);
                    val = value + "";
                    pr.println(val);
                    pr.close();
                    br.close();
                    fr.close();
                    rm.acquire();
                    rc--;
                    if(rc == 0)
                        wm.release();
                    rm.release();
                    break;
                case "D":
                    wm.acquire();
                    g.removeEdge(s, e);
                    wm.release();
                    break;
                default:
                    System.out.println("Error in batch file");

            }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean isReady() throws RemoteException {
		return ready;
	}
}