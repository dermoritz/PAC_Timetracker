package com.prodyna.pac.timtracker.webapi.webSockets;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Encodes objects to send json via webocket.
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
public class JSONEncoder implements Encoder.Text<Object> {

    private ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public void init(EndpointConfig config) {
        //
    }

    @Override
    public void destroy() {
        mapper = null;
    }

    @Override
    public String encode(Object object) throws EncodeException {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new EncodeException(object, "Problem on encoding object to json, cause: ", e);
        }
    }
    
}
