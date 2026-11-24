package br.com.vetcare.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
@Entity @Table(name="profissionais")
public class Professional {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @NotBlank private String nome; private String crmv; private String funcao; private String telefone;
 public Long getId(){return id;} public void setId(Long id){this.id=id;}
 public String getNome(){return nome;} public void setNome(String nome){this.nome=nome;}
 public String getCrmv(){return crmv;} public void setCrmv(String crmv){this.crmv=crmv;}
 public String getFuncao(){return funcao;} public void setFuncao(String funcao){this.funcao=funcao;}
 public String getTelefone(){return telefone;} public void setTelefone(String telefone){this.telefone=telefone;}
}
