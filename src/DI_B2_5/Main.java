package DI_B2_5;

import javafx.application.Application;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** B2.5 — Gimnasio del MI6: alta de agentes con listeners. */
public class Main extends Application {

    private static final int MAX_NOMBRE = 30;
    private static final PseudoClass ERROR = PseudoClass.getPseudoClass("error");

    private final TextField txtNombre = new TextField();
    private final Label lblContador = new Label("0/" + MAX_NOMBRE);
    private final TextField txtCorreo = new TextField();
    private final Label lblCorreo = new Label();
    private final ToggleGroup grupoCuota = new ToggleGroup();
    private final CheckBox chkPiscina = new CheckBox("Piscina (+10 €/mes)");
    private final Label lblTotal = new Label();
    private final CheckBox chkCondiciones = new CheckBox("Acepto la Ley de Secretos Oficiales");
    private final Button btnRegistrar = new Button("Registrar");
    private RadioButton rbBasica;

    @Override
    public void start(Stage stage) {
        // ---------- Cabecera ----------
        Label titulo = new Label("GIMNASIO DEL MI6");
        titulo.getStyleClass().add("titulo");
        Label subtitulo = new Label("Alta de agentes. Solo para ojos autorizados.");
        subtitulo.getStyleClass().add("subtitulo");

        // ---------- Campos ----------
        txtNombre.setId("txtNombre");
        txtNombre.setPromptText("p. ej. 007");
        lblContador.setId("lblContador");
        lblContador.getStyleClass().add("ayuda");
        txtCorreo.setId("txtCorreo");
        txtCorreo.setPromptText("agente@mi6.gov.uk");
        lblCorreo.setId("lblCorreo");
        lblCorreo.getStyleClass().add("ayuda");

        rbBasica = crearCuota("Básica · 25 €", 25);
        RadioButton rbCompleta = crearCuota("Completa · 40 €", 40);
        RadioButton rbDobleCero = crearCuota("Doble cero · 70 €", 70);
        rbBasica.setId("rbBasica");
        rbCompleta.setId("rbCompleta");
        rbDobleCero.setId("rbDobleCero");
        rbBasica.setSelected(true);
        HBox cuotas = new HBox(14, rbBasica, rbCompleta, rbDobleCero);

        chkPiscina.setId("chkPiscina");
        lblTotal.setId("lblTotal");
        lblTotal.getStyleClass().add("total");
        chkCondiciones.setId("chkCondiciones");

        btnRegistrar.setId("btnRegistrar");
        btnRegistrar.setOnAction(e -> registrar(stage));
        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.setId("btnLimpiar");
        btnLimpiar.setOnAction(e -> limpiar());
        HBox botones = new HBox(10, btnLimpiar, btnRegistrar);
        botones.setAlignment(Pos.CENTER_RIGHT);

        // ---------- Formulario ----------
        GridPane formulario = new GridPane();
        formulario.getStyleClass().add("formulario");
        formulario.addRow(0, new Label("Nombre en clave"), txtNombre, lblContador);
        formulario.addRow(1, new Label("Correo"), txtCorreo, lblCorreo);
        formulario.addRow(2, new Label("Cuota mensual"), cuotas);
        GridPane.setColumnSpan(cuotas, 2);
        formulario.add(chkPiscina, 1, 3, 2, 1);
        formulario.addRow(4, new Label("Total"), lblTotal);
        formulario.add(chkCondiciones, 0, 5, 3, 1);
        formulario.add(botones, 0, 6, 3, 1);

        VBox raiz = new VBox(14, titulo, subtitulo, formulario);
        raiz.getStyleClass().add("contenido");

        // TODO T1: contador de caracteres del nombre (máximo 30)
        txtNombre.textProperty().addListener((obs, viejo, nuevo) -> {

            lblContador.setText(nuevo.length() + "/" + MAX_NOMBRE);

            if (nuevo.length() > 30) {
                txtNombre.setText(viejo);
            }

        });

        // TODO T2: comprobar el correo mientras se escribe
        txtCorreo.textProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo.isBlank()) {
                mostrarAyuda(lblCorreo, "", null);
                txtCorreo.pseudoClassStateChanged(ERROR, false);
            } else if (correoValido(nuevo)) {
                mostrarAyuda(lblCorreo, "Correo OK", "correcto");
                txtCorreo.pseudoClassStateChanged(ERROR, false);
            } else {
                mostrarAyuda(lblCorreo, "Correo NO OK", "incorrecto");
                txtCorreo.pseudoClassStateChanged(ERROR, true);
            }
        });

        // TODO T3: recalcular el total al cambiar la cuota o la piscina
        grupoCuota.selectedToggleProperty().addListener((obs, anterior, actual) -> {
            calcularTotal();
        });

        chkPiscina.selectedProperty().addListener((obs, anterior, actual) -> {
            calcularTotal();
        });


        // TODO T4: Registrar solo se habilita con los datos completos
        //Qué debe pasar: Registrar solo está habilitado si el nombre no está vacío, el correo 
        //es válido y se ha marcado la casilla de las condiciones. 
        //1. Completa el método actualizarBoton(). Usa setDisable(...). 
        //2. Llama a actualizarBoton() cada vez que cambie cualquiera de los tres datos: 
        //desde los listeners de T1 y T2, y desde un listener nuevo en chkCondiciones.

        chkCondiciones.selectedProperty().addListener((obs, anterior, actual) -> {
            actualizarBoton();
        });

        calcularTotal();
        actualizarBoton();

        Scene scene = new Scene(raiz);
        scene.getStylesheets().add(getClass().getResource("mi6.css").toExternalForm());
        stage.setTitle("B2.5 — Gimnasio del MI6");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Habilita «Registrar» solo si el nombre no está vacío, el correo es válido y
     * se aceptan las condiciones.
     */
    private void actualizarBoton() {
        if (txtNombre.getText().isEmpty() || !correoValido(txtCorreo.getText()) || !chkCondiciones.isSelected()) {
            btnRegistrar.setDisable(true);
        } else {
            btnRegistrar.setDisable(false);
        }
    }

    // =========================================================
    // Métodos terminados: úsalos, no hace falta modificarlos
    // =========================================================

    /** Crea un RadioButton de cuota. Su precio se guarda en userData. */
    private RadioButton crearCuota(String texto, int precio) {
        RadioButton rb = new RadioButton(texto);
        rb.setToggleGroup(grupoCuota);
        rb.setUserData(precio);
        rb.setMinWidth(Region.USE_PREF_SIZE);
        return rb;
    }

    /**
     * Calcula el total mensual (cuota elegida + piscina) y lo muestra en lblTotal.
     */
    private void calcularTotal() {
        int total = 0;
        if (grupoCuota.getSelectedToggle() != null) {
            total = (int) grupoCuota.getSelectedToggle().getUserData();
        }
        if (chkPiscina.isSelected()) {
            total += 10;
        }
        lblTotal.setText(total + " €/mes");
    }

    /** Devuelve true si el texto tiene forma de correo: algo@algo.algo */
    private boolean correoValido(String texto) {
        return texto.matches("[\\w.-]+@[\\w-]+(\\.[\\w-]+)+");
    }

    /**
     * Cambia el texto de una etiqueta de ayuda y le deja solo la clase indicada (o
     * ninguna si es null).
     */
    private void mostrarAyuda(Label etiqueta, String texto, String clase) {
        etiqueta.setText(texto);
        etiqueta.getStyleClass().removeAll("correcto", "incorrecto");
        if (clase != null) {
            etiqueta.getStyleClass().add(clase);
        }
    }

    /** Deja el formulario como al principio. */
    private void limpiar() {
        txtNombre.clear();
        txtCorreo.clear();
        rbBasica.setSelected(true);
        chkPiscina.setSelected(false);
        chkCondiciones.setSelected(false);
        txtNombre.requestFocus();
    }

    private void registrar(Stage stage) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION,
                "Agente " + txtNombre.getText() + " registrado.\nCuota: " + lblTotal.getText());
        alerta.setHeaderText("Bienvenido al gimnasio del MI6");
        alerta.initOwner(stage);
        alerta.getDialogPane().getStylesheets().addAll(stage.getScene().getStylesheets());
        alerta.showAndWait();
        limpiar();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
