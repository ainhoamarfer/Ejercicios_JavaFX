package DI_B2_8;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

/**
 * Clase abstracta común a todas las pantallas del cajero.
 * Cada pantalla se coloca en el centro del marco de la aplicación.
 */
public abstract class Pantalla extends BorderPane {

    protected final Main app;
    protected final Banco banco;

    protected Pantalla(Main app, Banco banco) {
        this.app = app;
        this.banco = banco;
        getStyleClass().add("pantalla");
    }

    /** Coloca el foco en el control adecuado al mostrarse la pantalla. */
    public abstract void enfocar();

    /** Texto de ayuda de la pantalla, que se muestra con F1. */
    public abstract String ayuda();

    /** Atajo: el campo solo admite un PIN (hasta 4 dígitos). */
    public static void limitarPin(TextField campo) {
        limitar(campo, "\\d", "\\d{0,4}");
    }

    /**
     * Limita lo que puede contener un campo de texto.
     * @param campo                campo que se limita
     * @param caracteresPermitidos expresión regular de UN carácter permitido (por ejemplo "\\d")
     * @param patronCompleto       expresión regular que debe cumplir SIEMPRE el texto completo
     */
    protected static void limitar(TextField campo, String caracteresPermitidos, String patronCompleto) {
        // Filtro: impide ESCRIBIR con el teclado caracteres no permitidos
        campo.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            if (!e.getCharacter().matches(caracteresPermitidos)) {
                e.consume();
            }
        });
        // Listener: deshace cualquier otro cambio que no cumpla el patrón
        // (teclado en pantalla, pegar con Ctrl+V, setText...). El filtro no los ve.
        campo.textProperty().addListener((obs, viejo, nuevo) -> {
            if (!nuevo.matches(patronCompleto)) {
                campo.setText(viejo);
            }
        });
    }
}
