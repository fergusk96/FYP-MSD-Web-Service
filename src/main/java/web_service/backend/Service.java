package web_service.backend;

import static org.neo4j.helpers.collection.MapUtil.map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.neo4j.helpers.collection.Iterators;

import com.wrapper.spotify.SpotifyApi;

import web_service.executor.BoltCypherExecutor;
import web_service.executor.CypherExecutor;

public class Service {

	public static String accessToken = "";
	private final CypherExecutor cypher;
	private static final String USER_AGENT = "Mozilla/5.0";

	public Service(String uri) {
		cypher = createCypherExecutor(uri);
	}

	/* Create a driver that can query the database through java */

	private CypherExecutor createCypherExecutor(String uri) {
		try {
			String auth = new URL(uri.replace("bolt", "http")).getUserInfo();
			if (auth == null) {
				auth = "neo4j:qwerty";
				String[] parts = auth.split(":");

				return new BoltCypherExecutor(uri, parts[0], parts[1]);
			}
			return new BoltCypherExecutor(uri);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Invalid Neo4j-ServerURL " + uri);
		}
	}

	/*
	 * All of the following functions are mapped to in the Routes.java file
	 * arguments from these uris are used within the Cypher queries within each of
	 * the following methods. These functions return maps or lists of maps which are
	 * then converted to Json within the Routes.java file. In the interface itself,
	 * the methods of the form find* will be called when an object is clicked and
	 * the user wants to expand on the information displayed methods of the form
	 * *Search display a list of possible results based on the user's search. The
	 * user clicks one of the items in the search list and the find* method is used
	 * to find more detailed information about the clicked item
	 */

	public List<Iterable<Map<String, Object>>> findArtist(String name) {
		List<Iterable<Map<String, Object>>> artistList = new ArrayList<Iterable<Map<String, Object>>>();

		if (name == null)
			return Collections.emptyList();
		else {
			try {

				/*
				 * {name} is variable and corresponds to a name passed in the request URI
				 */

				artistList.add(Iterators.asCollection(cypher.query("MATCH (artist:ARTIST {name: {name}})\n"
						+ "OPTIONAL MATCH (artist)-[r:PERFORMS]->(s:SONG)\n"
						+ "OPTIONAL MATCH (s)-[RELEASED_ON]->(year:YEAR)\n"
						+ "RETURN artist.name as name, collect({title:s.title, year:year.year}) as songs limit 20",
						map("name", name))));
				artistList
						.add(Iterators.asCollection(cypher.query(
								"Match (artist:ARTIST{name:{name}})-[HAS_ALBUM]->(album:ALBUM) "
										+ "return artist.name, collect({album:album.name}) as albums",
								map("name", name))));
				artistList.add(
						Iterators.asCollection(cypher.query("Match (artist:ARTIST{name:{name}})-[r:HAS_TAG]->(tag:TAG) "
								+ "With artist, r, tag ORDER BY r.weight DESC "
								+ "return collect({Tag:tag.tag}) as Tags ", map("name", name))));

				return artistList;

			} catch (Exception e) {
				e.printStackTrace();
				return Collections.emptyList();
			}
		}

	}

