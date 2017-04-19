package com.icesoft.tumblr.downloader.dialog;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.icesoft.tumblr.downloader.configure.TumblrToken;
import com.icesoft.tumblr.downloader.service.SettingService;
import com.icesoft.tumblr.downloader.service.TumblrServices;

public class TokenDialog extends JDialog
{
	private static final long serialVersionUID = 7898637757439796777L;
	public static final String TITLE = "Tumblr Token Settings";
	
	private JButton btnSave,btnApply,btnLoad;
	
	private JTextField tfConsumerKey;
	private JTextField tfConsumerSecret;
	private JTextField tfOauthToken;
	private JTextField tfOauthTokenSecret;
	
	public TokenDialog() {
		this.setTitle(TITLE);
		this.setMinimumSize(new Dimension(800,320));
		TitledBorder titled = BorderFactory.createTitledBorder
				(
					null
					,TITLE
					,TitledBorder.CENTER
					,TitledBorder.TOP
				);
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel plPathSettings = new JPanel();
		plPathSettings.setBorder(titled);
		getContentPane().add(plPathSettings);
		GridBagConstraints gbc_plPathSettings = new GridBagConstraints();
		gbc_plPathSettings.fill = GridBagConstraints.BOTH;
		gbc_plPathSettings.insets = new Insets(5, 5, 5, 5);
		gbc_plPathSettings.gridx = 0;
		gbc_plPathSettings.gridy = 0;
				GridBagLayout gbl_plPathSettings = new GridBagLayout();
				gbl_plPathSettings.columnWidths = new int[] {};
				gbl_plPathSettings.columnWeights = new double[]{1.0};
				gbl_plPathSettings.rowWeights = new double[]{1.0, 1.0};
				plPathSettings.setLayout(gbl_plPathSettings);
		
				JPanel plConfigure = new JPanel();
				GridBagConstraints gbc_plConfigure = new GridBagConstraints();
				gbc_plConfigure.insets = new Insets(5, 5, 5, 5);
				gbc_plConfigure.fill = GridBagConstraints.HORIZONTAL;
				gbc_plConfigure.gridy = 0;
				gbc_plConfigure.gridx = 0;

				plPathSettings.add(plConfigure, gbc_plConfigure);
				GridBagLayout gbl_plConfigure = new GridBagLayout();
				gbl_plConfigure.columnWeights = new double[]{0.0, 1.0};
				gbl_plConfigure.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0};
				plConfigure.setLayout(gbl_plConfigure);
				
				JLabel lblConsumerKey = new JLabel("CONSUMER_KEY:");
				GridBagConstraints gbc_lblConsumerKey = new GridBagConstraints();
				gbc_lblConsumerKey.anchor = GridBagConstraints.EAST;
				gbc_lblConsumerKey.insets = new Insets(5, 5, 5, 5);
				gbc_lblConsumerKey.gridx = 0;
				gbc_lblConsumerKey.gridy = 0;
				plConfigure.add(lblConsumerKey, gbc_lblConsumerKey);
				
				JLabel lblOauthToken = new JLabel("OAUTH_TOKEN:");
				GridBagConstraints gbc_lblOauthToken = new GridBagConstraints();
				gbc_lblOauthToken.anchor = GridBagConstraints.EAST;
				gbc_lblOauthToken.insets = new Insets(5, 5, 5, 5);
				gbc_lblOauthToken.gridx = 0;
				gbc_lblOauthToken.gridy = 1;
				plConfigure.add(lblOauthToken, gbc_lblOauthToken);
				
				tfConsumerKey = new JTextField("");
				tfConsumerKey.setColumns(50);
				GridBagConstraints gbc_tfConsumerKey = new GridBagConstraints();
				gbc_tfConsumerKey.insets = new Insets(5, 5, 5, 5);
				gbc_tfConsumerKey.fill = GridBagConstraints.HORIZONTAL;
				gbc_tfConsumerKey.gridx = 1;
				gbc_tfConsumerKey.gridy = 0;
				plConfigure.add(tfConsumerKey, gbc_tfConsumerKey);
				
				tfOauthToken = new JTextField("");
				tfOauthToken.setColumns(50);
				GridBagConstraints gbc_tfOauthToken = new GridBagConstraints();
				gbc_tfOauthToken.insets = new Insets(5, 5, 5, 5);
				gbc_tfOauthToken.fill = GridBagConstraints.HORIZONTAL;
				gbc_tfOauthToken.gridx = 1;
				gbc_tfOauthToken.gridy = 1;
				plConfigure.add(tfOauthToken, gbc_tfOauthToken);
				
				JLabel lblConsumerSecret = new JLabel("CONSUMER_SECRET:");
				GridBagConstraints gbc_lblConsumerSecret = new GridBagConstraints();
				gbc_lblConsumerSecret.anchor = GridBagConstraints.EAST;
				gbc_lblConsumerSecret.insets = new Insets(5, 5, 5, 5);
				gbc_lblConsumerSecret.gridx = 0;
				gbc_lblConsumerSecret.gridy = 2;
				plConfigure.add(lblConsumerSecret, gbc_lblConsumerSecret);
				
				JLabel lblOauthTokenSecret = new JLabel("OAUTH_TOKEN_SECRET:");
				GridBagConstraints gbc_lblOauthTokenSecret = new GridBagConstraints();
				gbc_lblOauthTokenSecret.anchor = GridBagConstraints.EAST;
				gbc_lblOauthTokenSecret.insets = new Insets(5, 5, 5, 5);
				gbc_lblOauthTokenSecret.gridx = 0;
				gbc_lblOauthTokenSecret.gridy = 3;
				plConfigure.add(lblOauthTokenSecret, gbc_lblOauthTokenSecret);
				
