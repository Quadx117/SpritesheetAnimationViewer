package spritesheetAnimationViewer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class AnimationPanel extends JComponent
{
	/** The image used to draw */
	public BufferedImage image;

	/** The top left coordinate of the source rectangle. */
	public int sx1;
	/** The top right coordinate of the source rectangle. */
	public int sx2;
	/** The bottom left coordinate of the source rectangle. */
	public int sy1;
	/** The bottom right coordinate of the source rectangle. */
	public int sy2;

	public AnimationPanel()
	{}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		// Clear the component to the same color as the background
		g.setColor(this.getBackground());
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		// Draw the animation frame
		g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), sx1, sy1, sx2, sy2, null);
	}

	/*
	 * @Override
	 * public Dimension getMinimumSize()
	 * {
	 * return new Dimension(this.getWidth(), this.getHeight());
	 * }
	 * 
	 * @Override
	 * public Dimension getMaximumSize()
	 * {
	 * return new Dimension(this.getWidth(), this.getHeight());
	 * }
	 * 
	 * @Override
	 * public Dimension getPreferredSize()
	 * {
	 * return new Dimension(this.getWidth(), this.getHeight());
	 * }
	 */

	@Override
	public boolean isOpaque()
	{
		return true;
	}
}
