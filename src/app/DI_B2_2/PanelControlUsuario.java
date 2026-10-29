package app.DI_B2_2;

import java.text.Normalizer.Form;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class PanelControlUsuario extends Application{

    @Override
    public void start(Stage stage) {

      BorderPane root = new BorderPane();

      // Parte de arriba -------------------------------------------------------------------------
      VBox topContainer = new VBox();
      topContainer.setBackground(new Background(new BackgroundFill(Color.web("#DDECF6"), null, null)));

      // Título Azul
      Label titulo = new Label("Panel de Control");
      titulo.setFont(Font.font("System", 24));
      titulo.setTextFill(Color.web("#174A73")); 
      titulo.setPadding(new Insets(10, 10, 10, 10));

      // Barra de menús
      MenuBar menuBar = new MenuBar();
      menuBar.setBackground(new Background(new BackgroundFill(Color.web("#CFE8E2"), null, null)));

      Menu menuArchivo = new Menu("Archivo");
      Menu menuEditar = new Menu("Editar");
      Menu menuAyuda = new Menu("Ayuda");

      menuBar.getMenus().addAll(menuArchivo, menuEditar, menuAyuda);

      // meto todo
      topContainer.getChildren().addAll(titulo, menuBar);
      root.setTop(topContainer);


      // Parte de abajo -------------------------------------------------------------------------

      Label footer = new Label("Versión 1.0 | Usuario activo");
      footer.setMaxWidth(Double.MAX_VALUE);
      footer.setAlignment(Pos.CENTER);
      footer.setPadding(new Insets(5));
      footer.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #cccccc; -fx-border-width: 1 0 0 0;");

      root.setBottom(footer);

      // Parte izquierda -------------------------------------------------------------------------

      VBox izqContainer = new VBox(10);
      izqContainer.setBackground(new Background(new BackgroundFill(Color.web("#DFF2EB"), null, null)));
      izqContainer.setPadding(new Insets(20));
      izqContainer.setPrefWidth(160);

      Button inicio = new Button("Inicio");
      Button perfil = new Button("Perfil");
      Button config = new Button("Configuración");
      Button salir = new Button("Salir");

      inicio.setMaxWidth(Double.MAX_VALUE);
      perfil.setMaxWidth(Double.MAX_VALUE);
      config.setMaxWidth(Double.MAX_VALUE);
      salir.setMaxWidth(Double.MAX_VALUE);

      izqContainer.getChildren().addAll(inicio, perfil, config, salir);

      root.setLeft(izqContainer);

      // Parte desrecha -------------------------------------------------------------------------

      VBox derContainer = new VBox(10);
      derContainer.setBackground(new Background(new BackgroundFill(Color.web("#DFF2EB"), null, null)));
      derContainer.setPadding(new Insets(20));
      derContainer.setPrefWidth(160);

      Button guardar = new Button("guardar");
      Button actualizar = new Button("actualizar");
      Button exportar = new Button("exportar");
      Button eliminar = new Button("eliminar");

      derContainer.getChildren().addAll(guardar, actualizar, eliminar, exportar);

      root.setRight(derContainer);

      // Parte del centro -------------------------------------------------------------------------

      TextField nombre = new TextField("Nombre: ");
      TextField correo = new TextField("Correo: ");
      TextField tele = new TextField("Telefono: ");
      TextField pais = new TextField("Pais: ");
      TextField coment = new TextField("Comentarios: ");

      //Form formulario = new Form();
      //root.setCenter(formulario);

      //---------------------------------------------

      

      // Asignamos un campo de texto en el centro del BorderPane
      root.setCenter(new TextField("Center"));

      Scene scene = new Scene(root, 400, 300);

      stage.setTitle("Panel de Control de Usuario");

        // Asignamos la escena al escenario principal
      stage.setScene(scene);

        // Mostramos la ventana en pantalla
      stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
