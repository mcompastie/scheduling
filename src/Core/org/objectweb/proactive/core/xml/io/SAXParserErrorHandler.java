/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.core.xml.io;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


public class SAXParserErrorHandler extends DefaultHandler {
    static Logger logger = ProActiveLogger.getLogger(Loggers.XML);

    public SAXParserErrorHandler() {
    }

    @Override
    public void warning(SAXParseException ex) throws SAXException {
        logger.warn("WARNING: " + ex.getMessage());
    }

    @Override
    public void error(SAXParseException ex) throws SAXException {
        logger.error("ERROR: " + ex.getSystemId() + " Line:" +
            ex.getLineNumber() + " Message:" + ex.getMessage());
    }

    @Override
    public void fatalError(SAXParseException ex) throws SAXException {
        logger.fatal("FATAL ERROR: " + ex.getMessage());
    }
}
