TODO A5.1 — INICIO: Tamaño y posición de la ventana (apartado A5)
Se comprueba sobre la ventana principal (stage):
- getWidth() y getHeight() = 2/3 del ancho y del alto del área útil de la pantalla principal
- getX() y getY(): ventana centrada en esa área (tolerancia de 2 px)

Rectangle2D pantalla = Screen.getPrimary().getVisualBounds();
stage.setWidth(pantalla.getWidth() * 2/3);    // provisional
stage.setHeight(pantalla.getHeight() * 2/3);
stage.centerOnScreen();


TODO A1.3 — INICIO: Colocar la pantalla recibida en la zona central (apartado A1)
Se comprueba: la pantalla recibida (clase "pantalla") es la única pantalla del marco y ocupa el centro.

marco.getChildren().removeIf(n -> n instanceof Pantalla);
marco.setCenter(p);

menuOperaciones.add(btnIngresar, 0, 0);
btnIngresar.setMaxWidth(Double.MAX_VALUE);

HBox opciones = new HBox(btnCancelar, btnAceptar);
opciones.setAlignment(Pos.CENTER_RIGHT);

VBox formulario = new VBox(terminal, new HBox(lblImporte,txtImporte), new FlowPane(rapidos), opciones); 
formulario.setSpacing(12);

 txtTarjeta.textProperty().addListener((observer, anterior, actual) -> {
    if (actual.length() == 16){
        txtPin.requestFocus(); //permite asignar el foco
    }
});

 addEventFilter(KeyEvent.KEY_PRESSED, e -> {
    if (e.getCode() == KeyCode.ESCAPE){
        limpiar();
        e.consume();
    }
});