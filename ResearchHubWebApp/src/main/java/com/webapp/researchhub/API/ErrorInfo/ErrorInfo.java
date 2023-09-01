package com.webapp.researchhub.API.ErrorInfo;


/**
 * The class handles errors within our api's so that the body displays a message.
 * @author jwm22
 */
public class ErrorInfo {

    public final String message;

    public ErrorInfo(String message) {
        this.message = message;
    }
}
