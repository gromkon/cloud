import java.io.Serializable;

public class CloudPackage implements Serializable {

    private String text;

    public CloudPackage(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
