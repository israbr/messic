/**
 * @source: https://github.com/spheras/messic
 *
 * @licstart  The following is the entire license notice for the 
 *  JavaScript code in this page.
 *
 * The JavaScript code in this page is free software: you can
 * redistribute it and/or modify it under the terms of the GNU
 * General Public License (GNU GPL) as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option)
 * any later version.  The code is distributed WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU GPL for more details.
 *
 * As additional permission under GNU GPL version 3 section 7, you
 * may distribute non-source (e.g., minimized or compacted) forms of
 * that code without the copy of the GNU GPL normally required by
 * section 4, provided you include this license notice and a URL
 * through which recipients can access the Corresponding Source.
 *
 * @licend  The above is the entire license notice
 * for the JavaScript code in this page.
 *
 */
/* genereated code UID for the album we are seeing */
var VAR_AlbumCode;

/* init the album page */
function initAlbum() {

	// function to leave the upload section
	VAR_changeSection = function(nextFunction) {
		var visible = $("#messic-album-menuoption-save").is(':visible');
		if (visible) {
			$
					.confirm({
						'title' : messicLang.albumLeaveTitle,
						'message' : messicLang.albumLeaveContent,
						'buttons' : {
							'Yes' : {
								'title' : messicLang.confirmationYes,
								'class' : 'blue',
								'action' : function() {
									nextFunction();
								}
							},
							'No' : {
								'title' : messicLang.confirmationNo,
								'class' : 'gray',
								'action' : function() {
								} // Nothing to do in this case. You can as
									// well omit the action property.
							}
						}
					});
		} else {
			nextFunction();
		}
	}

	$("#messic-album-songs-body-songaction-play").hover(function() {
		$("#messic-playlist-background").addClass("interesting");
	}, function() {
		$("#messic-playlist-background").removeClass("interesting");
	});

	$("#messic-album-songs-head-songaction-add").click(function() {
		$("#messic-album-songs-head-songaction-addinput").click();
	});
	// event change for the input type file hidden
	$("#messic-album-songs-head-songaction-addinput").change(function(evt) {
		var files = evt.target.files; // FileList object
		albumAddFiles(files);
	});

	VAR_AlbumCode = UtilGetGUID();

	// cleaning all temp files
	$.ajax({
		url : 'services/albums/clear',
		type : 'POST',
		success : (function() {

		}),
		error : (function() {

		})
	});

}

/* Download the current album to the user */
function albumDownload(albumSid){
	var url='services/albums/'+albumSid+'/zip?messic_token='+VAR_MessicToken;    
    document.location.href=url;
}
/* Download the selected song to the user */
function albumDownloadSong(songSid){
	var url='services/songs/'+songSid+'/audio?messic_token='+VAR_MessicToken;    
    window.open(url);
}
/* Download the selected other resource to the user */
function albumDownloadResource(resourceSid){
	//TODO
}

/* change the status of the album to editing */
function albumEditStatus() {
	$("#messic-album-menuoption-edit").hide();
	$("#messic-album-menuoption-save").show();
	$("#messic-album-menuoption-discard").show();
}

