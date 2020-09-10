import java.sql.SQLException;

import javax.swing.table.DefaultTableModel;


public class PlaneThread implements Runnable
{
	Thread th;
	String op;  // type= takeoff or landing
	int pid;
	String de; //delay or emergency
	int delay; //delay in minutes
	String aname; //aircraftname
	Time takeoff; //takeofftime only in case of emergency
	String dept; //departure only in case of emergency
	String land; //in case of T in normal cases
	Time tt; //delay time or emergency landing time or arrival time or landing time in normal case
	Time end_time;
	int flag=0;
	TimerThread t;
	
	AirportServer as;
	static PlaneThread queue[];
	static int rear,front;
	static int dirtybit=0; //if it is 1 it means queue has been updated and hence database is to be updated and printed

	
		PlaneThread()
		{
			
		}
	
		PlaneThread(int pid,String de,String ts,String aname,String takeoff,String dept,String land)
		{
			this.op="";
			this.pid=0;
			this.de="";
			this.delay=0;
			
			this.takeoff=null;
			
			this.tt=null;
			this.end_time=null;
			
			//decoded msg
			int h;
			int m;
			int s;
			
			th=new Thread(this);
			this.pid=pid;
			this.de=de;
			
			
			if(takeoff!=null)
			{
				//System.out.println(takeoff);
			String data[]=takeoff.split(":");
			h=Integer.parseInt(data[0]);
			m=Integer.parseInt(data[1]);
			s=Integer.parseInt(data[2]);
			this.aname=aname;
			this.dept=dept;
			this.takeoff=new Time(h,m,s);
			}
			
			String data[]=ts.split(":");
			h=Integer.parseInt(data[0]);
			m=Integer.parseInt(data[1]);
			s=Integer.parseInt(data[2]);
			
			this.tt=new Time(h,m,s);
			System.out.println("TT IN PLANETHREAD:"+this.pid+" "+tt.toString());
			t=new TimerThread();
			
			th.start();
		}

		@Override
		public void run() 
		{
			if(de.equals("NORMAL"))
			{
				int i=front;
				while(i!=(rear+1))
				{
					if(queue[i].pid==this.pid)
					{
						System.out.println("IN NORMAL:"+queue[i].pid);
						check(i);
						break;
					}
					i++;
				}
			
			
			}
			else if(de.equals("EMERGENCY"))
			{
				this.shiftAll();
				
				
				queue[front]=this;
				Time t=new Time(queue[front].tt.hh,queue[front].tt.mm,queue[front].tt.ss);
				queue[front].end_time=Time.addMinutes(t,2);
				queue[front].op="E"; //E for emergency
				int i=front;
				while((i)!=(rear+1))
				{
					System.out.println(queue[i].pid+" "+queue[i].tt.toString()+" "+queue[i].de+" "+queue[i].op);
					i++;
				}
				
				
				this.changeTime();
				i=front;
				System.out.println("After changetime");
				while((i)!=(rear+1))
				{
					System.out.println(queue[i].pid+" "+queue[i].tt.toString()+" "+queue[i].de+" "+queue[i].op);
					i++;
				}
				
					updateDatabase();
					
				if(dirtybit==1)
				{	
					AirportServer.messageAll();
					
				}
				
					printQueue();
				
				
				
			}
			else if(de.equals("DELAY"))
			{
				int i=0;
				while((front+i)!=(rear+1))
				{
					if(this.pid==queue[i].pid)
					{
						this.delay=delay+queue[i].tt.findDiff(this.tt);
						Time t=new Time(this.tt.hh,this.tt.mm,this.tt.ss);
						queue[i].end_time=Time.addMinutes(t,2);
						queue[i].tt=this.tt;
						queue[i].delay=this.delay;
						this.checkDelay(i);
						break;
						
					}
					i++;
				}
				
				
				
					updateDatabase();
					if(dirtybit==1)
					{
						AirportServer.messageAll();
					}
					printQueue();
			}
				
			
			while(queue[front].pid!=this.pid)
				{
					System.out.print("");
				} 
				t.timer(this);
			
}
		void check(int i)
		{
			if(queue[i].tt!=this.tt)
			{
				AirportServer.messageAll();
			}
		}
		
