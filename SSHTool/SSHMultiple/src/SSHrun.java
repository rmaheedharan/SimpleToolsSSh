import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;

public class SSHrun {
	static HashMap<String, String> configMap;
	
public static void main(String args[]) throws Exception
{
	if(args.length == 0)
	{
		System.out.println("Missing or invalid parameter\nUsage: SSHrun --configFile <FilePath>");
		System.exit(1);
	}
	
	if(!args[0].split("=")[0].equals("--configFile"))
	{
		System.out.println("Missing or invalid parameter\nUsage: SSHrun --configFile <FilePath>");
		System.exit(1);
	}
	
	readConfig(new File(args[0].split("=")[1]));
	
	String username=configMap.get("server_user");
	String password=configMap.get("server_password");
	int maxThreads=Integer.parseInt(configMap.get("maxthreads"));
	
	FileInputStream cmdfile = new FileInputStream(configMap.get("commandfile"));
	BufferedReader cmdbr = new BufferedReader(new InputStreamReader(cmdfile));
	String tmplin, cmdline = null;
	
	while ((tmplin = cmdbr.readLine()) != null) {
		cmdline=tmplin;
	}
 
	cmdbr.close();
	
	MultipleSSH.fw= new FileWriter(new File(configMap.get("outputfile")));
	
	
	LineNumberReader reader = new LineNumberReader(new FileReader(new File(configMap.get("serverlistfile"))));
    while ((reader.readLine()) != null);
    int serverCount = reader.getLineNumber();
	
	FileInputStream serverFile = new FileInputStream(new File(configMap.get("serverlistfile")));
	
	BufferedReader br = new BufferedReader(new InputStreamReader(serverFile));
 
	String serverline = null;
	Thread th[] = new Thread[serverCount];
	
	int iterCount=0;
	MultipleSSH.threadcount=1;
	
	System.out.println("DC Array length: "+serverCount);
	
	while ((serverline = br.readLine()) != null) {
	 String serv,cmd,user_n,pswd;
	 MultipleSSH.threadcount++;
	 String lineitem[]=serverline.split(",");
	 serv = lineitem[0];
	 if(lineitem.length>=2)
	 cmd = lineitem[1];
	 else
		 cmd=cmdline;
	 if(lineitem.length>=3){
		 if(lineitem[2]!="")
		 {
		 user_n=lineitem[2];
		 pswd=lineitem[3];
		 }
		 else
		 {
			 user_n=username;
			 pswd=password;
		 }
	 }
	 else {
		 user_n=username;
		 pswd=password;
	 }
	 th[iterCount] = new Thread(new MultipleSSH(serv,user_n,pswd,cmd),serv);
	 th[iterCount].start();
	 if(MultipleSSH.threadcount>maxThreads)
	 th[iterCount].join();
	 iterCount++;
	}
 
	br.close();
	 
	 for(int i=0; i<serverCount;i++)
	 {
		if(th[i].isAlive()){ th[i].join();}
	 }
	 
	 System.out.println("End of program");
     MultipleSSH.fw.close();
     
}

public static void readConfig(File configFile) throws Exception
{

	FileInputStream fis = new FileInputStream(configFile);
	 
	//Construct BufferedReader from InputStreamReader
	BufferedReader br = new BufferedReader(new InputStreamReader(fis));
 
	String line = null;
	
	configMap = new HashMap<String, String>();
	
	while ((line = br.readLine()) != null) {
		configMap.put(line.split("=")[0],line.split("=")[1]);
	}
 
	br.close();
}

}
