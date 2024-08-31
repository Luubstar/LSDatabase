package org.luubstar.lsdatabase;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.luubstar.lsdatabase.Utils.ChangePanel;
import org.luubstar.lsdatabase.Utils.Database.Database;
import org.luubstar.lsdatabase.Utils.Notification;
import org.luubstar.lsdatabase.Utils.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static org.luubstar.lsdatabase.App.st;
import static org.luubstar.lsdatabase.App.writeLast;

public class MainController implements Initializable {

    @FXML
    private StackPane root;

    @FXML
    private Button button_Inicio;
    @FXML
    private Button button_Buscar;
    @FXML
    private Button button_Anadir;
    @FXML
    private Button button_Facturar;
    @FXML
    private Button button_Recordatorios;
    @FXML
    private Button button_notificacion;
    @FXML
    private Circle bell_circle;

    @FXML
    private MenuItem pInicio;
    @FXML
    private MenuItem pBuscar;
    @FXML
    private MenuItem pAdd;
    @FXML
    private MenuItem pFacturar;
    @FXML
    private MenuItem pRecordatorios;

    public static int index = -1;
    private final BooleanProperty canMove = new SimpleBooleanProperty(false);
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    private final List<Notification> notificaciones = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ChangePanel.init(root, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        button_Inicio.setOnMouseReleased(e -> ChangePanel.changeContent(Panel.DASHBOARD));
        button_Buscar.setOnMouseReleased(e -> ChangePanel.changeContent(Panel.BUSQUEDA));
        button_Anadir.setOnMouseReleased(e -> ChangePanel.changeContent(Panel.ANADIR));
        button_Facturar.setOnMouseReleased(e -> ChangePanel.changeContent(Panel.FACTURAR));
        button_Recordatorios.setOnMouseReleased(e -> ChangePanel.changeContent(Panel.RECORDATORIOS));

        pInicio.setOnAction(e -> ChangePanel.changeContent(Panel.DASHBOARD));
        pBuscar.setOnAction(e -> ChangePanel.changeContent(Panel.BUSQUEDA));
        pAdd.setOnAction(e -> ChangePanel.changeContent(Panel.ANADIR));
        pFacturar.setOnAction(e -> ChangePanel.changeContent(Panel.FACTURAR));
        pRecordatorios.setOnAction(e -> ChangePanel.changeContent(Panel.RECORDATORIOS));

        button_Inicio.disableProperty().bind(canMove);
        button_Buscar.disableProperty().bind(canMove);
        button_Anadir.disableProperty().bind(canMove);
        button_Facturar.disableProperty().bind(canMove);
        button_Recordatorios.disableProperty().bind(canMove);

        setNotificationButton();

        pInicio.disableProperty().bind(canMove);
        pBuscar.disableProperty().bind(canMove);
        pAdd.disableProperty().bind(canMove);
        pFacturar.disableProperty().bind(canMove);
        pRecordatorios.disableProperty().bind(canMove);
        ChangePanel.changeContent(Panel.DASHBOARD);
    }

    public void blockMovement(boolean b) {
        canMove.set(b);
    }

    public void open() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar archivo");
            fileChooser.setInitialDirectory(new File(new File(MainController.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getPath()));

            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Archivos de datos", "*.lsdata")
            );

            File selectedFile = fileChooser.showOpenDialog(button_Inicio.getScene().getWindow());

