package gui;

import java.awt.Component;
import java.math.BigDecimal;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.mapdb.Fun.Tuple2;
import org.mapdb.Fun.Tuple3;

import controller.Controller;
import core.item.assets.AssetCls;
import utils.NumberAsString;
import utils.Pair;

public class BalanceRenderer implements ListCellRenderer<Pair<Tuple2<String, Long>, Tuple3<BigDecimal, BigDecimal, BigDecimal>>> 
{
	private DefaultListCellRenderer defaultRenderer;
	
	public BalanceRenderer()
	{
		this.defaultRenderer = new DefaultListCellRenderer();
	}
	
	public Component getListCellRendererComponent(JList<? extends Pair<Tuple2<String, Long>, Tuple3<BigDecimal, BigDecimal, BigDecimal>>> list, Pair<Tuple2<String, Long>, Tuple3<BigDecimal, BigDecimal, BigDecimal>> value, int index, boolean isSelected, boolean cellHasFocus) 
	{
		JLabel renderer = (JLabel) this.defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		if(value != null)
		{
			AssetCls asset = Controller.getInstance().getAsset(value.getA().b);		
			renderer.setText("(" + asset.getKey() + ") " + asset.getName() + " - " + NumberAsString.getInstance().numberAsString(value.getB()));
		}
		
		return renderer;
	}
}