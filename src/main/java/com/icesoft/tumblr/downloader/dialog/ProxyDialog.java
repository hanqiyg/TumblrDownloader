package com.icesoft.tumblr.downloader.dialog;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListDataListener;

import com.icesoft.tumblr.downloader.configure.Settings.ProxyType;

import javax.swing.JButton;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import javax.swing.JComboBox;

public class ProxyDialog extends JDialog
{
	private static final long serialVersionUID = -3518995490315958755L;
	private JTextField tfProxyHost;
	private JTextField tfPort;


	private JComboBox<ProxyType> proxyType;
	public ProxyDialog() {
		this.setTitle("Proxy Settings");
		this.setMinimumSize(new Dimension(500,250));
		TitledBorder titled = BorderFactory.createTitledBorder
				(
					null
					,"Proxy Settings"
					,TitledBorder.CENTER
					,TitledBorder.TOP
					,new Font("微软雅黑", Font.BOLD, 16)
				);
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel plProxySettings = new JPanel();
		plProxySettings.setBorder(titled);
		getContentPane().add(plProxySettings);
		GridBagConstraints gbc_plProxySettings = new GridBagConstraints();
		gbc_plProxySettings.fill = GridBagConstraints.BOTH;
		gbc_plProxySettings.insets = new Insets(5, 5, 5, 5);
		gbc_plProxySettings.gridx = 0;
		gbc_plProxySettings.gridy = 0;
				GridBagLayout gbl_plProxySettings = new GridBagLayout();
				gbl_plProxySettings.columnWidths = new int[] {};
				gbl_plProxySettings.columnWeights = new double[]{1.0};
				gbl_plProxySettings.rowWeights = new double[]{1.0, 1.0};
				plProxySettings.setLayout(gbl_plProxySettings);
		
				JPanel plConfigure = new JPanel();
				GridBagConstraints gbc_plConfigure = new GridBagConstraints();
				gbc_plConfigure.insets = new Insets(5, 5, 5, 0);
				gbc_plConfigure.fill = GridBagConstraints.HORIZONTAL;
				gbc_plConfigure.gridy = 0;
				gbc_plConfigure.gridx = 0;

				plProxySettings.add(plConfigure, gbc_plConfigure);
				GridBagLayout gbl_plConfigure = new GridBagLayout();
				gbl_plConfigure.columnWeights = new double[]{0.0, 1.0};
				gbl_plConfigure.rowWeights = new double[]{0.0, 0.0, 0.0};
				plConfigure.setLayout(gbl_plConfigure);
				
				JLabel lbProxyType = new JLabel("ProxyType:");
				GridBagConstraints gbc_lbProxyType = new GridBagConstraints();
				gbc_lbProxyType.anchor = GridBagConstraints.NORTHEAST;
				gbc_lbProxyType.insets = new Insets(5, 5, 5, 5);
				gbc_lbProxyType.gridx = 0;
				gbc_lbProxyType.gridy = 0;
				plConfigure.add(lbProxyType, gbc_lbProxyType);
				
				proxyType = new JComboBox<ProxyType>();
				proxyType.setModel(new ComboBoxModel<ProxyType>(){
					ProxyType selection = null;
					@Override
					public int getSize() {
						return ProxyType.values().length;
					}
					@Override
					public ProxyType getElementAt(int index) {
						return ProxyType.values()[index];
					}
					@Override
					public void addListDataListener(ListDataListener l) {
					}
					@Override
					public void removeListDataListener(ListDataListener l) {
					}
					@Override
					public void setSelectedItem(Object anItem) {
						if(anItem instanceof ProxyType){
							ProxyType t = (ProxyType) anItem;
							this.selection = t;
						}
					}

					@Override
					public ProxyType getSelectedItem() {
						return selection;
					}					
				});
				proxyType.setSelectedIndex(0);
				GridBagConstraints gbc_comboBox = new GridBagConstraints();
				gbc_comboBox.insets = new Insets(0, 0, 5, 0);
				gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
				gbc_comboBox.gridx = 1;
				gbc_comboBox.gridy = 0;
				plConfigure.add(proxyType, gbc_comboBox);
				
				JLabel lblProxyhost = new JLabel("ProxyHost:");
				GridBagConstraints gbc_lblProxyhost = new GridBagConstraints();
				gbc_lblProxyhost.anchor = GridBagConstraints.EAST;
				gbc_lblProxyhost.insets = new Insets(5, 5, 5, 5);
				gbc_lblProxyhost.gridx = 0;
				gbc_lblProxyhost.gridy = 1;
				plConfigure.add(lblProxyhost, gbc_lblProxyhost);
				
				tfProxyHost = new JTextField();
				GridBagConstraints gbc_tfProxyHost = new GridBagConstraints();
				gbc_tfProxyHost.insets = new Insets(5, 5, 5, 0);
				gbc_tfProxyHost.anchor = GridBagConstraints.NORTH;
				gbc_tfProxyHost.fill = GridBagConstraints.HORIZONTAL;
				gbc_tfProxyHost.gridx = 1;
				gbc_tfProxyHost.gridy = 1;
				plConfigure.add(tfProxyHost, gbc_tfProxyHost);
				tfProxyHost.setColumns(10);
				
				JLabel lblProxyport = new JLabel("ProxyPort:");
				GridBagConstraints gbc_lblProxyport = new GridBagConstraints();
				gbc_lblProxyport.anchor = GridBagConstraints.EAST;
				gbc_lblProxyport.insets = new Insets(5, 5, 0, 5);
				gbc_lblProxyport.gridx = 0;
				gbc_lblProxyport.gridy = 2;
				plConfigure.add(lblProxyport, gbc_lblProxyport);
				
				tfPort = new JTextField();
				GridBagConstraints gbc_tfPort = new GridBagConstraints();
				gbc_tfPort.insets = new Insets(5, 5, 0, 0);
				gbc_tfPort.fill = GridBagConstraints.HORIZONTAL;
				gbc_tfPort.gridx = 1;
				gbc_tfPort.gridy = 2;
				plConfigure.add(tfPort, gbc_tfPort);
				tfPort.setColumns(10);
				
				JPanel plControl = new JPanel();
				GridBagConstraints gbc_plControl = new GridBagConstraints();
				gbc_plControl.insets = new Insets(5, 5, 5, 0);
				gbc_plControl.fill = GridBagConstraints.BOTH;
				gbc_plControl.gridx = 0;
				gbc_plControl.gridy = 1;
				plProxySettings.add(plControl, gbc_plControl);
				
				JPanel plTestResult = new JPanel();
				GridBagConstraints gbc_plTestResult = new GridBagConstraints();
				gbc_plTestResult.gridx = 0;
				gbc_plTestResult.gridy = 2;
				gbc_plTestResult.insets = new Insets(5, 5, 0, 0);
				gbc_plTestResult.fill = GridBagConstraints.HORIZONTAL;
				plProxySettings.add(plTestResult, gbc_plTestResult);
				plTestResult.setLayout(new GridLayout(0, 1, 0, 0));
				
				JLabel lblTestResult = new JLabel();
				plTestResult.add(lblTestResult);
				
				JButton btnTest = new JButton("Test");
				btnTest.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						lblTestResult.setText("checking..");
						if(checkSettings())
						{
							lblTestResult.setText("Pass.");
						}
						else
						{
							lblTestResult.setText("fail.");
						}
					}
				});
				plControl.add(btnTest);
				
				JButton btnApply = new JButton("Apply");
				btnApply.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
					}
				});
				plControl.add(btnApply);
				
				JButton btnSave = new JButton("Save");
				btnSave.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
					}
				});
				plControl.add(btnSave);
				
				JButton btnLoad = new JButton("Load");
				btnLoad.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
					}
				});
				plControl.add(btnLoad);
				
				JButton btnCancel = new JButton("Cancel");
				btnCancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ProxyDialog.this.dispose();
					}
				});
				plControl.add(btnCancel);
	}

/*	private boolean checkSystem() {
		System.setProperty("java.net.useSystemProxies", "true");
		System.out.println("detecting proxies");
		List l = null;
		try {
		    l = ProxySelector.getDefault().select(new URI("https://tumble.com"));
		} 
		catch (URISyntaxException e) {
		    e.printStackTrace();
		}
		if (l != null) {
		    for (Iterator<?> iter = l.iterator(); iter.hasNext();) {
		        java.net.Proxy proxy = (java.net.Proxy) iter.next();
		        switch(proxy.type()){
				case DIRECT:
					break;
				case HTTP:
					break;
				case SOCKS:
					break;
				default:
					break;		        
		        }

		    }
		}
		return false;
	}*/

	public boolean checkSettings(){
		return rootPaneCheckingEnabled;

	}
}
