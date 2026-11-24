package br.com.vetcare.patterns;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
class PricingStrategyTest {
 @Test void regularPriceKeepsBase(){assertEquals(new BigDecimal("100.00"),new RegularPricing().calculate(new BigDecimal("100.00")));}
 @Test void emergencyAddsFiftyPercent(){assertEquals(new BigDecimal("150.0000"),new EmergencyPricing().calculate(new BigDecimal("100.00")));}
}