/* function to start editing the title of the album */
function albumTitleEdit() {
	albumEditStatus();
	var div = $("#messic-album-name");
	var text = div.text().trim();
	var code = "<input type=\"text\" id=\"messic-album-name-textedit\" name=\"Name\" class=\"k-textbox\" value=\""
			+ text + "\" required/>";
	div.empty();
	div.append($(code));
	$("#messic-album-name input").focus();
	$("#messic-album-name input").select();
	$("#messic-album-name").removeClass("messic-album-editable")
	$("#messic-album-name").addClass("messic-album-editing")
}
/* function to start editing the year of the album */
function albumYearEdit() {
	albumEditStatus();
	var div = $("#messic-album-year");
	var text = div.contents(':not(div)').text().trim();
	var code = "<input type=\"text\" id=\"messic-album-year-textedit\" name=\"Year\" class=\"k-textbox\" value=\""
			+ text + "\" required/>";
	div.empty();
	div.append($(code));
	$("#messic-album-year input").focus();
	$("#messic-album-year input").select();
	$("#messic-album-year").removeClass("messic-album-editable")
	$("#messic-album-year").addClass("messic-album-editing")

	$("#messic-album-year input").kendoNumericTextBox({
		placeholder : messicLang.uploadYearPlaceholder,
		format : "#0",
		decimals : 0,
		max : 2100,
		min : 1500,
		spinners : true,
		step : 1
	});
}
/* function to start editing the genre of the album */
function albumGenreEdit() {
	albumEditStatus();
	var div = $("#messic-album-genre");
	var text = div.contents(':not(div)').text().trim();
	var code = "<input type=\"text\" id=\"messic-album-genre-textedit\" name=\"Genre\" class=\"k-textbox\" value=\""
			+ text + "\" required/>";
	div.empty();
	div.append($(code));
	$("#messic-album-genre input").focus();
	$("#messic-album-genre input").select();
	$("#messic-album-genre").removeClass("messic-album-editable")
	$("#messic-album-genre").addClass("messic-album-editing")

	$("#messic-album-genre input").kendoComboBox({
		placeholder : messicLang.uploadGenrePlaceholder,
		dataTextField : "name",
		dataValueField : "sid",
		filter : "contains",
		autoBind : false,
		minLength : 1,
		dataSource : {
			transport : {
				read : {
					url : "services/genres",
					type : "GET",
					dataType : "json"
				}
			},
			schema : {
				data : function(response) {
					return response;
				},
				model : {
					id : "sid"
				}
			}
		}
	});
}

/* function to start editing the comments of the album */
function albumCommentsEdit() {
	albumEditStatus();
	var div = $("#messic-album-comments");
	var text = div.contents(':not(div)').text().trim();
	var code = "<textarea maxlength=\"255\" id=\"messic-album-comments-textedit\" name=\"Comments\" class=\"k-textbox\">"+text+"</textarea>";
	div.empty();
	div.append($(code));
	$("#messic-album-comments input").focus();
	$("#messic-album-comments input").select();
	$("#messic-album-comments").removeClass("messic-album-editable")
	$("#messic-album-comments").addClass("messic-album-editing")
}

/* function to start editing the author of the album */
function albumAuthorEdit() {
	albumEditStatus();
	var div = $("#messic-album-author");
	var text = div.text().trim();
	var code = "<input type=\"text\" id=\"messic-album-author-textedit\" name=\"Name\" class=\"k-textbox\" value=\""
			+ text + "\" required/>";
	div.empty();
	div.append($(code));
	$("#messic-album-author input").focus();
	$("#messic-album-author input").select();
	$("#messic-album-author").removeClass("messic-album-editable");
	$("#messic-album-author").addClass("messic-album-editing");

	$("#messic-album-author input").kendoComboBox({
		placeholder : messicLang.uploadAuthorPlaceholder,
		dataTextField : "name",
		dataValueField : "sid",
		filter : "contains",
		autoBind : false,
		minLength : 1,
		change : function() {

		},
		dataSource : {
			transport : {
				read : {
					url : "services/authors",
					type : "GET",
					dataType : "json"
				}
			},
			schema : {
				data : function(response) {
					return response;
				},
				model : {
					id : "sid"
				}
			}
		}
	});

	$("#messic-album-author input").click(function(event){
		event.stopPropagation();
	});
}

function albumDiscardChanges(albumSid){
	$.confirm({
		'title' : messicLang.albumDiscardTitle,
		'message' : messicLang.albumDiscardContent,
		'buttons' : {
			'Yes' : {
				'title' : messicLang.albumDiscardYes,
				'class' : 'blue',
				'action' : function() {

					// cleaning all temp files
					$.ajax({
						url : 'services/albums/clear',
						type : 'POST',
						success : (function() {

						}),
						error : (function() {

						})
					});

					exploreEditAlbum(albumSid);
				}
			},
			'No' : {
				'title': messicLang.albumDiscardNo,
				'class' : 'blue',
				'action' : function(){

				}
			}
		}
	});

}

