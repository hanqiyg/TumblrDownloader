package com.icesoft.tumblr.downloader.panel;

import javax.swing.JPanel;
import javax.swing.JTable;

import org.apache.http.pool.PoolStats;

import com.icesoft.tumblr.downloader.DownloadManager;
import com.icesoft.tumblr.downloader.HttpClientConnectionManager;
import com.icesoft.tumblr.downloader.MainWindow;
import com.icesoft.tumblr.downloader.Settings;
import com.icesoft.tumblr.downloader.datamodel.DownloadModel;
import com.icesoft.tumblr.downloader.datamodel.ProgressCellRenderer;
import com.icesoft.tumblr.downloader.service.TumblrServices;
import com.icesoft.tumblr.downloader.workers.IHttpGetWorker;

import javax.swing.JScrollPane;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.List;

import javax.swing.JLabel;

public class DownloadPanel extends JPanel implements IRefreshable{
	private static final long serialVersionUID = 4111940040655069650L;
	private JTable table;
	private DownloadModel model;
	private JLabel lblStats;
	public DownloadPanel() {
		model = new DownloadModel();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0};
		gridBagLayout.rowHeights = new int[] {0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0};
		setLayout(gridBagLayout);
		JScrollPane scrollPane = new JScrollPane();
		table = new JTable();
		table.setModel(model);
		table.getColumn("Progress").setCellRenderer(new ProgressCellRenderer());
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.NORTH;
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		
		lblStats = new JLabel("");
		panel.add(lblStats);
		scrollPane.setColumnHeaderView(table.getTableHeader());
		scrollPane.setViewportView(table);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		add(scrollPane, gbc_scrollPane);
	}
	public void loadStats(){
		PoolStats s = HttpClientConnectionManager.getInstance().getStats();
		if(s != null){
			lblStats.setText("HttpClient Usage:" + s.getLeased() + " / " + s.getMax());
		}else{
			lblStats.setText("Fail to get HttpClient Infomation.");
		}
	}

	@Override
	public void refresh() {
		if(model!= null){
			model.fireTableDataChanged();
			loadStats();
		}	
	}	
}
