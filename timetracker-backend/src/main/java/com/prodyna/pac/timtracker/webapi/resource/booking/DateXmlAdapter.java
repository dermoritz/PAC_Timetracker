package com.prodyna.pac.timtracker.webapi.resource.booking;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapter to convert date to xml date format and vice versa.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
public class DateXmlAdapter extends XmlAdapter<String, Date> {
    
    /**
     * Thread safe {@link DateFormat}.
     */
    private static final ThreadLocal<DateFormat> DATE_FORMAT_TL = new ThreadLocal<DateFormat>() {

        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        }

    };
    
    @Override
    public Date unmarshal(String v) throws Exception {
        return DATE_FORMAT_TL.get().parse(v);
    }

    @Override
    public String marshal(Date v) throws Exception {
        return DATE_FORMAT_TL.get().format(v);
    }

}