/* function to save all the changes done */
function albumSaveChanges(albumSid) {
	// 1st, need to wait any pending load?
	if ($(".messic-album-songs-uploading-percent").length > 0) {
		$.confirm({
					'title' : messicLang.albumSavePendingTitle,
					'message' : messicLang.albumSavePendingContent,
					'buttons' : {
						'Yes' : {
							'title' : messicLang.albumSavePendingYes,
							'class' : 'blue',
							'action' : function() {
							}
						}
					}
				});

		return false;
	}

	var authordiv=$("#messic-album-author-textedit").data("kendoComboBox");
	if (authordiv){
		$.confirm({
			'title' : messicLang.albumSaveCreateTitle,
			'message' : messicLang.albumSaveCreateContent,
			'buttons' : {
				'Yes' : {
					'title' : messicLang.albumSaveCreateYes,
					'class' : 'blue',
					'action' : function() {
						albumSaveChangesDefinitely(albumSid,true);
					}
				},
				'No' : {
					'title' : messicLang.albumSaveCreateNo,
					'class' : 'blue',
					'action' : function() {
						albumSaveChangesDefinitely(albumSid,false);
					}
				}
			}
		});
	}else{
		albumSaveChangesDefinitely(albumSid,false);
	}
}

function albumSaveChangesDefinitely(albumSid,authorCreation){

	$.getJSON(
			"services/albums/" + albumSid,
			function(data) {
				
				data.code=VAR_AlbumCode;
				
				//edit the author name? or changing the author of this album
				var authordiv=$("#messic-album-author-textedit").data("kendoComboBox");
				if(authordiv){
					var autorSid=0;
					if(authordiv.value()!=authordiv.text()){
						//the authorHasBeenChanged?!
						authorSid=authordiv.value();
						data.author.sid=authorSid;
						data.author.name=authordiv.text();
					}else{
						if(authorCreation && data.author.name.toUpperCase()!=authordiv.text()){
							data.author.sid=-1;
						}
						data.author.name=authordiv.text();
					}

				}

				//edit of album name
				var titlediv=$("#messic-album-name-textedit");
				if(titlediv && titlediv.length>0){
					data.name=titlediv.val();
				}

				//edit year of the album
				var yeardiv=$("#messic-album-year-textedit");
				if(yeardiv.length>0){
					data.year=yeardiv.val();
				}

				//edit genre of the album
				var genrediv=$("#messic-album-genre-textedit").data("kendoComboBox");
				if(genrediv){
					var genreSid=0;
					if(genrediv.value()!=genrediv.text()){
						genreSid=genrediv.value();
					}
					data.genre={
							name:genrediv.text(),
							sid:genreSid,
					}
				}

				//edit of album name
				var commentsdiv=$("#messic-album-comments-textedit");
				if(commentsdiv && commentsdiv.length>0){
					data.comments=commentsdiv.val();
				}

				//new songs
				$(".messic-album-songs-bodyrow-new").each(function(){
					var songSid=-1;
					var songTrack=$(this).find(".messic-album-songs-body-songtrack").val();
					var songName=$(this).find(".messic-album-songs-body-songname").val();
					var songFileName=$(this).find(".messic-album-songs-body-songname").data("filename");
					if(!data.songs){
						data.songs=new Array();
					}
					data.songs.push({
						sid:songSid,
						track:songTrack,
						name:songName,
						fileName:songFileName,
					});
				});

				//editing songs
				$(".messic-album-songs-bodyrow-editsong").each(function(){
					var songSid=$(this).find(".messic-album-songs-body-songtrack").data("sid");
					var songTrack=$(this).find(".messic-album-songs-body-songtrack").val();
					var songName=$(this).find(".messic-album-songs-body-songname").val();
					if(!data.songs){
						data.songs=new Array();
					}
					data.songs.push({
						sid:songSid,
						track:songTrack,
						name:songName
					});
				});

				//new artworks
				$(".messic-album-songs-bodyrow-artwork-new").each(function(){
					var artworkSid=-1;
					var artworkName=$(this).find(".messic-album-songs-body-artworkname").text().trim();
					if(!data.artworks){
						data.artworks=new Array();
					}
					data.artworks.push({
						sid:artworkSid,
						fileName:artworkName
					});
				});

				//editing artworks
				$(".messic-album-songs-bodyrow-editartwork").each(function(){
					var artworkSid=$(this).find(".messic-album-songs-body-artworkname").data("sid");
					var artworkName=$(this).find(".messic-album-songs-body-artworkname").val();
					if(!data.artworks){
						data.artworks=new Array();
					}
					data.artworks.push({
						sid:artworkSid,
						fileName:artworkName
					});
				});

				//new other resources
				$(".messic-album-songs-bodyrow-other-new").each(function(){
					var otherSid=-1;
					var otherName=$(this).find(".messic-album-songs-body-othername").text();
					if(!data.others){
						data.others=new Array();
					}
					data.others.push({
						sid:otherSid,
						fileName:otherName
					});
				});

				//editing other resources
				$(".messic-album-songs-bodyrow-editother").each(function(){
					var otherSid=$(this).find(".messic-album-songs-body-othername").data("sid");
					var otherName=$(this).find(".messic-album-songs-body-othername").val();
					if(!data.others){
						data.others=new Array();
					}
					data.others.push({
						sid:otherSid,
						fileName:otherName
					});
				});

				$.ajax({
					url: 'services/albums',  //Server script to process data
					type: 'POST',
					success: function(){
						UtilShowInfo(messicLang.albumSaveOK);
						exploreEditAlbum(albumSid);
					},
					error: function(e){
						UtilShowInfo(messicLang.albumSaveFail);
					},
					processData: false,
					data: JSON.stringify(data),
					contentType: "application/json"
				});
	});



}

