/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitor;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Класс реализует строку состояния для вывода различной информации в созданные
 * панели, аналогично элементу управления StatusBar Visual Basic
 * @author Sergii.Tushinskyi
 */
public class StatusBarPane extends JPanel {

    private final List<ItemPane> itemList;// список, в котором будут храниться созданные элементы на основе меток
    private int index;// индекс элемента
    private Box mainBox;
    
    public StatusBarPane() {
        super();
        super.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        super.add(Box.createHorizontalStrut(2));
        itemList = new ArrayList<>();
        index = itemList.size();// начальный номер элементов в коллекции
//        System.out.println("index=" + index);
    }
    
    /**
     * Добавление нового элемента
     * @param key ключ - идентификатор нового элемента
     */
    public void addItemPane(String key) {
        if(!addItem(key)) 
            // если новый элемент не был добавлен, извещаем пользователя
            JOptionPane.showMessageDialog(null, "Элемент с таким именем уже существует в коллекции!", 
                    "StatusBarPane", JOptionPane.ERROR_MESSAGE);
        else index++;// увеличиваем счётчик элементов
    }
    
    /**
     * Удаление существующего элемента
     * @param index индекс удаляемого элемента
     */
    public void removeItemPane(int index) {
        // проверяем на существование элемента с указанным индексом
        boolean exist = false;
        for(ItemPane ip : itemList) {
            if(ip.getIndex() == index) {
                // элемент с данным порядковым номером существует
                exist = true;// устанавливаем флаг наличия элемента
                break;// выход из цикла
            }
        }
        if(exist == true) {
            removeItem(index);
        }
    }
    
    /**
     * Удаление существующего элемента
     * @param key ключ удаляемого элемента
     */
    public void removeItemPane(String key) {
        if(existItem(key)) {
            removeItem(index);
        }
    }
    
    /**
     * Возвращает выбранную панель по её номеру
     * @param index номер выбранной панели
     * @return выбранная панель
     */
    public ItemPane getItemPane(int index) {
        for(ItemPane ip : itemList) {
            if(ip.getIndex() == index) return ip;
        }
        return null;
    }
    
    /**
     * Возвращает выбранную панель по ключу
     * @param key ключ выбранной панели
     * @return выбранная панель
     */
    public ItemPane getItemPane(String key) {
        for(ItemPane ip : itemList) {
            if(ip.getKey().equals(key)) {
                return ip;
                
            }
        }
        return null;
    }
    
    /**
     * Создаёт и добавляет разделитель заданной ширины
     * @param width ширина разделителя
     */
    public void addSeparator(int width) {
        this.add(Box.createHorizontalStrut(width));
    }
    
    private boolean addItem(String key) {
        // проверяем на существование элемента с таким ключом в коллекции
        boolean exist = existItem(key);
        if(exist == false) {
            ItemPane ip = new ItemPane(index, key);
            this.add(ip);// добавляем панель в контейнер
            exist = itemList.add(ip);// возвращаем результат добавления панели
            
        }
        return exist;
    }
    
    /**
     * Функция проверяет существование элемента в коллекции
     * @param key критерий проверки
     * @return true - в случае удачи, иначе возвращает false
     */
    private boolean existItem(String key) {
        boolean exist = false;
        // проверяем наличие элементов в коллекции
        if(!itemList.isEmpty()){
            for (ItemPane itemList1 : itemList) {
                // проверяем совпадение критерия с ключом
                if(itemList1.getKey().equalsIgnoreCase(key)) {
                    exist = true;
                    index = itemList.indexOf(itemList1);
                    break;
                }
            }
        }
        return exist;
    }
    
    private void removeItem(int index) {
        this.remove(itemList.get(index));// удаляем элемент из контейнера
        itemList.remove(index);// удаляем элемент из коллекции
        this.index--;// уменьшаем индекс
        itemList.forEach((ip) -> {
            ip.setIndex(itemList.indexOf(ip));// переопределяем индексы эллементов
        });
    }
    
    class ItemPane extends JLabel {

        private int index;// индекс элемента в коллекции
        private String key;// идентификатор элемента в коллекции

        public ItemPane(int index, String key) {
            super();
            this.index = index;
            this.key = key;
        }
        
        /**
         * @return the index - индекс элемента в коллекции
         */
        public int getIndex() {
            return index;
        }

        /**
         * @param index the index to set
         */
        protected void setIndex(int index) {
            this.index = index;
        }

        /**
         * @return the key - уникальный ключ элемента
         */
        public String getKey() {
            return key;
        }

        /**
         * @param key the key to set
         */
        protected void setKey(String key) {
            this.key = key;
        }

        
    }
}
