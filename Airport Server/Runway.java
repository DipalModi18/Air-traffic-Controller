import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Runway
{
	static int flag=0;
	
	PlaneThread pth;
	String m;
	synchronized void useRunway(PlaneThread pth)
	{
		this.pth=pth;
		int i=0;
		DateFormat df=new SimpleDateFormat("HH:mm:ss");
		
		try {
			if(flag==1)
			{
				wait();
			}
			
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//PlaneThread pth=PlaneThread.pop();
		
		m="\n "+pth.pid+" IS USING RUNWAY..";
		System.out.println(m);
		AirportServer.runstatus.setText(m);
		
		while(i!=1)
		{
			
			Date d=new Date();
			String c[]=df.format(d).split(":");
			Time currtime=new Time(Integer.parseInt(c[0]),Integer.parseInt(c[1]),Integer.parseInt(c[2]));
			
			i=PlaneThread.queue[PlaneThread.front].end_time.compare(currtime);
			
		}
		if(i==1)
		{
			AirportServer.runstatus.setText("");
			notifyAll();
			pth.pop();
		}
		/*try {
			pth.pop();
			pth.th.sleep(2*60000);  //uses runway for 1 min
			
			flag=0;
			notifyAll();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} *///using runway
	}
	

}
