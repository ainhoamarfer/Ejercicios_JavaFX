package DI_B2_3;

import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/** B2.3 — Apollo 11, lista de comprobación del lanzamiento. */
public class Main extends Application {

    private final VBox lista = new VBox();
    private final Label contador = new Label();
    private Button btnEliminar;
    private int tamanoLetra = 13;

    @Override
    public void start(Stage stage) {

        // ---------- Cabecera ----------
        Label titulo = new Label("APOLLO 11 · LISTA DE COMPROBACIÓN");
        Label subtitulo = new Label("Escribe un paso y pulsa Enter. Empieza por ! si es crítico.");
        VBox cabecera = new VBox(2, titulo, subtitulo);

        // ---------- Nueva tarea ----------
        TextField txtTarea = new TextField();
        txtTarea.setPromptText("Nuevo paso de la lista…");
        Button btnAgregar = new Button("Agregar");
        HBox barraNueva = new HBox(txtTarea, btnAgregar);
        HBox.setHgrow(txtTarea, Priority.ALWAYS);

        // ---------- Lista ----------
        ScrollPane scroll = new ScrollPane(lista);
        scroll.setFitToWidth(true);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // ---------- Barra inferior ----------
        btnEliminar = new Button("Eliminar terminadas");
        Button btnLimpiar = new Button("Vaciar lista");
        Button btnMenos = new Button("A-");
        Button btnMas = new Button("A+");
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        HBox barraInferior = new HBox(contador, espacio, btnMenos, btnMas, btnEliminar, btnLimpiar);
        barraInferior.setAlignment(Pos.CENTER_LEFT);

        // TODO T1: asigna aquí los identificadores y clases del apartado 3

        // ---------- Lógica (no modificar) ----------
        btnAgregar.setOnAction(e -> agregar(txtTarea));
        txtTarea.setOnAction(e -> agregar(txtTarea));
        btnEliminar.setOnAction(e -> {
            lista.getChildren().removeIf(n -> ((CheckBox) n).isSelected());
            actualizar();
        });
        btnLimpiar.setOnAction(e -> vaciarLista(stage));

        for (String t : new String[] { "Comprobar presión del tanque de oxígeno",
                "!Armar el sistema de escape", "Alinear la plataforma inercial",
                "Confirmar enlace de radio con Houston" }) {
            lista.getChildren().add(crearTarea(t));
        }
        actualizar();

        // ---------- Raíz ----------
        VBox raiz = new VBox(cabecera, barraNueva, scroll, barraInferior);
        btnMenos.setOnAction(e -> cambiarLetra(raiz, -1));
        btnMas.setOnAction(e -> cambiarLetra(raiz, +1));

        Scene scene = new Scene(raiz, 480, 560);
        scene.getStylesheets().add(getClass().getResource("tareas.css").toExternalForm());

        stage.setTitle("B2.3 — Apollo 11");
        stage.setScene(scene);
        stage.show();
    }

    /** Pide confirmación en una ventana modal antes de vaciar la lista. */
    private void vaciarLista(Stage stage) {
        if (lista.getChildren().isEmpty()) {
            return;  // nada que vaciar
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "Se eliminarán todos los pasos, también los pendientes.");
        confirmacion.setTitle("Vaciar lista");
        confirmacion.setHeaderText("¿Vaciar la lista de comprobación?");

        // Centrada sobre la ventana principal y bloqueando la aplicación
        confirmacion.initOwner(stage);
        confirmacion.initModality(Modality.APPLICATION_MODAL);

        // El diálogo tiene su propia escena: se le añade la misma hoja de estilos
        List<String> hojas = stage.getScene().getStylesheets();
        confirmacion.getDialogPane().getStylesheets().addAll(hojas);

        // showAndWait() detiene el código hasta que el usuario responde
        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            lista.getChildren().clear();
            actualizar();
        }
    }

    private void agregar(TextField txt) {
        String texto = txt.getText().trim();
        if (!texto.isEmpty()) {
            lista.getChildren().add(crearTarea(texto));
            txt.clear();
            actualizar();
        }
    }

    private CheckBox crearTarea(String texto) {
        boolean urgente = texto.startsWith("!");
        CheckBox tarea = new CheckBox(urgente ? texto.substring(1).trim() : texto);
        tarea.setMaxWidth(Double.MAX_VALUE);
        tarea.selectedProperty().addListener((obs, antes, ahora) -> actualizar());
        // TODO T1: añade la clase "tarea" y, si es urgente, también la clase "urgente"
        return tarea;
    }

    private void actualizar() {
        long pendientes = lista.getChildren().stream()
                .filter(n -> !((CheckBox) n).isSelected()).count();
        contador.setText(pendientes + " pendientes de " + lista.getChildren().size());
        btnEliminar.setDisable(pendientes == lista.getChildren().size());
    }

    /** Cambia el tamaño de letra de toda la ventana. */
    private void cambiarLetra(VBox raiz, int cambio) {
        tamanoLetra = Math.max(10, Math.min(20, tamanoLetra + cambio));
        // TODO T5: aplica tamanoLetra a la raíz con setStyle
    }

    public static void main(String[] args) {
        launch(args);
    }
}