				tfConsumerSecret = new JTextField("");
				tfConsumerSecret.setColumns(50);
				GridBagConstraints gbc_tfConsumerSecret = new GridBagConstraints();
				gbc_tfConsumerSecret.insets = new Insets(5, 5, 5, 5);
				gbc_tfConsumerSecret.fill = GridBagConstraints.HORIZONTAL;
				gbc_tfConsumerSecret.gridx = 1;
				gbc_tfConsumerSecret.gridy = 2;
				plConfigure.add(tfConsumerSecret, gbc_tfConsumerSecret);
				
				tfOauthTokenSecret = new JTextField("");
				tfOauthTokenSecret.setColumns(50);
				GridBagConstraints gbc_tfOauthTokenSecret = new GridBagConstraints();
				gbc_tfOauthTokenSecret.insets = new Insets(5, 5, 5, 5);
				gbc_tfOauthTokenSecret.fill = GridBagConstraints.HORIZONTAL;
				gbc_tfOauthTokenSecret.gridx = 1;
				gbc_tfOauthTokenSecret.gridy = 3;
				plConfigure.add(tfOauthTokenSecret, gbc_tfOauthTokenSecret);
					
				JPanel plControl = new JPanel();
				GridBagConstraints gbc_plControl = new GridBagConstraints();
				gbc_plControl.insets = new Insets(5, 5, 5, 0);
				gbc_plControl.fill = GridBagConstraints.BOTH;
				gbc_plControl.gridx = 0;
				gbc_plControl.gridy = 1;
				plPathSettings.add(plControl, gbc_plControl);
				
				JPanel plTestResult = new JPanel();
				GridBagConstraints gbc_plTestResult = new GridBagConstraints();
				gbc_plTestResult.weighty = 1.0;
				gbc_plTestResult.fill = GridBagConstraints.HORIZONTAL;
				gbc_plTestResult.gridx = 0;
				gbc_plTestResult.gridy = 2;
				gbc_plTestResult.insets = new Insets(5, 5, 0, 0);
				plPathSettings.add(plTestResult, gbc_plTestResult);
				plTestResult.setLayout(new GridLayout(0, 1, 0, 0));
				
				JLabel lblTestResult = new JLabel("Please test path settings before save it.");
				plTestResult.add(lblTestResult);

				JButton btnTest = new JButton("Test");
				btnTest.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) 
					{
						TumblrToken token = UIToData();
						if(token != null)
						{
							lblTestResult.setText("test Token");
							String result = TumblrServices.getInstance().testConnect(token);
							lblTestResult.setText(result);
						}
						else
						{
							lblTestResult.setText("Error input.");
						}
					}
				});
				plControl.add(btnTest);
				
				btnApply = new JButton("Apply");
				btnApply.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						TumblrToken token = UIToData();
						if(token != null)
						{
							SettingService.getInstance().applyToken(token);
						}
						else
						{
							lblTestResult.setText("Token is empty.");
						}
					}
				});
				plControl.add(btnApply);
				
				btnSave = new JButton("Save");
				btnSave.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						TumblrToken token = UIToData();
						if(token != null)
						{
							SettingService.getInstance().saveToken(token);
						}
						else
						{
							lblTestResult.setText("Token is empty.");
						}
					}
				});
				plControl.add(btnSave);
				
				btnLoad = new JButton("Load");
				btnLoad.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						SettingService.getInstance().loadToken();
						dataToUI();
					}
				});
				plControl.add(btnLoad);
				
				JButton btnCancel = new JButton("Cancel");
				btnCancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						TokenDialog.this.dispose();
					}
				});
				plControl.add(btnCancel);
				dataToUI();
	}
	public void dataToUI()
	{
		TumblrToken token = SettingService.getInstance().getToken();
		if(token != null && token.getConsumer_key() 		!= null && !token.getConsumer_key()			.trim().isEmpty()){tfConsumerKey		.setText(token.getConsumer_key());}
		if(token != null && token.getConsumer_secret() 		!= null && !token.getConsumer_secret()		.trim().isEmpty()){tfConsumerSecret		.setText(token.getConsumer_secret());}
		if(token != null && token.getOauth_token() 			!= null && !token.getOauth_token()			.trim().isEmpty()){tfOauthToken			.setText(token.getOauth_token());}
		if(token != null && token.getOauth_token_secret() 	!= null && !token.getOauth_token_secret()	.trim().isEmpty()){tfOauthTokenSecret	.setText(token.getOauth_token_secret());}
	}
	public TumblrToken UIToData()
	{
		String consumerKey 		= tfConsumerKey.getText();
		String oauthToken 		= tfOauthToken.getText();
		String consumerSecret	= tfConsumerSecret.getText();
		String oauthTokenSecret = tfOauthTokenSecret.getText();
		if(consumerKey 		!=null && !consumerKey		.trim().isEmpty()
		&& oauthToken 		!=null && !oauthToken		.trim().isEmpty()
		&& consumerSecret 	!=null && !consumerSecret	.trim().isEmpty()
		&& oauthTokenSecret !=null && !oauthTokenSecret	.trim().isEmpty()
		)
		{
			return new TumblrToken(consumerKey,oauthToken,consumerSecret,oauthTokenSecret);
		}
		return null;
	}
}
