package v3.estruturart.com.br.estruturaart.model;

import java.util.Date;
import v3.estruturart.com.br.estruturaart.utility.StringUtilsPad;
// import org.apache.commons.fileupload.FileItem;

public class TbPedidoItemFoto extends AbstractModel
{
    private Integer id = 0;
    private String idStringItem = "";
    private String caminhoArquivo;
    private String caminhoArquivoCompleto;
    //private FileItem fileFoto;
    private Date dataInclusao;
    private String observacao;
    private String observacaoLimitado;
    private Integer pedidoItensId = 0;
    private String base64Imagem = "";

    public Integer getId()
    {
        return this.id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getCaminhoArquivo()
    {
        return this.caminhoArquivo;
    }

    public void setCaminhoArquivo(String caminhoArquivo)
    {
        this.caminhoArquivo = caminhoArquivo;
        this.caminhoArquivoCompleto = String.format(
            "files/item/%d/%s",
            getPedidoItensId(),
            caminhoArquivo
        );
    }

    public Date getDataInclusao()
    {
        return this.dataInclusao;
    }

    public void setDataInclusao(Date dataInclusao)
    {
        this.dataInclusao = dataInclusao;
    }

    public String getObservacao()
    {
        return this.observacao;
    }

    public void setObservacao(String observacao)
    {
        this.observacao = observacao;
        if (observacao.length() > 13) {
            this.observacaoLimitado = observacao.substring(0, 10) + "...";
        } else {
            this.observacaoLimitado = observacao;
        }
    }

    public Integer getPedidoItensId()
    {
        return this.pedidoItensId;
    }

    public String getPedidoItensIdString()
    {
        return StringUtilsPad.padLeft(String.valueOf(pedidoItensId), 5, "0");
    }

    public void setPedidoItensId(Integer pedidoItensId)
    {
        this.pedidoItensId = pedidoItensId;
        this.idStringItem = StringUtilsPad.padLeft(String.valueOf(pedidoItensId), 5, "0");
    }

    public String getCaminhoArquivoCompleto()
    {
        return this.caminhoArquivoCompleto;
    }

    public void setCaminhoArquivoCompleto(String caminhoArquivoCompleto)
    {
        this.caminhoArquivoCompleto = caminhoArquivoCompleto;
    }

    public String getBase64Imagem() {
        return base64Imagem;
    }

    public void setBase64Imagem(String base64Image) {
        this.base64Imagem = base64Image;
    }

    public boolean isValid()
    {
        boolean isValid = true;
        return isValid;
    }
}