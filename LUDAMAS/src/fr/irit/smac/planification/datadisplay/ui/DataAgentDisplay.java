package fr.irit.smac.planification.datadisplay.ui;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.irit.smac.planification.agents.DataAgent;
import fr.irit.smac.planification.agents.DataMorphAgent;
import fr.irit.smac.planification.datadisplay.controller.DataAgentDisplayController;
import fr.irit.smac.planification.datadisplay.model.CAVModel;
import fr.irit.smac.planification.system.CAV;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Separator;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class DataAgentDisplay implements Modifiable {
	
	private static final Color grey = Color.rgb(100, 100, 100);
	private static final String BOLDSTYLE = "-fx-font-weight: bold";
	private GridPane grid;
	private VBox root;
	private int agentType;
	private CAVModel cavModel;
	private int usedLines = 1;
	private DataAgentDisplayController controller;
	
	/* TESTS ONLY */
	private List<AgentPersonTest> personnes;

	public DataAgentDisplay(int agentType) {
		this.agentType = agentType;
	}
	
	public DataAgentDisplay(int agentType, CAVModel cavModel) {
		this.agentType = agentType;
		this.cavModel = cavModel;
		this.controller = new DataAgentDisplayController(cavModel);
	}
	
	public void buildWindow() {

		String agentTypeName;
		switch (agentType) {
		case 1:
			agentTypeName = "DataAgents";
			break;
		case 2:
			agentTypeName = "DataMorphAgents";
			break;
		case 3:
			agentTypeName = "CoallitionAgents";
			break;
		default:
			agentTypeName = "unknown";
		}

		Stage primaryStage = new Stage();
		primaryStage.setTitle(agentTypeName);

		// POUR LES TESTS EN ATTENDANT LES DONNEES
		AgentPersonTest personTest1 = new AgentPersonTest("name", "prenom", 5);
		AgentPersonTest personTest2 = new AgentPersonTest("name", "prenom", 5);
		AgentPersonTest personTest3 = new AgentPersonTest("name", "prenom", 5);
		personnes = new ArrayList<>();
		personnes.add(personTest1);
		personnes.add(personTest2);
		personnes.add(personTest3);

		/* CREATION DE LA MATRICE */
		grid = new GridPane();
		grid.setBorder(new Border(new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
				BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
				null, new BorderWidths(0.5), null)));

		/* Ajout des donnees dans la matrice en fonction du type demande */
		switch (agentType) {
		case 1:
			buildFirstLigneDataAgent(grid);
			buildLignesDataAgent(grid, personnes);
			break;
		case 2:
			buildFirstLigneDataMorphAgent(grid);
			buildLignesDataMorphAgent(grid, personnes);
			break;
		case 3:
			buildFirstLigneEffectorAgent(grid);
			buildLignesEffectorAgent(grid, personnes);
			break;
		default:
			System.out.println("Unknown agent type");
		}

		Button closeButton = new Button();
		closeButton.setText("CLOSE");
		closeButton.setId("closeID");
		closeButton.setPrefSize(70, 30);
		closeButton.setStyle(BOLDSTYLE);
		closeButton.setOnAction(controller);

		HBox hboxButtons = new HBox();
		hboxButtons.setSpacing(50.0);
		hboxButtons.setPadding(new Insets(10, 0, 20, 0));
		hboxButtons.getChildren().addAll(closeButton);
		hboxButtons.setAlignment(Pos.CENTER);

		/* SCENE GRAPHS PARAMETRAGES */
		root = new VBox();
		root.setPadding(new Insets(15, 15, 15, 15));
		root.getChildren().add(hboxButtons);
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

	// A CHANGER EN FONCTION DES ATTRIBUTS A AFFICHER
	private void buildFirstLigneDataAgent(GridPane grid) {

		Label labelId = new Label("Agent Name");
		buildBoldLabel(labelId);
		grid.add(labelId, 0, 0);
		Label labelDataMorph = new Label("DataMorphAgents");
		buildBoldLabel(labelDataMorph);
		grid.add(labelDataMorph, 1, 0);
		//TODO la suite ....
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
		grid.add(labelLinearFormula, 3,  0);
		Label labelMorphValue = new Label("Morph value");
		buildBoldLabel(labelMorphValue);
		grid.add(labelMorphValue, 4,  0);
	}

	private void buildFirstLigneEffectorAgent(GridPane grid) {

		for (int i = 0; i < 8; i++) {
			Label labelProperty = new Label("Label " + i);
			buildBoldLabel(labelProperty);
			grid.add(labelProperty, i + 1, 0);
		}
	}

	// A CHANGER EN FONCTION DES ATTRIBUTS A AFFICHER
	private VBox buildNewCellule(Object data, Object linkedData) {

		VBox cellule = new VBox();
		buildCellule(cellule);
		Label labelData = new Label(data.toString());
		Label labelLinkedData = new Label(linkedData.toString());
		Separator separator = new Separator(Orientation.HORIZONTAL);
		cellule.getChildren().addAll(labelData, separator, labelLinkedData);
		return cellule;
	}

	// A CHANGER EN FONCTION DES ATTRIBUTS A AFFICHER
	private void buildLignesDataAgent(GridPane grid, List<AgentPersonTest> data) {
		
		CAV cav = cavModel.getCav();
		Map<String, DataAgent> mapDataAgents = cav.getAllDataAgent();
		Collection<String> keysDataAgent = mapDataAgents.keySet();
		
		for(String key : keysDataAgent) {
			VBox labelNameBox = new VBox();
			buildCellule(labelNameBox);
			DataAgent dataAgent = mapDataAgents.get(key);
			Label labelAgentName = new Label(dataAgent.getDataName());
			labelAgentName.setStyle(BOLDSTYLE);
			labelNameBox.getChildren().add(labelAgentName);
			VBox buttonBox = new VBox();
			buildCellule(buttonBox);
			Button buttonOpenDataMorph = new Button("DataMorph");
			buttonOpenDataMorph.setPrefSize(70, 30);
			buttonOpenDataMorph.setId(key);
			buttonBox.getChildren().add(buttonOpenDataMorph);
			buttonOpenDataMorph.setOnAction(controller);
			grid.add(labelNameBox, 0, usedLines);
			grid.add(buttonBox, 1, usedLines);
			usedLines++;
		}
	}

	private void buildLignesDataMorphAgent(GridPane grid, List<AgentPersonTest> data) {
		
		CAV cav = cavModel.getCav();
		Map<String, DataAgent> allDataAgents = cav.getAllDataAgent();
		Collection<DataAgent> dataAgentValues = allDataAgents.values();
		List<DataMorphAgent> dataMorphAgents = new ArrayList<>();
		
		for(DataAgent dataAgent : dataAgentValues) {
			Collection<? extends DataMorphAgent> morphAgents = dataAgent.getAllMorphs();
			dataMorphAgents.addAll(morphAgents);
		}
		
		
//		for(String key : dataAgentsKeys) {
//			DataAgent dataAgent = allDataAgents.get(key);
//			Collection<? extends DataMorphAgent> morphAgents =  dataAgent.getAllMorphs();
//			for(DataMorphAgent DMAgent : morphAgents) {
//				if(!dataMorphAgents.contains(DMAgent)) {
//					dataMorphAgents.add(DMAgent);
//				}
//			}
//			
//		}
		//System.out.println(dataMorphAgents.size());
		
		for(DataMorphAgent dataMorphAgent : dataMorphAgents) {
			/* Name */
			VBox celluleAgentName = new VBox();
			buildCellule(celluleAgentName);
			Label labelAgentName = new Label(dataMorphAgent.getData());
			labelAgentName.setStyle(BOLDSTYLE);
			celluleAgentName.getChildren().add(labelAgentName);
			grid.add(celluleAgentName, 0, usedLines);
			/* usefulness */
			Label labelUsefulness = new Label(String.valueOf(dataMorphAgent.getUsefulness()));
			grid.add(labelUsefulness, 1, usedLines);
			/* linear formula */
			Label labelLinearFormula = new Label(dataMorphAgent.getMorphLRFormula());
			grid.add(labelLinearFormula, 2, usedLines);
			/* morph value */
			Label labelMorphValue = new Label(String.valueOf(dataMorphAgent.getMorphValue()));
			grid.add(labelMorphValue, 3, usedLines);
			usedLines++;
		}

//		for (int j = 0; j < 15; j++) {
//			VBox celluleAgentName = new VBox();
//			buildCellule(celluleAgentName);
//
//			Label labelAgentName = new Label("AName " + j);
//			labelAgentName.setStyle(BOLDSTYLE);
//			celluleAgentName.getChildren().add(labelAgentName);
//
//			VBox celluleNom = buildNewCellule(data.get(0).getNom(), data.get(0).getNom());
//			VBox cellulePrenom = buildNewCellule(data.get(0).getPrenom(), data.get(0).getPrenom());
//			VBox celluleAge = buildNewCellule(data.get(0).getAge(), data.get(0).getAge());
//
//			grid.add(celluleAgentName, 1, j + 1);
//			grid.add(celluleNom, 2, j + 1);
//			grid.add(cellulePrenom, 3, j + 1);
//			grid.add(celluleAge, 4, j + 1);
//		}

	}

	private void buildLignesEffectorAgent(GridPane grid, List<AgentPersonTest> data) {

		for (int j = 0; j < 15; j++) {
			VBox celluleAgentName = new VBox();
			buildCellule(celluleAgentName);

			Label labelAgentName = new Label("AName " + j);
			labelAgentName.setStyle(BOLDSTYLE);
			celluleAgentName.getChildren().add(labelAgentName);

			VBox celluleNom = buildNewCellule(data.get(0).getNom(), data.get(0).getNom());
			VBox cellulePrenom = buildNewCellule(data.get(0).getPrenom(), data.get(0).getPrenom());
			VBox celluleAge = buildNewCellule(data.get(0).getAge(), data.get(0).getAge());

			grid.add(celluleAgentName, 1, j + 1);
			grid.add(celluleNom, 2, j + 1);
			grid.add(cellulePrenom, 3, j + 1);
			grid.add(celluleAge, 4, j + 1);
		}
	}

	public void update() {

		Thread taskThread = new Thread(new Runnable() {
			@Override
			public void run() {
				
				Random rnd = new Random();
				personnes.get(0).setAge(rnd.nextInt(50));

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

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
						switch (agentType) {
						case 1:
							buildFirstLigneDataAgent(newGrid);
							buildLignesDataAgent(newGrid, personnes);
							break;
						case 2:
							buildFirstLigneDataMorphAgent(newGrid);
							buildLignesDataMorphAgent(newGrid, personnes);
							break;
						case 3:
							buildFirstLigneEffectorAgent(newGrid);
							buildLignesEffectorAgent(newGrid, personnes);
							break;
						default:
							System.out.println("Unknown agent type");
						}
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
