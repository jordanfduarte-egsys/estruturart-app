package v3.estruturart.com.br.estruturaart.utility;

public class Param {
    private Integer index;
    private Object value;

    public Param(Integer index, Object value) {
        this.index = index;
        this.value = value;
    }

    public Integer getIndex() {
        return this.index;
    }

    public Object getValue() {
        return this.value;
    }
}