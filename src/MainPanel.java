import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI.NormalColor;

public class MainPanel extends JPanel {
	private static final String SELECTED_FILE = "SELECTED_FILE";
	private static final String SELECTED_BROWSE_FILE = "SELECTED_BROWSE_FILE";
	private static final String SELECTED_FOLDER_SWIFT = "SELECTED_FOLDER_SWIFT";
	private static final String SELECTED_FOLDER_ANDROID = "SELECTED_FOLDER_ANDROID";
	Preferences prefs = Preferences.userNodeForPackage(this.getClass());

	private String filename = "translate.xlsx";

	private MainFrame frame;
	private JButton selectFolderAndroidButton;
	private JButton selectFolderSwiftButton;
	private JButton toXmlButton;
	private JButton selectFileButton;
	private JButton toCSVAllButton;
	private JButton toCSVNotAllButton;
	private JButton toStringButton;

	private JButton androidOpenInFinderButton;
	private JButton swiftOpenInFinderButton;
	private JButton fileOpenButton;
	private JButton clearButton;

	private JRadioButton selectFolderAndroidRadioButton;
	private JRadioButton selectFolderSwiftRadioButton;

	private JRadioButton selectFileRadioButton;
	private JRadioButton generateFileRadioButton;

	private JTextField folderAndroidLabel;
	private JTextField folderSwiftLabel;
	private JTextField fileLabel;
	private JTextField generateLabel;

	private JTextArea statusArea;

	private File selectedAndroidFolder = new File(prefs.get(SELECTED_FOLDER_ANDROID, ""));
	private File selectedSwiftFolder = new File(prefs.get(SELECTED_FOLDER_SWIFT, ""));

	private File selectedFolder = selectedAndroidFolder;

	private File selectedBrowseFile = new File(prefs.get(SELECTED_BROWSE_FILE, ""));
	private File selectedFile = selectedBrowseFile;

	private File generateFile;
	private Thread generateExcelThread;
	private Thread generateXmlThread;
	private Thread generateStringThread;
	private JComponent line1;
	private JComponent line2;
	private JScrollPane statusAreaScrollPane;

	public MainPanel(MainFrame frame) {
		this.frame = frame;

		initializeViews();

		addViews();

		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		setLayoutConstraint(layout);
		addSelectFolderAndFileListener();
		addRadioButtonListener();
		addGenereteButtonListener();
		addExtraButtonListener();

		selectFolderAndroidRadioButton.setSelected(true);
		selectFileRadioButton.setSelected(true);
		// scalingFont();
		setBackground(new Color(236,240,241));

		handleUI();
		updateGenerateFileLabel();
	}

	private void initializeViews() {
		// TODO Auto-generated method stub
		selectFolderAndroidRadioButton = new JRadioButton();
		selectFolderSwiftRadioButton = new JRadioButton();

		selectFileRadioButton = new JRadioButton();
		generateFileRadioButton = new JRadioButton();

		generateFileRadioButton.setEnabled(false);

		ButtonGroup folderGroup = new ButtonGroup();
		folderGroup.add(selectFolderAndroidRadioButton);
		folderGroup.add(selectFolderSwiftRadioButton);

		ButtonGroup fileGroup = new ButtonGroup();
		fileGroup.add(selectFileRadioButton);
		fileGroup.add(generateFileRadioButton);

		selectFolderAndroidButton = new JButton("Select Folder (Android)");
		selectFolderSwiftButton = new JButton("Select Folder (Swift)");
		toXmlButton = new JButton("To XML (Android)");
		toStringButton = new JButton("To String (Swift)");

		selectFileButton = new JButton("Select File");
		toCSVAllButton = new JButton("To Excel (All)");
		toCSVNotAllButton = new JButton("To Excel (Untranslate Only)");

		swiftOpenInFinderButton = new JButton("Go to Swift Folder");
		androidOpenInFinderButton = new JButton("Go to Android Folder");
		fileOpenButton = new JButton("Open generated file");

		clearButton = new JButton("Clear");

		statusArea = new JTextArea();
		statusArea.setEditable(false);

		folderAndroidLabel = new JTextField(selectedAndroidFolder.getAbsolutePath());
		folderSwiftLabel = new JTextField(selectedSwiftFolder.getAbsolutePath());
		fileLabel = new JTextField(selectedFile.getAbsolutePath());
		generateLabel = new JTextField();

		folderAndroidLabel.setEditable(false);
		folderSwiftLabel.setEditable(false);
		fileLabel.setEditable(false);
		generateLabel.setEditable(false);

		line1 = drawLine();
		line1.setBackground(Color.lightGray);

		line2 = drawLine();
		line2.setBackground(Color.lightGray);

		statusAreaScrollPane = new JScrollPane(statusArea);
		
		selectFolderAndroidButton.setHorizontalAlignment(SwingConstants.LEFT);
		selectFolderSwiftButton.setHorizontalAlignment(SwingConstants.LEFT);
		
		
	}

