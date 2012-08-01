/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2012 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */

package org.ow2.proactive_grid_cloud_portal.cli.cmd.rm;

import static org.ow2.proactive_grid_cloud_portal.cli.HttpResponseStatus.OK;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.ow2.proactive.utils.ObjectArrayFormatter;
import org.ow2.proactive_grid_cloud_portal.cli.CLIException;
import org.ow2.proactive_grid_cloud_portal.cli.cmd.AbstractCommand;
import org.ow2.proactive_grid_cloud_portal.cli.cmd.Command;
import org.ow2.proactive_grid_cloud_portal.cli.json.TopologyView;
import org.ow2.proactive_grid_cloud_portal.cli.utils.StringUtility;

public class GetTopologyCommand extends AbstractCommand implements Command {

    @Override
    public void execute() throws CLIException {

        HttpGet request = new HttpGet(resourceUrl("topology"));
        HttpResponse response = execute(request);
        if (statusCode(OK) == statusCode(response)) {
            TopologyView topology = readValue(response, TopologyView.class);
            Set<String> hostList = topology.getDistances().keySet();
            writeLine("%nHost list(%d):", hostList.size());
            for (String host : hostList) {
                writeLine("%s", host);
            }

            writeLine("%n%s:", "Host  view");
            ObjectArrayFormatter formatter = new ObjectArrayFormatter();
            formatter.setMaxColumnLength(80);
            formatter.setSpace(4);

            List<String> titles = new ArrayList<String>();
            titles.add("Host");
            titles.add("Distance (µs)");
            titles.add("Host");
            formatter.setTitle(titles);
            formatter.addEmptyLine();

            List<String> line;
            for (String host : hostList) {
                Map<String, String> hostTopology = topology.getDistances().get(
                        host);
                if (hostTopology != null) {
                    for (String anotherHost : hostTopology.keySet()) {
                        line = new ArrayList<String>();
                        line.add(host);
                        line.add(hostTopology.get(anotherHost));
                        line.add(anotherHost);
                        formatter.addLine(line);
                    }
                }
            }
            writeLine("%s", StringUtility.string(formatter));
        } else {
            handleError("An error occurred while retrieving the topology:",
                    response);
        }

    }

}
