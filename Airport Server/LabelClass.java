import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;


public class LabelClass extends JFrame implements ActionListener
{
	String name;
	TextArea ta;
	JButton ok;
	
	LabelClass(String name,String l)
	{
		super(name);
		this.setSize(500, 400);
		this.setLocation(300, 300);
		this.setVisible(true);
		this.name=name;
		
		this.setLayout(null);
		
		ta=new TextArea();
		ta.setBounds(20, 20, 480, 250);
		ta.setFont(new Font("Arial",Font.PLAIN,15));
		ta.setText(l);
		ok=new JButton("OK");
		ok.setBounds(165, 300, 70, 50);
		add(ta); add(ok);
		
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
