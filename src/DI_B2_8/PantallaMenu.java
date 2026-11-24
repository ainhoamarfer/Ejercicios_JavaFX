package DI_B2_8;

import java.util.Optional;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/** Pantalla 2: menú de operaciones. */
public class PantallaMenu extends Pantalla {

    private final Label terminal = new Label();
    private final Button btnIngresar = new Button("1   Ingresar");
    private final Button btnRetirar = new Button("2   Retirar");
    private final Button btnSaldo = new Button("3   Saldo");
    private final Button btnCambiarPin = new Button("4   Cambiar PIN");
    private final Button btnSalir = new Button("5   Salir");

    public PantallaMenu(Main app, Banco banco) {
        super(app, banco);

        terminal.getStyleClass().add("terminal");
        terminal.setMaxWidth(Double.MAX_VALUE);
        terminal.setText("BIENVENIDA/O, " + banco.getClienteActual().getNombre().toUpperCase()
                + "\nSELECCIONE UNA OPERACIÓN");

        btnIngresar.setId("btnIngresar");
        btnRetirar.setId("btnRetirar");
        btnSaldo.setId("btnSaldo");
        btnCambiarPin.setId("btnCambiarPin");
        btnSalir.setId("btnSalir");
        for (Button b : new Button[] { btnIngresar, btnRetirar, btnSaldo, btnCambiarPin, btnSalir }) {
            b.getStyleClass().add("boton-menu");
        }

        btnIngresar.setOnAction(e -> app.mostrar(new PantallaOperacion(app, banco, true)));
        btnRetirar.setOnAction(e -> app.mostrar(new PantallaOperacion(app, banco, false)));
        btnSaldo.setOnAction(e -> terminal.setText("SU SALDO ACTUAL ES "
                + Main.euros(banco.getSaldoActual()) + "\nSELECCIONE UNA OPERACIÓN"));
        btnCambiarPin.setOnAction(e -> cambiarPin());
        btnSalir.setOnAction(e -> {
            if (app.confirmar("¿Desea finalizar la sesión?")) {
                app.cerrarSesion();
            }
        });

        // TODO A3.1 — INICIO: Distribución del menú (apartado A3)
        //   Nombres que usan los tests:
        //   - contenedor de los cinco botones: id "menuOperaciones" y clase "menu-operaciones"
        //   - contenedor de terminal + botones: clase "formulario"
        //   - botones (ya creados): btnIngresar, btnRetirar, btnSaldo, btnCambiarPin, btnSalir
        //   Obligatorio: comentario // LAYOUT: con la justificación.
        
        GridPane menuOperaciones = new GridPane();

        menuOperaciones.getStyleClass().add("menu_operaciones");
       
        menuOperaciones.setId("menuOperaciones");
        ColumnConstraints c0 = new ColumnConstraints();
        c0.setPercentWidth(50);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(50);

        menuOperaciones.add(btnIngresar, 0, 0);
        btnIngresar.setMaxWidth(Double.MAX_VALUE);

        menuOperaciones.add(btnRetirar, 1, 0);
        btnRetirar.setMaxWidth(Double.MAX_VALUE);

        menuOperaciones.add(btnSaldo, 0, 1);
        btnSaldo.setMaxWidth(Double.MAX_VALUE);

        menuOperaciones.add(btnCambiarPin, 1, 1);
        btnCambiarPin.setMaxWidth(Double.MAX_VALUE);

        menuOperaciones.add(btnSalir, 0, 2, 2, 1);
        menuOperaciones.setColumnSpan(btnSalir, 2);
        btnSalir.setMaxWidth(Double.MAX_VALUE);

        VBox formulario = new VBox();
        formulario.getChildren().addAll(terminal, menuOperaciones);
        formulario.setId("formulario");

        setCenter(formulario);


        
        // TODO A3.1 — FIN

        // TODO C2.1 — INICIO: Teclas del menú (apartado C2)
        //   Se comprueba: DIGIT1..DIGIT5 y NUMPAD1..NUMPAD5 tienen el mismo efecto que
        //   btnIngresar, btnRetirar, btnSaldo, btnCambiarPin y btnSalir; Enter, el del botón con el foco.

          addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.DIGIT1 || e.getCode() == KeyCode.NUMPAD1){
                btnIngresar.fire();
            } else if ( e.getCode() == KeyCode.DIGIT2 || e.getCode() == KeyCode.NUMPAD2){
               btnRetirar.fire();
            } else if ( e.getCode() == KeyCode.DIGIT3 || e.getCode() == KeyCode.NUMPAD3){
               btnSaldo.fire();
            } else if ( e.getCode() == KeyCode.DIGIT4 || e.getCode() == KeyCode.NUMPAD4){
               btnCambiarPin.fire();
            } else if ( e.getCode() == KeyCode.DIGIT5 || e.getCode() == KeyCode.NUMPAD5){
               btnSalir.fire();
            } 
        });

        // TODO C2.1 — FIN
    }

    /** Abre el diálogo de cambio de PIN y aplica el resultado. */
    private void cambiarPin() {
        // Abre el diálogo y, si devuelve un PIN, lo cambia en el banco (TERMINADO)
        DialogoCambioPin dialogo = new DialogoCambioPin(app.getVentana(), banco);
        Optional<String> nuevo = dialogo.showAndWait();
        if (nuevo.isPresent()) {
            banco.cambiarPin(nuevo.get());
            app.alerta(Alert.AlertType.INFORMATION, "PIN cambiado",
                    "Su nuevo PIN ya está activo. No lo comparta con nadie.");
        }
        btnCambiarPin.requestFocus();
    }

    @Override
    public void enfocar() {
        // TODO D1.4 — INICIO: Foco inicial del menú (apartado D1)
        //   Se comprueba: foco en btnIngresar.
        btnIngresar.requestFocus();

        // TODO D1.4 — FIN
    }

    @Override
    public String ayuda() {
        return "Pulse el número de la operación (1 a 5) o muévase con las flechas o Tab "
                + "y pulse Enter.\n\n1 Ingresar · 2 Retirar · 3 Saldo · 4 Cambiar PIN · 5 Salir\n"
                + "Ctrl + / Ctrl −: tamaño de letra   ·   F2: grafo de escena";
    }
}
