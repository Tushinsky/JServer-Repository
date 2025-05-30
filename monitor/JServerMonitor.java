/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

/**
 *
 * @author Sergii.Tushinskyi
 */
public class JServerMonitor extends JFrame{

    private JTextArea textArea;

    public JServerMonitor() throws HeadlessException {
        WindowListener listener = new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent we) {
                setTitle("Monitor");
//                setSize(new Dimension(300, 250));
                // задаём иконку для фрейма
                URL url = getClass().getResource("/img/base.png");
                ImageIcon image = new ImageIcon(url);
                setIconImage(image.getImage());
                initComponents();
                setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                setLocationRelativeTo(null);

            }

            @Override
            public void windowClosing(WindowEvent we) {
                super.windowClosing(we); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void windowStateChanged(WindowEvent we) {
                super.windowStateChanged(we); //To change body of generated methods, choose Tools | Templates.
            }



        };

        addWindowListener(listener);
    }

    /**
     * Инициализация компонентов пользовательского интерфейса
     */
    private void initComponents() {
        this.getContentPane().add(mainPanel(), BorderLayout.CENTER);
//        this.getContentPane().add(barPane(), BorderLayout.SOUTH);
        this.setJMenuBar(menuBar());
        pack();
    }

    /**
     * Создаёт и возвращает панель, на которой располагаются элементы пользовательского
     * интерфейса
     * @return созданную панель содержимого
     */
    private JPanel mainPanel() {
        JPanel panel = new JPanel(new BorderLayout(3, 5));
        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();
        textArea = new JTextArea(25, 50);// текстовая область для вывода информации
        textArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(textArea, BorderLayout.CENTER);// в центре панели
        panel.add(rightPanel, BorderLayout.EAST);
        panel.add(barPane(), BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Создаёт и возвращает панель состояния приложения
     * @return экземпляр класса StatusBarPane
     */
    private StatusBarPane barPane() {
        StatusBarPane statusBarPane = new StatusBarPane();// панель сосотяния

        // панель со значком соединения
        JLabel onlinePane = statusBarPane.addItemPane("online");
        onlinePane.setIcon(new ImageIcon("src/img/online16.png"));
        onlinePane.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        statusBarPane.addSeparator(15);// разделитель

        // панель для вывода количества активных соединений
        JLabel usersPane = statusBarPane.addItemPane("users");
        usersPane.setIcon(new ImageIcon("src/img/users.png"));
        usersPane.setToolTipText("количество активных соединений");
        statusBarPane.setBorder(BorderFactory.createEtchedBorder());
        return statusBarPane;
    }

    private JMenuBar menuBar() {
        JMenuBar menubar = new JMenuBar();
        JMenu connectMenu = new JMenu("Connection");
        JMenuItem closeActiveConnectMenu = new JMenuItem("Закрыть все соединения");
        JMenuItem closeServerMenu = new JMenuItem("Закрыть сервер");
        connectMenu.add(closeActiveConnectMenu);
        connectMenu.add(closeServerMenu);
        menubar.add(connectMenu);
        return menubar;
    }

}
