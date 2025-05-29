/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitor;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
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
                super.windowOpened(we);
                /*
                задаём параметры - заголовок, размер, тип окна, расположение при
                отображении, значок приложения
                */
                setTitle("Monitor");
                setSize(300, 250);
                setType(Type.NORMAL);
                // задаём иконку для фрейма
                URL url = getClass().getResource("/img/base.png");
                ImageIcon image = new ImageIcon(url);
                setIconImage(image.getImage());
                initComponents();
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        
    }
    
    /**
     * Создаёт и возвращает панель, на которой располагаются элементы пользовательского
     * интерфейса
     * @return созданную панель содержимого
     */
    private JPanel mainPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        textArea = new JTextArea();// текстовая область для вывода информации
        panel.add(textArea, BorderLayout.CENTER);// в центре панели
        panel.add(barPane(), BorderLayout.SOUTH);// внизу панели
        return panel;
    }
    
    /**
     * Создаёт и возвращает панель состояния приложения
     * @return экземпляр класса StatusBarPane
     */
    private StatusBarPane barPane() {
        StatusBarPane statusBarPane = new StatusBarPane();// панель сосотяния
        
        // панель со значком соединения
        statusBarPane.addItemPane("online");
        StatusBarPane.ItemPane onlinePane = statusBarPane.getItemPane("online");
        onlinePane.setIcon(new ImageIcon("src/img/online16.png"));
        onlinePane.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        statusBarPane.addSeparator(15);// разделитель
        
        // панель для вывода количества активных соединений
        statusBarPane.addItemPane("users");
        StatusBarPane.ItemPane usersPane = statusBarPane.getItemPane("users");
        usersPane.setToolTipText("количество активных соединений");
        return statusBarPane;
    }

}
