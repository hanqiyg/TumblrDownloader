package com.icesoft.tumblr.downloader.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableRowSorter;

import org.apache.http.pool.PoolStats;
import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.managers.DownloadManager;
import com.icesoft.tumblr.downloader.managers.HttpClientConnectionManager;
import com.icesoft.tumblr.downloader.panel.interfaces.IUpdatable;
import com.icesoft.tumblr.downloader.tablemodel.DateCellRenderer;
import com.icesoft.tumblr.downloader.tablemodel.DownloadActiveFilter;
import com.icesoft.tumblr.downloader.tablemodel.DownloadModel;
import com.icesoft.tumblr.downloader.tablemodel.DownloadModel.ColName;
import com.icesoft.tumblr.downloader.tablemodel.DownloadPriorityRenderer;
import com.icesoft.tumblr.downloader.tablemodel.DownloadStateRenderer;
import com.icesoft.tumblr.downloader.tablemodel.DownloadTaskStateFilter;
import com.icesoft.tumblr.downloader.tablemodel.ProgressCellRenderer;
import com.icesoft.tumblr.downloader.ui.utils.MenuUtils;
import com.icesoft.tumblr.state.DownloadState;
import com.icesoft.tumblr.state.interfaces.IContext;
import javax.swing.JProgressBar;

public class DownloadPanel extends JPanel implements IUpdatable{
	private static final long serialVersionUID = 4111940040655069650L;
	private static Logger logger = Logger.getLogger(DownloadPanel.class);  
	private JTable table;
	private DownloadModel model;
	private JLabel lblHttpClientStats;
	private JLabel lblThreadState;
	private TableRowSorter<DownloadModel> sorter;
	private JProgressBar pbHttpClients;
	private JProgressBar pbThreads;
	private JProgressBar pbTasks;
	public DownloadPanel() {
		model = new DownloadModel();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0};
		setLayout(gridBagLayout);
		JScrollPane scrollPane = new JScrollPane();
		sorter = new TableRowSorter<DownloadModel>(model);
		table = new JTable();
		table.setModel(model);
		table.getColumn(ColName.PROGRESS.toString()).setCellRenderer(new ProgressCellRenderer());
		table.getColumn(ColName.CREATETIME.toString()).setCellRenderer(new DateCellRenderer());
		table.getColumn(ColName.PRIORITY.toString()).setCellRenderer(new DownloadPriorityRenderer());
		table.getColumn(ColName.STATUS.toString()).setCellRenderer(new DownloadStateRenderer());
		table.setRowSorter(sorter);
		table.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				mouseRightButtonClick(e);
			}		
		});
		
		JPanel plControl = new JPanel();	
		GridBagConstraints gbc_plControl = new GridBagConstraints();
		gbc_plControl.insets = new Insets(5, 5, 5, 5);
		gbc_plControl.fill = GridBagConstraints.BOTH;
		gbc_plControl.gridx = 0;
		gbc_plControl.gridy = 0;
		add(plControl, gbc_plControl);
		
			JButton btnWaiting = new JButton("Active");
			btnWaiting.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sorter.setRowFilter(new DownloadTaskStateFilter(DownloadState.WAIT));
				}
			});
			plControl.add(btnWaiting);
			
			JButton btnFinished = new JButton("Completed");
			btnFinished.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sorter.setRowFilter(new DownloadTaskStateFilter(DownloadState.COMPLETE));
				}
			});
			plControl.add(btnFinished);
			
			JButton btnException = new JButton("Exception");
			btnException.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sorter.setRowFilter(new DownloadTaskStateFilter(DownloadState.EXCEPTION));
				}
			});
			plControl.add(btnException);
			
			JButton btnActive = new JButton("Running");
			btnActive.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sorter.setRowFilter(new DownloadActiveFilter());
				}
			});
			plControl.add(btnActive);
			
			JButton btnAll = new JButton("ALL");
			btnAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sorter.setRowFilter(null);
				}
			});
			plControl.add(btnAll);
			
			JButton btnDownloadAll = new JButton("DownloadAll ");
			btnDownloadAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DownloadManager.getInstance().downloadResumeAllTasks();
				}
			});
			plControl.add(btnDownloadAll);
			
			JButton btnStopAll = new JButton("StopAll");
			btnStopAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sorter.setRowFilter(null);
					DownloadManager.getInstance().stopAllTask();
				}
			});
			plControl.add(btnStopAll);		
		
