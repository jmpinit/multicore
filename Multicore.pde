Computer comp;
CompControls controller;

int zoom = 4;

void setup()
{
	size(256, 256);
	noStroke();
	
	comp = new Computer();
	//controller = new CompControls(comp);
	
        comp.disk.load("C:\\Users\\owen\\Documents\\Processing\\Random\\Multicore\\programs");
	comp.disk.load("C:\\Documents and Settings\\Owen Trueblood\\My Documents\\Processing\\Random\\Multicore\\programs\\bootloader.bin");
	//comp.disk.load("C:\\Documents and Settings\\Owen Trueblood\\My Documents\\Processing\\Random\\Multicore\\programs\\eval.bin");
	//comp.disk.load(System.getProperty("user.dir")+"\\programs\\bootloader.bin");
	
	comp.CPU.startClock();
}

void draw()
{
	comp.run();
	drawMemFast();
}

void drawMemFast()
{
	background(0);
	
	int columns = width/16;
	int rows = height;
	
	int row = 0;
	int col = 0;
	for(int i=0; i<comp.RAM.getLength()&&row<rows&&col<columns; i++)
	{
		byte data = (byte)(comp.RAM.read(i)&0x00FF);
		
		if(i==comp.CPU.pc)
		{
			stroke(255, 0, 0);
			line(col*16-8, row, col*16+8, row);
		}/* else if(i==comp.core1.pc)
		{
			stroke(0, 255, 0);
			line(col*16-8, row, col*16+8, row);
		} else if(i==comp.core2.pc)
		{
			stroke(0, 0, 255);
			line(col*16-8, row, col*16+8, row);
		} else if(i==comp.core3.pc)
		{
			stroke(255, 128, 0);
			line(col*16-8, row, col*16+8, row);
		}*/
		
		for(int bit=0; bit<8; bit++)
		{
			if((data&(1<<bit))!=0)
				stroke(255, 255, 255);
			else
				stroke(0);

			point(col*16+8+(7-bit), row);
		}

		row++;
		if(row>=rows)
		{
			row = 0;
			col++;
		}
	}
}

void drawMem()
{
	background(0);
	
	int columns = (width/zoom)/16-1;
	int rows = height/zoom;

	int row = 0;
	int col = 0;
	for(int i=0; i<comp.RAM.getLength()&&row<rows&&col<columns; i++)
	{
		byte data = (byte)(comp.RAM.read(i)&0x00FF);
		
		if(comp.CPU.pc==i)
			stroke(255, 0, 0);
		else
			noStroke();
		
		for(int bit=0; bit<8; bit++)
		{
			if((data&(1<<bit))!=0)
				fill(255);
			else
				fill(0);

			rect((col*16+8+(7-bit))*zoom, row*zoom, zoom, zoom);
		}

		row++;
		if(row>=rows)
		{
			row = 0;
			col++;
		}
	}
}
