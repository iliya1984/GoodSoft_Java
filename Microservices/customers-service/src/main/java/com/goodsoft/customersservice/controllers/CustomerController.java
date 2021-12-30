package com.goodsoft.customersservice.controllers;

import com.goodsoft.customersservice.logic.createcustomer.CreateCustomerCommand;
import com.goodsoft.customersservice.logic.getcustomer.GetCustomerQuery;
import com.goodsoft.customersservice.logic.search.SearchCustomersQuery;
import com.goodsoft.infra.mediator.errorhandling.ValidationException;
import com.goodsoft.infra.mediator.factory.IPipelineFactory;
import com.goodsoft.infra.microservice.RestService;
import com.goodsoft.interfaces.customers.ICustomersService;
import com.goodsoft.interfaces.customers.models.customers.CreateCustomerModel;
import com.goodsoft.interfaces.customers.models.customers.GetCustomerResponse;
import com.goodsoft.interfaces.customers.models.search.CustomersSearchResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/customers")
public class CustomerController extends RestService implements ICustomersService
{
    private IPipelineFactory _pipelineFactory;

    public CustomerController(IPipelineFactory pipelineFactory)
    {
        _pipelineFactory = pipelineFactory;
    }

    @GetMapping(value = "/{id}")
    public GetCustomerResponse findById(@PathVariable("id") Long id) {

        var request = new GetCustomerQuery();
        request.setCustomerId(id);

        var pipeline = _pipelineFactory.getPipeline(request);
        var customer = pipeline.send(request);

        var response  = new GetCustomerResponse();
        response.setCustomer(customer);

        return response;
    }

    @GetMapping("/search")
    public CustomersSearchResponse search(String searchTerm, String filters, int skip, int take)
    {
        var query = new SearchCustomersQuery();
        query.setSearchTerm(searchTerm);
        query.setFilters(filters);
        query.setSkip(skip);
        query.setTake(take);

        var pipeline = _pipelineFactory.getPipeline(query);
        var searchResult = pipeline.send(query);

        var response  = new CustomersSearchResponse();
        response.setResult(searchResult);

        return response;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> create(@RequestBody CreateCustomerModel model)
    {
        try
        {
            var request = new CreateCustomerCommand();
            request.setModel(model);

            var pipeline = _pipelineFactory.getPipeline(request);
            var customer = pipeline.send(request);

            return new ResponseEntity(customer, HttpStatus.CREATED);
        }
        catch(Exception ex)
        {
            return exceptionResponse(ex);
        }
    }
}
