package v3.estruturart.com.br.estruturaart.service;

import android.content.Context;
import android.os.StrictMode;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import v3.estruturart.com.br.estruturaart.R;
import v3.estruturart.com.br.estruturaart.model.Orcamento;

public class Client
{
    private String url;
    private boolean hasError = false;
    private String message = "";
    private String json;
    private Context ctx;
    private OkHttpClient client;
    private Map<String, String> parameter = new HashMap<>();
    public static final MediaType FORMURLENCODED = MediaType.parse(
            "application/x-www-form-urlencoded; charset=utf-8"
    );

    public Client(Context ctx)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.client = new OkHttpClient();
        this.ctx = ctx;
        this.url = ctx.getString(R.string.ws_host);
    }

    public Object fromGet(String action, Type type) {
        try {
            OkHttpClient client2 = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(this.url + action)
                    .build();

            Response responseO = client2.newCall(request).execute();
            this.json = responseO.body().string();

            System.out.println("RESPONSTA WS: " + this.json);

            switch (responseO.code()) {
                case 200:
                    break;
                default:
                    throw new IOException(
                            "Erro ao chamar o web service. Codigo: "
                                    + responseO.code()
                                    + responseO.message()
                    );
            }
        } catch (MalformedURLException ex) {
            System.out.println("EXCEPTION1 => " + ex.getMessage());
            this.hasError = true;
            this.message = ex.getMessage();
        } catch (ProtocolException ex) {
            System.out.println("EXCEPTION2 => " + ex.getMessage());
            this.hasError = true;
            this.message = ex.getMessage();
        } catch (ConnectException ex) {
            System.out.println("EXCEPTION3 => " + ex.getMessage());
            this.hasError = true;
            this.message = ex.getMessage();
        } catch (Exception ex) {
            System.out.println("EXCEPTION4 => " + ex.getMessage());
            this.hasError = true;
            this.message = ex.getMessage();
        }

        Gson gson = new Gson();
        try {
            System.out.println("WS JSON: " + this.json);
            if (!this.json.equals("")) {
                return gson.fromJson(this.json, type);
            }
        } catch (NullPointerException ex) {
            this.hasError = true;
            this.message = ex.getMessage();

        } catch (Exception ex) {
            this.hasError = true;
            this.message = ex.getMessage();
        }
        return gson.fromJson("{}", type);
//        Class<?> theClass = Class.forName(type.toString());
//        return theClass.cast(new Object());
    }

    public Object fromPost(String action, Type type) {
        Gson gson = new Gson();
        try {
            OkHttpClient client2 = new OkHttpClient();
            RequestBody body = RequestBody.create(FORMURLENCODED, getParametersToString());
            Request request = new Request.Builder()
                    .url(this.url + action)
                    .post(body)
                    .build();

            Response responseO = client2.newCall(request).execute();
            this.json = responseO.body().string();

            System.out.println("RESPONSTA WS: " + this.json);

            switch (responseO.code()) {
                case 200:
                    break;
                default:
                    throw new IOException(
                            "Erro ao chamar o web service. Codigo: "
                                    + responseO.code()
                                    + responseO.message()
                    );
            }
        } catch(MalformedURLException ex){
            System.out.println("EXCEPTION1 => " + ex.getMessage());
            this.hasError = true;
            this.message = ex.getMessage();
        } catch(ProtocolException ex){
            System.out.println("EXCEPTION2 => " + ex.getMessage());
            this.hasError = true;
            this.message = ex.getMessage();
        } catch(ConnectException ex){
            System.out.println("EXCEPTION3 => " + ex.getMessage());
            this.hasError = true;
            this.message = ex.getMessage();
        } catch(Exception ex){
            System.out.println("EXCEPTION4 => " + ex.getMessage());
            this.hasError = true;
            this.message = ex.getMessage();
        }
        try {
            if (!this.json.equals("")) {
                return gson.fromJson(this.json, type);
            }
        } catch (NullPointerException ex) {
            this.hasError = true;
            this.message = ex.getMessage();

        } catch (Exception ex) {
            this.hasError = true;
            this.message = ex.getMessage();
        }


        return gson.fromJson("{}", type);
//        Class<?> theClass = Class.forName(type.toString());
//        return theClass.cast(new Object());
    }

    private String toString(HttpURLConnection client) throws IOException
    {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        String response = null;

        for (String line; (line = br.readLine()) != null; ) {
            sb.append(line + "\n");
        }

        try {
            response = sb.toString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return response;
    }

    public String getJson()
    {
        return this.json;
    }

    public String getParametersToString()
    {
        String paramString = "";
        for (Map.Entry<String, String> param : getParameter().entrySet()) {
            paramString += param.getKey() + "=" + param.getValue() + "&";
        }

        if (paramString.length() > 0) {
            return paramString.substring(0, paramString.length() - 1);
        }

        return paramString;
    }

    public Map<String, String> getParameter() {
        return parameter;
    }

    public void setParameter(Map<String, String> parameter) {
        this.parameter = parameter;
    }

    public boolean hasError() {
        return this.hasError;
    }

    public String getMessage() {
        return this.message;
    }
}
