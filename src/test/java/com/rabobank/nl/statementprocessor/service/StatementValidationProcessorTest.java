package com.rabobank.nl.statementprocessor.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.rabobank.nl.statementprocessor.api.model.ErrorRecord;
import com.rabobank.nl.statementprocessor.api.model.StatementRecord;
import com.rabobank.nl.statementprocessor.api.model.StatementResult;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StatementValidationProcessorTest {

  @InjectMocks
  StatementValidationProcessor underTest;

  /**
   * Test validation of multiple statement records with unique reference and correct end balance
   */
  @Test
  void validateStatementRecordsHappyScenario() {
    //given
    List<StatementRecord> statementRecords = new ArrayList<>();
    StatementRecord statementRecord = StatementRecord.builder().reference(1l).accountNumber("123")
        .description("Some Description").startBalance(new BigDecimal(100))
        .mutation(new BigDecimal(10)).endBalance(new BigDecimal(110)).build();
    StatementRecord statementRecord1 = StatementRecord.builder().reference(2l).accountNumber("123")
        .description("Some Description").startBalance(new BigDecimal(100))
        .mutation(new BigDecimal(10)).endBalance(new BigDecimal(110)).build();
    statementRecords.add(statementRecord);
    statementRecords.add(statementRecord1);

    //when
    StatementResult actualStatementResult = underTest.validateStatementRecords(statementRecords);

    StatementResult expectedStatementResult = StatementResult.builder()
        .result(StatementValidationProcessor.SUCCESS).errorRecords(
            Collections.emptyList()).build();

    //then
    assertThat(actualStatementResult).isNotNull();
    assertThat(actualStatementResult).isEqualTo(expectedStatementResult);
  }

  /**
   * Test validation of multiple statement records with duplicate reference but correct end balance
   */
  @Test
  void validateStatementRecordsDuplicateReferences() {
    //given
    List<StatementRecord> statementRecords = new ArrayList<>();
    StatementRecord statementRecord = StatementRecord.builder().reference(1l).accountNumber("123")
        .description("Some Description").startBalance(new BigDecimal(100))
        .mutation(new BigDecimal(10)).endBalance(new BigDecimal(110)).build();
    StatementRecord statementRecord1 = StatementRecord.builder().reference(1l).accountNumber("456")
        .description("Some Description").startBalance(new BigDecimal(100))
        .mutation(new BigDecimal(10)).endBalance(new BigDecimal(110)).build();
    statementRecords.add(statementRecord);
    statementRecords.add(statementRecord1);

    //when
    StatementResult actualStatementResult = underTest.validateStatementRecords(statementRecords);

    ErrorRecord errorRecord = ErrorRecord.builder().reference(1l).accountNumber("123").build();
    List<ErrorRecord> errorRecords = new ArrayList<>();
    errorRecords.add(errorRecord);

    StatementResult expectedStatementResult = StatementResult.builder()
        .result(StatementValidationProcessor.DUPLICATE_REFERENCE).errorRecords(
            errorRecords).build();

    //then
    assertThat(actualStatementResult).isNotNull();
    assertThat(actualStatementResult).isEqualTo(expectedStatementResult);
  }

  /**
   * Test validation of multiple statement records with unique reference but incorrect end balance
   */
  @Test
  void validateStatementRecordsInvalidEndBalance() {
    //given
    List<StatementRecord> statementRecords = new ArrayList<>();
    StatementRecord statementRecord = StatementRecord.builder().reference(1l).accountNumber("123")
        .description("Some Description").startBalance(new BigDecimal(100))
        .mutation(new BigDecimal(10)).endBalance(new BigDecimal(110)).build();
    StatementRecord statementRecord1 = StatementRecord.builder().reference(2l).accountNumber("456")
        .description("Some Description").startBalance(new BigDecimal(100))
        .mutation(new BigDecimal(-10)).endBalance(new BigDecimal(110)).build();
    statementRecords.add(statementRecord);
    statementRecords.add(statementRecord1);

    //when
    StatementResult actualStatementResult = underTest.validateStatementRecords(statementRecords);

    ErrorRecord errorRecord = ErrorRecord.builder().reference(2l).accountNumber("456").build();
    List<ErrorRecord> errorRecords = new ArrayList<>();
    errorRecords.add(errorRecord);

    StatementResult expectedStatementResult = StatementResult.builder()
        .result(StatementValidationProcessor.INCORRECT_END_BALANCE).errorRecords(
            errorRecords).build();

    //then
    assertThat(actualStatementResult).isNotNull();
    assertThat(actualStatementResult).isEqualTo(expectedStatementResult);
  }

  /**
   * Test validation of multiple statement records with unique reference but incorrect end balance
   */
  @Test
  void validateStatementRecordsDuplicateReferenceInvalidEndBalance() {
    //given
    List<StatementRecord> statementRecords = new ArrayList<>();
    StatementRecord statementRecord = StatementRecord.builder().reference(1l).accountNumber("123")
        .description("Some Description").startBalance(new BigDecimal(100))
        .mutation(new BigDecimal(10)).endBalance(new BigDecimal(110)).build();
    StatementRecord statementRecord1 = StatementRecord.builder().reference(1l).accountNumber("456")
        .description("Some Description").startBalance(new BigDecimal(100))
        .mutation(new BigDecimal(-10)).endBalance(new BigDecimal(110)).build();
    StatementRecord statementRecord2 = StatementRecord.builder().reference(2l).accountNumber("456")
        .description("Some Description").startBalance(new BigDecimal(100))
        .mutation(new BigDecimal(-10)).endBalance(new BigDecimal(110)).build();
    StatementRecord statementRecord3 = StatementRecord.builder().reference(2l).accountNumber("456")
        .description("Some Description").startBalance(new BigDecimal(100))
        .mutation(new BigDecimal(10)).endBalance(new BigDecimal(110)).build();
    statementRecords.add(statementRecord);
    statementRecords.add(statementRecord1);
    statementRecords.add(statementRecord2);
    statementRecords.add(statementRecord3);

    //when
    StatementResult actualStatementResult = underTest.validateStatementRecords(statementRecords);

    ErrorRecord errorRecord = ErrorRecord.builder().reference(1l).accountNumber("123").build();
    ErrorRecord errorRecord1 = ErrorRecord.builder().reference(2l).accountNumber("456").build();
    List<ErrorRecord> errorRecords = new ArrayList<>();
    errorRecords.add(errorRecord);
    errorRecords.add(errorRecord1);

    StatementResult expectedStatementResult = StatementResult.builder()
        .result(StatementValidationProcessor.DUPLICATE_REFERENCE_INCORRECT_END_BALANCE)
        .errorRecords(
            errorRecords).build();

    //then
    assertThat(actualStatementResult).isNotNull();
    assertThat(actualStatementResult).isEqualTo(expectedStatementResult);
  }
}