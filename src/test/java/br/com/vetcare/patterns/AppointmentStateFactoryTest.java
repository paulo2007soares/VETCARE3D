package br.com.vetcare.patterns;
import br.com.vetcare.model.AppointmentStatus; import org.junit.jupiter.api.Test; import static org.junit.jupiter.api.Assertions.*;
class AppointmentStateFactoryTest {
 @Test void scheduledCanBeConfirmed(){assertTrue(new AppointmentStateFactory().from(AppointmentStatus.AGENDADA).canMoveTo(AppointmentStatus.CONFIRMADA));}
 @Test void completedCannotBeReopened(){assertFalse(new AppointmentStateFactory().from(AppointmentStatus.CONCLUIDA).canMoveTo(AppointmentStatus.EM_ATENDIMENTO));}
}
