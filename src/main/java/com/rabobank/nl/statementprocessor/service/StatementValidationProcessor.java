package com.rabobank.nl.statementprocessor.service;

import com.rabobank.nl.statementprocessor.api.model.ErrorRecord;
import com.rabobank.nl.statementprocessor.api.model.StatementRecord;
import com.rabobank.nl.statementprocessor.api.model.StatementResult;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Business logic to process the statements and validates against different rules
 * duplicate reference or incorrect end balance
 */
@Service
@Slf4j
public class StatementValidationProcessor {


  public static final String SUCCESS = "SUCCESS";
  public static final String DUPLICATE_REFERENCE = "DUPLICATE_REFERENCE";
  public static final String INCORRECT_END_BALANCE = "INCORRECT_END_BALANCE";
  public static final String DUPLICATE_REFERENCE_INCORRECT_END_BALANCE = "DUPLICATE_REFERENCE_INCORRECT_END_BALANCE";

  /**
   * Perform the Reference not unique and incorrect balance validation on the provided statement records
   *
   * @param statementRecords - records of Statement
   * @return statementResult - Response with Result success or error
   */
  public StatementResult validateStatementRecords(List<StatementRecord> statementRecords) {

    List<ErrorRecord> errorRecordsWithReferenceNotUnique = validateReferenceNotUnique(
        statementRecords);

    List<ErrorRecord> errorRecordsWithEndBalance = validateEndBalance(statementRecords);

    String result = createResult(errorRecordsWithReferenceNotUnique.isEmpty(),
        errorRecordsWithEndBalance.isEmpty());
    return StatementResult.builder().errorRecords(Stream
        .concat(errorRecordsWithReferenceNotUnique.stream(), errorRecordsWithEndBalance.stream())
        .distinct().collect(
            Collectors.toList())).result(result).build();
  }


  private List<ErrorRecord> validateEndBalance(List<StatementRecord> statementRecords) {
    return statementRecords.stream().
        filter(statementRecord -> !statementRecord.getStartBalance().add(statementRecord.getMutation())
            .equals(statementRecord.getEndBalance()))
        .map(this::createErrorRecord).distinct()
        .collect(Collectors.toList());
  }

  private List<ErrorRecord> validateReferenceNotUnique(List<StatementRecord> statementRecords) {
    return statementRecords
        .stream().filter(statementRecord -> Collections.frequency(statementRecords, statementRecord) > 1)
        .map(this::createErrorRecord).distinct()
        .collect(Collectors.toList());
  }

  private ErrorRecord createErrorRecord(StatementRecord statementRecord) {
    final String logMethod = "createErrorRecord(StatementRecord):ErrorRecord Error : %s";
    ErrorRecord errorRecord = ErrorRecord.builder().reference(statementRecord.getReference())
        .accountNumber(statementRecord.getAccountNumber()).build();
    log.error(String.format(logMethod, errorRecord));
    return errorRecord;
  }

  private String createResult(boolean referenceNotUnique, boolean incorrectEndBalance) {
    if (!referenceNotUnique && !incorrectEndBalance) {
      return DUPLICATE_REFERENCE_INCORRECT_END_BALANCE;
    } else if (!referenceNotUnique) {
      return DUPLICATE_REFERENCE;
    } else if (!incorrectEndBalance) {
      return INCORRECT_END_BALANCE;
    } else {
      return SUCCESS;
    }
  }
}
