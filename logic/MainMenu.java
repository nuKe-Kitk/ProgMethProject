package logic;

import java.awt.event.KeyEvent;

import input.InputUtility;
import render.Background;
import render.Foreground;
import render.GameScreen;
import render.RenderableHolder;

public class MainMenu implements Runnable {
	private Background bg;
	private Foreground eye;
	private Foreground select;
	private int option;
	private boolean start;
	private boolean update;

	public MainMenu() {
		start = false;
		update = false;
		bg = new Background(null, 9998);
		eye = new Foreground(null, 9998, true);
		option = 0;
	}

	public void space() {// press space bar
		synchronized (RenderableHolder.getInstance()) {
			RenderableHolder.getInstance().getRenderableList().remove(bg);
		}
		bg = new Background(null, 9999);
		select = new Foreground(null, 9999 + option, false);
	}

	public void start() {
		// TODO Auto-generated method stub
		synchronized (RenderableHolder.getInstance()) {
			// level 1
			RenderableHolder.getInstance().getRenderableList().remove(bg);
			RenderableHolder.getInstance().getRenderableList().remove(select);
			System.out.println("Remove select " + select.toString());
			select = null;
			RenderableHolder.getInstance().getRenderableList().remove(eye);
		}
		Player player1 = new Player(0, 5, 0);
		Thread p1 = new Thread(player1);
		new Background(player1, 1);
		new Foreground(player1, 1, false);
		Monster m1 = new Monster(0,0,1,1);
		p1.start();
		start = true;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		InputUtility instance = InputUtility.getInstance();
		while (true) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			System.out.println("Hello");
			synchronized (instance) {
				if (instance.getKeytriggered(KeyEvent.VK_SPACE)) {
					instance.setKeytriggered(KeyEvent.VK_SPACE, false);
//					System.out.println(">"+instance.getKeytriggered(KeyEvent.VK_SPACE));
					space();
					break;
				}
				instance.postUpdate();
			}
			GameScreen.getGamescreen().repaint();
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (true) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			update = false;
			
			synchronized (instance) { 
//				System.out.println(instance.getKeytriggered(KeyEvent.VK_SPACE));
				if (instance.getKeytriggered(KeyEvent.VK_DOWN)) {
					update = true;
					if (option < 3)
						option++;
					else
						option = 0;
					instance.setKeytriggered(KeyEvent.VK_DOWN, false);
				} else if (instance.getKeytriggered(KeyEvent.VK_UP)) {
					update = true;
					if (option > 0)
						option--;
					else
						option = 3;
					instance.setKeytriggered(KeyEvent.VK_UP, false);
				} else if (instance.getKeytriggered(KeyEvent.VK_SPACE)) {
//					update = true;
					instance.setKeytriggered(KeyEvent.VK_SPACE, false);
//					RenderableHolder.getInstance().getRenderableList().remove(select);
					
					switch (option) {
					case 0:
						start();
						break;
					case 3:
						System.exit(0);
					}
				}
				instance.postUpdate();
			}
			synchronized (RenderableHolder.getInstance()) {
				if (update) {
					RenderableHolder.getInstance().getRenderableList().remove(select);
					select = new Foreground(null, 9999 + option, false);
				}	
			}
			GameScreen.getGamescreen().repaint();
			if (start)

				break;
		}
	}

}