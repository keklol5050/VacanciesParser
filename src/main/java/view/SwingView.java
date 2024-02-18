package view;

import controller.Controller;
import controller.Main;
import vo.Vacancy;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class SwingView extends Thread implements View {
    private final JFrame frame = new JFrame("Парсер вакансий");
    private final JTextField textField = new JTextField();
    private final JButton startButton = new JButton("Старт");
    private final JButton stopButton = new JButton("Стоп");
    private final JEditorPane textArea = new JEditorPane("text/html", "");
    private final String[] items = {"Дата добавления", "Зарплата", "Место работы", "С какого сайта"};
    private final JComboBox<String> comboBox = new JComboBox<>(items);
    private final JScrollPane scrollPane = new JScrollPane(textArea);
    private Controller controller;

    public SwingView() {
        startButton.setEnabled(false);
        comboBox.setEnabled(false);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        stopButton.setEnabled(false);
    }

    private static JMenuBar getjMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Меню");
        JMenuItem menuItem1 = new JMenuItem("Item 1");
        JMenuItem menuItem2 = new JMenuItem("Item 2");
        JMenuItem menuItem3 = new JMenuItem("Item 3");
        JMenuItem menuItem4 = new JMenuItem("Item 4");
        JMenuItem menuItem5 = new JMenuItem("Item 5");
        menu.add(menuItem1);
        menu.add(menuItem2);
        menu.add(menuItem3);
        menu.add(menuItem4);
        menu.add(menuItem5);
        menuBar.add(menu);
        return menuBar;
    }

    @Override
    public void update(List<Vacancy> vacancies) {
        if (!comboBox.isEnabled())
            comboBox.setEnabled(true);

        textField.setEditable(true);
        textField.setFocusable(true);
        startButton.setEnabled(true);
        stopButton.setEnabled(false);

        // Сохраняем текущую позицию прокрутки

        StringBuilder htmlText = new StringBuilder("<html>");

        for (Vacancy vacancy : vacancies) {
            htmlText.append(vacancy.toString()).append("<br>");
        }

        htmlText.append("</html>");
        textArea.setText(htmlText.toString());

        SwingUtilities.invokeLater(() ->
                scrollPane.getVerticalScrollBar().setValue(0)
        );
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("src/main/resources/icon.png"));
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setPlaceholder(textField, "Введите название вакансии..");

        JMenuBar menuBar = getjMenuBar();

        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

        topPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        topPanel.add(textField);
        topPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        topPanel.add(comboBox);
        topPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        topPanel.add(startButton);
        topPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        topPanel.add(stopButton);
        topPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                checkIsFieldEmpty();
            }

            @Override
            public void focusLost(FocusEvent e) {
                checkIsFieldEmpty();
            }
        });

        // Добавляем слушатель изменений текста
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkIsFieldEmpty();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkIsFieldEmpty();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkIsFieldEmpty();
            }
        });
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<String> box = (JComboBox<String>) e.getSource();
                String item = (String) box.getSelectedItem();
                switch (item) {
                    case "Дата добавления":
                        controller.sortDate();
                        break;
                    case "Зарплата":
                        controller.sortSalary();
                        break;
                    case "Место работы":
                        controller.sortCity();
                        break;
                    case "С какого сайта":
                        controller.sortSiteName();
                        break;
                    case null:
                    default:
                        throw new IllegalStateException("Unexpected value: " + item);
                }
            }
        });
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    Main.initProviders(textField.getText());
                    controller.updateModel();
                    controller.parse();
                }).start();
                textArea.setText("");
                textField.setEditable(false);
                textField.setFocusable(false);
                comboBox.setEnabled(false);
                stopButton.setEnabled(true);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    System.out.println("error while waiting for");
                }
                setPlaceholder(textField, "Парсим..");
                startButton.setEnabled(false);
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField.setEditable(true);
                textField.setFocusable(true);
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                textField.setText("");
                Main.stopAll();
                setPlaceholder(textField, "Введите название вакансии..");
            }
        });

        textArea.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (IOException | URISyntaxException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        frame.setJMenuBar(menuBar);
        frame.add(panel);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        startButton.requestFocusInWindow();
    }

    private void setPlaceholder(JTextField textField, String placeholder) {
        textField.setForeground(Color.GRAY);
        textField.setText(placeholder);

        textField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
    }

    private void checkIsFieldEmpty() {
        startButton.setEnabled((!textField.getText().equals("Введите название вакансии..") && !textField.getText().trim().isEmpty()));
    }
}
