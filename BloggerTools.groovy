#!/usr/bin
import com.google.api.client.*;
import com.google.api.*;
//import com.google.api.client.blogger.*;
//import sample.util.SimpleCommandLineParser;

//import com.google.api.client.http.HttpTransport;
//import com.google.api.services.blogger.Blogger;

//import com.google.gdata.data.Entry;
//import com.google.gdata.data.Feed;
//import com.google.gdata.data.Person;
//import com.google.gdata.data.PlainTextConstruct;
//import com.google.gdata.data.TextContent;
import com.google.api.util.*;


import java.io.IOException;
import java.net.URL;

def hello(){
    //BloggerService myService = new BloggerService("disapprovingbun");

    // The BlogId for the Blogger Buzz blog
    String BUZZ_BLOG_ID = "2399953";

// Configure the Java API Client for Installed Native App
//    HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
//    JsonFactory JSON_FACTORY = new JacksonFactory();

// Configure the Installed App OAuth2 flow.
//    Credential credential = OAuth2Native.authorize(HTTP_TRANSPORT,
//            JSON_FACTORY, new LocalServerReceiver(),
//            Arrays.asList(BloggerScopes.BLOGGER));

// Construct the Blogger API access facade object.
    ///home/tomekpilot/work/web/BloggerPostBuilder
    /*this.class.classLoader.rootLoader.addURL(new URL("file:google-api-services-blogger-v3-rev48-1.21.0.jar"));
    this.class.classLoader.rootLoader.addURL( new URL("file:google-api-client-1.21.0.jar"));
    this.class.classLoader.rootLoader.addURL( new URL("file:google-http-client-1.21.0.jar"));

    //def AbstractGoogleJsonClient = Class.forName("com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient").newInstance();
    def Blogger = Class.forName("com.google.api.services.blogger.Blogger").newInstance();
    def ObjectParser = Class.forName("com.google.api.client.util.ObjectParser").newInstance();
    print Blogger
   // Class Blogger = new GroovyClassLoader(getClass().getClassLoader()).parseClass(sourceFile);
   // GroovyObject myBlogger = (GroovyObject) groovyClass.newInstance();

    //Blogger blogger = Blogger.builder(HTTP_TRANSPORT, JSON_FACTORY).setApplicationName("Blogger-PostsList-Snippet/1.0").setHttpRequestInitializer(credential).build();
*/
    println('hello')
}