package com.revolut.accountmanager.util;

import com.revolut.model.responses.ServiceError;

import javax.ws.rs.core.Response;

public class ResponseUtils {

    public static Response handleException(Throwable t){
        if(t instanceof IllegalStateException || t instanceof IllegalArgumentException){
            return Response.status(Response.Status.BAD_REQUEST).entity(new ServiceError(t.getMessage())).build();
        }else{
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ServiceError(t.getMessage())).build();
        }
    }
}
