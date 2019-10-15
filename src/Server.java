import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TException;


public class Server{

	public static FileStoreHandler fileHandler;
	public static FileStore.Processor fileProcessor;
	public static int port;
	public static String ip;

	public static void main(String[] args) {

		try {
			port = Integer.parseInt(args[0]);
			processor = new FileStore.Processor<FileStore.Iface>(/*handler*/);
			Runnable simple = new Runnable() {
				public void run() {
					startServer(processor);
				}
			};
			new Thread(simple).start();
		} catch (Exception e) {
			System.err.println("Error: Incorrect Argument specifies. Usage: ./server.sh <port_no>" + e.getMessage());
			System.exit(0);
		}

	}
	public static void startServer(FileStore.Processor processor) {
		try {
			TServerTransport serverTransport = new TServerSocket(port);
			TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

			System.out.println("Starting the  server...");
			server.serve();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
