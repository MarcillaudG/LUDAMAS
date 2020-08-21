package fr.irit.smac.planification.datadisplay.ui;

import java.util.ArrayList;
import java.util.Collection;

import fr.irit.smac.planification.agents.DataAgent;
import fr.irit.smac.planification.agents.DataMorphAgent;
import fr.irit.smac.planification.datadisplay.interfaces.Modifiable;
import fr.irit.smac.planification.datadisplay.model.CAVModel;
import fr.irit.smac.planification.system.CAV;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class DataMorphAgentDisplay implements Modifiable {
	
	/* Nombre de lignes utilisees dans le gridpane (commence a 1 en raison de la premiere ligne) */
	private int usedLines = 1;
	private CAVModel cavModel;
	private GridPane grid;
	private VBox root;
	private ScrollPane scrollPane;
	
	/* Nom du DataAgent associe */
	private String dataAgentName;
	
	/* Constantes */
	private static final Color grey = Color.rgb(100, 100, 100);
	private static final String BOLDSTYLE = "-fx-font-weight: bold";
	
	public DataMorphAgentDisplay(CAVModel cavModel, String dataAgentName) {
		this.cavModel = cavModel;
		this.dataAgentName = dataAgentName;
		start();
	}
	
	/* Start
	 * Construction des differents composants de l'affichage des DataMorphAgents
	 */
	public void start() {
		grid = new GridPane();
		grid.setBorder(new Border(new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
				BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
				null, new BorderWidths(0.5), null)));
		
		buildFirstLigneDataMorphAgent(grid);

		root = new VBox();
		root.setPadding(new Insets(15, 15, 15, 15));
		root.getChildren().add(grid);

		StackPane stack = new StackPane();
		stack.getChildren().add(root);

		scrollPane = new ScrollPane();
		scrollPane.setContent(stack);
		scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

		stack.minWidthProperty().bind(Bindings.createDoubleBinding(() -> scrollPane.getViewportBounds().getWidth(),
				scrollPane.viewportBoundsProperty()));
		
		scrollPane.setPrefSize(1100, 500);
	}
	
	/* BuildFirstLigneDataMorphAgent
	 * Construit la premiere ligne du gridpane de l'affichage des agents
	 * Les proprietes des DataMorphAgents affichees sont
	 * Nom/Value/Usefulness/Linear Formuma/MorphValue/TrueValue
	 */
	private void buildFirstLigneDataMorphAgent(GridPane grid) {
		Label labelId = new Label("Name");
		buildBoldLabel(labelId);
		grid.add(labelId, 0,  0);
		Label labelValue = new Label("Value");
		buildBoldLabel(labelValue);
		grid.add(labelValue, 1,  0);
		Label labelUsefulness = new Label("Usefulness");
		buildBoldLabel(labelUsefulness);
		grid.add(labelUsefulness, 2,  0);
		Label labelLinearFormula = new Label("Linear formula");
		buildBoldLabel(labelLinearFormula);
		labelLinearFormula.setPrefWidth(260);
		grid.add(labelLinearFormula, 3,  0);
		Label labelMorphValue = new Label("Morph value");
		buildBoldLabel(labelMorphValue);
		grid.add(labelMorphValue, 4,  0);
		Label labelTrueValue = new Label("True value");
		buildBoldLabel(labelTrueValue);
		grid.add(labelTrueValue, 5,  0);
	}

	/* BuildCellule 
	 * Param in: VBox to build
	 * Sets size, alignment and border of the vbox given
	 */
	private void buildCellule(VBox box) {
		box.setPrefSize(160, 40);
		box.setAlignment(Pos.CENTER);
		box.setBorder(
				new Border(new BorderStroke(grey, grey, grey, grey, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
						BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null, new BorderWidths(0.5), null)));
	}

	/* BuildLabel
	 * Param in: Label to build
	 * Sets size, alignment and border of the label given
	 */
	private void buildLabel(Label label) {
		label.setAlignment(Pos.CENTER);
		label.setPrefSize(160, 40);
		label.setBorder(
				new Border(new BorderStroke(grey, grey, grey, grey, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
						BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null, new BorderWidths(1), null)));
	}

	/* BuildBoldLabel
	 * Param in: Label to build
	 * Sets size, bold style, alignment and border of the label given
	 */
	private void buildBoldLabel(Label label) {
		buildLabel(label);
		label.setStyle(BOLDSTYLE);
	}
	
	/* BuildLignesDataMorphAgent 
	 * Prend en parametre le gridpane ou les informations seront affichees et la 
	 * collection contenant les dataMorphAgents
	 * Construit le gridpane depuis la collection
	 */
	private void buildLignesDataMorphAgent(GridPane grid, Collection<? extends DataMorphAgent> dataMorphAgents) {
		CAV cav = cavModel.getCav();
		for(DataMorphAgent dataMorphAgent : dataMorphAgents) {
			/* Name */
			VBox celluleAgentName = new VBox();
			buildCellule(celluleAgentName);
			Label labelAgentName = new Label(dataMorphAgent.getName());
			labelAgentName.setStyle(BOLDSTYLE);
			celluleAgentName.getChildren().add(labelAgentName);
			grid.add(celluleAgentName, 0, usedLines);
			/* value */
			Label labelValue = new Label();
			try {
				labelValue.setText(String.valueOf(dataMorphAgent.getValue()));
			} catch(NullPointerException e) {
				labelValue.setText("No value");
			}
			buildLabel(labelValue);
			grid.add(labelValue, 1,  usedLines);
			/* usefulness */
			Label labelUsefulness = new Label(String.valueOf(dataMorphAgent.getUsefulness()));
			buildLabel(labelUsefulness);
			grid.add(labelUsefulness, 2, usedLines);
			/* linear formula */
			Label labelLinearFormula = new Label(dataMorphAgent.getLinearFormula());
			buildLabel(labelLinearFormula);
			labelLinearFormula.setPrefWidth(260);
			grid.add(labelLinearFormula, 3, usedLines);
			/* morph value */
			Label labelMorphValue = new Label(String.valueOf(dataMorphAgent.getMorphValue()));
			buildLabel(labelMorphValue);
			grid.add(labelMorphValue, 4, usedLines);
			/* dataMorphAgent button */
			Label labelTrueValue = new Label(String.valueOf(cav.getTrueValueForInput(dataMorphAgent.getInput())));
			buildLabel(labelTrueValue);
			grid.add(labelTrueValue, 5, usedLines);
			usedLines++;
		}
	}
	
	/* Update
	 * Implente depuis Modifiable
	 * Reconstruit un gridpane pour mettre a jour l'etat actuel 
	 * des DataMorphAgents
	 */
	@Override
	public void update() {
		Thread taskThread = new Thread(new Runnable() {
			@Override
			public void run() {
				CAV cav = cavModel.getCav();
				DataAgent dataAgent = cav.getDataAgentWithName(dataAgentName);
				Collection<? extends DataMorphAgent> dataMorphAgents = new ArrayList<>(dataAgent.getAllMorphs());
				
				Platform.runLater(new Runnable() {
					@Override 
					public void run() {
						root.getChildren().remove(grid);
						grid.setVisible(false);
						GridPane newGrid = new GridPane();
						newGrid.setBorder(new Border(new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK,
								Color.BLACK, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
								BorderStrokeStyle.SOLID, null, new BorderWidths(0.5), null)));
						usedLines = 1;
						buildFirstLigneDataMorphAgent(newGrid);
						buildLignesDataMorphAgent(newGrid, dataMorphAgents);
						root.getChildren().add(newGrid);
						grid = newGrid;
						/* Le travail du thread est termine, on rend un token
						 * au semaphore du cav modele
						 */
						cavModel.V();
					}
				});
			}
		});
		taskThread.start();
		
	}

	public ScrollPane getScrollPane() {
		return scrollPane;
	}
}
