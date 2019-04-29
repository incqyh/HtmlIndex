package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import query.WrongQueryException;

public class View extends JFrame implements DocumentListener {
    private static final long serialVersionUID = 1L;

    Control control;

    JButton button_search;
    JButton button_back;
    JButton button_forward;
    JButton button_go;
    JTextField text_url;
    JTextField text_search;
    JList<String> list_result;
    JList<String> list_history;
    JEditorPane editor_html;

    public View() {
        init();
        this.setSize(1200, 800);
        this.setVisible(true);
    }

    public void init() {
        control = Control.GetInstance();
        initLayout();
        initAction();
    }

    void initLayout() {
        button_search = new JButton("search");
        button_back = new JButton("back");
        button_forward = new JButton("forward");
        button_go = new JButton("go");
        text_url = new JTextField();
        text_search = new JTextField("", 20);

        list_result = new JList<String>();
        ScrollPane sp_result = new ScrollPane();
        sp_result.add(list_result);

        list_history = new JList<String>();
        ScrollPane sp_history = new ScrollPane();
        sp_history.add(list_history);

        editor_html = new JEditorPane();
        ScrollPane sp_html = new ScrollPane();
        sp_html.add(editor_html);

        GridBagLayout grid2 = new GridBagLayout();
        GridBagConstraints s = new GridBagConstraints();
        s.fill = GridBagConstraints.BOTH;
        s.gridwidth = 1;
        s.weightx = 0;
        s.weighty = 0;
        grid2.setConstraints(button_back, s);
        s.gridwidth = 1;
        s.weightx = 0;
        s.weighty = 0;
        grid2.setConstraints(button_forward, s);
        s.gridwidth = 2;
        s.weightx = 1;
        s.weighty = 0;
        grid2.setConstraints(text_url, s);
        s.gridwidth = 0;
        s.weightx = 0;
        s.weighty = 0;
        grid2.setConstraints(button_go, s);
        s.gridwidth = 0;
        s.weightx = 1;
        s.weighty = 1;
        grid2.setConstraints(sp_html, s);

        JPanel panel2 = new JPanel();
        panel2.setLayout(grid2);
        panel2.add(button_back);
        panel2.add(button_forward);
        panel2.add(text_url);
        panel2.add(button_go);
        panel2.add(sp_html);
        panel2.setBorder(BorderFactory.createTitledBorder("browser"));

        GridBagLayout grid11 = new GridBagLayout();
        s.gridwidth = 1;
        s.weightx = 1;
        s.weighty = 0;
        grid11.setConstraints(text_search, s);
        s.gridwidth = 0;
        s.weightx = 0;
        s.weighty = 0;
        grid11.setConstraints(button_search, s);
        s.gridwidth = 0;
        s.weightx = 1;
        s.weighty = 1;
        grid11.setConstraints(sp_result, s);

        JPanel panel11 = new JPanel();
        panel11.setLayout(grid11);
        panel11.add(text_search);
        panel11.add(button_search);
        panel11.add(sp_result);
        panel11.setBorder(BorderFactory.createTitledBorder("search"));

        GridBagLayout grid12 = new GridBagLayout();
        s.gridwidth = 0;
        s.weightx = 1;
        s.weighty = 1;
        grid12.setConstraints(sp_history, s);

        JPanel panel12 = new JPanel();
        panel12.setLayout(grid12);
        panel12.add(sp_history);
        panel12.setBorder(BorderFactory.createTitledBorder("history"));

        GridBagLayout grid1 = new GridBagLayout();
        s.gridwidth = 0;
        s.weightx = 0;
        s.weighty = 1;
        grid1.setConstraints(panel11, s);
        s.gridwidth = 0;
        s.weightx = 0;
        s.weighty = 1;
        grid1.setConstraints(panel12, s);

        JPanel panel1 = new JPanel();
        panel1.setLayout(grid1);
        panel1.add(panel11);
        panel1.add(panel12);

        this.add(panel1);
        this.add(panel2);
        GridBagLayout mainGrid = new GridBagLayout();
        s.gridwidth = 1;
        s.weightx = 1;
        s.weighty = 1;
        mainGrid.setConstraints(panel1, s);
        s.gridwidth = 2;
        s.weightx = 1;
        s.weighty = 1;
        mainGrid.setConstraints(panel2, s);
        this.setLayout(mainGrid);
    }

    void initAction() {
        Document doc = text_search.getDocument();  
        doc.addDocumentListener(this);

        button_search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ArrayList<String> re = control.searchAction(text_search.getText());
                    String[] rearr = (String[]) (re.toArray(new String[re.size()]));
                    list_result.setListData(rearr);
                } catch (WrongQueryException e1) {
                    JOptionPane.showMessageDialog(null, "wrong query!");
                }
            }
        });

        button_back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    text_url.setText(control.backAction());
                    editor_html.setPage(text_url.getText());
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, "wrong url!");
                }
            }
        });

        button_forward.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    text_url.setText(control.forwardAction());
                    editor_html.setPage(text_url.getText());
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, "wrong url!");
                }
            }
        });

        button_go.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    editor_html.setPage(text_url.getText());
                    control.goAction(text_url.getText());

                    ArrayList<String> re = control.getHistory();
                    list_history.setListData((String[]) re.toArray(new String[re.size()]));
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, "wrong url!");
                }
            }
        });

    }

    @Override
    public void insertUpdate(DocumentEvent e) {

        try {
            ArrayList<String> re = control.searchAction(text_search.getText());
            String[] rearr = (String[]) (re.toArray(new String[re.size()]));
            list_result.setListData(rearr);
        } catch (WrongQueryException e1) {
            // JOptionPane.showMessageDialog(null, "wrong query!");
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e) {

        try {
            ArrayList<String> re = control.searchAction(text_search.getText());
            String[] rearr = (String[]) (re.toArray(new String[re.size()]));
            list_result.setListData(rearr);
        } catch (WrongQueryException e1) {
            // JOptionPane.showMessageDialog(null, "wrong query!");
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }
}