/*
 * function that add the selected files by the user to the webpage of the album
 * (need to be saved)
 */
function albumAddFiles(files) {
	albumEditStatus();

	for (var i = 0, f; f = files[i]; i++) {
		if (f.type.match('image*')) {
			albumAddFileImage(f);
		} else if (f.type.match('audio.*')) {
			albumAddFileAudio(f);
		} else {
			albumAddFileOther(f);
		}
	}
}

/* function to edit an existing song in the album */
function albumEditSong(sid,track,name,authorName,albumName,divButton, rate){
	albumEditStatus();
	var code = "<div>";
	code = code + "  <input type=\"text\" class=\"messic-album-songs-bodyfield messic-album-songs-body-songtrack\" value=\"" + track + "\" data-sid=\""+sid+"\">";
	code = code + "  <input type=\"text\" class=\"messic-album-songs-bodyfield messic-album-songs-body-songname\" value=\"" + name + "\">";
	code = code + "  <div class=\"messic-album-songs-bodyfield messic-album-songs-body-songaction\">";
	code = code + "    <div title=\""+messicLang.albumSongDelete+"\" class=\"messic-album-songs-body-songaction-remove\" onclick=\"albumRemoveSong('"+sid+"','"+track+"-"+UtilEscapeJS(name)+"',$(this).parent().parent())\"></div>";
	code = code + "    <div title=\""+messicLang.albumSongPlay+"\" class=\"messic-album-songs-body-songaction-play\" onclick=\""+
	"addSong('"+UtilEscapeJS(name)+"','"+UtilEscapeJS(authorName)+"','"+UtilEscapeJS(albumName)+"','"+UtilEscapeJS(name)+"',"+rate+")"+"\"></div>";
	code = code + "  </div>";
	code = code + "  <div class=\"divclearer\"></div>";
	code = code + "</div>";

	var divRowParent=$(divButton).parent().parent();
	$(divRowParent).empty();
	$(divRowParent).append($(code));
	$(divRowParent).addClass("messic-album-songs-bodyrow-edit");
	$(divRowParent).addClass("messic-album-songs-bodyrow-editsong");
}

