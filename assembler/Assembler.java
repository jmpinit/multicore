import java.util.*;
import java.io.*;
import java.lang.reflect.*;

public class Assembler
{
	int address = 0;
	int offset = 0;
	Vector<Symbol> symbols = new Vector<Symbol>();
	Vector<Command> program = new Vector<Command>();
	Vector<Code> data = new Vector<Code>();
	short[] bin;
	
	Vector<Statement> statements = new Vector<Statement>();
	
	public Assembler()
	{
		//directives
		addDiffStatement("@", "Org");
		addDiffStatement("#", "Offset");
		addDiffStatement(".def", "Def");
		addDiffStatement(">", "Label");
		addDiffStatement(".db", "Databyte");
		addDiffStatement("//", "Comment");
		
		//instructions
		addStatement("NOP");
		addStatement("INC");
		addStatement("DEC");
		addStatement("ADD");
		addStatement("ADDI");
		addStatement("SUB");
		addStatement("SUBI");
		addStatement("MULT");
		addStatement("MULTI");
		addStatement("DIV");
		addStatement("DIVI");
		addStatement("AND");
		addStatement("ANDI");
		addStatement("OR");
		addStatement("ORI");
		addStatement("NOR");
		addStatement("NORI");
		addStatement("NOT");
		addStatement("LSL");
		addStatement("LSR");
		addStatement("LDI");
		addStatement("MOV");
		addStatement("STO");
		addStatement("GET");
		addStatement("PUSH");
		addStatement("POP");
		addStatement("GOTO");
		addStatement("RCALL");
		addStatement("RET");
		addStatement("SGR");
		addStatement("SGRI");
		addStatement("SLS");
		addStatement("SLSI");
		addStatement("SEQ");
		addStatement("SEQI");
		addStatement("SNE");
		addStatement("SNEI");
		addStatement("SCS");
		addStatement("SCC");
		addStatement("BREAK");
	}
	
	public void addStatement(String text)
	{
		try
		{
			statements.add(new Statement(text, Class.forName("Assembler$"+text, true, ClassLoader.getSystemClassLoader())));
		} catch(Exception e)
		{
			System.out.println(e.toString());
		}
	}
	
	public void addDiffStatement(String text, String type)
	{
		try
		{
			statements.add(new Statement(text, Class.forName("Assembler$"+type, true, ClassLoader.getSystemClassLoader())));
		} catch(Exception e)
		{
			System.out.println(e.toString());
		}
	}
	
	public void assemble(String source, String destination)
	{
		Vector<String> lines = suckText(source);
		
		lines = clean(lines);
		
		//assemble
		pass1(lines);
		pass2(lines);
		dryRun();
		build();
		System.out.println();
		
		System.out.println("Info: Symbols-");
		for(Symbol s: symbols)
			System.out.println(s.text+"="+s.value);
		System.out.println();
		
		//display
		System.out.println("Info: Program data-");
		for(int i=0; i<bin.length; i++)
			System.out.print(bin[i]+",");
		System.out.println();
		System.out.println();
		
		//save to file
		try
		{
			DataOutputStream os = new DataOutputStream(new FileOutputStream(destination));
			
			for(int i=0; i<bin.length; i++)
				os.write((int)bin[i]);
			
			os.close();
		} catch(IOException e)
		{
			error(e.toString());
		}
	}
	
	private Vector<String> clean(Vector<String> lines)
	{
		Vector<String> clean = new Vector<String>();
		
		//remove comments
		for(String text: lines)
		{
			int pos = text.indexOf(';');
			
			if(pos>=0)
				clean.add(text.substring(0, pos));
			else
				clean.add(text);
		}
		
		//remove empty lines
		Iterator itr = clean.iterator(); 
		while(itr.hasNext())
		{
			String text = (String)itr.next();
			if(text.trim().equals(""))
			{
				itr.remove();
			}
		}
		
		return clean;
	}
	
