import java.awt.event.*;
import javax.swing.*;

public class CompControls extends JFrame implements ActionListener
{
	Computer controlee;
	JButton btnStart, btnStop, btnStep;
	JTextField txtRate;
	JLabel lblRate;
	
	public CompControls(Computer c)
	{
		controlee = c;
		
		setTitle("Multicore Controls");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel pane = new JPanel();
		
		lblRate = new JLabel("Clock Rate:");
		txtRate = new JTextField("0", 5);
		
		//create buttons
		btnStart = new JButton("Start");
		btnStart.setActionCommand("start");
		btnStart.addActionListener(this);
		
		btnStop = new JButton("Stop");
		btnStop.setActionCommand("stop");
		btnStop.addActionListener(this);
		
		btnStep = new JButton("Step");
		btnStep.setActionCommand("step");
		btnStep.addActionListener(this);
		
		pane.add(lblRate);
		pane.add(txtRate);
		
		pane.add(btnStart);
		pane.add(btnStop);
		pane.add(btnStep);
		
		add(pane);
		
		pack();
		
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
        if("start".equals(e.getActionCommand()))
		{
			controlee.pause = false;
        } else if("stop".equals(e.getActionCommand()))
		{
			controlee.pause = true;
		} else if("step".equals(e.getActionCommand()))
		{
			controlee.run();
		}
    }
}