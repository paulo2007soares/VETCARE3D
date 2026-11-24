package br.com.vetcare.patterns;
import java.math.BigDecimal;
public class EmergencyPricing implements PricingStrategy { public BigDecimal calculate(BigDecimal base){return base.multiply(new BigDecimal("1.50"));} }
