package com.rabobank.nl.statementprocessor.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabobank.nl.statementprocessor.api.model.ErrorRecord;
import com.rabobank.nl.statementprocessor.api.model.StatementRecord;
import com.rabobank.nl.statementprocessor.api.model.StatementResult;
import com.rabobank.nl.statementprocessor.service.StatementValidationProcessor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(controllers = CustomerStatementProcessorController.class)
class CustomerStatementProcessorControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  StatementValidationProcessor statementValidationProcessor;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void process_CustomerStatementHappy() throws Exception {

    //Given
    List<StatementRecord> statementRecords = new ArrayList<>();
    StatementRecord statementRecord = StatementRecord.builder().reference(1l).accountNumber("123")
        .description("Some Description").startBalance(new BigDecimal(100))
        .mutation(new BigDecimal(10)).endBalance(new BigDecimal(110)).build();
    StatementRecord statementRecord1 = StatementRecord.builder().reference(2l).accountNumber("123")
        .description("Some Description").startBalance(new BigDecimal(100))
        .mutation(new BigDecimal(10)).endBalance(new BigDecimal(110)).build();
    statementRecords.add(statementRecord);
    statementRecords.add(statementRecord1);

    //When
    StatementResult statementResult = StatementResult.builder()
        .result(StatementValidationProcessor.SUCCESS).errorRecords(Collections.emptyList()).build();
    when(statementValidationProcessor.validateStatementRecords(anyList())).thenReturn(
        statementResult);

    MvcResult mvcResult = mockMvc.perform(
        post("/customerstatement/v1/process")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(statementRecords)))
        .andExpect(status().isOk())
        .andReturn();

    String actualResponseBody = mvcResult.getResponse().getContentAsString();
    String expectedResponseBody = objectMapper.writeValueAsString(statementResult);

    //Then
    assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
  }

  @Test
  void process_CustomerStatementWithDuplicateReferences() throws Exception {

    //Given
    List<StatementRecord> statementRecords = new ArrayList<>();
    StatementRecord statementRecord = StatementRecord.builder().reference(1l).accountNumber("123")
        .description("Some Description").startBalance(new BigDecimal(100))
        .mutation(new BigDecimal(10)).endBalance(new BigDecimal(110)).build();
    StatementRecord statementRecord1 = StatementRecord.builder().reference(1l).accountNumber("456")
        .description("Some Description").startBalance(new BigDecimal(100))
        .mutation(new BigDecimal(10)).endBalance(new BigDecimal(110)).build();
    statementRecords.add(statementRecord);
    statementRecords.add(statementRecord1);

    //When
    ErrorRecord errorRecord = ErrorRecord.builder().reference(1l).accountNumber("123").build();
    List<ErrorRecord> errorRecords = new ArrayList<>();
    errorRecords.add(errorRecord);
    StatementResult statementResult = StatementResult.builder()
        .result(StatementValidationProcessor.DUPLICATE_REFERENCE).errorRecords(errorRecords)
        .build();
    when(statementValidationProcessor.validateStatementRecords(anyList())).thenReturn(
        statementResult);

    MvcResult mvcResult = mockMvc.perform(
        post("/customerstatement/v1/process")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(statementRecords)))
        .andExpect(status().isOk())
        .andReturn();

    String actualResponseBody = mvcResult.getResponse().getContentAsString();
    String expectedResponseBody = objectMapper.writeValueAsString(statementResult);

    //Then
    assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
  }

  @Test
  void process_CustomerStatementWithInvalidEndBalance() throws Exception {

    //Given
    List<StatementRecord> statementRecords = new ArrayList<>();
    StatementRecord statementRecord = StatementRecord.builder().reference(1l).accountNumber("123")
        .description("Some Description").startBalance(new BigDecimal(100))
        .mutation(new BigDecimal(10)).endBalance(new BigDecimal(110)).build();
    StatementRecord statementRecord1 = StatementRecord.builder().reference(2l).accountNumber("456")
        .description("Some Description").startBalance(new BigDecimal(100))
        .mutation(new BigDecimal(-10)).endBalance(new BigDecimal(110)).build();
    statementRecords.add(statementRecord);
    statementRecords.add(statementRecord1);

    //When
    ErrorRecord errorRecord = ErrorRecord.builder().reference(2l).accountNumber("456").build();
    List<ErrorRecord> errorRecords = new ArrayList<>();
    errorRecords.add(errorRecord);
    StatementResult statementResult = StatementResult.builder()
        .result(StatementValidationProcessor.INCORRECT_END_BALANCE).errorRecords(errorRecords)
        .build();
    when(statementValidationProcessor.validateStatementRecords(anyList())).thenReturn(
        statementResult);

    MvcResult mvcResult = mockMvc.perform(
        post("/customerstatement/v1/process")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(statementRecords)))
        .andExpect(status().isOk())
        .andReturn();

    String actualResponseBody = mvcResult.getResponse().getContentAsString();
    String expectedResponseBody = objectMapper.writeValueAsString(statementResult);

    //Then
    assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
  }

  @Test
  void process_CustomerStatementWithDuplicateReferenceInvalidEndBalance() throws Exception {

    //Given
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

    //When
    ErrorRecord errorRecord = ErrorRecord.builder().reference(1l).accountNumber("123").build();
    ErrorRecord errorRecord1 = ErrorRecord.builder().reference(2l).accountNumber("456").build();
    List<ErrorRecord> errorRecords = new ArrayList<>();
    errorRecords.add(errorRecord);
    errorRecords.add(errorRecord1);

    StatementResult statementResult = StatementResult.builder()
        .result(StatementValidationProcessor.DUPLICATE_REFERENCE_INCORRECT_END_BALANCE)
        .errorRecords(errorRecords).build();
    when(statementValidationProcessor.validateStatementRecords(anyList())).thenReturn(
        statementResult);

    MvcResult mvcResult = mockMvc.perform(
        post("/customerstatement/v1/process")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(statementRecords)))
        .andExpect(status().isOk())
        .andReturn();

    String actualResponseBody = mvcResult.getResponse().getContentAsString();
    String expectedResponseBody = objectMapper.writeValueAsString(statementResult);

    //Then
    assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
  }

  @Test
  void process_CustomerStatementJsonParsingException() throws Exception {

    //Given
    StatementResult statementResults = StatementResult.builder().build();

    //When
    MvcResult mvcResult = mockMvc.perform(
        post("/customerstatement/v1/process")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(statementResults)))
        .andExpect(status().isBadRequest())
        .andReturn();

    StatementResult statementResult = StatementResult.builder()
        .result("BAD_REQUEST").errorRecords(Collections.emptyList()).build();
    String actualResponseBody = mvcResult.getResponse().getContentAsString();
    String expectedResponseBody = objectMapper.writeValueAsString(statementResult);

    //Then
    assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
  }

  @Test
  void process_CustomerStatementOtherErrors() throws Exception {

    //Given

    //When
    MvcResult mvcResult = mockMvc.perform(
        put("/customerstatement/v1/process")
            .contentType("application/json"))
        .andExpect(status().is5xxServerError())
        .andReturn();

    StatementResult statementResult = StatementResult.builder()
        .result("INTERNAL_SERVER_ERROR").errorRecords(Collections.emptyList()).build();
    String actualResponseBody = mvcResult.getResponse().getContentAsString();
    String expectedResponseBody = objectMapper.writeValueAsString(statementResult);

    //Then
    assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
  }
}