
package br.com.vetcare.service;

import br.com.vetcare.model.Appointment;
import br.com.vetcare.model.Professional;
import br.com.vetcare.patterns.AppointmentEvent;
import br.com.vetcare.patterns.AppointmentEventPublisher;
import br.com.vetcare.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository repository;

    @Mock
    private AppointmentEventPublisher publisher;

    @InjectMocks
    private AppointmentService service;

    @Test
    void deveImpedirConsultaSemProfissional() {
        Appointment consulta = new Appointment();
        consulta.setDataHora(LocalDateTime.now());

        assertThrows(
            IllegalArgumentException.class,
            () -> service.save(consulta)
        );

        verifyNoInteractions(repository);
    }

    @Test
    void deveImpedirConsultaSemData() {
        Appointment consulta = new Appointment();
        Professional profissional = new Professional();
        profissional.setId(1L);
        consulta.setProfissional(profissional);

        assertThrows(
            IllegalArgumentException.class,
            () -> service.save(consulta)
        );

        verifyNoInteractions(repository);
    }

    @Test
    void deveImpedirDoisAgendamentosNoMesmoHorario() {
        Professional profissional = new Professional();
        profissional.setId(1L);

        Appointment consulta = new Appointment();
        consulta.setProfissional(profissional);
        consulta.setDataHora(LocalDateTime.of(2026, 12, 10, 14, 0));

        when(repository.existsByProfissionalIdAndDataHoraAndStatusNotAndIdNot(
            eq(1L), any(), any(), anyLong()
        )).thenReturn(true);

        assertThrows(
            IllegalArgumentException.class,
            () -> service.save(consulta)
        );

        verify(repository, never()).save(any());
    }

    @Test
    void deveSalvarConsultaQuandoHorarioEstiverLivre() {
        Professional profissional = new Professional();
        profissional.setId(1L);

        Appointment consulta = new Appointment();
        consulta.setProfissional(profissional);
        consulta.setDataHora(LocalDateTime.of(2026, 12, 10, 15, 0));

        when(repository.existsByProfissionalIdAndDataHoraAndStatusNotAndIdNot(
            eq(1L), any(), any(), anyLong()
        )).thenReturn(false);

        when(repository.save(consulta)).thenReturn(consulta);

        Appointment resultado = service.save(consulta);

        assertNotNull(resultado);
        verify(repository).save(consulta);
        verify(publisher).publish(any(AppointmentEvent.class));
    }
}
