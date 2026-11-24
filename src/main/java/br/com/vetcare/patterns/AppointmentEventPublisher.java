package br.com.vetcare.patterns;
import java.util.*; import org.springframework.stereotype.Component;
@Component
public class AppointmentEventPublisher {
 private final List<AppointmentObserver> observers;
 public AppointmentEventPublisher(List<AppointmentObserver> observers){this.observers=new ArrayList<>(observers);}
 public void publish(AppointmentEvent event){observers.forEach(o->o.onEvent(event));}
}
