package safe.gui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import logic.TripleDES;

/**
 *
 * @author Андрей
 */
public class MainFrame extends javax.swing.JFrame {

    /**
     * Creates new form MainFrame
     */
    private final String rootDerectory = System.getProperty("user.home") + "//AppData//safe"; 
    private String[] files;
    
    public MainFrame() {
        initComponents();
        this.setTitle("Электронный сейф 1.0");
        this.addHandler();
        this.setResizable(false);
        this.listDirectory(rootDerectory);      
    }
    
    
    private void listDirectory(String directory) 
    {
        File dir = new File(directory);
        if (!dir.isDirectory()) {
            try {
                dir.mkdirs();
                
            } catch (Exception ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        files = dir.list();
        java.util.Arrays.sort(files);
        
        fileList.removeAll();
        for (String s : files) {
            System.out.println(s);
        }
        fileList.setListData(files);
    }
    
    private void addHandler() {
        delButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if(fileList.getSelectedValue() == null) {
                    return;
                }
                File file = new File(rootDerectory, (String)fileList.getSelectedValue());
                file.setExecutable(true);
                System.out.println(file.canWrite());
                file.delete();
                listDirectory(rootDerectory);
                
            }
            
            
        });
        keyGenButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser savefile = new JFileChooser(FileSystemView.getFileSystemView());
                savefile.setDialogTitle("Сохраните ключ");
                savefile.setPreferredSize(new Dimension(500, 500));
                savefile.setFileFilter(new FileNameExtensionFilter(".key", "key"));
                int res = savefile.showDialog(MainFrame.this, "Сохранить ключ");
                if(res == JFileChooser.APPROVE_OPTION) {
                    TripleDES trip = new TripleDES("-g", savefile.getSelectedFile().getAbsolutePath());
                    JOptionPane.showMessageDialog(rootPane, "Ключ создан!", "Оповещение", JOptionPane.INFORMATION_MESSAGE);
                }
                else {
                    return;
                }
                
            }
        });
        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser addfile = new JFileChooser();
                addfile.setPreferredSize(new Dimension(500, 500));
                if(addfile.showDialog(MainFrame.this, "Выберите файл") == JFileChooser.APPROVE_OPTION) {
                    JFileChooser openkey = new JFileChooser();
                    openkey.setPreferredSize(new Dimension(500, 500));
                    openkey.setDialogTitle("Выберите ключ");
                    openkey.setFileFilter(new FileNameExtensionFilter(".key", "key"));
                    JOptionPane.showMessageDialog(rootPane, "Выберите сгенерированный ключ для кодирования файла", "Оповещение", JOptionPane.INFORMATION_MESSAGE);
                    if (openkey.showDialog(MainFrame.this, "Выбрать ключ") == JFileChooser.APPROVE_OPTION) {
                        File outfile = new File(rootDerectory, addfile.getSelectedFile().getName());
                         
                        try {
                            TripleDES tr = tr = new TripleDES(new FileInputStream(addfile.getSelectedFile()), new FileOutputStream(outfile), "-e", openkey.getSelectedFile().getAbsolutePath());
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        listDirectory(rootDerectory);
                        JOptionPane.showMessageDialog(rootPane, "Файл добавлен в хранилище!", "Оповещение", JOptionPane.INFORMATION_MESSAGE);

                    }
                    else {
                        return;
                    }
                }
                else {
                    return;
                }
            }
        });
        extractButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(fileList.getSelectedValue() == null) {
                    return;
                }
                
                JFileChooser sendfile = new JFileChooser();
                sendfile.setDialogTitle("Сохраните файл");
                sendfile.setPreferredSize(new Dimension(500, 500));
                sendfile.setMultiSelectionEnabled(false);
                sendfile.setSelectedFile(new File((String)fileList.getSelectedValue()));
                
                if(sendfile.showDialog(MainFrame.this, "Сохранить файл") == JFileChooser.APPROVE_OPTION) {
                    JFileChooser openkey = new JFileChooser();
                    openkey.setDialogTitle("Выберите ключ");
                    openkey.setPreferredSize(new Dimension(500, 500));
                    openkey.setFileFilter(new FileNameExtensionFilter(".key", "key"));
                    JOptionPane.showMessageDialog(rootPane, "Выберите ключ для раскодирования файла", "Оповещение", JOptionPane.INFORMATION_MESSAGE);
                    if (openkey.showDialog(MainFrame.this, "Выбрать файл") == JFileChooser.APPROVE_OPTION) {
                        File outfile = new File(sendfile.getSelectedFile().getAbsolutePath());
                        File inputfile = new File(rootDerectory, (String)fileList.getSelectedValue());
                        try {
                            TripleDES triple = new TripleDES(new FileInputStream(inputfile),new FileOutputStream(outfile), "-d", openkey.getSelectedFile().getAbsolutePath());
                            JOptionPane.showMessageDialog(rootPane, "Файл раскодирован!", "Оповещение", JOptionPane.INFORMATION_MESSAGE);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else {
                        return;
                    }
                }
                else {
                    return;
                }
                
            }
        });
    }
    
    
    
    
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        eastPanel = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        extractButton = new javax.swing.JButton();
        delButton = new javax.swing.JButton();
        keyGenButton = new javax.swing.JButton();
        westPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        fileList = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        addButton.setText("Добавить");

        extractButton.setText("Извлечь");

        delButton.setText("Удалить");

        keyGenButton.setText("Генерировать ключ");
        keyGenButton.setActionCommand("genKeyButton");
        keyGenButton.setAutoscrolls(true);

        javax.swing.GroupLayout eastPanelLayout = new javax.swing.GroupLayout(eastPanel);
        eastPanel.setLayout(eastPanelLayout);
        eastPanelLayout.setHorizontalGroup(
            eastPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(eastPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(eastPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(delButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(extractButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(eastPanelLayout.createSequentialGroup()
                        .addComponent(keyGenButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(addButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        eastPanelLayout.setVerticalGroup(
            eastPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(eastPanelLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(addButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(extractButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(delButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(keyGenButton)
                .addGap(27, 27, 27))
        );

        keyGenButton.getAccessibleContext().setAccessibleName("keyGenButton");
        keyGenButton.getAccessibleContext().setAccessibleDescription("keyGenButton");

        fileList.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jScrollPane1.setViewportView(fileList);

        javax.swing.GroupLayout westPanelLayout = new javax.swing.GroupLayout(westPanel);
        westPanel.setLayout(westPanelLayout);
        westPanelLayout.setHorizontalGroup(
            westPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
        );
        westPanelLayout.setVerticalGroup(
            westPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, westPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 486, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(westPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addComponent(eastPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(eastPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(westPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        eastPanel.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton delButton;
    private javax.swing.JPanel eastPanel;
    private javax.swing.JButton extractButton;
    private javax.swing.JList fileList;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton keyGenButton;
    private javax.swing.JPanel westPanel;
    // End of variables declaration//GEN-END:variables
}