	private void pass1(Vector<String> lines)
	{
		//first pass - collect information
		address = 0;
		for(int i=0; i<lines.size(); i++)
		{
			Line line = breakLine(lines.get(i));
			
			if(line.statement==null)
			{
				error("Error on line "+(i+1)+": invalid command");
			}
			
			//add to program
			Command info = null;
			try
			{
				Constructor[] cons = line.statement.type.getDeclaredConstructors();
				info = (Command)(cons[0].newInstance(new Object[]{this}));
					
				if(info.numParams!=line.parameters.size())
				{
					System.out.println(lines.get(i));
					System.out.println(info.numParams+" != "+line.parameters.size());
					error("Error on line "+(i+1)+": incorrect # of parameters");
				}
				
				info.parameters = line.parameters;
				
				if(!info.global)
					info = null;
			} catch(Exception e)
			{
				error(e.toString());
			}
			
			if(info!=null)
				info.resolve();
		}
		
		System.out.println("Status: First pass successful");
	}
	
	private void pass2(Vector<String> lines)
	{
		//second pass - build program
		address = 0;
		for(int i=0; i<lines.size(); i++)
		{
			Line line = breakLine(lines.get(i));
			
			if(line.statement==null)
			{
				System.out.println(lines.get(i));
				error("Error on line "+(i+1)+": invalid command");
			}
			
			//add to program
			try
			{
				Constructor[] cons = line.statement.type.getDeclaredConstructors();
				Command n = (Command)(cons[0].newInstance(new Object[]{this}));
				
				if(n.numParams!=line.parameters.size())
				{
					System.out.println(lines.get(i));
					error("Error on line "+(i+1)+": incorrect # of parameters");
				}
				
				n.parameters = line.parameters;
				
				program.add(n);
			} catch(Exception e)
			{
				error(e.toString());
			}
		}
		
		System.out.println("Status: Second pass successful");
	}
	
	private Line breakLine(String text)
	{
		//break apart line
		String[] chunks = text.split("[\\s,]");
		Vector<String> picks = new Vector<String>(Arrays.asList(chunks));
		
		//remove empty chunks
		Iterator itr = picks.iterator(); 
		while(itr.hasNext())
		{
			String pick = (String)itr.next();
			if(pick.trim().equals(""))
			{
				itr.remove();
			}
		}
		
		String command = picks.get(0).trim();
		
		//separate parameters
		Vector<String> parameters = new Vector<String>();
		if(picks.size()>=2)
		{
			for(int pi=1; pi<picks.size(); pi++)
			{
				String p = picks.get(pi).trim();
				
				if(!p.equals(""))
					parameters.add(p);
			}
		}
		
		//decode into statement
		Statement target = null;
		for(Statement s: statements)
		{
			if(s.name.equals(command))
				target = s;
		}
		
		return new Line(target, command, parameters);
	}
	
	private void dryRun()
	{
		address = 0;
		for(Command c: program)
			c.move();
			
		System.out.println("Status: Dry run successful");
	}
	
	private void build()
	{
		//build data
		for(Command c: program)
			c.resolve();
		
		int max = 0;
		for(Code c: data)
		{
			if(c.address>max)
				max = c.address;
		}
		
		bin = new short[max+1];
		for(Code c: data)
		{
			if(c.value<0)
				error("Error: something was not decoded properly");
		
			if(c.value<=0xFF)
			{
				bin[c.address] = c.value;
			} else {
				error("Error: a value exceeds 0xFF");
			}
		}
		
		System.out.println("Status: Build successful");
	}
	
	public void error(String text)
	{
		System.out.println(text);
		System.exit(0);
	}
	
	public Integer getValue(String text)
	{
		Integer value = null;
		
		for(Symbol s: symbols)
		{
			if(s.text.equals(text))
				value = s.value;
		}
		
		if(value==null)
		{
			try
			{
				value = Integer.parseInt(text);
			} catch(Exception e) { }
		}
		
		return value;
	}
	
