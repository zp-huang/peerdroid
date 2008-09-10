/*
 *  Copyright (c) 2001 Sun Microsystems, Inc.  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 *  3. The end-user documentation included with the redistribution,
 *  if any, must include the following acknowledgment:
 *  "This product includes software developed by the
 *  Sun Microsystems, Inc. for Project JXTA."
 *  Alternately, this acknowledgment may appear in the software itself,
 *  if and wherever such third-party acknowledgments normally appear.
 *
 *  4. The names "Sun", "Sun Microsystems, Inc.", "JXTA" and "Project JXTA" must
 *  not be used to endorse or promote products derived from this
 *  software without prior written permission. For written
 *  permission, please contact Project JXTA at http://www.jxta.org.
 *
 *  5. Products derived from this software may not be called "JXTA",
 *  nor may "JXTA" appear in their name, without prior written
 *  permission of Sun.
 *
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED.  IN NO EVENT SHALL SUN MICROSYSTEMS OR
 *  ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 *  USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 *  SUCH DAMAGE.
 *  ====================================================================
 *
 *  This software consists of voluntary contributions made by many
 *  individuals on behalf of Project JXTA.  For more
 *  information on Project JXTA, please see
 *  <http://www.jxta.org/>.
 *
 *  This license is based on the BSD license adopted by the Apache Foundation.
 *
 *  $Id: PlatformConfig.java,v 1.2 2005/05/24 02:39:56 hamada Exp $
 */
package net.jxta.impl.protocol;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.AdvertisementFactory.Instantiator;
import net.jxta.document.Attributable;
import net.jxta.document.Attribute;
import net.jxta.document.Document;
import net.jxta.document.Element;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocument;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.StructuredDocumentUtils;
import net.jxta.document.StructuredTextDocument;
import net.jxta.document.TextElement;
import net.jxta.document.XMLElement;
import net.jxta.id.ID;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.platform.ModuleClassID;
import net.jxta.protocol.ConfigParams;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *  This type of advertisement is generated by configuration and serves to pass
 *  to all services their optional user-driven configuration. Some of which may
 *  be published as part of the above process. A PlatformConfig is not itself
 *  published.
 */
public final class PlatformConfig extends ConfigParams {

    /**
     *  Description of the Field
     */
    public final static String DEBUG_TAG = "Dbg";
    /**
     *  Description of the Field
     */
    public final static String DEBUG_TAG_DEFAULT = "user default";
    /**
     *  Description of the Field
     */
    public final static String DESC_TAG = "Desc";
    private final static Logger LOG = Logger.getLogger(PlatformConfig.class.getName());
    /**
     *  Description of the Field
     */
    public final static String MCID_TAG = "MCID";
    /**
     *  Description of the Field
     */
    public final static String NAME_TAG = "Name";
    /**
     *  Description of the Field
     */
    public final static String PARAM_TAG = "Parm";
    /**
     *  Description of the Field
     */
    public final static String PID_TAG = "PID";
    /**
     *  Description of the Field
     */
    public final static String SVC_TAG = "Svc";
    private final static String advType = "jxta:PlatformConfig";

    private String debugLevel = DEBUG_TAG_DEFAULT;

    /**
     *  Descriptive meta-data about this peer.
     */
    private Element description = null;
    private final static String[] fields = {NAME_TAG, PID_TAG};
    /**
     *  Description of the Field
     */
    protected Map indexMap = new HashMap();

    /**
     *  The name of this peer. Not gaurnteed to be unique in any way. May be
     *  empty or null.
     */
    private String name = null;

    /**
     *  The id of this peer.
     */
    private PeerID pid = null;

    /**
     *  Use the Instantiator through the factory
     */
    PlatformConfig() { }

