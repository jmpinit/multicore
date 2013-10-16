import java.io.*;
import java.util.*;

public class DiskDrive
{
	public final static int ENABLE		= 0;
	public final static int OFFSET		= 1;
	public final static int DATA		= 3;
	public final static int FILENAME	= 4;
	
	int parameterLoc;
	boolean disabled = true;
	byte data[];
	Memory RAM;
	
	public DiskDrive(Memory mem, int addr)
	{
		RAM = mem;
		parameterLoc = addr;
		data = new byte[RAM.getLength()];
	}
	
	public void load(String filename)
	{
		try
		{
			FileInputStream file_input	= new FileInputStream(new File(filename));
			DataInputStream data_in		= new DataInputStream(file_input);
			
			data_in.readFully(data);
			
			data_in.close();
			file_input.close();
		} catch(IOException e)
		{
			e.toString();
		}
		
		int max = RAM.getLength();
		for(int i=0; i<data.length&&i<max; i++)
		{
			if(data[i]<0)
				RAM.write(i, 256-Math.abs(data[i]));
			else
				RAM.write(i, data[i]);
		}
	}
	
	public void update()
	{
		if(RAM.read(parameterLoc+ENABLE)!=0)
		{
			//update the filename if we need to
			if(disabled)
			{
				disabled = false;
				
				String filename = "";
				for(int i=0; i<12; i++)
					filename += (char)RAM.read(parameterLoc+FILENAME+i);
				filename = filename.trim();
				
				data = new byte[RAM.getLength()];
				try
				{
					FileInputStream file_input	= new FileInputStream(new File("C:\\Documents and Settings\\Owen Trueblood\\My Documents\\Processing\\Random\\Multicore\\programs\\"+filename));
					//FileInputStream file_input	= new FileInputStream(new File(System.getProperty("user.dir")+"\\programs\\"+filename));
					DataInputStream data_in		= new DataInputStream(file_input);
					
					data_in.readFully(data);
					
					data_in.close();
					file_input.close();
				} catch(IOException e)
				{
					e.toString();
				}
			}
			
			//update the data
			short offsetH = RAM.read(parameterLoc+OFFSET);
			short offsetL = RAM.read(parameterLoc+OFFSET+1);
			short offset = (short)((offsetH<<8)|offsetL);
			
			int value = 0;
			if(offset>=0&&offset<data.length)
				value = data[offset];
			
			if(data[offset]<0)
				RAM.write(parameterLoc+DATA, 256-Math.abs(data[offset]));
			else
				RAM.write(parameterLoc+DATA, value);
		} else {
			disabled = true;
		}
	}
}
