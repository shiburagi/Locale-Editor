import javax.swing.JPanel;
import javax.swing.JTable;

public class EditorPanel extends JPanel {

	public EditorPanel(EditorFrame frame) {
		// TODO Auto-generated constructor stub
		
		JTable jTable = new JTable(frame.map.size(), frame.map.size());
	}
}
