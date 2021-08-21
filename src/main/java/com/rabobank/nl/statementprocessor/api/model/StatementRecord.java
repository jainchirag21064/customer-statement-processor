package com.rabobank.nl.statementprocessor.api.model;

import java.math.BigDecimal;
import java.util.Objects;
import lombok.Builder;
import lombok.Data;

/**
 * Pojo for capturing the request object
 */
@Builder
@Data
public class StatementRecord {

  private Long reference;
  private String accountNumber;
  private BigDecimal startBalance;
  private BigDecimal mutation;
  private String description;
  private BigDecimal endBalance;

  @Override
  public boolean equals(final Object obj) {
    //when same object instance is sent
    if (this == obj) {
      return true;
    }
    //Check if the passed object is not null and of different class
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    //Final check of objects based on reference number
    StatementRecord that = (StatementRecord) obj;
    return Objects.equals(reference, that.reference);
  }

  @Override
  public int hashCode() {
    return reference != null ? reference.hashCode() : 0;
  }
}
