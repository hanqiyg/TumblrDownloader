package com.icesoft.tumblr.downloader.panel;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.icesoft.tumblr.downloader.TextAreaOutputStream;

import java.awt.GridBagLayout;
import java.io.PrintStream;
import java.awt.GridBagConstraints;

public class ConsolePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8519785866822503087L;

	/**
	 * Create the panel.
	 */
	public ConsolePanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JTextArea textArea = new JTextArea();
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 0;
		gbc_textArea.gridy = 0;
		add(textArea, gbc_textArea);
		
		TextAreaOutputStream taos = new TextAreaOutputStream( textArea, 60 );
        PrintStream ps = new PrintStream( taos );

        System.setOut( ps );
        System.setErr( ps );
        
	}

}
