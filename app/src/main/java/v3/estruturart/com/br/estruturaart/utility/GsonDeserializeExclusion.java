package v3.estruturart.com.br.estruturaart.utility;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public class GsonDeserializeExclusion implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getDeclaredClass() == NumberFormat.class;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }

}
