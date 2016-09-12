//global object which prepares the JSON object to be posted once the user decided which hash is a fake and which isnt.
window.jsonCallback = {};


$( window ).load(function() {

	$("#down_button").click(function(){
	    $("#input_div").animate({
	        height: 'toggle'
	        }, 390, function() {
	    });
	    $('#output').toggle();
	   $( "#down_button" ).css("display", "none");
	   $('#down_button_output').toggle();
	});

	$("#down_button_output").click(function(){
	    $("#input_div").animate({
	        height: 'toggle'
	        }, 390, function() {
	    });
	    $('#output').toggle();
	   $( "#down_button_output" ).css("display", "none");
	   $('#down_button').toggle();
	});

	$("body").on('click', 'li', function() {
	    var text = $(this).text();
	    var docId = this.id;
	    var title = "";
	    $("#result_output").html($("#result_" + this.id).html());
	    $("#result_input").html($("#inputResult_" + this.id).html());
	    
	    buildPieChart(window.jsonCallback, 'chartContainerPlag', this.id);

	    $.each(window.jsonCallback['suspiciousDocs'], function(index, value) {
			if(value['docId'] == docId){
				title = value['title'];
				$('#result_input').animate({scrollTop:$('#'+ value['firstFakeHashId']).position().top}, 'slow');
	    		$('#result_output').animate({scrollTop:$('#'+ value['firstRealHashId']).position().top}, 'slow');
			}
		})

        $("#activeInputResult").val(this.id);
        $('#wiki_link').html("<a target='_blank' href='https://de.wikipedia.org/wiki?curid="+docId+"'>"+title+"</a>");

	});

	$("body").on("click", "span.glyphicon-remove-circle", function(){
    	$(this).parent().removeClass("marked");
		$(this).parent().addClass("unmarked");
		$(this).removeClass("glyphicon-remove-circle");
		$(this).addClass("glyphicon-repeat");

		var id = $(this).parent().attr('id');
		var res = id.substring(5, 100);

		var resDocArray = id.split("_");
		resDoc = resDocArray[1];

		var realId = "real_" + res

		$("#" + realId).removeClass("marked");
		$("#" + realId).addClass("unmarked");
		$("#inputResult_" + $("#activeInputResult").val()).html($(this).parent().parent().html());
		$("#result_" + $("#activeInputResult").val()).html($("#result_output").html());
		removeElementFromPostJSON(resDocArray[1], resDocArray[2]);
		sumUniqueHashes();
		buildSumPieChart(getSumPercent(), "chartContainerAll");
	});

	$("body").on("click", "span.glyphicon-repeat", function(){
    	$(this).parent().removeClass("unmarked");
		$(this).parent().addClass("marked");
		$(this).removeClass("glyphicon-repeat");
		$(this).addClass("glyphicon-remove-circle");

		var id = $(this).parent().attr('id');
		var res = id.substring(5, 100);

		var resDocArray = id.split("_");
		resDoc = resDocArray[1];

		var realId = "real_" + res

		$("#" + realId).removeClass("unmarked");
		$("#" + realId).addClass("marked");

		$("#inputResult_" + resDoc).html($(this).parent().parent().html());
		$("#result_" + resDoc).html($("#result_output").html());
		addElementFromPostJSON(resDocArray[1], resDocArray[2]);
		sumUniqueHashes();
		buildSumPieChart(getSumPercent(), "chartContainerAll");
	});

    document.querySelector('#submit_button').onclick =  function postData(event) {
        console.log(event);

        // Stop form from submitting normally
        event.preventDefault();
       //fakeInput();
      
        var $this = $(this);
        $this.button('loading');

 $('#output').toggle();
         	$( "#down_button_output" ).css("display", "none");

        // Get some values from elements on the page:
        var $form = $(this),
                text = $("#input").val(),
                step = $("#step").val(),
                url = '/handleInput'// $form.attr("action");

        $.post( url, { text: text, step: step })
		  .success(function( data ) {
			 $('#output').toggle();
         	$( "#down_button_output" ).css("display", "none");
		    $this.button('reset');
			

		    $("#result_input").html(text);
		    //data instead of getJSONObj()

		    $("#input_div").animate({
                height: 'toggle'
               }, 390, function() {
              });
              $( "#down_button" ).toggle( "slow", function() {

             });

		    console.log(data);
		    $("#result_input").html(text)
		    //data instead of getJSONObj()
		    window.jsonCallback = data;
		    prepareData(data, text);
		    sumUniqueHashes();
		    buildPieCharts(data);
		    scrollToHash();
		 })


    }
     $('#form_input').submit(function(e){
         e.preventDefault()
     })


    //2nd POST Request to renew Data
    document.querySelector('#submit_button_pdf').onclick =  function postData(event) {
            console.log(event);

           var $this = $(this);
        $this.button('loading');
        var pdf = $('#pdf')[0]['files'][0];
        // Get some values from elements on the page:
        var $form = $(this),
                text = $("#input").val(),
                step = $("#step").val(),
                url = '/handlePdfInput'// $form.attr("action");

        $.post( url, { pdf: pdf, step: step })
		  .success(function( data ) {

		    $this.button('reset');


		    $("#result_input").html(text);
		    //data instead of getJSONObj()

		    $("#input_div").animate({
                height: 'toggle'
               }, 390, function() {
              });
              $( "#down_button" ).toggle( "slow", function() {

             });

		    console.log(data);
		    $("#result_input").html(text)
		    //data instead of getJSONObj()
		    window.jsonCallback = data;
		    prepareData(data, text);
		    buildPieCharts(data);
		 })

        }



    $(':file').change(function(){
	    var file = this.files[0];
	    var name = file.name;
	    var size = file.size;
	    var type = file.type;

	    if (type.toLowerCase().indexOf("pdf") >= 0){
	    	$('#submit_button').prop('disabled', false);
	    	$('#fileError').hide();
	    }else{
	    	$('#submit_button').prop('disabled', true);
	    	$('#fileError').show();

	    }
	    //Your validation
	});


});

