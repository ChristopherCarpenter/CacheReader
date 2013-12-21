package com.sk.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.sk.cache.DataSource;
import com.sk.cache.fs.CacheSystem;
import com.sk.gui.GridPainter.GridGetter;
import com.sk.gui.GridPainter.Side;
import com.sk.wrappers.ObjectDefinition;
import com.sk.wrappers.region.LocalObject;
import com.sk.wrappers.region.Region;
import com.sk.wrappers.region.RegionLoader;

public class RegionViewer {

	private static Region region;
	private static int plane;
	private static boolean shouldShowObjects = false;

	public static void main(String[] args) throws FileNotFoundException {
		CacheSystem sys = new CacheSystem(new DataSource(new File("/Users/Strikeskids/jagexcache/Runescape/LIVE")));
		final RegionLoader rl = new RegionLoader(sys);
		final JFrame frame = new JFrame("Regions");
		frame.getContentPane().setLayout(new BorderLayout());
		JPanel top = new JPanel();
		final JTextField xval = new JTextField(5);
		final JTextField yval = new JTextField(5);
		final JTextField pval = new JTextField(5);
		final DefaultListModel<ObjectDefinition> objectModel = new DefaultListModel<>();
		JButton update = new JButton("Update");
		update.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						region = rl.load(Integer.parseInt(xval.getText()), Integer.parseInt(yval.getText()));
						plane = Integer.parseInt(pval.getText());
						frame.repaint();
					}
				});
			}
		});
		top.add(xval);
		top.add(yval);
		top.add(pval);
		top.add(update);
		frame.getContentPane().add(top, BorderLayout.NORTH);
		frame.getContentPane().add(new GridPainter(new GridGetter() {
			@Override
			public Color getColor(int x, int y, Side side) {
				if (region == null)
					return null;
				int flag = region.flags[plane][x][y];
				if ((flag & 0x200100) != 0 && side == Side.CENTER)
					return Color.red;
				if (side == Side.NORTH && (flag & 0x2) != 0)
					return Color.green;
				if (side == Side.EAST && (flag & 0x8) != 0)
					return Color.blue;
				if (side == Side.SOUTH && (flag & 0x20) != 0)
					return Color.yellow;
				if (side == Side.WEST && (flag & 0x80) != 0)
					return Color.orange;
				return null;
			}

			@Override
			public void hoverCell(int x, int y) {
				if (region == null)
					return;
				objectModel.clear();
				for (LocalObject o : region.objects.getObjects()) {
					if (o.x == x && o.y == y && o.plane == plane) {
						objectModel.addElement(region.getLoader().objectDefinitionLoader.load(o.id));
					}
				}
			}
		}, Region.width, Region.height), BorderLayout.CENTER);
		if (shouldShowObjects)
			frame.getContentPane().add(new JScrollPane(new JList<>(objectModel)), BorderLayout.EAST);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

}