/* function to edit an existing artwork in the album */
function albumEditArtwork(sid,name,divButton){
	albumEditStatus();
	var code = "<div>";
	code=code+"<div class=\"messic-album-songs-bodyrow messic-album-songs-bodyrow-artwork\">";
	code=code+"  <div class=\"messic-album-songs-bodyfield messic-album-songs-body-artwork\"><img src=\"services/albums/"+sid+"/resource?messic_token="+VAR_MessicToken+"\" onclick=\"albumShowArtwork('"+sid+"')\"/></div>";
	code=code+"  <input type=\"text\" class=\"messic-album-songs-bodyfield messic-album-songs-body-artworkname\" value=\"" + name + "\" data-sid=\""+sid+"\">";
	code=code+"  <div class=\"messic-album-songs-bodyfield messic-album-songs-body-artworkaction\">";
	code=code+"    <div title=\""+messicLang.albumArtworkShow+"\" class=\"messic-album-songs-body-songaction-show\" onclick=\"albumShowArtwork('"+sid+"')\"></div>";
	code=code+"	   <div title=\""+messicLang.albumArtworkRemove+"\" class=\"messic-album-songs-body-songaction-remove\" onclick=\"albumRemoveResource("+sid+",$(this).parent().parent())\"></div>";
	code=code+"  </div>";
	code=code+"  <div class=\"divclearer\"></div>";
	code=code+"</div>";
	
	var divRowParent=$(divButton).parent().parent();
	$(divRowParent).empty();
	$(divRowParent).append($(code));
	$(divRowParent).addClass("messic-album-songs-bodyrow-edit");
	$(divRowParent).addClass("messic-album-songs-bodyrow-editartwork");
}

/* function to edit an existing resource 'other' in the album */
function albumEditOther(sid,name,divButton){
	albumEditStatus();

	var code="<div>";
	code=code+"  <div class=\"messic-album-songs-bodyfield messic-album-songs-body-otherfile\">..</div>";
	code=code+"  <div class=\"messic-album-songs-bodyfield messic-album-songs-body-othername\">"+name+"</div>";
	code=code+"  <div class=\"messic-album-songs-bodyfield messic-album-songs-body-otheraction\">";
	code=code+"    <div class=\"messic-album-songs-body-songaction-remove\" onclick=\"albumRemoveResource("+sid+",$(this).parent().parent())\"></div>";
	code=code+"  </div>";
	code=code+"  <div class=\"divclearer\"></div>";
	code=code+"</div>";

	
	var divRowParent=$(divButton).parent().parent();
	$(divRowParent).empty();
	$(divRowParent).append($(code));
	$(divRowParent).addClass("messic-album-songs-bodyrow-edit");
	$(divRowParent).addClass("messic-album-songs-bodyrow-editother");
}

/* function to add an audio file to the album */
function albumAddFileAudio(file) {
	$.getJSON(
					"services/songs/" + file.name + "/wizard",
					function(data) {

						var code = "<div class=\"messic-album-songs-bodyrow messic-album-songs-bodyrow-new\">";
						code = code
								+ "  <input type=\"text\" class=\"messic-album-songs-bodyfield messic-album-songs-body-songtrack\" value=\""
								+ data.track + "\">";
						code = code
								+ "  <input type=\"text\" class=\"messic-album-songs-bodyfield messic-album-songs-body-songname\" value=\""
								+ data.name + "\" data-filename=\""+UtilEscapeHTML(file.name)+"\">";
						code = code
								+ "  <div class=\"messic-album-songs-uploading\"><div class=\"messic-album-songs-uploading-percent\"></div></div>";
						code = code
								+ "  <div class=\"messic-album-songs-bodyfield messic-album-songs-body-songaction\">";
						code = code
								+ "    <div title=\""+messicLang.albumSongDelete+"\" class=\"messic-album-songs-body-songaction-remove\" onclick=\"albumRemoveLocalResource(this)\"></div>";
						code = code + "  </div>";
						code = code + "  <div class=\"divclearer\"></div>";
						code = code + "</div>";

						var newelement = $(code);
						$("#messic-album-songs-body").prepend(newelement);
						var percentdiv = $(newelement).find(
								".messic-album-songs-uploading-percent");
						
						uploadResource(percentdiv,file);
					}
			);
}

