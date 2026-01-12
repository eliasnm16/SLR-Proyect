package controlador;

import java.net.URL;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

public class AlertUtils {

    private static void mostrar(
            Alert.AlertType type,
            String titulo,
            String msg,
            String claseCss
    ) {
        Alert alert = new Alert(type);
        alert.setTitle(titulo);
        alert.setHeaderText(titulo);
        alert.setContentText(msg);

        aplicarEstilo(alert, claseCss);

        alert.showAndWait();
    }

    private static void aplicarEstilo(Alert alert, String clase) {
        DialogPane dp = alert.getDialogPane();

        URL css = AlertUtils.class.getResource("/vista/admin-alert.css");
        if (css != null && !dp.getStylesheets().contains(css.toExternalForm())) {
            dp.getStylesheets().add(css.toExternalForm());
        }

        dp.getStyleClass().add(clase);
    }



    public static void info(String titulo, String msg) {
        mostrar(Alert.AlertType.INFORMATION, titulo, msg, "alert-info");
    }

    public static void warning(String titulo, String msg) {
        mostrar(Alert.AlertType.WARNING, titulo, msg, "alert-warning");
    }

    public static void error(String titulo, String msg) {
        mostrar(Alert.AlertType.ERROR, titulo, msg, "alert-error");
    }

    public static void success(String titulo, String msg) {
        mostrar(Alert.AlertType.INFORMATION, titulo, msg, "alert-success");
    }
}
