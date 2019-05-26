package com.revolut.accountmanager.controllers;

import com.revolut.model.requests.AccountRegisterRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountController {

    @PUT
    @Path("/")
    public Response register(AccountRegisterRequest registerRequest){
        return null;
    }

    @GET
    @Path("/{accountId}")
    public Response view(@PathParam("accountId") String accountId){
        return null;
    }

    @GET
    @Path("/{accountId}/statement")
    // TODO : to give paginated response
    public Response statement(@PathParam("accountId") String accountId){
        return null;
    }

}
