import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class aaa {
    @Test
    public void bbb() {
        Map<String, Object> model = new HashMap<>();
        model.put("id", 1);
        model.put("name", "John");
//        StringBuilder sb = new StringBuilder();
//        if (model == null) {
//            System.out.println("");
//        }
//        boolean hasModel = false;
//        sb.append("?");
//        for (Map.Entry<String, Object> entry : model.entrySet()) {
//            if (hasModel) {
//                sb.append("&");
//            } else {
//                hasModel = true;
//            }
//            sb.append(entry.getKey() + "=");
//            sb.append(entry.getValue());
//        }
        if (model == null) System.out.println("");
        StringBuilder sb = new StringBuilder("?");
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            sb.append(entry.getKey() + "=");
            sb.append(entry.getValue());
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }

        System.out.println(sb.toString());
    }

    public void m1(int[] id) {

    }
}
