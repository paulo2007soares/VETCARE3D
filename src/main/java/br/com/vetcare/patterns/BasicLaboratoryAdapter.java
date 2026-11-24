package br.com.vetcare.patterns;
public class BasicLaboratoryAdapter implements LaboratoryAdapter {
 public String normalizeResult(String externalPayload){return externalPayload==null?"":externalPayload.trim();}
}
