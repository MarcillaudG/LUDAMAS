package fr.irit.smac.planification.datadisplay.ui;

import java.util.Collection;

import fr.irit.smac.planification.agents.DataAgent;
import fr.irit.smac.planification.agents.DataMorphAgent;
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

public class DataMorphAgentDisplay implements Modifiable {
	
	private int usedLines = 1;
	private CAVModel cavModel;
	private static final Color grey = Color.rgb(100, 100, 100);
	private static final String BOLDSTYLE = "-fx-font-weight: bold";
	private GridPane grid;
	private VBox root;
	private String dataAgentName;
	private Stage primaryStage;
	
	public DataMorphAgentDisplay(CAVModel cavModel, String dataAgentName) {
		this.primaryStage = new Stage();
		this.cavModel = cavModel;
		this.dataAgentName = dataAgentName;
		start();
	}
	
	public void start() {
		primaryStage.setTitle(dataAgentName + ": DataMorphAgents");
		primaryStage.setOnCloseRequest(new CloseModifiableController(cavModel, this));
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

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(stack);
		scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

		stack.minWidthProperty().bind(Bindings.createDoubleBinding(() -> scrollPane.getViewportBounds().getWidth(),
				scrollPane.viewportBoundsProperty()));

		primaryStage.setScene(new Scene(scrollPane, 885, 500));
		primaryStage.show();
	}
	
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
		labelLinearFormula.setPrefWidth(240);
		grid.add(labelLinearFormula, 3,  0);
		Label labelMorphValue = new Label("Morph value");
		buildBoldLabel(labelMorphValue);
		grid.add(labelMorphValue, 4,  0);
		Label labelTrueValue = new Label("True value");
		buildBoldLabel(labelTrueValue);
		grid.add(labelTrueValue, 5,  0);
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
	
	
	private void buildLignesDataMorphAgent(GridPane grid) {
		
		CAV cav = cavModel.getCav();
		DataAgent dataAgent = cav.getDataAgentWithName(this.dataAgentName);
		Collection<? extends DataMorphAgent> dataMorphAgents = dataAgent.getAllMorphs();
		
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
			labelLinearFormula.setPrefWidth(240);
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
						buildFirstLigneDataMorphAgent(newGrid);
						buildLignesDataMorphAgent(newGrid);
						root.getChildren().add(newGrid);
						grid = newGrid;
					}
				});
			}
		});
		taskThread.start();
	}

}
