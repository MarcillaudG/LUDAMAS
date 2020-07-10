package fr.irit.smac.planification.datadisplay.ui;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.irit.smac.planification.datadisplay.controller.DataAgentDisplayController;
import fr.irit.smac.planification.datadisplay.model.CAVModel;
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
	
	/* TESTS ONLY */
	private List<AgentPersonTest> personnes;

	public DataAgentDisplay(int agentType) {
		this.agentType = agentType;
	}
	
	public DataAgentDisplay(int agentType, CAVModel cavModel) {
		this.agentType = agentType;
		this.cavModel = cavModel;
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
		closeButton.setOnAction(new DataAgentDisplayController(cavModel));

		Button refreshButton = new Button();
		refreshButton.setText("REFRESH");
		refreshButton.setPrefSize(70, 30);
		refreshButton.setStyle(BOLDSTYLE);
		refreshButton.setOnAction(new DataAgentDisplayController(cavModel));

		HBox hboxButtons = new HBox();
		hboxButtons.setSpacing(50.0);
		hboxButtons.setPadding(new Insets(10, 0, 20, 0));
		hboxButtons.getChildren().addAll(closeButton, refreshButton);
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

		/* TESTS DE MOFICIATION DU GRIDPANE */
		update();
	}

	private void buildCellule(VBox box) {

		box.setPrefSize(75, 40);
		box.setAlignment(Pos.CENTER);
		box.setBorder(
				new Border(new BorderStroke(grey, grey, grey, grey, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
						BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null, new BorderWidths(0.5), null)));
	}

	private void buildBoldLabel(Label label) {

		label.setAlignment(Pos.CENTER);
		label.setStyle(BOLDSTYLE);
		label.setPrefSize(75, 40);
		label.setBorder(
				new Border(new BorderStroke(grey, grey, grey, grey, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
						BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null, new BorderWidths(1), null)));
	}

	// A CHANGER EN FONCTION DES ATTRIBUTS A AFFICHER
	private void buildFirstLigneDataAgent(GridPane grid) {

		for (int i = 0; i < 8; i++) {
			Label labelProperty = new Label("Label " + i);
			buildBoldLabel(labelProperty);
			grid.add(labelProperty, i + 1, 0);
		}
	}

	private void buildFirstLigneDataMorphAgent(GridPane grid) {

		for (int i = 0; i < 8; i++) {
			Label labelProperty = new Label("Label " + i);
			buildBoldLabel(labelProperty);
			grid.add(labelProperty, i + 1, 0);
		}
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

	private void buildLignesDataMorphAgent(GridPane grid, List<AgentPersonTest> data) {

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
