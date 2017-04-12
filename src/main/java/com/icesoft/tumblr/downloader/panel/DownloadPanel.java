package com.icesoft.tumblr.downloader.panel;

import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
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
import com.icesoft.tumblr.state.DownloadPriority;
import com.icesoft.tumblr.state.DownloadState;
import com.icesoft.tumblr.state.interfaces.IContext;

public class DownloadPanel extends JPanel implements IUpdatable{
	private static final long serialVersionUID = 4111940040655069650L;
	private static Logger logger = Logger.getLogger(DownloadPanel.class);  
	private JTable table;
	private DownloadModel model;
	private JLabel lblHttpClientStats;
	private JLabel lblThreadState;
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
		for(DownloadState state : DownloadState.values()){
			JButton button = new JButton(state.toString());
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sorter.setRowFilter(new DownloadTaskStateFilter(state));
				}
			});
			plControl.add(button);
		}
		
		JButton btnActive = new JButton("Active");
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
		btnActive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sorter.setRowFilter(null);
				DownloadManager.getInstance().stopAll();
			}
		});
		plControl.add(btnStopAll);
		
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
		
		lblHttpClientStats = new JLabel("");
		plStatus.add(lblHttpClientStats);
		
		lblThreadState = new JLabel("");
		plStatus.add(lblThreadState);
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
			lblHttpClientStats.setText("HttpClient Usage:" + s.getLeased() + " / " + s.getMax());
		}else{
			lblHttpClientStats.setText("Fail to get HttpClient Infomation.");
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
		String info = String.format("[monitor] [%d/%d] Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s",
                pool.getPoolSize(),
                pool.getCorePoolSize(),
                pool.getActiveCount(),
                pool.getCompletedTaskCount(),
                pool.getTaskCount(),
                pool.isShutdown(),
                pool.isTerminated());
		lblThreadState.setText(info);
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
	           Map<Integer,IContext> contexts = model.getContexts(selections);
	           if(contexts != null && contexts.size() > 0){
	        	   JPopupMenu menu = MenuUtils.getDownloadPanelRightButtonMenu(contexts, model);
		           menu.show(table, e.getX(), e.getY());  
	           } 
	       } 
	}
	private JPopupMenu getRightMouseMenu(IContext task, int focusedRowIndex){
		JPopupMenu rightMouseMenu = new JPopupMenu();
		rightMouseMenu.add(getOpenFolderMenuItem(task,focusedRowIndex));	
		rightMouseMenu.add(getStopMenuItem(task,focusedRowIndex));
		rightMouseMenu.add(getDownloadMenuItem(task,focusedRowIndex));
		rightMouseMenu.add(getPriorityMenuItem(task,focusedRowIndex));
		return rightMouseMenu; 
	}

	public JMenuItem getOpenFolderMenuItem(IContext context, int focusedRowIndex){
		JMenuItem item = new JMenuItem("Open Folder");  
		item.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent evt) {  
       
            	if (Desktop.isDesktopSupported()) {
            	    try {
            	    	File parent = new File(context.getAbsolutePath()).getParentFile();
            	    	if(parent.exists())
            	    	{
            	    		Desktop.getDesktop().open(parent);
            	    	}else
            	    	{
            	    		logger.debug("Folder[" + parent.getAbsolutePath() +"] does not exist.");
            	    	}					
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            }
        }); 
        return item;
	}
	public JMenu getPriorityMenuItem(IContext context, int focusedRowIndex){
		JMenu item = new JMenu("Priority");
		DownloadPriority priority = context.getPriority();
		for(DownloadPriority p : DownloadPriority.values())
		{
			if(p.equals(priority))
			{
				JMenuItem sub = new JMenuItem(p.name());
				item.add(sub);
			}
			else
			{
				JMenuItem sub = new JMenuItem(p.name());
				sub.addActionListener(new ActionListener() {  
		            public void actionPerformed(ActionEvent evt) {  
		            	context.setPriority(p);
		            	model.fireTableRowsUpdated(focusedRowIndex, focusedRowIndex);
		            }
		        });
				item.add(sub);
			}
		}
		return item;
	}
	public JMenuItem getStopMenuItem(IContext context, int focusedRowIndex){
		JMenuItem item = new JMenuItem("Stop");  
		item.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent evt) {  
            	context.setRun(false);
            }
        }); 
        return item;
	}
	public JMenuItem getDownloadMenuItem(IContext context, int focusedRowIndex){
		JMenuItem item = new JMenuItem("Download");  
		item.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent evt) {  
            	DownloadManager.getInstance().downloadResumeSingleTask(context);
            }
        }); 
        return item;
	}
}