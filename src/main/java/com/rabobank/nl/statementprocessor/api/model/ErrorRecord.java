package com.rabobank.nl.statementprocessor.api.model;

import java.util.Objects;
import lombok.Builder;
import lombok.Data;

/**
 * Pojo for capturing the error response
 */
@Builder
@Data
public class ErrorRecord {

  private Long reference;
  private String accountNumber;

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
    ErrorRecord that = (ErrorRecord) obj;
    return Objects.equals(reference, that.reference);
  }

  @Override
  public int hashCode() {
    return reference != null ? reference.hashCode() : 0;
  }

}
