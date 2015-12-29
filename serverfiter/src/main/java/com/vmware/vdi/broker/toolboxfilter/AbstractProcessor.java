package com.vmware.vdi.broker.toolboxfilter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;

public abstract class AbstractProcessor implements XmlMessageProcessor {



    protected static final Logger log = Logger
            .getLogger(AbstractProcessor.class);


    protected Element element;


    protected Map<String, Object> attributes = new HashMap<String, Object>();

    protected static final String TUNNEL_MVDI_REASON = "mvdi";

    public AbstractProcessor(Element element) {
        this.element = element;
    }





    public Map<String, Object> getServletRequestAttributes() {
        return attributes;
    }



    /**
     * Helper function for parsing XML API input. This will handle grabbing a
     * child element of the input key from the input element. If the element is
     * missing, a result is set and null is returned. Otherwise the parsed
     * element is returned.
     *
     * @param request
     *                The HttpServletRequest for this API message handler.
     * @param element
     *                The element to parse the child element from.
     * @param key
     *                The key of the child element to parse.
     *
     * @return The parsed element.
     */
    protected Element getRequiredChildElement(HttpServletRequest request,
            Element element, String key) {

        Element child = getOptionalChildElement(element, key);
        return child;
    }

    /**
     * Helper function for parsing XML API input. This will handle grabbing all
     * child elements of the input key from the input element. If no elements
     * match the key, a result is set and null is returned. Otherwise the parsed
     * elements are returned.
     *
     * @param request
     *                The HttpServletRequest for this API message handler.
     * @param element
     *                The element to parse the child elements from.
     * @param key
     *                The key of the child elements to parse.
     *
     * @return The parsed elements.
     */
    protected List<Element> getRequiredChildElements(
            HttpServletRequest request, Element element, String key) {

        List<Element> children = getOptionalChildElements(element, key);
        return children;
    }

    /**
     * Helper function for parsing XML API input. This will handle grabbing a
     * child element of the input key from the input element. If the element is
     * missing, null is returned but no error is set. Otherwise the parsed
     * element is returned.
     *
     * @param element
     *                The element to parse the child element from.
     * @param key
     *                The key of the child element to parse.
     *
     * @return The parsed element.
     */
    protected Element getOptionalChildElement(Element element, String key) {
        return element.getChild(key);
    }

    /**
     * Helper function for parsing XML API input. This will handle grabbing all
     * child elements of the input key from the input element. If no elements
     * match the key, null is returned but no error is set. Otherwise the parsed
     * elements are returned.
     *
     * @param element
     *                The element to parse the child elements from.
     * @param key
     *                The key of the child elements to parse.
     *
     * @return The parsed elements.
     */
    protected List<Element> getOptionalChildElements(Element element, String key) {
        List<Element> children = element.getChildren(key);
        if (children.size() == 0)
            return null;

        return children;
    }

    /**
     * Helper function for parsing XML API input. This will handle grabbing the
     * input key out of the root element for this message. If the key is missing
     * a result is set and null is returned, otherwise the parsed value is
     * returned.
     *
     * @param request
     *                The HttpServletRequest for this API message handler.
     * @param key
     *                The key of the value to parse.
     *
     * @return The parsed string value.
     */
    protected String parseInputValue(HttpServletRequest request, String key) {
        return parseValueInElement(request, element, key);
    }

    /**
     * Helper function for parsing XML API input. This will handle grabbing the
     * input value key out of the input element for this message. If the value
     * is missing a result is set and null is returned, otherwise the parsed
     * value is returned.
     *
     * @param request
     *                The HttpServletRequest for this API message handler.
     * @param elem
     *                The element to parse.
     * @param key
     *                The key of the value to parse.
     *
     * @return The parsed string value, or null.
     */
    protected String parseValueInElement(HttpServletRequest request,
            Element elem, String key) {

        String value = null;
        value = parseOptionalValueInElement(elem, key);
        return value;
    }

