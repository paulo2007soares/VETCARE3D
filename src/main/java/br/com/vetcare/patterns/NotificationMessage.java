package br.com.vetcare.patterns;
public record NotificationMessage(String channel,String message) {
 public static Builder builder(){return new Builder();}
 public static class Builder {
  private String channel="INTERNA"; private String message;
  public Builder channel(String v){channel=v;return this;}
  public Builder message(String v){message=v;return this;}
  public NotificationMessage build(){if(message==null||message.isBlank())throw new IllegalArgumentException("Mensagem obrigatória.");return new NotificationMessage(channel,message);}
 }
}
