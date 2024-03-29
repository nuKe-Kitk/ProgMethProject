package render;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JComponent;

import input.InputUtility;
import logic.Data;
import logic.Monster;
import logic.Player;

public class GameScreen extends JComponent {

	private static final long serialVersionUID = 1L;
	private float opacity;
	private int direction;
	private static final GameScreen gameScreen = new GameScreen();
	public GameScreen() {
		super();

		setDoubleBuffered(true);
		this.setPreferredSize(new Dimension(Data.screenWidth, Data.screenHeight));
		opacity = 0;
		direction = 1;
		InputUtility instance = InputUtility.getInstance();

		this.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() >= 0 && e.getKeyCode() <= 255) {
					synchronized (instance) {
						instance.setKeypressed(e.getKeyCode(), false);
					}
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				synchronized (instance) {
					if (e.getKeyCode() >= 0 && e.getKeyCode() <= 255 && !instance.getKeypressed(e.getKeyCode())) {
						instance.setKeypressed(e.getKeyCode(), true);
						instance.setKeytriggered(e.getKeyCode(), true);
						if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
							if(Data.pause){
								Data.pause = false;
								instance.notifyAll();
							}
							else
								Data.pause = true;
						}
					}
				}
			}
		});

	}

	public static GameScreen getGamescreen() {
		return gameScreen;
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g2d);
		g2d.setBackground(Color.BLACK);
		g2d.clearRect(0, 0, Data.screenWidth, Data.screenHeight);
		opacity += 0.01 * direction;
		if (opacity >= 0.999 || opacity <= 0.001)
			direction *= -1;
		synchronized (RenderableHolder.getInstance()) {
			for (IRenderable entity : RenderableHolder.getInstance().getRenderableList()) {
				if(entity instanceof Player){
					g2d.setComposite((AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ((Player)entity).getFade())));
				}else if(entity instanceof Monster){
					g2d.setComposite((AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ((Monster)entity).getFade())));
				}else if (entity instanceof Foreground && ((Foreground) entity).fadable)
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
				else if(entity instanceof Background)
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ((Background)entity).getFade()));
				else if(entity instanceof Foreground)
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ((Foreground)entity).getFade()));
				else if(entity instanceof Title){
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ((Title)entity).getFade()));
				}else
					g2d.setComposite(AlphaComposite.SrcOver.derive(1f));
				if (entity.isVisible())
					entity.render(g2d);
			}
		}
	}
}
