package v3.estruturart.com.br.estruturaart.model;

import java.sql.Date;
import java.sql.SQLException;

import v3.estruturart.com.br.estruturaart.utility.Util;

public class TbFornecedor extends AbstractModel
{
    private Integer id = 0;
    private String nome = "";
    private Date dataInclusao;
    private int status;
    private String telefone = "";

    /**
     * @return the id
     */
    public Integer getId()
    {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Integer id)
    {
        this.id = id;
    }

    /**
     * @return the nome
     */
    public String getNome()
    {
        return nome;
    }

    /**
     * @param nome
     *            the nome to set
     */
    public void setNome(String nome)
    {
        this.nome = nome;
    }

    /**
     * @return the dataInclusao
     */
    public Date getDataInclusao()
    {
        return dataInclusao;
    }

    /**
     * @param dataInclusao
     *            the dataInclusao to set
     */
    public void setDataInclusao(Date dataInclusao)
    {
        this.dataInclusao = dataInclusao;
    }

    /**
     * @return the status
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getStatusNome()
    {
        return this.status == 1 ? "Ativo" : "Inativo";
    }

    public String getDateFormat(String format)
    {
        return this.getSimpleDateFormat(format).format(this.getDataInclusao());
    }

    public String getTelefone()
    {
        return this.telefone;
    }

    public String getTelefoneString()
    {
        if (this.telefone.equals("")) {
            return "-";
        }

        return this.telefone;
    }

    public void setTelefone(String telefone)
    {
        this.telefone = telefone;
    }

    @Override
    public boolean isValid() throws SQLException
    {
        return true;
    }
}
