package web_service.backend;


import static spark.Spark.get;

import java.net.URLDecoder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import spark.servlet.SparkApplication;

public class Routes implements SparkApplication {

    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private Service service;

    public Routes(Service service) {
        this.service = service;
    }

    @SuppressWarnings("deprecation")
	public void init() {
    	CorsFilter corsfilter = new CorsFilter();
    	corsfilter.apply(); // All cross origin resource sharing for each of these routes
        get("/simArtists/:name", (req, res) -> gson.toJson(service.findSimilarArtists(URLDecoder.decode(req.params("name")))));
        get("/simSongs/:title/:artist", (req, res) -> gson.toJson(service.findSimilarSongs(URLDecoder.decode(req.params("title")),URLDecoder.decode(req.params("artist")))));
        get("/tag/:tags", (req, res) -> gson.toJson(service.findTag(URLDecoder.decode(req.params("tags")))));
        get("/song/artist/:title/:name", (req, res) -> gson.toJson(service.findSongwithartist(req.params("title"),req.params("name"))));
        get("/songdetails/:type/:title/:name", (req, res) -> gson.toJson(service.findSongDetailsWithArtist(req.params("type"),req.params("title"),req.params("name"))));
        get("/segmentdetails/:type/:title/:name", (req, res) -> gson.toJson(service.findSegmentDetailsWithArtist(req.params("type"),req.params("title"),req.params("name"))));
        get("/spotify/:album/:artist", (req,res) -> gson.toJson(service.getAlbumSpotify(req.params("album"),req.params("artist"))));
        get("/album/artist/:albumName/:artistName", (req,res) -> gson.toJson(service.findAlbum(URLDecoder.decode(req.params("albumName")),URLDecoder.decode(req.params("artistName")))));
        get("/artist/:name", (req, res) -> gson.toJson(service.findArtist(URLDecoder.decode(req.params("name")))));
        get("/songSearch", (req, res) -> gson.toJson(service.songSearch(req.queryParams("q"))));
        get("/artistSearch", (req, res) -> gson.toJson(service.artistSearch(req.queryParams("q"))));
        get("/albumSearch", (req, res) -> gson.toJson(service.albumSearch(req.queryParams("q"))));
        get("/graph", (req, res) -> {
            int limit = req.queryParams("limit") != null ? Integer.valueOf(req.queryParams("limit")) : 100;
            return gson.toJson(service.graph(limit));
        });
    }
}
