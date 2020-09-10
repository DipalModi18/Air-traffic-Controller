
public class AirportMain 
{

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		AirportServer as=new AirportServer();
		as.createGui();
		
		as.setLocation(50,30);
		as.setSize(1250, 700);
		as.setVisible(true);
		as.running();

	}

}