/*		JPanel plStatus = new JPanel();
		GridBagConstraints gbc_plStatus = new GridBagConstraints();
		gbc_plStatus.anchor = GridBagConstraints.NORTH;
		gbc_plStatus.fill = GridBagConstraints.HORIZONTAL;
		gbc_plStatus.insets = new Insets(0, 0, 5, 0);
		gbc_plStatus.gridx = 0;
		gbc_plStatus.gridy = 1;
		add(plStatus, gbc_plStatus);
		
			lblHttpClientStats = new JLabel("");
			plStatus.add(lblHttpClientStats);
			
			lblThreadState = new JLabel("");
			plStatus.add(lblThreadState);*/
		
		JPanel plStatusProgress = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.NORTH;
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.insets = new Insets(5, 5, 5, 5);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		add(plStatusProgress, gbc_panel);
		
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, 0.0, 1.0};
		gbl_panel.rowWeights = new double[]{1.0};
		plStatusProgress.setLayout(gbl_panel);
		
			JLabel lblHttpclients = new JLabel("HttpClient:");
			GridBagConstraints gbc_lblHttpclients = new GridBagConstraints();
			gbc_lblHttpclients.anchor = GridBagConstraints.NORTHWEST;
			gbc_lblHttpclients.insets = new Insets(5, 5, 5, 5);
			gbc_lblHttpclients.gridx = 0;
			gbc_lblHttpclients.gridy = 0;
			plStatusProgress.add(lblHttpclients, gbc_lblHttpclients);
			
			pbHttpClients = new JProgressBar();
			GridBagConstraints gbc_pbHttpClients = new GridBagConstraints();
			gbc_pbHttpClients.anchor = GridBagConstraints.NORTH;
			gbc_pbHttpClients.fill = GridBagConstraints.HORIZONTAL;
			gbc_pbHttpClients.insets = new Insets(5, 5, 5, 5);
			gbc_pbHttpClients.gridx = 1;
			gbc_pbHttpClients.gridy = 0;
			plStatusProgress.add(pbHttpClients, gbc_pbHttpClients);
			
			JLabel lblThreads = new JLabel("Threads:");
			GridBagConstraints gbc_lblThreads = new GridBagConstraints();
			gbc_lblThreads.anchor = GridBagConstraints.NORTHWEST;
			gbc_lblThreads.insets = new Insets(5, 5, 5, 5);
			gbc_lblThreads.gridx = 2;
			gbc_lblThreads.gridy = 0;
			plStatusProgress.add(lblThreads, gbc_lblThreads);
			
			pbThreads = new JProgressBar();
			GridBagConstraints gbc_pbThreads = new GridBagConstraints();
			gbc_pbThreads.anchor = GridBagConstraints.NORTH;
			gbc_pbThreads.fill = GridBagConstraints.HORIZONTAL;
			gbc_pbThreads.insets = new Insets(5, 5, 5, 5);
			gbc_pbThreads.gridx = 3;
			gbc_pbThreads.gridy = 0;
			plStatusProgress.add(pbThreads, gbc_pbThreads);
	
			
			JLabel lbTasks = new JLabel("Tasks:");
			GridBagConstraints gbc_lbTasks = new GridBagConstraints();
			gbc_lbTasks.anchor = GridBagConstraints.NORTHWEST;
			gbc_lbTasks.insets = new Insets(5, 5, 5, 5);
			gbc_lbTasks.gridx = 4;
			gbc_lbTasks.gridy = 0;
			plStatusProgress.add(lbTasks, gbc_lbTasks);
			
			pbTasks = new JProgressBar();
			GridBagConstraints gbc_pbTasks = new GridBagConstraints();
			gbc_pbTasks.anchor = GridBagConstraints.NORTH;
			gbc_pbTasks.fill = GridBagConstraints.HORIZONTAL;
			gbc_pbTasks.insets = new Insets(5, 5, 5, 5);
			gbc_pbTasks.gridx = 5;
			gbc_pbTasks.gridy = 0;
			plStatusProgress.add(pbTasks, gbc_pbTasks);
		
		scrollPane.setColumnHeaderView(table.getTableHeader());
		scrollPane.setViewportView(table);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 2;
		add(scrollPane, gbc_scrollPane);
	}
	public void loadStats(){
		PoolStats s = HttpClientConnectionManager.getInstance().getStats();		
		if(s != null){
			pbHttpClients.setMaximum(s.getMax());
			pbHttpClients.setMinimum(0);
			pbHttpClients.setValue(s.getLeased());
			pbHttpClients.setString(s.getLeased() + " / " + s.getMax());
			pbHttpClients.setStringPainted(true);
		}else{
			pbHttpClients.setMaximum(1);
			pbHttpClients.setMinimum(0);
			pbHttpClients.setValue(1);
			pbHttpClients.setString("");
			pbHttpClients.setStringPainted(true);
		}
	}
	public  void loadThreadsInfo(){
/*		ThreadInfo[] infos = DownloadManager.getInstance().getThreadsInfo();
		int total = 0;
		int run = 0;

		for(ThreadInfo i : infos){
			String name = i.getThreadName();
			State state = i.getThreadState();

			if(name.contains("pool-1")){
				total++;
				if(state.equals(State.RUNNABLE)){
					run++;
				}
			}
		}*/
		ThreadPoolExecutor pool = DownloadManager.getInstance().getPool();
		
		pbThreads.setMaximum(pool.getPoolSize());
		pbThreads.setMinimum(0);
		pbThreads.setValue(pool.getActiveCount());
		pbThreads.setString(pool.getActiveCount() + " / " + pool.getPoolSize());
		pbThreads.setStringPainted(true);
		
		pbTasks.setMaximum((int)pool.getTaskCount());
		pbTasks.setMinimum(0);
		pbTasks.setValue((int)pool.getCompletedTaskCount());
		pbTasks.setString(pool.getCompletedTaskCount() + " / " + pool.getTaskCount());
		pbTasks.setStringPainted(true);
	}

	@Override
	public void update() {
		if(model!= null){
			fireTableDataChangeAndPreserveSelection(table);
			loadStats();
			loadThreadsInfo();
		}	
	}
	public void fireTableDataChangeAndPreserveSelection(JTable table){
        final int[] sel = table.getSelectedRows();
        DownloadModel model =  (DownloadModel) table.getModel();
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
	           boolean isSelected = false;
	           int[] selections = table.getSelectedRows();
	           for(int i : selections){
	        	   if(i == focusedRowIndex)
	        	   {
	        		   isSelected = true; 
	        	   }
	           }
	           if(!isSelected)
	           {
		           table.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
	           }
	           selections = table.getSelectedRows();
	           List<IContext> contexts = model.getContexts(selections);
	           if(contexts != null && contexts.size() > 0){
	        	   JPopupMenu menu = MenuUtils.getDownloadPanelRightButtonMenu(contexts, model);
		           menu.show(table, e.getX(), e.getY());  
	           } 
	       } 
	}	
}