package fr.irit.smac.planification.datadisplay.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import fr.irit.smac.planification.agents.DataAgent;
import fr.irit.smac.planification.datadisplay.controller.CloseModifiableController;
import fr.irit.smac.planification.datadisplay.controller.DataAgentDisplayController;
import fr.irit.smac.planification.datadisplay.interfaces.Modifiable;
import fr.irit.smac.planification.datadisplay.model.CAVModel;
import fr.irit.smac.planification.system.CAV;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
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
import javafx.stage.Stage;

public class DataAgentDisplay implements Modifiable {
	
	private static final Color grey = Color.rgb(100, 100, 100);
	private static final String BOLDSTYLE = "-fx-font-weight: bold";
	private GridPane grid;
	private VBox root;
	private CAVModel cavModel;
	private int usedLines = 1;
	private DataAgentDisplayController controller;
	private Stage primaryStage;
	
	public DataAgentDisplay(CAVModel cavModel) {
		this.cavModel = cavModel;
		this.controller = new DataAgentDisplayController(cavModel);
		this.primaryStage = new Stage();
		start();
	}
	
	public void start() {
		
		primaryStage.setTitle("DataAgentsDisplay");
		primaryStage.setOnCloseRequest(new CloseModifiableController(cavModel, this));
		grid = new GridPane();
		grid.setBorder(new Border(new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
				BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
				null, new BorderWidths(0.5), null)));

		buildFirstLigneDataAgent(grid);
		buildLignesDataAgent(grid);

		root = new VBox();
		root.setPadding(new Insets(15, 15, 15, 15));
		root.getChildren().add(grid);

		StackPane stack = new StackPane();
		stack.getChildren().add(root);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(stack);
		scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

		stack.minWidthProperty().bind(Bindings.createDoubleBinding(() -> scrollPane.getViewportBounds().getWidth(),
				scrollPane.viewportBoundsProperty()));

		primaryStage.setScene(new Scene(scrollPane, 645, 500));
		primaryStage.show();
	}

	private void buildCellule(VBox box) {

		box.setPrefSize(120, 40);
		box.setAlignment(Pos.CENTER);
		box.setBorder(
				new Border(new BorderStroke(grey, grey, grey, grey, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
						BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null, new BorderWidths(0.5), null)));
	}
	
	private void buildLabel(Label label) {
		label.setAlignment(Pos.CENTER);
		label.setPrefSize(120, 40);
		label.setBorder(
				new Border(new BorderStroke(grey, grey, grey, grey, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
						BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null, new BorderWidths(1), null)));
	}

	private void buildBoldLabel(Label label) {

		buildLabel(label);
		label.setStyle(BOLDSTYLE);
	}

	private void buildFirstLigneDataAgent(GridPane grid) {

		Label labelId = new Label("Agent Name");
		buildBoldLabel(labelId);
		grid.add(labelId, 0, 0);
		Label labelValue = new Label("Value");
		buildBoldLabel(labelValue);
		grid.add(labelValue, 1, 0);
		Label labelCoalition = new Label("Value");
		buildBoldLabel(labelCoalition);
		grid.add(labelCoalition, 2, 0);
		Label labelDataMorph = new Label("DataMorphAgents");
		buildBoldLabel(labelDataMorph);
		labelDataMorph.setPrefWidth(240);
		grid.add(labelDataMorph, 3, 0);
	}

	private void buildLignesDataAgent(GridPane grid) {
		
		CAV cav = cavModel.getCav();
		
		for(DataAgent dataAgent : cav.getAllDataAgent()) {
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
			grid.add(labelValue, 1, usedLines);
			/* Coalition */
			Label labelCoalition = new Label();
			buildLabel(labelCoalition);
			try {
				labelCoalition.setText(dataAgent.getCoalition().getData());
			} catch (NullPointerException e) {
				labelCoalition.setText("No coalition");
			}
			grid.add(labelCoalition, 2, usedLines);
			/* DataMorphButton */
			VBox buttonBox = new VBox();
			buildCellule(buttonBox);
			buttonBox.setPrefWidth(240);
			Button buttonOpenDataMorph = new Button("DataMorphs");
			buttonOpenDataMorph.setPrefSize(150, 30);
			buttonOpenDataMorph.setId(dataAgent.getDataName());
			buttonBox.getChildren().add(buttonOpenDataMorph);
			buttonOpenDataMorph.setOnAction(controller);
			grid.add(buttonBox, 3, usedLines);
			usedLines++;
		}
	}

	/* Implemented from Modifiable
	 * @see fr.irit.smac.planification.datadisplay.interfaces.Modifiable#update()
	 * Recreates a grid with new values
	 */
	public void update() {

		Thread taskThread = new Thread(new Runnable() {
			@Override
			public void run() {
				
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
						buildLignesDataAgent(newGrid);
						root.getChildren().add(newGrid);
						grid = newGrid;
					}
				});
			}
		});
		taskThread.start();
	}
	
	public void setCavModel(CAVModel cavModel) {
		this.cavModel = cavModel;
	}
	
	public CAVModel getCavModel() {
		return cavModel;	
	}

}
