/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jserver;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Properties;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import monitor.JServerMonitor;

/**
 *
 * @author Sergii.Tushinskyi
 */
public class JServer {

    private static int PORT = 0;// 8192 - номер порта, который будет прослушивать сервер
    private static String localHost;
    // шаблоны сообщений сервера
    private static final String MSG = "Клиент {%d} передал сообщение:\n\r";
    private static final String CLOSE_MESSAGE = "Клиент {%d} закрыл соединение\n\r";
    private static final String OPEN_MESSAGE = "Принят клиент ID={%d}\n\r";
    private static final String CLIENTNAME_MESSAGE = "Клиент ID={%d} name={%s}\n\r";
    private static final LinkedList<ServerSomething> SERVER_LIST = new LinkedList<>();
    private static JServerMonitor monitor;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ServerSocket srvSocket = null;// создаём канал сервера
        try {
            try {
                // читаем свойства подключения
                readConnectProperties();
                // получаем IP адрес локальгого компьютера (если сервер расположен на локальной машине)
                InetAddress ia = InetAddress.getByName(localHost);
                srvSocket = new ServerSocket(PORT, 0, ia);// создаём канал
                System.out.println("Сервер запущен на порту " + srvSocket.getLocalPort());
                monitor = new JServerMonitor();// монитор пользователей и переданных сообщений
                
                addAppToSystemTray(srvSocket);// добавляем иконку в трей
                // запускается бесконечный цикл ожидания подключения клиентов
                while (true) {
                    Socket socket = srvSocket.accept();// создаём канал для принятия данных
                    ServerSomething serverSomething = new ServerSomething(socket);
                    serverSomething.start();
                    SERVER_LIST.add(serverSomething);
                    System.out.printf(OPEN_MESSAGE, 
                            SERVER_LIST.get(SERVER_LIST.size() - 1).getId());

                }
            } catch (IOException ex) {
                System.out.println("Исключение: " + ex);
            }


        } finally {
            // в случае ошибки закрываем созданный канал
            try{
                if(srvSocket != null) {
                    srvSocket.close();
                }
            } catch (IOException ex) {
                System.out.println("Исключение: " + ex);
            }
        }
    }
    
    /**
     * Читает свойства соединения из файла свойств
     */
    private static void readConnectProperties() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("server_conn.properties")) {
            props.load(in);
            in.close();// закрываем поток ввода
            localHost = props.getProperty("host", "localhost");
            PORT = Integer.parseInt(props.getProperty("port", "0"));
        } catch (IOException ex) {
            localHost = "localhost";
            PORT = 0;
        }
    }
    
    /**
     * Добавляет значок приложения в системный трей
     */
    private static void addAppToSystemTray(ServerSocket srvSocket) {
        final TrayIcon trayIcon;
        // есть поддержка системного трея
        if(SystemTray.isSupported()) {
            // получаем объект системного трея
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage("/img/online16.png");
            
            // значок для отображения в системном трее
            trayIcon = new TrayIcon(image, "JServer");
            
            // действие для пункта меню
            ActionListener exitAction = (ActionEvent e) -> {
                disconnetAll();
                System.exit(0);
            };
            ActionListener disconnectAction = ((ae) -> {
                disconnetAll();
                trayIcon.displayMessage("JServer",
                        "All Connection Have Been Closed!",
                        TrayIcon.MessageType.INFO);
            });
            
            // создаём всплывающее меню и его пункты
            final JPopupMenu popup = new JPopupMenu();
            JMenuItem exitItem = new JMenuItem("Закрыть сервер");
            exitItem.addActionListener(exitAction);
            exitItem.setBackground(Color.red);
            JMenuItem disconnectItem = new JMenuItem("Закрыть активные соединения");
            disconnectItem.addActionListener(disconnectAction);
            disconnectItem.setBackground(Color.LIGHT_GRAY);
            popup.add(disconnectItem);
            popup.add(exitItem);
            
            // задаём слушатель при двойном щелчке по значку
            ActionListener al = (ActionEvent e) -> {
                monitor.setVisible(true);
                trayIcon.displayMessage("JServer",
                        "Монитор запущен.", TrayIcon.MessageType.INFO);
            };
            
            trayIcon.setImageAutoSize(false);
            trayIcon.addActionListener(al);
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        popup.setLocation(e.getX(), e.getY());
                        popup.setInvoker(popup);
                        popup.setVisible(true);
                    }
                }
                
            });
            
            try {
                tray.add(trayIcon);
                trayIcon.displayMessage("JServer", "Сервер запущен на порту " + 
                        srvSocket.getLocalPort() + "\n\rhost " + 
                        srvSocket.getInetAddress().getHostAddress(),
                        TrayIcon.MessageType.INFO);
            } catch (AWTException e) {
                System.err.println("TrayIcon could not be added.");
            }
        } else {
            // нет поддержки системного трея
            System.err.println("The system tray is not supported.");
        }
    }
    
    /**
     * Закрывает все открытые каналы
     */
    private static void disconnetAll() {
        SERVER_LIST.forEach(svr -> {
            svr.forceDownService();// принудительное закрытие канала
        });
    }
    
    /**
     * Класс-поток, отвечающий за создание выделенного канала связи клиенту
     */
    private static class ServerSomething extends Thread {
        private final Socket socket;// канал связи
        private final BufferedReader bufIn;// объект чтения из потока ввода
        private final BufferedWriter bufOut;// объект записи в поток вывода
        
        public ServerSomething(Socket socket) throws IOException {
            this.socket = socket;
            // если потоки ввода/вывода приведут к генерированию исключения, оно
            // пробросится дальше; для потоков задаём кодировку символов
            Charset charset = Charset.forName("Cp1251");
            bufIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), 
                    charset));
            bufOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), 
                    charset));
            
        }

        @Override
        public void run() {
            String word;
            try {
                // первое - имя клиента
                word = bufIn.readLine();
                send("Hello, " + word);// выталкиваем данные и очищаем поток
                this.setName(word);// задаём имя потоку
                System.out.printf(CLIENTNAME_MESSAGE, 
                            SERVER_LIST.get(SERVER_LIST.size() - 1).getId(), this.getName());
                // передаём список подключенных соединений
                sendClientConnections();
                
                try {
                    while (true) {
                        word = bufIn.readLine();
                        System.out.printf(MSG, this.getId());
                        System.out.println(word);
                        if(word.equalsIgnoreCase("quit")) {
                            this.downService();
                            break;
                        }

                        /*
                        проверяем сообщение на индивидуальность - если передаётся
                        код контакта или его имя, то сообщение передаём только ему, иначе
                        передаём в общий чат
                        */
                        sendToContact(word);
                    }
                } catch (NullPointerException ex) {
                    
                }
            } catch (IOException ex) {
                
            }
        }

        /**
         * Отсылает сообщение по каналу
         * @param word сообщение для передачи
         */
        private void send(String word) {
            try {
                bufOut.write(word + "\n");// сообщение заканчиваем символом новой строки
                bufOut.flush();// выталкиваем и очищаем канал
            } catch (IOException ex) {
                
            }
        }

        /**
         * Закрывает канал и прерывает выполнение потока
         */
        private void downService() {
            try {
                if(!socket.isClosed()) {
                    // извещаем других пользователей о закрытии соединения
                    JServer.SERVER_LIST.stream().filter((vr) -> (!vr.equals(this))).map((vr) -> {
                        vr.send("\t" + this.getName() + " {" + this.getId() + "} закрыл соединение\n");
                        return vr;
                    }).forEachOrdered((_item) -> {
                        System.out.printf(CLOSE_MESSAGE, _item.getId());
                    });
                    socket.close();
                    bufIn.close();
                    bufOut.close();
                    this.interrupt();// прерываем поток
                    JServer.SERVER_LIST.remove(this);// удаляем из списка
                }
            } catch (IOException ex) {
                
            }
        }
        
        /**
         * Передаёт список существующих подключений новому клиенту
         */
        private void sendClientConnections() {
            // передаём перечень клиентов, если в сети находистя больше одного
            if(JServer.SERVER_LIST.size() > 1) {
                send("В сети пользователи:");
                JServer.SERVER_LIST.stream().filter((vr) -> (!vr.equals(this))).map((vr) -> {
                    vr.send("Подключился:\t" + this.getName() + " ID=" + this.getId());
                    return vr;
                }).forEachOrdered((vr) -> {
                    send("Name:\t" + vr.getName() + " ID=" + vr.getId());
                });
            }
        }
        
        /**
         * Передаёт сообщение выбранному контакту по его идентификатору
         * @param word сообщение, которое передаётся
         * @return true, если сообщение передано удачно, иначе false
         */
        private void sendToContact(String word) {
            int pos = word.indexOf(":");// получаем первое вхождение символа ":"
            if(pos == - 1) {
                sendMSGEveryone(word);// если символ не найден (сообщение в общий чат)
            }
            String subWord = word.substring(0, pos);// выделили подстроку (код или имя)
            try {
                long id = Long.parseLong(subWord);// код контакта (идентификатор потока)
                if(id == 0) {
                    // если код равен 0 (сообщение в общий чат)
                    sendMSGEveryone(word);
                } else {
                    for (ServerSomething vr : JServer.SERVER_LIST) {
                        if(vr.getId() == id) {
                            // передаём сообщение в эту нить
                            vr.send("\t" + word.substring(pos + 1));
                            break;// завершаем цикл
                        }
                    }
                }
            } catch (NumberFormatException ex) {
                // ошибка может выскочить, если передан код, который не может быть преобразован
                System.out.println("contactName=" + subWord);
                /*
                фильтруем список каналов по заданному имени
                */
                for (ServerSomething vr : JServer.SERVER_LIST) {
                        if(vr.getName().equals(subWord)) {
                            // передаём сообщение в эту нить
                            vr.send("\t" + word.substring(pos + 1));
                            break;// завершаем цикл
                        }
                    }
                
            }
        }
        
        /**
         * Передаёт сообщение в общий чат
         * @param word сообщение для передачи
         */
        private void sendMSGEveryone(String word) {
            JServer.SERVER_LIST.stream().filter((vr) -> (!vr.equals(this))).forEachOrdered((vr) -> {
                vr.send("\t" + word);
            });
        }
        
        /**
         * Принудительно закрывает канал и прерывает выполнение потока без уведомления
         */
        private void forceDownService() {
            try {
                if(!socket.isClosed()) {
                    this.send("quit");// извещаем клиента о закрытии канала
                    socket.close();
                    bufIn.close();
                    bufOut.close();
                    this.interrupt();// прерываем поток
                    JServer.SERVER_LIST.remove(this);// удаляем из списка
                }
            } catch (IOException ex) {
                
            }
        }
    }
}
