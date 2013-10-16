import java.util.*;

public class Memory
{
	short data[];
	
	public Memory(int size)
	{
		data = new short[size];
	}
	
	public void write(int address, int value)
	{
		short val = (short)value;
		//make sure we are only writing a byte
		if(Math.abs(val)>0xFF)
			val = (short)0xFF;
		
		if(address>=0&&address<data.length)
			data[address] = (short)Math.abs(val);
	}
	
	public short read(int address)
	{
		if(address>=0&&address<data.length)
			return data[address];
		else
			return 0x00;
	}
	
	public int getLength() { return data.length; }
}