    /**
     *  Use the Instantiator through the factory
     *
     *@param  root  Description of the Parameter
     */
    PlatformConfig(Element root) {
        if (!XMLElement.class.isInstance(root)) {
            throw new IllegalArgumentException(getClass().getName() + " only supports XLMElement");
        }

        XMLElement doc = (XMLElement) root;

        String doctype = doc.getName();

        String typedoctype = "";
        Attribute itsType = doc.getAttribute("type");
        if (null != itsType) {
            typedoctype = itsType.getValue();
        }

        if (!doctype.equals(getAdvertisementType()) && !getAdvertisementType().equals(typedoctype)) {
            throw new IllegalArgumentException("Could not construct : "
                     + getClass().getName() + "from doc containing a " + doc.getName());
        }

        Enumeration elements = doc.getChildren();

        while (elements.hasMoreElements()) {
            Element elem = (Element) elements.nextElement();

            if (!handleElement(elem)) {
                if (LOG.isEnabledFor(Level.DEBUG)) {
                    LOG.debug("Unhandled Element: " + elem.toString());
                }
            }
        }

        // Sanity Check!!!

    }

    /**
     *  Make a safe clone of this PlatformConfig.
     *
     *@return    Object an object of class PlatformConfig that is a deep-enough
     *      copy of this one.
     */
    public Object clone() {

        PlatformConfig result = new PlatformConfig();

        result.setPeerID(getPeerID());
        result.setName(getName());
        result.setDesc(getDesc());
        result.setDebugLevel(getDebugLevel());

        Iterator eachEntry = getServiceParamsEntrySet().iterator();

        while (eachEntry.hasNext()) {
            Map.Entry anEntry = (Map.Entry) eachEntry.next();

            result.putServiceParam((ID) anEntry.getKey(), (Element) anEntry.getValue());
        }

        return result;
    }

    /**
     *  {@inheritDoc}
     *
     *@return    The advType value
     */
    public String getAdvType() {
        return getAdvertisementType();
    }

    /**
     *  returns the advertisement type
     *
     *@return    string type
     */
    public static String getAdvertisementType() {
        return advType;
    }

    /**
     *  returns the debugLevel
     *
     *@return    String the debugLevel
     */
    public String getDebugLevel() {
        return debugLevel.trim();
    }

    /**
     *  returns the description
     *
     *@return    the description
     */
    public StructuredDocument getDesc() {
        if (null != description) {
            StructuredDocument newDoc =
                    StructuredDocumentUtils.copyAsDocument(description);

            return newDoc;
        } else {
            return null;
        }
    }

    /**
     *  returns the description
     *
     *@return    String the description
     */
    public String getDescription() {
        return (null == description) ? (String) null : (String) description.getValue();
    }

    /**
     *  {@inheritDoc}
     *
     *@param  encodeAs  Description of the Parameter
     *@return           The document value
     */
    public Document getDocument(MimeMediaType encodeAs) {
        StructuredDocument adv = (StructuredDocument) super.getDocument(encodeAs);

        Element e;

        // peer ID is optional. (at least for the PlatformConfig it is)
        PeerID peerID = getPeerID();
        if ((null != peerID) && !ID.nullID.equals(peerID)) {
            e = adv.createElement(PID_TAG, peerID.toString());
            adv.appendChild(e);
        }

        // name is optional
        if (getName() != null) {
            e = adv.createElement(NAME_TAG, getName());
            adv.appendChild(e);
        }

        // desc is optional
        StructuredDocument desc = getDesc();
        if (desc != null) {
            StructuredDocumentUtils.copyElements(adv, adv, desc);
        }

        String debugLvl = getDebugLevel();
        if ((debugLvl != null) && !DEBUG_TAG_DEFAULT.equals(debugLvl)) {
            e = adv.createElement(DEBUG_TAG, debugLvl);
            adv.appendChild(e);
        }

        super.addDocumentElements(adv);

        return adv;
    }

    /**
     *  Returns a unique ID for that peer X group intersection. This is for
     *  indexing purposes only.
     *
     *@return    The iD value
     */

    public ID getID() {

        return pid;
    }

    /**
     *  {@inheritDoc}
     *
     *@return    The indexFields value
     */
    public final String[] getIndexFields() {
        return fields;
    }

