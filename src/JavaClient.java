import org.apache.thrift.TException;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Client{
	public static String port;
	public static String ip;
	public static void main(String[] args){
		if (args.length != 2) {
			System.out.println("Incorrect number of arguments! Client is terminating...");
			System.exit(0);
		}
		port=(args[1]);
		ip=args[0];
		String result=ip+":"+port;
		System.out.println("result: "+result);
		String finalAns="128.226.114.203:17000";
		String finalAnsHash=sha256(finalAns);
		String testSuccVar="128.226.114.203:18000";
		System.out.println("FinalAns: "+finalAnsHash);
		String testSuccVarHash=sha256(testSuccVar);
		//System.out.println("CheckingAns: "+testSuccVarHash);
		String tempKey="128.226.114.202:17500";
		String key=sha256(tempKey);
		System.out.println("key: "+key);
		testPred(key);
		//testSucc(key);
		System.out.println("Done");
	}

	public static void testPred(String key){
	//NodeID ans=null;
          try{
                    TTransport transport;
                     transport = new TSocket(ip,Integer.parseInt(port));
			System.out.println("Tsocket working");
                      transport.open();
			System.out.println("Transport working");
                       TProtocol protocol = new  TBinaryProtocol(transport);
			System.out.println("TBinary working");
                       FileStore.Client client = new FileStore.Client (protocol);
			 System.out.println("FileStore.Client working");
                       NodeID ans= client.findPred(key);
			System.out.println("Ans: "+ans.getId());
			 transport.close();
                    }catch(Exception e){
                        System.err.println("Exception occured:" + e.getMessage());
               }
	//System.out.println(ans);
         } 


	public static void testSucc(String key){
	try{
                    TTransport transport;
                     transport = new TSocket(ip,Integer.parseInt(port));
                        System.out.println("Tsocket working");
                      transport.open();
                        System.out.println("Transport working");
                       TProtocol protocol = new  TBinaryProtocol(transport);
                        System.out.println("TBinary working");
                       FileStore.Client client = new FileStore.Client (protocol);
                         System.out.print("FileStore.Client working");
                       NodeID ans= client.findSucc(key);
                        System.out.println("CheckingAns: "+ans.getId());
                         transport.close();
                    }catch(Exception e){
                        System.err.println("Exception occured:" + e.getMessage());
               }

	}






  public static String sha256(String base) {
    try{
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(base.getBytes("UTF-8"));
        StringBuffer hexString = new StringBuffer();

        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    } catch(Exception ex){
       throw new RuntimeException(ex);
    }
}


}