            if (selectedFile != null) {
               open(selectedFile.getAbsolutePath());
            } else {
                logger.info("Dialogo de selección cerrado");
            }
        }
        catch (Exception e){
            logger.error("Error en la lectura ", e);
        }
    }

    public void open(String s) throws IOException, InstantiationException {
        File selectedFile = new File(s);
        logger.info("Cargando fichero {}", selectedFile);
        Database.disconect();
        Database.loadFile(selectedFile.getPath());
        writeLast(selectedFile.getPath());
        Database.start();

        ((SearchController) ChangePanel.getController(Panel.BUSQUEDA)).search();
    }

    public void save(){
        Database.file.save();
        Database.unsaved = false;
    }

    public void saveAs(){
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar archivo");
            fileChooser.setInitialDirectory(new File(new File(MainController.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getPath()));

            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Archivos de datos", "*.lsdata")
            );

            File selectedFile = fileChooser.showSaveDialog(button_Inicio.getScene().getWindow());

            if (selectedFile != null) {
                logger.info("Guardando fichero {}", selectedFile);
                Database.file.save(selectedFile);
                Database.unsaved = false;
            } else {
                logger.info("Dialogo de guardado cerrado");
            }
        }
        catch (Exception e){
            logger.error("Error en la lectura ", e);
        }
    }

    public void newfile(){
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Crear archivo");
            fileChooser.setInitialDirectory(new File(new File(MainController.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getPath()));

            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Archivos de datos", "*.lsdata")
            );

            File selectedFile = fileChooser.showSaveDialog(button_Inicio.getScene().getWindow());

            if (selectedFile != null) {
                logger.info("Creando fichero {}", selectedFile);
                Database.createNew(selectedFile.getAbsolutePath().replace("\\", "/"));
                Database.loadFile(selectedFile.getAbsolutePath().replace("\\", "/"));
                Database.start();
                Database.unsaved = false;
            } else {
                logger.info("Dialogo de creación cerrado");
            }
        }
        catch (Exception e){
            logger.error("Error en la lectura ", e);
        }
    }

    public void setNotificationButton(){
        ContextMenu notificationMenu = new ContextMenu();
        VBox notificationBox = new VBox(10);
        notificationBox.setPadding(new Insets(10));
        notificationMenu.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-width: 1;");

        ScrollPane scrollPane = new ScrollPane(notificationBox);
        scrollPane.setPrefSize(250, 200);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-width: 1;");

        MenuItem notificationContainer = new MenuItem(null, scrollPane);
        notificationContainer.getStyleClass().add("notification");
        notificationMenu.getItems().add(notificationContainer);

        notificaciones.add(new Notification("Hola", "Caracola", Date.valueOf(LocalDate.now())));
        notificaciones.add(new Notification("Hola", "Caracola", Date.valueOf(LocalDate.now())));
        notificaciones.add(new Notification("Hola", "Caracola", Date.valueOf(LocalDate.now())));
        notificaciones.add(new Notification("Hola", "Caracola", Date.valueOf(LocalDate.now())));
        notificaciones.add(new Notification("Hola", "Caracola", Date.valueOf(LocalDate.now())));
        notificaciones.add(new Notification("Hola", "Caracola", Date.valueOf(LocalDate.now())));
        notificaciones.add(new Notification("Hola", "Caracola", Date.valueOf(LocalDate.now())));
        notificaciones.add(new Notification("Hola", "Caracola", Date.valueOf(LocalDate.now())));
        notificaciones.add(new Notification("Hola", "Caracola", Date.valueOf(LocalDate.now())));
        notificaciones.add(new Notification("Hola", "CaracolaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", Date.valueOf(LocalDate.now())));
        notificaciones.add(new Notification("Hola", "CaracolaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", Date.valueOf(LocalDate.now())));
        notificaciones.add(new Notification("Hola", "CaracolaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", Date.valueOf(LocalDate.now())));
        notificaciones.add(new Notification("Hola", "CaracolaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", Date.valueOf(LocalDate.now())));notificaciones.add(new Notification("Hola", "Caracola", Date.valueOf(LocalDate.now())));

        notificaciones.add(new Notification("Hola", "CaracolaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", Date.valueOf(LocalDate.now())));

        Stage primaryStage = App.st;
        button_notificacion.setOnAction(e -> {
            notificationBox.getChildren().clear();

            if (!notificationMenu.isShowing() && !notificaciones.isEmpty()) {
                bell_circle.setVisible(false);

                for (Notification notification : notificaciones) {
                    Button notificationButton = createNotificationButton(notification);
                    notificationBox.getChildren().add(notificationButton);
                }

                notificationMenu.show(button_notificacion,
                        primaryStage.getX() + button_notificacion.getLayoutX(),
                        primaryStage.getY() + button_notificacion.getLayoutY() + button_notificacion.getHeight() + 30);
                st.requestFocus();
            } else {notificationMenu.hide();}
        });
    }

    private Button createNotificationButton(Notification notification) {
        HBox notificationContent = new HBox(10);
        notificationContent.setPadding(new Insets(5));
        notificationContent.setStyle("-fx-background-color: #AFAFAF; -fx-background-radius: 5;");
        notificationContent.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(notification.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold;");

        Label messageLabel = new Label(notification.getMessage());
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(150);

        VBox textContent = new VBox(5, titleLabel, messageLabel);

        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-font-weight: bold;");
        closeButton.setOnAction(e -> notificaciones.remove(notification));

        // Agregar el contenido de texto y el botón de cerrar al HBox
        notificationContent.getChildren().addAll(textContent, closeButton);

        Button notificationButton = new Button();
        notificationButton.setGraphic(notificationContent);
        notificationButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        return notificationButton;
    }

    public void addNotificacion(Notification n){
        bell_circle.setVisible(true);
        if(!notificaciones.contains(n)){notificaciones.add(n);}
    }
}