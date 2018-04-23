import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ConfigWindow extends JFrame {
	
	JTextField minenInput;
	JTextField yInput;
	JTextField xInput;
	
	public ConfigWindow() {
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("Minesweeper für Arme");
		JButton ok = new JButton("OK");
		xInput = new JTextField();
		yInput = new JTextField();
		minenInput = new JTextField();
		JLabel x = new JLabel("Breite des Spielfeldes(4 bis 30 Felder):");
		JLabel y = new JLabel("Höhe des Spielfeldes(4 bis 30 Felder):");
		JLabel minen = new JLabel("Anzahl der Minen(max. Breite*Höhe-9):");
		JLabel welcome = new JLabel ("Willkommen zu Minesweeper!");
		welcome.setFont(new Font(welcome.getText(), 0, 30));
		
		ok.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				try {
					startGame(Integer.parseInt(xInput.getText()), Integer.parseInt(yInput.getText()), Integer.parseInt(minenInput.getText()));
				} catch (Exception e) {
					JOptionPane.showMessageDialog(ConfigWindow.this, "Sie haben ungültige Daten eingegeben.");
//					e.printStackTrace();
				}
			}
		});
		
		JPanel set = new JPanel();
		set.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(4,4,4,4);
		
		c.gridx = 1;
		c.gridy = 1;
		set.add(x, c);
		c.gridx = 2;
		set.add(xInput, c);
		c.gridy = 2;
		set.add(yInput, c);
		c.gridx = 1;
		set.add(y, c);
		c.gridy = 3;
		set.add(minen, c);
		c.gridx = 2;
		set.add(minenInput, c);
		c.gridy = 4;
		set.add(ok, c);

		
		this.add(welcome, BorderLayout.NORTH);
		this.add(set, BorderLayout.CENTER);
		
		getRootPane().setDefaultButton(ok);
		
		this.setLocationRelativeTo(null);
		this.pack();
		this.setVisible(true);
	}
	
	public void startGame(int x, int y, int minen) throws IllegalArgumentException {
		
		if(x > 30 || x < 4 || y > 30 || y < 4 || minen < 1 || minen > x*y-9) {
			throw new IllegalArgumentException();
		}
		
		this.dispose();
		new MineSweeperWindow(x,y,minen);
	}
}
