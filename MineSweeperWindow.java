import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MineSweeperWindow extends JFrame {

	int sizeQM = 24;
	Spielfeld spielfeld;

	public MineSweeperWindow(int sizeX, int sizeY, int mines) {

		Container contentPane = getContentPane(); //////// WICHTIG ! Sonst zählt die Titlebar dazu!
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("Minesweeper by Andreas Wenzel");

		spielfeld = new Spielfeld(sizeX, sizeY, mines, sizeQM, this);
		
		spielfeld.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent e) {
				
				byte[] location = spielfeld.hitTest(e.getX(), e.getY());
				
				if(location != null) {
					if(spielfeld.safe && e.getButton() == MouseEvent.BUTTON1) {
						spielfeld.minenAbwerfen(location[0], location[1]); // übergibt Koordinaten des "Safe Spots" (erstes geklicktes Feld)
						spielfeld.safe = false;
					}
					if(e.getButton() == MouseEvent.BUTTON1) {
						spielfeld.betreteQM(location);
						if(spielfeld.area[location[0]][location[1]].danger && !spielfeld.area[location[0]][location[1]].flagged) 
							spielfeld.removeMouseListener(this);
					}
					else
						spielfeld.flagQM(location[0], location[1]);
					
					spielfeld.repaint();
					
				}
			}
		});
		
		contentPane.add(spielfeld, BorderLayout.CENTER);
//		this.setSize(sizeQM * sizeX, sizeQM * sizeY);
		this.setSize(sizeQM * (sizeX+1)+2, sizeQM * (sizeY+2)); // Warum auch immer das funktioniert
//		this.setSize(sizeQM * (sizeX+4), sizeQM * (sizeY+4));
		this.setMinimumSize(new Dimension(this.getWidth(), this.getHeight()));
//		this.setResizable(false);
//		this.setMaximumSize(new Dimension(this.getWidth(), this.getHeight()));
//		this.pack();
//		System.out.println(sizeX + ":" + sizeY + ":" + sizeQM);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
}