		void shiftAll()
		{
			System.out.println("IN SHIFTALL");
			
			int temp=rear;
			queue[temp+1]=new PlaneThread();
			while(temp>=front)
			{
				//queue[temp+1]=queue[temp];
				queue[temp+1].op=queue[temp].op;
				queue[temp+1].pid=queue[temp].pid;
				queue[temp+1].de=queue[temp].de;
				queue[temp+1].delay=queue[temp].delay;
				queue[temp+1].aname=queue[temp].aname;
				queue[temp+1].takeoff=queue[temp].takeoff;
				queue[temp+1].dept=queue[temp].dept;
				queue[temp+1].land=queue[temp].land;
				queue[temp+1].tt=queue[temp].tt;
				queue[temp+1].end_time=queue[temp].end_time;
				temp--;
				
			}
			rear++;
			//int i=front;
			
			
		}
		void changeTime()
		{
			//called by emergency aircraft
			int i=front+1;
			while(queue[i].end_time.greaterThan(queue[i-1].tt) && queue[i-1].end_time.greaterThan(queue[i].tt))
			{
				dirtybit=1;
				queue[i].delay=queue[i].tt.findDiff(queue[i-1].end_time);
				queue[i].tt=queue[i-1].end_time;
				Time t=new Time(queue[i].tt.hh,queue[i].tt.mm,queue[i].tt.ss);
				queue[i].end_time=Time.addMinutes(t,2); //2 min for takeoff and landing
				i++;
			}
			//i=front;
			/*while((i)!=(rear+1))
			{
				//System.out.println(queue[i].pid+" "+queue[i].aname+" "+queue[i].takeoff.toString()+" "+queue[i].tt.toString()+" "+dept);
				i++;
			}*/
			
		}
		
		void checkDelay(int pos)
		{
			int i=pos;
			while(queue[i].end_time.greaterThan(queue[i+1].tt))
			{
				dirtybit=1;
				queue[i+1].delay=queue[i+1].tt.findDiff(queue[i].end_time);
				queue[i+1].tt=queue[i].end_time;
				Time t=new Time(queue[i+1].tt.hh,queue[i+1].tt.mm,queue[i+1].tt.ss);
				queue[i+1].end_time=Time.addMinutes(t,2);
				i++;
			}
		}
		
		void updateDatabase()
		{
			int i=front;
			dirtybit=0;
			while(i!=rear+1)
			{
				if(queue[i].op.equals("T"))
				{
					try 
					{
						AirportServer.st.executeUpdate("update aircraftdata set delay="+queue[i].delay+" where aircraftid="+queue[i].pid);
					} 
					catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(queue[i].op.equals("L"))
				{
					try 
					{
						
						AirportServer.st.executeUpdate("update aircraftdata set delay="+queue[i].delay+" where aircraftid="+queue[i].pid);
					} 
					catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(queue[i].op.equals("E"))
				{
					System.out.println(queue[i].pid+" "+queue[i].aname+" "+queue[i].takeoff+" "+queue[i].tt+" "+dept);
						try {
							AirportServer.st.executeUpdate("insert into emergency values("+queue[i].pid+",'"+queue[i].aname+"','"+queue[i].takeoff.toString()+"','"+queue[i].tt.toString()+"',0,'"+queue[i].dept+"','VADODARA')");
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
				i++;
			}
		}
		
		
		static void printQueue()
		{
			try 
			{
				
				String head[]={"AircraftId","AircraftName","TakeoffTime","LandingTime","Delay","Departure","Arrival","Takeoff/Land"};
				String data[][]=new String[10][8];
				int i=0;
				
				int j;
				for(i=front,j=0;i<=rear;i++,j++)
				{
					data[j][0]=""+queue[i].pid;
					data[j][1]=""+queue[i].aname;
					
						if(queue[i].op.equals("E"))
						{
							data[j][2]=""+queue[i].takeoff;
							data[j][3]=""+queue[i].tt;
							data[j][5]=""+queue[i].dept;
							data[j][6]="VADODARA";
						}
						else if(queue[i].op.equals("T"))
						{
							data[j][2]=""+queue[i].tt;
							data[j][3]=""+queue[i].land;
							data[j][5]="VADODARA";
							data[j][6]=queue[i].dept;
						}
						else if(queue[i].op.equals("L"))
						{
							data[j][2]=""+queue[i].land;
							data[j][3]=""+queue[i].tt;
							data[j][5]=""+queue[i].dept;
							data[j][6]="VADODARA";
						}
					data[j][4]=""+queue[i].delay;
					data[j][7]=queue[i].op;
					
						
					
				}
				/*int k=i;
				for(i=0;i<k;i++)
				{
					for(int j=0;j<8;j++)
					{
						AirportServer.jt.getModel().setValueAt(data[i][j], i, j);
					}
				}*/
			
				
				AirportServer.print(head,data);
				//table and scrollpane
				System.out.println("IN PRINTQ");
				
				i=front;
				while(i!=(rear+1))
				{

					System.out.println(queue[i].pid+" "+queue[i].tt.toString()+" "+queue[i].de+" "+queue[i].op+" "+queue[i].delay);
					i++;
				}
				
				
				
				//System.out.println("\n in emergency print");
				
				
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		static void pop()
		{
			
			front++;
			printQueue();
			
		}

}
