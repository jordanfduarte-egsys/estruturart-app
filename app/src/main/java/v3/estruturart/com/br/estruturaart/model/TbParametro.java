package v3.estruturart.com.br.estruturaart.model;

import v3.estruturart.com.br.estruturaart.model.TbEstado;
import v3.estruturart.com.br.estruturaart.model.TbUsuario;
import v3.estruturart.com.br.estruturaart.model.TbPedido;
import v3.estruturart.com.br.estruturaart.utility.Util;

public class TbParametro extends AbstractModel
{
    private Integer id = 0;
    private String cep = "";
    private String logradouro = "";
    private String bairro = "";
    private String numero = "";
    private String complemento = "";
    private String uf = "";
    private String cidade = "";
    private String subject = "";
    private String from = "";
    private String hostMail = "";
    private String host = "";
    private String usuario = "";
    private String senha = "";

    public Integer getId()
    {
        return this.id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getCep()
    {
        return this.cep;
    }

    public void setCep(String cep)
    {
        this.cep = cep;
    }

    public String getLogradouro()
    {
        return this.logradouro;
    }

    public void setLogradouro(String logradouro)
    {
        this.logradouro = logradouro;
    }

    public String getBairro()
    {
        return this.bairro;
    }

    public void setBairro(String bairro)
    {
        this.bairro = bairro;
    }

    public String getNumero()
    {
        return this.numero;
    }

    public void setNumero(String numero)
    {
        this.numero = numero;
    }

    public String getComplemento()
    {
        return this.complemento;
    }

    public void setComplemento(String complemento)
    {
        this.complemento = complemento;
    }

    public String getUf()
    {
        return this.uf;
    }

    public void setUf(String uf)
    {
        this.uf = uf;
    }

    public String getCidade()
    {
        return this.cidade;
    }

    public void setCidade(String cidade)
    {
        this.cidade = cidade;
    }

    public String getSubject()
    {
        return this.subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getFrom()
    {
        return this.from;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public String getHostMail()
    {
        return this.hostMail;
    }

    public void setHostMail(String hostMail)
    {
        this.hostMail = hostMail;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public String getHost()
    {
        return this.host;
    }

    public String getUsuario()
    {
        return this.usuario;
    }

    public void setUsuario(String usuario)
    {
        this.usuario = usuario;
    }

    public String getSenha()
    {
        return this.senha;
    }

    public void setSenha(String senha)
    {
        this.senha = senha;
    }

    public boolean isValid() {return true; }
}
