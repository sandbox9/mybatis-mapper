package kr.sadalmelik.mybatis.mapper.ui;

import kr.sadalmelik.mybatis.mapper.MyBatisMapper;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyBatisMapperClient extends JPanel {
    private MyBatisMapper myBatisMapper;
    private String watchBasePath;

    public MyBatisMapperClient(MyBatisMapper myBatisMapper) {
        this.myBatisMapper = myBatisMapper;
    }

    private JTextField mapperIdField;
    private JTextArea paramJsonTextArea;
    private JTextArea consoleTextArea;

    private JButton executeButton;

    public void run() {
        JPanel mainPanel = getMainPanel();

        mainPanel.add(getMapperIdPanel());
        mainPanel.add(getParameterPanel());
        mainPanel.add(getButtonPanel());
        mainPanel.add(getConsolePanel());

        JFrame frame = new JFrame();
        frame.add(mainPanel);
        setMainFrameAttribute(frame);
    }

    private Component getConsolePanel() {
        JPanel paramJsonPanel = new JPanel();
        consoleTextArea = new JTextArea(22, 58);
        consoleTextArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(consoleTextArea);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        paramJsonPanel.add(scroll);

        return paramJsonPanel;
    }

    private void setMainFrameAttribute(JFrame frame) {
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private JPanel getMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        return mainPanel;
    }

    private JPanel getButtonPanel() {
        JPanel buttonPanel = new JPanel();
        executeButton = new JButton("실행");
        executeButton.addActionListener(new ExecuteButtonListener());
        buttonPanel.add(executeButton);
        return buttonPanel;
    }

    private JPanel getParameterPanel() {
        // create the middle panel components
        JPanel paramJsonPanel = new JPanel();
        paramJsonPanel.setBorder(new TitledBorder(new EtchedBorder(), "parameterJson"));
        paramJsonTextArea = new JTextArea(16, 40);
        JScrollPane scroll = new JScrollPane(paramJsonTextArea);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //Add Textarea in to middle panel
        paramJsonPanel.add(scroll);
        return paramJsonPanel;
    }

    private JPanel getMapperIdPanel() {
        JPanel mapperIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel mapperIdLabel = new JLabel("매퍼ID : ");
        mapperIdField = new JTextField(20);
        mapperIdPanel.add(mapperIdLabel);
        mapperIdPanel.add(mapperIdField);
        return mapperIdPanel;
    }


    class ExecuteButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            consoleTextArea.append(mapperIdField.getText() + "\n");
            consoleTextArea.append(paramJsonTextArea.getText() + "\n");

            String result = myBatisMapper.bind(mapperIdField.getText(), paramJsonTextArea.getText() + "\n");
            consoleTextArea.append(result);
        }
    }



}