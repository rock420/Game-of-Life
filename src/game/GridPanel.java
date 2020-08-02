package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GridPanel extends JPanel{
	private int viewableCellX;
	private int viewableCellY;
	
	private int topLeftX;
	private int topLeftY;
	
	public int pixelWidth;
	public int pixelHeight;
	
	private int mouseX;
	private int mouseY;
	
	private Cell[][] gridViewable;
	private final int baseCellSize;
	private int cellSize;
	public Map<Point,Integer> liveCell = new ConcurrentHashMap<Point,Integer>();
	ReadWriteLock gridLock = new ReentrantReadWriteLock();
	
	
	private final double MAX_ZOOM = 4.0;
	private final double MIN_ZOOM = 0.1;
	private final double WHEEL_ZOOM_SPEED = 0.1;
	private double zoom_factor = 1.0;
	
	boolean isPaused = false;
	boolean grid = false;
	
	public GridPanel(int viewableWidth,int viewableHeight,int pixelWidth,int pixelHeight,int cellSize,int topLeftX,int topLeftY) {
		super(null);
		setLayout(null);
		this.viewableCellX = viewableWidth;
		this.viewableCellY = viewableHeight;
		this.pixelHeight = pixelHeight;
		this.pixelWidth = pixelWidth;
		this.topLeftX = topLeftX;
		this.topLeftY = topLeftY;
		this.cellSize = cellSize;
		this.baseCellSize = cellSize;
		
		resetGrid();
		this.addMouseMotionListener(new MouseMotionGrid());
		this.addMouseWheelListener(new MouseWheelGrid());
		this.addKeyListener(new keyPressGrid());
		this.setFocusable(true);
	}
	
	public void initialize() {
		/*liveCell.put(new Point(20,10), 1);
		liveCell.put(new Point(20,11), 1);
		liveCell.put(new Point(20,12), 1);
		liveCell.put(new Point(21,10), 1);
		liveCell.put(new Point(19,11), 1);
		liveCell.put(new Point(7,4), 1);
		liveCell.put(new Point(8,4), 1);
		liveCell.put(new Point(9,4), 1); */
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("/home/ayan/eclipse-workspace/GameOfLife/input.txt"));
		}catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		String line;
		try {
			line = reader.readLine();
			while (line != null) {
				String[] inputArr = line.split(",");
				int x = Integer.parseInt(inputArr[0]);
				int y = Integer.parseInt(inputArr[1]);
				liveCell.put(new Point(x,y), 1);
				
				// read next line
				line = reader.readLine(); 
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public synchronized void resetGrid()
	{	
		gridViewable = new Cell[viewableCellY+2][viewableCellX+2];
		
		gridLock.writeLock().lock();
		try {
			liveCell = new ConcurrentHashMap<Point,Integer>();
		}
		finally {
			gridLock.writeLock().unlock();
		}
		
		for(int i=0;i<viewableCellY+2;i++)
		{
			for(int j=0;j<viewableCellX+2;j++)
			{	
				Cell cell = new Cell(topLeftX+j,topLeftY+i);
				gridViewable[i][j] = cell;
			}
		}
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g; 
		
		removeAll(); //clear the panel
		g2.setBackground(Color.BLACK);
		g2.fillRect(0, 0, pixelWidth, pixelHeight);
		
		for(int i=0;i<viewableCellY+2;i++)
		{
			for(int j=0;j<viewableCellX+2;j++)
			{
				Cell cell = gridViewable[i][j];
				if(cell.isLive())
				{
					g2.setColor(cell.getColor());
					g2.fillRect(j*cellSize, i*cellSize, cellSize, cellSize);
				}
				if(grid) {
					g2.setColor(Color.GRAY);
					g2.drawRect(j*cellSize, i*cellSize, cellSize, cellSize);
				}
			}
		}
		
	}
	
	public synchronized void updateGrid()
	{	
		for(int i=0;i<viewableCellY+2;i++)
		{
			for(int j=0;j<viewableCellX+2;j++)
			{
				Point coordinate = new Point(topLeftX+j,topLeftY+i);
				Cell cell = gridViewable[i][j];
				
				if(liveCell.containsKey(coordinate)) cell.live();
				else cell.dead();
				
				cell.changeCoordinate(topLeftX+j,topLeftY+i);
			}
		}
	}
	
	
	
	private class MouseMotionGrid implements MouseMotionListener{

		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			int draggedX = (int) e.getPoint().getX();
			int draggedY = (int) e.getPoint().getY();
			
			if(e.isShiftDown()) {   // Drag screen
				dragScreen(draggedX-mouseX,draggedY-mouseY);
			}
			
			mouseX = draggedX;
			mouseY = draggedY;
			
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			if(e.getX()>pixelWidth) mouseX = pixelWidth;
			else if(e.getX()<0) mouseX = 0;
			else mouseX = e.getX();
			
			if(e.getY()>pixelHeight) mouseY = pixelHeight;
			else if(e.getY()<0) mouseY = 0;
			else mouseY = e.getY();
			
			if(e.isControlDown() )
				addCell(mouseX,mouseY);
			
		}
		
	}
	
	private void addCell(int mx,int my)
	{	
		int coordinateX = (mx/cellSize)+ topLeftX;
		int coordinateY = (my/cellSize)+topLeftY;
		liveCell.put(new Point(coordinateX,coordinateY), 1);
		updateGrid();
		
	}
	
	private void dragScreen(int dx,int dy)
	{
		dx = -dx;
		dy = -dy;
		
		int numCellX = 0;
		int numCellY = 0;
		
		if(dx>0) {  //left side dragging
			numCellX -= (dx/cellSize);
		}
		else {  //right side dragging
			numCellX += (Math.abs(dx)/cellSize);
		}
		
		if(dy>0) { //top side dragging
			numCellY -= (dy/cellSize);
		}
		else { // downside dragging
			numCellY += (Math.abs(dy)/cellSize);
		}
		
		//move the screen
		topLeftX -= numCellX;
		topLeftY -= numCellY;
		this.removeAll();
		updateGrid();
		repaint();
	}
	
	public synchronized void  zoomRedraw(double dzoomFactor)
	{
		zoom_factor += dzoomFactor;
		
		if(zoom_factor>MAX_ZOOM) zoom_factor = MAX_ZOOM;
		else if(zoom_factor<MIN_ZOOM) zoom_factor = MIN_ZOOM;
		
		int oldCellSize = cellSize;
		int newCellSize = (int)(baseCellSize*zoom_factor);
		
		int oldCellRelativeX = mouseX/oldCellSize;
		int oldCellRelativeY = mouseY/oldCellSize;
		
		int newCellRelativeX = mouseX/newCellSize;
		int newCellRelativeY = mouseY/newCellSize;
		
		// change the parameters based on zooming
		topLeftX = topLeftX - (newCellRelativeX - oldCellRelativeX);
		topLeftY = topLeftY - (newCellRelativeY - oldCellRelativeY);
		
		viewableCellX = pixelWidth/newCellSize;
		viewableCellY = pixelHeight/newCellSize;
		
		cellSize = newCellSize;
		
		//update the grid and repaint
		this.removeAll();
		remakeView();
		repaint();
	}
	
	public void remakeView()
	{
		gridViewable = new Cell[viewableCellY+2][viewableCellX+2];
		for(int i=0;i<viewableCellY+2;i++)
		{
			for(int j=0;j<viewableCellX+2;j++)
			{
				Point coordinate = new Point(topLeftX+j,topLeftY+i);
				Cell cell = new Cell(topLeftX+j,topLeftY+i);
				if(liveCell.containsKey(coordinate)) cell.live();
				gridViewable[i][j] = cell;
			}
		}
	}
	
	private class MouseWheelGrid implements MouseWheelListener {

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			// TODO Auto-generated method stub
			int direction = -e.getWheelRotation();  // scroll up for zoom in and scroll down for zoom out
			if(e.isShiftDown())
				zoomRedraw(direction*WHEEL_ZOOM_SPEED);
		}
		
	}
	
	private class keyPressGrid implements KeyListener{

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub	
		}

		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			
			if(e.getKeyCode() == KeyEvent.VK_SPACE) {
				isPaused = !isPaused;
			}
		        
			if(e.getKeyCode()== KeyEvent.VK_ESCAPE) {
				resetGrid();
				initialize();
			}
			
			if(e.getKeyCode()== KeyEvent.VK_G) {
				grid = !grid;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}

	
}
