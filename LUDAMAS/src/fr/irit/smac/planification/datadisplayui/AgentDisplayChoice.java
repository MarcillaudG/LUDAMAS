package fr.irit.smac.planification.datadisplayui;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Label;

public class AgentDisplayChoice {
	
	private Stage primaryStage;

	public AgentDisplayChoice() {
		primaryStage = new Stage();
		start(primaryStage);
	}
	
	public void start(Stage primaryStage) {

		
		/* DEBUT COMPOSANTS PARTIE SUPERIEURE */
		primaryStage.setTitle("Data Display Choice");
		Label labelTitle = new Label("AGENT TYPES");
		labelTitle.setFont(new Font("Segoe UI", 20));
		labelTitle.setStyle("-fx-font-weight: bold");

		/* DEBUT COMPOSANTS PARTIE INFERIEURE */
		Button agentTypeOne = new Button();
		agentTypeOne.setText("DataAgent");
		agentTypeOne.setPrefSize(120, 70);
		agentTypeOne.setOnAction(new OpenDataMorphDisplayHandler());

		Button agentTypeTwo = new Button();
		agentTypeTwo.setText("DataMorphAgent");
		agentTypeTwo.setPrefSize(120, 70);
		agentTypeTwo.setOnAction(new OpenDataMorphDisplayHandler());

		Button agentTypeThree = new Button();
		agentTypeThree.setText("EffectorAgent");
		agentTypeThree.setPrefSize(120, 70);
		agentTypeThree.setOnAction(new OpenEffectorDisplayHandler());

		Button agentTypeFour = new Button();
		agentTypeFour.setText("Chart Display");
		agentTypeFour.setPrefSize(120, 70);
		agentTypeFour.setOnAction(new OpenChartDisplayHandler());

		/* CONFIGURATION SCENE GRAPHS */
		GridPane grid = new GridPane();
		grid.add(agentTypeOne, 0, 0);
		grid.add(agentTypeTwo, 1, 0);
		grid.add(agentTypeThree, 2, 0);
		grid.add(agentTypeFour, 1, 1);
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(20);
		grid.setVgap(20);
		grid.setPadding(new Insets(0, 0, 40, 0));

		VBox topBorderPane = new VBox(labelTitle);
		topBorderPane.setAlignment(Pos.CENTER);
		topBorderPane.setPadding(new Insets(15, 0, 20, 0));
		topBorderPane.setBorder(new Border(new BorderStroke(null, null, Color.rgb(162, 162, 162), null,
				BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, null,
				new BorderWidths(1), null)));

		BorderPane panelRoot = new BorderPane();
		panelRoot.setTop(topBorderPane);
		panelRoot.setCenter(grid);

		primaryStage.setScene(new Scene(panelRoot, 500, 300));
		primaryStage.show();
	}
}