	private void addViews() {
		// TODO Auto-generated method stub
		add(selectFolderAndroidButton);
		add(selectFolderSwiftButton);
		add(toXmlButton);
		add(selectFileButton);
		add(toCSVAllButton);
		add(folderAndroidLabel);
		add(folderSwiftLabel);
		add(fileLabel);
		add(toStringButton);
		add(toCSVNotAllButton);
		add(swiftOpenInFinderButton);
		add(androidOpenInFinderButton);
		add(fileOpenButton);
		add(line1);
		add(line2);

		add(statusAreaScrollPane);
		add(clearButton);

		add(selectFolderAndroidRadioButton);
		add(selectFolderSwiftRadioButton);

		add(selectFileRadioButton);
		add(generateFileRadioButton);
		add(generateLabel);
	}

	private void addExtraButtonListener() {
		// TODO Auto-generated method stub
		fileOpenButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Desktop desktop = Desktop.getDesktop();
				File dirToOpen = null;
				try {
					dirToOpen = new File(selectedFolder, filename);
					desktop.open(dirToOpen);
				} catch (IllegalArgumentException iae) {
					System.out.println("File Not Found");
					JOptionPane.showMessageDialog(MainPanel.this, "File Not Found");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		swiftOpenInFinderButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Desktop desktop = Desktop.getDesktop();
				File dirToOpen = null;
				try {
					dirToOpen = selectedSwiftFolder;
					desktop.open(dirToOpen);
				} catch (IllegalArgumentException iae) {
					JOptionPane.showMessageDialog(MainPanel.this, "Folder Not Found");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		androidOpenInFinderButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Desktop desktop = Desktop.getDesktop();
				File dirToOpen = null;
				try {
					dirToOpen = selectedAndroidFolder;
					desktop.open(dirToOpen);
				} catch (IllegalArgumentException iae) {
					JOptionPane.showMessageDialog(MainPanel.this, "Folder Not Found");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				statusArea.setText(null);
			}
		});
	}

