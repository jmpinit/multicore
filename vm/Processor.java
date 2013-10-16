import java.util.*;

public class Processor
{
	public int pc, sp;	//program counter and stack pointer
	boolean carry = false;
	private Memory RAM;
	
	long startTime = 0;
	long cycleCount = 0;

	public Command[] commands = {
		//indirect
		new NOP(),
		new INC(),
		new DEC(),
		new ADD(),
		new ADDI(),
		new SUB(),
		new SUBI(),
		new MULT(),
		new MULTI(),
		new DIV(),
		new DIVI(),
		new AND(),
		new ANDI(),
		new OR(),
		new ORI(),
		new NOR(),
		new NORI(),
		new NOT(),
		new LSL(),
		new LSR(),
		new LDI(),
		new MOV(),
		new STO(),
		new GET(),
		new PUSH(),
		new POP(),
		new GOTO(),
		new RCALL(),
		new RET(),
		new SGR(),
		new SGRI(),
		new SLS(),
		new SLSI(),
		new SEQ(),
		new SEQI(),
		new SNE(),
		new SNEI(),
		new SCS(),
		new SCC(),
		new BREAK()
	};
	
	public Processor(Memory mem)
	{
		RAM = mem;
		pc = 0;
		sp = RAM.getLength()-1;
	}
	
	public void startClock()
	{
		startTime = System.nanoTime();
	}
	
	public void executeNext()
	{
		int instruction = RAM.read(pc);
		
		//print debugging info
		/*System.out.println("====="+this+"=====");
		System.out.println("@"+pc+" "+commands[instruction].getClass().getCanonicalName());
		if(commands[instruction].size>0)
		{
			for(int i=0; i<commands[instruction].size; i++)
				System.out.print(RAM.read(pc+i+1)+",");
			System.out.println();
		}*/
		
		if(instruction<commands.length)
			commands[instruction].execute();
		else
			commands[0].execute();
		
		int max = RAM.getLength();
		if(pc>=max)
			pc %= max;
		
		if(pc<0)
		{
			if(Math.abs(pc)>=max)
				pc = max-pc%max;
			else
				pc = max+pc;
		}
		
		cycleCount++;
	}
	
	//gets the single short value at the 2 short address pointed to by address
	public short readIndirect(int address)
	{
		//resolve the address
		int indirectAddr = RAM.read(address);
		return RAM.read(indirectAddr);
	}
	
	public static Vector<Label> getLabels()
	{
		Vector<Label> labels = new Vector<Label>();
		labels.add(new Label("ADD", 0));
		labels.add(new Label("SUB", 1));
		labels.add(new Label("MULT", 2));
		labels.add(new Label("DIV", 3));
		labels.add(new Label("AND", 4));
		labels.add(new Label("OR", 5));
		labels.add(new Label("NOR", 6));
		labels.add(new Label("NOT", 7));
		labels.add(new Label("MOV", 8));
		labels.add(new Label("GOTO", 9));
		labels.add(new Label("RJMP", 10));
		labels.add(new Label("SKIP_GR", 11));
		labels.add(new Label("SKIP_LESS", 12));
		labels.add(new Label("SKIP_EQ", 13));
		labels.add(new Label("SKIP_NEQ", 14));
		labels.add(new Label("INPUT", 15));
		labels.add(new Label("OUTPUT", 16));
		labels.add(new Label("SKIP_FLAG", 17));
		labels.add(new Label("CLR_FLAG", 18));
		
		return labels;
	}
	
	//instructions
	private abstract class Command
	{
		public int size = 2;
		abstract void execute();
		void next()
		{
			pc += size+1;
		}
	}
	
	private void info(String msg)
	{
		System.out.println("Info: "+msg);
	}
	
	//----------INSTRUCTION CODE----------
	private class NOP extends Command
	{
		public NOP(){size=0;}
		
		public void execute() { next(); }
	}
	
	private class INC extends Command
	{
		public INC(){size=1;}
		
		public void execute()
		{
			int target = RAM.read(pc+1);
			int val = RAM.read(target);
			
			//calculate and save result
			int result = val+1;
			RAM.write(target, result);
			
			if(result>0xFF) { carry = true; } else { carry = false; }
			
			next();
		}
	}
	
