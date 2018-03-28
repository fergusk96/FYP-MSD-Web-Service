package web_service.executor;

import java.util.Iterator;
import java.util.Map;


/*An interface class that runs Cypher queries over the
 * by passing a list of parameters. See Service.java for examples
 * */ 

public interface CypherExecutor {
    Iterator<Map<String,Object>> query(String statement, Map<String,Object> params); 
}