	public Integer getByte(String text)
	{
		Integer value = null;
		
		boolean hByte = false;
		if(text.startsWith("^"))
		{
			hByte = true;
			text = text.substring(1, text.length());
		}
		
		for(Symbol s: symbols)
		{
			if(s.text.equals(text))
				value = s.value;
		}
		
		if(value==null)
		{
			try
			{
				value = Integer.parseInt(text);
			} catch(Exception e) { }
		}
		
		if(value!=null)
		{
			if(hByte)
				value = value>>8;
			value &= 0xFF;
		}
		
		return value;
	}
	
	public Vector<String> suckText(String filename)
	{
		Vector<String> lines = new Vector<String>();
		
		//grep the lines of program
		try
		{
			FileInputStream fstream = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String strLine;
			while((strLine = br.readLine()) != null)
			{
				lines.add(strLine);
			}
			
			in.close();
		} catch(IOException e)
		{
			error(e.toString());
		}
		
		return lines;
	}
	
	private class Line
	{
		public Statement statement;
		public Vector<String> parameters;
		public String command;
		
		public Line(Statement s, String c, Vector<String> ps)
		{
			statement = s;
			command = c;
			parameters = ps;
		}
	}
	
	private class Symbol
	{
		public String text;
		public Integer value = -1;
		
		public Symbol(String t, int v)
		{
			text = t;
			value = v;
		}
	}
	
	private class Code
	{
		public int address;
		public short value;
		
		public Code(int a, short v)
		{
			address = a;
			value = v;
		}
	}
	
	private class Statement
	{
		public String name;
		public Class type;
		
		public Statement(String n, Class t)
		{
			name = n;
			type = t;
		}
	}
	
	private abstract class Command
	{
		protected int numParams;
		public Vector<String> parameters;
		protected boolean global = false;
		
		public abstract void resolve();
		public abstract void move();
	}
	
	//----------DIRECTIVES----------
	private class Org extends Command
	{
		public Org()
		{
			numParams = 1;
		}
		
		public void resolve()
		{
			move();
		}
		
		public void move()
		{
			address = Integer.parseInt(parameters.get(0));
		}
	}
	
	private class Offset extends Command
	{
		public Offset()
		{
			numParams = 1;
		}
		
		public void resolve()
		{
			move();
		}
		
		public void move()
		{
			offset = Integer.parseInt(parameters.get(0));
		}
	}
	
	private class Def extends Command
	{
		public Def()
		{
			numParams = 2;
			global = true;
		}
		
		public void resolve()
		{
			String text = parameters.get(0);
			int value = getValue(parameters.get(1));
			symbols.add(new Symbol(text, value));
		}
		
		public void move() {}
	}
	
	private class Label extends Command
	{
		public Label()
		{
			numParams = 1;
		}
		
		public void resolve() {}
		
		public void move()
		{
			String text = parameters.get(0);
			symbols.add(new Symbol(text, address+offset));
		}
	}
	
	private class Databyte extends Command
	{
		public Databyte()
		{
			numParams = 1;
		}
		
		public void resolve()
		{
			Integer value = getByte(parameters.get(0));
				
			if(value!=null)
				data.add(new Code(address, value.shortValue()));
				
			move();
		}
		
		public void move()
		{
			address++;
		}
	}
	
	private class Comment extends Command
	{
		public Comment()
		{
			numParams = 1;
		}
		
		public void resolve() {}
		public void move() {}
	}
	
	//----------INSTRUCTIONS----------
	
	private abstract class Instruction extends Command
	{
		public Instruction()
		{
			numParams = 2;
		}
	
		protected void add(int code)
		{
			data.add(new Code(address, (short)code));
			address++;
			
			for(String p: parameters)
			{
				Integer value = getByte(p);
				
				if(value!=null)
					data.add(new Code(address, value.shortValue()));
				address++; 
			}
		}
		
		public void move()
		{
			address += numParams+1;
		}
	}
	
	private class NOP extends Instruction {
		public NOP() { numParams = 0; }
		public void resolve() { add(0); }
	}
	
	private class INC extends Instruction {
		public INC() { numParams = 1; }
		public void resolve() { add(1); }
	}
	
	private class DEC extends Instruction {
		public DEC() { numParams = 1; }
		public void resolve() { add(2); }
	}
	
