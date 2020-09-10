import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TimerThread 
{
	int i=0;
	int j=0;
	PlaneThread pth;
	static Runway r;

	void timer(PlaneThread pth)
	{
		this.pth=pth;
		System.out.println("IN TIMER");
		DateFormat df=new SimpleDateFormat("HH:mm:ss");
		System.out.println("PID IN TIMER:"+pth.pid);
				
		System.out.println("TT:"+pth.tt.toString());
		
		while(this.i!=1)
		{
			
			Date d=new Date();
			String c[]=df.format(d).split(":");
			Time currtime=new Time(Integer.parseInt(c[0]),Integer.parseInt(c[1]),Integer.parseInt(c[2]));
			
			this.i=PlaneThread.queue[PlaneThread.front].tt.compare(currtime);
			
		}
		
		if(i==1)
		{
			System.out.println("I:"+i);
			r.useRunway(PlaneThread.queue[PlaneThread.front]);
			
		}
		else
			return;
}
}