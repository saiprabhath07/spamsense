import javax.swing.SwingUtilities;
import ui.SpamSenseGUI;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SpamSenseGUI::new);
    }
}