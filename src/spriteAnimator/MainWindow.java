package spriteAnimator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicMenuBarUI;

@SuppressWarnings("serial")
public class MainWindow extends JFrame implements Runnable
{
	/**
	 * The BufferedImage that will contain the data of the loaded sprite sheet.
	 */
	private BufferedImage loadedImage;

	// private Canvas canvas = new Canvas();
	private AnimationPanel animationCanvas;
	public boolean running = false;

	private MainWindow mainWindow;

	private String title = "Sprite Animator (Alpha 0.7)";
	private int windowWidth = 900;
	private int windowHeight = 600;
	// TODO: Need to find a way to get that info in another way
	private int animationPanelWidth = 300;
	private int animationPanelHeight = 250;

	private Color bgColor = new Color(80, 80, 80);
	private Color bgColorTxtBox = new Color(70, 70, 70);
	private Color outsideBorderColor = new Color(70, 70, 70);
	private Color insideBorderColor = new Color(94, 94, 94);
	private Color titleColor = new Color(200, 200, 200);
	private Color textColor = new Color(160, 160, 160);

	private JPanel contentPane;
	private JPanel spriteSheetViewerPanel;
	private JPanel animationPanel;
	private JLabel picLabel;
	private JTextField txtFrameWidth;
	private JTextField txtFrameHeight;
	private JTextField txtXOffset;
	private JTextField txtYOffset;
	private JTextField txtAnimationLength;
	private JSlider fpsSlider;
	private JSlider zoomSlider;

	private final JFileChooser fileChooser = new JFileChooser();
	private final FileFilter filter = new FileNameExtensionFilter("Images (*.jpg, *.png)", "jpg", "jpeg", "png");

	private int time = 0;
	private int dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2;
	private int animPos = 0;
	// TODO: Need to find a better way for these default values
	private int xAxisAnim = 0;
	private int yAxisAnim = 1; // Set the default animation axis to y;
	private int zoom = 1;

	private int frameWidth = 0;
	private int frameHeight = 0;
	private int animLength = 0;
	private int xSpriteOffset = 0;
	private int ySpriteOffset = 0;

	private long animLastTime = 0;
	private long animCurrentTime = 0;
	private double animDelta = 0.0;
	private double animSpeed = 1.0 / 10.0;

	private String path;

	/**
	 * Create the frame.
	 */
	public MainWindow()
	{
		mainWindow = this;
		// TODO: find a way to not use this hack
		// TODO: See if we can get rid of canvas (JPanel ?)
		// canvas.setBounds(4, 4, 292, 242);

		// canvas.setPreferredSize(new Dimension(animationPanelWidth, animationPanelHeight));

		Dimension size = new Dimension(windowWidth, windowHeight);
		//getContentPane().setPreferredSize(size);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
		setTitle(title);
		// getContentPane().add(this);

		contentPane = new JPanel();
		// contentPane.setPreferredSize(size);
		contentPane.setBackground(bgColor);
		contentPane.setForeground(textColor);
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 600, 300 };
		gbl_contentPane.rowHeights = new int[] { 250, 350 };
		gbl_contentPane.columnWeights = new double[] { 100.0, 0.0 };
		gbl_contentPane.rowWeights = new double[] { 100.0, 0.0 };
		contentPane.setLayout(gbl_contentPane);

		fileChooser.setFileFilter(filter);
		initMenuBar();
		initSpriteSheetViewerPanel();
		initAnimationPanel();
		initAnimationSettingsPanel();
		// setJMenuBar(mb);

