package com.rabobank.nl.statementprocessor.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabobank.nl.statementprocessor.api.model.StatementRecord;
import com.rabobank.nl.statementprocessor.api.model.StatementResult;
import com.rabobank.nl.statementprocessor.service.StatementValidationProcessor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerStatementProcessorControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;


  @Test
  void process_CustomerStatementThroughAllLayersHappyScenario() throws Exception {
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

    //when
    MvcResult mvcResult = mockMvc.perform(
        post("/customerstatement/v1/process")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(statementRecords)))
        .andExpect(status().isOk())
        .andReturn();

    String actualResponseBody = mvcResult.getResponse().getContentAsString();

    StatementResult statementResult = StatementResult.builder()
        .result(StatementValidationProcessor.SUCCESS).errorRecords(Collections.emptyList()).build();
    String expectedResponseBody = objectMapper.writeValueAsString(statementResult);

    //Then
    assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
  }

  @Test
  void process_CustomerStatementThroughAllLayersWithJsonParsingException() throws Exception {
    //Given
    StatementResult statementResults = StatementResult.builder().build();
    //when
    MvcResult mvcResult = mockMvc.perform(
        post("/customerstatement/v1/process")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(statementResults)))
        .andExpect(status().isBadRequest())
        .andReturn();

    String actualResponseBody = mvcResult.getResponse().getContentAsString();

    StatementResult statementResult = StatementResult.builder()
        .result("BAD_REQUEST").errorRecords(Collections.emptyList()).build();
    String expectedResponseBody = objectMapper.writeValueAsString(statementResult);

    //Then
    assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
  }

  @Test
  void process_CustomerStatementThroughAllLayersOtherErrors() throws Exception {

    //when
    MvcResult mvcResult = mockMvc.perform(
        put("/customerstatement/v1/process")
            .contentType("application/json"))
        .andExpect(status().is5xxServerError())
        .andReturn();

    String actualResponseBody = mvcResult.getResponse().getContentAsString();

    StatementResult statementResult = StatementResult.builder()
        .result("INTERNAL_SERVER_ERROR").errorRecords(Collections.emptyList()).build();
    String expectedResponseBody = objectMapper.writeValueAsString(statementResult);

    //Then
    assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
  }


}