function uploadResource(percentdiv, file){
	// reading the file to show the image
	var reader = new FileReader();
	// Closure to capture the file information.
	reader.onload = function(eldiv) {
		var fsend = function(e) {
			var bin = e.target.result;
			$.ajax({
						url : 'services/albums/'
								+ VAR_AlbumCode
								+ "?fileName="
								+ encodeURIComponent(file.name),
						type : 'PUT',
						// Ajax events
						success : (function() {
							// at the end we put 100%
							// completed, and add filename
							// data to the div (this is also
							// a way to know if the upload
							// have been finished
							eldiv.width('100');
							eldiv.data('file', file.name);
							eldiv.removeClass('messic-album-songs-uploading-percent');
							eldiv.addClass('messic-album-songs-uploading-percent-finished');
						}),
						error : (function() {
							UtilShowInfo("Error uploading the new song!");
							eldiv
									.addClass("messic-album-songs-uploading-percent-failed");
						}),
						xhr : (function() {
							var xhr = new window.XMLHttpRequest();
							// Upload progress
							xhr.upload
									.addEventListener(
											"progress",
											function(evt) {
												if (evt.lengthComputable) {
													var percentComplete = evt.loaded
															/ evt.total;
													// Do
													// something
													// with
													// upload
													// progress
													console
															.log(percentComplete);
													eldiv
															.width((percentComplete * 100)
																	+ '%');
												}
											}, false);
							return xhr;
						}),
						processData : false,
						data : bin
					});
		}

		return fsend;
	}(percentdiv);

	// Read in the image file as a data URL.
	reader.readAsArrayBuffer(file);
}

/* remove the new local resource that is trying to add */
function albumRemoveLocalResource(div){
	$(div).parent().parent().remove();
}

/* function to add an image file to the album */
function albumAddFileImage(file) {
	var code = "<div class=\"messic-album-songs-bodyrow messic-album-songs-bodyrow-artwork messic-album-songs-bodyrow-artwork-new\">";
	code = code
			+ "  <div class=\"messic-album-songs-bodyfield messic-album-songs-body-artwork\" onclick=\"albumShowLocalArtwork($(this).parent())\"></div>";
	code = code
			+ "  <div class=\"messic-album-songs-bodyfield messic-album-songs-body-artworkname\">"
			+ file.name + "</div>";
	code = code
			+ "  <div class=\"messic-album-songs-uploading\"><div class=\"messic-album-songs-uploading-percent\"></div></div>";
	code = code
			+ "  <div class=\"messic-album-songs-bodyfield messic-album-songs-body-artworkaction\">";
	code = code
			+ "    <div class=\"messic-album-songs-body-songaction-show\" onclick=\"albumShowLocalArtwork($(this).parent().parent())\"></div>";
	code = code
			+ "    <div class=\"messic-album-songs-body-songaction-remove\" onclick=\"albumRemoveLocalResource(this)\"></div>";
	code = code + "  </div>";
	code = code + "  <div class=\"divclearer\"></div>";
	code = code + "</div>";

	var newelement = $(code);
	$("#messic-album-songs-body").prepend(newelement);

	// reading the file to show the image
	var reader = new FileReader();
	// Closure to capture the file information.
	reader.onload = (function(theFile, element) {
		return function(e) {
			// Create a new image.
			var img = new Image();
			// Set the img src property using the data URL.
			img.src = e.target.result;
			// Add the image to the page.
			element
					.find(
							".messic-album-songs-bodyfield.messic-album-songs-body-artwork")
					.append(img);
		};
	})(file, newelement);

	// Read in the image file as a data URL.
	reader.readAsDataURL(file);
	

	var percentdiv = $(newelement).find(".messic-album-songs-uploading-percent");
	uploadResource(percentdiv,file);

}
/* function to add an other file to the album */
function albumAddFileOther(file) {
	var code = "<div class=\"messic-album-songs-bodyrow messic-album-songs-bodyrow-other messic-album-songs-bodyrow-other-new\">";
	code = code + "  <div class=\"messic-album-songs-bodyfield messic-album-songs-body-otherfile\">...</div>";
	code = code + "  <div class=\"messic-album-songs-bodyfield messic-album-songs-body-othername\">"+ file.name + "</div>";
	code = code	+ "  <div class=\"messic-album-songs-uploading\"><div class=\"messic-album-songs-uploading-percent\"></div></div>";
	code = code + "  <div class=\"messic-album-songs-bodyfield messic-album-songs-body-otheraction\">";
	code = code + "    <div class=\"messic-album-songs-body-songaction-remove\" onclick=\"albumRemoveLocalResource(this)\"></div>";
	code = code + "  </div>";
	code = code + "  <div class=\"divclearer\"></div>";
	code = code + "</div>";

	var newelement = $(code);
	$("#messic-album-songs-body").prepend(newelement);
	var percentdiv = $(newelement).find(".messic-album-songs-uploading-percent");
	uploadResource(percentdiv,file);
}

