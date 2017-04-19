package com.icesoft.tumblr.downloader.panel;

import javax.swing.JPanel;
import com.icesoft.tumblr.downloader.dialog.PathDialog;
import com.icesoft.tumblr.downloader.dialog.ProxyDialog;
import com.icesoft.tumblr.downloader.dialog.TokenDialog;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;

public class SettingsPanel extends JPanel {
	private static final long serialVersionUID = 4286659292547234267L;
	

	public SettingsPanel() {
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
		
		JButton btnPathSettings = new JButton("Path Settings");
		btnPathSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PathDialog pd = new PathDialog();
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
		
		JButton btnTokenSettings = new JButton("Token Settings");
		btnTokenSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TokenDialog pd = new TokenDialog();
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
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		add(btnTokenSettings);		
		add(btnPathSettings);
		add(btnProxySettings);		
	}
}
