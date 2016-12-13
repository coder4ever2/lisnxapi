


import grails.transaction.Transactional
import groovy.lang.MetaClass;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.InitializingBean;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo
import com.flickr4java.flickr.photos.PhotoList
import com.flickr4java.flickr.photos.PhotosInterface
import com.flickr4java.flickr.photos.SearchParameters



@Transactional
class FlickrService implements InitializingBean, GroovyInterceptable {


    def static transactional = false
    def grailsApplication
    def setting

	// use your own API key
	public static final String flickrApiKey = "33334f44d8930d17a60ea9bac5a20ba2";
	public static final String flickrSharedSecret = "789ce72935c2d550";
	static String flickrServer = "www.flickr.com";

	
	
	def searchImages(){
		log.info("In searchImages method of FlickrService")
		REST rest;
		try {
			rest = new REST();
			rest.setHost(flickrServer);

			Flickr flickr = new Flickr(flickrApiKey,flickrSharedSecret, rest);
			Flickr.debugStream = false;

			SearchParameters searchParams = new SearchParameters();
			searchParams.setSort(SearchParameters.INTERESTINGNESS_DESC);

			// enter search keywords
			def tags = {"qr code"};
			searchParams.setTags(tags);
			searchParams.setText("happy birthday");

			PhotosInterface photosInterface = flickr.getPhotosInterface();
			PhotoList photoList;
			// change the number of results per page
			final int perPage = 20;

			// this is just to find out the total results and pages
			photoList = photosInterface.search(searchParams, perPage, 0);
			int totalPages = photoList.getPages();
			int totalResults = photoList.getTotal();
			photoList.clear();

			for (int x = 0; x < totalPages; x++) {
				if (photoList != null){
					// retrieve each page
					photoList = photosInterface.search(searchParams, perPage, x);
					if (photoList != null) {
						for (int y = 0; y < photoList.size(); y++) {
							try {
								Photo photo = (Photo)photoList.get(y);
								log.info((x * perPage) + y + "/" + totalResults + " url: " + photo.getUrl());
								
							} catch (Exception e) {
								log.error("Exception in FlickrService:", e)
							}
						}
					}
				}
			}

		} catch (Exception e) {
			log.error("Exception in FlickrService:", e)
		}
		
	}

	@Override
	public MetaClass getMetaClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getProperty(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object invokeMethod(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMetaClass(MetaClass arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setProperty(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		 this.setting = grailsApplication.config.setting
		
	}
}