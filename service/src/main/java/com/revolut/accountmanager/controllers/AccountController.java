package com.revolut.accountmanager.controllers;

import com.revolut.accountmanager.factory.ActionFactory;
import com.revolut.model.RequestContainer;
import com.revolut.model.entity.AccountView;
import com.revolut.model.requests.AccountDepositRequest;
import com.revolut.model.requests.AccountRegisterRequest;
import com.revolut.model.requests.AccountWithdrawRequest;
import com.revolut.model.responses.AccountStatementResponse;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import static com.revolut.accountmanager.util.ResponseUtils.handleException;

@Path("/v1/account")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class AccountController {

    @Inject
    private ActionFactory actionFactory;

    @PUT
    @Path("/")
    public Response register(@Valid AccountRegisterRequest registerRequest){
        try{
            AccountView account = actionFactory.accountRegistrationAction().process(registerRequest);
            return Response.ok(account).build();
        }catch (Exception e){
            return handleException(e);
        }
    }

    @GET
    @Path("/{accountId}")
    public Response view(@PathParam("accountId") @NotNull Integer accountId){
        try{
            log.info("View request for id : {}", accountId);
            AccountView account = actionFactory.viewAccountAction().process(accountId);
            return Response.ok(account).build();
        }catch (Exception e){
            return handleException(e);
        }
    }

    @POST
    @Path("/{accountId}/deposit")
    public Response deposit(@NotNull @HeaderParam("X-Request-Id") String requestId,
                            @PathParam("accountId") Integer accountId,
                            @Valid AccountDepositRequest accountDepositRequest){
        try{
            log.info("View request for id : {}", accountId);
            RequestContainer.set(requestId);
            AccountView account = actionFactory.accountDepositAction(accountId).process(accountDepositRequest);
            return Response.ok(account).build();
        }catch (Exception e){
            return handleException(e);
        }finally {
            RequestContainer.clear();
        }
    }

    @POST
    @Path("/{accountId}/withdraw")
    public Response withdraw(@NotNull @HeaderParam("X-Request-Id") String requestId,
                             @PathParam("accountId") Integer accountId,
                             @Valid AccountWithdrawRequest accountWithdrawRequest){
        try{
            RequestContainer.set(requestId);
            AccountView account = actionFactory.accountWithdrawAction(accountId).process(accountWithdrawRequest);
            return Response.ok(account).build();
        }catch (Exception e){
            return handleException(e);
        }finally {
            RequestContainer.clear();
        }
    }

    @GET
    @Path("/{accountId}/statement")
    // Ignoring the paginated way of responding in case of too many records !
    public Response statement(@PathParam("accountId") Integer accountId){
        try{
            AccountStatementResponse statementResponse = actionFactory.viewStatementAction().process(accountId);
            return Response.ok(statementResponse).build();
        }catch (Exception e){
            return handleException(e);
        }finally {
            RequestContainer.clear();
        }
    }

}