function albumShowArtworkDestroy() {
	$('.messic-album-artwork-show-overlay').remove();
	$('.messic-album-artwork-show').remove();
}

/* function to show a local resource (not uploaded) at the page */
function albumShowLocalArtwork(resource) {
	var code = '<div class=\"messic-album-artwork-show-overlay\" onclick=\"albumShowArtworkDestroy()\"></div>';
	code = code + '<div class=\"messic-album-artwork-show\">';
	code = code + "   <img></img>";
	code = code + "</div>";

	var codeobj = $(code);
	codeobj.find("img").attr("src", $(resource).find("img").attr("src"));
	codeobj.hide().appendTo('body');
	codeobj.fadeIn();
}

/* function to set as cover an artwork */
function albumCoverArtwork(albumSid, resourceSid, divimg) {
	$.ajax({
		url : 'services/albums/' + albumSid + "/" + resourceSid + "/cover",
		type : 'POST',
		success : function(result) {
			var imgCloned=$("#messic-album-cover img").clone();
			var imgResource=divimg;
			imgCloned.attr("src",imgResource.attr("src"));
			$("#messic-album-cover img").replaceWith(imgCloned);
			
			UtilResetAlbumRandom(albumSid);
			
			UtilShowInfo(messicLang.albumCoverChanged);
		},
		fail : function(result) {
			UtilShowInfo("Fail while setting cover");
		}
	});

}

/* function to show a resource at the page */
function albumShowArtwork(albumSid, resourceSid) {
	var code = '<div class=\"messic-album-artwork-show-overlay\" onclick=\"albumShowArtworkDestroy()\"></div>';
	code = code + '<div class=\"messic-album-artwork-show\">';
	code = code + "   <img src=\"services/albums/" + resourceSid
			+ "/resource?messic_token="+VAR_MessicToken+"\"></img>";
	code = code + "   <a href=\"services/albums/" + resourceSid
			+ "/resource?messic_token="+VAR_MessicToken+"\" target=\"_blank\"></a>"
	code = code + "   <div id=\"messic-album-artwork-setcover\" onclick=\"albumCoverArtwork("+albumSid+","+resourceSid+",$(this).parent().find('img') )\"></div>"
	code = code + "</div>";

	$(code).hide().appendTo('body').fadeIn();
}

/* function to show the cover at the page */
function albumShowCover(albumSid) {
	var code = '<div class=\"messic-album-artwork-show-overlay\" onclick=\"albumShowArtworkDestroy()\"></div>';
	code = code + '<div class=\"messic-album-artwork-show\">';
	code = code + "   <img src=\"services/albums/" + albumSid
			+ "/cover?messic_token="+VAR_MessicToken+"&"+UtilGetRandom(0,99999999)+"\"></img>";
	code = code + "   <a href=\"services/albums/" + albumSid
			+ "/cover?messic_token="+VAR_MessicToken+"&"+UtilGetRandom(0,99999999)+"\" target=\"_blank\"></a>"
	code = code + "</div>";

	$(code).hide().appendTo('body').fadeIn();
}

/* delete a song from the album */
function albumRemoveResource(resourceSid, div) {

	$.confirm({
				'title' : messicLang.albumResourceRemoveTitle,
				'message' : messicLang.albumResourceRemoveContent,
				'buttons' : {
					'Yes' : {
						'title' : messicLang.confirmationYes,
						'class' : 'blue',
						'action' : function() {
							$.ajax({
										url : 'services/albumresources/'
												+ resourceSid,
										type : 'DELETE',
										success : function(result) {
											UtilShowInfo(messicLang.albumResourceRemoveOK);
											$(div).fadeOut();
											$(div).remove();
										},
										fail : function(result) {
											UtilShowInfo(messicLang.albumResourceRemoveFail);
										}
									});
						}
					},
					'No' : {
						'title' : messicLang.confirmationNo,
						'class' : 'gray',
						'action' : function() {
							UtilShowInfo(messicLang.albumResourceRemoveCancel);
						}
					}
				}
			});

}

