package fr.irit.smac.planification.datadisplay.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.irit.smac.planification.agents.AVTAgent;
import fr.irit.smac.planification.agents.CoalitionAgent;
import fr.irit.smac.planification.agents.DataAgent;
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

public class AVTAgentDisplay implements Modifiable {

	/* Nombre de lignes utilisees dans le gridPane (commence a 1 en raison de la premiere
	 * ligne
	 */
	private int usedLines = 1;
	private CAVModel cavModel;
	private GridPane grid;
	private VBox root;
	private String coalitionName;
	private ScrollPane scrollPane;

	/* Constants */
	private static final Color grey = Color.rgb(100, 100, 100);
	private static final String BOLDSTYLE = "-fx-font-weight: bold";

	public AVTAgentDisplay(CAVModel cavModel, String coalitionName) {
		this.cavModel = cavModel;
		this.coalitionName = coalitionName;
		start();
	}
	
	/*
	 * Start
	 * Construction des composants du dataDisplay
	 */
	public void start() {
		grid = new GridPane();
		grid.setBorder(new Border(new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
				BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
				null, new BorderWidths(0.5), null)));

		buildFirstLigneAVTAgent(grid);

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
		scrollPane.setPrefSize(1100, 300);
	}

	/*
	 * Displayed attributes of an AVTAgent: Name/Weight/Associate DataAgent(name +
	 * value)/AccelerationCoeff/DecelerationCoeff
	 */
	private void buildFirstLigneAVTAgent(GridPane grid) {

		Label labelId = new Label("Name");
		buildBoldLabel(labelId);
		grid.add(labelId, 0, 0);
		Label labelHeight = new Label("Weight");
		buildBoldLabel(labelHeight);
		grid.add(labelHeight, 1, 0);
		Label labelDataAgent = new Label("DataAgent");
		buildBoldLabel(labelDataAgent);
		labelDataAgent.setPrefWidth(348);
		grid.add(labelDataAgent, 2, 0);
		Label labelAcc = new Label("AccelerationCoeff");
		buildBoldLabel(labelAcc);
		grid.add(labelAcc, 3, 0);
		Label labelDeceleration = new Label("DecelerationCoeff");
		buildBoldLabel(labelDeceleration);
		grid.add(labelDeceleration, 4, 0);
	}

	/* BuildLignesAVTAgent
	 * Prend en parametre le gridpane a remplir et la liste des agents
	 * et affiche les donnees de chaque agents dans le gridpane
	 */
	private void buildLignesAVTAgent(GridPane grid, List<AVTAgent> allAVT) {
		for (AVTAgent avt : allAVT) {
			/* name */
			VBox celluleAgentName = new VBox();
			buildCellule(celluleAgentName);
			Label labelAgentName = new Label(avt.getDataAgent().getDataName());
			celluleAgentName.getChildren().add(labelAgentName);
			grid.add(celluleAgentName, 0, usedLines);
			/* height */
			Label labelHeight = new Label(String.valueOf(avt.getWeight()));
			buildLabel(labelHeight);
			grid.add(labelHeight, 1, usedLines);
			/* DataAgent + value */
			DataAgent dataAgent = avt.getDataAgent();
			Label labelDataAgent = new Label(
					dataAgent.getDataName() + " VALUE: " + String.valueOf(dataAgent.askValue()));
			buildLabel(labelDataAgent);
			labelDataAgent.setPrefWidth(348);
			grid.add(labelDataAgent, 2, usedLines);
			/* AccelerationCoeff */
			// TODO get accelerationCoeff
			Label labelAcceleration = new Label("");
			buildLabel(labelAcceleration);
			grid.add(labelAcceleration, 3, usedLines);
			/* DecelerationCoeff */
			// TODO get decelerationCoeff
			Label labelDeceleration = new Label("");
			buildLabel(labelDeceleration);
			grid.add(labelDeceleration, 4, usedLines);
			usedLines++;
		}
	}

	/*
	 * GetAvtsFromCoalition Gets all AVTAgent from every coalition agents and builds
	 * a collection for it
	 */
	private Collection<AVTAgent> getAvtsFromCoalition() {
		List<AVTAgent> resultat = new ArrayList<>();
		CAV cav = cavModel.getCav();
		List<CoalitionAgent> coalitions = cav.getAllCoalitions();
		for (CoalitionAgent coalitionAgent : coalitions) {
			String name = coalitionAgent.getName();
			if (name != null && name.equals(coalitionName)) {
				resultat = new ArrayList<>(coalitionAgent.getAllAVT());
			}
		}
		return resultat;
	}

	/*
	 * BuildCellule Param in: VBox to build Sets size, alignment and border of the
	 * vbox given
	 */
	private void buildCellule(VBox box) {

		box.setPrefSize(180, 40);
		box.setAlignment(Pos.CENTER);
		box.setBorder(
				new Border(new BorderStroke(grey, grey, grey, grey, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
						BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null, new BorderWidths(0.5), null)));
	}

	/*
	 * BuildLabel Param in: Label to build Sets size, alignment and border of the
	 * label given
	 */
	private void buildLabel(Label label) {
		label.setAlignment(Pos.CENTER);
		label.setPrefSize(180, 40);
		label.setBorder(
				new Border(new BorderStroke(grey, grey, grey, grey, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
						BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null, new BorderWidths(1), null)));
	}

	/*
	 * BuildBoldLabel Param in: Label to build Sets size, bold style, alignment and
	 * border of the label given
	 */
	private void buildBoldLabel(Label label) {

		buildLabel(label);
		label.setStyle(BOLDSTYLE);
	}

	/* Update
	 * Implente depuis Modifiable
	 * A chaque cycle de l'exeperience, l'affichage est mis a jour
	 */
	@Override
	public void update() {
		Thread taskThread = new Thread(new Runnable() {

			@Override
			public void run() {
				List<AVTAgent> allAVT = new ArrayList<>(getAvtsFromCoalition());
				
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
						buildFirstLigneAVTAgent(newGrid);
						buildLignesAVTAgent(newGrid, allAVT);
						root.getChildren().add(newGrid);
						grid = newGrid;
						/* Le travail du thread est termine, on rend le token
						 * au semphore du cavModel
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
