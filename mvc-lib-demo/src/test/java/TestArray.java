import com.nf.mvc.util.ReflectionUtils;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestArray {
    @Test
    public void testArray() throws NoSuchMethodException {
        Map<String, String[]> map = new HashMap<>();
        map.put("id", new String[]{"1111", "22222"});
        Class<aaa> clz = aaa.class;
        Method m1 = clz.getDeclaredMethod("m1", int[].class);
        List<String> paramNames = ReflectionUtils.getParamNames(clz, m1.getName());
        String[] strings = map.get(paramNames.get(0));
        int[] values = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            values[i] = Integer.parseInt(strings[i]);
        }

        System.out.println(Arrays.toString(values));
    }
}