/* delete a song from the album */
function albumRemoveSong(songSid, songName, div) {

	$.confirm({
				'title' : messicLang.albumSongRemoveTitle,
				'message' : messicLang.albumSongRemoveContent + "[" + songName + "]",
				'buttons' : {
					'Yes' : {
						'title' : messicLang.confirmationYes,
						'class' : 'blue',
						'action' : function() {
							$.ajax({
										url : 'services/songs/' + songSid,
										type : 'DELETE',
										success : function(result) {
											UtilShowInfo(messicLang.albumSongRemoveOK);
											$(div).fadeOut();
											$(div).remove();
										},
										fail : function(result) {
											UtilShowInfo(messicLang.albumSongRemoveFail);
										}
									});
						}
					},
					'No' : {
						'title' : messicLang.confirmationNo,
						'class' : 'gray',
						'action' : function() {
							UtilShowInfo(messicLang.albumSongRemoveCancel);
						}
					}
				}
			});

}

/* Show the musicinfo obtained from a plugin in the web page */
function albumShowMusicInfo(authorName, albumName, pluginName, div) {
	// selected the new one
	$(".messic-album-plugincontainer-menuitem").removeClass(
			"messic-album-plugincontainer-menuitem-selected");
	$(div).addClass("messic-album-plugincontainer-menuitem-selected");

	var contentdiv = $(".messic-album-plugincontainer-content");
	$(contentdiv).empty();
	$(contentdiv)
			.append(
					"<div class=\"messic-album-plugincontainer-content-wait\">"+messicLang.albumMusicinfoLoading+"</div>");

	$.getJSON("services/musicinfo?pluginName=" + pluginName + "&albumName="
			+ albumName + "&authorName=" + authorName, function(data) {
		var resulthtml = data.htmlContent;

		$(contentdiv).empty();
		$(contentdiv).append(resulthtml);
	});
}

/* Remove the album */
function albumRemove(albumSid) {
	$.confirm({
				'title' : messicLang.albumRemoveTitle,
				'message' : messicLang.albumRemoveContent,
				'buttons' : {
					'Yes' : {
						'title' : messicLang.confirmationYes,
						'class' : 'blue',
						'action' : function() {

							$.ajax({
										url : 'services/albums/' + albumSid,
										type : 'DELETE',
										success : function(result) {
											UtilShowInfo(messicLang.albumRemoveOK);
											$.get(
															"explore.do",
															function(data) {
																$(
																		"#messic-page-content")
																		.empty();
																var posts = $($.parseHTML(data)).filter('#content').children();
																$("#messic-page-content").append(posts);
																initExplore();
															});
										},
										fail : function(result) {
											UtilShowInfo(messicLang.albumRemoveFail);
										}
									});

						}
					},
					'No' : {
						'title' : messicLang.confirmationNo,
						'class' : 'gray',
						'action' : function() {
							UtilShowInfo(messicLang.albumRemoveCancel);
						} // Nothing to do in this case. You can as well omit
							// the action property.
					}
				}
			});
}

/**
 * Function that add all the songs of an album to a playlist
 * @param albumSid albumSid to be added
 */
function albumAddToPlaylist(albumSid){
	$.getJSON("services/albums/"+albumSid+"?songsInfo=true&authorInfo=false", function(data) {
    	var songSids=new Array();
    	for(var j=0;j<data.songs.length;j++){
    		songSids.push(data.songs[j].sid);
    	}
    	playlistAddToPlaylist(songSids);
		
	});		
}

/**
 * Add a song to a certain playlist selected by the user
 * @param songSid sid of the song to be added
 */
function albumAddSongToPlaylist(songSid){
	var songSids=new Array();
	songSids.push(songSid);
	playlistAddToPlaylist(songSids);
}