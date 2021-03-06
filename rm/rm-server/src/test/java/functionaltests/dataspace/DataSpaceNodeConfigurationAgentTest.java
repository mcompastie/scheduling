/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 */
package functionaltests.dataspace;

import java.util.concurrent.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.varia.NullAppender;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.objectweb.proactive.api.PAActiveObject;
import org.ow2.proactive.resourcemanager.nodesource.dataspace.DataSpaceNodeConfigurationAgent;


/**
 * Tests locking mechanism on DataSpaceNodeConfigurationAgent.
 * <p>
 * This test submits a main thread which holds the reading cache lock for a long time
 * And multiple small threads which hold the reading cache lock for a small time.
 * <p>
 * When the main thread terminates, all small threads should as well be terminated, otherwise,
 * the cache cleaning mechanism did prevent their execution
 */
public class DataSpaceNodeConfigurationAgentTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private ExecutorService executor;

    final int NB_TASKS = 31;

    @BeforeClass
    public static void configureLog4J() {
        BasicConfigurator.configure(new NullAppender());
    }

    @Before
    public void before() {
        executor = Executors.newFixedThreadPool(NB_TASKS);
    }

    @After
    public void after() {
        executor.shutdownNow();
    }

    @Test
    public void testCacheLockingConcurrency() throws Exception {

        // set a very small cache cleaning period (100ms)
        System.setProperty(DataSpaceNodeConfigurationAgent.NODE_DATASPACE_CACHE_CLEANING_PERIOD, "100");
        DataSpaceNodeConfigurationAgent nodeConfigurationAgent = (DataSpaceNodeConfigurationAgent) PAActiveObject.newActive(DataSpaceNodeConfigurationAgent.class.getName(),
                                                                                                                            null);
        boolean result = nodeConfigurationAgent.configureNode();
        PAActiveObject.terminateActiveObject(nodeConfigurationAgent, false);

        // submit a main thread which will hold the cache read lock for a long time (40s)
        Future future = executor.submit(new Runnable() {
            @Override
            public void run() {
                lockAndWait(20000);
            }
        });

        Thread.sleep(2000);

        Future[] futures = new Future[NB_TASKS - 1];

        // submit multiple threads which will hold the lock each for one second

        for (int i = 0; i < NB_TASKS - 1; i++) {

            futures[i] = executor.submit(new Runnable() {
                @Override
                public void run() {
                    lockAndWait(500);
                }
            });
        }

        // wait for the main thread to finish
        future.get();

        for (int i = 0; i < NB_TASKS - 1; i++) {
            // ensure all small threads are terminated at this time (they were not blocked by the cache cleaning thread)
            Assert.assertTrue(futures[i].isDone());
        }

    }

    private void lockAndWait(long sleepPeriod) {
        try {
            DataSpaceNodeConfigurationAgent.lockCacheSpaceCleaning();
            Thread.sleep(sleepPeriod);
            DataSpaceNodeConfigurationAgent.unlockCacheSpaceCleaning();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
