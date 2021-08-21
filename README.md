# Customer Statement Processor

### Summary ###
Bank receives monthly deliveries of customer statement records. This information is delivered in JSON format. 
These records need to be validated. based on below conditions
    * all transaction references should be unique
    * end balance needs to be validated
    * Return both the transaction reference and description of each of the failed records

### Assignment ###
Implement a REST service which receives the customer statement JSON as a POST data, Perform the below validations
1. All transaction references should be unique
2. The end balance needs to be validated ( Start Balance +/- Mutation = End Balance )

##Run locally

```bash
#Clone the repository
git clone https://github.com/jainchirag21064/customer-statement-processor.git

#Start application
mvn spring-boot:run
```

The application can be accessed at `http://localhost:8081/customerstatement/v1/process`.

### API Specification ###
Open API specification can be accessed at below location
`http://localhost:8081/swagger-ui.html`

##Assumption/Improvement
The Customer Statement Request model is not validated which can be done using the @Valid annotation and definfing respective validation on the attribute.
