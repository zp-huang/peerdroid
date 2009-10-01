/*
 *  Copyright (c) 2001 Sun Microsystems, Inc.  All rights
 *  reserved.
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
 *  $Id: ResolverSrdiMsg.java,v 1.1 2005/05/03 06:43:35 hamada Exp $
 */
package net.jxta.protocol;

import net.jxta.document.Document;
import net.jxta.document.MimeMediaType;
import net.jxta.credential.Credential;
/**
 * ResolverSrdiMsg is generic resolver wrapper for Services that wish to 
 * implement their own distribution of indices. this message provides the 
 * scoping within the group, and service. In addition it also defines a credential
 * which should be verified by the service.
 */

public abstract class ResolverSrdiMsg {

	private String handlername = null;
	private Credential credential = null;
	private String payload = null;


	/**
	 * All messages have a type (in xml this is !doctype)
	 * which identifies the message
	 *
	 * @return    String type of the advertisement
	 */

	public static String getMessageType() {
		return "jxta:ResolverSRDI";
	}


	/**
	 * returns the handlername
	 *
	 * @return    String handlername name
	 *
	 */

	public String getHandlerName() {
		return handlername;
	}


	/**
	 * returns the credential
	 *
	 * @return    StructuredDocument credential
	 */

	public Credential getCredential() {
		return credential;
	}


	/**
	 * returns the payload
	 *
	 * @return    String value of query
	 */

	public String getPayload() {
		return payload;
	}


	/**
	 * set the handlername
	 *
	 * @param  name  string handlername
	 */

	public void setHandlerName(String name) {
		this.handlername = name;
	}


	/**
	 * set the credential object
	 *
	 * @param  cred  credential
	 */

	public void setCredential(Credential cred) {
		this.credential = cred;
	}


	/**
	 * set the SRDI payload
	 *
	 * @param  payload   The new payload value
	 */

	public void setPayload(String payload) {
		this.payload = payload;
	}


	/**
	 * Write advertisement into a document. asMimeType is a mime media-type
	 * specification and provides the form of the document which is being
	 * requested. Two standard document forms are defined. "text/text" encodes
	 * the document in a form nice for printing out and "text/xml" which
	 * provides an XML format.
	 *
	 * @param  asMimeType  mime-type representation requested for that document
	 * @return             Document document representing the advertisement
	 */

	public abstract Document getDocument(MimeMediaType asMimeType);

}
