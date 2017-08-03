package gui.library;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.apache.commons.lang3.math.NumberUtils;

import lang.Lang;
import utils.MenuPopupUtil;

public class My_INN_JTextField extends JTextField {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private My_INN_JTextField th;
	private Color text_Color;

	public My_INN_JTextField(){
		super();
		th = this;
		th.setToolTipText(Lang.getInstance().translate("Must be 10 or 12 numbers"));
		text_Color = this.getForeground();
		th.setForeground(Color.RED);
		MenuPopupUtil.installContextMenu(this);
		
		addCaretListener(new CaretListener(){

			@Override
			public void caretUpdate(CaretEvent arg0) {
				if (!NumberUtils.isNumber(th.getText()))	{
					th.setForeground(Color.RED);
				return ;
				}
				if (th.getText().length() != 10 && th.getText().length()!=12) {
					
					th.setForeground(Color.RED);
					return ;
				}
				th.setForeground(text_Color);
			}
			
			
		});
	
		
	}

}
