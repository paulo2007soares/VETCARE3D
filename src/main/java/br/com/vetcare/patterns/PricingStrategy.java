package br.com.vetcare.patterns;
import java.math.BigDecimal;
public interface PricingStrategy { BigDecimal calculate(BigDecimal base); }
