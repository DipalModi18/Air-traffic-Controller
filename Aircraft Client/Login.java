import java.awt.Color;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;


public class Login extends JFrame implements ActionListener
{
	JTextField user;
	JPasswordField password;
	JButton login,cancel;
	JLabel us,pw;
	Font f;
	AircraftFrame af;
	
	//from dataabase
	int planeid;
	Connection con;
	Statement st;
	ResultSet rs;
	String u;
	String passwd;
			

	 
	
	Login(String s)
	{
		super(s);
		setLayout(null);
		setContentPane(new JLabel(new ImageIcon("images//img1.jpg")));
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost:3306/aircraftdatabase","root","root");
			st=con.createStatement();
			rs=st.executeQuery("select * from aircraft3");
			if(rs.next())
			{
			planeid=rs.getInt(1);
			u=rs.getString(3);
			passwd=rs.getString(4);
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("DATABASE ERROR:");
			e.printStackTrace();
			
		}
		
		
		user=new JTextField();
		password=new JPasswordField();
		login=new JButton("LOGIN");
		cancel=new JButton("CANCEL");
		us=new JLabel("UserID");
		pw=new JLabel("Password");
		
		f=new Font("Arial",Font.BOLD,30);
		setFont(f);
		us.setFont(f);
		pw.setFont(f);
		us.setForeground(new Color(240,240,240));
		pw.setForeground(new Color(240,240,240));
		
		login.setForeground(Color.BLACK);
		  login.setBackground(Color.WHITE);
		  Border line = new LineBorder(Color.BLACK);
		  Border margin = new EmptyBorder(5, 15, 5, 15);
		  Border compound = new CompoundBorder(line, margin);
		  login.setBorder(compound);
		  
		  cancel.setForeground(Color.BLACK);
		  cancel.setBackground(Color.WHITE);
		  cancel.setBorder(compound);
		
		us.setBounds(50,50,150,40);
		user.setBounds(250,50,150,40);
		pw.setBounds(50,150,150,40);
		password.setBounds(250,150,150,40);
		
		
		login.setBounds(70,250,130,40);
		cancel.setBounds(270,250,130,40);
		
		add(user);  add(password);  add(login);   add(cancel);  add(us);  add(pw);
		login.addActionListener(this);
		cancel.addActionListener(this);
		
		
	}
	int authentication()
	{
		String input_user=user.getText();
		String input_passwd=password.getText();
		LabelClass lc;
		
		if(input_user.equals(u))
		{
			if(input_passwd.equals(passwd))
			{
				return 1;
			}
			else
			{
				Label l=new Label("INCORRECT PASSWORD");
				lc=new LabelClass("ERROR",l);
				
				return 0;
			}
			
		}
		else
		{
			Label l=new Label("INVALID USERNAME");
			lc=new LabelClass("ERROR",l);
		
			return 0;
		}
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String s=arg0.getActionCommand();
		if(s.equals("LOGIN"))
		{
			int n=authentication();
			if(n==1)
			{
				af=new AircraftFrame((planeid));
				this.setVisible(false);
				af.setVisible(true);
				af.setSize(750,600);
				af.setLocation(200,20);
				//af.running();
			}
		}
		
	}

}

