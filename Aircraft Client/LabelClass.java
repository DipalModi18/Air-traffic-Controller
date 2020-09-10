import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;


public class LabelClass extends JFrame implements ActionListener
{
	String name;
	Label l;
	JButton ok;
	
	LabelClass(String name,Label l)
	{
		super(name);
		this.setSize(300, 200);
		this.setLocation(300, 300);
		this.setVisible(true);
		this.name=name;
		this.l=l;
		this.setLayout(null);
		
		l.setBounds(20, 20, 270, 40);
		l.setFont(new Font("Arial",Font.PLAIN,20));
		ok=new JButton("OK");
		ok.setBounds(110, 85, 80, 40);
		add(l); add(ok);
		
		ok.addActionListener(this);
		
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		// TODO Auto-generated method stub
		String lab=arg0.getActionCommand();
			if(lab.equals("OK"))
			{
				this.setVisible(false);
			}
		
	}

}
