import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JTextField;

public class WeatherAppGui extends JFrame {
    public WeatherAppGui() {
        super("Weather App");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // setting size of gui
        setSize(450, 650);

        // to load gui in center
        setLocationRelativeTo(null);

        setLayout(null);
        // prevent any resize
        setResizable(false);

        addGuiComponents();
    }

    private void addGuiComponents() {
        JTextField searchTextField = new JTextField();
        searchTextField.setBounds(15, 15, 351, 45);

        // changing font styles and size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));
        add(searchTextField);
    }

    // set the location and size of the component

}
