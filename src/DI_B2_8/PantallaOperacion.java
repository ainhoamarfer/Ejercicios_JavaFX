package DI_B2_8;

import java.util.Optional;

import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/** Pantalla 3: ingresar o retirar dinero. */
public class PantallaOperacion extends Pantalla {

    private static final double LIMITE_CONFIRMACION = 300;
    private static final PseudoClass ERROR = PseudoClass.getPseudoClass("error");

    private final boolean ingreso;
    private final Label terminal = new Label();
    private final Label lblImporte = new Label("_Importe (€)");
    private final TextField txtImporte = new TextField();
    private final Button btnCancelar = new Button("Cancelar");
    private final Button btnAceptar = new Button("Aceptar");
    private final TecladoNumerico teclado = new TecladoNumerico(true);
    private final Button[] rapidos;

    public PantallaOperacion(Main app, Banco banco, boolean ingreso) {
        super(app, banco);
        this.ingreso = ingreso;

        terminal.getStyleClass().add("terminal");
        terminal.setMaxWidth(Double.MAX_VALUE);
        terminal.setText("SU SALDO ACTUAL ES " + Main.euros(banco.getSaldoActual())
                + "\nINSERTE CANTIDAD A " + (ingreso ? "INGRESAR" : "RETIRAR"));
        txtImporte.setId("txtImporte");
        txtImporte.setPrefColumnCount(8);
        lblImporte.setId("lblImporte");
        lblImporte.getStyleClass().add("etiqueta-campo");
        lblImporte.setMnemonicParsing(true);
        lblImporte.setLabelFor(txtImporte);
        teclado.setDestino(txtImporte);
        limitar(txtImporte, "[\\d,]", "\\d{0,6}(,\\d{0,2})?");

        btnCancelar.setId("btnCancelar");
        btnCancelar.setCancelButton(true);
        btnCancelar.setOnAction(e -> app.mostrarMenu());
        btnAceptar.setId("btnAceptar");
        btnAceptar.getStyleClass().add("boton-principal");
        btnAceptar.setDefaultButton(true);
        btnAceptar.setOnAction(e -> aceptar());

        // Importes rápidos: solo al retirar
        int[] cantidades = ingreso ? new int[0] : new int[] { 20, 50, 100, 200, 500 };
        rapidos = new Button[cantidades.length];
        for (int i = 0; i < cantidades.length; i++) {
            int cantidad = cantidades[i];
            Button b = new Button(cantidad + " €");
            b.setId("rapido" + cantidad);
            b.getStyleClass().add("importe-rapido");
            b.setOnAction(e -> {
                txtImporte.setText(String.valueOf(cantidad));
                txtImporte.requestFocus();
                txtImporte.positionCaret(txtImporte.getText().length());
            });
            // TODO D2.2 — INICIO: Los importes rápidos no reciben el foco (apartado D2)
            //   Se comprueba: los botones con clase "importe-rapido" (ids rapido20 … rapido500) no reciben el foco.


            // TODO D2.2 — FIN
            rapidos[i] = b;
        }

        // TODO A4.1 — INICIO: Distribución de la pantalla de operación (apartado A4)
        //   Nombres que usan los tests:
        //   - contenedor del formulario: id "formulario" y clase "formulario"
        //   - contenedor de los importes rápidos: id "importesRapidos"
        //   - ya creados: lblImporte (id "lblImporte") y txtImporte en la misma fila; btnCancelar y btnAceptar
        //   Obligatorio: comentario // LAYOUT: con la justificación.
        
        HBox opciones = new HBox(btnCancelar, btnAceptar);
        opciones.setAlignment(Pos.CENTER_RIGHT);

        VBox formulario = new VBox(terminal, new HBox(lblImporte,txtImporte), new FlowPane(rapidos), opciones); 
        formulario.setSpacing(12);
       
        HBox contenido = new HBox(formulario, teclado);
        contenido.setSpacing(30);

        setCenter(contenido);
        // TODO A4.1 — FIN

        // TODO C3.1 — INICIO: Importe marcado como erróneo mientras se escribe (apartado C3)
        //   Nombres que usan los tests: pseudo-clase "error" (constante ERROR) en txtImporte; usa validar(texto).


        // TODO C3.1 — FIN
    }

    /** Convierte el texto del importe en número; 0 si está vacío o no es válido. */
    private double leerImporte(String texto) {
        try {
            return texto.isEmpty() ? 0 : Double.parseDouble(texto.replace(',', '.'));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /**
     * Comprueba el importe escrito.
     * @return null si es válido, o el mensaje de error si no lo es
     */
    private String validar(String texto) {
        double cantidad = leerImporte(texto);
        if (cantidad <= 0) {
            return "Cantidad no válida.";
        }
        if (!ingreso && Math.round(cantidad * 100) % 1000 != 0) {
            return "Este cajero solo dispensa billetes de 10, 20 y 50 €.";
        }
        if (!ingreso && cantidad > banco.getSaldoActual()) {
            return "Fondos insuficientes.";
        }
        return null;
    }

    private void aceptar() {
        double cantidad = leerImporte(txtImporte.getText());
        String error = validar(txtImporte.getText());

        // TODO E2.1 — INICIO: Avisos, confirmación y recibo de la operación (apartado E2)
        //   Textos que usan los tests: avisos con las cabeceras "Importe" (advertencia) y "¡D'oh!" (error);
        //   confirmación con confirmarRetirada(cantidad) solo si se retiran más de LIMITE_CONFIRMACION;
        //   recibo con app.mostrarRecibo(...) y vuelta al menú con app.mostrarMenu().
        // Provisional: hace la operación sin avisos ni recibo
        if (error == null) {
            if (ingreso) {
                banco.ingresar(cantidad);
            } else {
                banco.retirar(cantidad);
            }
            app.mostrarMenu();
        }
        // TODO E2.1 — FIN
    }

    /** Pide confirmación para una retirada elevada. Devuelve true si el usuario confirma. */
    private boolean confirmarRetirada(double cantidad) {
        // TODO E2.2 — INICIO: Confirmación de una retirada elevada (apartado E2)
        //   Botones que usan los tests: "Retirar <importe>" (el de aceptar) y "Cambiar importe" (el de cancelar).
        //   Devuelve true solo si se elige retirar.
        return true;   // provisional
        // TODO E2.2 — FIN
    }

    /** Tras cualquier aviso, el foco vuelve al importe con el texto seleccionado. */
    private void volverAlImporte() {
        // TODO D2.3 — INICIO: Devolver el foco al importe tras un aviso (apartado D2)
        //   Se comprueba: foco en txtImporte con todo su texto seleccionado.


        // TODO D2.3 — FIN
    }

    @Override
    public void enfocar() {
        // TODO D1.5 — INICIO: Foco inicial de la operación (apartado D1)
        //   Se comprueba: foco en txtImporte.


        // TODO D1.5 — FIN
    }

    @Override
    public String ayuda() {
        return "Escriba el importe con el teclado o con el teclado en pantalla. "
                + "Use la coma para los céntimos.\n\n"
                + "Enter: aceptar   ·   Esc: volver al menú   ·   Alt+I: importe\n"
                + (ingreso ? "" : "Solo se dispensan múltiplos de 10 €. Por encima de 300 € se pide confirmación.\n")
                + "Ctrl + / Ctrl −: tamaño de letra   ·   F2: grafo de escena";
    }
}
