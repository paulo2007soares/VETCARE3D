package br.com.vetcare.patterns;
public class NotificationFactory {
 public NotificationChannel create(String channel){return (destination,message)->"SIMULADO: "+channel+" para "+destination+" — "+message;}
}
