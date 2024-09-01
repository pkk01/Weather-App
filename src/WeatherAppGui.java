import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.json.simple.JSONObject;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;

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
        // set the location and size of the component
        searchTextField.setBounds(15, 15, 351, 45);

        // changing font styles and size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));
        add(searchTextField);

        // weather image
        JLabel weatherConditionImage = new JLabel(loadImage("src\\assets\\cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        // temperature text

        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 45);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        // center the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // weather condition description
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        // adding humidity image
        JLabel humidityImage = new JLabel(loadImage("src\\assets\\humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        // humidity text

        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100% </html>"); // we can use html in JLabel
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        // windspeed image

        JLabel windSpeedImage = new JLabel(loadImage("src\\assets\\windspeed.png"));
        windSpeedImage.setBounds(220, 500, 74, 66);
        add(windSpeedImage);

        // windspeed text
        JLabel windSpeedText = new JLabel("<html><b>Windspeed </b> 15km/h </html>");
        windSpeedText.setBounds(310, 500, 85, 55);
        windSpeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windSpeedText);

        // developer watermark
        JLabel pkText = new JLabel("Developed by PK");
        pkText.setBounds(270, 565, 1000, 55);
        pkText.setFont(new Font("Dialog", Font.PLAIN, 20));
        pkText.setForeground(Color.RED);
        add(pkText);

        // search button
        JButton searchButton = new JButton(loadImage("src\\assets\\search.png"));

        // change cursor to a hand when hovering over the button

        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = searchTextField.getText();

                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    return;
                }

                weatherData = WeatherApp.getWeatherData(userInput);

                if (weatherData == null) {
                    System.out.println("Error: Weather data is not available.");
                    weatherConditionDesc.setText("Weather data unavailable");
                    temperatureText.setText("N/A");
                    humidityText.setText("<html><b>Humidity</b> N/A</html>");
                    windSpeedText.setText("<html><b>Windspeed</b> N/A</html>");
                    return;
                }

                String weatherCondition = (String) weatherData.get("weather_condition");

                switch (weatherCondition) {
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src\\assets\\clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src\\assets\\cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src\\assets\\rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src\\assets\\snow.png"));
                        break;
                    default:
                        weatherConditionImage.setIcon(loadImage("src\\assets\\cloudy.png"));
                }

                String temperature = weatherData.get("temperature").toString();
                temperatureText.setText(temperature + " C");

                weatherConditionDesc.setText(weatherCondition);

                String humidity = weatherData.get("humidity").toString();
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                String windspeed = weatherData.get("windspeed").toString();
                windSpeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");
            }
        });

        add(searchButton);

    }

    private ImageIcon loadImage(String resourcePath) {
        try {
            BufferedImage image = ImageIO.read(new File(resourcePath));

            return new ImageIcon(image);
        } catch (Exception e) {

            e.printStackTrace();
        }
        System.out.println("Could not find resource");
        return null;
    }

}
