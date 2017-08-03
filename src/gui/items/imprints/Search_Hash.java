package gui.items.imprints;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import core.crypto.Base58;
import core.crypto.Crypto;
import database.DBSet;
import database.HashesSignsMap;
import gui.Split_Panel;
import gui.library.My_JFileChooser;
import lang.Lang;

public class Search_Hash extends Split_Panel {
	
	
	public Search_Hash(){
		super("Search_Hash");
	
	this.jButton2_jToolBar_RightPanel.setVisible(false);
	this.jButton1_jToolBar_RightPanel.setVisible(false);
	this.searth_Favorite_JCheckBox_LeftPanel.setVisible(false);
	this.searth_My_JCheckBox_LeftPanel.setVisible(false);
	this.searthLabel_SearchToolBar_LeftPanel.setText(Lang.getInstance().translate("Hash"));
	this.searchTextField_SearchToolBar_LeftPanel.setMinimumSize(new Dimension(500,20));
	this.searchTextField_SearchToolBar_LeftPanel.setPreferredSize(new Dimension(500,20));
	this.button2_ToolBar_LeftPanel.setVisible(false);
	this.button1_ToolBar_LeftPanel.setText(Lang.getInstance().translate("Search Hash"));
		
	this.button1_ToolBar_LeftPanel.addActionListener(new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
				Hashs_from_Files();

			}
			
	});
		
	TableModelImprints aaa = new TableModelImprints();
	JTable Table_Hash = new JTable(aaa);
	
	this.jScrollPanel_LeftPanel.setViewportView(Table_Hash);
	
	DBSet db = DBSet.getInstance();
	HashesSignsMap map = db.getHashesSignsMap();
	byte[] a = "3j2AAAJYRoYVEtdoXZeLZBhjA6eWmJyr4Ng9F6N3whwY".getBytes();
	Object hashs = map.get("3j2AAAJYRoYVEtdoXZeLZBhjA6eWmJyr4Ng9F6N3whwY".getBytes());
			
		
		
	}
	
	
	protected void Hashs_from_Files() {
		
		// открыть диалог для файла
		//JFileChooser chooser = new JFileChooser();
		// руссификация диалога выбора файла
		//new All_Options().setUpdateUI(chooser);
		My_JFileChooser chooser = new My_JFileChooser();
		chooser.setDialogTitle(Lang.getInstance().translate("Select File"));
		

		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		

		// FileNameExtensionFilter filter = new FileNameExtensionFilter(
		// "Image", "png", "jpg");
		// chooser.setFileFilter(filter);

		int returnVal = chooser.showOpenDialog(getParent());
		if (returnVal == JFileChooser.APPROVE_OPTION) {

				// make HASHES from files
				File patch = chooser.getSelectedFile();

				

					
					File file = new File(patch.getPath());

					// преобразуем в байты
					long file_len = file.length();
					if (file_len > Integer.MAX_VALUE) {
				//		table_Model.addRow(new Object[] { "",
				//				Lang.getInstance().translate("length very long") + " - " + file_name });
				//		continue;
					}
					byte[] fileInArray = new byte[(int) file.length()];
					FileInputStream f = null;
					try {
						f = new FileInputStream(patch.getPath());
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				//		table_Model.addRow(new Object[] { "",
				//				Lang.getInstance().translate("error streaming") + " - " + file_name });
				//		continue;
					}
					try {
						f.read(fileInArray);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				//		table_Model.addRow(new Object[] { "",
				//				Lang.getInstance().translate("error reading") + " - " + file_name });
				//		continue;
					}
					try {
						f.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				//		continue;
					}

					/// HASHING
					String hashe = Base58.encode(Crypto.getInstance().digest(fileInArray));
				//	table_Model.addRow(new Object[] { hashes,
				//			Lang.getInstance().translate("from file ") + file_name });
					this.searchTextField_SearchToolBar_LeftPanel.setText(hashe);

			
		
		
		
		
		}
				
				
			}
		
			
			
		

	}
	
	


