import java.util.ResourceBundle;

public class StringBundle {

    private static ResourceBundle resourceBundle = null;
    private StringBundle(){}

    public static synchronized ResourceBundle getInstance() {
        if(resourceBundle == null) resourceBundle = ResourceBundle.getBundle("Strings");

        return resourceBundle;
    }

}
