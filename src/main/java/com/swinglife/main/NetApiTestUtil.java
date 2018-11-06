package com.swinglife.main;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NetApiTestUtil {


    public static void main(String args[]) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());


        JFrame jFrame = new JFrame("API测试工具--1.0  by Swinglife");
        jFrame.setLayout(new FlowLayout());
        jFrame.setSize(800,1000);
        JPanel jPanel = new JPanel();
        jPanel.setPreferredSize(new Dimension(800, 80));
        jPanel.setLayout(new FlowLayout());
        JLabel urlLabel = new JLabel("URL:");
        jPanel.add(urlLabel);

        JTextField urlTextField = new JTextField(53);
        urlTextField.setText("http://");
        jPanel.add(urlTextField);

        JComboBox comboBox=new JComboBox();
        comboBox.addItem("GET");
        comboBox.addItem("POST");
        comboBox.addItem("DELETE");
        comboBox.addItem("PUT");
        jPanel.add(comboBox);

        JLabel threadLabel = new JLabel("线程:");
        jPanel.add(threadLabel);

        JTextField threadTextField = new JTextField(10);
        threadTextField.setText("1");
        jPanel.add(threadTextField);


        JLabel numLabel = new JLabel("次数:");
        jPanel.add(numLabel);

        JTextField numTextField = new JTextField(10);
        numTextField.setText("1");
        jPanel.add(numTextField);


        JPanel paramPanel = new JPanel();
        paramPanel.setLayout(new FlowLayout());
        paramPanel.setPreferredSize(new Dimension(800, 50));
        JComboBox paramTypeComboBox=new JComboBox();
        paramTypeComboBox.addItem("RAW");
        paramTypeComboBox.addItem("DATA-FROM");
        JButton addButton = new JButton("添加参数");
        JButton addHeadButton = new JButton("添加请求头");
        JButton actionButton = new JButton("发送请求");
        JButton restButton = new JButton("重置");

        paramPanel.add(paramTypeComboBox);
        paramPanel.add(addButton);
        paramPanel.add(addHeadButton);
        paramPanel.add(actionButton);
        paramPanel.add(restButton);

        List<ParamTextFiled> list = new ArrayList<>();
        JTextArea jTextArea = new JTextArea(10,50);;
        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(0, 187, 591, 98);

        scrollPane_1.setViewportView(jTextArea);


        JTextArea jTextArea2 = new JTextArea(30,60);;
        JScrollPane scrollPane_2 = new JScrollPane();
        scrollPane_2.setBounds(0, 187, 591, 98);
        scrollPane_2.setViewportView(jTextArea2);

        addButton.addActionListener(e -> {
            String paramType = (String) paramTypeComboBox.getSelectedItem();

            if(paramType.equals("DATA-FROM")) {
                JPanel paramPanelData = new JPanel();
                JLabel paramNameLabel = new JLabel("参数名称:");
                paramPanelData.add(paramNameLabel);

                JTextField paramNameTextField = new JTextField(10);
                paramPanelData.add(paramNameTextField);

                JLabel paramValueLabel = new JLabel("参数数据:");
                paramPanelData.add(paramValueLabel);

                JTextField paramValueTextField = new JTextField(35);
                paramPanelData.add(paramValueTextField);

                ParamTextFiled paramTextFiled = new ParamTextFiled();
                paramTextFiled.setjTextFieldName(paramNameTextField);
                paramTextFiled.setjTextFieldValue(paramValueTextField);
                list.add(paramTextFiled);
                jFrame.add(paramPanelData);
            }
            if(paramType.equals("RAW")){
                JPanel paramPanelData = new JPanel();
                paramPanelData.add(scrollPane_1);
                jFrame.add(paramPanelData);
            }
            jFrame.revalidate();
        });

        restButton.addActionListener(e->{
            jFrame.remove(scrollPane_2);
            jFrame.repaint();
            jFrame.validate();
            jFrame.revalidate();

        });

        actionButton.addActionListener(e -> {

            String method = (String) comboBox.getSelectedItem();
            String url = urlTextField.getText();
            String threadCount = threadTextField.getText();
            String number = numTextField.getText();
            StringBuffer stringBuffer = new StringBuffer();
            if(method.equals("POST")){
                String paramType = (String) paramTypeComboBox.getSelectedItem();
                    if(paramType.equals("RAW")){
                        String data = jTextArea.getText();
                        JSONObject jsonObject = new JSONObject(data);
                        for(int i = 0;i<Integer.valueOf(threadCount);i++){
                            System.out.println("创建线程");
                            String finalUrl1 = url;
                            Thread thread = new Thread(() -> {
                                for(int j = 0;j<Integer.valueOf(number);j++){
                                    jTextArea2.append("请求-> " +finalUrl1+"\n");
                                    jTextArea2.append("参数-> " +jsonObject.toString()+"\n");
                                    HTTPClientUtils httpClientUtils = new HTTPClientUtils();
                                    String result =  httpClientUtils.httpPost(finalUrl1,jsonObject);
                                    jTextArea2.append("返回-> " +result+"\n");
                                }
                            });
                            thread.start();
                        }
                    }
            }
            //GET请求
            if(method.equals("GET")){
                url = url+"?i=0";
                for(ParamTextFiled paramTextFiled : list){
                    url = url+"&"+paramTextFiled.getjTextFieldName().getText()+"="+paramTextFiled.getjTextFieldValue().getText();
                }

                for(int i = 0;i<Integer.valueOf(threadCount);i++){
                    System.out.println("创建线程");
                    String finalUrl = url;
                    Thread thread = new Thread(() -> {
                        System.out.println("请求次数:"+number);
                        for(int j = 0;j<Integer.valueOf(number);j++){
                            jTextArea2.append("请求-> " +finalUrl+"\n");
                            System.out.println("请求...");
                            HTTPClientUtils httpClientUtils = new HTTPClientUtils();
                            String result =  httpClientUtils.httpGet(finalUrl);
                            jTextArea2.append("返回-> " +result+"\n");
                        }
                    });
                    thread.start();
                }

            }

            jTextArea2.setText("");
            jFrame.remove(scrollPane_2);
            jFrame.add(scrollPane_2);
            jFrame.revalidate();

        });


        jFrame.add(jPanel);
        jFrame.add(paramPanel);




        jFrame.setVisible(true);
    }




}


class ParamTextFiled{
    JTextField jTextFieldName;
    JTextField jTextFieldValue;

    public JTextField getjTextFieldName() {
        return jTextFieldName;
    }

    public void setjTextFieldName(JTextField jTextFieldName) {
        this.jTextFieldName = jTextFieldName;
    }

    public JTextField getjTextFieldValue() {
        return jTextFieldValue;
    }

    public void setjTextFieldValue(JTextField jTextFieldValue) {
        this.jTextFieldValue = jTextFieldValue;
    }
}
