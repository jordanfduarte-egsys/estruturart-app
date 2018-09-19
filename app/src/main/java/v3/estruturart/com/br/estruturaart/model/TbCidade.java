package v3.estruturart.com.br.estruturaart.model;

public class TbCidade extends AbstractModel
{
    private Integer id = 0;
    private int estadoId;
    private String uf;
    private String nome;
    private TbEstado estado = new TbEstado();

    public int getId()
    {
        return this.id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public int getEstadoid()
    {
        return this.estadoId;
    }

    public void setEstadoid(int estadoId)
    {
        this.estadoId = estadoId;
    }

    public String getUf()
    {
        return this.uf;
    }

    public void setUf(String uf)
    {
        this.uf = uf;
    }

    public String getNome()
    {
        return this.nome;
    }

    public void setNome(String nome)
    {
        this.nome = nome;
    }

    public TbEstado getEstado()
    {
        return this.estado;
    }

    public void setEstado(TbEstado estado)
    {
        this.estado = estado;
    }

    public String toString() {
        return this.nome;
    }

    public boolean isValid() {
        return true;
    }
}