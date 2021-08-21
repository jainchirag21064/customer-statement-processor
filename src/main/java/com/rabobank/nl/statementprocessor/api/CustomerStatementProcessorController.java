package com.rabobank.nl.statementprocessor.api;

import com.rabobank.nl.statementprocessor.api.model.StatementRecord;
import com.rabobank.nl.statementprocessor.api.model.StatementResult;
import com.rabobank.nl.statementprocessor.service.StatementValidationProcessor;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller is the entry for resource Customer Statement Processor API
 */
@RestController
@RequestMapping("/customerstatement")
@RequiredArgsConstructor
@Slf4j
@OpenAPIDefinition(info = @Info(
    title = "Customer Statement Processor",
    version = "1.0",
    contact = @Contact(
        name = "Customer Statement Processor API Support",
        url = "http://exampleurl.com/contact",
        email = "jainchirag21064@gmail.com")
))
public class CustomerStatementProcessorController {

  private final StatementValidationProcessor statementValidationProcessor;

  @Operation(summary = "Process the customer statement")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully performed validation on customer statement.",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = StatementResult.class), examples = @ExampleObject(value = "{\"result\":\"SUCCESS\",\"errorRecords\" : []}"))}),
      @ApiResponse(responseCode = "200", description = "Duplicate Data Reference or Incorrect End Balance or both",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = StatementResult.class), examples = @ExampleObject(value = "{\"result\":\"DUPLICATE_REFERENCE\",\"errorRecords\" : [{\"reference\":\"266362727632761273627\",\"accountNumber\":\"1232323\"}]}"))}),
      @ApiResponse(responseCode = "400", description = "Error during parsing JSON",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = StatementResult.class), examples = @ExampleObject(value = "{\"result\":\"BAD_REQUEST\",\"errorRecords\" : []}"))}),
      @ApiResponse(responseCode = "500", description = "Any other situation",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = StatementResult.class), examples = @ExampleObject(value = "{\"result\":\"INTERNAL_SERVER_ERROR\",\"errorRecords\" : []}"))})
  })
  @PostMapping("/v1/process")
  public StatementResult process(
      @RequestBody List<StatementRecord> statementRecords) {
    return statementValidationProcessor.validateStatementRecords(statementRecords);
  }

}
