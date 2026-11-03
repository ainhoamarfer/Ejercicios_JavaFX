package DI_B2_4;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/** Una bola es un círculo de tamaño y color aleatorios. (No hace falta modificar esta clase). */
public class Bola extends Circle {

    public Bola(double x, double y) {
        super(x, y, 20 + Math.random() * 15);
        colorAleatorio();
        getStyleClass().add("bola");
    }

    /** Cambia el relleno de la bola por un color aleatorio. */
    public void colorAleatorio() {
        setFill(Color.hsb(Math.random() * 360, 0.7, 0.95));
    }
}
