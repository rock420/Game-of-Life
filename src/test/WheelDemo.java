package test;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;

public class WheelDemo implements MouseWheelListener {

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		int step = e.getWheelRotation();
		System.out.println(step);
	}
	
	  public static void main(String[] args) {
		    JFrame f = new JFrame();
		  
		    f.addMouseWheelListener(new WheelDemo());
		    f.setSize(200, 200);
		    f.setVisible(true);
	  }

}
