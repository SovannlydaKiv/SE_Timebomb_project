import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class app extends Application {

    @Override
    public void start(Stage stage) {

        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
        card.setEffect(new DropShadow(12, Color.rgb(0, 0, 0, 0.2)));

        Label title = new Label("Time Tracker");
        title.setFont(Font.font("Segoe UI", 28));

        Label timer = new Label("00:00:00");
        timer.setFont(Font.font("Consolas", 52));
        timer.setAlignment(Pos.CENTER);
        timer.setMaxWidth(Double.MAX_VALUE);

        // Replaced Ready badge with Task Description
        Label taskLabel = new Label("Task Description");
        taskLabel.setFont(Font.font("Segoe UI", 18));

        TextField taskInput = new TextField();
        taskInput.setFont(Font.font("Segoe UI", 18));
        taskInput.setPromptText("What are you working on?");
        taskInput.setStyle("-fx-background-radius: 8; -fx-padding: 10;");

        Label catLabel = new Label("Category:");
        catLabel.setFont(Font.font("Segoe UI", 18));

        HBox categories = new HBox(10);
        categories.getChildren().addAll(
                makeTag("Work"),
                makeTag("Meetings"),
                makeTag("Break"),
                makeTag("Personal"),
                makeTag("Learning"));

        Button startBT = new Button("Start Timer");
        startBT.setFont(Font.font("Segoe UI", 20));
        startBT.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-background-radius: 10;");
        startBT.setPrefWidth(250);

        // Center button
        HBox startWrapper = new HBox(startBT);
        startWrapper.setAlignment(Pos.CENTER);
        VBox.setMargin(startWrapper, new Insets(10, 0, 10, 0));

        Label today = new Label("Today's Entries");
        today.setFont(Font.font("Segoe UI", 22));

        VBox list = new VBox(10);
        list.getChildren().addAll(
                makeEntry("Design UI screen", "Work", "00:45:12"),
                makeEntry("Standup meeting", "Meetings", "00:25:08"));

        Label total = new Label("Total Time: 01:10:20");
        total.setFont(Font.font("Segoe UI", 18));
        total.setMaxWidth(Double.MAX_VALUE);
        total.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(
                title,
                timer,
                taskLabel,
                taskInput,
                catLabel,
                categories,
                startWrapper,
                today,
                list,
                total);

        VBox root = new VBox();
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #F5F6FA;");
        root.getChildren().add(card);

        Scene scene = new Scene(root, 1200, 850);
        stage.setScene(scene);
        stage.setTitle("Time Tracker");
        stage.show();
    }

    private Button makeTag(String name) {
        Button b = new Button(name);
        b.setStyle("-fx-background-color: #EFEFEF; -fx-background-radius: 6; -fx-border-color: #D0D0D0;");
        b.setFont(Font.font("Segoe UI", 16));
        return b;
    }

    private HBox makeEntry(String task, String category, String duration) {
        HBox row = new HBox();
        row.setPadding(new Insets(10));
        row.setStyle("-fx-background-color: #FBFBFB; -fx-background-radius: 8; -fx-border-color: #E0E0E0;");
        row.setSpacing(20);

        Label t = new Label(task);
        t.setFont(Font.font("Segoe UI", 17));
        t.setPrefWidth(450);

        Label c = new Label(category);
        c.setFont(Font.font("Segoe UI", 16));
        c.setPrefWidth(200);

        Label d = new Label(duration);
        d.setFont(Font.font("Consolas", 20));
        d.setPrefWidth(200);
        d.setAlignment(Pos.CENTER_RIGHT);

        row.getChildren().addAll(t, c, d);
        return row;
    }

    public static void main(String[] args) {
        launch(args);
    }
}