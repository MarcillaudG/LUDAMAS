package fr.irit.smac.planification.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

import fr.irit.smac.planification.system.CAV;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.JSeparator;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.imageio.ImageIO;
import javax.swing.AbstractListModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;

import java.awt.Component;
import javax.swing.Box;
import java.awt.Font;
import java.awt.Image;

import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

public class MainUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8411409608156984380L;
	private JPanel contentPane;
	private JTextField txtFC;
	private BufferedImage image;
	private JSpinner spinCopy;
	//private JFileChooser jfc;
	private JSpinner spinVarEff;
	private JSpinner spinEff;
	private JSpinner spinVar;
	private JSpinner spinSitu;
	private JTextField txtfNotNoised;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainUI frame = new MainUI();
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
	public MainUI() {
		setTitle("LUDA");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 767, 598);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNumberOfVar = new JLabel("Number of Var");
		lblNumberOfVar.setHorizontalAlignment(SwingConstants.CENTER);
		lblNumberOfVar.setBounds(326, 218, 82, 14);
		contentPane.add(lblNumberOfVar);

		JButton btnRunDataset = new JButton("Run Dataset");
		btnRunDataset.setEnabled(false);
		btnRunDataset.setBounds(87, 456, 115, 23);
		btnRunDataset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if(txtfNotNoised.getText().equals("")) {
					CAV cav = new CAV("CAV", (Integer) spinEff.getValue(), (Integer)spinSitu.getValue(), (Integer)spinVarEff.getValue(), (Integer)spinCopy.getValue(), txtFC.getText());
					new VisuEffector("Test", (Integer) spinEff.getValue(), cav);
				}
				else {
					CAV cav = new CAV("CAV", (Integer) spinEff.getValue(), (Integer)spinSitu.getValue(), (Integer)spinVarEff.getValue(), (Integer)spinCopy.getValue(), txtFC.getText(), txtfNotNoised.getText());
					new VisuEffector("Test", (Integer) spinEff.getValue(), cav);
				}
				dispose();
			}
		});
		contentPane.add(btnRunDataset);

		JLabel lblPath = new JLabel("Choose the path of the dataset");
		lblPath.setHorizontalAlignment(SwingConstants.CENTER);
		lblPath.setBounds(30, 222, 170, 14);
		contentPane.add(lblPath);


		try {                
			image = ImageIO.read(new File("C:\\Users\\gmarcill\\git\\LUDAMAS\\LUDAMAS\\src\\fr\\irit\\smac\\img\\luda.jpg"));
		} catch (IOException ex) {
			// handle exception...
		}

		JLabel picLabel = new JLabel();
		picLabel.setBounds(280, 11, 184, 78);
	Image dimg = image.getScaledInstance(picLabel.getWidth(), picLabel.getHeight(),
				Image.SCALE_SMOOTH);
		ImageIcon imageIcon = new ImageIcon(dimg);
		picLabel.setIcon(imageIcon);
		getContentPane().add(picLabel);

		JButton btnRunSynthetic = new JButton("Run Synthetic");
		btnRunSynthetic.setBounds(465, 456, 115, 23);
		btnRunSynthetic.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CAV cav = new CAV("CAV", (Integer) spinEff.getValue(), (Integer)spinSitu.getValue(), (Integer)spinVarEff.getValue(), (Integer)spinCopy.getValue(), (Integer) spinVar.getValue());
				new VisuEffector("Test", (Integer) spinEff.getValue(), cav);
				dispose();
			}
		});
		contentPane.add(btnRunSynthetic);

		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(292, 470, -61, -469);
		contentPane.add(separator);

		JLabel lblNumberOfCopy = new JLabel("Number of copies");
		lblNumberOfCopy.setHorizontalAlignment(SwingConstants.CENTER);
		lblNumberOfCopy.setBounds(493, 218, 96, 14);
		contentPane.add(lblNumberOfCopy);

		JLabel lblNbEff = new JLabel("Number of effectors");
		lblNbEff.setHorizontalAlignment(SwingConstants.CENTER);
		lblNbEff.setBounds(326, 274, 115, 14);
		contentPane.add(lblNbEff);

		JLabel lblnbVarEff = new JLabel("Number of Var per effector");
		lblnbVarEff.setHorizontalAlignment(SwingConstants.CENTER);
		lblnbVarEff.setBounds(465, 274, 153, 14);
		contentPane.add(lblnbVarEff);

		JLabel lblNoise = new JLabel("Choose the noise for your experiment");
		lblNoise.setHorizontalAlignment(SwingConstants.CENTER);
		lblNoise.setBounds(292, 354, 193, 23);
		contentPane.add(lblNoise);

		JComboBox cmb = new JComboBox();
		cmb.setModel(new DefaultComboBoxModel(new String[] {"None", "Gaussian"}));
		cmb.setBounds(326, 388, 96, 22);
		contentPane.add(cmb);

		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Initial", "None"}));
		comboBox.setBounds(558, 388, 96, 22);
		contentPane.add(comboBox);

		JLabel lblCopyfunction = new JLabel("Copy Function");
		lblCopyfunction.setHorizontalAlignment(SwingConstants.CENTER);
		lblCopyfunction.setBounds(552, 358, 115, 14);
		contentPane.add(lblCopyfunction);

		Component verticalStrut = Box.createVerticalStrut(20);
		verticalStrut.setBounds(258, 11, -5, 448);
		contentPane.add(verticalStrut);

		txtFC = new JTextField();
		txtFC.setBounds(30, 248, 263, 20);
		contentPane.add(txtFC);
		JButton btnFC = new JButton("Open...");
		btnFC.setBounds(204, 218, 89, 23);
		btnFC.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				jfc.setDialogTitle("Choose the path of the dataset ");
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

				int returnValue = jfc.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					if (jfc.getSelectedFile().isFile()) {
						txtFC.setText(jfc.getSelectedFile().toString());
						if(txtFC.getText().contains(".csv")){
							btnRunDataset.setEnabled(true);
						}
						else {
							btnRunDataset.setEnabled(false);
						}
					}
				}
			}
		});
		contentPane.add(btnFC);

		JTextArea txtrChooseTheExperiment = new JTextArea();
		txtrChooseTheExperiment.setFont(new Font("Tahoma", Font.BOLD, 15));
		txtrChooseTheExperiment.setLineWrap(true);
		txtrChooseTheExperiment.setWrapStyleWord(true);
		txtrChooseTheExperiment.setEditable(false);
		txtrChooseTheExperiment.setRows(8);
		txtrChooseTheExperiment.setText("Choose the experiment you want: you can use either the synthetic generator or a chosen dataset.\r\nYou have several possiblities if you want to add noise to the data.\r\nWith the synthetic generator or if your dataset only have one instance for each data, you can precise the number of instance for each data. ");
		txtrChooseTheExperiment.setBounds(20, 96, 710, 111);
		txtrChooseTheExperiment.setBackground(new Color(240, 240, 240));
		contentPane.add(txtrChooseTheExperiment);

		spinCopy = new JSpinner();
		spinCopy.setModel(new SpinnerNumberModel(3, 0, 100, 1));
		spinCopy.setBounds(503, 243, 50, 20);
		contentPane.add(spinCopy);

		spinVarEff = new JSpinner();
		spinVarEff.setModel(new SpinnerNumberModel(3, 1, 1000, 1));
		spinVarEff.setBounds(505, 300, 50, 20);
		contentPane.add(spinVarEff);

		spinEff = new JSpinner();
		spinEff.setModel(new SpinnerNumberModel(1, 1, 100, 1));
		spinEff.setBounds(349, 300, 59, 20);
		contentPane.add(spinEff);

		spinVar = new JSpinner();
		spinVar.setModel(new SpinnerNumberModel(10, 10, 1000, 1));
		spinVar.setBounds(349, 243, 59, 20);
		contentPane.add(spinVar);

		JLabel lblNumberOfSituations = new JLabel("Number of situations");
		lblNumberOfSituations.setBounds(622, 218, 108, 14);
		contentPane.add(lblNumberOfSituations);

		spinSitu = new JSpinner();
		spinSitu.setModel(new SpinnerNumberModel(1, 1, 100, 1));
		spinSitu.setBounds(637, 243, 59, 20);
		contentPane.add(spinSitu);

		JLabel label = new JLabel("Choose the path of the dataset");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(30, 304, 170, 14);
		contentPane.add(label);

		JButton btnNotNoised = new JButton("Open...");
		btnNotNoised.setBounds(204, 300, 89, 23);
		btnNotNoised.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				jfc.setDialogTitle("Choose the path of the not noised dataset ");
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

				int returnValue = jfc.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					if (jfc.getSelectedFile().isFile()) {
						txtfNotNoised.setText(jfc.getSelectedFile().toString());
						if(txtfNotNoised.getText().contains(".csv")){
							btnRunDataset.setEnabled(true);
						}
						else {
							btnRunDataset.setEnabled(false);
						}
					}
				}
			}
		});
		contentPane.add(btnNotNoised);

		txtfNotNoised = new JTextField();
		txtfNotNoised.setBounds(30, 330, 263, 20);
		contentPane.add(txtfNotNoised);

	}
}
