import java.awt.event.*;
import javax.swing.*;

public class Peeker extends JFrame implements ActionListener
{
	JTextField address;
	JLabel value;
	JButton peek;
	
	Memory RAM;
	
	public Peeker(Memory mem)
	{
		RAM = mem;
		
		setTitle("Peeker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel pane = new JPanel();
		
		address = new JTextField("0", 5);
		
		value = new JLabel("0");
		
		peek = new JButton("Peek");
		peek.setActionCommand("peek");
		peek.addActionListener(this);
		
		pane.add(address);
		pane.add(value);
		pane.add(peek);
		
		add(pane);
		
		pack();
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
        if ("peek".equals(e.getActionCommand())) {
            try
			{
				int addr = Integer.parseInt(address.getText());
				int val = RAM.read(addr);
				
				value.setText(Integer.toString(val));
			} catch(Exception ex) {}
        }
    }
}