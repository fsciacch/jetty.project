//
//  ========================================================================
//  Copyright (c) 1995-2016 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//


package org.eclipse.jetty.server.session.remote;

import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.session.AbstractInvalidationSessionTest;
import org.eclipse.jetty.server.session.AbstractTestServer;
import org.eclipse.jetty.server.session.InfinispanTestSessionServer;
import org.eclipse.jetty.session.infinispan.InfinispanSessionManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * InvalidationSessionTest
 *
 *
 */
public class RemoteInvalidationSessionTest extends AbstractInvalidationSessionTest
{

    public static RemoteInfinispanTestSupport __testSupport;
    public static final int IDLE_PASSIVATE_SEC = 3;
   
    
    
    @BeforeClass
    public static void setup () throws Exception
    {
      __testSupport = new RemoteInfinispanTestSupport("remote-session-test");
      __testSupport.setup();
    }
    
    @AfterClass
    public static void teardown () throws Exception
    {
       __testSupport.teardown();
    }
    
    /** 
     * @see org.eclipse.jetty.server.session.AbstractInvalidationSessionTest#createServer(int)
     */
    @Override
    public AbstractTestServer createServer(int port, int maxInterval, int inspectInterval)
    {
        InfinispanTestSessionServer server =  new InfinispanTestSessionServer(port, maxInterval, inspectInterval, __testSupport.getCache())
        {

            /** 
             * @see org.eclipse.jetty.server.session.InfinispanTestSessionServer#newSessionManager()
             */
            @Override
            public SessionManager newSessionManager()
            {
               InfinispanSessionManager mgr = (InfinispanSessionManager)super.newSessionManager();
               mgr.getSessionStore().setIdlePassivationTimeoutSec(IDLE_PASSIVATE_SEC);
               return mgr;
            }

        };
        return server;
    }

    
    
    @Ignore
    @Override
    public void testInvalidation() throws Exception
    {
        //Ignore 
        //super.testInvalidation();
    }

    /** 
     * @see org.eclipse.jetty.server.session.AbstractInvalidationSessionTest#pause()
     */
    @Override
    public void pause()
    {
        //This test moves a session from node 1 to node 2, then invalidates the session back on node1. This
        //should never happen with a decent load balancer.
        //The infinispan session manager on node 2 will hold the session in local memory for a specific (configurable)
        //amount of time. We've set the stale session time to 3 sec, so we need to pause for at least this long before making
        //another request to node2 so 
        //that the node will re-load the session from the database and discover that it has gone.
        try
        {
            Thread.sleep(2 * IDLE_PASSIVATE_SEC * 1000);
        }
        catch (InterruptedException e)
        {
        }
    }

}