		repaint();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void initMenuBar()
	{
		
		JMenuBar menuBar = new JMenuBar();

		JMenu mnFileMenu = new JMenu("File");
		menuBar.add(mnFileMenu);

		JMenuItem mntmOpenMenuItem = new JMenuItem("Open");
		mntmOpenMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		mnFileMenu.add(mntmOpenMenuItem);

		JSeparator separator = new JSeparator();
		mnFileMenu.add(separator);

		JMenuItem mntmExitMenuItem = new JMenuItem("Exit");
		mnFileMenu.add(mntmExitMenuItem);

		JMenu mnEditMenu = new JMenu("Edit");
		menuBar.add(mnEditMenu);

		JMenu mnHelpMenu = new JMenu("Help");
		menuBar.add(mnHelpMenu);

		// Add listener to each menu item
		// File Menu
		mntmOpenMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				// Handle open button action.
				int returnVal = fileChooser.showOpenDialog(mainWindow);

				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					// Reset things to normal
					txtFrameWidth.setText("0");
					txtFrameHeight.setText("0");
					txtXOffset.setText("0");
					txtYOffset.setText("0");
					txtAnimationLength.setText("0");
					fpsSlider.setValue(10);
					zoomSlider.setValue(1);

					path = fileChooser.getSelectedFile().toString();
					try
					{
						loadedImage = ImageIO.read(new File(path));
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
					picLabel.setIcon(new ImageIcon(loadedImage));
					spriteSheetViewerPanel.revalidate();
					animationCanvas.image = loadedImage;
				}
				else
				{
				}
			}
		});
		mntmExitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});

		setJMenuBar(menuBar);
	}

	private void initSpriteSheetViewerPanel()
	{
		spriteSheetViewerPanel = new JPanel();
		spriteSheetViewerPanel.setBackground(bgColor);
		spriteSheetViewerPanel.setRequestFocusEnabled(false);
		spriteSheetViewerPanel.setFocusable(false);
		spriteSheetViewerPanel.setLayout(new GridBagLayout()); // used to center the JLabel inside
																// the JPanel
		spriteSheetViewerPanel.setBorder(new CompoundBorder(new MatteBorder(2, 2, 2, 0, outsideBorderColor),
				new LineBorder(insideBorderColor, 2)));
		GridBagConstraints gbc_spriteSheetViewerPanel = new GridBagConstraints();
		gbc_spriteSheetViewerPanel.fill = GridBagConstraints.BOTH;
		gbc_spriteSheetViewerPanel.gridx = 0;
		gbc_spriteSheetViewerPanel.gridy = 0;
		gbc_spriteSheetViewerPanel.gridheight = GridBagConstraints.REMAINDER;

		contentPane.add(spriteSheetViewerPanel, gbc_spriteSheetViewerPanel);

		picLabel = new JLabel();
		picLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
		spriteSheetViewerPanel.add(picLabel);
	}

	private void initAnimationPanel()
	{
		animationPanel = new JPanel();
		animationPanel.setBackground(bgColor);
		animationPanel.setRequestFocusEnabled(false);
		animationPanel.setFocusable(false);
		animationPanel.setLayout(new GridBagLayout());
		animationPanel.setBorder(new CompoundBorder(new MatteBorder(2, 2, 0, 2, outsideBorderColor), new LineBorder(
				insideBorderColor, 2)));
		GridBagConstraints gbc_animationPanel = new GridBagConstraints();
		gbc_animationPanel.fill = GridBagConstraints.BOTH;
		gbc_animationPanel.gridx = 1;
		gbc_animationPanel.gridy = 0;
		contentPane.add(animationPanel, gbc_animationPanel);

		animationCanvas = new AnimationPanel();
		animationCanvas.setBackground(bgColor);
		animationCanvas.setFocusable(false);
		animationCanvas.setBorder(new EmptyBorder(10, 10, 10, 10));
		animationPanel.add(animationCanvas);
		// animationPanel.add(canvas);
	}

	private void initAnimationSettingsPanel()
	{
		final int lblWidth = 150;
		final int lblHeight = 14;
		final int lblWidthTitle = 130;
		final int lblHeightTitle = 25;
		final int borderWidth = 2;
		final int txtFieldWidth = 40;
		final int txtFieldHeight = 16;
		final int sliderWidth = 250;
		final int sliderHeight = 40;
		final int yPadding = 6;

		JPanel animationSettingsPanel = new JPanel();
		animationSettingsPanel.setForeground(textColor);
		animationSettingsPanel.setBackground(bgColor);
		animationSettingsPanel.setBorder(new CompoundBorder(new LineBorder(outsideBorderColor, borderWidth),
				new LineBorder(insideBorderColor, borderWidth)));
		animationSettingsPanel.setLayout(null);
		GridBagConstraints gbc_animationSettingsPanel = new GridBagConstraints();
		gbc_animationSettingsPanel.insets = new Insets(0, 0, 0, 0);
		gbc_animationSettingsPanel.fill = GridBagConstraints.BOTH;
		gbc_animationSettingsPanel.gridx = 1;
		gbc_animationSettingsPanel.gridy = 1;
		contentPane.add(animationSettingsPanel, gbc_animationSettingsPanel);

		JLabel lblAnimationSettings = new JLabel("Animation settings");
		lblAnimationSettings.setLocation(4, 5);
		lblAnimationSettings.setSize(new Dimension(lblWidthTitle, lblHeightTitle));
		lblAnimationSettings.setMinimumSize(new Dimension(lblWidthTitle, lblHeightTitle));
		lblAnimationSettings.setMaximumSize(new Dimension(lblWidthTitle, lblHeightTitle));
		lblAnimationSettings.setPreferredSize(new Dimension(lblWidthTitle, lblHeightTitle));
		lblAnimationSettings.setHorizontalAlignment(SwingConstants.CENTER);
		lblAnimationSettings.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblAnimationSettings.setFocusable(false);
		lblAnimationSettings.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblAnimationSettings.setForeground(titleColor);
		lblAnimationSettings.setBackground(bgColor);
		animationSettingsPanel.add(lblAnimationSettings);

		JLabel lblFrameWidth = new JLabel("Frame Width (in pixels)");
		lblFrameWidth.setMinimumSize(new Dimension(lblWidth, lblHeight));
		lblFrameWidth.setMaximumSize(new Dimension(lblWidth, lblHeight));
		lblFrameWidth.setPreferredSize(new Dimension(lblWidth, lblHeight));
		lblFrameWidth.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblFrameWidth.setRequestFocusEnabled(false);
		lblFrameWidth.setVerifyInputWhenFocusTarget(false);
		lblFrameWidth.setFocusable(false);
		lblFrameWidth.setFocusTraversalKeysEnabled(false);
		lblFrameWidth.setForeground(textColor);
		lblFrameWidth.setBackground(bgColor);
		lblFrameWidth.setBounds(25, 30, lblWidth, lblHeight);
		animationSettingsPanel.add(lblFrameWidth);

		txtFrameWidth = new JTextField();
		lblFrameWidth.setLabelFor(txtFrameWidth);
		txtFrameWidth.setMinimumSize(new Dimension(txtFieldWidth, txtFieldHeight));
		txtFrameWidth.setMaximumSize(new Dimension(txtFieldWidth, txtFieldHeight));
		txtFrameWidth.setPreferredSize(new Dimension(txtFieldWidth, txtFieldHeight));
		txtFrameWidth.setColumns(10);
		txtFrameWidth.setBackground(bgColorTxtBox);
		txtFrameWidth.setForeground(textColor);
		txtFrameWidth.setText("0");
		txtFrameWidth.setBounds(180, lblFrameWidth.getY() - 1, txtFieldWidth, txtFieldHeight);
		animationSettingsPanel.add(txtFrameWidth);

		JLabel lblFrameHeight = new JLabel("Frame Height (in pixels)");
		lblFrameHeight.setMinimumSize(new Dimension(lblWidth, lblHeight));
		lblFrameHeight.setMaximumSize(new Dimension(lblWidth, lblHeight));
		lblFrameHeight.setPreferredSize(new Dimension(lblWidth, lblHeight));
		lblFrameHeight.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblFrameHeight.setRequestFocusEnabled(false);
		lblFrameHeight.setVerifyInputWhenFocusTarget(false);
		lblFrameHeight.setFocusable(false);
		lblFrameHeight.setFocusTraversalKeysEnabled(false);
		lblFrameHeight.setForeground(textColor);
		lblFrameHeight.setBackground(bgColor);
		lblFrameHeight.setBounds(lblFrameWidth.getX(), (int) lblFrameWidth.getBounds().getMaxY() + yPadding, lblWidth,
				lblHeight);
		animationSettingsPanel.add(lblFrameHeight);

		txtFrameHeight = new JTextField();
		lblFrameHeight.setLabelFor(txtFrameHeight);
		txtFrameHeight.setMinimumSize(new Dimension(txtFieldWidth, txtFieldHeight));
		txtFrameHeight.setMaximumSize(new Dimension(txtFieldWidth, txtFieldHeight));
		txtFrameHeight.setPreferredSize(new Dimension(txtFieldWidth, txtFieldHeight));
		txtFrameHeight.setColumns(10);
		txtFrameHeight.setBackground(bgColorTxtBox);
		txtFrameHeight.setForeground(textColor);
		txtFrameHeight.setText("0");
		txtFrameHeight.setBounds(txtFrameWidth.getX(), lblFrameHeight.getY() - 1, txtFieldWidth, txtFieldHeight);
		animationSettingsPanel.add(txtFrameHeight);

		JLabel lblXOffset = new JLabel("X Offset (in sprites)");
		lblXOffset.setMinimumSize(new Dimension(lblWidth, lblHeight));
		lblXOffset.setMaximumSize(new Dimension(lblWidth, lblHeight));
		lblXOffset.setPreferredSize(new Dimension(lblWidth, lblHeight));
		lblXOffset.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblXOffset.setRequestFocusEnabled(false);
		lblXOffset.setVerifyInputWhenFocusTarget(false);
		lblXOffset.setFocusable(false);
		lblXOffset.setFocusTraversalKeysEnabled(false);
		lblXOffset.setForeground(textColor);
		lblXOffset.setBackground(bgColor);
		lblXOffset.setBounds(lblFrameHeight.getX(), (int) lblFrameHeight.getBounds().getMaxY() + yPadding, lblWidth,
				lblHeight);
		animationSettingsPanel.add(lblXOffset);

		txtXOffset = new JTextField();
		lblXOffset.setLabelFor(txtXOffset);
		txtXOffset.setMinimumSize(new Dimension(txtFieldWidth, txtFieldHeight));
		txtXOffset.setMaximumSize(new Dimension(txtFieldWidth, txtFieldHeight));
		txtXOffset.setPreferredSize(new Dimension(txtFieldWidth, txtFieldHeight));
		txtXOffset.setColumns(10);
		txtXOffset.setBackground(bgColorTxtBox);
		txtXOffset.setForeground(textColor);
		txtXOffset.setText("0");
		txtXOffset.setBounds(txtFrameWidth.getX(), lblXOffset.getY() - 1, txtFieldWidth, txtFieldHeight);
		animationSettingsPanel.add(txtXOffset);

		JLabel lblYOffset = new JLabel("Y Offset (in sprites)");
		lblYOffset.setMinimumSize(new Dimension(lblWidth, lblHeight));
		lblYOffset.setMaximumSize(new Dimension(lblWidth, lblHeight));
		lblYOffset.setPreferredSize(new Dimension(lblWidth, lblHeight));
		lblYOffset.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblYOffset.setRequestFocusEnabled(false);
		lblYOffset.setVerifyInputWhenFocusTarget(false);
		lblYOffset.setFocusable(false);
		lblYOffset.setFocusTraversalKeysEnabled(false);
		lblYOffset.setForeground(textColor);
		lblYOffset.setBackground(bgColor);
		lblYOffset.setBounds(lblXOffset.getX(), (int) lblXOffset.getBounds().getMaxY() + yPadding, lblWidth, lblHeight);
		animationSettingsPanel.add(lblYOffset);

		txtYOffset = new JTextField();
		lblYOffset.setLabelFor(txtYOffset);
		txtYOffset.setMinimumSize(new Dimension(txtFieldWidth, txtFieldHeight));
		txtYOffset.setMaximumSize(new Dimension(txtFieldWidth, txtFieldHeight));
		txtYOffset.setPreferredSize(new Dimension(txtFieldWidth, txtFieldHeight));
		txtYOffset.setColumns(10);
		txtYOffset.setBackground(bgColorTxtBox);
		txtYOffset.setForeground(textColor);
		txtYOffset.setText("0");
		txtYOffset.setBounds(txtFrameWidth.getX(), lblYOffset.getY() - 1, txtFieldWidth, txtFieldHeight);
		animationSettingsPanel.add(txtYOffset);

		JLabel lblAnimationLength = new JLabel("Animation Length (in sprites)");
		lblAnimationLength.setMinimumSize(new Dimension(lblWidth, lblHeight));
		lblAnimationLength.setMaximumSize(new Dimension(lblWidth, lblHeight));
		lblAnimationLength.setPreferredSize(new Dimension(lblWidth, lblHeight));
		lblAnimationLength.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblAnimationLength.setRequestFocusEnabled(false);
		lblAnimationLength.setVerifyInputWhenFocusTarget(false);
		lblAnimationLength.setFocusable(false);
		lblAnimationLength.setFocusTraversalKeysEnabled(false);
		lblAnimationLength.setForeground(textColor);
		lblAnimationLength.setBackground(bgColor);
		lblAnimationLength.setBounds(lblYOffset.getX(), (int) lblYOffset.getBounds().getMaxY() + yPadding, lblWidth,
				lblHeight);
		animationSettingsPanel.add(lblAnimationLength);

		txtAnimationLength = new JTextField();
		lblAnimationLength.setLabelFor(txtAnimationLength);
		txtAnimationLength.setMinimumSize(new Dimension(txtFieldWidth, txtFieldHeight));
		txtAnimationLength.setMaximumSize(new Dimension(txtFieldWidth, txtFieldHeight));
		txtAnimationLength.setPreferredSize(new Dimension(txtFieldWidth, txtFieldHeight));
		txtAnimationLength.setColumns(10);
		txtAnimationLength.setBackground(bgColorTxtBox);
		txtAnimationLength.setForeground(textColor);
		txtAnimationLength.setText("0");
		txtAnimationLength
				.setBounds(txtFrameWidth.getX(), lblAnimationLength.getY() - 1, txtFieldWidth, txtFieldHeight);
		animationSettingsPanel.add(txtAnimationLength);

		JLabel lblAnimationOrientation = new JLabel("Animation Orientation");
		lblAnimationOrientation.setMinimumSize(new Dimension(lblWidth, lblHeight));
		lblAnimationOrientation.setMaximumSize(new Dimension(lblWidth, lblHeight));
		lblAnimationOrientation.setPreferredSize(new Dimension(lblWidth, lblHeight));
		lblAnimationOrientation.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblAnimationOrientation.setRequestFocusEnabled(false);
		lblAnimationOrientation.setVerifyInputWhenFocusTarget(false);
		lblAnimationOrientation.setFocusable(false);
		lblAnimationOrientation.setFocusTraversalKeysEnabled(false);
		lblAnimationOrientation.setForeground(textColor);
		lblAnimationOrientation.setBackground(bgColor);
		lblAnimationOrientation.setBounds(lblAnimationLength.getX(), (int) lblAnimationLength.getBounds().getMaxY()
				+ yPadding + 10, lblWidth, lblHeight);
		animationSettingsPanel.add(lblAnimationOrientation);

		JRadioButton rdbtnVertical = new JRadioButton("Vertical");
		rdbtnVertical.setSelected(true);
		rdbtnVertical.setForeground(textColor);
		rdbtnVertical.setBackground(bgColor);
		rdbtnVertical
				.setBounds(txtAnimationLength.getX() - 20, lblAnimationOrientation.getY() - 1, 100, txtFieldHeight);
		animationSettingsPanel.add(rdbtnVertical);

		JRadioButton rdbtnHorizontal = new JRadioButton("Horizontal");
		rdbtnHorizontal.setForeground(textColor);
		rdbtnHorizontal.setBackground(bgColor);
		rdbtnHorizontal.setBounds(rdbtnVertical.getX(), (int) rdbtnVertical.getBounds().getMaxY() + yPadding - 1,
				100, txtFieldHeight);
		animationSettingsPanel.add(rdbtnHorizontal);

		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnHorizontal);
		group.add(rdbtnVertical);

		// add action listeners
		rdbtnVertical.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				xAxisAnim = 0;
				yAxisAnim = 1;
			}
		});

		rdbtnHorizontal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				xAxisAnim = 1;
				yAxisAnim = 0;
			}
		});

		JLabel lblFramesPerSecond = new JLabel("Frames Per Second");
		lblFramesPerSecond.setMinimumSize(new Dimension(300 - 50, lblHeight));
		lblFramesPerSecond.setMaximumSize(new Dimension(300 - 50, lblHeight));
		lblFramesPerSecond.setPreferredSize(new Dimension(300 - 50, lblHeight));
		lblFramesPerSecond.setHorizontalAlignment(SwingConstants.CENTER);
		lblFramesPerSecond.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblFramesPerSecond.setRequestFocusEnabled(false);
		lblFramesPerSecond.setVerifyInputWhenFocusTarget(false);
		lblFramesPerSecond.setFocusable(false);
		lblFramesPerSecond.setFocusTraversalKeysEnabled(false);
		lblFramesPerSecond.setForeground(textColor);
		lblFramesPerSecond.setBackground(bgColor);
		lblFramesPerSecond.setBounds(lblAnimationLength.getX(), (int) rdbtnHorizontal.getBounds().getMaxY()
				+ yPadding + 10, 300 - 50, lblHeight);
		animationSettingsPanel.add(lblFramesPerSecond);

		fpsSlider = new JSlider();
		fpsSlider.setMinimumSize(new Dimension(sliderWidth, sliderHeight));
		fpsSlider.setMaximumSize(new Dimension(sliderWidth, sliderHeight));
		fpsSlider.setPreferredSize(new Dimension(sliderWidth, sliderHeight));
		fpsSlider.setSnapToTicks(true);
		fpsSlider.setPaintLabels(true);
		fpsSlider.setPaintTicks(true);
		fpsSlider.setMajorTickSpacing(10);
		fpsSlider.setMinorTickSpacing(1);
		fpsSlider.setMinimum(0);
		fpsSlider.setMaximum(30);
		fpsSlider.setValue(10);
		fpsSlider.setForeground(textColor);
		fpsSlider.setBackground(bgColor);
		fpsSlider.setBounds(25, (int) lblFramesPerSecond.getBounds().getMaxY() + yPadding, sliderWidth, sliderHeight);
		// fpsSlider.addChangeListener(this);
		animationSettingsPanel.add(fpsSlider);

		// add change listener
		fpsSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (fpsSlider.getValue() > 0)
				{
					animSpeed = 1.0 / fpsSlider.getValue();
				}
				else
				{
					animSpeed = 0;
				}
			}
		});

		JLabel lblZoom = new JLabel("Zoom");
		lblZoom.setMinimumSize(new Dimension(300 - 50, lblHeight));
		lblZoom.setMaximumSize(new Dimension(300 - 50, lblHeight));
		lblZoom.setPreferredSize(new Dimension(300 - 50, lblHeight));
		lblZoom.setHorizontalAlignment(SwingConstants.CENTER);
		lblZoom.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblZoom.setRequestFocusEnabled(false);
		lblZoom.setVerifyInputWhenFocusTarget(false);
		lblZoom.setFocusable(false);
		lblZoom.setFocusTraversalKeysEnabled(false);
		lblZoom.setForeground(textColor);
		lblZoom.setBackground(bgColor);
		lblZoom.setBounds(fpsSlider.getX(), (int) fpsSlider.getBounds().getMaxY() + yPadding + 10, 300 - 50, lblHeight);
		animationSettingsPanel.add(lblZoom);

		zoomSlider = new JSlider();
		zoomSlider.setMinimumSize(new Dimension(sliderWidth, sliderHeight));
		zoomSlider.setMaximumSize(new Dimension(sliderWidth, sliderHeight));
		zoomSlider.setPreferredSize(new Dimension(sliderWidth, sliderHeight));
		zoomSlider.setSnapToTicks(true);
		zoomSlider.setPaintLabels(true);
		zoomSlider.setPaintTicks(true);
		zoomSlider.setMajorTickSpacing(1);
		zoomSlider.setMinimum(1);
		zoomSlider.setMaximum(10);
		zoomSlider.setValue(1);
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		JLabel label = new JLabel("x1");
		label.setForeground(textColor);
		labelTable.put(1, label);
		label = new JLabel("x2");
		label.setForeground(textColor);
		labelTable.put(2, label);
		label = new JLabel("x3");
		label.setForeground(textColor);
		labelTable.put(3, label);
		label = new JLabel("x4");
		label.setForeground(textColor);
		labelTable.put(4, label);
		label = new JLabel("x5");
		label.setForeground(textColor);
		labelTable.put(5, label);
		label = new JLabel("x6");
		label.setForeground(textColor);
		labelTable.put(6, label);
		label = new JLabel("x7");
		label.setForeground(textColor);
		labelTable.put(7, label);
		label = new JLabel("x8");
		label.setForeground(textColor);
		labelTable.put(8, label);
		label = new JLabel("x9");
		label.setForeground(textColor);
		labelTable.put(9, label);
		label = new JLabel("x10");
		label.setForeground(textColor);
		labelTable.put(10, label);
		zoomSlider.setLabelTable(labelTable);
		zoomSlider.setForeground(textColor);
		zoomSlider.setBackground(bgColor);
		zoomSlider.setBounds(25, (int) lblZoom.getBounds().getMaxY() + yPadding, sliderWidth, sliderHeight);
		// zoomSlider.addChangeListener(this);
		animationSettingsPanel.add(zoomSlider);

		// add change listener
		zoomSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e)
			{
				zoom = zoomSlider.getValue();
			}
		});
	}

	public synchronized void start()
	{
		running = true;
		Thread thread = new Thread(this, "main");
		thread.start();
	}

	public void run()
	{
		running = true;

		long lastTime = System.nanoTime();
		long currentTime;
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0 / 60.0;
		double delta = 0;
		int frames = 0;
		int updates = 0;
		requestFocus();

		animLastTime = System.currentTimeMillis();
		while (running)
		{
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / ns;
			lastTime = currentTime;
			while (delta >= 1)
			{
				update();
				updates++;
				delta--;
			}
			frames++;
			render();

			if (System.currentTimeMillis() - timer > 1000)
			{
				timer += 1000;
				this.setTitle(title + "  |  " + updates + " ups, " + frames + " fps");
				updates = frames = 0;
			}
		}
		running = false;
	}

	public void update()
	{
		if (time > 7500)
			time = 0;
		time++;
		animCurrentTime = System.currentTimeMillis();
		animDelta += (animCurrentTime - animLastTime) / 1000.0;
		animLastTime = animCurrentTime;
		updateValues();
		updateSprite();

	}

	public void updateValues()
	{
		frameWidth = parseFrameWidth();
		frameHeight = parseFrameHeight();
		animationCanvas.setSize(frameWidth * zoom, frameHeight * zoom);
		animationCanvas.setPreferredSize(animationCanvas.getSize());
		animationCanvas.revalidate();
		animationCanvas.repaint();
		xSpriteOffset = parseXSpriteOffset();
		ySpriteOffset = parseYSpriteOffset();
		animLength = parseAnimLength();
	}

	public void updateSprite()
	{
		if (animSpeed != 0)
		{
			if (animDelta >= animSpeed)
			{
				animPos++;
				animDelta -= animSpeed;
			}
		}
		if (animPos > animLength - 1)
			animPos = 0;
		// animationCanvas.dx1 = animationPanelWidth / 2 - (frameWidth * zoom / 2);
		// animationCanvas.dy1 = animationPanelHeight / 2 - (frameHeight * zoom / 2);
		// animationCanvas.dx2 = animationPanelWidth / 2 + (frameWidth * zoom / 2);
		// animationCanvas.dy2 = animationPanelHeight / 2 + (frameHeight * zoom / 2);
		/*
		 * If xAxisAnim = 1, our sprites for animation are on the same line, so we animate
		 * on the xAxis. Otherwise we animate on the yAxis
		 */
		animationCanvas.sx1 = (animPos * xAxisAnim * frameWidth) + xSpriteOffset;
		animationCanvas.sy1 = (animPos * yAxisAnim * frameHeight) + ySpriteOffset;
		animationCanvas.sx2 = frameWidth + (animPos * xAxisAnim * frameWidth) + xSpriteOffset;
		animationCanvas.sy2 = frameHeight + (animPos * yAxisAnim * frameHeight) + ySpriteOffset;
	}

	private int parseFrameWidth()
	{
		int spSize;
		try
		{
			spSize = Integer.parseInt(txtFrameWidth.getText());
		}
		catch (NumberFormatException e)
		{
			spSize = 0;
		}
		return spSize;
	}

	private int parseFrameHeight()
	{
		int spSize;
		try
		{
			spSize = Integer.parseInt(txtFrameHeight.getText());
		}
		catch (NumberFormatException e)
		{
			spSize = 0;
		}
		return spSize;
	}

	private int parseAnimLength()
	{
		int animLength;
		try
		{
			animLength = Integer.parseInt(txtAnimationLength.getText());
		}
		catch (NumberFormatException e)
		{
			animLength = 0;
		}
		return animLength;
	}

	private int parseXSpriteOffset()
	{
		int xSpriteOffset;
		try
		{
			xSpriteOffset = Integer.parseInt(txtXOffset.getText());
			xSpriteOffset *= parseFrameWidth();
		}
		catch (NumberFormatException e)
		{
			xSpriteOffset = 0;
		}
		return xSpriteOffset;
	}

	private int parseYSpriteOffset()
	{
		int ySpriteOffset;
		try
		{
			ySpriteOffset = Integer.parseInt(txtYOffset.getText());
			ySpriteOffset *= parseFrameHeight();
		}
		catch (NumberFormatException e)
		{
			ySpriteOffset = 0;
		}
		return ySpriteOffset;
	}

	public void render()
	{
		animationCanvas.paintComponent(animationCanvas.getGraphics());
	}

}