    /**
     * Helper function for parsing XML API input. This will handle grabbing the
     * input key out of the root element for this message. If the key is
     * missing, null is returned but no error is set, otherwise the parsed value
     * is returned.
     *
     * @param request
     *                The HttpServletRequest for this API message handler.
     * @param key
     *                The key of the value to parse.
     *
     * @return The parsed string value.
     */
    protected String parseOptionalInputValue(HttpServletRequest request,
            String key) {

        return parseOptionalValueInElement(element, key);
    }

    /**
     * Helper function for parsing XML API input. This will handle grabbing the
     * input key out of the input element for this message. If the key is
     * missing, null is returned but no error is set, otherwise the parsed value
     * is returned.
     *
     * @param elem
     *                The element to parse.
     * @param key
     *                The key of the value to parse.
     *
     * @return The parsed string value.
     */
    protected String parseOptionalValueInElement(Element elem, String key) {
        String value = null;

        Element el = elem.getChild(key);
        if (el != null) {
            String temp = el.getTextTrim();
            if (StringUtils.isNotBlank(temp)) {
                value = temp;
            }
        }

        return value;
    }

    /**
     * Helper function for parsing XML API input. This will handle grabbing the
     * input attribute key out of the input element for this message. If the
     * attribute is missing a result is set and null is returned, otherwise the
     * parsed attribute is returned.
     *
     * @param request
     *                The HttpServletRequest for this API message handler.
     * @param elem
     *                The element to parse.
     * @param key
     *                The key of the attribute to parse.
     *
     * @return The parsed string attribute.
     */
    protected String parseAttributeInElement(HttpServletRequest request,
            Element elem, String key) {

        String value = parseOptionalAttributeInElement(elem, key);

        return value;
    }

    /**
     * Helper function for parsing XML API input. This will handle grabbing the
     * input attribute key out of the input element for this message. If the
     * attribute is missing, null is returned but no result is set, otherwise
     * the parsed attribute is returned.
     *
     * @param elem
     *                The element to parse.
     * @param key
     *                The key of the attribute to parse.
     *
     * @return The parsed string attribute.
     */
    protected String parseOptionalAttributeInElement(Element elem, String key) {
        String attribValue = null;

        Attribute attr = elem.getAttribute(key);
        if (attr != null) {
            String temp = attr.getValue().trim();
            if (StringUtils.isNotBlank(temp)) {
                attribValue = temp;
            }
        }

        return attribValue;
    }

    /**
     * Helper function for parsing XML API input. This will handle grabbing the
     * value of the input element for this message. If the value is missing a
     * result is set and null is returned, otherwise the parsed value is
     * returned.
     *
     * @param request
     *                The HttpServletRequest for this API message handler.
     * @param elem
     *                The element with the value to parse.
     *
     * @return The parsed string value.
     */
    protected String parseElementValue(HttpServletRequest request, Element elem) {
        String value = null;

        String temp = elem.getTextTrim();
        if (StringUtils.isNotBlank(temp)) {
            value = temp;
        }


        return value;
    }



    public String getJspFolderName() {
        return null;
    }


    protected Set<String> getOptionalSet(final String setName,
            final String itemName, final Collection<String> accepted) {

        Element setElement = getOptionalChildElement(element, setName);

        Set<String> returnSet = null;

        if (null != setElement) {

            @SuppressWarnings("rawtypes")
            List items = setElement.getChildren(itemName);

            if (null != items && !items.isEmpty()) {

                for (Object o : items) {

                    Element e = (Element) o;

                    String item = e.getTextTrim();

                    if (accepted.contains(item)) {

                        if (returnSet == null) {
                            returnSet = new HashSet<String>();
                        }
                        returnSet.add(item);
                    }
                }
            }
        }

        if (returnSet == null) {
            /*
             * Return an empty Set to indicate that the client has called
             * get-configuration but hasn't specified any (valid) values.
             */
            returnSet = Collections.emptySet();
        }

        return returnSet;
    }
}