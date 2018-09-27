package v3.estruturart.com.br.estruturaart.model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import v3.estruturart.com.br.estruturaart.R;
import v3.estruturart.com.br.estruturaart.utility.Util;
import v3.estruturart.com.br.estruturaart.utility.Param;

public class TbUsuario extends AbstractModel {
    private Integer id = 0;
    private String tipoPessoa = "1";
    private String nome;
    private String cpfCnpj;
    private String rgIncricaoEstadual;
    private String email;
    private String telefone;
    private String codigo;
    private String senha;
    private Integer perfilId;
    private Integer statusUsuarioId;
    private TbPerfil perfil;
    private TbStatusUsuario statusUsuario;
    private List<TbTipoPessoa> tiposPessoa;
    private List<TbPerfil> perfis;
    private String tipoPessoaNome;

    public TbUsuario()
    {
        tiposPessoa = new ArrayList<TbTipoPessoa>();
        perfil = new TbPerfil();
        perfis = new ArrayList<TbPerfil>();

        TbTipoPessoa fisica = new TbTipoPessoa();
        TbTipoPessoa juridica = new TbTipoPessoa();

        fisica.setId(1);
        fisica.setNome("Pessoa física");

        juridica.setId(2);
        juridica.setNome("Pessoa jurídica");

        tiposPessoa.add(fisica);
        tiposPessoa.add(juridica);
    }

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
     * @return the tipoPessoa
     */
    public String getTipoPessoa()
    {
        return tipoPessoa;
    }

    /**
     * @param tipoPessoa
     *            the tipoPessoa to set
     */
    public void setTipoPessoa(String tipoPessoa)
    {
        this.tipoPessoaNome = tipoPessoa;
        if (tipoPessoa.length() > 1) {
            if (tipoPessoa.matches("(.*)física")) {
                this.tipoPessoa = "1";
            } else {
                this.tipoPessoa = "2";
            }
        } else {
            this.tipoPessoa = tipoPessoa;
        }
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
     * @return the cpfCnpj
     */
    public String getCpfCnpj()
    {
        return cpfCnpj;
    }

    /**
     * @return the cpfCnpj
     */
    public String getCpfCnpjString()
    {
        if (getTipoPessoa().equals("1")) {
            return Util.mask("###.###.###-##", cpfCnpj);
        }

        return Util.mask("##.###.###/####-##", cpfCnpj);
    }

    /**
     * @param cpfCnpj
     *            the cpfCnpj to set
     */
    public void setCpfCnpj(String cpfCnpj)
    {
        this.cpfCnpj = cpfCnpj;
    }

    /**
     * @return the rgIncricaoEstadual
     */
    public String getRgIncricaoEstadual()
    {
        return rgIncricaoEstadual;
    }

    /**
     * @param rgIncricaoEstadual
     *            the rgIncricaoEstadual to set
     */
    public void setRgIncricaoEstadual(String rgIncricaoEstadual)
    {
        this.rgIncricaoEstadual = rgIncricaoEstadual;
    }

    /**
     * @return the email
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email)
    {
        this.email = email;
    }

    /**
     * @return the telefone
     */
    public String getTelefone()
    {
        return telefone;
    }

    /**
     * @param telefone
     *            the telefone to set
     */
    public void setTelefone(String telefone)
    {
        this.telefone = telefone;
    }

    /**
     * @return the codigo
     */
    public String getCodigo()
    {
        return codigo;
    }

    /**
     * @param codigo
     *            the codigo to set
     */
    public void setCodigo(String codigo)
    {
        this.codigo = codigo;
    }

    /**
     * @return the senha
     */
    public String getSenha()
    {
        return senha;
    }

    /**
     * @param senha
     *            the senha to set
     */
    public void setSenha(String senha)
    {
        this.senha = senha;
    }

    /**
     * @return the perfilId
     */
    public Integer getPerfilId()
    {
        return perfilId;
    }

    /**
     * @param perfilId
     *            the perfilId to set
     */
    public void setPerfilId(Integer perfilId)
    {
        this.perfilId = perfilId;
    }

    /**
     * @return the statusUsuarioId
     */
    public Integer getStatusUsuarioId()
    {
        return statusUsuarioId;
    }

    /**
     * @param statusUsuarioId
     *            the statusUsuarioId to set
     */
    public void setStatusUsuarioId(Integer statusUsuarioId)
    {
        this.statusUsuarioId = statusUsuarioId;
    }

    /**
     * @return the perfil
     */
    public TbPerfil getPerfil()
    {
        return perfil;
    }

    /**
     * @param perfil
     *            the perfil to set
     */
    public void setPerfil(TbPerfil perfil)
    {
        this.perfil = perfil;
    }

    /**
     * @return the statusUsuario
     */
    public TbStatusUsuario getStatusUsuario()
    {
        return statusUsuario;
    }

    /**
     * @param statusUsuario
     *            the statusUsuario to set
     */
    public void setStatusUsuario(TbStatusUsuario statusUsuario)
    {
        this.statusUsuario = statusUsuario;
    }

    public List<TbTipoPessoa> getTiposPessoa()
    {
        return this.tiposPessoa;
    }

    public List<TbPerfil> getPerfis()
    {
        return this.perfis;
    }

    public void addPerfil(TbPerfil perfil)
    {
        this.perfis.add(perfil);
    }

    public void setPerfilAll(List<TbPerfil> perfis)
    {
        this.perfis = perfis;
    }

    public String generateCode()
    {
        return String.format("US%06d%s", this.getId(), String.valueOf(this.getNome().toUpperCase().charAt(0)));
    }

    @Override
    public boolean isValid()
    {
        boolean isValid = true;

        if (!Util.isNomeCompletoValid(this.getNome())) {
            this.getValidation().add(new Param(R.id.edNomeCompleto, "Informe o nome completo!"));
            isValid = false;
        }

        if (this.getTipoPessoa().equals("1")) {
            if (!Util.isCPFValid(this.getCpfCnpj())) {
                this.getValidation().add(new Param(R.id.etCpfCnpj, "Informe um CPF válido!"));
                isValid = false;
            }
        } else {
            if (!Util.isCNPJValid(this.getCpfCnpj())) {
                this.getValidation().add(new Param(R.id.etCpfCnpj, "Informe um CNPJ válido!"));
                isValid = false;
            }
        }

        if (!Util.isEmailValid(this.getEmail())) {
            this.getValidation().add(new Param(R.id.edEmail, "Informe um e-mail válido!"));
            isValid = false;
        }

        if (getRgIncricaoEstadual().equals("")) {
            this.getValidation().add(new Param(R.id.edRgInscricaoEstadual, "Informe o RG ou inscriçao estadual válida!"));
            isValid = false;
        }


        this.setSenha("");

        if (this.getTelefone().length() <= 8) {
            this.getValidation().add(new Param(R.id.edCelular, "Informe um telefone válida!"));
            isValid = false;
        }

        return isValid;
    }



    /**
     * @return the tipoPessoaNome
     */
    public String getTipoPessoaNome()
    {
        return tipoPessoaNome;
    }

    /**
     * @param tipoPessoaNome
     *            the tipoPessoaNome to set
     */
    public void setTipoPessoaNome(String tipoPessoaNome)
    {
        this.tipoPessoaNome = tipoPessoaNome;
    }

    public String toJson() {
        return (new Gson()).toJson(this);
    }
}
