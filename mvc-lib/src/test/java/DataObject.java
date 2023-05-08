import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class DataObject {
    private final String data;

    private static int objectCounter = 0;

    public DataObject(String data) {
        this.data = data;
    }
    // standard constructors/getters


    public static DataObject get(String data) {
        objectCounter++;
        return new DataObject(data);
    }
}
