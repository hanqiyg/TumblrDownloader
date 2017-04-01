package com.icesoft.tumblr.downloader.panel;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import org.apache.http.pool.PoolStats;

import com.icesoft.tumblr.downloader.managers.HttpClientConnectionManager;
import com.icesoft.tumblr.downloader.panel.interfaces.IUpdatable;
import com.icesoft.tumblr.downloader.tablemodel.DateCellRenderer;
import com.icesoft.tumblr.downloader.tablemodel.DownloadModel;
import com.icesoft.tumblr.downloader.tablemodel.DownloadModel.ColName;
import com.icesoft.tumblr.downloader.tablemodel.DownloadTaskStateFilter;
import com.icesoft.tumblr.downloader.workers.DownloadTask;
import com.icesoft.tumblr.downloader.tablemodel.ProgressCellRenderer;

import javax.swing.JScrollPane;
import java.awt.GridBagLayout;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.Insets;import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JButton;

public class DownloadPanel extends JPanel implements IUpdatable{
	private static final long serialVersionUID = 4111940040655069650L;
	private JTable table;
	private DownloadModel model;
	private JLabel lblStats;
	private TableRowSorter<DownloadModel> sorter;
	public DownloadPanel() {
		model = new DownloadModel();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0};
		gridBagLayout.rowHeights = new int[] {0};
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0};
		setLayout(gridBagLayout);
		JScrollPane scrollPane = new JScrollPane();
		sorter = new TableRowSorter<DownloadModel>(model);
		table = new JTable();
		table.setModel(model);
		table.getColumn(ColName.PROGRESS.toString()).setCellRenderer(new ProgressCellRenderer());
		table.getColumn(ColName.CREATETIME.toString()).setCellRenderer(new DateCellRenderer());
		table.setRowSorter(sorter);
		table.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				mouseRightButtonClick(e);
			}		
		});
		
		JPanel plControl = new JPanel();
		for(DownloadTask.STATE state : DownloadTask.STATE.values()){
			JButton button = new JButton(state.toString());
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sorter.setRowFilter(new DownloadTaskStateFilter(state));
				}
			});
			plControl.add(button);
		}
		JButton btnAll = new JButton("ALL");
		btnAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sorter.setRowFilter(null);
			}
		});
		plControl.add(btnAll);
		
		GridBagConstraints gbc_plControl = new GridBagConstraints();
		gbc_plControl.insets = new Insets(5, 5, 5, 5);
		gbc_plControl.fill = GridBagConstraints.BOTH;
		gbc_plControl.gridx = 0;
		gbc_plControl.gridy = 0;
		add(plControl, gbc_plControl);
		
		
		JPanel plStatus = new JPanel();
		GridBagConstraints gbc_plStatus = new GridBagConstraints();
		gbc_plStatus.anchor = GridBagConstraints.NORTH;
		gbc_plStatus.fill = GridBagConstraints.HORIZONTAL;
		gbc_plStatus.insets = new Insets(0, 0, 5, 0);
		gbc_plStatus.gridx = 0;
		gbc_plStatus.gridy = 1;
		add(plStatus, gbc_plStatus);
		
		lblStats = new JLabel("");
		plStatus.add(lblStats);
		scrollPane.setColumnHeaderView(table.getTableHeader());
		scrollPane.setViewportView(table);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 2;
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
	public void update() {
		if(model!= null){
			fireTableDataChangeAndPreserveSelection(table);
			loadStats();
		}	
	}
	public void fireTableDataChangeAndPreserveSelection(JTable table){
        final int[] sel = table.getSelectedRows();
        AbstractTableModel model =  (AbstractTableModel) table.getModel();
        model.fireTableDataChanged();
        for (int i=0; i<sel.length; i++)
        	table.getSelectionModel().addSelectionInterval(sel[i], sel[i]);
	}

	private void mouseRightButtonClick(MouseEvent e) {
		 if (e.getButton() == MouseEvent.BUTTON3) {  
	           int focusedRowIndex = table.rowAtPoint(e.getPoint());  
	           if (focusedRowIndex == -1) {  
	               return;  
	           }  
	           table.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
	           DownloadTask task = model.getTask(focusedRowIndex);
	           if(task != null){
	        	   JPopupMenu menu = getRightMouseMenu(task);
		           menu.show(table, e.getX(), e.getY());  
	           } 
	       }  
	}
	private JPopupMenu getRightMouseMenu(DownloadTask task){
		JPopupMenu rightMouseMenu = new JPopupMenu();
		rightMouseMenu.add(getOpenFolderMenuItem(task));
		rightMouseMenu.add(getStopMenuItem(task));
		return rightMouseMenu;  
	}

	public JMenuItem getOpenFolderMenuItem(DownloadTask task){
		JMenuItem item = new JMenuItem("Open Folder");  
		item.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent evt) {  
            	String folder = task.getFile().getParent();
            	if (Desktop.isDesktopSupported()) {
            	    try {
						Desktop.getDesktop().open(new File(folder));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            }
        }); 
        return item;
	}
	public JMenuItem getStopMenuItem(DownloadTask task){
		JMenuItem item = new JMenuItem("Stop");  
		item.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent evt) {  
            	task.stop();
            }
        }); 
        return item;
	}
}

