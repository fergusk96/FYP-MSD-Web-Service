package web_service.executor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.AuthToken;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Value;

public class BoltCypherExecutor implements CypherExecutor {

    private final org.neo4j.driver.v1.Driver driver;

    /*The methods implemented below are necessary
     * in order to make Cypher queries to a Neo4j database
     * in Java. Notice how the username is set as neo4j and
     * password qwerty. The neo4j Java wrapper allows us to build a neo4j driver
     * using these credentials and a server URI.
     * Much of this code is adapted from the getting started tutorial at 
     * https://github.com/neo4j-examples/neo4j-movies-java-bolt
     * */
    
    public BoltCypherExecutor(String url) {
        this(url, null, null);
    }

    public BoltCypherExecutor(String url, String username, String password) {

    	boolean hasPassword = password != null && !password.isEmpty();
        AuthToken token = hasPassword ? AuthTokens.basic("neo4j", "qwerty") : AuthTokens.none();
        driver = GraphDatabase.driver("bolt://ec2-34-241-97-172.eu-west-1.compute.amazonaws.com:7687", token, Config.build().withEncryptionLevel(Config.EncryptionLevel.NONE).toConfig());
        
      

    }

    /*The following two methods are necessary to execute a cypher query with 
     * a list of parameters implemented as a map.
     */
    @Override
    public Iterator<Map<String, Object>> query(String query, Map<String, Object> params) {
        try (Session session = driver.session()) {
            List<Map<String, Object>> list = session.run(query, params)
                    .list( r -> r.asMap(BoltCypherExecutor::convert));
            return list.iterator();
        }
    }

    static Object convert(Value value) {
        switch (value.type().name()) {
            case "PATH":
                return value.asList(BoltCypherExecutor::convert);
            case "NODE":
            case "RELATIONSHIP":
                return value.asMap();
        }
        return value.asObject();
    }

}