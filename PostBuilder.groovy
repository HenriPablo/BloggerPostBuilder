#! /usr/bin/env groovy

import sun.font.TrueTypeFont;
/* blogger API jar */
import com.google.gdata.client.*;
import com.google.gdata.data.*;
import com.google.gdata.util.*;
import java.io.IOException;
import java.net.URL;
/* end blogger API jar */

println "\nWelcome to Groovy Blogger Post Builder\n";

// get path to directory we're in
basePath = new File(".").getCanonicalPath();
//println "Canonoical Path: ${basePath}"

absPath = new File("./").getAbsolutePath();
//println "Absolute Path: ${absPath}"

/* 
 * the following lifted from: 
 * 		http://stackoverflow.com/questions/1163093/how-do-you-get-the-path-of-the-running-script-in-groovy
 * 
 */
scriptDir = new File(getClass().protectionDomain.codeSource.location.path).parent
scriptFile = getClass().protectionDomain.codeSource.location.path

def title = "";
def commonFileName = "";

// use date as part of default post name
date = new Date();

/* 	
 * default config values 
 * check existence of default configuration file 
 * 
 */

def defaultConfig  = new ConfigSlurper().parse( new File( scriptDir + '/default.config').toURI().toURL() );

def customConfig;

/* check for existence of custom config based on title of post */
def customConfigCheck( String configFileName, groovy.util.ConfigObject defaultConfig ){

	def customConfigFile = new File( basePath + "/" + configFileName + ".config");

	if( customConfigFile.exists() && customConfigFile.length() > 0 ){
		customConfig = new ConfigSlurper().parse( new File( configFileName + '.config' ).toURI().toURL() );
	} else {
		new File( configFileName + '.config').withWriter{
			writer ->
				defaultConfig.writeTo(writer);
		}
		customConfig = new ConfigSlurper().parse( new File( configFileName + '.config' ).toURI().toURL() );
	}
	return customConfig;
}/* end customConfigCheck */


/* START parsing command line args */
def strArgs = [];
def s = this.args

s.split(){
	strArgs << it;
}

if( strArgs.size == 0 ){
	showHelp()
	}
	else
	{
		strArgs.each { param ->
			print( param + "\n")
			if( param == "help"){
				showHelp();
			}

			def p = param.split(":");
			// replace spaces with hyphens in title
			if( p[0] == "title")
			{
				commonFileName = p[1].toLowerCase().replaceAll( " ", "-");
				customConfig = customConfigCheck( commonFileName, defaultConfig );
				customConfig.config.title = p[1];
				customConfig.config.commonFileName = commonFileName;
			}
		}/* end param loop */
		println "commonFileName:"
		println commonFileName

		println "\nCUSTOM CONFIG:";
		print customConfig;

		println "\nDEFAULT CONFIG:";
		print defaultConfig;
	}
this.getClass().classLoader.rootLoader.addURL(new File("/home/tomekpilot/work/web/BloggerPostBuilder/file.jar").toURL())
/* TEMP DEBUG EXIT */
//System.exit(0);

//println defaultConfig;

/* END parsing command */




/* DEFAULT CSS, JS file */
def defaultCss = new File( scriptDir + "/default.css");
def defaultJs = new File( scriptDir + "/default.js");


if( !defaultCss.exists()){
	new File( scriptDir + "/default.css").write("");
}

if( !defaultJs.exists()){
	new File( scriptDir + "/default.js").write("");
}


/* CUSTOM - HTML CONTENTS */
def customHtml = new File( basePath + "/" + commonFileName + ".html");
println "customHtml.exists():"
println customHtml.exists()


/* if it didn't exist before, read in the one just created */
if( !customHtml.exists()){
	customHtml = new File( basePath + "/" + commonFileName + ".html");
}


/* CUSTOM - CSS CONTENTS */
def customCss = new File( basePath + "/" + commonFileName + ".css");
if( !customCss.exists()){
	 new File( basePath + "/" + commonFileName + ".css").write("");
	 println "Custom CSS has ${customCss.length()} bytes";
}
/* CUSTOM - JS CONTENTS */
def customJs = new File( basePath + "/" + commonFileName + ".js");
if( !customJs.exists()){
	 new File( basePath + "/" + commonFileName + ".js").write("");
}
/* 
 * THE HEART OF THE POST 
 * 	this holds DEV or PRODUCTION post contents of HTML, CSS and JS
 */
String outputString = "";

/* CSS */

if( defaultCss.exists() && !customCss ){
	outputString += blockTxt( defaultCss, 'css', customConfig );
} else if( customCss.exists() ){
	outputString += blockTxt( customCss, 'css', customConfig );
	println "${customCss.length()} bytes written to Custom CSS";
}

