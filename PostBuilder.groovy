#! /usr/bin/env groovy

import sun.font.TrueTypeFont;

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

//println "path script directory: " + scriptDir
//println "path to script file: " + scriptFile



def title = "";
def commonFileName = "";

// use date as part of default post name
date = new Date();

//println date;

/* 	
 * default config values 
 * check existence of default configuration file 
 * 
 */

def defaultConfig  = new ConfigSlurper().parse( new File( scriptDir + '/default.config').toURI().toURL() );



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
			print( param )
			if( param == "help"){
				showHelp();
			}

			def p = param.split(":");
			// replace spaces with hyphens in title
			if( p[0] == "title")
			{
				commonFileName = p[1].toLowerCase().replaceAll( " ", "-");
				//p[1] = p[1].replaceAll( " ", " ")
				defaultConfig.config.title = p[1];
			}
		}
	}

//println defaultConfig;

/* END parsing command */


/* check for existence of custom config based on title of post */
def customConfig;
def customConfigFile = new File( basePath + "/" + commonFileName + ".config");

if( customConfigFile.exists()){	
	customConfig = new ConfigSlurper().parse( new File( commonFileName + '.config' ).toURI().toURL() );
} else {
	new File( commonFileName + '.config').withWriter{
		writer ->
				defaultConfig.writeTo(writer);
	}
	customConfig = new ConfigSlurper().parse( new File( commonFileName + '.config' ).toURI().toURL() );
}

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

if( customConfig.config.dev == true || customConfig.config.extJS == true ){ /* include jQuery from CDN */
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