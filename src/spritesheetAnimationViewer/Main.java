package spritesheetAnimationViewer;

import javax.swing.UIManager;

/**
 * This class is the main entry point of the program. It contains
 * the main method to launch the application.
 */
public class Main
{
	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{

		// Set the Look to the system's look
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		/*
		 * SwingUtilities.invokeLater(new Runnable() {
		 * public void run()
		 * {
		 * MainWindow frame = new MainWindow();
		 * //frame.setVisible(true);
		 * frame.run();
		 * }
		 * });
		 */
		MainWindow program = new MainWindow();
		program.start();
	}
}

// TODO: file to save all the user defined presets (last used directory, animation orientation,
// etc). add a reset to default button.
// TODO: handle selecting a color not to be rendered or using the alpha channel of the image
// create a new bufferedImage and change all the pixels matching the selected color to transparent ?

// TODO: Refactor the animation code (add a better timer) (redo the whole thing using JMono ?)

// TODO: have a look at javax.swing.JColorChooser
// https://docs.oracle.com/javase/8/docs/api/javax/swing/JColorChooser.html
// or
// https://docs.oracle.com/javafx/2/api/javafx/scene/control/ColorPicker.html
// TODO: When the application gets the focus, it highlights the text in the textbox
// handle that case so it doesn't do that (make the application focusable ?)
// TODO: Try to eliminate as much code as possible from the loop.
// i.e. : all the parsing for the textboxes should be handled with events

// TODO: UI Color profiles in menu (Dark Theme, Light Theme)
