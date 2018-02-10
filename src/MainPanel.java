import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainPanel extends JPanel {
	private static final String SELECTED_FILE = "SELECTED_FILE";
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

	private JButton folderOpenInFinder;
	private JButton fileOpenInFinder;
	private JButton fileOpen;
	private JButton clearButton;

	private JRadioButton selectFolderAndroidRadioButton;
	private JRadioButton selectFolderSwiftRadioButton;

	private JLabel folderAndroidLabel;
	private JLabel folderSwiftLabel;
	private JLabel fileLabel;

	private JTextArea statusArea;

	private File selectedAndroidFolder = new File(prefs.get(SELECTED_FOLDER_ANDROID, ""));
	private File selectedSwiftFolder = new File(prefs.get(SELECTED_FOLDER_SWIFT, ""));

	private File selectedFolder = selectedAndroidFolder;

	private File selectedFile = new File(prefs.get(SELECTED_FILE, ""));
	private Thread generateExcelThread;
	private Thread generateXmlThread;
	private Thread generateStringThread;

	public MainPanel(MainFrame frame) {
		this.frame = frame;

		selectFolderAndroidRadioButton = new JRadioButton();
		selectFolderSwiftRadioButton = new JRadioButton();

		ButtonGroup group = new ButtonGroup();
		group.add(selectFolderAndroidRadioButton);
		group.add(selectFolderSwiftRadioButton);

		selectFolderAndroidButton = new JButton("select folder (Android)");
		selectFolderSwiftButton = new JButton("select folder (Swift)");
		toXmlButton = new JButton("To XML (Android)");
		toStringButton = new JButton("To String (Swift)");

		selectFileButton = new JButton("select file");
		toCSVAllButton = new JButton("To Excel (All)");
		toCSVNotAllButton = new JButton("To Excel (Untranslate Only)");

		fileOpenInFinder = new JButton("Go to Generated File Folder");
		folderOpenInFinder = new JButton("Go to XML/String Folder");
		fileOpen = new JButton("Open generated file");

		clearButton = new JButton("Clear");

		statusArea = new JTextArea();
		statusArea.setEditable(false);

		folderAndroidLabel = new JLabel(selectedAndroidFolder.getAbsolutePath());
		folderSwiftLabel = new JLabel(selectedSwiftFolder.getAbsolutePath());
		fileLabel = new JLabel(selectedFile.getAbsolutePath());
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		setBackground(Color.WHITE);

		JComponent jPanel1 = drawLine();
		jPanel1.setBackground(Color.lightGray);

		JComponent jPanel2 = drawLine();
		jPanel2.setBackground(Color.lightGray);

		JScrollPane jScrollPane = new JScrollPane(statusArea);

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
		// add(fileOpenInFinder);
		add(folderOpenInFinder);
		add(fileOpen);
		add(jPanel1);
		add(jPanel2);

		add(jScrollPane);
		add(clearButton);

		add(selectFolderAndroidRadioButton);
		add(selectFolderSwiftRadioButton);

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
		layout.putConstraint(SpringLayout.NORTH, selectFolderAndroidButton, (int) (10 * frame.scale),
				SpringLayout.NORTH, this);

		layout.putConstraint(SpringLayout.WEST, folderAndroidLabel, pad7, SpringLayout.EAST, selectFolderAndroidButton);
		layout.putConstraint(SpringLayout.NORTH, folderAndroidLabel, pad6, SpringLayout.NORTH,
				selectFolderAndroidButton);

		layout.putConstraint(SpringLayout.WEST, selectFolderSwiftButton, 0, SpringLayout.EAST,
				selectFolderAndroidRadioButton);
		layout.putConstraint(SpringLayout.NORTH, selectFolderSwiftButton, pad7, SpringLayout.SOUTH,
				selectFolderAndroidButton);

		layout.putConstraint(SpringLayout.WEST, folderSwiftLabel, pad7, SpringLayout.EAST, selectFolderSwiftButton);
		layout.putConstraint(SpringLayout.NORTH, folderSwiftLabel, pad6, SpringLayout.NORTH, selectFolderSwiftButton);

		layout.putConstraint(SpringLayout.WEST, toCSVAllButton, 0, SpringLayout.EAST, selectFolderAndroidRadioButton);
		layout.putConstraint(SpringLayout.NORTH, toCSVAllButton, pad7, SpringLayout.SOUTH, selectFolderSwiftButton);

		layout.putConstraint(SpringLayout.WEST, toCSVNotAllButton, 0, SpringLayout.EAST,
				selectFolderAndroidRadioButton);
		layout.putConstraint(SpringLayout.NORTH, toCSVNotAllButton, pad7, SpringLayout.SOUTH, toCSVAllButton);

		layout.putConstraint(SpringLayout.WEST, jPanel1, 0, SpringLayout.EAST, selectFolderAndroidRadioButton);
		layout.putConstraint(SpringLayout.EAST, jPanel1, -pad7, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, jPanel1, pad10, SpringLayout.SOUTH, toCSVNotAllButton);

		layout.putConstraint(SpringLayout.WEST, selectFileButton, 0, SpringLayout.EAST, selectFolderAndroidRadioButton);
		layout.putConstraint(SpringLayout.NORTH, selectFileButton, pad10, SpringLayout.SOUTH, jPanel1);

		layout.putConstraint(SpringLayout.WEST, fileLabel, pad7, SpringLayout.EAST, selectFileButton);
		layout.putConstraint(SpringLayout.NORTH, fileLabel, pad6, SpringLayout.NORTH, selectFileButton);

		layout.putConstraint(SpringLayout.WEST, toXmlButton, 0, SpringLayout.EAST, selectFolderAndroidRadioButton);
		layout.putConstraint(SpringLayout.NORTH, toXmlButton, pad7, SpringLayout.SOUTH, selectFileButton);

		layout.putConstraint(SpringLayout.WEST, toStringButton, 0, SpringLayout.EAST, selectFolderAndroidRadioButton);
		layout.putConstraint(SpringLayout.NORTH, toStringButton, pad7, SpringLayout.SOUTH, toXmlButton);

		layout.putConstraint(SpringLayout.WEST, jPanel2, 0, SpringLayout.EAST, selectFolderAndroidRadioButton);
		layout.putConstraint(SpringLayout.EAST, jPanel2, -pad7, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, jPanel2, pad10, SpringLayout.SOUTH, toStringButton);

		layout.putConstraint(SpringLayout.EAST, clearButton, (int) (-10 * frame.scale), SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, clearButton, pad10, SpringLayout.SOUTH, jPanel2);

		layout.putConstraint(SpringLayout.WEST, folderOpenInFinder, 0, SpringLayout.EAST,
				selectFolderAndroidRadioButton);
		layout.putConstraint(SpringLayout.NORTH, folderOpenInFinder, pad10, SpringLayout.SOUTH, jPanel2);

		// layout.putConstraint(SpringLayout.WEST, fileOpenInFinder,
		// (int)(5*frame.scale),
		// SpringLayout.EAST, folderOpenInFinder);
		// layout.putConstraint(SpringLayout.NORTH, fileOpenInFinder,
		// (int)(5*frame.scale),
		// SpringLayout.SOUTH, jPanel2);
		//

		layout.putConstraint(SpringLayout.WEST, fileOpen, pad7, SpringLayout.EAST, folderOpenInFinder);
		layout.putConstraint(SpringLayout.NORTH, fileOpen, pad10, SpringLayout.SOUTH, jPanel2);

		layout.putConstraint(SpringLayout.WEST, jScrollPane, 0, SpringLayout.EAST, selectFolderAndroidRadioButton);
		layout.putConstraint(SpringLayout.EAST, jScrollPane, (int) (-10 * frame.scale), SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, jScrollPane, pad10, SpringLayout.SOUTH, folderOpenInFinder);
		layout.putConstraint(SpringLayout.SOUTH, jScrollPane, (int) (-10 * frame.scale), SpringLayout.SOUTH, this);

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

		selectFileButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectFile();
			}
		});
		toXmlButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (generateXmlThread == null) {
					generateXmlThread = new Thread() {
						public void run() {
							statusArea.append("Converting...\n");
							TreeMap<String, String[]> map = toMap(selectedAndroidFolder);
							TreeMap<String, String[]> tempMap = IO.getInstance().readExcel(selectedFile);

							while (!tempMap.isEmpty()) {
								Entry<String, String[]> entry = tempMap.pollFirstEntry();
								map.put(entry.getKey(), entry.getValue());
							}
							statusArea.append("Generating XML...\n");

							IO.getInstance().writeXML(selectedAndroidFolder, map);
							statusArea.append("XML created: " + selectedAndroidFolder.getAbsolutePath() + "\n");
							generateXmlThread = null;
						};
					};
					generateXmlThread.start();

				}

			}
		});

		toStringButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (generateStringThread == null) {
					generateStringThread = new Thread() {
						public void run() {
							statusArea.append("Converting...\n");

							TreeMap<String, String[]> map = new TreeMap<>();
							TreeMap<String, String[]> tempMap = IO.getInstance().readExcel(selectedFile);

							while (!tempMap.isEmpty()) {
								Entry<String, String[]> entry = tempMap.pollFirstEntry();
								map.put(entry.getKey(), entry.getValue());
							}
							statusArea.append("Generating String File...\n");

							IO.getInstance().writeString(selectedSwiftFolder, map);
							statusArea.append("String File created: " + selectedAndroidFolder.getAbsolutePath() + "\n");

							generateStringThread = null;
						}
					};

					generateStringThread.start();
				}

			}
		});

		fileOpen.addActionListener(new ActionListener() {

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

		fileOpenInFinder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Desktop desktop = Desktop.getDesktop();
				File dirToOpen = null;
				try {
					dirToOpen = selectedAndroidFolder;
					desktop.open(dirToOpen);
				} catch (IllegalArgumentException iae) {
					System.out.println("File Not Found");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		folderOpenInFinder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Desktop desktop = Desktop.getDesktop();
				File dirToOpen = null;
				try {
					dirToOpen = selectedFolder;
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

		selectFolderAndroidRadioButton.setSelected(true);
		// scalingFont();

		setButtonColor();

	}

	private void setButtonColor() {
		// TODO Auto-generated method stub
		Component[] components = this.getComponents();
		for (Component component : components) {
			if (component instanceof JButton) {
				// component.setBackground(new Color(26,188,156));
				// component.setForeground(Color.WHITE);
			} else if (component instanceof JRadioButton) {
				component.setBackground(Color.white);
			}
		}

	}

	private void scalingFont() {
		// TODO Auto-generated method stub
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();

		double scale = width / 1280.0;
		Font tempfont = clearButton.getFont();
		Font font = new Font(tempfont.getName(), tempfont.getStyle(), (int) (scale * 12));

		Component[] components = this.getComponents();
		for (Component component : components) {
			component.setFont(font);
			System.out.println(component.getClass().getSimpleName());
		}

	}

	private void generateExcelFile(boolean b) {
		// TODO Auto-generated method stub

		if (generateExcelThread == null) {

			generateExcelThread = new Thread() {
				@Override
				public void run() {
					statusArea.append("Converting...\n");
					// TODO Auto-generated method stub
					TreeMap<String, String[]> tempMap = toMap(selectedFolder);
					TreeMap<String, String[]> map = new TreeMap<>();
					if (tempMap.isEmpty())
						return;
					Entry<String, String[]> entry = tempMap.pollFirstEntry();
					map.put(entry.getKey(), entry.getValue());

					while (!tempMap.isEmpty()) {

						entry = tempMap.pollFirstEntry();
						System.out.println(entry.getKey());
						if (entry.getValue().length == 1) {
							map.put(entry.getKey(), entry.getValue());

						} else if (entry.getValue()[1] == null || entry.getValue()[0].equals("."))
							map.put(entry.getKey(), entry.getValue());
						else if (b)
							map.put(entry.getKey(), entry.getValue());

					}

					statusArea.append("Generating Excel...\n");

					statusArea.append("Excel created : "
							+ IO.getInstance().writeAsExcel(new File(selectedFolder, filename), map).getAbsolutePath()
							+ "\n");
					generateExcelThread = null;
				}
			};
			generateExcelThread.start();
		}

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

	protected TreeMap<String, String[]> toMap(File folder) {
		if (folder == selectedAndroidFolder) {
			File[] files = folder.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					File stringFile = new File(new File(dir, name), "strings.xml");
					return name.matches("(values)[\\-a-zA-z]*") && stringFile.exists();
				}
			});
			TreeMap<String, String[]> map = new TreeMap<>();
			int i = 0;
			String[] labels = new String[files.length];
			for (File file : files) {
				labels[i++] = file.getName();
			}
			System.out.println(labels);
			Arrays.sort(labels);
			i = 0;
			for (String label : labels) {
				extract(new File(folder, label), map, labels.length, i++);
			}
			map.put(".", labels);
			return map;
		} else {
			File[] files = folder.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					File file = new File(dir, name);
					return file.isDirectory()&&name.toLowerCase().endsWith(".lproj") && file.listFiles(new FilenameFilter() {
						public boolean accept(File dir, String name) {
							System.out.println(name);
							return name.toLowerCase().endsWith(".strings");
						}
					}).length > 0;
				}
			});

			System.out.println("Files : " + files.length);
			TreeMap<String, String[]> map = new TreeMap<>();
			int i = 0;
			String[] labels = new String[files.length];
			for (File file : files) {
				String[] split = file.getName().split("\\.");

				labels[i++] = split[0].equalsIgnoreCase("Base") ? "Default" : split[0].toLowerCase();
			}
			System.out.println(labels);
			Arrays.sort(labels);
			i = 0;
			for (String label : labels) {
				extractString(new File(folder, label), map, labels.length, i++);
			}
			map.put(".", labels);
			return map;
		}
	}

	protected void extract(File file, TreeMap<String, String[]> map, int length, int index) {
		// TODO Auto-generated method stub
		File[] files = file.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.equals("strings.xml");
			}
		});

		if (files.length > 0) {
			List<Pair> list = IO.getInstance().readXML(files[0]);
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

	protected void extractString(File file, TreeMap<String, String[]> map, int length, int index) {
		// TODO Auto-generated method stub
		File[] files = file.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				System.out.println(name);
				return name.toLowerCase().endsWith(".strings");
			}
		});

		if (files != null && files.length > 0) {
			List<Pair> list = IO.getInstance().readXML(files[0]);
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

	protected void selectAndroidFolder() {
		JFileChooser fileChooser = new JFileChooser(selectedAndroidFolder);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedAndroidFolder = fileChooser.getSelectedFile();
			prefs.put(SELECTED_FOLDER_ANDROID, selectedAndroidFolder.getAbsolutePath());
			folderAndroidLabel.setText(selectedAndroidFolder.getAbsolutePath());
		}
	}

	protected void selectSwiftFolder() {
		JFileChooser fileChooser = new JFileChooser(selectedSwiftFolder);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedSwiftFolder = fileChooser.getSelectedFile();
			prefs.put(SELECTED_FOLDER_SWIFT, selectedSwiftFolder.getAbsolutePath());
			folderAndroidLabel.setText(selectedSwiftFolder.getAbsolutePath());
		}
	}

	protected void selectFile() {
		JFileChooser fileChooser = new JFileChooser(selectedFile);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("XLS files", "xlsx", "xls");
		fileChooser.setFileFilter(filter);
		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile();
			prefs.put(SELECTED_FILE, selectedFile.getAbsolutePath());
			fileLabel.setText(selectedFile.getAbsolutePath());
		}
	}
}
