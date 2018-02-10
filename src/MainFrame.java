import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.alee.laf.WebLookAndFeel;

public class MainFrame extends JFrame {
	double scale = 0;

	public MainFrame() {


		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();

		scale = width / 1280.0;

		setDefaultSize((int) (scale * 14));

		Container c = getContentPane();
		c.add(new MainPanel(this));
		// this.pack();
		this.setMinimumSize(new Dimension((int) (800 * scale), (int) (600 * scale)));
		this.pack();
		this.setTitle("Locale Converter v1.0");
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private static void initLookAndFeel() {
		try {
			if (OsUtils.isMac())
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			else
				WebLookAndFeel.install();

		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void setDefaultSize(int size) {

		Set<Object> keySet = UIManager.getLookAndFeelDefaults().keySet();
		Object[] keys = keySet.toArray(new Object[keySet.size()]);

		for (Object key : keys) {

			if (key != null && key.toString().toLowerCase().contains("font")) {

				Font font = UIManager.getDefaults().getFont(key);
				if (font != null) {
					font = font.deriveFont((float) size);
					UIManager.put(key, font);
				}

			}

		}

	}

	public static void main(String[] a) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Install WebLaF as application L&F
				initLookAndFeel();
				new MainFrame().setDefaultCloseOperation(EXIT_ON_CLOSE);

			}
		});
	}
}
