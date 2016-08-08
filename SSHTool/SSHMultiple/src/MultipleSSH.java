import java.io.FileWriter;
import java.util.Date;
import net.neoremind.sshxcute.core.*;
import net.neoremind.sshxcute.exception.TaskExecFailException;
import net.neoremind.sshxcute.task.CustomTask;
import net.neoremind.sshxcute.task.impl.ExecCommand;

public class MultipleSSH implements Runnable{
	
	String server;
	String userid;
	String passwd;
	String command;
	static FileWriter fw;
	static int threadcount;
	public MultipleSSH(String server, String user, String password, String command) throws Exception
	{
		this.server = server;
		this.userid = user;
		this.passwd = password;
		this.command = command;
	}

	public void run() {
		// TODO Auto-generated method stub
		try {			    
	             ConnBean cb = new ConnBean(this.server, this.userid, this.passwd);     
	             SSHExec ssh  = new SSHExec(cb);          
	             System.out.println(new Date()+" - START - "+this.server+" - "+this.command+" - "+this.userid);
	             CustomTask ct1 = new ExecCommand(this.command);
	             ssh.connect();	         	             
	             Result res = ssh.exec(ct1);
	             if (res.isSuccess)
	             {	     
	            	 System.out.println(new Date()+" - END (SUCCESS) - "+this.server+" - "+this.command+" - "+this.userid);
	            	 new Thread();
					//System.out.println("OUTPUT: "+this.server+" - "+res.sysout);
	            	 Thread.sleep(10);                
	                     //System.out.println(server+"\t"+res.rc+"\t"+res.sysout);
	                     fw.write(server+"\t"+res.sysout);	                   
	                     //System.out.println("ThreadCount: "+threadcount);
	             }
	             else
	             {
	            	 System.out.println(new Date()+" - END (FAILURE) - "+this.server+" - "+this.command+" - "+this.userid+ " - "+res.error_msg);
	             }
	             ssh.disconnect();
	              
	     } catch (TaskExecFailException e) {
	    	 System.out.println(new Date()+" - END (FAILURE) - "+this.server+" - "+this.command+" - "+this.userid + " - " +e.getMessage());
	             e.printStackTrace();
	             threadcount--;
	     } catch (Exception e) {
	    	 System.out.println(new Date()+" - END (FAILURE) - "+this.server+" - "+this.command+" - "+this.userid + " - " +e.getMessage());
	             e.printStackTrace();
	             threadcount--;
	     } finally {
	    	 threadcount--;
	     }
	}
	
}

