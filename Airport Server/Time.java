
public class Time 
{
	int hh;
	int mm;
	int ss;
	

	Time(int h,int m,int s)
	{
		hh=h;
		mm=m;
		ss=s;
	}
	
	boolean greaterThan(Time t)
	{
		if(this.hh>t.hh)
		{
			return true;
		}
		else if(this.hh==t.hh)
		{
			if(this.mm>t.mm)
			{
				return true;
			}
			else if(this.mm==t.mm)
			{
				if(this.ss>t.ss)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	public String toString()
	{
		String s=""+hh+":"+mm+":"+ss;
		return s;
	}
	
	static Time addMinutes(Time th,int min)
	{
		Time t=th;
		t.mm=th.mm+min;
		if(t.mm>=60)
		{
			t.hh=t.hh+1;
			t.mm=t.mm-60;
		}
		return t;
		
	}
	int findDiff(Time t)
	{
		int delay=0;
		if(t.hh>this.hh)
		{
			delay=(t.hh-this.hh)*60+(t.mm-this.mm);
		}
		else if(t.hh==this.hh)
		{
			delay=t.mm-this.mm;
		}
		return delay;
	}
	
	int compare(Time t)
	{
		if(this.hh==t.hh )
		{
			if(this.mm==t.mm)
				return 1;
			else
				return 0;
		}
		else
			return 0;
	}
}
