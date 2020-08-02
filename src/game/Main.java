package game;

import java.awt.EventQueue;

public class Main {
	
	public static void main(String[] args){
		GameFrame gFrame = new GameFrame();
		System.out.println("Frame initialized");
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int i=0;
				while(true)
				{	
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						break;
					}
					//i++;
					if(!gFrame.gPanel.isPaused) {
							gFrame.nexState();
							//gFrame.gPanel.updateGrid();
							//gFrame.gPanel.repaint();
							EventQueue.invokeLater(new Runnable() {
								public void run() {
									gFrame.gPanel.updateGrid();
									gFrame.gPanel.repaint();
								}
							});
					}
				}
			}
		
		}).start();
	
	}
}
