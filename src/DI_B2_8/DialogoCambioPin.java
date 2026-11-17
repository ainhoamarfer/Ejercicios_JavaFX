package DI_B2_8;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Window;

/**
 * Diálogo modal para cambiar el PIN.
 * Devuelve el PIN nuevo si el usuario pulsa «Guardar», o nada si cancela.
 */
public class DialogoCambioPin extends Dialog<String> {

    private final PasswordField txtActual = new PasswordField();
    private final PasswordField txtNuevo = new PasswordField();
    private final PasswordField txtRepetir = new PasswordField();
    private final ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);

    public DialogoCambioPin(Window propietaria, Banco banco) {
        txtActual.setId("txtPinActual");
        txtNuevo.setId("txtPinNuevo");
        txtRepetir.setId("txtPinRepetir");
        Pantalla.limitarPin(txtActual);
        Pantalla.limitarPin(txtNuevo);
        Pantalla.limitarPin(txtRepetir);

        setTitle("Cambiar PIN");
        setHeaderText("Introduzca su PIN actual y el nuevo PIN de 4 dígitos");

        // Propietaria y modalidad
        initOwner(propietaria);
        initModality(Modality.WINDOW_MODAL);
        // Hojas de estilo de la ventana principal
        getDialogPane().getStylesheets().addAll(propietaria.getScene().getStylesheets());

        // Botones
        getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        // LAYOUT: GridPane, porque es un formulario de dos columnas (etiquetas y campos)
        // en el que cada etiqueta debe quedar alineada con su campo.
        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.addRow(0, new Label("PIN actual:"), txtActual);
        formulario.addRow(1, new Label("PIN nuevo:"), txtNuevo);
        formulario.addRow(2, new Label("Repetir PIN nuevo:"), txtRepetir);
        getDialogPane().setContent(formulario);

        // «Guardar» solo está habilitado si los tres campos tienen 4 dígitos y los dos nuevos coinciden
        Button guardar = (Button) getDialogPane().lookupButton(btnGuardar);
        guardar.setId("btnGuardarPin");
        Runnable comprobar = () -> guardar.setDisable(
                txtActual.getText().length() != 4
                || txtNuevo.getText().length() != 4
                || !txtNuevo.getText().equals(txtRepetir.getText()));
        txtActual.textProperty().addListener((obs, viejo, nuevo) -> comprobar.run());
        txtNuevo.textProperty().addListener((obs, viejo, nuevo) -> comprobar.run());
        txtRepetir.textProperty().addListener((obs, viejo, nuevo) -> comprobar.run());
        comprobar.run();

        // PIN actual incorrecto: aviso y el diálogo NO se cierra
        guardar.addEventFilter(ActionEvent.ACTION, e -> {
            if (!banco.pinCorrecto(txtActual.getText())) {
                Alert error = new Alert(Alert.AlertType.ERROR, "El PIN actual no es correcto.");
                error.setHeaderText(null);
                error.initOwner(getDialogPane().getScene().getWindow());
                error.getDialogPane().getStylesheets().addAll(getDialogPane().getStylesheets());
                error.showAndWait();
                txtActual.requestFocus();
                txtActual.selectAll();
                e.consume();
            }
        });

        // Resultado: el PIN nuevo si se pulsa «Guardar»; null (vacío) si se cancela
        setResultConverter(boton -> boton == btnGuardar ? txtNuevo.getText() : null);

        // Foco inicial en el PIN actual
        Platform.runLater(txtActual::requestFocus);
    }
}
