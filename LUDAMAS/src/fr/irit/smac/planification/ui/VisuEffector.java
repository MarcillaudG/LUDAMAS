package fr.irit.smac.planification.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import fr.irit.smac.lxplot.LxPlot;
import fr.irit.smac.planification.system.CAV;

import javax.swing.JScrollPane;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class VisuEffector extends JFrame {

	private JPanel contentPane;
	
	private List<MatrixUITable> matrices;
	private Map<String, Integer> mapGrid;
	
	private CAV cav;

	private JPanel panel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VisuEffector frame = new VisuEffector("TEST", 7, null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public VisuEffector(String name, int nbEffector, CAV cav) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 769, 521);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("Run");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmRun = new JMenuItem("Run");
		mnNewMenu.add(mntmRun);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(1, 0, 0, 0));

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane);
		
		panel = new JPanel();
		scrollPane.setViewportView(panel);
		panel.setLayout(new GridLayout(nbEffector/2+1, 2, 0, 0));
		this.matrices = new ArrayList<>();
		this.mapGrid = new TreeMap<>();
		this.setVisible(true);
		this.cav = cav;
		cav.setMainWindow(this);
		mntmRun.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread() {
					public void run() {

						int i =1;
						cav.generateNewValues(i);
						while(i < 1000) {
							System.out.println("CYCLE : "+i);
							cav.manageSituation(i);
							i++;
							cav.generateNewValues(i);
							pack();
							panel.repaint();
							contentPane.repaint();

						}
					}
				};
				t.start();
			}
		});
		Thread t = new Thread() {
			public void run() {

				int i =1;
				cav.generateNewValues(i);
				while(i < 1000) {
					System.out.println("CYCLE : "+i);
					cav.manageSituation(i);
					i++;
					cav.generateNewValues(i);
					pack();
					panel.repaint();
					contentPane.repaint();
				}
			}
		};
		t.start();
		
		
	}

	public void addEff(MatrixUITable myUI) {
		this.panel.add(myUI, this.matrices.size()/2,this.matrices.size()%2);
		this.mapGrid.put(myUI.getName(), this.mapGrid.size());
		this.matrices.add(myUI);
	}
	
	public void addMatrix(MatrixUITable myUI) {
		this.panel.add(myUI, this.matrices.size()/2,this.matrices.size()%2);
		this.mapGrid.put(myUI.getName(), this.mapGrid.size());
		this.matrices.add(myUI);
	}

}
