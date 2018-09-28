package v3.estruturart.com.br.estruturaart.utility;

import java.util.List;

public class JsonModel implements IJsonModel
{
    private boolean status = true;
    private String message;
    private List<?> list;
    private Object object;
    private Integer id;

    public boolean isStatus()
    {
        return status;
    }

    public void setStatus(boolean status)
    {
        this.status = status;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public List<?> getList()
    {
        return list;
    }

    public void setList(List<?> list)
    {
        this.list = list;
    }

    public Object getObject()
    {
        return object;
    }

    public void setObject(Object object)
    {
        this.object = object;
    }

    public Object getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }
}
