package v3.estruturart.com.br.estruturaart.utility;

public interface AsyncResponse {
    String onExecTask(String result, int id);
    String onPreTask(String result, int id);
    String onPosTask(String result, int id);
}