function buildPieCharts(data){
	buildPieChart(data, "chartContainerPlag", data['suspiciousDocs'][0]['docId']);
	buildSumPieChart(getSumPercent(), "chartContainerAll");
}

function buildSumPieChart(percent, divId){


	var dataPointsPie = [];
	dataPointsPie.push({ y:  Math.round((percent*100*100))/100, legendText: 'Wikiartikel', label: 'Wikiartikel', 'color': '#e2493c'});
	var noPlagPercent = 0.0;
	if (percent < 1.0){
	    noPlagPercent = 1.0 - percent;
	    dataPointsPie.push({ y: Math.round((noPlagPercent*100*100))/100, legendText: 'Eigener Text', label: 'Eigener Text', 'color': '#88B24D'});
	}
	var chart = new CanvasJS.Chart(divId,
		{
			exportFileName: "Pie Chart",
			exportEnabled: true,
			animationEnabled: true,
			legend:{
				verticalAlign: "bottom",
				horizontalAlign: "center"
			},
			 title:{
			     text: "Gesamtanteil"
			    // more attributes 
			 },
			
			data: [
				{
					type: "pie",
					showInLegend: true,
					toolTipContent: "{legendText}: <strong>{y}%</strong>",
					indexLabel: "{label} {y}%",
					dataPoints: dataPointsPie
				}
			]
		});
	chart.render();
}

