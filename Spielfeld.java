import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Spielfeld extends JPanel {

	int sizeX; // sizeX * sizeY entspricht der ...
	int sizeY; // ... Anzahl der Quadratmeter (QMs)
	int mines; // Anzahl der Minen
//	int flaggedMinesCount = 0; // zählt, wie viele Minen schon entschäft wurden
	int betretenCount = 0; // zählt, wie viele Felder betreten wurden
	qm[][] area; // das Minenfeld, bestehend aus sizeX*sizeY Quadratmetern (QMs)
	int sizeQM; // Maße eines QMs (immer quadratisch, also sizeQM*SizeQM)
	MineSweeperWindow msw;
	boolean safe = true; // false, wenn mindestens eine Mine auf dem Feld liegt
	boolean gameOver = false; // Hilfsvariable für das Spielende
	boolean deathExists; // Hilfsvariable 2 für das Spielende

	public Spielfeld(int sizeX, int sizeY, int mines, int sizeQM, MineSweeperWindow msw) {

		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.mines = mines;
		this.sizeQM = sizeQM;
		this.msw = msw;
		this.setSize(msw.getWidth(), msw.getHeight());
		area = new qm[sizeX][sizeY];

		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {

				/////////// einzelne QMs erzeugen //////////
				area[i][j] = new qm();

				area[i][j].x = /* this.getWidth() */sizeQM * i;
				area[i][j].y = /* this.getHeight() */sizeQM * j;

			}
		}
	}

	public void paintComponent(final Graphics g) {

		super.paintComponent(g);
		g.setFont(new Font(null, sizeQM * 4 / 5, sizeQM * 4 / 5));
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {

				if (area[i][j].betreten) { // Wenn das Feld betreten wurde
					
					if (area[i][j].danger) { // Wenn da eine Mine liegt
						
						if(!gameOver) { // Wenn es die angeklickte Mine ist, die grade gezeichnet wird
							
							gameOver = true;
							gameOverAnimation(); // startet schonmal die beiden scheduler
							area[i][j].death = true;
						}
						if(area[i][j].death) {
							g.setColor(new Color(200, 0, 0));
							g.fillRect(area[i][j].x, area[i][j].y, sizeQM, sizeQM);
						}
						
						g.setColor(Color.BLACK);
						g.fillOval(area[i][j].x + 3, area[i][j].y + 3, sizeQM - 6, sizeQM - 6);
						
						if (area[i][j].flagged) {
							g.setColor(new Color(200, 0, 0));
							g.drawString("M", area[i][j].x + sizeQM / 4 - 1,
									area[i][j].y + sizeQM / 2 + sizeQM / 3);
						}
						
						
					} else { // Wenn da keine Mine liegt
						
						switch (area[i][j].connectingMines) { // QM-Farbe anhand der Anzahl der angrenzenden Minen bestimmen
						case 0:
							g.setColor(Color.GREEN);
							break;
						case 1:
							g.setColor(new Color(196, 255, 0));
							break;
						case 2:
							g.setColor(Color.YELLOW);
							break;
						case 3:
							g.setColor(new Color(255, 193, 0));
						case 4:
							g.setColor(Color.ORANGE);
							break;
						case 5:
							g.setColor(new Color(255, 86, 0));
							break;
						default:
							g.setColor(Color.RED);
						}
						
						g.fillRect(area[i][j].x, area[i][j].y, sizeQM, sizeQM);
						g.setColor(Color.BLACK);
//						g.drawRect(area[i][j].x + 1, area[i][j].y + 1, sizeQM - 2, sizeQM - 2);
						if (area[i][j].connectingMines > 0)
							g.drawString("" + area[i][j].connectingMines, area[i][j].x + sizeQM / 3 - 1,
									area[i][j].y + sizeQM / 2 + sizeQM / 3);

					}
					
				} else if (area[i][j].flagged) { // Wenn der QM geflagt wurde
					
					if(deathExists) {
						g.setColor(Color.GREEN);
//						g.fillRect(area[i][j].x, area[i][j].y, sizeQM, sizeQM);
					} else
					
					g.setColor(Color.BLACK);
					g.drawString("M", area[i][j].x + sizeQM / 4 - 1,
							area[i][j].y + sizeQM / 2 + sizeQM / 3);
//					g.drawRect(area[i][j].x + 1, area[i][j].y + 1, sizeQM - 2, sizeQM - 2);
					
				} else
					g.setColor(new Color(200, 200, 200));

				g.setColor(Color.BLACK);
				g.drawRect(area[i][j].x, area[i][j].y, sizeQM, sizeQM);
			}
		}
	}

	public byte[] hitTest(int x, int y) { // wird bei klick aufgerufen

		byte[] ret = new byte[2];
		for (byte i = 0; i < sizeX; i++) {
			for (byte j = 0; j < sizeY; j++) {
				if (area[i][j].x < x && area[i][j].x + sizeQM > x && area[i][j].y < y && area[i][j].y + sizeQM > y) {
					ret[0] = i;
					ret[1] = j;
					return ret;
				}
			}
		}
		return null;
	}

	public class qm { // Ersatz für struct

		int x; // Koordinaten
		int y; // in Pixeln !!
		boolean danger; // true, wenn mine vorhanden
		boolean betreten = false;
		boolean flagged = false;
		boolean death = false;
		byte connectingMines = 0;
	}

	public void betreteQM(byte[] location) {

		if(area[location[0]][location[1]].flagged && !gameOver)
			return;
//		if(area[location[0]][location[1]].danger && !gameOver) {
//			gameOver = true;
//		}
		
		area[location[0]][location[1]].betreten = true;
		betretenCount++;
		
		if(area[location[0]][location[1]].connectingMines < 1) { // FloodFill
			
			byte[] recLoc = new byte[2];
			
			for(byte i = (byte) ((location[0] > 0) ? -1 : 0); i < ((location[0] < sizeX-1) ? 2 : 1); i++)
				for(byte j = (byte) ((location[1] > 0) ? -1 : 0); j < ((location[1] < sizeY-1) ? 2 : 1); j++) {
					
					if(!area[location[0]+i][location[1]+j].betreten) {	
						
						recLoc[0] = (byte) (location[0]+i);
						recLoc[1] = (byte) (location[1]+j);
						betreteQM(recLoc);
					}
				}
		}
		// Bei betreten des letzten sicheren QMs:
		if(betretenCount == sizeX*sizeY-mines && !gameOver && !area[location[0]][location[1]].danger) {
			
			gameOver = true; // verhindert rekursive Wiederholung dieses if-Blocks
			
			for(byte i = 0; i < sizeX; i++)
				for(byte j = 0; j < sizeY; j++) {
					
					if(area[i][j].danger && !area[i][j].flagged)
						flagQM(i, j);
					
				}
			
			repaint();
			if(JOptionPane.showConfirmDialog(msw, "Du hast gewonnen!\nErneut spielen?", "Glückwunsch!", JOptionPane.YES_NO_OPTION) 
					== JOptionPane.YES_OPTION) {
				if(JOptionPane.showConfirmDialog(msw, "Möchten Sie die Einstellungen beibehalten?", "Erneut spielen", JOptionPane.YES_NO_OPTION)			    				== JOptionPane.YES_OPTION) 
			   		new MineSweeperWindow(sizeX, sizeY, mines);
			   	else
			   		new ConfigWindow();
						
		  	}
				
		   	msw.dispose();
		}		
	}

	public void minenAbwerfen(byte safeX, byte safeY) {

		for(short i = 0; i < mines;) {
			
			int randomQMx = (int) Math.round(Math.random()*(sizeX-1));
			int randomQMy = (int) Math.round(Math.random()*(sizeY-1));
			
			if(!area[randomQMx][randomQMy].danger 
					&& !(randomQMx >= safeX-1 && randomQMx <= safeX+1 && randomQMy >= safeY-1 && randomQMy <= safeY+1)) {
				
				area[randomQMx][randomQMy].danger = true;
				i++;
			}
		}
		
		for(int x = 0; x < sizeX; x++) // Zahlenwerte der Felder zuweisen
			for(int y = 0; y < sizeY; y++)
				area[x][y].connectingMines = countConnectingMines(x, y);
	}
	
	public byte countConnectingMines(int x, int y) { // auch area[x][y] zählt dazu, also höchstens 9
		
		byte ret = 0;
		
		for(byte i = (byte) ((x > 0) ? -1 : 0); i < ((x < sizeX-1) ? 2 : 1); i++)
			for(byte j = (byte) ((y > 0) ? -1 : 0); j < ((y < sizeY-1) ? 2 : 1); j++) {
				ret += area[x+i][y+j].danger ? (byte)1 : (byte)0;	
			}
		
		return ret;
	}
	
	public void flagQM(int x, int y) {
		
		if(area[x][y].betreten)
			return;
		
//		if(area[x][y].danger)
//			flaggedMinesCount = area[x][y].flagged ? flaggedMinesCount - 1 : flaggedMinesCount + 1; 
		
		area[x][y].flagged = !area[x][y].flagged;
		
		
	}
	
	public void gameOverAnimation() {
			
//		this.removeMouseListener(this.getMouseListeners());
		ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();
		
		s.schedule(new Runnable() {
			
			public void run() {
				
				deathExists = true;
				for(byte i = 0; i < sizeX; i++)
					for(byte j = 0; j < sizeY; j++) {
						
						if(area[i][j].danger)
							betreteQM(new byte[] {i,j});
						
					}
				
				repaint();
				s.shutdown();
			}
			
		}, 1, TimeUnit.SECONDS);
		
		s.schedule(new Runnable() {
			
		    public void run() {
		        
		    	if(JOptionPane.showConfirmDialog(msw, "   Game Over!\nErneut versuchen?", "Game Over!", JOptionPane.YES_NO_OPTION) 
		    			== JOptionPane.YES_OPTION) {
		    		
		    		if(JOptionPane.showConfirmDialog(msw, "Möchten Sie die Einstellungen beibehalten?", "Erneut spielen", JOptionPane.YES_NO_OPTION)
		    				== JOptionPane.YES_OPTION) 
		    			new MineSweeperWindow(sizeX, sizeY, mines);
		    		else
		    			new ConfigWindow();
		    	}
		    	
		    	msw.dispose();
		    	
		    	s.shutdown();
		    }
		}, 3, TimeUnit.SECONDS);
	}
}
