import java.awt.*;
import javax.swing.*;

public class Terminal extends JFrame
{
	final static Font font = new Font("Monospaced", Font.PLAIN, 12);
	JTextArea display;
	
	Memory RAM;
	int width, height;
	int textLoc;
	
	public Terminal(Memory m, int addr, int w, int h)
	{
		RAM = m;
		textLoc = addr;
		
		width = w;
		height = h;
		
		setTitle("Multicore Terminal");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel pane = new JPanel();
		
		display = new JTextArea(height+1, width+1);
		display.setFont(font);
		display.setBackground(Color.BLACK);
		display.setForeground(Color.GREEN);
		display.disable();
		display.setLineWrap(true);
		
		pane.add(display);
		
		add(pane);
		pack();
		setVisible(true);
	}
	
	public void update()
	{
		String text = "";
		for(int i=0; i<width*height; i++)
		{
			char c = (char)RAM.read(textLoc+i);
			
			if(Character.isWhitespace(c))
				text += ' ';
			else
				text += (char)RAM.read(textLoc+i);
		}
		
		display.setText(text);
	}
}