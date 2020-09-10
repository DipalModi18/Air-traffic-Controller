import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;



public class AirportServer extends JFrame
{
	//gui
public static ResultSet rs;
Connection con;
public static Statement st;
public static JTable jt;
public static JScrollPane jsp;
Font f;
int i,j;
JPanel title;
JLabel inmsg,outmsg,runmsg,sch;
public static TextArea inte,outte,runstatus;
public static String intemsg,outtemsg,runstatusmsg;
JButton setWeather;

//attribute
public static ServerSocket ser;
static Socket client[];
DataInputStream dis[];
static DataOutputStream dos;
PlaneThread pth[];
PlaneThread temp;
static int planeid[];
static int count;
int k,n;
String message[];
Thread t;
static Container c;



	AirportServer()
	{
		super("BARODA INTERNATIONAL AIRPORT");
		setLayout(null);
		//Color col=new Color(175,238,238);
		this.getContentPane().setBackground(Color.DARK_GRAY);
	
		f=new Font("Calibri",Font.BOLD,25);
		this.setFont(f);
		
		
		try
		{
		Class.forName("com.mysql.jdbc.Driver");
		con=DriverManager.getConnection("jdbc:mysql://localhost/airportdatabase","root","");
		st=con.createStatement();
		pth=new PlaneThread[20];
		PlaneThread.queue=new PlaneThread[20];
		intemsg="";
		outtemsg="";
		
		
		//initialization
		ser=new ServerSocket(123);
		
		 c=this;
		
		client=new Socket[20];
		dis=new DataInputStream[20];
		
		planeid=new int[20];
		count=0;
		c=this;
		
		addWindowListener(new WindowAdapter()
		
		{
			public void windowClosing(WindowEvent we)
			{
				try {
					rs.close();
					st.close();
					con.close();
					System.exit(0);
				} 
				catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		});	
			
		}
		catch(Exception e)
		{
			System.out.print("\n Database error :");
			e.printStackTrace();
		}
	}


	void createGui()
	{
		try
		{
		rs=st.executeQuery("select * from aircraftdata");
		//head=new;
		String head[]={"AircraftId","AircraftName","TakeoffTime","LandingTime","Delay","Departure","Arrival","Takeoff/Land"};
		String data[][]=new String[10][8];
		i=0;
		PlaneThread.rear=0;
		PlaneThread.front=0;
		TimerThread.r=new Runway();
		
		while(rs.next())
		{
			data[i][0]=""+rs.getInt("aircraftid");
			data[i][1]=""+rs.getString("aircraftname");
			data[i][2]=""+rs.getString("takeofftime");
			data[i][3]=""+rs.getString("landingtime");
			data[i][4]=""+rs.getInt("delay");
			data[i][5]=""+rs.getString("departure");
			data[i][6]=""+rs.getString("arrival");
			data[i][7]=""+rs.getString("takeoff_land");
			
			//initializing queue first time
			
			
			temp=new PlaneThread();
			temp.pid=rs.getInt("aircraftid");
			temp.aname=rs.getString(2);
			//System.out.println(temp.pid);
			if(rs.getString("takeoff_land").equals("T"))
			{
				String ss[]=rs.getString("takeofftime").split(":");
				temp.tt=new Time(Integer.parseInt(ss[0]),Integer.parseInt(ss[1]),Integer.parseInt(ss[2]));
				temp.land=rs.getString(4);
				temp.dept=rs.getString(7);
				//System.out.println(temp.tt.toString());
			}
			else if(rs.getString("takeoff_land").equals("L"))
			{
				String ss[]=rs.getString("landingtime").split(":");
				temp.tt=new Time(Integer.parseInt(ss[0]),Integer.parseInt(ss[1]),Integer.parseInt(ss[2]));
				temp.land=rs.getString(3);
				temp.dept=rs.getString(6);
				//System.out.println(temp.tt.toString());
			}
			Time t=new Time(temp.tt.hh,temp.tt.mm,temp.tt.ss);
			temp.end_time=Time.addMinutes(t,2);
			
			temp.op=rs.getString("takeoff_land");
			
			PlaneThread.queue[i]=temp;
			
			i++;
		}
		int k=i;
		PlaneThread.rear=i-1;
		i=PlaneThread.front;
		System.out.println("QUEUE:");
		while(i!=(PlaneThread.rear+1))
		{
			System.out.println(PlaneThread.queue[i].pid+" "+PlaneThread.queue[i].tt.toString()+" "+PlaneThread.queue[i].end_time.toString());
			i++;
		}
		
		
		
		jt=new JTable(data,head);
		jsp=new JScrollPane(jt);
		jsp.setBounds(30, 100, 1190, 300);
		jt.setRowHeight(30);
		jsp.setVisible(true);
		
		title=new JPanel();
		title.setBackground(Color.DARK_GRAY);
		
		sch=new JLabel("TODAY'S FLIGHT SCHEDULE",JLabel.CENTER);
		inmsg=new JLabel("INCOMING MESSAGES");
		outmsg=new 	JLabel("SENT MESSAGES");
		runmsg=new JLabel("RUNWAY STATUS");
		inte=new TextArea();
		outte=new TextArea();
		runstatus=new TextArea();
		setWeather=new JButton("Set Weather");
		title.setBounds(30,15,1190,60);
		sch.setBounds(245,10,600,40);
		sch.setFont(new Font("Castellar",Font.BOLD,40));
		sch.setForeground(Color.white);
		
		inmsg.setBounds(30,410,250,35);
		inmsg.setForeground(Color.white);
		inmsg.setFont(new Font("Arial",Font.PLAIN,18));
		inte.setBounds(30, 450, 500, 70);
		outmsg.setBounds(30, 530, 200, 35);
		outmsg.setForeground(Color.white);
		outmsg.setFont(new Font("Arial",Font.PLAIN,18));
		outte.setBounds(30, 570, 500, 70);
		runmsg.setBounds(600, 410, 500, 35);
		runmsg.setForeground(Color.white);
		runstatus.setBounds(600, 450, 500, 70);
		runmsg.setFont(new Font("Arial",Font.PLAIN,18));
		add(title);
		title.add(sch);
		  add(inmsg); add(outmsg);   add(inte); add(outte); 
		 add(runmsg); add(runstatus); add(jsp);   
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	static void print(String head[],String data[][])
	{
		jsp.setVisible(false);
        jt=new JTable(data,head);
		
		jsp=new JScrollPane(jt);
		jsp.setBounds(30, 100, 1190, 300);
		jt.setRowHeight(30);
		jsp.setVisible(true);
		c.add(jsp);
	}
	
	void running()
	{
		String ss = null; //temporary
		while(true)
		{
		try 
		{
			client[count]=ser.accept();	
			dis[count]=new DataInputStream(client[count].getInputStream());
			 String msg=dis[count].readUTF();
			
			 OutputStream os=client[count].getOutputStream();
				dos=new DataOutputStream(os);
				dos.writeUTF("ACKNOWLEDMENT: REQUEST ACCEPTED");
				dos.flush();
				
			String data[]=msg.split("-");
			planeid[count]=Integer.parseInt(data[0]);
			int l=data.length;
			String ts=null;
			String land=null;
			
			String dept="";
			int pid=0;
			if(l==1)
			{
				
				try
				{
				rs=st.executeQuery("select * from aircraftdata");
				while(rs.next())
				{
					pid=rs.getInt(1);
					
					if(pid==planeid[count])
					{
						if(rs.getString(8).equals("T"))
						{
							ts=rs.getString(3);	
						}
						else if(rs.getString(8).equals("L"))
						{
							ts=rs.getString(4);
						}
						break;
					}
					System.out.println("TS IN AS:"+ts);
				}
				ss="\n\n * NORMAL REQUEST ACCEPTED OF AIRCRAFTID:  "+pid;
				intemsg=ss+intemsg;
				pth[count]=new PlaneThread(planeid[count],"NORMAL",ts,null,null,null,null);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else if(l==3)
			{
				try
				{
				rs=st.executeQuery("select * from aircraftdata");
				
				while(rs.next())
				{
					 pid=rs.getInt(1);
					
					if(pid==planeid[count])
					{
						pth[count]=new PlaneThread(planeid[count],data[1],data[2],null,null,null,null);
						ss="\n\n * DELAY REQUEST ACCEPTED OF AIRCRAFTID:  "+pid+"\n DELAY TIME:  "+data[2];
						intemsg=ss+intemsg;
						break;
					}
				}
				
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
			}
			else
			{
			ss="\n\n * EMERGENCY REQUEST ACCEPTED OF AIRCRAFTID:  "+planeid[count]+"\n LANDING TIME:  "+data[2]+"\n AIRCRAFT NAME: "+data[3]+"\n DEPARRURE:  "+data[5]+"\n TAKEOFF TIME:  "+data[4];
			intemsg=ss+intemsg;
			pth[count]=new PlaneThread(planeid[count],data[1],data[2],data[3],data[4],data[5],null);
			}
			System.out.println("Request accepted "+planeid[count]);
			
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		LabelClass lc=new LabelClass("INCOMING MESSAGE",ss);
		inte.setText(intemsg);
		count++;
		}	
	}
	
	static void messageAll()
	{
		System.out.println("IN MESSAGE ALL");
		try 
		{
			int i=PlaneThread.front;
			int j=0;
			
			/*while(j<count)
			{
				System.out.println(planeid[j++]);
			}*/
				while(i!=PlaneThread.rear+1)
				{
					if(PlaneThread.queue[i].delay!=0)
					{
						j=0;
						
							while(PlaneThread.queue[i].pid!=planeid[j] && j<=count)
							{
								j++;
							}
							System.out.println("pid:"+PlaneThread.queue[i].pid+" J:"+j+" count:"+count);
							if(j<=count && PlaneThread.queue[i].flag==0)
							{
								
								PlaneThread.queue[i].flag=1;
								String msg="DELAY "+PlaneThread.queue[i].tt+" "+PlaneThread.queue[i].op; 
								System.out.println(msg);
								outtemsg="\n\n * SENT TO AIRCRAFTID: "+planeid[j]+"\n DELAY TIME: "+PlaneThread.queue[i].tt+outtemsg;
								outte.setText(outtemsg);
								System.out.println("J:"+j);
								OutputStream os=client[j].getOutputStream();
								dos=new DataOutputStream(os);
								dos.writeUTF(msg);
								dos.flush();
							}
					}
					i++;
				}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		} 

	
}
