package kr.sadalmelik.mybatis.mapper;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyBatisMapperClient extends JPanel {
    private MyBatisMapper myBatisMapper;

    public MyBatisMapperClient(MyBatisMapper myBatisMapper) {
        this.myBatisMapper = myBatisMapper;
    }

    private JTextField mapperIdField;
    private JTextArea paramJsonTextArea;

    private JButton executeButton;

    public void run() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel mapperIdPanel = new JPanel (new FlowLayout(FlowLayout.LEFT));
        JLabel mapperIdLabel = new JLabel("매퍼ID : ");
        mapperIdField = new JTextField(20);
        mapperIdPanel.add(mapperIdLabel);
        mapperIdPanel.add(mapperIdField);

        mainPanel.add(mapperIdPanel);
        JPanel paramJsonPanel = new JPanel ();
        paramJsonPanel.setBorder(new TitledBorder(new EtchedBorder(), "parameterJson"));

        // create the middle panel components
        paramJsonTextArea = new JTextArea ( 16, 58 );
        JScrollPane scroll = new JScrollPane ( paramJsonTextArea );
        scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );

        //Add Textarea in to middle panel
        paramJsonPanel.add(scroll);
        mainPanel.add(paramJsonPanel);

        JPanel buttonPanel = new JPanel ();
        executeButton = new JButton("실행");
        executeButton.addActionListener(new ExecuteButtonListener());
        buttonPanel.add(executeButton);

        mainPanel.add(buttonPanel);

        JFrame frame = new JFrame ();
        frame.add(mainPanel);
        frame.pack ();
        frame.setLocationRelativeTo(null);
        frame.setVisible ( true );
    }

    class ExecuteButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println(mapperIdField.getText());
            System.out.println(paramJsonTextArea.getText());

            myBatisMapper.bind(mapperIdField.getText(), paramJsonTextArea.getText());
        }
    }



}