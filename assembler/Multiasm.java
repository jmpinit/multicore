import java.io.*;
import java.util.*;

public class Multiasm
{
	public static void main(String args[])
	{
		Assembler asm = new Assembler();
		
		if(args.length==2)
			asm.assemble(args[0], args[1]);
		else
			System.out.println("Program usage: Multiasm <source filename> <destination filename>");
	}
}