/* JAVASCRIPT */

if( customConfig.config.dev == true && customConfig.config.extJS == true ){ /* include jQuery from CDN */
	/*
		TODO: move this into config or at least allow for version to be arg based,
	use some default version is not provided
	*/
    outputString += '<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js" type="text/javascript"></script>';
    outputString += '\n';
}

if( defaultJs.exists() && !customJs.exists() ){
    outputString += blockTxt(defaultJs, 'js', customConfig );
} else if( customJs.exists() ){
	outputString += blockTxt(customJs, 'js', customConfig )
}


/* HTML */
if( customHtml.exists() ){
    outputString += blockTxt( customHtml, 'html', customConfig );
}

/* do we need to create the whole file setup?*/
if( customConfig.config.create == "all" ){
	println "\n trying to call createFiles() \n";
	println "title : " + commonFileName + "\n";
	this.createFiles( commonFileName );
}


new File( basePath + "/" + commonFileName + "-composit.html").write( outputString.trim() );
this.updatePost( outputString.trim() );


// open composit file in default browser
// credit goes to: http://www.centerkey.com/java/browser/
if( customConfig.config.preview == true){
	String url = "file://${basePath}/${ commonFileName }-composit.html";
	url = new URL(url).text;
	//java.awt.Desktop.getDesktop().browse(java.net.URI.create( url ));
	java.awt.Desktop.desktop.browse url.toURI()
}

/**
 * Create string form of a given content file, be it JavaScript, Style Sheet, HTML or a text file
 * @parameter file: file to be gotten it's text contents
 * @parameter fileType: string marking how to output the given text from file, for instance if we pass it 'js'
 *     output will contain opening and closing <script> tags, for Style Sheet we'll have opening and closing <style> tag
 */

def blockTxt( File file, String fileType, customConfig ){
	
	def dev = customConfig.config.dev	
    def result = ""

    switch( fileType ){
		
		case 'css' :
			if( dev ){
				result += "<html><head><title>" +  customConfig.config.title + "</title>\n"
			}
			result += '<style type="text/css">\n'
			result += file.getText();
			result += '\n</style>\n'
			break
		
        case 'js' :
            result += '<script type="text/javascript">\n'
            result += file.getText();
            result += '\n</script>\n'
			if(dev){
				result += "</head><body>"
			}
            break


        case 'html' :
			result += ('\n' + file.getText())
			if( dev ){
				result += "</body></html>"
			}
            
        break

        case 'txt' :
            result += file.getText()
        break

        default :
            println 'Could not create string contents of file'
        break
    }
	


    return '\n' + result + '\n'
}

def createFiles( String fileName ){

	println "create() called!"
	
	basePath = new File(".").getCanonicalPath()
	//println basePath
	
	new File( basePath + "/" + fileName + ".html").write("");
	new File( basePath + "/" + fileName +  ".js").write("");
	new File( basePath + "/" + fileName +  ".css").write("");
}

def showHelp(){
	println "\nPost Builder Help:\n";
	println "\tReguired Argument: 'title'\n";
	println "\tFirst time use example: \n\t\"PostBuilder.groovy title:'Your Post Title'\" \n";
	println "\t\tNotes:\n\t\tIt is not necessary to use 'goovy' to run the script if:";
	println "\t\t\tLocation of directory where sript is residing is added to the PATH system variable\n";
	println "See 'blogger-post-builder-read-me.txt' for more information.";
	System.exit(0);
}

def updatePost( String postBody )
	{

		def json = groovy.json.JsonOutput.toJson( postBody  )
		print json;

//		def req = 'https://www.googleapis.com/blogger/v3/blogs/\' + pager.creds.blogID + \'/posts?fetchBodies=false&labels=\' + pager.defaults.label + \'&key=AIzaSyChPDh-rh9eJlVlYuLiDW2nvJegZksFhEw'

		def req ="";// 'https://www.googleapis.com/blogger/v3/blogs/6635756895615555070/posts?fetchBodies=false&labels=The%20Disapprovers&key=AIzaSyChPDh-rh9eJlVlYuLiDW2nvJegZksFhEw'
		//req =  new URL(req).getText();
		//print req;
		//req.trustAllCerts()
		//req.trustAllHosts()
		//print(req.body())
		File sourceFile = new File("BloggerTools.groovy");
		Class groovyClass = new GroovyClassLoader(getClass().getClassLoader()).parseClass(sourceFile);
		GroovyObject myObject = (GroovyObject) groovyClass.newInstance();
		myObject.hello()
	}