            //  <![CDATA[   

            $(document).ready(function() {

                $('.separator a').css('outline', 'none');

                $('img', '.separator a').each(function() {
                    $(this).attr('thumbWidth', $(this).attr('width'));
                    $(this).attr('thumbHeight', $(this).attr('height'));
                    $(this).attr('originalSrc', $(this).attr('src'));
                });


                $('.separator a').click(function(event) {
                    event.preventDefault();
                    var smPix = $('img', this);
                    /* caption paragraph */
                    var p = $(this).siblings().filter(':first').addClass('activeP');
                    console.dir( p )
                    
                    if (smPix.hasClass('activePix')) {
                        smPix.removeClass('activePix');
                        smPix.attr('width', smPix.attr('thumbwidth'));
                        smPix.attr('height', smPix.attr('thumbheight'));
                        smPix.parent().parent().siblings().css({ 'opacity':'1' });
                        
                        p.removeClass('activeP')
                        
                        return;
                    } else {
                        
                        var w = $('img.activePix').attr('thumbwidth');
                        var h = $('img.activePix').attr('thumbheight');
                    	
                        /* check if there are any activePix'es at all */
                        if( $('img.activePix').length > 0  ){

                        
                            $('img.activePix').attr('width', w );
                            $('img.activePix').attr('height', h );
                        
                            $('img.activePix').removeClass('activePix')      
                            $('p.activeP').removeClass( 'activeP' );
                        }
                        
                        smPix.addClass('activePix')
                        var multiplier = 4;	
                        var w1 = $('img.activePix').attr('thumbwidth');
                        //alert(  w1  )
                        //alert( w1 * multiplier )
                        
                        if( w1 * multiplier > 1000) {
                        	multiplier = 2;
                        }
                        smPix.attr('width', $('img.activePix').attr('thumbwidth') * multiplier );
                        smPix.attr('height',  $('img.activePix').attr('thumbheght') * multiplier );
                        
                        smPix.parent().parent().siblings().css({ 'opacity':'0.2' });
                        smPix.parent().parent().css({ 'opacity':'1' });
                        
                        //smPix.parent().parent().parent().css({ 'background-color':'#000' });
                        
                        /* caption paragraph */
                        
                        return;
                    } // else

                });// click

            });// doc ready

            //]]>