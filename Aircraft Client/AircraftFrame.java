import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;


class AircraftFrame extends JFrame implements ActionListener,Runnable
{
	JTextArea taincoming,tasent;
	JTextField tfdelay,tfdept,tfarr;
	JLabel lincoming,ldelay,ldept,larr,lsent;
	JButton bsend,bemergency,bdelay;
	int planeid;
	Socket client;
	String msg,outmsg,inmsg; //msg=stores all incoming messages  outmsg=stores all out msg
	DataInputStream dis;
	DataOutputStream dos;
	static Statement st;
	static Connection con;
	static ResultSet rs;
	String ts; //delay time or emergency landing time;
	String aname;  //aircraft name;
	String takeoff;  //takeoff time
	Time toff,arrivaltime; //  takeoff time, and arrival time  equivalent to  takeoff,ts
	String dept;  //departure
	String arr; // arrival city
	JTextField tfeme;
	Thread th;
	int delay;  // delay in minutes
	Font font;
	
	
	AircraftFrame(int planeid)
	{
		
		super("AIRFRAFT ID:"+planeid);
		setLayout(null);
		//setContentPane(new JLabel(new ImageIcon("images//img4.jpg")));
		this.getContentPane().setBackground(Color.DARK_GRAY);
		
		th=new Thread(this);
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost:3306/aircraftdatabase","root","root");
			st=con.createStatement();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.planeid=planeid;
		
		taincoming=new JTextArea();
		tasent=new JTextArea();
		tfdelay=new JTextField();
		tfdept=new JTextField();
		tfarr=new JTextField();
		lincoming=new JLabel("INCOMING MSGS");
		lsent=new JLabel("OUTGOING MSGS");
		ldelay=new JLabel("DELAY TIME");
		ldept=new JLabel("DEPARTURE");
		larr=new JLabel("ARRIVAL");
		bsend=new JButton("SEND");
		bemergency=new JButton("EMERGENCY");
		bdelay=new JButton("OK");
		tfeme=new JTextField();
		
		lincoming.setBounds(25,25,150,50);
		lsent.setBounds(25, 150, 150, 50);
		taincoming.setBounds(200,25,500,100);
		tasent.setBounds(200, 150, 500, 100);
		ldept.setBounds(25,300,150,50);
		tfdept.setBounds(200,300,150,50);
		larr.setBounds(450,300,100,50);
		tfarr.setBounds(550,300,150,50);
		bsend.setBounds(210,400,125,50);
		bemergency.setBounds(400,400,125,50);
		tfeme.setBounds(550, 400, 150, 50);
		ldelay.setBounds(250,500,100,50);
		tfdelay.setBounds(375,500,150,50);
		bdelay.setBounds(550,500,100,50);
		
		font=new Font("Arial",Font.BOLD,15);
		lincoming.setFont(font);
		lsent.setFont(font);
		ldept.setFont(font);
		larr.setFont(font);
		ldelay.setFont(font);
		lincoming.setForeground(new Color(250,250,250));
		lsent.setForeground(new Color(250,250,250));
		ldept.setForeground(new Color(250,250,250));
		larr.setForeground(new Color(250,250,250));
		ldelay.setForeground(new Color(250,250,250));
		
		bsend.setForeground(Color.BLACK);
		  bsend.setBackground(Color.WHITE);
		  Border line = new LineBorder(Color.BLACK);
		  Border margin = new EmptyBorder(5, 15, 5, 15);
		  Border compound = new CompoundBorder(line, margin);
		  bsend.setBorder(compound);
		  bemergency.setForeground(Color.BLACK);
		  bemergency.setBackground(Color.WHITE);
		  bemergency.setBorder(compound);
		  bdelay.setForeground(Color.BLACK);
		  bdelay.setBackground(Color.WHITE);
		  bdelay.setBorder(compound);
		
		//msg=""+planeid;
		
		try 
		{
			client=new Socket("192.168.43.45",123);
			//dos.writeUTF(null);
			dis=new DataInputStream(client.getInputStream());
			dos=new DataOutputStream(client.getOutputStream());
			
			rs=st.executeQuery("select * from flight3");
			//System.out.print(rs);
			
			
			while(rs.next())
			{
				dept=rs.getString(3);
				tfdept.setText(dept+"-"+rs.getString(5));
				
				ts=rs.getString(6);
								
				arr=rs.getString(4);
				tfarr.setText(arr+"-"+rs.getString(6));
			
				tfdelay.setText(""+rs.getInt(7));
				
				takeoff=rs.getString(5);
				
			}
			String data[]=takeoff.split(":");
			toff=new Time(Integer.parseInt(data[0]),Integer.parseInt(data[1]),Integer.parseInt(data[2]));
			data=ts.split(":");
			arrivaltime=new Time(Integer.parseInt(data[0]),Integer.parseInt(data[1]),Integer.parseInt(data[2]));
			
			rs=st.executeQuery("select * from aircraft3");
			
			if(rs.next())
			{
				aname=rs.getString(2);
			}
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		add(taincoming);  add(lsent);  add(tfdelay); add(tfdept);  add(tfarr);  add(lincoming);  add(ldelay);  
		add(tasent); add(ldept);  add(larr);  add(bsend);  add(bemergency);  add(bdelay); add(tfeme);
		
		bsend.addActionListener(this);
		bemergency.addActionListener(this);
		bdelay.addActionListener(this);
		inmsg="";
		outmsg="";
		msg=""+planeid;
		th.start();
	}

	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
		/*try {
			//dos.writeUTF(null);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
		String lab=arg0.getActionCommand();
		String local;
		
		try
		{
		if(lab.equals("SEND"))
		{
			
			dos.writeUTF(msg);
			outmsg=outmsg+"\n NORMAL REQUEST :"+msg;
		}
		else if(lab.equals("EMERGENCY"))
		{
			rs=st.executeQuery("select * from flight3");
				if(rs.next())
				{
			
			String eme=tfeme.getText(); //send eme in time
			local=msg+"-EMERGENCY-"+eme+"-"+aname+"-"+takeoff+"-"+dept;
			outmsg=outmsg+"\n"+local;
			dos.writeUTF(local);
				}
		}
		else if(lab.equals("OK"))
		{
			String delay=tfdelay.getText();  //send delay in time
			local=msg+"-DELAY-"+delay;
			outmsg=outmsg+"\n"+local;
			dos.writeUTF(local);
		}
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
		tasent.setText(outmsg);
		
	}
	
	public void run()
	{
		while(true)
		{
			 try {
				String k=dis.readUTF();
				
				String data[]=k.split(" ");
				inmsg=inmsg+"\n "+k;
				if(data[0].equals("DELAY"))
				{
					String datats[]=data[1].split(":");
					Time delaytime=new Time(Integer.parseInt(datats[0]),Integer.parseInt(datats[1]),Integer.parseInt(datats[2]));
					
					if(data[2].equals("T"))
					{
						tfdept.setText(dept+"-"+data[1]);
						
						delay=toff.findDiff(delaytime);
						
					}
					else
					{
						tfarr.setText(arr+"-"+data[1]);
						delay=arrivaltime.findDiff(delaytime);
					}
					
					try 
					{
						st.executeUpdate("update flight3 set delay="+delay+" where flightno="+planeid);
					} 
					catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
				taincoming.setText(inmsg);
				System.out.println("\n MSG FROM SERVER: "+inmsg);  
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
		}
	}
	
}

