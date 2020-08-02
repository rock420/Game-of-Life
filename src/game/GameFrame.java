package game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JDialog;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class GameFrame extends JFrame{
	
	public GridPanel gPanel;
	Dimension screensize=Toolkit.getDefaultToolkit().getScreenSize();
	int screenHeight=screensize.height;
	int screenWidth=screensize.width;
	int k=0;
	public GameFrame() {
		super("Game of Life");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		gPanel = new GridPanel(40, 30, 20*40, 20*30, 20,0, 0);
		add(gPanel,BorderLayout.CENTER);
		//setBounds(0,0,screenWidth,screenHeight);
		this.setMinimumSize(new Dimension(gPanel.pixelWidth, gPanel.pixelHeight));
		initialize();
		pack();
		setVisible(true);
	}
	
	public void initialize() {
		gPanel.liveCell.put(new Point(20,10), 1);
		gPanel.liveCell.put(new Point(20,11), 1);
		gPanel.liveCell.put(new Point(20,12), 1);
		gPanel.liveCell.put(new Point(21,10), 1);
		gPanel.liveCell.put(new Point(19,11), 1);
		gPanel.liveCell.put(new Point(7,4), 1);
		gPanel.liveCell.put(new Point(8,4), 1);
		gPanel.liveCell.put(new Point(9,4), 1); 
		
	}
	
	public synchronized void nexState()
	{	k++;
		//System.out.println(k);
		ArrayList<Point> live = new ArrayList<Point>();
		ArrayList<Point> dead = new ArrayList<Point>();
		HashMap<Point,Integer> candidates = new HashMap<Point,Integer>();
		
		for(Point point:gPanel.liveCell.keySet())
		{ 
			int aliveNeighbour = 0;
			for(int i=-1;i<=1;i++){
				for(int j=-1;j<=1;j++) {
					if(i==0 && j==0) continue;
					// neighbour point
					Point neighbour  = new Point(point.x+j, point.y+i);
					
					if(gPanel.liveCell.containsKey(neighbour)) aliveNeighbour++;
					else {
						candidates.put(neighbour, candidates.getOrDefault(neighbour, 0)+1);
					}
				}
			}
			
			if(aliveNeighbour<2 || aliveNeighbour>3) dead.add(point);     //UnderPopulation and OverPopulation
		}
		
		for (HashMap.Entry<Point,Integer> candidate : candidates.entrySet()) {
			if(candidate.getValue()==3) live.add(candidate.getKey());   // Reproduction
		}
		
		gPanel.gridLock.writeLock().lock();
		try {
			for(Point point:live) {
				//System.out.println(point);
				gPanel.liveCell.put(point, 1);
			}
			for(Point point:dead) {
				gPanel.liveCell.remove(point);
			}
		}
		finally {
			gPanel.gridLock.writeLock().unlock();
		}
		
	}
	
	
	public static void main(String[] args)
	{
		System.out.println("started");
	}
}