	public Iterable<Map<String, Object>> findSimilarSongs(String title, String artistName) {
		if (title == null)
			return Collections.emptyList();

		try {
			return Iterators.asCollection(cypher.query(
					"MATCH (song:SONG{title:{title}})-[:SIMILAR_TO]->(simSong:SONG)"
					+ "MATCH (a:ARTIST{name:{artistName}})-[:PERFORMS]->(song)"
					+ "MATCH (simSong)<-[:PERFORMS]-(artist:ARTIST)															MATCH (simSong)-[:IN_ALBUM]->(album:ALBUM)\n"
							+ "return simSong.title, artist.name, album.name",
					map("title", title, "artistName",artistName)));

		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	public Iterable<Map<String, Object>> findSimilarArtists(String name) {
		if (name == null)
			return Collections.emptyList();

		try {
			return Iterators
					.asCollection(cypher.query(
							"MATCH " + "(artist:ARTIST{name:{name}})-[SIMILAR_TO]->(simArtist:ARTIST)  "
									+ "return artist.name, collect({artist:simArtist}) as similar_artists",
							map("name", name)));

		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	/*
	 * find all songs and the performing artist that are associated with each tag
	 * passed through the uri
	 */
	public Iterable<Map<String, Object>> findTag(String query) {
		try {
			String[] temp = query.split(":");
			List<String> myTags = new ArrayList<>(Arrays.asList(temp));

			if (query == null || query.trim().isEmpty()) {

				return Collections.emptyList();
			}

			return Iterators.asCollection(cypher.query("WITH {myTags} as tags\n"
					+ "MATCH (song:SONG)-[:HAS_TAG]->(tag:TAG)\n" + "WHERE tag.tag in tags\n"
					+ "WITH song, size(tags) as inputCnt, count(DISTINCT tag) as cnt\n" + "WHERE cnt = inputCnt\n"
					+ " OPTIONAL MATCH(song)<-[:PERFORMS]-(artist:ARTIST)" + "RETURN song,artist limit 100",
					map("myTags", myTags)));
		} 
			catch(Exception e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				String sStackTrace = sw.toString(); // stack trace as a string

			return (Iterable<Map<String, Object>>) map("error", sStackTrace);
		}
	}

	/*
	 * Some songs and albums may have the same name as other nodes of the same type,
	 * so to return just one song we must refer to both a song and artist name. In
	 * the next two methods we search for songs and albums using both their title
	 * and associated artist to guarantee uniqueness
	 */

	public List<Iterable<Map<String, Object>>> findSongwithartist(String title, String name) {
		
		if (title == null)
			return Collections.emptyList();

		else {

			List<Iterable<Map<String, Object>>> songList = new ArrayList<Iterable<Map<String, Object>>>();
			Iterable<Map<String, Object>> tagList = Iterators.asCollection(cypher.query(
					"MATCH(song:SONG{title:{title}})\n" + "MATCH (song)<-[PERFORMS]-(artist:ARTIST{name:{name}})"
							+ "OPTIONAL MATCH (song)-[l:HAS_TAG]->(k:TAG) \n" + "with song,l,k \n"
							+ "ORDER BY l.weight DESC limit 10\n" + "return collect({tag:k.tag}) as tags",
					map("title", title, "name", name)));

			Iterable<Map<String, Object>> detailsList = Iterators.asCollection(cypher.query(
					"MATCH (song:SONG{title:{title}}) " + "MATCH (song)<-[PERFORMS]-(artist:ARTIST{name:{name}})"
							+ "RETURN song.title as title, song.tempo as tempo,\n"
							+ "							song.beats_per_bar as beats_per_bar, song.duration as duration,\n"
							+ "							song.loudness as loudness, song.key as key, song.mode as mode",
					map("title", title, "name", name)));
			songList.add(detailsList);
			songList.add(tagList);
			return songList;

		}

	}

	/*
	 * This method retrieves the cover art and release year of an album where the
	 * user has already clicked on a row containing the album name and artist name.
	 * Cover art and album release year are not part of the MSD so this method uses
	 * queries to the Spotify API
	 */

	public List<Map<String, Object>> findAlbum(String albumName, String artistName) {

		List<Map<String, Object>> albumList = new ArrayList<Map<String, Object>>();
		if (albumName == null || artistName == null) {
			albumList.add(Collections.emptyMap());
		} else {
			Map<String, Object> imageList = new HashMap<String, Object>();
			Map<String, Object> songList = new HashMap<String, Object>();
			try {
				imageList = getAlbumSpotify(albumName, artistName);
				songList = Iterators.singleOrNull(cypher.query(
						"MATCH(album:ALBUM{name:{albumName}})<-[:HAS_ALBUM]-(artist:ARTIST{name:{artistName}})										"
						+ "MATCH(song)-[:IN_ALBUM]->(album)  "
						+ "return collect(song.title) as Songs,artist.name,album.name",
						map("artistName", artistName, "albumName", albumName)));
			} catch (Exception e) {
				songList = Collections.emptyMap();
				imageList = Collections.emptyMap();
				e.printStackTrace();
			}
			albumList.add(imageList);
			albumList.add(songList);
		}
		return albumList;
	}

	/*
	 * This method returns song analytics for a clicked row containing a song and
	 * artist name. The GUI features a way to download the data returned in this
	 * file as a csv file. This method covers getting various forms of analytic
	 * information by using the type of an analytics required as an argument
	 */

	public Iterable<Map<String, Object>> findSongDetailsWithArtist(String type, String title, String name) {

		return Iterators.asCollection(cypher.query(
				"MATCH (song:SONG{title:{title}}) " + "MATCH (song)<-[:PERFORMS]-(artist:ARTIST{name:{name}})"
						+ "MATCH (song)-[:HAS_" + type.toUpperCase() + "]->(sb:SONG_" + type.toUpperCase() + ")"
						+ "return sb." + type + "_start as " + type + "_start_point",
				map("title", title, "name", name)));
	}

	/*
	 * This function is similar to the above but this method covers pitches and
	 * timbres for each song segment. This information is also downloadable as a csv
	 * through the GUI
	 */

	public Iterable<Map<String, Object>> findSegmentDetailsWithArtist(String type, String title, String name) {

		return Iterators.asCollection(cypher.query(
				"MATCH (song:SONG{title:{title}}) " + "MATCH (song)<-[:PERFORMS]-(artist:ARTIST{name:{name}})"
						+ "MATCH (song)-[:HAS_SEGMENTS]->(sb:SONG_SEGMENTS)" + "MATCH (sb)-[HAS_" + type.toUpperCase()
						+ "]->(spt:SONG_SEGMENTS_" + type.toUpperCase() + ")" + "return spt.segments_timbre",
				map("title", title, "name", name)));
	}

	/*
	 * The following methods are called once a search box button is clicked on the
	 * GUI it uses partial matching to return a list of possible nodes from the
	 * graph database based on the use query The user then clicks on one of the
	 * displayed items to show more detailed information
	 */

	@SuppressWarnings("unchecked")
	public Iterable<Map<String, Object>> artistSearch(String query) {
		if (query == null || query.trim().isEmpty())
			return Collections.emptyList();
		return Iterators
				.asCollection(cypher.query("MATCH (artist:ARTIST)\n" + " WHERE lower(artist.name) CONTAINS {part}\n"
						+ " RETURN artist ORDER BY length(artist.name)", map("part", query.toLowerCase())));
	}

	public Iterable<Map<String, Object>> songSearch(String query) {

		if (query == null || query.trim().isEmpty())
			return Collections.emptyList();

		return Iterators.asCollection(cypher.query(
				"MATCH (song:SONG) WHERE lower(song.title) CONTAINS {part} "
						+ "OPTIONAL MATCH (artist:ARTIST)-[PERFORMS]->(song)	"
						+ "OPTIONAL MATCH (song)-[:IN_ALBUM]->(album:ALBUM)	"
						+ "OPTIONAL MATCH (song)-[:RELEASED_ON]->(year:YEAR)"
						+ "RETURN song.title,artist.name,album.name,year.year " + "ORDER BY length(song.title)",
				map("part", query.toLowerCase())));
	}

	public Iterable<Map<String, Object>> albumSearch(String query) {

		if (query == null || query.trim().isEmpty())
			return Collections.emptyList();

		return Iterators.asCollection(cypher.query("MATCH (album:ALBUM)<-[r:HAS_ALBUM]-(artist:ARTIST) "
				+ "WHERE lower(album.name) CONTAINS {part}" + "RETURN album, artist.name ORDER BY length(album.name)",
				map("part", query.toLowerCase())));
	}

	/*
	 * The following three methods allow us to return album cover art and release
	 * year through Spotify queries. We can use similar steps to implement many
	 * other queries to this powerful API and add to the power of the MSD
	 */

	public Map<String, Object> getAlbumSpotify(String album, String artist) throws IOException {
		setSpotifyAccessToken(); // If token is valid, return token. Otherwise request new access token.
		String Get_URL = getSpotifySearchQuery(album, artist); // Encode search query
		URL obj = new URL(Get_URL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("Authorization", "Bearer " + accessToken); // Place access tokens generated inside
																			// request header

		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) { // Success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			/*
			 * Parse the returned JSON to obtain album release date and cover art
			 */

			JSONParser parser = new JSONParser();
			Object myObj;
			try {
				myObj = parser.parse(response.toString());
				JSONArray array = new JSONArray();
				array.add(myObj);
				JSONObject curObj = (JSONObject) array.get(0);
				JSONObject albumJson = (JSONObject) curObj.get("albums");
				JSONArray itemsJson = (JSONArray) albumJson.get("items");
				JSONObject imagesJson = (JSONObject) itemsJson.get(0);
				JSONArray imagesArrJson = (JSONArray) imagesJson.get("images");
				imagesArrJson.add(imagesJson.get("release_date"));

				return map("response", imagesArrJson);
			} catch (Exception e) {
				return map("response", e.getStackTrace().toString());
			}

		} else {
			return map("response", "bad connection");
		}

	}

	public static void setSpotifyAccessToken() {

		accessToken = AuthorizationCodeRefresh.authorizationCodeRefresh_Sync(); // Set access token variable

	}

	/*
	 * Method for encoding appropriate search string to return album information
	 * given album name and artist name
	 */
	public static String getSpotifySearchQuery(String album, String artist) {

		artist = artist.replaceAll(" ", "+");
		album = album.replaceAll(" ", "+");
		return "https://api.spotify.com/v1/search?q=album%3A" + album + "+artist%3A" + artist
				+ "&type=album&market=SE&limit=10";

	}

	/*
	 * This method is also adapted from the getting started tutorial at
	 * https://github.com/neo4j-examples/neo4j-movies-java-bolt It creates a
	 * background of clickable nodes for the GUI. This is both visually appealing
	 * and fun to play around with while remaining true to the graph database theme
	 */

	public Map<String, Object> graph(int limit) {
		limit = 20;
		Iterator<Map<String, Object>> result = cypher.query(
				"MATCH (song:SONG)<-[:SIMILAR_TO]-(simSong:SONG) "
						+ " RETURN song.title as song, collect(simSong.title) as simSongs" + " LIMIT {limit}",
				map("limit", limit));
		List nodes = new ArrayList();
		List rels = new ArrayList();
		int i = 0;
		while (result.hasNext()) {
			Map<String, Object> row = result.next();
			nodes.add(map("title", row.get("song"), "label", "song"));
			int target = i;
			i++;
			for (Object name : (Collection) row.get("simSongs")) {
				Map<String, Object> song = map("title", name, "label", "song");
				int source = nodes.indexOf(song);
				if (source == -1) {
					nodes.add(song);
					source = i++;
				}
				rels.add(map("source", source, "target", target));
			}
		}
		return map("nodes", nodes, "links", rels);
	}

}
