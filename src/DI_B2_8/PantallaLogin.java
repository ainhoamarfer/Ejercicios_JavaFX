package DI_B2_8;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** Pantalla 1: identificación con tarjeta y PIN. */
public class PantallaLogin extends Pantalla {

    private final Label terminal = new Label("INTRODUZCA SU TARJETA Y PIN");
    private final Label lblTarjeta = new Label("_Número de tarjeta");
    private final TextField txtTarjeta = new TextField();
    private final Label lblPin = new Label("_PIN (4 dígitos)");
    private final PasswordField txtPin = new PasswordField();
    private final Button btnAceptar = new Button("Aceptar");
    private final TecladoNumerico teclado = new TecladoNumerico(false);

    public PantallaLogin(Main app, Banco banco) {
        super(app, banco);

        terminal.getStyleClass().add("terminal");
        terminal.setMaxWidth(Double.MAX_VALUE);
        txtTarjeta.setId("txtTarjeta");
        txtTarjeta.setPromptText("16 dígitos");
        txtPin.setId("txtPin");
        txtPin.setPromptText("••••");
        txtPin.setMaxWidth(120);
        btnAceptar.setId("btnAceptar");
        btnAceptar.getStyleClass().add("boton-principal");
        btnAceptar.setOnAction(e -> aceptar());
        lblTarjeta.setId("lblTarjeta");
        lblPin.setId("lblPin");
        lblTarjeta.getStyleClass().add("etiqueta-campo");
        lblPin.getStyleClass().add("etiqueta-campo");

        // Distribución (TERMINADO). Sírvete de ella como ejemplo de comentario LAYOUT:
        // LAYOUT: un VBox para el formulario, porque sus elementos van uno debajo de otro,
        // y un HBox centrado que coloca el formulario y el teclado uno al lado del otro.
        // El orden de los hijos del VBox es también el orden de tabulación (tarjeta → PIN → Aceptar).
        VBox formulario = new VBox(8, terminal, lblTarjeta, txtTarjeta, lblPin, txtPin, btnAceptar);
        formulario.setId("formulario");
        formulario.getStyleClass().add("formulario");
        HBox contenido = new HBox(30, formulario, teclado);
        contenido.setAlignment(Pos.CENTER);
        setCenter(contenido);

        // Solo dígitos: hasta 16 en la tarjeta y 4 en el PIN (TERMINADO)
        limitar(txtTarjeta, "\\d", "\\d{0,16}");
        limitar(txtPin, "\\d", "\\d{0,4}");

        // TODO C1.1 — INICIO: Eventos de la pantalla de identificación (apartado C1)
        //   Nombres que usan los tests: txtTarjeta, txtPin, btnAceptar (ids ya asignados).
        //   Se comprueba: 16 dígitos en txtTarjeta -> foco en txtPin; Enter -> mismo efecto que btnAceptar;
        //   Esc -> txtTarjeta y txtPin vacíos y foco en txtTarjeta (método limpiar()).


        // TODO C1.1 — FIN

        // El teclado en pantalla escribe en el último campo que ha tenido el foco (TERMINADO)
        teclado.setDestino(txtTarjeta);
        txtTarjeta.focusedProperty().addListener((obs, antes, ahora) -> {
            if (ahora) {
                teclado.setDestino(txtTarjeta);
            }
        });
        txtPin.focusedProperty().addListener((obs, antes, ahora) -> {
            if (ahora) {
                teclado.setDestino(txtPin);
            }
        });

        // Clic en una etiqueta: el foco pasa a su campo (TERMINADO)
        lblTarjeta.setOnMouseClicked(e -> txtTarjeta.requestFocus());
        lblPin.setOnMouseClicked(e -> txtPin.requestFocus());

        // Mnemónicos: Alt+N lleva a la tarjeta y Alt+P al PIN (TERMINADO)
        lblTarjeta.setMnemonicParsing(true);
        lblTarjeta.setLabelFor(txtTarjeta);
        lblPin.setMnemonicParsing(true);
        lblPin.setLabelFor(txtPin);
    }

    private void aceptar() {
        // TODO E1.1 — INICIO: Resultado de la identificación (apartado E1)
        //   Textos que usan los tests (cabeceras de los avisos, con app.alerta):
        //   "Error de identificación" y "Tarjeta bloqueada". Mensajes exactos: ver enunciado, E1.
        //   Después: txtPin vacío y con el foco (incorrecto); ambos campos vacíos y foco en txtTarjeta (bloqueada).
        // Provisional: solo entra si los datos son correctos, sin avisar de nada
        if (banco.login(txtTarjeta.getText(), txtPin.getText()) == Banco.ResultadoLogin.CORRECTO) {
            app.iniciarSesion();
        }
        // TODO E1.1 — FIN
    }

    private void limpiar() {
        txtTarjeta.clear();
        txtPin.clear();
        txtTarjeta.requestFocus();
    }

    @Override
    public void enfocar() {
        // TODO D1.3 — INICIO: Foco inicial de la identificación (apartado D1)
        //   Se comprueba: foco en txtTarjeta.


        // TODO D1.3 — FIN
    }

    @Override
    public String ayuda() {
        return "Escriba los 16 dígitos de la tarjeta y su PIN de 4 dígitos.\n\n"
                + "Tab: siguiente campo   ·   Alt+N: tarjeta   ·   Alt+P: PIN\n"
                + "Enter: aceptar   ·   Esc: borrar los campos\n"
                + "Ctrl + / Ctrl −: tamaño de letra   ·   F2: grafo de escena";
    }
}
