import javax.swing.*;
import java.io.*;

public class Computer implements Runnable
{
	public Memory RAM;
	public Processor CPU;//, core1, core2, core3;
	public DiskDrive disk;
	public Terminal term;
	public Peeker peek;
	
	public boolean pause = false;
	public int rate = 0;
	
	public Computer()
	{
		RAM = new Memory(8192);
		CPU = new Processor(RAM);
		/*core1 = new Processor(RAM);	core1.pc = 3;
		core2 = new Processor(RAM);	core2.pc = 6;
		core3 = new Processor(RAM);	core3.pc = 9;*/
		disk = new DiskDrive(RAM, 768);
		term = new Terminal(RAM, 256, 32, 16);
		//peek = new Peeker(RAM);
	}
	
	public void run()
	{
		if(!pause)
		{
			disk.update();
			
			CPU.executeNext();
			/*core1.executeNext();
			core2.executeNext();
			core3.executeNext();*/
			
			term.update();
		}
	}
}
