package benicio.solucoes.studiom.models;

public class ClienteModel {
    String id, nome, numero, linkDoZap;

    public ClienteModel(String id, String nome, String numero, String linkDoZap) {
        this.id = id;
        this.nome = nome;
        this.numero = numero;
        this.linkDoZap = linkDoZap;
    }

    public ClienteModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getLinkDoZap() {
        return linkDoZap;
    }

    public void setLinkDoZap(String linkDoZap) {
        this.linkDoZap = linkDoZap;
    }
}
