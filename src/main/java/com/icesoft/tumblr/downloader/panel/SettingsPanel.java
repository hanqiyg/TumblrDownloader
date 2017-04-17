package com.icesoft.tumblr.downloader.panel;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.configure.Settings;
import com.icesoft.tumblr.downloader.dialog.ProxyDialog;
import com.icesoft.tumblr.downloader.service.TumblrServices;
import com.icesoft.tumblr.settings.TumblrToken;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;

public class SettingsPanel extends JPanel {
	private static final long serialVersionUID = 4286659292547234267L;
	private static Logger logger = Logger.getLogger(SettingsPanel.class);  
	private JTextField tfCONSUMER_KEY;
	private JTextField tfCONSUMER_SECRET;
	private JTextField tfOAUTH_TOKEN;
	private JTextField tfOAUTH_TOKEN_SECRET;
	private JTextField tfUserInfo;
	
	private TumblrToken token;
	private JTextField tfPath;

	public SettingsPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0, 0, 0};
		gridBagLayout.columnWidths = new int[] {0};
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0};
		setLayout(gridBagLayout);
		
		JPanel plKey = new JPanel();
		GridBagConstraints gbc_plKey = new GridBagConstraints();
		gbc_plKey.insets = new Insets(5, 5, 5, 0);
		gbc_plKey.fill = GridBagConstraints.BOTH;
		gbc_plKey.gridx = 0;
		gbc_plKey.gridy = 0;
		add(plKey, gbc_plKey);
		GridBagLayout gbl_plKey = new GridBagLayout();
		gbl_plKey.rowHeights = new int[] {0};
		gbl_plKey.columnWidths = new int[] {0};
		gbl_plKey.columnWeights = new double[]{1.0, 0.0};
		gbl_plKey.rowWeights = new double[]{0.0, 0.0, 0.0};
		plKey.setLayout(gbl_plKey);
		
		JPanel plKeyInput = new JPanel();
		GridBagConstraints gbc_plKeyInput = new GridBagConstraints();
		gbc_plKeyInput.fill = GridBagConstraints.HORIZONTAL;
		gbc_plKeyInput.anchor = GridBagConstraints.NORTH;
		gbc_plKeyInput.insets = new Insets(0, 0, 5, 0);
		gbc_plKeyInput.gridwidth = 2;
		gbc_plKeyInput.gridx = 0;
		gbc_plKeyInput.gridy = 0;
		plKey.add(plKeyInput, gbc_plKeyInput);
		GridBagLayout gbl_plKeyInput = new GridBagLayout();
		gbl_plKeyInput.columnWidths = new int[] {102, 102, 102, 102};
		gbl_plKeyInput.rowHeights = new int[] {34, 34};
		gbl_plKeyInput.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0};
		gbl_plKeyInput.rowWeights = new double[]{0.0, 0.0};
		plKeyInput.setLayout(gbl_plKeyInput);
		
		JLabel lblCONSUMER_KEY = new JLabel("CONSUMER_KEY:");
		GridBagConstraints gbc_lblCONSUMER_KEY = new GridBagConstraints();
		gbc_lblCONSUMER_KEY.insets = new Insets(5, 5, 5, 5);
		gbc_lblCONSUMER_KEY.gridx = 0;
		gbc_lblCONSUMER_KEY.gridy = 0;
		plKeyInput.add(lblCONSUMER_KEY, gbc_lblCONSUMER_KEY);
		
		tfCONSUMER_KEY = new JTextField();
		tfCONSUMER_KEY.setColumns(10);
		GridBagConstraints gbc_tfCONSUMER_KEY = new GridBagConstraints();
		gbc_tfCONSUMER_KEY.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfCONSUMER_KEY.insets = new Insets(5, 5, 5, 5);
		gbc_tfCONSUMER_KEY.gridx = 1;
		gbc_tfCONSUMER_KEY.gridy = 0;
		plKeyInput.add(tfCONSUMER_KEY, gbc_tfCONSUMER_KEY);
		
		JLabel lblCONSUMER_SECRET = new JLabel("CONSUMER_SECRET:");
		GridBagConstraints gbc_lblCONSUMER_SECRET = new GridBagConstraints();
		gbc_lblCONSUMER_SECRET.insets = new Insets(5, 5, 5, 5);
		gbc_lblCONSUMER_SECRET.gridx = 2;
		gbc_lblCONSUMER_SECRET.gridy = 0;
		plKeyInput.add(lblCONSUMER_SECRET, gbc_lblCONSUMER_SECRET);
		
		tfCONSUMER_SECRET = new JTextField();
		tfCONSUMER_SECRET.setColumns(10);
		GridBagConstraints gbc_tfCONSUMER_SECRET = new GridBagConstraints();
		gbc_tfCONSUMER_SECRET.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfCONSUMER_SECRET.insets = new Insets(5, 5, 5, 5);
		gbc_tfCONSUMER_SECRET.gridx = 3;
		gbc_tfCONSUMER_SECRET.gridy = 0;
		plKeyInput.add(tfCONSUMER_SECRET, gbc_tfCONSUMER_SECRET);
		
		JLabel lblOAUTH_TOKEN = new JLabel("OAUTH_TOKEN:");
		GridBagConstraints gbc_lblOAUTH_TOKEN = new GridBagConstraints();
		gbc_lblOAUTH_TOKEN.insets = new Insets(5, 5, 5, 5);
		gbc_lblOAUTH_TOKEN.gridx = 0;
		gbc_lblOAUTH_TOKEN.gridy = 1;
		plKeyInput.add(lblOAUTH_TOKEN, gbc_lblOAUTH_TOKEN);
		
		tfOAUTH_TOKEN = new JTextField();
		tfOAUTH_TOKEN.setColumns(10);
		GridBagConstraints gbc_tfOAUTH_TOKEN = new GridBagConstraints();
		gbc_tfOAUTH_TOKEN.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfOAUTH_TOKEN.insets = new Insets(5, 5, 5, 5);
		gbc_tfOAUTH_TOKEN.gridx = 1;
		gbc_tfOAUTH_TOKEN.gridy = 1;
		plKeyInput.add(tfOAUTH_TOKEN, gbc_tfOAUTH_TOKEN);
		
		JLabel label_3 = new JLabel("OAUTH_TOKEN_SECRET:");
		GridBagConstraints gbc_label_3 = new GridBagConstraints();
		gbc_label_3.insets = new Insets(5, 5, 5, 5);
		gbc_label_3.gridx = 2;
		gbc_label_3.gridy = 1;
		plKeyInput.add(label_3, gbc_label_3);
		
		tfOAUTH_TOKEN_SECRET = new JTextField();
		tfOAUTH_TOKEN_SECRET.setColumns(10);
		GridBagConstraints gbc_tfOAUTH_TOKEN_SECRET = new GridBagConstraints();
		gbc_tfOAUTH_TOKEN_SECRET.insets = new Insets(5, 5, 5, 5);
		gbc_tfOAUTH_TOKEN_SECRET.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfOAUTH_TOKEN_SECRET.gridx = 3;
		gbc_tfOAUTH_TOKEN_SECRET.gridy = 1;
		plKeyInput.add(tfOAUTH_TOKEN_SECRET, gbc_tfOAUTH_TOKEN_SECRET);		
		JPanel plKeyCtrl = new JPanel();
		GridBagConstraints gbc_plKeyCtrl = new GridBagConstraints();
		gbc_plKeyCtrl.gridwidth = 2;
		gbc_plKeyCtrl.fill = GridBagConstraints.HORIZONTAL;
		gbc_plKeyCtrl.anchor = GridBagConstraints.NORTH;
		gbc_plKeyCtrl.insets = new Insets(0, 0, 0, 5);
		gbc_plKeyCtrl.gridx = 0;
		gbc_plKeyCtrl.gridy = 1;
		plKey.add(plKeyCtrl, gbc_plKeyCtrl);
		
		JButton btnTest = new JButton("Test");
		btnTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = TumblrServices.getInstance().testConnect(loadTokenFromInput());
				tfUserInfo.setText(msg);
			}
		});
		plKeyCtrl.add(btnTest);
		
		JButton btnApply = new JButton("Apply");
		btnTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Settings.getInstance().setToken(loadTokenFromInput());
			}
		});
		plKeyCtrl.add(btnApply);
		
		JButton btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadTokenToInput(Settings.getInstance().getToken());
			}
		});
		plKeyCtrl.add(btnLoad);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e)
			{
				Settings.getInstance().saveToken(loadTokenFromInput());
				tfUserInfo.setText("Save Keys: sucess");				
			}
		});
		plKeyCtrl.add(btnSave);
		
		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				resetKeys();
			}
		});
		plKeyCtrl.add(btnClear);
		

		
		JPanel plKeyState = new JPanel();
		GridBagConstraints gbc_plKeyState = new GridBagConstraints();
		gbc_plKeyState.gridwidth = 2;
		gbc_plKeyState.fill = GridBagConstraints.HORIZONTAL;
		gbc_plKeyState.insets = new Insets(5, 5, 5, 5);
		gbc_plKeyState.gridx = 0;
		gbc_plKeyState.gridy = 2;
		plKey.add(plKeyState, gbc_plKeyState);
		GridBagLayout gbl_plKeyState = new GridBagLayout();
		gbl_plKeyState.columnWidths = new int[] {438};
		gbl_plKeyState.rowHeights = new int[] {0};
		gbl_plKeyState.columnWeights = new double[]{1.0};
		gbl_plKeyState.rowWeights = new double[]{0.0};
		plKeyState.setLayout(gbl_plKeyState);
		
		tfUserInfo = new JTextField();
		tfUserInfo.setEditable(false);
		GridBagConstraints gbc_tfUserInfo = new GridBagConstraints();
		gbc_tfUserInfo.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfUserInfo.anchor = GridBagConstraints.NORTH;
		gbc_tfUserInfo.insets = new Insets(5, 5, 5, 5);
		gbc_tfUserInfo.gridx = 0;
		gbc_tfUserInfo.gridy = 0;
		plKeyState.add(tfUserInfo, gbc_tfUserInfo);
		tfUserInfo.setColumns(10);
		
		JPanel plSavepath = new JPanel();
		GridBagConstraints gbc_plSavepath = new GridBagConstraints();
		gbc_plSavepath.insets = new Insets(0, 0, 5, 0);
		gbc_plSavepath.anchor = GridBagConstraints.NORTH;
		gbc_plSavepath.fill = GridBagConstraints.HORIZONTAL;
		gbc_plSavepath.gridx = 0;
		gbc_plSavepath.gridy = 1;
		add(plSavepath, gbc_plSavepath);
		GridBagLayout gbl_plSavepath = new GridBagLayout();
		gbl_plSavepath.columnWeights = new double[]{1.0};
		gbl_plSavepath.rowWeights = new double[]{0.0, 1.0};
		plSavepath.setLayout(gbl_plSavepath);
		
		JPanel plPathSelect = new JPanel();
		GridBagConstraints gbc_plPathSelect = new GridBagConstraints();
		gbc_plPathSelect.anchor = GridBagConstraints.NORTH;
		gbc_plPathSelect.fill = GridBagConstraints.HORIZONTAL;
		gbc_plPathSelect.insets = new Insets(5, 5, 5, 0);
		gbc_plPathSelect.gridx = 0;
		gbc_plPathSelect.gridy = 0;
		plSavepath.add(plPathSelect, gbc_plPathSelect);
		GridBagLayout gbl_plPathSelect = new GridBagLayout();
		gbl_plPathSelect.columnWidths = new int[] {0, 0};
		gbl_plPathSelect.rowHeights = new int[] {0};
		gbl_plPathSelect.columnWeights = new double[]{1.0, 0.0};
		gbl_plPathSelect.rowWeights = new double[]{0.0};
		plPathSelect.setLayout(gbl_plPathSelect);
		
		tfPath = new JTextField();
		if(Settings.getInstance().getSaveLocation() != null){
			String path = Settings.getInstance().getSaveLocation();
			File file = new File(path);
			tfPath.setText(file.getAbsolutePath());
		}
		tfPath.setEditable(true);
		GridBagConstraints gbc_tfPath = new GridBagConstraints();
		gbc_tfPath.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfPath.insets = new Insets(5, 5, 5, 5);
		gbc_tfPath.gridx = 0;
		gbc_tfPath.gridy = 0;
		plPathSelect.add(tfPath, gbc_tfPath);
		tfPath.setColumns(10);
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		JButton btnPath = new JButton("Choose Folder");
		btnPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i = jFileChooser.showOpenDialog(null);
				if(i == JFileChooser.APPROVE_OPTION){
					String path = jFileChooser.getSelectedFile().getAbsolutePath();
					tfPath.setText(path);
				}
			}
		});
		GridBagConstraints gbc_btnPath = new GridBagConstraints();
		gbc_btnPath.anchor = GridBagConstraints.EAST;
		gbc_btnPath.insets = new Insets(5, 5, 5, 5);
		gbc_btnPath.gridx = 1;
		gbc_btnPath.gridy = 0;
		plPathSelect.add(btnPath, gbc_btnPath);
		
		JPanel plPathCtrl = new JPanel();
		GridBagConstraints gbc_plPathCtrl = new GridBagConstraints();
		gbc_plPathCtrl.fill = GridBagConstraints.BOTH;
		gbc_plPathCtrl.gridx = 0;
		gbc_plPathCtrl.gridy = 1;
		plSavepath.add(plPathCtrl, gbc_plPathCtrl);
		
		JButton btnPathApply = new JButton("Apply");
		btnPathApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(tfPath.getText() != null && !tfPath.getText().trim().equals(""))
				{
					File file = new File(tfPath.getText().trim());
					if(file.exists()){
						if(file.isDirectory()){
							Settings.getInstance().setSaveLocation(file.getAbsolutePath());
						}
					}else{
						file.mkdirs();			
						Settings.getInstance().setSaveLocation(file.getAbsolutePath());
					}
					
				}
			}
		});
		plPathCtrl.add(btnPathApply);
		
		JButton btnPathLoad = new JButton("Load");
		btnPathLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Settings.getInstance().getSaveLocation() != null)
				{
					tfPath.setText(Settings.getInstance().getSaveLocation());
				}
			}
		});
		plPathCtrl.add(btnPathLoad);
		
		JButton btnPathSave = new JButton("Save");
		btnPathSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Settings.getInstance().saveLocation();
			}
		});
		plPathCtrl.add(btnPathSave);
		
		JButton btnProxySettings = new JButton("Proxy Settings");
		btnProxySettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProxyDialog pd = new ProxyDialog();
				int width = (SettingsPanel.this.getWidth() / 2) < pd.getMinimumSize().width
						?pd.getMinimumSize().width
						:SettingsPanel.this.getWidth() / 2;
				int height = (SettingsPanel.this.getHeight() / 2) < pd.getMinimumSize().height
						?pd.getMinimumSize().height
						:SettingsPanel.this.getHeight() / 2 ;
				int x = SettingsPanel.this.getX() + width / 2;
				int y = SettingsPanel.this.getY() + height / 2;
				pd.setBounds(x, y, width, height);
				pd.setModal(true);
				pd.setVisible(true);
			}
		});
		GridBagConstraints gbc_btnProxySettings = new GridBagConstraints();
		gbc_btnProxySettings.gridx = 0;
		gbc_btnProxySettings.gridy = 2;
		add(btnProxySettings, gbc_btnProxySettings);

		
		loadTokenToInput(Settings.getInstance().getToken());
	}
	public TumblrToken loadTokenFromInput()
	{
		token = new TumblrToken
				(
					tfCONSUMER_KEY.getText(),
					tfCONSUMER_SECRET.getText(),
					tfOAUTH_TOKEN.getText(),
					tfOAUTH_TOKEN_SECRET.getText()
				);
		return token;
	}
	public void loadTokenToInput(TumblrToken token)
	{
		tfCONSUMER_KEY.setText(token.getConsumer_key());
		tfCONSUMER_SECRET.setText(token.getConsumer_secret());
		tfOAUTH_TOKEN.setText(token.getOauth_token());
		tfOAUTH_TOKEN_SECRET.setText(token.getOauth_token_secret());
	}
	public void resetKeys()
	{
		tfCONSUMER_KEY.setText("");
		tfCONSUMER_SECRET.setText("");
		tfOAUTH_TOKEN.setText("");
		tfOAUTH_TOKEN_SECRET.setText("");
	}
}