	private class ADD	extends Instruction { public ADD()	{ super(); } public void resolve() { add(3); } }
	private class ADDI	extends Instruction { public ADDI()	{ super(); } public void resolve() { add(4); } }
	private class SUB	extends Instruction { public SUB()	{ super(); } public void resolve() { add(5); } }
	private class SUBI	extends Instruction { public SUBI()	{ super(); } public void resolve() { add(6); } }
	private class MULT	extends Instruction { public MULT()	{ super(); } public void resolve() { add(7); } }
	private class MULTI extends Instruction { public MULTI(){ super(); } public void resolve() { add(8); } }
	private class DIV	extends Instruction { public DIV()	{ super(); } public void resolve() { add(9); } }
	private class DIVI 	extends Instruction { public DIVI()	{ super(); } public void resolve() { add(10); } }
	private class AND	extends Instruction { public AND()	{ super(); } public void resolve() { add(11); } }
	private class ANDI	extends Instruction { public ANDI()	{ super(); } public void resolve() { add(12); } }
	private class OR	extends Instruction { public OR()	{ super(); } public void resolve() { add(13); } }
	private class ORI	extends Instruction { public ORI()	{ super(); } public void resolve() { add(14); } }
	private class NOR 	extends Instruction { public NOR()	{ super(); } public void resolve() { add(15); } }
	private class NORI	extends Instruction { public NORI() { super(); } public void resolve() { add(16); } }
	
	private class NOT extends Instruction {
		public NOT() { numParams = 1; }
		public void resolve() { add(17); }
	}
	
	private class LSL extends Instruction {
		public LSL() { numParams = 1; }
		public void resolve() { add(18); }
	}
	
	private class LSR extends Instruction {
		public LSR() { numParams = 1; }
		public void resolve() { add(19); }
	}
	
	private class LDI extends Instruction {
		public LDI() { numParams = 2; }
		public void resolve() { add(20); }
	}
	
	private class MOV extends Instruction {
		public MOV() { numParams = 2; }
		public void resolve() { add(21); }
	}
	
	private class STO extends Instruction {
		public STO() { numParams = 3; }
		public void resolve() { add(22); }
	}
	
	private class GET extends Instruction {
		public GET() { numParams = 3; }
		public void resolve() { add(23); }
	}
	
	private class PUSH extends Instruction {
		public PUSH() { numParams = 1; }
		public void resolve() { add(24); }
	}
	
	private class POP extends Instruction {
		public POP() { numParams = 1; }
		public void resolve() { add(25); }
	}
	
	private class GOTO	extends Instruction { public GOTO()	{ super(); } public void resolve() { add(26); } }
	private class RCALL	extends Instruction { public RCALL(){ super(); } public void resolve() { add(27); } }
	
	private class RET extends Instruction {
		public RET() { numParams = 0; }
		public void resolve() { add(28); }
	}
	
	private class SLS	extends Instruction { public SLS()	{ super(); } public void resolve() { add(29); } }
	private class SLSI	extends Instruction { public SLSI()	{ super(); } public void resolve() { add(30); } }
	private class SGR	extends Instruction { public SGR()	{ super(); } public void resolve() { add(31); } }
	private class SGRI	extends Instruction { public SGRI()	{ super(); } public void resolve() { add(32); } }
	private class SEQ	extends Instruction { public SEQ()	{ super(); } public void resolve() { add(33); } }
	private class SEQI	extends Instruction { public SEQI()	{ super(); } public void resolve() { add(34); } }
	private class SNE	extends Instruction { public SNE()	{ super(); } public void resolve() { add(35); } }
	private class SNEI	extends Instruction { public SNEI()	{ super(); } public void resolve() { add(36); } }
	
	private class SCS extends Instruction {
		public SCS() { numParams = 0; }
		public void resolve() { add(37); }
	}
	
	private class SCC extends Instruction {
		public SCC() { numParams = 0; }
		public void resolve() { add(38); }
	}
	
	private class BREAK extends Instruction {
		public BREAK() { numParams = 0; }
		public void resolve() { add(39); }
	}
}