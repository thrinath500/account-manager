package com.revolut.accountmanager.controllers;

import com.revolut.accountmanager.factory.ActionFactory;
import com.revolut.model.requests.MoneyTransferRequest;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.revolut.accountmanager.util.ResponseUtils.handleException;

@Path("/v1/transaction")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionController {

    @Inject
    private ActionFactory actionFactory;

    @POST
    @Path("/transfer")
    public Response transfer(@Valid MoneyTransferRequest moneyTransferRequest){
        try{
            actionFactory.moneyTransferAction().process(moneyTransferRequest);
            return Response.ok().build();
        }catch (Exception e){
            return handleException(e);
        }
    }


}
