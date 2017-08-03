package gui.items.voting;

import gui.CoreRowSorter;
import gui.library.MTable;
import gui.models.WalletPollsTableModel;
import gui.voting.PollFrame;
import lang.Lang;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;

import controller.Controller;
import core.item.assets.AssetCls;
import core.voting.Poll;
import database.wallet.PollMap;

@SuppressWarnings("serial")
public class MyVotingPanel extends JPanel
{
	public MyVotingPanel()
	{
		this.setLayout(new GridBagLayout());
		setName(Lang.getInstance().translate("My Votings"));
		
		//PADDING
		this.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		//TABLE GBC
		GridBagConstraints tableGBC = new GridBagConstraints();
		tableGBC.fill = GridBagConstraints.BOTH; 
		tableGBC.anchor = GridBagConstraints.NORTHWEST;
		tableGBC.weightx = 1;
		tableGBC.weighty = 1;
		tableGBC.gridwidth = 10;
		tableGBC.gridx = 0;	
		tableGBC.gridy= 0;	
		
		//BUTTON GBC
		GridBagConstraints buttonGBC = new GridBagConstraints();
		buttonGBC.insets = new Insets(10, 0, 0, 10);
		buttonGBC.fill = GridBagConstraints.NONE;  
		buttonGBC.anchor = GridBagConstraints.NORTHWEST;
		buttonGBC.gridx = 0;	
		buttonGBC.gridy = 1;	
		
		//TABLE
		final WalletPollsTableModel pollsModel = new WalletPollsTableModel();
		final MTable table = new MTable(pollsModel);
		
		//POLLS SORTER
		Map<Integer, Integer> indexes = new TreeMap<Integer, Integer>();
		indexes.put(WalletPollsTableModel.COLUMN_NAME, PollMap.NAME_INDEX);
		indexes.put(WalletPollsTableModel.COLUMN_ADDRESS, PollMap.CREATOR_INDEX);
		CoreRowSorter sorter = new CoreRowSorter(pollsModel, indexes);
		table.setRowSorter(sorter);
				
		//CHECKBOX FOR CONFIRMED
		TableColumn confirmedColumn = table.getColumnModel().getColumn(3);
		confirmedColumn.setCellRenderer(table.getDefaultRenderer(Boolean.class));
				
		table.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mousePressed(MouseEvent e) 
			{
				Point p = e.getPoint();
				int row = table.rowAtPoint(p);
				table.setRowSelectionInterval(row, row);
		     }
		});
		
		table.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mousePressed(MouseEvent e) 
			{
				Point p = e.getPoint();
				int row = table.rowAtPoint(p);
				table.setRowSelectionInterval(row, row);
				
				if(e.getClickCount() == 2)
				{
					row = table.convertRowIndexToModel(row);
					Poll poll = pollsModel.getPoll(row);
					new PollFrame(poll, Controller.getInstance().getAsset(AssetCls.FEE_KEY));
				}
		     }
		});
		
		//ADD NAMING SERVICE TABLE
		this.add(new JScrollPane(table), tableGBC);
		
		
	}
	
	public void onCreateClick()
	{
//		new CreatePollFrame();
	}
	
	public void onAllClick()
	{
//		new AllPollsFrame();
	}
}
