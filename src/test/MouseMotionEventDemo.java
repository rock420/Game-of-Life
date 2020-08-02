package test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MouseMotionEventDemo extends JPanel implements MouseMotionListener {
  private int mX, mY;
  int moveX ,moveY;

  public MouseMotionEventDemo() {
    addMouseMotionListener(this);
    setVisible(true);
  }

  public void mouseMoved(MouseEvent me) {
    moveX = (int) me.getPoint().getX();
    moveY = (int) me.getPoint().getY();
    //System.out.println("Moved");
    //System.out.println(me.getPoint());
    //repaint();
  }

  public void mouseDragged(MouseEvent me) {
    //mouseMoved(me);
	if(me.isShiftDown()) {
		System.out.println("Down");
		return;
	}
	
    mX = (int) me.getPoint().getX();
    mY = (int) me.getPoint().getY();
    //System.out.println(me.getPoint());
    //System.out.println(moveX);
    //System.out.println(moveY);
    repaint();
    System.out.println("Draged");
  }

  public void paint(Graphics g) { 
    g.setColor(Color.blue);
    g.fillRect(mX, mY, 5, 5);
  }
  

  public static void main(String[] args) {
    JFrame f = new JFrame();
  
    f.getContentPane().add(new MouseMotionEventDemo());
    f.addMouseWheelListener(new WheelDemo());
    f.setSize(200, 200);
    f.setVisible(true);
  }

}