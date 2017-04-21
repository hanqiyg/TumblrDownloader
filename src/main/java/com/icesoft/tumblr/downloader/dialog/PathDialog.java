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
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.icesoft.tumblr.downloader.service.SettingService;


public class PathDialog extends JDialog
{
	private static final long serialVersionUID = 7898637757439796777L;
	private JButton btnSave,btnApply,btnLoad;
	private JTextField tfPath;
	private JFileChooser jFileChooser;
	public PathDialog() {
		this.setTitle("Path Settings");
		this.setMinimumSize(new Dimension(380,260));
		TitledBorder titled = BorderFactory.createTitledBorder
				(
					null
					,"Path Settings"
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
				gbc_plConfigure.insets = new Insets(5, 5, 5, 0);
				gbc_plConfigure.fill = GridBagConstraints.HORIZONTAL;
				gbc_plConfigure.gridy = 0;
				gbc_plConfigure.gridx = 0;

				plPathSettings.add(plConfigure, gbc_plConfigure);
				GridBagLayout gbl_plConfigure = new GridBagLayout();
				gbl_plConfigure.columnWeights = new double[]{1.0};
				gbl_plConfigure.rowWeights = new double[]{0.0, 0.0};
				plConfigure.setLayout(gbl_plConfigure);
				
				tfPath = new JTextField();
				tfPath.setEditable(false);
				tfPath.setColumns(10);
				GridBagConstraints gbc_textField = new GridBagConstraints();
				gbc_textField.insets = new Insets(0, 0, 5, 0);
				gbc_textField.fill = GridBagConstraints.HORIZONTAL;
				gbc_textField.gridx = 0;
				gbc_textField.gridy = 0;
				plConfigure.add(tfPath, gbc_textField);
				
				JButton button = new JButton("Choose Folder");
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int i = jFileChooser.showOpenDialog(null);
						if(i == JFileChooser.APPROVE_OPTION){
							String path = jFileChooser.getSelectedFile().getAbsolutePath();
							tfPath.setText(path);
						}
					}
				});
				GridBagConstraints gbc_button = new GridBagConstraints();
				gbc_button.gridx = 0;
				gbc_button.gridy = 1;
				plConfigure.add(button, gbc_button);
					
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
				jFileChooser = new JFileChooser();
				jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				JButton btnTest = new JButton("Test");
				btnTest.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) 
					{
						String path = tfPath.getText().trim();
						if(path == null || path.isEmpty())
						{
							lblTestResult.setText("Path can not be empty.");
							btnApply.setEnabled(false);
							btnSave.setEnabled(false);	
						}
						else
						{
							if(SettingService.getInstance().testPath(path))
							{
								lblTestResult.setText("Pass.");
								btnApply.setEnabled(true);
								btnSave.setEnabled(true);	
							}else{
								lblTestResult.setText("Fail.");
								btnApply.setEnabled(false);
								btnSave.setEnabled(false);	
							}
						}
					}
				});
				plControl.add(btnTest);
				
				btnApply = new JButton("Apply");
				btnApply.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String path = UIToData();
						if(path != null)
						{
							SettingService.getInstance().applyPath(path);
						}
					}
				});
				plControl.add(btnApply);
				
				btnSave = new JButton("Save");
				btnSave.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String path = UIToData();
						if(path != null)
						{
							SettingService.getInstance().savePath(path);
						}
					}
				});
				plControl.add(btnSave);
				
				btnLoad = new JButton("Load");
				btnLoad.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						SettingService.getInstance().loadPath();
						DataToUI();
					}
				});
				plControl.add(btnLoad);
				
				JButton btnCancel = new JButton("Cancel");
				btnCancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						PathDialog.this.dispose();
					}
				});
				plControl.add(btnCancel);
				btnApply.setEnabled(false);
				btnSave.setEnabled(false);	
				DataToUI();
	}
	public void DataToUI()
	{
		tfPath.setText(SettingService.getInstance().getPath());
	}
	public String UIToData(){
		if(tfPath.getText() != null && !tfPath.getText().trim().isEmpty()){
			return tfPath.getText().trim();
		}
		return null;
	}
}