	private class DEC extends Command
	{
		public DEC(){size=1;}
		
		public void execute()
		{
			int target = RAM.read(pc+1);
			int val = RAM.read(target);
			
			//calculate and save result
			int result = val-1;
			RAM.write(target, result);
			
			if(result<0) { carry = true; } else { carry = false; }
			
			next();
		}
	}
	
	private class ADD extends Command
	{
		public void execute()
		{
			int dest = RAM.read(pc+1);
			int val1 = readIndirect(pc+1);
			int val2 = readIndirect(pc+2);
			
			//calculate and save result
			int result = val1+val2;
			RAM.write(dest, result);
			
			if(result>0xFF) { carry = true; } else { carry = false; }
			
			next();
		}
	}
	
	private class ADDI extends Command
	{
		public void execute()
		{
			int dest = RAM.read(pc+1);
			int val1 = readIndirect(pc+1);
			int val2 = RAM.read(pc+2);
			
			//calculate and save result
			int result = val1+val2;
			RAM.write(dest, result);
			
			if(result>0xFF) { carry = true; } else { carry = false; }
			
			next();
		}
	}

	private class SUB extends Command
	{
		public void execute()
		{
			int dest = RAM.read(pc+1);
			int val1 = readIndirect(pc+1);
			int val2 = readIndirect(pc+2);
			
			//calculate and save result
			int result = val1-val2;
			RAM.write(dest, result);
			
			if(result<0) { carry = true; } else { carry = false; }
			
			next();
		}
	}
	
	private class SUBI extends Command
	{
		public void execute()
		{
			int dest = RAM.read(pc+1);
			int val1 = readIndirect(pc+1);
			int val2 = RAM.read(pc+2);
			
			//calculate and save result
			int result = val1-val2;
			RAM.write(dest, result);
			
			if(result<0) { carry = true; } else { carry = false; }
			
			next();
		}
	}

	private class MULT extends Command
	{
		public void execute()
		{
			int dest = RAM.read(pc+1);
			int val1 = readIndirect(pc+1);
			int val2 = readIndirect(pc+2);
			
			//calculate and save result
			int result = val1*val2;
			RAM.write(dest, result);
			
			if(result>0xFF) { carry = true; } else { carry = false; }
			
			next();
		}
	}
	
	private class MULTI extends Command
	{
		public void execute()
		{
			int dest = RAM.read(pc+1);
			int val1 = readIndirect(pc+1);
			int val2 = RAM.read(pc+2);
			
			//calculate and save result
			int result = val1*val2;
			RAM.write(dest, result);
			
			if(result>0xFF) { carry = true; } else { carry = false; }
			
			next();
		}
	}
	
	private class DIV extends Command
	{
		public void execute()
		{
			int dest = RAM.read(pc+1);
			int val1 = readIndirect(pc+1);
			int val2 = readIndirect(pc+2);
			
			//calculate and save result
			if(val2!=0)
			{
				int result = val1/val2;
				RAM.write(dest, result);
			} else {
				RAM.write(dest, 0);
			}
			
			next();
		}
	}
	
	private class DIVI extends Command
	{
		public void execute()
		{
			int dest = RAM.read(pc+1);
			int val1 = readIndirect(pc+1);
			int val2 = RAM.read(pc+2);
			
			//calculate and save result
			if(val2!=0)
			{
				int result = val1/val2;
				RAM.write(dest, result);
			} else {
				RAM.write(dest, 0);
			}
			
			next();
		}
	}
	
	private class AND extends Command
	{
		public void execute()
		{
			int dest = RAM.read(pc+1);
			int val1 = readIndirect(pc+1);
			int val2 = readIndirect(pc+2);
			
			//calculate and save result
			int result = val1&val2;
			RAM.write(dest, result);
			
			next();
		}
	}
	
	private class ANDI extends Command
	{
		public void execute()
		{
			int dest = RAM.read(pc+1);
			int val1 = readIndirect(pc+1);
			int val2 = RAM.read(pc+2);
			
			//calculate and save result
			int result = val1&val2;
			RAM.write(dest, result);
			
			next();
		}
	}
	
