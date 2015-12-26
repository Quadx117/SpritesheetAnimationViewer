package spriteAnimator;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class AnimationPanel extends JComponent
{
	public BufferedImage image;

	public int sx1;
	public int sx2;
	public int sy1;
	public int sy2;

	public AnimationPanel()
	{}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if (image == null)
		{
			g.setColor(this.getBackground());
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
		}

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
