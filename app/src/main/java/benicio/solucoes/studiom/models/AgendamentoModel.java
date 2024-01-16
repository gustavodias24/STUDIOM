package benicio.solucoes.studiom.models;

public class AgendamentoModel {
    String idCliente, nomeCliente, data, hora, pacote, id;
    int status, aula;

    public AgendamentoModel(String idCliente, String nomeCliente, String data, String hora, String pacote, String id, int status, int aula) {
        this.idCliente = idCliente;
        this.nomeCliente = nomeCliente;
        this.data = data;
        this.hora = hora;
        this.pacote = pacote;
        this.id = id;
        this.status = status;
        this.aula = aula;
    }

    public AgendamentoModel() {
    }

    public int getAula() {
        return aula;
    }

    public void setAula(int aula) {
        this.aula = aula;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getPacote() {
        return pacote;
    }

    public void setPacote(String pacote) {
        this.pacote = pacote;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
