package com.rabobank.nl.statementprocessor.api.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * POJO for capturing the result of validation
 */
@Builder
@Data
public class StatementResult {

  private String result;
  private List<ErrorRecord> errorRecords;
}
