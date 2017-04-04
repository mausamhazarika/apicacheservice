package mausam.projects.githubcache.endpoints.tests;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.grizzly.http.server.HttpServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import mausam.projects.githubcache.Main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.StringReader;

public class OrgResourceTest {

    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() throws Exception {
        // start the server
        server = Main.startServer(new String[]{"7000","100"});
        // create the client
        Client c = ClientBuilder.newClient();
        target = c.target(Main.BASE_URI);
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    /**
     * Test to see that the message "Got it!" is sent in the response.
     */
    @Test
    public void testGetIt() {
        String responseMsg = target.path("/orgs/Netflix").request().get(String.class);
        assertNotNull(responseMsg);
        JsonReader jsonReader =  Json.createReader(new StringReader(responseMsg));
        JsonObject orgObj = jsonReader.readObject();
        assertEquals("Netflix, Inc.", orgObj.getString("name"));
    }
}
