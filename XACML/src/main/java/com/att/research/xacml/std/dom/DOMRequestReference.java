/*
 *                        AT&T - PROPRIETARY
 *          THIS FILE CONTAINS PROPRIETARY INFORMATION OF
 *        AT&T AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN
 *             ACCORDANCE WITH APPLICABLE AGREEMENTS.
 *
 *          Copyright (c) 2013 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package com.att.research.xacml.std.dom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.att.research.xacml.api.RequestReference;
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.std.StdMutableRequestReference;
import com.att.research.xacml.std.StdRequestReference;

/**
 * DOMRequestReference extends {@link com.att.research.xacml.std.StdMutableRequestReference} with methods for creation from
 * DOM {@link org.w3c.dom.Node}s.
 * 
 * @author car
 * @version $Revision: 1.2 $
 */
public class DOMRequestReference {
	private static final Log logger	= LogFactory.getLog(DOMRequestReference.class);
	
	protected DOMRequestReference() {
	}

	/**
	 * Creates a new <code>DOMRequestReference</code> by parsing the given <code>Node</code> representing a XACML RequestReference element.
	 * 
	 * @param nodeRequestReference the <code>Node</code> representing the XACML RequestReference element
	 * @return a new <code>DOMRequestReference</code> parsed from the given <code>Node</code>
	 * @throws DOMStructureException if the conversion cannot be made
	 */
	public static RequestReference newInstance(Node nodeRequestReference) throws DOMStructureException {
		Element	elementRequestReference			= DOMUtil.getElement(nodeRequestReference);
		boolean bLenient						= DOMProperties.isLenient();
		
		StdMutableRequestReference stdRequestReference	= new StdMutableRequestReference();
		
		NodeList children	= elementRequestReference.getChildNodes();
		int numChildren;
		boolean sawAttributesReference	= false;
		if (children != null && (numChildren = children.getLength()) > 0) {
			for (int i = 0 ; i < numChildren ; i++) {
				Node child	= children.item(i);
				if (DOMUtil.isElement(child)) {
					if (DOMUtil.isInNamespace(child, XACML3.XMLNS) && XACML3.ELEMENT_ATTRIBUTESREFERENCE.equals(child.getLocalName())) {
						stdRequestReference.add(DOMRequestAttributesReference.newInstance(child));	
						sawAttributesReference	= true;
					} else {
						if (!bLenient) {
							throw DOMUtil.newUnexpectedElementException(child, nodeRequestReference);
						}
					}
				}
			}
		}
		if (!sawAttributesReference && !bLenient) {
			throw DOMUtil.newMissingElementException(nodeRequestReference, XACML3.XMLNS, XACML3.ELEMENT_ATTRIBUTESREFERENCE);
		}
		return new StdRequestReference(stdRequestReference);
	}
	
	public static boolean repair(Node nodeRequestReference) throws DOMStructureException {
		Element	elementRequestReference			= DOMUtil.getElement(nodeRequestReference);
		boolean result							= false;
		
		NodeList children	= elementRequestReference.getChildNodes();
		int numChildren;
		boolean sawAttributesReference	= false;
		if (children != null && (numChildren = children.getLength()) > 0) {
			for (int i = 0 ; i < numChildren ; i++) {
				Node child	= children.item(i);
				if (DOMUtil.isElement(child)) {
					if (DOMUtil.isInNamespace(child, XACML3.XMLNS) && XACML3.ELEMENT_ATTRIBUTESREFERENCE.equals(child.getLocalName())) {
						result					= DOMRequestAttributesReference.repair(child) || result;
						sawAttributesReference	= true;
					} else {
						logger.warn("Unexpected element " + child.getNodeName());
						elementRequestReference.removeChild(child);
						result	= true;
					}
				}
			}
		}
		if (!sawAttributesReference) {
			throw DOMUtil.newMissingAttributeException(nodeRequestReference, XACML3.XMLNS, XACML3.ELEMENT_ATTRIBUTESREFERENCE);
		}
		
		return result;
	}
}
