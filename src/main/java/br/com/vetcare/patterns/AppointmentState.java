package br.com.vetcare.patterns;
import br.com.vetcare.model.AppointmentStatus;
public interface AppointmentState { AppointmentStatus status(); boolean canMoveTo(AppointmentStatus next); }
