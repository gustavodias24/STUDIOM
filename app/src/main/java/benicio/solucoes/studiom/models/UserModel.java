package benicio.solucoes.studiom.models;

public class UserModel {
    String nome,id,email,senha;
    int comissao;

    public UserModel() {
    }

    @Override
    public String toString() {
        return  "Nome: " + nome + '\n' +
                "E-mail: " + email + '\n' +
                "Senha: " + senha + '\n' +
                "Comissao: " + comissao + "%";
    }

    public UserModel(String nome, String id, String email, String senha, int comissao) {
        this.nome = nome;
        this.id = id;
        this.email = email;
        this.senha = senha;
        this.comissao = comissao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public int getComissao() {
        return comissao;
    }

    public void setComissao(int comissao) {
        this.comissao = comissao;
    }
}
