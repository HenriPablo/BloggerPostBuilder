#! /usr/bin/env groovy


import sun.font.TrueTypeFont;

println "\nWelcome to Groovy Blogger Post Builder"

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
def commonFileName = "default";

// use date as part of default post name
date = new Date();

//println date;

/* 	
 * default config values 
 * check existence of default configuration file 
 * 
 */

def defaultConfig  = new ConfigSlurper().parse( new File( scriptDir + '/default.config').toURI().toURL() );

//println defaultConfig;


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
			// replace spaces with underscores in title
			if( p[0] == "title")
			{
				commonFileName = p[1].replaceAll( " ", "-")
				p[1] = p[1].replaceAll( " ", "-")
				defaultConfig.config.title = p[1];

			}
		}
	}

//println defaultConfig;

/* END parsing command */


/* check for existence of custom config based on title of post */
def customConfig 
def customConfigFile = new File( basePath + "/" + defaultConfig.config.title + ".config");

if( customConfigFile.exists()){	
	customConfig = new ConfigSlurper().parse( new File( defaultConfig.config.title + '.config' ).toURI().toURL() );
} else {
	new File( defaultConfig.config.title + '.config').withWriter{
		writer ->
				defaultConfig.writeTo(writer);
	}
	customConfig = new ConfigSlurper().parse( new File( defaultConfig.config.title + '.config' ).toURI().toURL() );
}

/* DEFAULT CSS, JS file */
def defaultCss = new File( scriptDir + "/" + commonFileName + ".css");
def defaultJs = new File( scriptDir + "/" + commonFileName + ".js");


if( !defaultCss.exists()){
	new File( scriptDir + "/" + commonFileName + ".css").write("");
}

if( !defaultJs.exists()){
	new File( scriptDir + "/" + commonFileName + ".js").write("");
}


/* CUSTOM - POST HTML CONTENTS */
def customHtml = new File( basePath + "/" + customConfig.config.title + ".html");

if( !customHtml.exists()){
	// make this optional: new File( basePath + "/" + customConfig.config.title + ".html" ).write("<h1>" + customConfig.config.title + "</h1>");
	
	/* if it didn't exist before, read in the one just created */
	customHtml = new File( basePath + "/" + customConfig.config.title + ".html");
}


/* 
 * THE HEART OF THE POST 
 * 	this holds DEV or PRODUCTION post contents of HTML, CSS and JS
 */
String outputString = "";

/* CSS */

if( defaultCss.exists() ){
	outputString += blockTxt( defaultCss, 'css', customConfig );
}

/* JAVASCRIPT */

if( customConfig.config.dev == true ){ /* include jQuery from CDN */
	/*
		TODO: move this into config or at least allow for version to be arg based,
	use some default version is not provided
	*/
    outputString += '<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js" type="text/javascript"></script>';
    outputString += '\n';
}

if( defaultJs.exists() ){
    outputString += blockTxt(defaultJs, 'js', customConfig )
}


/* HTML */
if( customHtml.exists() ){
    outputString += blockTxt( customHtml, 'html', customConfig );
}

/* do we need to create the whole file setup?*/
if( customConfig.config.create == "all" ){
	println "\n trying to call createFiles() \n";
	println "title : " + customConfig.config.title + "\n";
	this.createFiles( customConfig.config.title );
}


new File( basePath + "/" + customConfig.config.title + "-composit.html").write( outputString.trim() );

// open composit file in default browser
// credit goes to: http://www.centerkey.com/java/browser/
if( customConfig.config.preview == true){
	String url = "file://${basePath}/${ customConfig.config.title }-composit.html";
	java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
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
				result += "<html><head><title>" +  customConfig.config.title + "</title>"
			}
			result += '<style type="text/css">\n'
			result += file.getText();
			result += '\n</style>'
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

	System.exit(0);
}

