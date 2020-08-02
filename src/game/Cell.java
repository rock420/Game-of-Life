package game;

import java.awt.Color;

public class Cell {
	public int x;
	public int y;
	private Color color = Color.BLACK;
	
	public Cell()
	{
		
	}
	public Cell(int x,int y)
	{
		this.x = x;
		this.y = y;
	}
	public Color getColor() {
		return color;
	}
	public void dead() {
		this.color = Color.BLACK;
	} 
	public void live() {
		this.color = Color.WHITE;
	}
	
	public boolean isLive()
	{
		return this.color==Color.WHITE;
	}
	
	public void changeCoordinate(int x,int y)
	{
		this.x = x;
		this.y = y;
	}

}
