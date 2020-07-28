package fr.irit.smac.planification.datadisplay.ui;


import java.util.List;
import java.util.Map;

import fr.irit.smac.planification.agents.AVTAgent;
import fr.irit.smac.planification.agents.CoalitionAgent;
import fr.irit.smac.planification.agents.DataAgent;
import fr.irit.smac.planification.datadisplay.controller.CloseModifiableController;
import fr.irit.smac.planification.datadisplay.interfaces.Modifiable;
import fr.irit.smac.planification.datadisplay.model.CAVModel;
import fr.irit.smac.planification.system.CAV;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

public class AVTAgentDisplay implements Modifiable{
	
	private int usedLines = 1;
	private CAVModel cavModel;
	private GridPane grid;
	private static final Color grey = Color.rgb(100, 100, 100);
	private static final String BOLDSTYLE = "-fx-font-weight: bold";
	private VBox root;
	private Stage primaryStage;
	private String coalitionName;

	public AVTAgentDisplay(CAVModel cavModel, String coalitionName) {
		this.cavModel = cavModel;
		this.primaryStage = new Stage();
		this.coalitionName = coalitionName;
		start();
	}

	public void start() {
		primaryStage.setTitle(coalitionName + ": AVTAgents");
		primaryStage.setOnCloseRequest(new CloseModifiableController(cavModel, this));
		grid = new GridPane();
		grid.setBorder(new Border(new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
				BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
				null, new BorderWidths(0.5), null)));

		buildFirstLigneCoalitionAgent(grid);

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

		primaryStage.setScene(new Scene(scrollPane, 750, 300));
		primaryStage.show();
	}

	private void buildFirstLigneCoalitionAgent(GridPane grid) {

		Label labelId = new Label("Name");
		buildBoldLabel(labelId);
		grid.add(labelId, 0, 0);
		Label labelHeight = new Label("Weight");
		buildBoldLabel(labelHeight);
		grid.add(labelHeight, 1, 0);
		Label labelDataAgent = new Label("DataAgent");
		buildBoldLabel(labelDataAgent);
		labelDataAgent.setPrefWidth(240);
		grid.add(labelDataAgent, 2, 0);
		Label labelAcc = new Label("AccelerationCoeff");
		buildBoldLabel(labelAcc);
		grid.add(labelAcc, 3, 0);
		Label labelDeceleration = new Label("DecelerationCoeff");
		buildBoldLabel(labelDeceleration);
		grid.add(labelDeceleration, 4, 0);
	}
	

	private void buildLignesCoalitionAgent(GridPane grid) {
		
		Map<String, AVTAgent> avtAgents = getAvtMapFromCoalition();
		if(avtAgents!=null) {
			for(String key : avtAgents.keySet()) {
				AVTAgent avt = avtAgents.get(key);
				/* name */
				VBox celluleAgentName = new VBox();
				buildCellule(celluleAgentName);
				Label labelAgentName = new Label(key);
				celluleAgentName.getChildren().add(labelAgentName);
				grid.add(celluleAgentName, 0, usedLines);
				/* height */
				Label labelHeight = new Label(String.valueOf(avt.getWeight()));
				buildLabel(labelHeight);
				grid.add(labelHeight, 1, usedLines);
				/* DataAgent + value */
				DataAgent dataAgent = avt.getDataAgent();
				Label labelDataAgent = 
						new Label(dataAgent.getDataName() + " VALUE: " + String.valueOf(dataAgent.askValue()));
				buildLabel(labelDataAgent);
				labelDataAgent.setPrefWidth(240);
				grid.add(labelDataAgent, 2, usedLines);
				/* AccelerationCoeff */
				//TODO get accelerationCoeff
				Label labelAcceleration = new Label("");
				buildLabel(labelAcceleration);
				grid.add(labelAcceleration, 3, usedLines);
				/* DecelerationCoeff */
				//TODO get decelerationCoeff
				Label labelDeceleration = new Label("");
				buildLabel(labelDeceleration);
				grid.add(labelDeceleration, 4, usedLines);
				usedLines++;
			}
		}
	}
	
	private Map<String, AVTAgent> getAvtMapFromCoalition() {
		Map<String, AVTAgent> resultat = null;
		CAV cav = cavModel.getCav();
		List<CoalitionAgent> coalitions = cav.getAllCoalitions();
		for(CoalitionAgent coalitionAgent : coalitions) {
			String name = coalitionAgent.getName();
			if(name!=null && name.equals(coalitionName)) {
				resultat = coalitionAgent.getAllAVT();
			}
		}
		return resultat;
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

	@Override
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
						buildFirstLigneCoalitionAgent(newGrid);
						buildLignesCoalitionAgent(newGrid);
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