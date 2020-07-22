package fr.irit.smac.planification.datadisplay.ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import fr.irit.smac.planification.agents.CoalitionAgent;
import fr.irit.smac.planification.agents.DataAgent;
import fr.irit.smac.planification.datadisplay.controller.CoalitionAgentDisplayController;
import fr.irit.smac.planification.datadisplay.model.CAVModel;
import fr.irit.smac.planification.system.CAV;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

public class CoalitionAgentDisplay implements Modifiable {

	private int usedLines = 1;
	private CAVModel cavModel;
	private GridPane grid;
	private static final Color grey = Color.rgb(100, 100, 100);
	private static final String BOLDSTYLE = "-fx-font-weight: bold";
	private VBox root;
	private Stage primaryStage;
	private CoalitionAgentDisplayController controller;

	public CoalitionAgentDisplay(CAVModel cavModel) {
		this.cavModel = cavModel;
		this.primaryStage = new Stage();
		this.controller = new CoalitionAgentDisplayController(cavModel);
		start();
	}

	public void start() {
		primaryStage.setTitle("CoalitionAgentsDisplay");
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

		primaryStage.setScene(new Scene(scrollPane, 1100, 500));
		primaryStage.show();
	}

	private void buildFirstLigneCoalitionAgent(GridPane grid) {

		Label labelId = new Label("Name");
		buildBoldLabel(labelId);
		grid.add(labelId, 0, 0);
		Label labelValue = new Label("Value");
		buildBoldLabel(labelValue);
		grid.add(labelValue, 1, 0);
		Label labelLinkedAgents = new Label("Linked Agents");
		buildBoldLabel(labelLinkedAgents);
		labelLinkedAgents.setPrefWidth(500);
		grid.add(labelLinkedAgents, 2, 0);
		Label labelInput = new Label("Input");
		buildBoldLabel(labelInput);
		grid.add(labelInput, 3, 0);
		Label labelAVT = new Label("AVTAgents");
		buildBoldLabel(labelAVT);
		labelAVT.setPrefWidth(200);
		grid.add(labelAVT, 4, 0);
	}

	private void buildLignesCoalitionAgent(GridPane grid) {
		CAV cav = cavModel.getCav();
		List<CoalitionAgent> coalitions = cav.getAllCoalitions();
		for(CoalitionAgent coalitionAgent : coalitions) {
			/* name */
			VBox celluleAgentName = new VBox();
			buildCellule(celluleAgentName);
			Label labelAgentName = new Label(coalitionAgent.getName());
			buildBoldLabel(labelAgentName);
			celluleAgentName.getChildren().add(labelAgentName);
			grid.add(celluleAgentName, 0, usedLines);
			/* value */
			Label labelValue = new Label(String.valueOf(coalitionAgent.getValue()));
			buildLabel(labelValue);
			grid.add(labelValue, 1, usedLines);
			/* linked agents */
			StringBuilder result = new StringBuilder();
			Collection<DataAgent> dataAgents = coalitionAgent.getDatas().values();
			for(Iterator<DataAgent> it = dataAgents.iterator(); it.hasNext();) {
				result.append(it.next().getDataName());
				if(it.hasNext()) {
					result.append(" // ");
				}
			}
			Label labelLinkedAgents = new Label(result.toString());
			buildLabel(labelLinkedAgents);
			labelLinkedAgents.setPrefWidth(500);
			grid.add(labelLinkedAgents, 2, usedLines);
			/* input */
			Label labelInput = new Label();
			buildLabel(labelInput);
			if(coalitionAgent.getInput()==null) {
				labelInput.setText("No input");
			} else {
				labelInput.setText(coalitionAgent.getInput());
			}
			grid.add(labelInput, 3, usedLines);
			/* AVTAgents */
			VBox buttonBox = new VBox();
			buildCellule(buttonBox);
			buttonBox.setPrefWidth(200);
			Button buttonOpenAVT = new Button("AVTAgents");
			buttonOpenAVT.setPrefSize(130, 30);
			buttonOpenAVT.setId(coalitionAgent.getName());
			buttonOpenAVT.setOnAction(controller);
			//TODO:
			/*
			 * 1 - getAllCoalition (list<CoalitionAgent> from CAV)
			 * 2 - look for the coalitionAgent by its name given in buttonID
			 * 3 - process
			 */
			buttonBox.getChildren().add(buttonOpenAVT);
			grid.add(buttonBox, 4, usedLines);
			usedLines++;
		}
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
}
