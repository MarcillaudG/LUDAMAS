package fr.irit.smac.planification.datadisplay.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.util.Collection;

import fr.irit.smac.planification.agents.DataAgent;
import fr.irit.smac.planification.datadisplay.controller.DataAgentDisplayController;
import fr.irit.smac.planification.datadisplay.interfaces.Modifiable;
import fr.irit.smac.planification.datadisplay.model.CAVModel;
import fr.irit.smac.planification.system.CAV;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
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

public class DataAgentDisplay implements Modifiable{
	
	private GridPane grid;
	private VBox root;
	private CAVModel cavModel;
	private ScrollPane scrollPane;
	/* Nombre de lignes utilisees dans le gridPane (commence a 1 en raison de la premiere ligne) */
	private int usedLines = 1;
	private DataAgentDisplayController controller;
	
	/* Constants */
	private static final Color grey = Color.rgb(100, 100, 100);
	private static final String BOLDSTYLE = "-fx-font-weight: bold";
	
	public DataAgentDisplay(CAVModel cavModel) {
		this.cavModel = cavModel;
		start();
	}
	
	/* Start
	 * Construction des composants pour l'affichage des DataAgents
	 */
	public void start() {
		
		grid = new GridPane();
		grid.setBorder(new Border(new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
				BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
				null, new BorderWidths(0.5), null)));

		buildFirstLigneDataAgent(grid);

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

	/* BuildCellule 
	 * Param in: VBox to build
	 * Sets size, alignment and border of the vbox given
	 */
	private void buildCellule(VBox box) {

		box.setPrefSize(120, 40);
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
		label.setPrefSize(120, 40);
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

	/* Displayed attributes of a DataAgent
	 * Name/Value/Associate coalitionAgent/Associate DataMorphAgents
	 */
	private void buildFirstLigneDataAgent(GridPane grid) {

		Label labelId = new Label("Agent Name");
		buildBoldLabel(labelId);
		grid.add(labelId, 0, 0);
		Label labelValue = new Label("Value");
		buildBoldLabel(labelValue);
		labelValue.setPrefWidth(315);
		grid.add(labelValue, 1, 0);
		Label labelCoalition = new Label("Coalition");
		buildBoldLabel(labelCoalition);
		labelCoalition.setPrefWidth(310);
		grid.add(labelCoalition, 2, 0);
		Label labelDataMorph = new Label("DataMorphAgents");
		buildBoldLabel(labelDataMorph);
		labelDataMorph.setPrefWidth(310);
		grid.add(labelDataMorph, 3, 0);
	}

	/* BuildLignesDataAgent
	 * Prend en parametre le gridpane ou on affiche les proprietes des agents
	 * et la collection contenant tous les dataAgents a afficher
	 * Ajoute un a un dans l'affichage les agents et leur proprietes
	 */
	private void buildLignesDataAgent(GridPane grid, Collection<DataAgent> allDataAgents) {
		for(DataAgent dataAgent : allDataAgents) {
			/* Name */
			VBox labelNameBox = new VBox();
			buildCellule(labelNameBox);
			Label labelAgentName = new Label(dataAgent.getDataName());
			labelAgentName.setStyle(BOLDSTYLE);
			labelNameBox.getChildren().add(labelAgentName);
			grid.add(labelNameBox, 0, usedLines);
			/* Value */
			Label labelValue = new Label(String.valueOf(dataAgent.askValue()));
			buildLabel(labelValue);
			labelValue.setPrefWidth(315);
			grid.add(labelValue, 1, usedLines);
			/* Coalition */
			Label labelCoalition = new Label();
			buildLabel(labelCoalition);
			labelCoalition.setPrefWidth(310);
			try {
				labelCoalition.setText(dataAgent.getCoalition().getData());
			} catch (NullPointerException e) {
				labelCoalition.setText("No coalition");
			}
			grid.add(labelCoalition, 2, usedLines);
			/* DataMorphButton */
			VBox buttonBox = new VBox();
			buildCellule(buttonBox);
			buttonBox.setPrefWidth(310);
			Button buttonOpenDataMorph = new Button("DataMorphs");
			buttonOpenDataMorph.setPrefSize(150, 30);
			buttonOpenDataMorph.setId(dataAgent.getDataName());
			buttonBox.getChildren().add(buttonOpenDataMorph);
			buttonOpenDataMorph.setOnAction(controller);
			grid.add(buttonBox, 3, usedLines);
			usedLines++;
		}
	}

	/* Update 
	 * Implente depuis Modifiable 
	 * Re-cree un gridpane pour mettre a jour les donnees affiches des DataAgents
	 */
	public void update() {

		Thread taskThread = new Thread(new Runnable() {
			@Override
			public void run() {
				CAV cav = cavModel.getCav();
				Collection<DataAgent> allDataAgents = cav.getAllDataAgent();
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
						buildFirstLigneDataAgent(newGrid);
						buildLignesDataAgent(newGrid, allDataAgents);
						root.getChildren().add(newGrid);
						grid = newGrid;
					}
				});
				/* Le travail du thread est termine, on rend un token
				 * au semaphore du cav modele
				 */
				cavModel.V();
			}
		});
		taskThread.start();
	}
	
	public void setController(DataAgentDisplayController controller) {
		this.controller = controller;
	}
	
	public void setCavModel(CAVModel cavModel) {
		this.cavModel = cavModel;
	}
	
	public CAVModel getCavModel() {
		return cavModel;	
	}
	
	public VBox getRoot() {
		return root;
	}
	
	public ScrollPane getScrollPane() {
		return scrollPane;
	}

}
