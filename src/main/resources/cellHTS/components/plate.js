/*
 * //
 * // Copyright (C) 2009 Boutros-Labs(German cancer research center) b110-it@dkfz.de
 * //
 * //
 * //    This program is free software: you can redistribute it and/or modify
 * //    it under the terms of the GNU General Public License as published by
 * //    the Free Software Foundation, either version 3 of the License, or
 * //    (at your option) any later version.
 * //
 * //    This program is distributed in the hope that it will be useful,
 * //    but WITHOUT ANY WARRANTY; without even the implied warranty of
 * //    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * //
 * //    You should have received a copy of the GNU General Public License
 * //    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

var PlateDesigner = Class.create();
PlateDesigner.prototype = {

    //constructor needs one td element from the plateTable, out of this it can traverse all other elements
    //of this table to bind them to eventlisteners regarding their id ..it doesnt matter which td element you
    //provide as long its from the table of interest and of object type HtmlTableCellElement
    //...the key to do this is traversing the DOM <td><tr> tree;-)

    //second parameter is the welltype(positive,negative,other,etc.)  as a drop down menu html object HTMLSelectElement
    //third param are a JSON key<-> value paired list of all already filled wells
    //fourth is the URL where you want to send the AJAX request to
    //last parameter is for initializing the array graphical new
    initialize: function(tdElement, wellType,plateNumElement,clickedWellsJSON, URI,paramName) {
        this.tdElement = document.getElementById(tdElement);
        this.wellType = document.getElementById(wellType);
        plateNumElement = document.getElementById(plateNumElement);
        //store the current platenum in a static field
        PlateDesigner.prototype.plateNum = plateNumElement.value;        
        //store the obj so we can access it in event handler
        PlateDesigner.prototype.thisObj = this;

        this.paramName = paramName;
        if(clickedWellsJSON!="null")  {
           PlateDesigner.prototype.thisObj.clickedWellsJSON = clickedWellsJSON.evalJSON();
        }
        else {
            PlateDesigner.prototype.thisObj.clickedWellsJSON =null;
        }
        this.plateColors = plateColors;
        this.textColors = textColors;

        //make a translation hash for color=>type
        //TODO:please note:only color names and no color codes are supported right now...line //make a translation hash for color=>type in plate.js is the culprit because of numbers escpecially leading zeros as hash index
        this.plateTypes= new Array();
        for (var plateTypeKey in plateColors) {
            var value = plateColors[plateTypeKey];
            this.plateTypes[value]= plateTypeKey;
        }
        this.URI = URI;
        
        //get all the rows (tr's) of the table with the tdElement and store them in the object
        //go back to parent <td>s element and then back to the parent <tr> element and select all td's from this point
        this.rows = this.tdElement.parentNode.parentNode.childNodes;
        this.rowsAmount = this.rows.length;

        //store the number of columns from the table too
        //get all column element <td>s from this row
        var columns = this.tdElement.parentNode.cells;
        this.columnsAmount = columns.length;


        this.bindFullTableToMouseClickEvent();
        //if we already got predefined wells a.k.a. clickedWellsJSON (e.g. from redrawing the page)
        //we will paint them now
        if(PlateDesigner.prototype.thisObj.clickedWellsJSON!=null) {
           this.drawPredefinedWells();
        }
        //bind the plateNum select box to event onchange
        //plateNumElement.addEventListener("change", this.changePlateNum, false);
        this.catchEvent(plateNumElement,"change",this.changePlateNum);

        //this is only the first time when we are initalizing it (Note: the initalizer method will be started every time we make a pagereload but the clickedWEllsJSon Array will only the first time be undefined)           
        if(PlateDesigner.prototype.thisObj.clickedWellsJSON==null) {
            this.clearColorTableWithoutAjax();
        }

    },
    changePlateNum: function(evt) {
        //IE 8 uses window.event to access possible events
        if (!evt) var evt = window.event;
        //IE8 has srcElement, Netscape target    ... this function gets the element which fired one event
        var obj = evt.target ? evt.target : evt.srcElement;
        //var obj = evt.currentTarget;
        //change the current plateNum to the current selected plate num
        PlateDesigner.prototype.plateNum  = obj.value;
        //clear the whole table
        PlateDesigner.prototype.thisObj.clearColorTableWithoutAjax();
        //get the current design for the current plate
        PlateDesigner.prototype.thisObj.drawPredefinedWells();          
    },
    //extract the row and column number out of strings such as well_B, well_B1, well_2
    extractRawColumn : function (wellName) {
        var match = /well_(\D*)0?(\d*)/.exec(wellName);
        var charRow = match[1];
        var column = match[2];

        //undefined column for well Ids= well_B,well_D
        if (column == "") {
            column = -1;
        }
        else {

            column=parseInt(column);
        }

        //format row ASCII to row number
        //first column is pseudo column with header X and should be ignored => 65-1
        var row = charRow.charCodeAt(0) - 64;
        return {row : row, column : column};
    },
    //bind the whole table elements to mouse click events they belong to
    bindFullTableToMouseClickEvent : function() {
        //make three regular expressions to distinguish well types (heading,first row, regular wells)
        var headingPatt = /well_\d+$/;
        var wellPatt = /well_[A-Z]\d+$/;
        var firstColumnPatt = /well_[A-Z]$/;

        //iterate through all the rows
        for (var i = 0; i < this.rowsAmount; i++) {
            var rowOI = this.rows[i];
            var cells = rowOI.cells;
            for (var j = 0; j < this.columnsAmount; j++) {
                
                //for every element
                var element = cells[j];

                var wellName = element.id;

                //now categorize and bind to appropriate event handlers regarding their name
                //e.g. well_X,well_A is first row, well_1 is first column, well_B02 is regular well element

                //well_X is a special well in the left upper corner and is used to reset the color of the whole table
                if(wellName=="well_X") {
                    //Event.observe(element, 'click', this.clearColorTable.bindAsEventListener(this));
                    this.catchEvent(element,"click",this.clearColorTableFast);
                }
                else if (headingPatt.test(wellName)) {
                    //Event.observe(element, 'click', this.markFullColumn.bindAsEventListener(this));
                    this.catchEvent(element,"click",this.markFullColumn);
                }
                else if (firstColumnPatt.test(wellName)) {
                    //register the first row A,B,C,D,... tds's
                    //Event.observe(element, 'click', this.markFullRow.bindAsEventListener(this));
                    this.catchEvent(element,"click",this.markFullRow);

                }  //register all the regular wells too
                else if (wellPatt.test(wellName)) {
                    //Event.observe(element, 'click', this.onClickWell.bindAsEventListener(this));
                    this.catchEvent(element,"click",this.onClickWell);
                }
            }
        }


    },
    //this will be started everytime we change the plate
    drawPredefinedWells : function() {
         //get the currently used platenum
         var wellsArr = PlateDesigner.prototype.thisObj.clickedWellsJSON;
         var wellsAmount=0;
         if(wellsArr!=null) {
             wellsAmount= wellsArr.length;
         }
         var plateNum = PlateDesigner.prototype.plateNum;            
         for (var i = 1; i < this.rowsAmount; i++) {
            var rowOI = this.rows[i];
            var cells = rowOI.cells;


             for (var j = 1; j < this.columnsAmount; j++) {
                 //for every element
                 var element = cells[j];
                 var wellName = element.id;                   

                 //draw the 'background' with the sample color...we dont want to submit them via AJAX to reduce overhead
                 //so we just draw them and overwrite the ones wit other colors later
                 element.style.backgroundColor = plateColors['sample'];
                 //change textcolor
                 var textColor = this.textColors['sample'];
                 //TODO: is element.textCOntent multi browser compatible???
                 var textStr=element.innerText||element.textContent;
                 element.innerHTML = "<font color=\""+textColor+"\">"+textStr+"</font>";

                 //if this well is pre selected
                 for (var k = 0; k < wellsAmount; k++) {

                     var plateNums = wellsArr[k]["plateNum"];


                     for (var l = 0; l < plateNums.length; l++) {
                         var singlePlateNum = plateNums[l];
                         //have to convert number to string first to make it comparable
                         if ('' +singlePlateNum == plateNum) {
                             if (wellsArr[k]["wellID"] == wellName) {
                                 var type = wellsArr[k]["wellType"];
                                 var color= plateColors[type];
                                 element.style.backgroundColor = color;
                                 var textCol = this.textColors[type];
                                 var textString = element.innerText || element.textContent;
                                 //TODO: is element.textCOntent multi browser compatible???
                                 element.innerHTML = "<font color=\""+textCol+"\">"+textString+"</font>"
                                 var bla = "text";
                                 //change textcolor as well

                             }
                         }
                     }

                 }

             }
         }
        
    },
    markFullColumn : function(mouseEvent) {
        //IE 8 uses window.event to access possible events
        if (!mouseEvent) var mouseEvent = window.event;
        
        var thisObj = PlateDesigner.prototype.thisObj;
        //TODO:this is messed up...huge design mistake...the whole plate logic such as marking a full column etc. should be outsourced into the server side (e.g. tapestry) but which information should be sent by ajax
        //TODO:maybe it would be best to send only single wells and do the logic in both js and server side independantly
        //var element = mouseEvent.currentTarget;

        
        //IE8 has relatedTarget, Netscape fromElement
        var element = mouseEvent.target ? mouseEvent.target : mouseEvent.srcElement;

        var wellName = element.id;
        ////extract the number of row and column out of wellname e.g. A1=  (0,1)
        var rowColumn = thisObj.extractRawColumn(wellName);
        var columnOI = rowColumn.column;

        //elements to send via JSON/Ajax
        var elements = new Array();
        //iterate through all rows
        for (var i = 1; i < thisObj.rowsAmount; i++) {
            //get the ith row
            var row = thisObj.rows[i];
            //get all elements of that row
            var cells = row.cells;
            var element = cells[columnOI];
            //PlateDesigner.prototype.thisObj.overwriteElement(element);
            //extract the value of the currently selected drop down box
            var wellTypeValue = thisObj.wellType.value;
            //...just overwrite it with the new color
            element.style.backgroundColor = plateColors[wellTypeValue];
            elements.push(element);
        }
        //send it via Ajax
        thisObj.sendAjax(elements);

    },    
    markFullRow : function(mouseEvent) {
        //IE 8 uses window.event to access possible events
        if (!mouseEvent) var mouseEvent = window.event;

        var thisObj = PlateDesigner.prototype.thisObj;

        //IE8 has relatedTarget, Netscape fromElement
        var element = mouseEvent.target ? mouseEvent.target : mouseEvent.srcElement;
        //var element = mouseEvent.currentTarget;
        var wellName = element.id;
        //extract the number of row and column out of wellname
        var rowColumn = thisObj.extractRawColumn(wellName);

        var row = rowColumn.row;
        var column = rowColumn.column;


        //get the row of interest
        var rowOI = thisObj.rows[row];
        //get the cells (single <td>s OI)
        var cells = rowOI.cells;

        //elements to send via JSON/Ajax
        var elements = new Array();

        //iterate through rows...do not mark first row !
        for (var i = 1; i < thisObj.columnsAmount; i++) {
            var element = cells[i];
            //extract the value of the currently selected drop down box
            var wellTypeValue = thisObj.wellType.value;
            //...just overwrite it with the new color
            element.style.backgroundColor = plateColors[wellTypeValue];

            //PlateDesigner.prototype.thisObj.overwriteElement(element);
            elements.push(element);
        }
        //send it via Ajax
        thisObj.sendAjax(elements);

    },
    onClickWell : function(mouseEvent) {
        //IE 8 uses window.event to access possible events
        if (!mouseEvent) var mouseEvent = window.event;

        //IE8 has relatedTarget, Netscape fromElement
        var element = mouseEvent.target ? mouseEvent.target : mouseEvent.srcElement;
        
        //this is cool: if we click in the table element in the border we will get the HTMLTableCellElement which we want BUT
        //if we click in the text in the middle we would get a HTMLFontElement which we dont want here!
        
        if(element instanceof HTMLFontElement) {
            //redirect to embedded element which will be the HTMLTableCellElement
            element = element.parentNode;
        }
        
        //extract the value of the currently selected drop down box
        var wellTypeValue = PlateDesigner.prototype.thisObj.wellType.value;
        //...just overwrite it with the new color
        element.style.backgroundColor = plateColors[wellTypeValue];
        
        //send it via Ajax
        PlateDesigner.prototype.thisObj.sendAjax(new Array(element));

    },
//    //this function colors/uncolors a table cell
//    markElement : function(e) {
//        //extract the value of the currently selected drop down box
//        var wellTypeValue = this.wellType.value;
//
//
//        var plateNum = PlateDesigner.prototype.plateNum;
//        //store if we are contaminated what was in before
//
//
//        //first init stuff if not init before
//        if(e.style.backgroundColor=="") {
//            e.style.backgroundColor= plateColors["sample"];
//        }
//
//        //if the well you clicked were empty ...
//        if (e.style.backgroundColor == plateColors['sample'] || e.style.backgroundColor == '') {
//
//                //...just overwrite it with the new color
//                e.style.backgroundColor = plateColors[wellTypeValue];
//
//
//        }
//        //if it was not empty before (we have to erase it)
//        else {
//            //just erase it a.k.a. leave it blank
//            e.style.backgroundColor = plateColors['sample'];
//        }
//    },
//    //just as markElement without all the contaminated well logic,just good old plain overwrite
//    overwriteElement : function(e) {
//        //extract the value of the currently selected drop down box
//        var wellTypeValue = this.wellType.value;
//
//
//        var plateNum = PlateDesigner.prototype.plateNum;
//        //store if we are contaminated what was in before
//
//
//        //first init stuff if not init before
//        if(e.style.backgroundColor=="") {
//            e.style.backgroundColor= plateColors["sample"];
//        }
//        //this is for storing what was in the well before
//        var arrID = plateNum+"-"+e.id;
//
//        //if we are contaminated just add it
//        if(wellTypeValue == 'cont1') {
//            //store in this array what was in the well before we clicked the contaminated well because we want to restore it
//            //in the case of unclicking it
//            if(this.contaminatedArr[arrID]==null) {
//                //get the old well's type
//                this.contaminatedArr[arrID] = this.plateTypes[e.style.backgroundColor];
//                //change the wells type to contaminated color
//
//
//            }
//        }
//        e.style.backgroundColor = plateColors[wellTypeValue];
//
//    },
    overwriteColorWell : function(element) {
        var wellTypeValue = this.wellType.value;
        element.style.backgroundColor = plateColors[wellTypeValue];
    },
    //this sub clears the colors of the complete table

    //THIS IS DEPRECATED BECAUSE ITS TOO SLOW
    clearColorTable : function() {
        var thisObj = PlateDesigner.prototype.thisObj;
        //elements to send via JSON/Ajax
        var elements = new Array();

        //iterate through all the rows
        //first row and column mustnt be iterated
        for (var i = 1; i < thisObj.rowsAmount; i++) {
             var rowOI = thisObj.rows[i];
             var cells = rowOI.cells;
             for (var j = 1; j < thisObj.columnsAmount; j++) {
                  //for every element
                  var element = cells[j];
                  element.style.backgroundColor = plateColors['sample'];
                  elements.push(element);
             }
        }
        //send it via Ajax
        thisObj.sendAjax(elements);

    },
    //this speed up things dramatically
    clearColorTableFast : function() {
        var plateNum = PlateDesigner.prototype.plateNum;
        var thisObj = PlateDesigner.prototype.thisObj;
        //clear optical visually
        thisObj.clearColorTableWithoutAjax();

        //send directly erasing info to the server...the server will handle the whole erasing process by itself
        var data = {
                        plateNum : plateNum,
                        wellID : "well_X",   //well X was clicked means erase it...this is the signal for the server side erasure
                        wellType  : "undefined"
                      };



        thisObj.sendSingleAjaxData(data);


    },
    //this sub clears the colors of the complete table without sending ajax...only visual here!!!
    clearColorTableWithoutAjax : function() {
        //iterate through all the rows
        for (var i = 1; i < this.rowsAmount; i++) {
             var rowOI = this.rows[i];
             var cells = rowOI.cells;
             for (var j = 1; j < this.columnsAmount; j++) {
                  //for every element
                  var element = cells[j];
                  element.style.backgroundColor = plateColors['sample'];
             }
        }
        //send it via Ajax
        //this.sendAjax(elements);

    },
    //this method can send Ajax from HTML elements to a specified URI
    //we send well_ID plus wellType as a JSON object to the java event link handler
    
    sendAjax: function(elements) {
        //get currenty used plate 
        var plateNum = PlateDesigner.prototype.plateNum;
        var elementsAmount = elements.length;
        var dataCollection = new Array();

        var wellsArr = PlateDesigner.prototype.thisObj.clickedWellsJSON;
        
        for(var i=0;i<elementsAmount;i++) {
            var wellType = this.plateTypes[elements[i].style.backgroundColor]||alert('Error:not defined');

            
           //add the plateNum to the plateNums array if not defined before
           var data = {
                        plateNum : plateNum,
                        wellID : elements[i].id,
              //send the plateType instead of the color
                        wellType  : wellType
                      };
            dataCollection.push(data);
        }
        var jsonObj = Object.toJSON(dataCollection);

        new Ajax.Request(
                this.URI,
        {
          method: 'post',
            //send the ajax request parameters not anonymously but with a name for the parameters so we can better
            // access the parameters server side (using tapestry request.getParam e.g.)
          parameters: this.paramName+"="+jsonObj,
            //return the complete JSON string of all enabled wells..the logic lies in java
          onSuccess: function(response) {
                         var jSONText=response.responseText;
                         var jSONStuff= jSONText.evalJSON();
                         PlateDesigner.prototype.thisObj.clickedWellsJSON= jSONStuff;
                         PlateDesigner.prototype.thisObj.drawPredefinedWells();
                     }
        }
        );
    },
    //this sends directly single well data objs
    sendSingleAjaxData: function(data) {
        var dataCollection = new Array();
        dataCollection.push(data);
        var jsonObj = Object.toJSON(dataCollection);

        new Ajax.Request(
                this.URI,
        {
          method: 'post',
            //send the ajax request parameters not anonymously but with a name for the parameters so we can better
            // access the parameters server side (using tapestry request.getParam e.g.)
          parameters: this.paramName+"="+jsonObj,
            //return the complete JSON string of all enabled wells..the logic lies in java
          onSuccess: function(response) {
                         var jSONText=response.responseText;
                         var jSONStuff= jSONText.evalJSON();
                         PlateDesigner.prototype.thisObj.clickedWellsJSON= jSONStuff;
              //redraw the plates
                         PlateDesigner.prototype.thisObj.drawPredefinedWells();

                     }
        }
        );
    },
    //for cross-browser compatibiltiy regarding event listeners we need to add the following code
    catchEvent : function(eventObj,event,eventHandler) {
        //mozilla and netscape event handling (W3C standard)
        if(eventObj.addEventListener) {
            eventObj.addEventListener(event,eventHandler,false);
        }
        //windows event handling mechanism!!!
        else if (eventObj.attachEvent) {
            event = "on"+event;
            eventObj.attachEvent(event,eventHandler)
        }
    }
    

 }