    /**
     *  {@inheritDoc}
     *
     *@return    The indexMap value
     */
    public final Map getIndexMap() {
        return Collections.unmodifiableMap(indexMap);
    }

    /**
     *  returns the name of the peer.
     *
     *@return    String name of the peer.
     */

    public String getName() {
        return name;
    }

    /**
     *  Returns the id of the peer.
     *
     *@return    PeerID the peer id
     */

    public PeerID getPeerID() {
        return pid;
    }

    /**
     *  {@inheritDoc}
     *
     *@param  raw  Description of the Parameter
     *@return      Description of the Return Value
     */
    protected boolean handleElement(Element raw) {

        if (super.handleElement(raw)) {
            return true;
        }

        XMLElement elem = (XMLElement) raw;

        if (elem.getName().equals(PID_TAG)) {
            try {
                URI pID = new URI(elem.getTextValue());
                setPeerID((PeerID) IDFactory.fromURI(pID));
            } catch (URISyntaxException badID) {
                throw new IllegalArgumentException("Bad PeerID ID in advertisement: " + elem.getTextValue());
            } catch (ClassCastException badID) {
                throw new IllegalArgumentException("Id is not a peer id: " + elem.getTextValue());
            }
            return true;
        }

        if (elem.getName().equals(NAME_TAG)) {
            setName(elem.getTextValue());
            return true;
        }

        if (elem.getName().equals(DESC_TAG)) {
            setDesc(elem);
            return true;
        }

        if (DEBUG_TAG.equals(elem.getName())) {
            setDebugLevel(elem.getTextValue());
            return true;
        }

        return false;
    }

    /**
     *  sets the debugLevel
     *
     *@param  debugLevel  the debugLevel
     */
    public void setDebugLevel(String debugLevel) {
        this.debugLevel = debugLevel;
    }

    /**
     *  sets the description
     *
     *@param  desc  the description
     */
    public void setDesc(Element desc) {

        if (null != desc) {
            this.description = StructuredDocumentUtils.copyAsDocument(desc);
        } else {
            this.description = null;
        }
    }

    /**
     *  sets the description
     *
     *@param  description  the description
     */
    public void setDescription(String description) {

        if (null != description) {
            StructuredDocument newdoc =
                    StructuredDocumentFactory.newStructuredDocument(
                    MimeMediaType.XMLUTF8, "Desc", description);

            setDesc(newdoc);
        } else {
            this.description = null;
        }
    }

    /**
     *  sets the name of the peer.
     *
     *@param  name  name of the peer.
     */

    public void setName(String name) {
        this.name = name;
        if (name != null) {
            indexMap.put(NAME_TAG, name);
        }
    }

    /**
     *  Sets the id of the peer.
     *
     *@param  pid  the id of this peer.
     */

    public void setPeerID(PeerID pid) {
        this.pid = pid;
        if (pid != null) {
            indexMap.put(PID_TAG, pid.toString());
        }
    }

    /**
     *  Instantiator for PlatformConfig
     */
    public static class Instantiator implements AdvertisementFactory.Instantiator {

        /**
         *  Returns the identifying type of this Advertisement.
         *
         *@return    String the type of advertisement
         */
        public String getAdvertisementType() {
            return advType;
        }

        /**
         *  Constructs an instance of <CODE>Advertisement</CODE> matching the
         *  type specified by the <CODE>advertisementType</CODE> parameter.
         *
         *@return    The instance of <CODE>Advertisement</CODE> or null if it
         *      could not be created.
         */
        public Advertisement newInstance() {
            return new PlatformConfig();
        }

        /**
         *  Constructs an instance of <CODE>Advertisement</CODE> matching the
         *  type specified by the <CODE>advertisementType</CODE> parameter.
         *
         *@param  root  Specifies a portion of a StructuredDocument which will
         *      be converted into an Advertisement.
         *@return       The instance of <CODE>Advertisement</CODE> or null if it
         *      could not be created.
         */
        public Advertisement newInstance(Element root) {
            return new PlatformConfig(root);
        }
    }
}

