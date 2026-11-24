package br.com.vetcare.patterns;
import br.com.vetcare.model.AppointmentStatus;
public class AppointmentStateFactory {
 public AppointmentState from(AppointmentStatus current){
  return new AppointmentState(){
   public AppointmentStatus status(){return current;}
   public boolean canMoveTo(AppointmentStatus next){
    return switch(current){
     case AGENDADA -> next==AppointmentStatus.CONFIRMADA||next==AppointmentStatus.CANCELADA||next==AppointmentStatus.FALTOU;
     case CONFIRMADA -> next==AppointmentStatus.EM_ATENDIMENTO||next==AppointmentStatus.CANCELADA||next==AppointmentStatus.FALTOU;
     case EM_ATENDIMENTO -> next==AppointmentStatus.CONCLUIDA;
     case CONCLUIDA, CANCELADA, FALTOU -> false;
    };
   }
  };
 }
}