	private class OR extends Command
	{
		public void execute()
		{
			int dest = RAM.read(pc+1);
			int val1 = readIndirect(pc+1);
			int val2 = readIndirect(pc+2);
			
			//calculate and save result
			int result = val1|val2;
			RAM.write(dest, result);
			
			next();
		}
	}
	
	private class ORI extends Command
	{
		public void execute()
		{
			int dest = RAM.read(pc+1);
			int val1 = readIndirect(pc+1);
			int val2 = RAM.read(pc+2);
			
			//calculate and save result
			int result = val1|val2;
			RAM.write(dest, result);
			
			next();
		}
	}
	
	private class NOR extends Command
	{
		public void execute()
		{
			int dest = RAM.read(pc+1);
			int val1 = readIndirect(pc+1);
			int val2 = readIndirect(pc+2);
			
			//calculate and save result
			int result = val1^val2;
			RAM.write(dest, result);
			
			next();
		}
	}
	
	private class NORI extends Command
	{
		public void execute()
		{
			int dest = RAM.read(pc+1);
			int val1 = readIndirect(pc+1);
			int val2 = RAM.read(pc+2);
			
			//calculate and save result
			int result = val1^val2;
			RAM.write(dest, result);
			
			next();
		}
	}
	
	private class NOT extends Command
	{
		public NOT(){size=1;}
		
		public void execute()
		{
			int dest = RAM.read(pc+1);
			int val = RAM.read(dest);
			
			//calculate and save result
			int result = ~val;
			RAM.write(dest, result);
			
			next();
		}
	}
	
	private class LSL extends Command
	{
		public LSL(){size=1;}
		
		public void execute()
		{
			int dest = RAM.read(pc+1);
			int val = RAM.read(dest);
			
			//calculate and save result
			int result = val<<1;
			RAM.write(dest, result);
			
			if(result>0xFF) { carry = true; } else { carry = false; }
			
			next();
		}
	}
	
	private class LSR extends Command
	{
		public LSR(){size=1;}
		
		public void execute()
		{
			int dest = RAM.read(pc+1);
			int val = RAM.read(dest);
			
			//calculate and save result
			int result = val>>>1;
			RAM.write(dest, result);
			
			if(result==0) { carry = true; } else { carry = false; }
			
			next();
		}
	}
	
	private class LDI extends Command
	{
		public void execute()
		{
			int dest = RAM.read(pc+1);
			int result = RAM.read(pc+2);
			
			//calculate and save result
			RAM.write(dest, result);
			
			next();
		}
	}
	
	private class MOV extends Command
	{
		public void execute()
		{
			int dest = RAM.read(pc+1);
			int result = readIndirect(pc+2);
			
			//calculate and save result
			RAM.write(dest, result);
			
			next();
		}
	}
	
	private class STO extends Command
	{
		public STO(){size=3;}
		
		public void execute()
		{
			short destH = readIndirect(pc+1);
			short destL = readIndirect(pc+2);
			short result = readIndirect(pc+3);
			
			//calculate and save result
			int dest = (destH<<8)|destL;
			RAM.write(dest, result);
			
			next();
		}
	}
	
	private class GET extends Command
	{
		public GET(){size=3;}
		
		public void execute()
		{
			short dest = RAM.read(pc+1);
			short srcH = readIndirect(pc+2);
			short srcL = readIndirect(pc+3);
			
			//calculate and save result
			int src = (srcH<<8)|srcL;
			short result = RAM.read(src);
			RAM.write(dest, result);
			
			next();
		}
	}
	
	private class PUSH extends Command
	{
		public PUSH(){size=1;}
		
		public void execute()
		{
			short result = readIndirect(pc+1);
			
			//push value onto stack
			RAM.write(sp, result);
			sp--;
			
			next();
		}
	}
	
	private class POP extends Command
	{
		public POP(){size=1;}
		
		public void execute()
		{
			short dest = RAM.read(pc+1);
			
			//pop value off of stack
			sp++;
			short result = RAM.read(sp);
			
			RAM.write(dest, result);
			
			next();
		}
	}
	
