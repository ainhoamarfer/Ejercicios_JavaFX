package DI_B2_8;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

/** B2.8 — Cajero del Banco de Springfield. */
public class Main extends Application {

    private static final Locale ES = Locale.of("es", "ES");
    private static final String TITULO = "Cajero automático";

    private final Banco banco = new Banco();
    // TODO A1.1 — INICIO: Tipo del marco de la aplicación (apartado A1)
    //   Nombres que usan los tests (no los cambies):
    //   - atributo marco (id "marco", ya asignado), raíz de la escena
    //   - hijos del marco: cabecera (id "cabecera"), barraEstado (id "barraEstado")
    //     y la pantalla actual (clase "pantalla")
    //   - método mostrar(Pantalla p): deja la pantalla recibida como única pantalla del marco

    private final BorderPane marco = new BorderPane();

    // TODO A1.1 — FIN
    private final Label lblFoco = new Label("Foco: —");
    private final PauseTransition temporizador = new PauseTransition(Duration.seconds(30));
    private Stage ventana;
    private Scene scene;
    private int tamanoLetra = 14;
    private Stage recibo;
    private Stage inspector;
    private final TextArea txtArbol = new TextArea();

    @Override
    public void start(Stage stage) {
        ventana = stage;
        marco.setId("marco");
        txtArbol.setId("txtArbol");
        txtArbol.getStyleClass().add("arbol");

        // ---------- Cabecera: logotipo (un donut dibujado con dos círculos) y título ----------
        Circle masa = new Circle(16);
        masa.getStyleClass().add("donut-masa");
        Circle agujero = new Circle(6);
        agujero.getStyleClass().add("donut-agujero");
        StackPane logo = new StackPane(masa, agujero);
        Label titulo = new Label("Banco de Springfield");
        titulo.getStyleClass().add("titulo-banco");
        HBox cabecera = new HBox(12, logo, titulo);
        cabecera.setId("cabecera");
        cabecera.getStyleClass().add("cabecera");
        cabecera.setAlignment(Pos.CENTER_LEFT);

        // ---------- Barra de estado ----------
        lblFoco.setId("lblFoco");
        HBox barraEstado = new HBox(lblFoco);
        barraEstado.setId("barraEstado");
        barraEstado.getStyleClass().add("barra-estado");

        // TODO A1.2 — INICIO: Colocar la cabecera y la barra de estado (apartado A1)
        //   Se comprueba: cabecera (id "cabecera") arriba y barraEstado (id "barraEstado") abajo, a todo el ancho.
        marco.setTop(cabecera);
        marco.setBottom(barraEstado);

        //marco.getChildren().addAll(cabecera, barraEstado);
        // TODO A1.2 — FIN

        scene = new Scene(marco);
        scene.getStylesheets().add(getClass().getResource("cajero.css").toExternalForm());

        // TODO D1.1 — INICIO: Monitor de foco en la barra de estado (apartado D1)
        //   Nombres que usan los tests:
        //   - etiqueta lblFoco (id "lblFoco"): texto "Foco: <id> (<Tipo>)", generado con describir(nodo)
        //   - foco inicial con el método enfocar() de cada pantalla (TODO D1.2 a D1.5)

        scene.focusOwnerProperty().addListener((observ, viejo, nuevo) -> {
            lblFoco.setText(describir(nuevo));
        });
        // TODO D1.1 — FIN

        // Título de la ventana según esté activa o no (TERMINADO)
        stage.focusedProperty().addListener((obs, antes, ahora) ->
                stage.setTitle(ahora ? TITULO : TITULO + " (en espera)"));

        // F2 abre la ventana del grafo de escena (TERMINADO)
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.F2), this::abrirInspector);

        // Ctrl + y Ctrl − del teclado numérico cambian el tamaño de letra (TERMINADO)
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.ADD, KeyCombination.SHORTCUT_DOWN),
                () -> cambiarLetra(2));
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.SUBTRACT, KeyCombination.SHORTCUT_DOWN),
                () -> cambiarLetra(-2));

        // TODO D3.1 — INICIO: Atajo F1 para la ayuda (apartado D3)
        //   Se comprueba: F1, con el foco en cualquier control y en cualquier pantalla, llama a mostrarAyuda().
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.F1), this::mostrarAyuda);


        // TODO D3.1 — FIN

        // Cierre de sesión por inactividad (TERMINADO)
        scene.addEventFilter(InputEvent.ANY, e -> {
            if (banco.haySesion()) {
                temporizador.playFromStart();
            }
        });
        temporizador.setOnFinished(e -> {
            if (banco.haySesion()) {
                cerrarSesion();
                // showAndWait() no se puede llamar al terminar una animación: se aplaza
                Platform.runLater(() -> alerta(Alert.AlertType.INFORMATION, "Sesión cerrada",
                        "Sesión cerrada por inactividad."));
            }
        });

        // Confirmar el cierre con una sesión abierta (TERMINADO)
        stage.setOnCloseRequest(e -> {
            if (banco.haySesion() && !confirmar("Hay una sesión abierta. ¿Cerrar el cajero?")) {
                e.consume();
            }
        });

        stage.setTitle(TITULO);
        stage.setScene(scene);

        // TODO A5.1 — INICIO: Tamaño y posición de la ventana (apartado A5)
        //   Se comprueba sobre la ventana principal (stage):
        //   - getWidth() y getHeight() = 2/3 del ancho y del alto del área útil de la pantalla principal
        //   - getX() y getY(): ventana centrada en esa área (tolerancia de 2 px)

        Rectangle2D pantalla = Screen.getPrimary().getVisualBounds();

        stage.setWidth(pantalla.getWidth() * 1/3);    // provisional
        stage.setHeight(pantalla.getHeight() * 2/3);

        stage.centerOnScreen();
        // TODO A5.1 — FIN
        stage.show();
        mostrar(new PantallaLogin(this, banco));
    }

    // =========================================================
    // Cambio de pantallas y sesión
    // =========================================================

    /** Coloca una pantalla en la zona central del marco y le da su foco inicial. */
    public void mostrar(Pantalla p) {
        // TODO A1.3 — INICIO: Colocar la pantalla recibida en la zona central (apartado A1)
        //   Se comprueba: la pantalla recibida (clase "pantalla") es la única pantalla del marco y ocupa el centro.
        marco.getChildren().removeIf(n -> n instanceof Pantalla);
        //marco.getChildren().add(p);

        marco.setCenter(p);
        // TODO A1.3 — FIN

        // TODO D1.2 — INICIO: Foco inicial al mostrar una pantalla (apartado D1)
        //   Se comprueba: tras mostrar una pantalla, el foco queda en el control que indica su método enfocar().
        Platform.runLater(p::enfocar);

        // TODO D1.2 — FIN
        actualizarInspector();
    }

    public void mostrarMenu() {
        mostrar(new PantallaMenu(this, banco));
    }

    public void iniciarSesion() {
        temporizador.playFromStart();
        mostrarMenu();
    }

    public void cerrarSesion() {
        banco.logout();
        temporizador.stop();
        if (recibo != null) {
            recibo.close();
        }
        mostrar(new PantallaLogin(this, banco));
    }

    public Stage getVentana() {
        return ventana;
    }

    // =========================================================
    // Ventanas no modales
    // =========================================================

    /** Muestra el recibo de una operación en una ventana aparte. */
    public void mostrarRecibo(String operacion, double importe, double saldo) {
        Label cabecera = new Label("BANCO DE SPRINGFIELD");
        cabecera.getStyleClass().add("recibo-titulo");
        Label fecha = new Label(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        Label lblOperacion = new Label(operacion.toUpperCase() + ": " + euros(importe));
        Label lblSaldo = new Label("SALDO: " + euros(saldo));
        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setId("btnCerrarRecibo");
        VBox contenido = new VBox(10, cabecera, fecha, lblOperacion, lblSaldo, btnCerrar);
        contenido.setId("recibo");
        contenido.getStyleClass().add("recibo");
        contenido.setAlignment(Pos.CENTER);

        // TODO E3.1 — INICIO: Recibo en una ventana no modal (apartado E3)
        //   Nombres que usan los tests:
        //   - atributo recibo (Stage): título "Recibo", propietaria ventana, 300 x 260
        //   - contenido con id "recibo" y botón con id "btnCerrarRecibo" (ya creados)
        //   - nunca hay más de una ventana visible con el título "Recibo"
        

        //la otra escena se hizo asi
        //scene.getStylesheets().add(getClass().getResource("cajero.css").toExternalForm());


        Scene escena = new Scene(contenido, 300, 260);
        //añade estilos de la otra escena
        escena.getStylesheets().addAll(scene.getStylesheets());
        recibo = new Stage();
        recibo.setTitle("Título recibo");

        //el dueño de recibo es ventana
        recibo.initOwner(ventana);

        recibo.initModality(Modality.NONE);
        recibo.setScene(escena);
        recibo.show();
        btnCerrar.setOnAction(e -> recibo.close());
        ventana.requestFocus();

        // TODO E3.1 — FIN
    }

    /** Abre (o trae al frente) la ventana que muestra el grafo de escena de la pantalla actual. */
    private void abrirInspector() {
        if (inspector != null && inspector.isShowing()) {
            inspector.toFront();
            return;
        }
        txtArbol.setEditable(false);
        Scene escena = new Scene(txtArbol, 480, 520);
        escena.getStylesheets().addAll(scene.getStylesheets());
        inspector = new Stage();
        inspector.setTitle("Grafo de escena");
        inspector.initOwner(ventana);
        inspector.initModality(Modality.NONE);
        inspector.setScene(escena);
        inspector.show();
        actualizarInspector();
    }

    private void actualizarInspector() {
        if (inspector != null && inspector.isShowing()) {
            txtArbol.setText(describirArbol(marco, 0));
        }
    }

    /**
     * Devuelve una descripción del árbol de nodos que cuelga de «nodo», con una línea por nodo
     * y dos espacios de sangría por cada nivel de profundidad.
     */
    public String describirArbol(Node nodo, int nivel) {
        StringBuilder sb = new StringBuilder();
        sb.append("  ".repeat(nivel)).append(nodo.getClass().getSimpleName());
        if (nodo.getId() != null) {
            sb.append(" #").append(nodo.getId());
        }
        for (String clase : nodo.getStyleClass()) {
            sb.append(" .").append(clase);
        }
        sb.append('\n');
        // Los controles son hojas: no se recorren sus nodos internos (su «skin»)
        if (nodo instanceof Parent padre && !(nodo instanceof Control)) {
            for (Node hijo : padre.getChildrenUnmodifiable()) {
                sb.append(describirArbol(hijo, nivel + 1));
            }
        }
        return sb.toString();
    }

    // =========================================================
    // Diálogos (TERMINADOS: úsalos en tus TODO)
    // =========================================================

    /** Crea un Alert con la ventana principal como propietaria y la hoja de estilos del cajero. */
    public Alert crearAlerta(Alert.AlertType tipo, String cabecera, String contenido) {
        Alert a = new Alert(tipo, contenido);
        a.setTitle(TITULO);
        a.setHeaderText(cabecera);
        a.initOwner(ventana);
        a.getDialogPane().getStylesheets().addAll(scene.getStylesheets());
        return a;
    }

    /** Muestra un Alert y espera a que el usuario lo cierre. */
    public void alerta(Alert.AlertType tipo, String cabecera, String contenido) {
        crearAlerta(tipo, cabecera, contenido).showAndWait();
    }

    /** Pide confirmación. Devuelve true si el usuario pulsa Aceptar. */
    public boolean confirmar(String mensaje) {
        Optional<ButtonType> r = crearAlerta(Alert.AlertType.CONFIRMATION, null, mensaje).showAndWait();
        return r.isPresent() && r.get() == ButtonType.OK;
    }

    private void mostrarAyuda() {
        if (marco.getChildren().isEmpty()) {
            return;
        }
        for (Node n : marco.getChildren()) {
            if (n instanceof Pantalla p) {
                alerta(Alert.AlertType.INFORMATION, "Ayuda", p.ayuda());
            }
        }
    }

    // =========================================================
    // Utilidades
    // =========================================================

    private void cambiarLetra(int delta) {
        // Tamaño de letra entre 12 y 24 (TERMINADO)
        tamanoLetra = Math.max(12, Math.min(24, tamanoLetra + delta));
        // Valor calculado en ejecución: uno de los pocos casos en que setStyle() está justificado
        marco.setStyle("-fx-font-size: " + tamanoLetra + "px;");
    }

    /** Texto del monitor de foco: «Foco: id (Tipo)», o «Foco: —» si no hay ningún nodo. */
    private String describir(Node n) {
        if (n == null) {
            return "Foco: —";
        }
        String id = n.getId() != null ? n.getId() : "(sin id)";
        return "Foco: " + id + " (" + n.getClass().getSimpleName() + ")";
    }

    /** Formato de moneda español: 1.250,00 €. */
    public static String euros(double cantidad) {
        return String.format(ES, "%,.2f €", cantidad);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
