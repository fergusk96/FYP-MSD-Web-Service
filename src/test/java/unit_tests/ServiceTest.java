package unit_tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import web_service.backend.Service;
import web_service.util.Util;

public class ServiceTest {

    @Test
    public void findSongGivenArtistAndTitle() throws Exception {
    	Service service = new Service(Util.getNeo4jUrl());
        List<Iterable<Map<String, Object>>> song = service.findSongwithartist("poker face", "lady gaga");
        assertEquals("poker face", song.get(0).iterator().next().get("title"));
        
    }
    

}