	private void addGenereteButtonListener() {
		// TODO Auto-generated method stub
		toCSVAllButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				generateExcelFile(true);
			}

		});

		toCSVNotAllButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				generateExcelFile(false);
			}
		});

		toXmlButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				convertExcelTo(selectedAndroidFolder);

			}
		});

		toStringButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				convertExcelTo(selectedSwiftFolder);

			}
		});
	}

	private void addRadioButtonListener() {

		selectFileRadioButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				selectedFile = selectedBrowseFile;
			}
		});

		generateFileRadioButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				selectedFile = generateFile;
			}
		});
		selectFolderAndroidRadioButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				selectedFolder = selectedAndroidFolder;
			}
		});

		selectFolderSwiftRadioButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				selectedFolder = selectedSwiftFolder;

			}
		});
	}

	private void addSelectFolderAndFileListener() {

		selectFileButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectFile();
			}
		});

		selectFolderAndroidButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectAndroidFolder();
			}
		});

		selectFolderSwiftButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectSwiftFolder();
			}
		});

	}

	private void setLayoutConstraint(SpringLayout layout) {
		int pad2 = (int) (3 * frame.scale);
		int pad6 = (int) (4 * frame.scale);
		int pad7 = (int) (7 * frame.scale);
		int pad10 = (int) (10 * frame.scale);

		layout.putConstraint(SpringLayout.WEST, selectFolderAndroidRadioButton, (int) (10 * frame.scale),
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, selectFolderAndroidRadioButton, (int) (11 * frame.scale),
				SpringLayout.NORTH, this);

		layout.putConstraint(SpringLayout.WEST, selectFolderSwiftRadioButton, (int) (10 * frame.scale),
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, selectFolderSwiftRadioButton, (int) (pad7 + frame.scale),
				SpringLayout.SOUTH, selectFolderAndroidButton);

		layout.putConstraint(SpringLayout.WEST, selectFolderAndroidButton, 0, SpringLayout.EAST,
				selectFolderAndroidRadioButton);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, selectFolderAndroidButton, 0, SpringLayout.VERTICAL_CENTER,
				selectFolderAndroidRadioButton);

		layout.putConstraint(SpringLayout.WEST, folderAndroidLabel, pad7, SpringLayout.EAST, selectFolderAndroidButton);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, folderAndroidLabel, 0, SpringLayout.VERTICAL_CENTER,
				selectFolderAndroidButton);
		layout.putConstraint(SpringLayout.EAST, folderAndroidLabel, -pad7, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.WEST, selectFolderSwiftButton, 0, SpringLayout.EAST,
				selectFolderAndroidRadioButton);
		layout.putConstraint(SpringLayout.EAST, selectFolderSwiftButton, 0, SpringLayout.EAST,
				selectFolderAndroidButton);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, selectFolderSwiftButton, 0, SpringLayout.VERTICAL_CENTER,
				selectFolderSwiftRadioButton);

		layout.putConstraint(SpringLayout.WEST, folderSwiftLabel, pad7, SpringLayout.EAST, selectFolderSwiftButton);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, folderSwiftLabel, 0, SpringLayout.VERTICAL_CENTER,
				selectFolderSwiftButton);
		layout.putConstraint(SpringLayout.EAST, folderSwiftLabel, -pad7, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.WEST, toCSVAllButton, 0, SpringLayout.EAST, selectFolderAndroidRadioButton);
		layout.putConstraint(SpringLayout.NORTH, toCSVAllButton, pad7, SpringLayout.SOUTH, selectFolderSwiftButton);

		layout.putConstraint(SpringLayout.WEST, toCSVNotAllButton, 0, SpringLayout.EAST,
				selectFolderAndroidRadioButton);
		layout.putConstraint(SpringLayout.NORTH, toCSVNotAllButton, pad7, SpringLayout.SOUTH, toCSVAllButton);

		layout.putConstraint(SpringLayout.WEST, line1, 0, SpringLayout.EAST, selectFolderAndroidRadioButton);
		layout.putConstraint(SpringLayout.EAST, line1, -pad7, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, line1, pad10, SpringLayout.SOUTH, toCSVNotAllButton);

		layout.putConstraint(SpringLayout.WEST, selectFileRadioButton, (int) (10 * frame.scale), SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.NORTH, selectFileRadioButton, (int) (pad10 + frame.scale), SpringLayout.SOUTH,
				line1);

		layout.putConstraint(SpringLayout.WEST, generateFileRadioButton, (int) (10 * frame.scale), SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.NORTH, generateFileRadioButton, (int) (pad7 + frame.scale),
				SpringLayout.SOUTH, selectFileButton);

		layout.putConstraint(SpringLayout.WEST, selectFileButton, 0, SpringLayout.EAST, selectFolderAndroidRadioButton);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, selectFileButton, 0, SpringLayout.VERTICAL_CENTER,
				selectFileRadioButton);

		layout.putConstraint(SpringLayout.WEST, fileLabel, pad7, SpringLayout.EAST, selectFileButton);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, fileLabel, 0, SpringLayout.VERTICAL_CENTER,
				selectFileButton);
		layout.putConstraint(SpringLayout.EAST, fileLabel, -pad7, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.WEST, generateLabel, 0, SpringLayout.EAST, selectFolderAndroidRadioButton);
		layout.putConstraint(SpringLayout.EAST, generateLabel, -pad7, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, generateLabel, 0, SpringLayout.VERTICAL_CENTER,
				generateFileRadioButton);

		layout.putConstraint(SpringLayout.WEST, toXmlButton, 0, SpringLayout.EAST, selectFolderAndroidRadioButton);
		layout.putConstraint(SpringLayout.NORTH, toXmlButton, pad10, SpringLayout.SOUTH, generateLabel);

		layout.putConstraint(SpringLayout.WEST, toStringButton, 0, SpringLayout.EAST, selectFolderAndroidRadioButton);
		layout.putConstraint(SpringLayout.NORTH, toStringButton, pad7, SpringLayout.SOUTH, toXmlButton);

		layout.putConstraint(SpringLayout.WEST, line2, 0, SpringLayout.EAST, selectFolderAndroidRadioButton);
		layout.putConstraint(SpringLayout.EAST, line2, -pad7, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, line2, pad10, SpringLayout.SOUTH, toStringButton);

		layout.putConstraint(SpringLayout.EAST, clearButton, (int) (-10 * frame.scale), SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, clearButton, pad10, SpringLayout.SOUTH, line2);

		layout.putConstraint(SpringLayout.WEST, androidOpenInFinderButton, 0, SpringLayout.EAST,
				selectFolderAndroidRadioButton);
		layout.putConstraint(SpringLayout.NORTH, androidOpenInFinderButton, pad10, SpringLayout.SOUTH, line2);

		layout.putConstraint(SpringLayout.WEST, swiftOpenInFinderButton, pad7, SpringLayout.EAST,
				androidOpenInFinderButton);
		layout.putConstraint(SpringLayout.NORTH, swiftOpenInFinderButton, pad10, SpringLayout.SOUTH, line2);
		//

		layout.putConstraint(SpringLayout.EAST, fileOpenButton, -pad7, SpringLayout.WEST, clearButton);
		layout.putConstraint(SpringLayout.NORTH, fileOpenButton, pad10, SpringLayout.SOUTH, line2);

		layout.putConstraint(SpringLayout.WEST, statusAreaScrollPane, 0, SpringLayout.EAST,
				selectFolderAndroidRadioButton);
		layout.putConstraint(SpringLayout.EAST, statusAreaScrollPane, (int) (-10 * frame.scale), SpringLayout.EAST,
				this);
		layout.putConstraint(SpringLayout.NORTH, statusAreaScrollPane, pad10, SpringLayout.SOUTH,
				androidOpenInFinderButton);
		layout.putConstraint(SpringLayout.SOUTH, statusAreaScrollPane, (int) (-10 * frame.scale), SpringLayout.SOUTH,
				this);

	}

	protected void updateGenerateFileLabel() {
		// TODO Auto-generated method stub
		generateLabel.setText(
				String.format("Generate File : %s", generateFile == null ? "" : generateFile.getAbsolutePath()));
		// generateLabel.setText(String.format("<html><b>Generate File :
		// </b>%s</html>",
		// generateFile == null ? "" : generateFile.getAbsolutePath()));
		generateFileRadioButton.setEnabled(generateFile != null);
		generateLabel.setEnabled(generateFile != null);
	}

	private void handleUI() {
		// TODO Auto-generated method stub
		// if (!OsUtils.isMac()) {
		// }
		Component[] components = this.getComponents();
		for (Component component : components) {
			if (component instanceof JButton) {
				JButton button = (JButton) component;
				// component.setBackground(new Color(26,188,156));
				changeButtonColor(button, BEButtonUI.NormalColor.normal);
				button.setForeground(Color.black);
//				button.setPreferredSize(new Dimension((int) button.getPreferredSize().getWidth() + 10,
//						(int) (button.getPreferredSize().getHeight() + 10)));
				
				button.setMargin(new Insets(4,8,4,8));


			} else if (component instanceof JRadioButton) {
				component.setBackground(getBackground());
			} else if (component instanceof JTextField) {
				JTextField textField = (JTextField) component;
				textField.setBorder(
						BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.gray, 1, true),
								BorderFactory.createEmptyBorder(2, 4, 2, 4)));
			}
		}

		changeButtonColor(toCSVAllButton, NormalColor.blue);
		changeButtonColor(toCSVNotAllButton, NormalColor.lightBlue);

		changeButtonColor(toXmlButton, NormalColor.blue);
		changeButtonColor(toStringButton, NormalColor.lightBlue);

		changeButtonColor(fileOpenButton, BEButtonUI.NormalColor.green);
		changeButtonColor(clearButton, BEButtonUI.NormalColor.red);
	}

	private void changeButtonColor(JButton button, NormalColor red) {
		// TODO Auto-generated method stub
		button.setForeground(Color.WHITE);
		button.setUI(new BEButtonUI().setNormalColor(red));
	}

	private void generateExcelFile(boolean b) {
		// TODO Auto-generated method stub

		if (generateExcelThread == null) {

			generateExcelThread = new Thread() {
				@Override
				public void run() {
					toCSVAllButton.setEnabled(false);
					toCSVNotAllButton.setEnabled(false);
					append(statusAreaScrollPane, statusArea, "Converting...\n");
					// TODO Auto-generated method stub
					TreeMap<String, TreeMap<String, String[]>> tempAllMap = toMap(selectedFolder);
					TreeMap<String, TreeMap<String, String[]>> allMap = new TreeMap<>();

					boolean isValid = false;
					if (tempAllMap.isEmpty()) {
					} else {

						while (!tempAllMap.isEmpty()) {
							Entry<String, TreeMap<String, String[]>> mapEntry = tempAllMap.pollFirstEntry();
							TreeMap<String, String[]> tempMap = mapEntry.getValue();
							Entry<String, String[]> entry = tempMap.pollFirstEntry();
							TreeMap<String, String[]> map;

							map = new TreeMap<>();
							allMap.put(mapEntry.getKey(), map);
							map.put(entry.getKey(), entry.getValue());
							if (tempMap.isEmpty()) {

							} else {
								isValid = true;
								while (!tempMap.isEmpty()) {

									entry = tempMap.pollFirstEntry();
									System.out.println(entry.getKey());
									if (entry.getValue().length == 1) {
										map.put(entry.getKey(), entry.getValue());

									} else if (entry.getValue() != null
											&& (entry.getValue()[1] == null || ".".equals(entry.getValue()[0])))
										map.put(entry.getKey(), entry.getValue());
									else if (b)
										map.put(entry.getKey(), entry.getValue());

								}

							}
						}

						append(statusAreaScrollPane, statusArea, "Generating Excel...\n");
						generateFile = IO.getInstance().writeAsExcelWithMultiBook(new File(selectedFolder, filename),
								allMap);
						append(statusAreaScrollPane, statusArea,
								"Excel created : " + generateFile.getAbsolutePath() + "\n\n");

						updateGenerateFileLabel();
					}
					if (!isValid)
						append(statusAreaScrollPane, statusArea,
								"Unable to generate file. Please select a valid project folder.\n\n");

					toCSVAllButton.setEnabled(true);
					toCSVNotAllButton.setEnabled(true);
					generateExcelThread = null;

				}
			};
			generateExcelThread.start();
		}

	}

	protected void convertExcelTo(File folder) {
		// TODO Auto-generated method stub
		if (generateStringThread == null) {
			generateStringThread = new Thread() {
				public void run() {
					toXmlButton.setEnabled(false);
					toStringButton.setEnabled(false);
					append(statusAreaScrollPane, statusArea, "Converting...\n");

					TreeMap<String, TreeMap<String, String[]>> allMap = toMap(folder);
					TreeMap<String, TreeMap<String, String[]>> tempAllMap = IO.getInstance()
							.readExcelWithMultiBook(selectedFile);

					boolean isValid = false;
					if (tempAllMap.size() == 0) {
					} else {
						append(statusAreaScrollPane, statusArea, String.format("Creating %d %s file(s)...\n",
								tempAllMap.size(), folder == selectedAndroidFolder ? "XML" : "Strings"));

						while (!tempAllMap.isEmpty()) {
							Entry<String, TreeMap<String, String[]>> mapEntry = tempAllMap.pollFirstEntry();
							TreeMap<String, String[]> tempMap = mapEntry.getValue();
							if (tempMap.size() == 0) {
								// append(statusAreaScrollPane, statusArea,
								// "Unable to convert,
								// Please select a valid file.\n");
							} else {
								isValid = true;
								TreeMap<String, String[]> map;
								if (allMap.containsKey(mapEntry.getKey())) {
									map = allMap.get(mapEntry.getKey());
								} else {
									map = new TreeMap<>();
								}
								while (!tempMap.isEmpty()) {
									Entry<String, String[]> entry = tempMap.pollFirstEntry();
									map.put(entry.getKey(), entry.getValue());
								}

								try {
									append(statusAreaScrollPane, statusArea, String.format(" --> Creating %s.%s...\n",
											mapEntry.getKey(), folder == selectedAndroidFolder ? "xml" : "strings"));
									if (folder == selectedAndroidFolder) {
										IO.getInstance().writeXML(mapEntry.getKey(), folder, map);
									} else
										IO.getInstance().writeString(mapEntry.getKey(), folder, map);

									append(statusAreaScrollPane, statusArea, String.format("  |--> %s.%s created...\n",
											mapEntry.getKey(), folder == selectedAndroidFolder ? "xml" : "strings"));

								} catch (Exception e) {
									append(statusAreaScrollPane, statusArea,
											String.format("  |-->fail to create %s.%s file...\n", mapEntry.getKey(),
													folder == selectedAndroidFolder ? "xml" : "strings"));

									e.printStackTrace();
								}

							}
						}
						// append(statusAreaScrollPane, statusArea, "\n");
					}
					if (!isValid) {
						append(statusAreaScrollPane, statusArea, "Unable to convert, Please select a valid file.\n\n");
					} else
						append(statusAreaScrollPane, statusArea,
								String.format("All %s File created: %s\n\n",
										folder == selectedAndroidFolder ? "XML" : "Strings",
										selectedAndroidFolder.getAbsolutePath()));

					toXmlButton.setEnabled(true);
					toStringButton.setEnabled(true);
					generateStringThread = null;

				}
			};

			generateStringThread.start();
		}
	}

	protected void append(JScrollPane scrollPane, JTextArea textArea, String text) {
		// TODO Auto-generated method stub
		textArea.append(text);

		textArea.setCaretPosition(textArea.getDocument().getLength());
	}

	private JComponent drawLine() {
		// TODO Auto-generated method stub
		return new JComponent() {

			@Override
			public void paint(Graphics g) {
				// TODO Auto-generated method stub
				super.paint(g);
				g.setColor(getBackground());
				g.drawLine(0, 0, getWidth(), 0);
			}
		};
	}

	protected TreeMap<String, TreeMap<String, String[]>> toMap(File folder) {
		File[] files;

		boolean isAndroid = folder == selectedAndroidFolder;
		if (isAndroid) {
			files = folder.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					File file = new File(dir, name);
					return file.isDirectory() && name.toLowerCase().startsWith("values")
							&& file.listFiles(new FilenameFilter() {
								public boolean accept(File dir, String name) {
									System.out.println(name);
									return name.startsWith("string") && name.endsWith(".xml");

								}
							}).length > 0;
				}
			});

		} else {
			files = folder.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					File file = new File(dir, name);
					return file.isDirectory() && name.toLowerCase().endsWith(".lproj")
							&& file.listFiles(new FilenameFilter() {
								public boolean accept(File dir, String name) {
									System.out.println(name);
									return name.toLowerCase().endsWith(".strings");
								}
							}).length > 0;
				}
			});

		}
		if (files == null || files.length == 0) {
			return new TreeMap<>();
		}
		TreeMap<String, TreeMap<String, String[]>> map = new TreeMap<>();
		int i = 0;
		TreeMap<String, File> fileMap = new TreeMap<>();
		for (File file : files) {
			String[] split = file.getName().split(isAndroid ? "-" : "\\.");
			String label = isAndroid ? split.length == 1 ? "Base" : split[1] : split[0];
			fileMap.put(label.equalsIgnoreCase("Base") ? "*Default" : label.toLowerCase(), file);
		}
		i = 0;

		String[] labels = new String[fileMap.size()];
		while (!fileMap.isEmpty()) {
			Entry<String, File> entry = fileMap.pollFirstEntry();
			File file = entry.getValue();
			labels[i] = entry.getKey();
			if (isAndroid) {
				extract(file, map, labels, i);
			} else
				extractString(file, map, labels, i);
			i++;
		}
		return map;
	}

	protected void extract(File folder, TreeMap<String, TreeMap<String, String[]>> allMap, String[] labels, int index) {
		// TODO Auto-generated method stub
		int length = labels.length;
		System.out.println("Extract");
		File[] files = folder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				name = name.toLowerCase();
				return name.startsWith("string") && name.endsWith(".xml");
			}
		});

		if (files.length > 0) {
			System.out.println(files[0].getParentFile().getName());
			for (File file : files) {
				TreeMap<String, String[]> map;
				String key = file.getName().toLowerCase().substring(0, file.getName().length() - 4);
				if (allMap.containsKey(key)) {
					map = allMap.get(key);
				} else {
					map = new TreeMap<>();
					map.put(".", labels);
					allMap.put(key, map);
				}
				List<Pair> list = IO.getInstance().readXML(file);
				for (Pair pair : list) {
					String[] s;
					if (map.containsKey(pair.first)) {
						s = map.get(pair.first);
					} else {
						s = new String[length];
						map.put(pair.first, s);
					}
					s[index] = pair.second;
				}

			}
		}
	}

	protected void extractString(File folder, TreeMap<String, TreeMap<String, String[]>> allMap, String[] labels,
			int index) {
		int length = labels.length;

		File[] files = folder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				System.out.println(name);
				return name.toLowerCase().endsWith(".strings");
			}
		});

		if (files != null && files.length > 0) {
			for (File file : files) {
				TreeMap<String, String[]> map;
				String key = file.getName().substring(0, file.getName().length() - ".strings".length());
				if (allMap.containsKey(key)) {
					map = allMap.get(key);
				} else {
					map = new TreeMap<>();
					map.put(".", labels);
					allMap.put(key, map);
				}
				List<Pair> list = IO.getInstance().readString(file);
				for (Pair pair : list) {
					String[] s;
					if (map.containsKey(pair.first)) {
						s = map.get(pair.first);
					} else {
						s = new String[length];
						map.put(pair.first, s);
					}
					s[index] = pair.second;
				}
			}
		}
	}

	protected void selectAndroidFolder() {
		JFileChooser fileChooser = new JFileChooser(selectedAndroidFolder);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedAndroidFolder = fileChooser.getSelectedFile();
			prefs.put(SELECTED_FOLDER_ANDROID, selectedAndroidFolder.getAbsolutePath());
			folderAndroidLabel.setText(selectedAndroidFolder.getAbsolutePath());

			if (selectFolderAndroidRadioButton.isSelected())
				selectedFolder = selectedAndroidFolder;
		}
	}

	protected void selectSwiftFolder() {
		JFileChooser fileChooser = new JFileChooser(selectedSwiftFolder);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedSwiftFolder = fileChooser.getSelectedFile();
			prefs.put(SELECTED_FOLDER_SWIFT, selectedSwiftFolder.getAbsolutePath());
			folderSwiftLabel.setText(selectedSwiftFolder.getAbsolutePath());

			if (selectFolderSwiftRadioButton.isSelected())
				selectedFolder = selectedSwiftFolder;
		}
	}

	protected void selectFile() {
		JFileChooser fileChooser = new JFileChooser(selectedFile);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("XLS files", "xlsx", "xls");
		fileChooser.setFileFilter(filter);
		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedBrowseFile = fileChooser.getSelectedFile();
			prefs.put(SELECTED_BROWSE_FILE, selectedBrowseFile.getAbsolutePath());
			fileLabel.setText(selectedBrowseFile.getAbsolutePath());

			if (selectFileRadioButton.isSelected())
				selectedFile = selectedBrowseFile;
		}
	}
}