	private class GOTO extends Command
	{
		public void execute()
		{
			short destH = RAM.read(pc+1);
			short destL = RAM.read(pc+2);
			pc = (destH<<8)|destL;
		}
	}
	
	private class RCALL extends Command
	{
		public void execute()
		{
			short destH = RAM.read(pc+1);
			short destL = RAM.read(pc+2);
			
			//push return address onto stack
			RAM.write(sp, pc>>8);	//high byte
			sp--;
			RAM.write(sp, pc&0xFF);	//low byte
			sp--;
			
			pc = (destH<<8)|destL;
		}
	}
	
	private class RET extends Command
	{
		public RET(){size=0;}
		
		public void execute()
		{
			//pop return address off of stack
			sp++;
			short destL = RAM.read(sp);		//low byte
			sp++;
			short destH = RAM.read(sp);		//high byte
			
			pc = (destH<<8)|destL;
		}
	}
	
	private class SGR extends Command
	{
		public void execute()
		{
			int val1 = readIndirect(pc+1);
			int val2 = readIndirect(pc+2);
			
			next();
			
			//goto command after next
			if(val2>val1)
				pc += commands[RAM.read(pc)].size+1;
		}
	}
	
	private class SGRI extends Command
	{
		public void execute()
		{
			int val1 = readIndirect(pc+1);
			int val2 = RAM.read(pc+2);
			
			next();
			
			//goto command after next
			if(val2>val1)
				pc += commands[RAM.read(pc)].size+1;
		}
	}
	
	private class SLS extends Command
	{
		public void execute()
		{
			int val1 = readIndirect(pc+1);
			int val2 = readIndirect(pc+2);
			
			next();
			
			//goto command after next
			if(val2<val1)
				pc += commands[RAM.read(pc)].size+1;
		}
	}
	
	private class SLSI extends Command
	{
		public void execute()
		{
			int val1 = readIndirect(pc+1);
			int val2 = RAM.read(pc+2);
			
			next();
			
			//goto command after next
			if(val2<val1)
				pc += commands[RAM.read(pc)].size+1;
		}
	}
	
	private class SEQ extends Command
	{
		public void execute()
		{
			int val1 = readIndirect(pc+1);
			int val2 = readIndirect(pc+2);
			
			next();
			
			//goto command after next
			if(val2==val1)
				pc += commands[RAM.read(pc)].size+1;
		}
	}
	
	private class SEQI extends Command
	{
		public void execute()
		{
			int val1 = readIndirect(pc+1);
			int val2 = RAM.read(pc+2);
			
			next();
			
			//goto command after next
			if(val2==val1)
				pc += commands[RAM.read(pc)].size+1;
		}
	}
	
	private class SNE extends Command
	{
		public void execute()
		{
			int val1 = readIndirect(pc+1);
			int val2 = readIndirect(pc+2);
			
			next();
			
			//goto command after next
			if(val2!=val1)
				pc += commands[RAM.read(pc)].size+1;
		}
	}
	
	private class SNEI extends Command
	{
		public void execute()
		{
			int val1 = readIndirect(pc+1);
			int val2 = RAM.read(pc+2);
			
			next();
			
			//goto command after next
			if(val2!=val1)
				pc += commands[RAM.read(pc)].size+1;
		}
	}
	
	private class SCS extends Command
	{
		public SCS(){size=0;}
		
		public void execute()
		{
			next();
			
			//goto command after next
			if(carry)
				pc += commands[RAM.read(pc)].size+1;
		}
	}
	
	private class SCC extends Command
	{
		public SCC(){size=0;}
		
		public void execute()
		{
			next();
			
			//goto command after next
			if(!carry)
				pc += commands[RAM.read(pc)].size+1;
		}
	}
	
	private class BREAK extends Command
	{
		public BREAK(){size=0;}
		
		public void execute()
		{
			System.out.println("Time: "+((System.nanoTime()-startTime)/1000000000000.0)+" seconds");
			System.out.println("Cycles: "+cycleCount);
			
			next();
		}
	}
}