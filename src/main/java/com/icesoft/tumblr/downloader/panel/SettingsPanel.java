package com.icesoft.tumblr.downloader.panel;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;

import com.icesoft.tumblr.downloader.Settings;
import com.icesoft.tumblr.downloader.service.TumblrServices;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SettingsPanel extends JPanel {
	private static final long serialVersionUID = 4286659292547234267L;
	private JTextField tfCONSUMER_KEY;
	private JTextField tfCONSUMER_SECRET;
	private JTextField tfOAUTH_TOKEN;
	private JTextField tfOAUTH_TOKEN_SECRET;
	private JTextField tfUserInfo;
	
	private TumblrServices 	services;

	public SettingsPanel(Settings settings,TumblrServices services) {
		this.services = services;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0};
		gridBagLayout.rowHeights = new int[] {0};
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0};
		setLayout(gridBagLayout);
		
		JPanel keyPanel = new JPanel();
		GridBagConstraints gbc_keyPanel = new GridBagConstraints();
		gbc_keyPanel.anchor = GridBagConstraints.NORTH;
		gbc_keyPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_keyPanel.insets = new Insets(0, 0, 5, 0);
		gbc_keyPanel.gridx = 0;
		gbc_keyPanel.gridy = 0;
		add(keyPanel, gbc_keyPanel);
		GridBagLayout gbl_keyPanel = new GridBagLayout();
		gbl_keyPanel.columnWidths = new int[] {102, 102, 102, 102};
		gbl_keyPanel.rowHeights = new int[] {34, 34};
		gbl_keyPanel.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0};
		gbl_keyPanel.rowWeights = new double[]{0.0, 0.0};
		keyPanel.setLayout(gbl_keyPanel);
		
		JLabel lblCONSUMER_KEY = new JLabel("CONSUMER_KEY:");
		GridBagConstraints gbc_lblCONSUMER_KEY = new GridBagConstraints();
		gbc_lblCONSUMER_KEY.insets = new Insets(5, 5, 5, 5);
		gbc_lblCONSUMER_KEY.gridx = 0;
		gbc_lblCONSUMER_KEY.gridy = 0;
		keyPanel.add(lblCONSUMER_KEY, gbc_lblCONSUMER_KEY);
		
		tfCONSUMER_KEY = new JTextField();
		tfCONSUMER_KEY.setColumns(10);
		GridBagConstraints gbc_tfCONSUMER_KEY = new GridBagConstraints();
		gbc_tfCONSUMER_KEY.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfCONSUMER_KEY.insets = new Insets(5, 5, 5, 5);
		gbc_tfCONSUMER_KEY.gridx = 1;
		gbc_tfCONSUMER_KEY.gridy = 0;
		keyPanel.add(tfCONSUMER_KEY, gbc_tfCONSUMER_KEY);
		
		JLabel lblCONSUMER_SECRET = new JLabel("CONSUMER_SECRET:");
		GridBagConstraints gbc_lblCONSUMER_SECRET = new GridBagConstraints();
		gbc_lblCONSUMER_SECRET.insets = new Insets(5, 5, 5, 5);
		gbc_lblCONSUMER_SECRET.gridx = 2;
		gbc_lblCONSUMER_SECRET.gridy = 0;
		keyPanel.add(lblCONSUMER_SECRET, gbc_lblCONSUMER_SECRET);
		
		tfCONSUMER_SECRET = new JTextField();
		tfCONSUMER_SECRET.setColumns(10);
		GridBagConstraints gbc_tfCONSUMER_SECRET = new GridBagConstraints();
		gbc_tfCONSUMER_SECRET.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfCONSUMER_SECRET.insets = new Insets(5, 5, 5, 5);
		gbc_tfCONSUMER_SECRET.gridx = 3;
		gbc_tfCONSUMER_SECRET.gridy = 0;
		keyPanel.add(tfCONSUMER_SECRET, gbc_tfCONSUMER_SECRET);
		
		JLabel lblOAUTH_TOKEN = new JLabel("OAUTH_TOKEN:");
		GridBagConstraints gbc_lblOAUTH_TOKEN = new GridBagConstraints();
		gbc_lblOAUTH_TOKEN.insets = new Insets(5, 5, 5, 5);
		gbc_lblOAUTH_TOKEN.gridx = 0;
		gbc_lblOAUTH_TOKEN.gridy = 1;
		keyPanel.add(lblOAUTH_TOKEN, gbc_lblOAUTH_TOKEN);
		
		tfOAUTH_TOKEN = new JTextField();
		tfOAUTH_TOKEN.setColumns(10);
		GridBagConstraints gbc_tfOAUTH_TOKEN = new GridBagConstraints();
		gbc_tfOAUTH_TOKEN.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfOAUTH_TOKEN.insets = new Insets(5, 5, 5, 5);
		gbc_tfOAUTH_TOKEN.gridx = 1;
		gbc_tfOAUTH_TOKEN.gridy = 1;
		keyPanel.add(tfOAUTH_TOKEN, gbc_tfOAUTH_TOKEN);
		
		JLabel label_3 = new JLabel("OAUTH_TOKEN_SECRET:");
		GridBagConstraints gbc_label_3 = new GridBagConstraints();
		gbc_label_3.insets = new Insets(5, 5, 5, 5);
		gbc_label_3.gridx = 2;
		gbc_label_3.gridy = 1;
		keyPanel.add(label_3, gbc_label_3);
		
		tfOAUTH_TOKEN_SECRET = new JTextField();
		tfOAUTH_TOKEN_SECRET.setColumns(10);
		GridBagConstraints gbc_tfOAUTH_TOKEN_SECRET = new GridBagConstraints();
		gbc_tfOAUTH_TOKEN_SECRET.insets = new Insets(5, 5, 5, 5);
		gbc_tfOAUTH_TOKEN_SECRET.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfOAUTH_TOKEN_SECRET.gridx = 3;
		gbc_tfOAUTH_TOKEN_SECRET.gridy = 1;
		keyPanel.add(tfOAUTH_TOKEN_SECRET, gbc_tfOAUTH_TOKEN_SECRET);		
		JPanel controlPanel = new JPanel();
		GridBagConstraints gbc_controlPanel = new GridBagConstraints();
		gbc_controlPanel.anchor = GridBagConstraints.NORTH;
		gbc_controlPanel.insets = new Insets(0, 0, 5, 0);
		gbc_controlPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_controlPanel.gridx = 0;
		gbc_controlPanel.gridy = 1;
		add(controlPanel, gbc_controlPanel);		
		JButton btnTest = new JButton("Test");
		btnTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String consumer_key = tfCONSUMER_KEY.getText();
				String consumer_secret = tfCONSUMER_SECRET.getText();
				String oauth_token = tfOAUTH_TOKEN.getText();
				String oauth_token_secret = tfOAUTH_TOKEN_SECRET.getText();
				String msg = SettingsPanel.this.services.testConnect(consumer_key,consumer_secret,oauth_token,oauth_token_secret);
				tfUserInfo.setText(msg);
			}
		});
		controlPanel.add(btnTest);		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveKeys();
				Settings.saveKeySettings();
				tfUserInfo.setText("Save Keys: sucess");				
			}
		});
		controlPanel.add(btnSave);
		
		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetKeys();
			}
		});
		
		JButton btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Settings.loadKeySettings();
				loadKeys();
			}
		});
		controlPanel.add(btnLoad);
		controlPanel.add(btnReset);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.NORTH;
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 2;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] {0};
		gbl_panel.rowHeights = new int[] {0};
		gbl_panel.columnWeights = new double[]{1.0};
		gbl_panel.rowWeights = new double[]{0.0};
		panel.setLayout(gbl_panel);
		
		tfUserInfo = new JTextField();
		tfUserInfo.setEditable(false);
		GridBagConstraints gbc_tfUserInfo = new GridBagConstraints();
		gbc_tfUserInfo.insets = new Insets(5, 5, 5, 5);
		gbc_tfUserInfo.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfUserInfo.gridx = 0;
		gbc_tfUserInfo.gridy = 0;
		panel.add(tfUserInfo, gbc_tfUserInfo);
		tfUserInfo.setColumns(10);
		loadKeys();
	}
	public void saveKeys(){
		Settings.consumer_key = tfCONSUMER_KEY.getText();
		Settings.consumer_secret = tfCONSUMER_SECRET.getText();
		Settings.oauth_token = tfOAUTH_TOKEN.getText();
		Settings.oauth_token_secret = tfOAUTH_TOKEN_SECRET.getText();
	}
	public void loadKeys(){
		tfCONSUMER_KEY.setText(Settings.consumer_key);
		tfCONSUMER_SECRET.setText(Settings.consumer_secret);
		tfOAUTH_TOKEN.setText(Settings.oauth_token);
		tfOAUTH_TOKEN_SECRET.setText(Settings.oauth_token_secret);
	}
	public void resetKeys(){
		tfCONSUMER_KEY.setText("");
		tfCONSUMER_SECRET.setText("");
		tfOAUTH_TOKEN.setText("");
		tfOAUTH_TOKEN_SECRET.setText("");
	}
}
