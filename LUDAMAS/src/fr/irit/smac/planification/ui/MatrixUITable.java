package fr.irit.smac.planification.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import fr.irit.smac.planification.matrix.Input;
import fr.irit.smac.planification.matrix.Matrix;
import fr.irit.smac.planification.ui.MatrixUI.MorphColumnCellRenderer;
import fr.irit.smac.planification.ui.MatrixUI.StatusColumnCellRenderer;

public class MatrixUITable extends JTable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8872734280555285585L;
	private JTable tableMorph;

	private Matrix mat;
	private DefaultTableModel tableModel ;


	private DefaultTableModel tableMorphModel ;

	private List<String> inputs;
	private List<String> datas;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					List<String> exem = new ArrayList<String>();
					for(int i =0; i < 8;i++) {
						exem.add("IN:"+i);
					}
					MatrixUI frame = new MatrixUI(new Matrix(exem));
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
	public MatrixUITable(Matrix mat) {
		this.mat = mat;
		setName(mat.getname());
		this.datas = new ArrayList<>();
		this.inputs = new ArrayList<>();
		setBounds(100, 100, 450, 300);
		tableModel = new DefaultTableModel();
		this.tableMorphModel = new DefaultTableModel();
		//table = new JTable(this.mat.getNbInput(),3);
		this.setModel(tableModel);

		this.tableMorph = new JTable(this.tableMorphModel);

		String [] data = {""};
		this.tableModel.addRow(data);
		this.tableModel.addColumn("inputs");

		this.tableMorphModel.addRow(data);
		this.tableMorphModel.addColumn("inputs");
		/*this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("Matrix");
        this.setContentPane(table);
        this.pack();
        this.setVisible(true);*/
		this.setVisible(true);
		this.update();
	}

	public void update() {
		//this.table = new JTable(this.mat.getInput().size()+1, this.mat.getNbData()+1);
		//contentPane.add(table, BorderLayout.CENTER);
		int row = 1;
		int column = 1;
		int nbRow = 1;

		//System.out.println("NBINPUT:"+this.mat.getNbInput());
		//System.out.println("NBData:"+this.mat.getNbData());
		// look after rows
		for(Input input :this.mat.getInput()) {
			if(nbRow >= this.tableModel.getRowCount()) {
				String [] data = {input.getData()};
				this.tableModel.addRow(data);
				this.tableMorphModel.addRow(data);
			}
			nbRow++;
		}
		List<String> dataTmp = new ArrayList<>(this.mat.getData());
		dataTmp.removeAll(this.datas);

		for(String data : dataTmp) {
			this.tableModel.addColumn(data);
			this.tableMorphModel.addColumn(data);
			this.datas.add(data);
		}

		for(int i =0; i < this.tableModel.getColumnCount();i++) {
			this.getColumnModel().getColumn(i).setCellRenderer(new StatusColumnCellRenderer());
			this.tableMorph.getColumnModel().getColumn(i).setCellRenderer(new MorphColumnCellRenderer());
		}
		// look after columns
		for(String data : this.mat.getData()) {
			boolean exist = false;
			/*for(int i = 0; i < this.tableModel.getColumnCount() && !exist;i++) {
				if(data.equals(this.tableModel.getValueAt(0, i))) {
					exist = true;
					//this.table.getColumnModel().addColumn(new TableColumn());
				}
			}
			if(!exist) {
				this.tableModel.addColumn(data);
			}*/
			this.tableModel.setValueAt(data, 0, column);
			this.tableMorphModel.setValueAt(data, 0, column);
			column++;
		}
		//System.out.println("ROW:"+this.tableModel.getRowCount());
		//System.out.println("Column:"+this.tableModel.getColumnCount());

		// update the values
		for(Input input :this.mat.getInput()) {
			//this.table.setValueAt(input.getData(), row, 0);
			column = 1;
			for(String data : this.mat.getData()) {
				this.tableModel.setValueAt(this.mat.getMatrix().get(input).get(data), row, column);
				this.tableMorphModel.setValueAt(this.mat.getMorphValue(input.getData(),data), row, column);
				column++;
			}
			row++;
		}
	}

	public class ScrollableJTable extends JPanel {

		private JTable table;
		public ScrollableJTable(int row, int col) {
			initializeUI(row,col);
		}

		private void initializeUI(int row, int col) {
			setLayout(new BorderLayout());
			setPreferredSize(new Dimension(500, 200));

			table = new JTable(row, col);

			// Turn off JTable's auto resize so that JScrollPane will show a
			// horizontal scroll bar.
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

			JScrollPane pane = new JScrollPane(table);
			add(pane, BorderLayout.CENTER);
			this.setOpaque(true);


		}

		public void setTable(JTable table) {
			this.table = table;
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			JScrollPane pane = new JScrollPane(table);
			add(pane, BorderLayout.CENTER);
			this.setOpaque(true);

		}

	}

	public class StatusColumnCellRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2148689509691682788L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

			//Cells are by default rendered as a JLabel.
			JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

			//Get the status for the current row.
			TableModel tableModel = (TableModel) table.getModel();
			if(row > 0 && col > 0 && (value == null || ((String)table.getValueAt(0, col)).equals((CharSequence) table.getValueAt(row, 0)))) {
				l.setBackground(Color.GRAY);
			}
			else
			if(value != null) {
				if (value.equals(1.0f)) {
					l.setBackground(Color.GREEN);
				}
				else {
						if(value instanceof Float &&  (float)value > 0.0f){
							l.setBackground(Color.ORANGE);
						}
						else {
							l.setBackground(Color.WHITE);
						}
				}
			}
			//Return the JLabel which renders the cell.
			return l;

		}
	}

	public class MorphColumnCellRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3605652632598480699L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

			//Cells are by default rendered as a JLabel.
			JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

			//Get the status for the current row.
			TableModel tableModel = (TableModel) table.getModel();
			if(row > 0 && col > 0 && (value == null || ((String)table.getValueAt(0, col)).equals((CharSequence) table.getValueAt(row, 0)))) {
				l.setBackground(Color.GRAY);
			}
			else
			if(value != null) {
				if (value.equals(1.0f)) {
					l.setBackground(Color.GREEN);
				}
				else {
					if(value instanceof Float &&  (float)value > 0.0f){
						l.setBackground(Color.ORANGE);
					}
					else {
						l.setBackground(Color.WHITE);
					}
				}
			}
			//Return the JLabel which renders the cell.
			return l;

		}
	}

}