function buildPieChart(data, divId, docId){

	var dataPointsPie = [];
	var percent = 0.0;
	var noPlagPercent = 0.0;

	$.each(data['suspiciousDocs'], function(index, value) {
			if(value['docId'] == docId){
				dataPointsPie.push({ y:  Math.round((value['similarityValue']*100*100))/100, legendText: '' + value['title'], label: '' + value['title'] , 'color': '#e2493c'});
				percent = percent + parseFloat(data['suspiciousDocs'][index]['similarityValue']);
			}
	})
	if (percent < 1.0){
	    noPlagPercent = 1.0 - percent;
	    dataPointsPie.push({ y: Math.round((noPlagPercent*100*100))/100, legendText: 'Eigener Text', label: 'Eigener Text', 'color': '#88B24D'});
	}

	var chart = new CanvasJS.Chart(divId,
		{
			exportFileName: "Pie Chart",
			exportEnabled: true,
			animationEnabled: true,
			legend:{
				verticalAlign: "bottom",
				horizontalAlign: "center"
			},
			 title:{
			     text: "Anteil pro Wikiartikel"
			    // more attributes 
			 },

			data: [
				{
					type: "pie",
					showInLegend: true,
					toolTipContent: "{legendText}: <strong>{y}%</strong>",
					indexLabel: "{label} {y}%",
					dataPoints: dataPointsPie
				}
			]
		});
	chart.render();
}

	function fakeInput(){
		$("#input_div").animate({
        		height: 'toggle'
        }, 390, function() {
        });
        $( "#down_button" ).toggle( "slow", function() {

        });

		

		$.getJSON("js/test.json", function(json) {
		    var $form = $('#form_input'),
                text = $form.find("textarea[id='input']").val(),
                //text = escape($form.find("textarea[id='input']").val()),
                step = $form.find("select[id='step']").val(),
                url = $form.attr("action");

		window.jsonCallback = json;
		prepareData(json, text);
		sumUniqueHashes();
		buildPieCharts(json);
		scrollToHash();

		});

	}

	function scrollToHash(){

		var active  = $('.active');
		var title = "";
		var docId = active[2].id;
		 $.each(window.jsonCallback['suspiciousDocs'], function(index, value) {
			if(value['docId'] == docId){
				title = value['title'];
				$('#result_input').animate({scrollTop:$('#'+ value['firstFakeHashId']).position().top}, 'slow');
	    		$('#result_output').animate({scrollTop:$('#'+ value['firstRealHashId']).position().top}, 'slow');
			}
		})
		$('#wiki_link').html("<a target='_blank' href='https://de.wikipedia.org/wiki?curid="+docId+"'>"+title+"</a>");
	}

	/*function addElementFromPostJSON(docId, hashId){
		$.each(window.jsonCallback['suspiciousDocs'], function(indexTwo, valueTwo) {
					if(valueTwo['docId'] == docId){
						$.each(valueTwo['hashKeyAndPositions'], function(indexThree, valueThree) {
		    					if(indexThree == hashId){
		    						window.jsonCallback['suspiciousDocs'][indexTwo]['hashKeyAndPositions'][indexThree]['remove'] = 0;
		    					}
		    			})
					}
		})
		recalculateSimilarity(docId);
		buildPieChart(window.jsonCallback, "chartContainerPlag", docId);
	}*/

	function addElementFromPostJSON(docId, hashId){
		$.each(window.jsonCallback['suspiciousDocs'], function(indexTwo, valueTwo) {
					if(valueTwo['docId'] == docId){
						$.each(valueTwo['cleanOriginHashes'], function(indexThree, valueThree) {
		    					if(indexThree == hashId){
		    						$.each(valueThree['hashes'], function(indexFour, valueFour) {
			    						$.each(valueTwo['hashKeyAndPositions'], function(indexFive, valueFive) {
			    							if (valueFive['hash'] == valueFour['hash']){
			    								window.jsonCallback['suspiciousDocs'][indexTwo]['hashKeyAndPositions'][indexFive]['remove'] = 0;
			    							}
			    							
			    						})
		    						})			
		    					}
		    		})
					}
		})
		recalculateSimilarity(docId);
		buildPieChart(window.jsonCallback, "chartContainerPlag", docId);
	}

	/*function removeElementFromPostJSON(docId, hashId){
		$.each(window.jsonCallback['suspiciousDocs'], function(indexTwo, valueTwo) {
					if(valueTwo['docId'] == docId){
						$.each(valueTwo['hashKeyAndPositions'], function(indexThree, valueThree) {
		    					if(indexThree == hashId){
		    						window.jsonCallback['suspiciousDocs'][indexTwo]['hashKeyAndPositions'][indexThree]['remove'] = 1;
		    					}
		    		})
					}
		})
		recalculateSimilarity(docId);
		buildPieChart(window.jsonCallback, "chartContainerPlag", docId);
	}*/


	function removeElementFromPostJSON(docId, hashId){
		$.each(window.jsonCallback['suspiciousDocs'], function(indexTwo, valueTwo) {
					if(valueTwo['docId'] == docId){
						$.each(valueTwo['cleanOriginHashes'], function(indexThree, valueThree) {
		    					if(indexThree == hashId){
		    						$.each(valueThree['hashes'], function(indexFour, valueFour) {
			    						$.each(valueTwo['hashKeyAndPositions'], function(indexFive, valueFive) {
			    							if (valueFive['hash'] == valueFour['hash']){
			    								window.jsonCallback['suspiciousDocs'][indexTwo]['hashKeyAndPositions'][indexFive]['remove'] = 1;
			    							}
			    							
			    						})
		    						})			
		    					}
		    		})
					}
		})
		recalculateSimilarity(docId);
		buildPieChart(window.jsonCallback, "chartContainerPlag", docId);
	}

	function recalculateSimilarity(docId){
		var newPercent = 0.0;
		var hashes = 0;
		$.each(window.jsonCallback['suspiciousDocs'], function(indexTwo, valueTwo) {
					if(valueTwo['docId'] == docId){
						$.each(valueTwo['hashKeyAndPositions'], function(indexThree, valueThree) {
								if("remove" in window.jsonCallback['suspiciousDocs'][indexTwo]['hashKeyAndPositions'][indexThree]){
									if (window.jsonCallback['suspiciousDocs'][indexTwo]['hashKeyAndPositions'][indexThree]['remove'] == 0){
										hashes = hashes +1;
									}
								}else{
									hashes = hashes +1;
								}	
		    		})
					}
		})
		var newPercent = hashes / (window.jsonCallback['nGramSize'] / 100) / 100;
		$.each(window.jsonCallback['suspiciousDocs'], function(indexTwo, valueTwo) {
					if(valueTwo['docId'] == docId){
						window.jsonCallback['suspiciousDocs'][indexTwo]['similarityValue'] = newPercent;
					}
		})

	}

	function getValueFromHtmlElement(text, key){
		var index = text.search(key);
		var string = text.substring(index);
		var res = string.split(" ");
		var res = res[0].split("\"");
		return res[1];
	}


	function objToString (obj) {
	    var str = '';
	    for (var p in obj) {
	        if (obj.hasOwnProperty(p)) {
	            str += p + '::' + obj[p] + '\n';
	        }
	    }
	    return str;
	}

    function progress(percent, $element) {
        var progressBarWidth = percent * $element.width() / 100;
        $element.find('div').animate({ width: progressBarWidth }, 500).html(percent + "% ");
    }

    function getJSONObj() {
		$.getJSON("js/test.json", function(json) {
		    console.log(json); // this will show the info it in firebug console
		    return json;
		});

        var jsonObj = '{' +
        		'"nGramSize": "83",' +
        		'"suspiciousDocs": {' +
	                '"0": {' +
	                	'"docId": "201",' +
	                	'"similarityValue": "0.3",' +
	                	'"text": "Alan Smithee steht als Pseudonym für einen fiktiven Regisseur, der Filme verantwortet, bei denen der eigentliche Regisseur seinen Namen nicht mit dem Werk in Verbindung gebracht haben möchte. Von 1968 bis 2000 wurde es von der Directors Guild of America (DGA) für solche Situationen empfohlen, seither ist es Thomas Lee. Alan Smithee ist jedoch weiterhin in Gebrauch. Alternative Schreibweisen sind unter anderem die Ursprungsvariante Allen Smithee sowie Alan Smythee und Adam Smithee. Auch zwei teilweise asiatisch anmutende Schreibweisen Alan Smi Thee und Sumishii Aran gehören – so die Internet Movie Database – dazu. == Geschichte == === Entstehung === Das Pseudonym entstand 1968 infolge der Arbeiten am Western-Film Death of a Gunfighter (deutscher Titel Frank Patch – Deine Stunden sind gezählt). Regisseur Robert Totten und Hauptdarsteller Richard Widmark gerieten in einen Streit, woraufhin Don Siegel als neuer Regisseur eingesetzt wurde. Der Film trug nach Abschluss der Arbeiten noch deutlich Tottens Handschrift, der auch mehr Drehtage als Siegel daran gearbeitet hatte, weshalb dieser die Nennung seines Namens als Regisseur ablehnte. Totten selbst lehnte aber ebenfalls ab. Als Lösung wurde Allen Smithee als ein möglichst einzigartiger Name gewählt (bei der späteren Variante Alan Smithee war das Anagramm The Alias Men vermutlich kein Entstehungsgrund). In den zeitgenössischen Kritiken wurde der Regisseur u. a. von Roger Ebert mit den Worten gelobt: TEMPLATE === Aufdeckung und Abkehr === 1997 kam die Parodie An Alan Smithee Film: Burn Hollywood Burn (deutscher Titel Fahr zur Hölle Hollywood) in die Kinos, was das Pseudonym einem größeren Publikum bekannt machte, nicht zuletzt weil Arthur Hiller, der eigentliche Regisseur des Films selbst seinen Namen zurückzog und analog zum Filmtitel das Pseudonym Alan Smithee benutzte. Der Film gilt als einer der schlechtesten Filme der 1990er Jahre und gewann fünf Goldene Himbeeren. Der Film Supernova ist der erste Post-Smithee-Film, dort führte ein gewisser Thomas Lee alias Walter Hill die Regie. == Verwendung == Die Verwendung dieses oder eines anderen Pseudonyms ist für Mitglieder der DGA streng reglementiert. Ein Regisseur, der für einen von ihm gedrehten Film seinen Namen nicht hergeben möchte, hat nach Sichtung des fertigen Films drei Tage Zeit, anzuzeigen, dass er ein Pseudonym verwenden möchte. Der Rat der DGA entscheidet binnen zwei Tagen über das Anliegen. Erhebt die Produktionsfirma Einspruch, entscheidet ein Komitee aus Mitgliedern der DGA und der Vereinigung der Film- und Fernsehproduzenten, ob der Regisseur ein Pseudonym angeben darf. Über die Beantragung muss der Regisseur Stillschweigen halten, ebenso darf er den fertigen Film nicht öffentlich kritisieren, wenn die DGA ihm die Verwendung eines Pseudonyms zugesteht. Ein Antrag des Regisseurs auf Pseudonymisierung kann abgelehnt werden, so durfte Tony Kaye den Namen Smithee bei dem Film American History X nicht einsetzen, obwohl er den Antrag stellte.Auch bei nicht-US-amerikanischen Produktionen wird der Name verwendet, wie etwa beim Pilotfilm der Fernsehserie Schulmädchen. 2007 sendete die ARD am 8. und 9. August den zweiteiligen TV-Film Paparazzo. Auch in diesem Werk erscheint anstatt des eigentlichen Regisseurs Stephan Wagner Alan Smithee im Abspann.Zu den Regisseuren, die das Pseudonym benutzt haben, gehören: * Don Siegel und Robert Totten (für Frank Patch – Deine Stunden sind gezählt),* David Lynch (für die dreistündige Fernsehfassung von Der Wüstenplanet),* Chris Christensen (The Omega Imperative),* Stuart Rosenberg (für Let’s Get Harry),* Richard C. Sarafian (für Starfire),* Dennis Hopper (für Catchfire),* Arthur Hiller (für An Alan Smithee Film: Burn Hollywood Burn),* Rick Rosenthal (Birds II) und* Kevin Yagher (Hellraiser IV – Bloodline).Der Pilotfilm der Serie MacGyver und die fünfte Folge der ersten Staffel führen einen Alan Smithee als Regisseur. Auf der TV-Serien-Seite TV Rage wird Jerrold Freedman als Regisseur des Pilotfilms angegeben. Der Regisseur der fünften Folge ist unbekannt.Zu den Drehbuchautoren, die das Pseudonym benutzt haben, gehören:* Sam Raimi und Ivan Raimi (die das Drehbuch zu Die total beknackte Nuß als „Alan Smithee, Jr.“ und „Alan Smithee, Sr.“ schrieben)Auch in Computerspielen wird dieses Pseudonym angegeben:* Im Abspann des Ego-Shooters Marine Sharpshooter IV aus dem Jahr 2008 wird als Art Director des Spiels „Alan Smithee“ genannt.2014 produzierte die New Yorker Performance-Kompanie Big Dance Theater Alan Smithee Directed this Play, das im August des Jahres auch in Berlin bei Tanz im August aufgeführt wurde. == Literatur ==* Jeremy Braddock, Stephen Hock (Hrsg.): Directed by Allen Smithee. Foreword by Andrew Sarris. University of Minnesota Press, Minneapolis, London 2001, ISBN 0-8166-3534-X.== Weblinks ==* TEMPLATE* Artikel über Smithee von ABC Online (englisch)* Der Mann, der niemals lebte, Spiegel Online einestages* Alan Smithee lebt!, DRadio Wissen== Referenzen ==TEMPLATE",' +
	                	'"title": "Alan Smithee",' +
	                	'"hashKeyAndPositions": {'+
		                							'"0": {' +
					                        			'"hash": "2312312312323",' +
					                        			'"originCharPositions": {' +
						                        			'"0": {'+
							                        			'"startCharIndex": "0",' +
							                        			'"endCharIndex": "50"' +
						                        		  	'}'+
					                        		  	'},'+
					                        		  	'"wikiCharPositions": {'+
						                        			'"0": {'+
							                        			'"startCharIndex": "0",' +
							                        			'"endCharIndex": "50"' +
						                        		  	'}'+
					                        		  	'}'+
					                        		'},'+
					                        		'"1": {' +
					                        			'"hash": "23123232312312323",' +
					                        			'"originCharPositions": {' +
						                        			'"0": {'+
							                        			'"startCharIndex": "0",' +
							                        			'"endCharIndex": "50"' +
						                        		  	'}'+
					                        		  	'},'+
					                        		  	'"wikiCharPositions": {'+
						                        			'"0": {'+
							                        			'"startCharIndex": "0",' +
							                        			'"endCharIndex": "50"' +
						                        		  	'}'+
					                        		  	'}'+
					                        		'}'+
	                        		  			'}'+
	                  '},'+

	                '"1": {' +
	                	'"docId": "30",' +
	                	'"similarityValue": "0.5",' +
	                	'"text": "blaa Smithee steht als Pseudonym für einen fiktiven Regisseur, der Filme verantwortet, bei denen der eigentliche Regisseur seinen Namen nicht mit dem Werk in Verbindung gebracht haben möchte. Von 1968 bis 2000 wurde es von der Directors Guild of America (DGA) für solche Situationen empfohlen, seither ist es Thomas Lee. Alan Smithee ist jedoch weiterhin in Gebrauch. Alternative Schreibweisen sind unter anderem die Ursprungsvariante Allen Smithee sowie Alan Smythee und Adam Smithee. Auch zwei teilweise asiatisch anmutende Schreibweisen Alan Smi Thee und Sumishii Aran gehören – so die Internet Movie Database – dazu. == Geschichte == === Entstehung === Das Pseudonym entstand 1968 infolge der Arbeiten am Western-Film Death of a Gunfighter (deutscher Titel Frank Patch – Deine Stunden sind gezählt). Regisseur Robert Totten und Hauptdarsteller Richard Widmark gerieten in einen Streit, woraufhin Don Siegel als neuer Regisseur eingesetzt wurde. Der Film trug nach Abschluss der Arbeiten noch deutlich Tottens Handschrift, der auch mehr Drehtage als Siegel daran gearbeitet hatte, weshalb dieser die Nennung seines Namens als Regisseur ablehnte. Totten selbst lehnte aber ebenfalls ab. Als Lösung wurde Allen Smithee als ein möglichst einzigartiger Name gewählt (bei der späteren Variante Alan Smithee war das Anagramm The Alias Men vermutlich kein Entstehungsgrund). In den zeitgenössischen Kritiken wurde der Regisseur u. a. von Roger Ebert mit den Worten gelobt: TEMPLATE === Aufdeckung und Abkehr === 1997 kam die Parodie An Alan Smithee Film: Burn Hollywood Burn (deutscher Titel Fahr zur Hölle Hollywood) in die Kinos, was das Pseudonym einem größeren Publikum bekannt machte, nicht zuletzt weil Arthur Hiller, der eigentliche Regisseur des Films selbst seinen Namen zurückzog und analog zum Filmtitel das Pseudonym Alan Smithee benutzte. Der Film gilt als einer der schlechtesten Filme der 1990er Jahre und gewann fünf Goldene Himbeeren. Der Film Supernova ist der erste Post-Smithee-Film, dort führte ein gewisser Thomas Lee alias Walter Hill die Regie. == Verwendung == Die Verwendung dieses oder eines anderen Pseudonyms ist für Mitglieder der DGA streng reglementiert. Ein Regisseur, der für einen von ihm gedrehten Film seinen Namen nicht hergeben möchte, hat nach Sichtung des fertigen Films drei Tage Zeit, anzuzeigen, dass er ein Pseudonym verwenden möchte. Der Rat der DGA entscheidet binnen zwei Tagen über das Anliegen. Erhebt die Produktionsfirma Einspruch, entscheidet ein Komitee aus Mitgliedern der DGA und der Vereinigung der Film- und Fernsehproduzenten, ob der Regisseur ein Pseudonym angeben darf. Über die Beantragung muss der Regisseur Stillschweigen halten, ebenso darf er den fertigen Film nicht öffentlich kritisieren, wenn die DGA ihm die Verwendung eines Pseudonyms zugesteht. Ein Antrag des Regisseurs auf Pseudonymisierung kann abgelehnt werden, so durfte Tony Kaye den Namen Smithee bei dem Film American History X nicht einsetzen, obwohl er den Antrag stellte.Auch bei nicht-US-amerikanischen Produktionen wird der Name verwendet, wie etwa beim Pilotfilm der Fernsehserie Schulmädchen. 2007 sendete die ARD am 8. und 9. August den zweiteiligen TV-Film Paparazzo. Auch in diesem Werk erscheint anstatt des eigentlichen Regisseurs Stephan Wagner Alan Smithee im Abspann.Zu den Regisseuren, die das Pseudonym benutzt haben, gehören: * Don Siegel und Robert Totten (für Frank Patch – Deine Stunden sind gezählt),* David Lynch (für die dreistündige Fernsehfassung von Der Wüstenplanet),* Chris Christensen (The Omega Imperative),* Stuart Rosenberg (für Let’s Get Harry),* Richard C. Sarafian (für Starfire),* Dennis Hopper (für Catchfire),* Arthur Hiller (für An Alan Smithee Film: Burn Hollywood Burn),* Rick Rosenthal (Birds II) und* Kevin Yagher (Hellraiser IV – Bloodline).Der Pilotfilm der Serie MacGyver und die fünfte Folge der ersten Staffel führen einen Alan Smithee als Regisseur. Auf der TV-Serien-Seite TV Rage wird Jerrold Freedman als Regisseur des Pilotfilms angegeben. Der Regisseur der fünften Folge ist unbekannt.Zu den Drehbuchautoren, die das Pseudonym benutzt haben, gehören:* Sam Raimi und Ivan Raimi (die das Drehbuch zu Die total beknackte Nuß als „Alan Smithee, Jr.“ und „Alan Smithee, Sr.“ schrieben)Auch in Computerspielen wird dieses Pseudonym angegeben:* Im Abspann des Ego-Shooters Marine Sharpshooter IV aus dem Jahr 2008 wird als Art Director des Spiels „Alan Smithee“ genannt.2014 produzierte die New Yorker Performance-Kompanie Big Dance Theater Alan Smithee Directed this Play, das im August des Jahres auch in Berlin bei Tanz im August aufgeführt wurde. == Literatur ==* Jeremy Braddock, Stephen Hock (Hrsg.): Directed by Allen Smithee. Foreword by Andrew Sarris. University of Minnesota Press, Minneapolis, London 2001, ISBN 0-8166-3534-X.== Weblinks ==* TEMPLATE* Artikel über Smithee von ABC Online (englisch)* Der Mann, der niemals lebte, Spiegel Online einestages* Alan Smithee lebt!, DRadio Wissen== Referenzen ==TEMPLATE",' +
	                	'"title": "Alan Smithee",' +
	                	'"hashKeyAndPositions": {'+
		                							'"0": {' +
					                        			'"hash": "2312312312323",' +
					                        			'"originCharPositions": {' +
						                        			'"0": {'+
							                        			'"startCharIndex": "0",' +
							                        			'"endCharIndex": "50"' +
						                        		  	'}'+
					                        		  	'},'+
					                        		  	'"wikiCharPositions": {'+
						                        			'"0": {'+
							                        			'"startCharIndex": "0",' +
							                        			'"endCharIndex": "50"' +
						                        		  	'}'+
					                        		  	'}'+
					                        		'},'+
					                        		'"1": {' +
					                        			'"hash": "23123232312312323",' +
					                        			'"originCharPositions": {' +
						                        			'"0": {'+
							                        			'"startCharIndex": "0",' +
							                        			'"endCharIndex": "50"' +
						                        		  	'}'+
					                        		  	'},'+
					                        		  	'"wikiCharPositions": {'+
						                        			'"0": {'+
							                        			'"startCharIndex": "0",' +
							                        			'"endCharIndex": "50"' +
						                        		  	'}'+
					                        		  	'}'+
					                        		'}'+
	                        		  			'}'+
                	'}'+
                '}'+
          '}';
        //return JSON.parse(jsonObj);
    }

    function prepareData(data, inputTextOrginal){
    	$("#results").html("");
    	$("#inputResults").html("");


		var first = 0;

		 $.each(data['suspiciousDocs'], function(index, value) {

		 	var text = value['text'];
		 	var docId = value['docId'];
		 	var replaceStrings = {};
		 	var inputText = inputTextOrginal;
		 	var replaceOrginalStrings = {};
		 	var replaceStringsTwo = {};
		 	var replaceOrginalStringsTwo= {};
		 	if (first == 0){
		 		$("#chooseArticle").html("");
		 		$("#chooseArticle").html( $("#chooseArticle").html() + '<li class="active" id="'+ docId +'"><a class="simValue" data-toggle="pill" data-sim="'+ (Math.round(value['similarityValue']*100*100))/100 +'" href="#'+ docId +'">' + value['title'] +'</a></li>');
		 	}else{
		 		$("#chooseArticle").html( $("#chooseArticle").html() + '<li class="" id="'+ docId +'"><a clas="simValue" data-toggle="pill" data-sim="'+ (Math.round(value['similarityValue']*100*100))/100 +'" href="#'+ docId +'">' + value['title'] + '</a></li>');
		 	}


		 	sumHashMarks(data['suspiciousDocs'][index]['hashKeyAndPositions'], index, 'originCharPositions');
		 	sumHashMarks(data['suspiciousDocs'][index]['hashKeyAndPositions'], index, 'wikiCharPositions');
		 	

		 	$.each(window.jsonCallback['suspiciousDocs'][index]['cleanWikiHashes'], function(indexTwo, valueTwo) {
		 		var hashId = "" + docId + "_" + indexTwo;
		 	 	replaceStrings[hashId] = markHash(text, valueTwo['startIndex'], valueTwo['endIndex'], hashId);
		 	 	replaceOrginalStrings[hashId] = getHash(text, valueTwo['startIndex'], valueTwo['endIndex']);
		 	})


		 	$.each(replaceStrings, function(indexThree, valueThree) {
		 	 	text = text.replace(replaceOrginalStrings[indexThree], replaceStrings[indexThree]);
		 	})


		 	$.each(window.jsonCallback['suspiciousDocs'][index]['cleanOriginHashes'], function(indexTwo, valueTwo) {

		 		var hashId = "" + docId + "_" + indexTwo;
		 	 	replaceStringsTwo[hashId] = markHashInput(inputTextOrginal, valueTwo['startIndex'], valueTwo['endIndex'], hashId);
		 	 	replaceOrginalStringsTwo[hashId] = getHash(inputTextOrginal, valueTwo['startIndex'], valueTwo['endIndex']);
		 	})

		 	$.each(replaceStringsTwo, function(indexThree, valueThree) {
		 	 	inputText = inputText.replace(replaceOrginalStringsTwo[indexThree], replaceStringsTwo[indexThree]);
		 	})


		 	/*$.each(value['hashKeyAndPositions'], function(indexTwo, valueTwo) {
		 		var hashId = "" + docId + "_" + indexTwo;
		 	 	replaceStrings[hashId] = markHash(text, valueTwo, hashId, 'wikiCharPositions');
		 	 	replaceOrginalStrings[hashId] = getHash(text, valueTwo, hashId, 'wikiCharPositions');
		 	})
		 	$.each(replaceStrings, function(indexThree, valueThree) {
		 	 	text = text.replace(replaceOrginalStrings[indexThree], replaceStrings[indexThree]);
		 	})


		 	$.each(value['hashKeyAndPositions'], function(indexTwo, valueTwo) {
		 		var hashId = "" + docId + "_" + indexTwo;
		 	 	replaceStringsTwo[hashId] = markHashInput(inputTextOrginal, valueTwo, hashId, 'originCharPositions');
		 	 	replaceOrginalStringsTwo[hashId] = getHash(inputTextOrginal, valueTwo, hashId, 'originCharPositions');
		 	})
		 	$.each(replaceStrings, function(indexThree, valueThree) {
		 	 	inputText = inputText.replace(replaceOrginalStringsTwo[indexThree], replaceStringsTwo[indexThree]);
		 	})*/


		 	var div = "<div id='result_" + docId + "'>"+ text +"</div>";
		 	$("#results").html($("#results").html() + div);

		
		 	var div = "<div id='inputResult_" + docId + "'>"+ inputText +"</div>";
		 	$("#inputResults").html($("#inputResults").html() + div);

		 	if (first == 0){
		 		$("#nonMarkedInput").html(inputText);
		 		$("#result_output").html($("#result_" + docId).html());
		 		$("#result_input").html(inputText);
		 	}
		 	first = 1;


		 	data['suspiciousDocs'][index]['firstFakeHashId'] = getFirstHash(inputText);
		 	data['suspiciousDocs'][index]['firstRealHashId'] = getFirstHash(text);

		 })

		 var test = 1;
    }



    function sumHashMarks(hashes, docIndex, type){
    	var typeIndex = "";
    	if (type == 'originCharPositions'){
    		typeIndex = "cleanOriginHashes";
    	}else{
    		typeIndex = "cleanWikiHashes";
    	}

    	window.jsonCallback['suspiciousDocs'][docIndex][typeIndex] = {};
    	var min = getMin(hashes, type, 0, 'startCharIndex');
    	var counter = 0;
    	window.jsonCallback['suspiciousDocs'][docIndex][typeIndex][counter] = {};
    	window.jsonCallback['suspiciousDocs'][docIndex][typeIndex][counter]['hashes'] = {};
    	window.jsonCallback['suspiciousDocs'][docIndex][typeIndex][counter]['startIndex'] = min;
    	window.jsonCallback['suspiciousDocs'][docIndex][typeIndex][counter]['remove'] = 0;
    	$.each(hashes, function(index, value) {
		 	if (value[type][0]['startCharIndex'] == min){
		 		window.jsonCallback['suspiciousDocs'][docIndex][typeIndex][counter]['hashes'][value['hash']] = value;
		 		window.jsonCallback['suspiciousDocs'][docIndex][typeIndex][counter]['endIndex'] = value[type][0]['endCharIndex'];
		 	}
		})
    	var start = min;
		do {
	    	var end = window.jsonCallback['suspiciousDocs'][docIndex][typeIndex][counter]['endIndex'];
	    	var newEnd = end;

			do {
			 var ret = getHigherEnd(hashes, start, newEnd, docIndex, type, typeIndex, counter);
			 if (ret != 0){
			 	newEnd = ret;
			 }
			} while (ret != 0)
			
			window.jsonCallback['suspiciousDocs'][docIndex][typeIndex][counter]['endIndex'] = newEnd;

			var newStart = findNewStart(hashes, newEnd, docIndex, type);
			if (newStart != 0){
					counter = counter + 1;
					start = newStart;
					window.jsonCallback['suspiciousDocs'][docIndex][typeIndex][counter] = {};
					window.jsonCallback['suspiciousDocs'][docIndex][typeIndex][counter]['endIndex'] = findEndByStart(hashes, newStart, type);
					window.jsonCallback['suspiciousDocs'][docIndex][typeIndex][counter]['startIndex'] = start;
					window.jsonCallback['suspiciousDocs'][docIndex][typeIndex][counter]['hashes'] = {};
					window.jsonCallback['suspiciousDocs'][docIndex][typeIndex][counter]['remove'] = 0;
			}

		} while (newStart != 0)



    }

    function findEndByStart(hashes, start, type){
    	var end = 0;
    	$.each(hashes, function(index, value) {
		 	if (value[type][0]['startCharIndex'] == start){	
		 		end = value[type][0]['endCharIndex'];
		 	}
		})
		return end;
    }

    function findNewStart(hashes, start, docIndex, type){
    	var newStart = 100000000000;
    	$.each(hashes, function(index, value) {
		 	if (value[type][0]['startCharIndex'] >= start){
		 		if(value[type][0]['startCharIndex'] < newStart){
		 				newStart = value[type][0]['startCharIndex'];
		 		}
		 	}
		})
		if (newStart == 100000000000){
				return 0;
		}else{
				return newStart;
		}
    }

   	function getHigherEnd(hashes, start, end, docIndex, type, typeIndex, counter){
   		var newEnd = end;
   		$.each(hashes, function(index, value) {
			if (value[type][0]['startCharIndex'] >=  start && value[type][0]['startCharIndex'] < end){

				if(!(value['hash'] in window.jsonCallback['suspiciousDocs'][docIndex][typeIndex][counter]['hashes'])){
					window.jsonCallback['suspiciousDocs'][docIndex][typeIndex][counter]['hashes'][value['hash']] = value;
				}

				if(value[type][0]['endCharIndex'] > newEnd){
		 		  newEnd = value[type][0]['endCharIndex'];
		 		}  
		 	}	
		})
		if (newEnd == end){
			return 0;
		}else{
			return newEnd;
		}
		
   	}

    function getMin(array, indexForMin, secondIndex, thirdIndex){
	    var found = 10000000000000000;
		$.each(array, function(index, value) {
			 	if(indexForMin != ""){
			 		if(value[indexForMin][secondIndex][thirdIndex] < found){
			 			found = value[indexForMin][secondIndex][thirdIndex];
			 		}
			 	}else{
			 		if(value < found){
			 			found = value;
			 		}
			 	}
			})
		return found;
    }

    function getFirstHash(text){
    	var resDocArray = text.split("<mark id='");
    	var hash = resDocArray[1].split("'");
    	return hash[0];
    }

   /* function getHash(text, data, index, type){
    	var hashString = text.substring(data[type][0]['startCharIndex'], data[type][0]['endCharIndex']);
    	return hashString;
    }*/

    function getHash(text, start, end){
    	var hashString = text.substring(start, end);
    	return hashString;
    }

  /*  function markHash(text, data, index, type){
    	var hashString = text.substring(data[type][0]['startCharIndex'], data[type][0]['endCharIndex']);
    	hashString = "<mark id='real_" + index + "' class='marked'>" + hashString + "</mark>";
    	return hashString;
    }*/

     function markHash(text, start, end, index){
    	var hashString = text.substring(start, end);
    	hashString = "<mark id='real_" + index + "' class='marked'>" + hashString + "</mark>";
    	return hashString;
    }

   /* function markHashInput(text, data, index, type){
    	var hashString = text.substring(data[type][0]['startCharIndex'], data['originCharPositions'][0]['endCharIndex']);
    	hashString = "<mark id='fake_" + index + "' class='marked'>" + hashString + "<span class='glyphicon glyphicon-remove-circle changeCursor' aria-hidden='true'></span></mark>";
    	return hashString;
    }*/

    function markHashInput(text, start, end, index){
    	var hashString = text.substring(start, end);
    	hashString = "<mark id='fake_" + index + "' class='marked'>" + hashString + "<span class='glyphicon glyphicon-remove-circle changeCursor' aria-hidden='true'></span></mark>";
    	return hashString;
    }

    function sumUniqueHashes(){
    	window.jsonCallback['uniqueHashes'] = {}; 

		$.each(window.jsonCallback['suspiciousDocs'], function(indexTwo, valueTwo) {
			$.each(valueTwo['hashKeyAndPositions'], function(indexThree, valueThree) {
				if("remove" in window.jsonCallback['suspiciousDocs'][indexTwo]['hashKeyAndPositions'][indexThree]){
					if (window.jsonCallback['suspiciousDocs'][indexTwo]['hashKeyAndPositions'][indexThree]['remove'] == 0){
						if(!(valueThree['hash'] in window.jsonCallback['uniqueHashes'])){
							window.jsonCallback['uniqueHashes'][window.jsonCallback['suspiciousDocs'][indexTwo]['hashKeyAndPositions'][indexThree]['hash']] = valueThree;
						}
					}else{
						if((valueThree['hash'] in window.jsonCallback['uniqueHashes'])){
							delete window.jsonCallback['uniqueHashes'][valueThree['hash']];
						}
					}
				}else{
					if(!(valueThree['hash'] in window.jsonCallback['uniqueHashes'])){
						window.jsonCallback['uniqueHashes'][window.jsonCallback['suspiciousDocs'][indexTwo]['hashKeyAndPositions'][indexThree]['hash']] = valueThree;
					}
				}	
		    })
		})
    }

    function getSumPercent(){
    	var sumPercent = getSizeOfArray(window.jsonCallback['uniqueHashes']) / (window.jsonCallback['nGramSize'] / 100) / 100;
		return sumPercent;
    }

    function getSizeOfArray(array){
    	var size = 0;
    	$.each(array, function(indexTwo, valueTwo) {
			size = size + 1;
    	})
    	return size;
    }